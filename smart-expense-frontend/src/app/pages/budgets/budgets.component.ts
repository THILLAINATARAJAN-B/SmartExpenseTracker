import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { BudgetService } from '../../core/services/budget.service';
import { AuthService } from '../../core/services/auth.service';
import { Budget } from '../../core/models/budget.model';

@Component({
  selector: 'app-budgets',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './budgets.component.html',
  styleUrl: './budgets.component.css'
})
export class BudgetsComponent implements OnInit {
  budgets: Budget[] = [];
  filteredBudgets: Budget[] = [];         // ← filtered view
  loading = false;
  dataLoaded = false;
  showForm = false;
  successMessage = '';
  errorMessage = '';

  // Filter fields
  filterCategory = '';
  filterMonth = 0;
  filterYear = 0;

  categories = ['FOOD', 'TRANSPORT', 'SHOPPING', 'ENTERTAINMENT', 'HEALTH', 'EDUCATION', 'OTHER'];
  months = [
    { value: 1, label: 'January' }, { value: 2, label: 'February' },
    { value: 3, label: 'March' },   { value: 4, label: 'April' },
    { value: 5, label: 'May' },     { value: 6, label: 'June' },
    { value: 7, label: 'July' },    { value: 8, label: 'August' },
    { value: 9, label: 'September' },{ value: 10, label: 'October' },
    { value: 11, label: 'November' },{ value: 12, label: 'December' }
  ];

  form: Budget = {
    category: 'FOOD', monthlyLimit: 0,
    month: new Date().getMonth() + 1,
    year: new Date().getFullYear(),
    userId: 0
  };

  constructor(
    private budgetService: BudgetService,
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
    this.loadBudgets();
  }

  loadBudgets(): void {
    this.loading = true;
    this.budgetService.getBudgets(this.authService.getUserId()).subscribe({
      next: (data) => {
        this.budgets = [...data];
        this.applyFilters();
        this.loading = false;
        this.dataLoaded = true;
        this.cdr.detectChanges();
      },
      error: () => {
        this.loading = false;
        this.dataLoaded = true;
        this.budgets = [];
        this.filteredBudgets = [];
        this.cdr.detectChanges();
      }
    });
  }

  applyFilters(): void {
    let result = [...this.budgets];

    if (this.filterCategory) {
      result = result.filter(b => b.category === this.filterCategory);
    }
    if (this.filterMonth) {
      result = result.filter(b => b.month === this.filterMonth);
    }
    if (this.filterYear) {
      result = result.filter(b => b.year === this.filterYear);
    }

    this.filteredBudgets = result;
    this.cdr.detectChanges();
  }

  clearFilters(): void {
    this.filterCategory = '';
    this.filterMonth = 0;
    this.filterYear = 0;
    this.applyFilters();
  }

  get activeFilterCount(): number {
    return [this.filterCategory, this.filterMonth || '', this.filterYear || '']
      .filter(v => !!v).length;
  }

  saveBudget(): void {
    this.form.userId = this.authService.getUserId();
    this.budgetService.setBudget(this.form).subscribe({
      next: () => {
        this.successMessage = 'Budget saved!';
        this.showForm = false;
        this.loadBudgets();
        setTimeout(() => this.successMessage = '', 3000);
      },
      error: () => { this.errorMessage = 'Failed to save budget'; }
    });
  }

  deleteBudget(id: number): void {
    if (!confirm('Delete this budget?')) return;
    this.budgetService.deleteBudget(id).subscribe({
      next: () => {
        this.loadBudgets();
        this.successMessage = 'Budget deleted!';
        setTimeout(() => this.successMessage = '', 2000);
      },
      error: () => { this.errorMessage = 'Failed to delete'; }
    });
  }

  getMonthLabel(month: number): string {
    return this.months.find(m => m.value === month)?.label ?? month.toString();
  }
}
