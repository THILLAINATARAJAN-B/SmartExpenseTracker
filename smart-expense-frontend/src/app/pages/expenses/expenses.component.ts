import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ExpenseService } from '../../core/services/expense.service';
import { AuthService } from '../../core/services/auth.service';
import { Expense } from '../../core/models/expense.model';

@Component({
  selector: 'app-expenses',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './expenses.component.html',
  styleUrl: './expenses.component.css'
})
export class ExpensesComponent implements OnInit {
  expenses: Expense[] = [];
  filteredExpenses: Expense[] = [];       // ← filtered view
  loading = false;
  dataLoaded = false;
  showForm = false;
  editMode = false;
  successMessage = '';
  errorMessage = '';

  // Filter fields
  searchText = '';
  filterCategory = '';
  filterFrom = '';
  filterTo = '';

  categories = ['FOOD', 'TRANSPORT', 'SHOPPING', 'ENTERTAINMENT', 'HEALTH', 'EDUCATION', 'OTHER'];

  form: Expense = {
    title: '', amount: 0, category: 'FOOD',
    description: '', date: new Date().toISOString().split('T')[0],
    userId: 0
  };

  constructor(
    private expenseService: ExpenseService,
    public authService: AuthService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    const userId = this.authService.getUserId();
    if (!userId || userId === 0) {
      this.router.navigate(['/login']);
      return;
    }
    this.loadExpenses();
  }

  loadExpenses(): void {
    const userId = this.authService.getUserId();
    if (!userId) return;

    this.loading = true;
    this.expenseService.getExpenses(userId).subscribe({
      next: (data) => {
        this.expenses = [...data];
        this.applyFilters();
        this.loading = false;
        this.dataLoaded = true;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Load expenses error:', err);
        this.loading = false;
        this.dataLoaded = true;
        this.errorMessage = 'Failed to load expenses';
        this.cdr.detectChanges();
      }
    });
  }

  // ✅ Client-side filter: search + category + date range
  applyFilters(): void {
    let result = [...this.expenses];

    if (this.searchText.trim()) {
      const q = this.searchText.toLowerCase();
      result = result.filter(e =>
        e.title.toLowerCase().includes(q) ||
        (e.description?.toLowerCase().includes(q) ?? false)
      );
    }

    if (this.filterCategory) {
      result = result.filter(e => e.category === this.filterCategory);
    }

    if (this.filterFrom) {
      result = result.filter(e => e.date >= this.filterFrom);
    }

    if (this.filterTo) {
      result = result.filter(e => e.date <= this.filterTo);
    }

    this.filteredExpenses = result;
    this.cdr.detectChanges();
  }

  clearFilters(): void {
    this.searchText = '';
    this.filterCategory = '';
    this.filterFrom = '';
    this.filterTo = '';
    this.applyFilters();
  }

  openAddForm(): void {
    this.editMode = false;
    this.form = {
      title: '', amount: 0, category: 'FOOD',
      description: '', date: new Date().toISOString().split('T')[0],
      userId: this.authService.getUserId()
    };
    this.showForm = true;
  }

  editExpense(expense: Expense): void {
    this.editMode = true;
    this.form = { ...expense };
    this.showForm = true;
  }

  saveExpense(): void {
    this.form.userId = this.authService.getUserId();
    const op = this.editMode
      ? this.expenseService.updateExpense(this.form.id!, this.form)
      : this.expenseService.addExpense(this.form);

    op.subscribe({
      next: () => {
        this.successMessage = this.editMode ? 'Expense updated!' : 'Expense added!';
        this.showForm = false;
        this.loadExpenses();
        setTimeout(() => this.successMessage = '', 3000);
      },
      error: () => { this.errorMessage = 'Failed to save expense'; }
    });
  }

  deleteExpense(id: number): void {
    if (!confirm('Delete this expense?')) return;
    this.expenseService.deleteExpense(id).subscribe({
      next: () => {
        this.loadExpenses();
        this.successMessage = 'Deleted!';
        setTimeout(() => this.successMessage = '', 2000);
      },
      error: () => { this.errorMessage = 'Failed to delete'; }
    });
  }

  exportCsv(): void {
    this.expenseService.exportCsv(this.authService.getUserId()).subscribe(blob => {
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url; a.download = 'expenses.csv'; a.click();
    });
  }

  get activeFilterCount(): number {
    return [this.searchText, this.filterCategory, this.filterFrom, this.filterTo]
      .filter(v => !!v).length;
  }
}
