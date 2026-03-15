import { Component, OnInit, ChangeDetectorRef } from '@angular/core';  // ← add
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';                               // ← add
import { BudgetService } from '../../core/services/budget.service';
import { AuthService } from '../../core/services/auth.service';
import { Dashboard } from '../../core/models/dashboard.model';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent implements OnInit {
  dashboard: Dashboard | null = null;
  loading = false;                        // ← start false
  dataLoaded = false;
  month = new Date().getMonth() + 1;
  year = new Date().getFullYear();

  constructor(
    private budgetService: BudgetService,
    public authService: AuthService,
    private router: Router,               // ← add
    private cdr: ChangeDetectorRef        // ← add
  ) {}

  ngOnInit(): void {
    const userId = this.authService.getUserId();
    if (!userId || userId === 0) {
      this.router.navigate(['/login']);
      return;
    }
    this.loadDashboard();
  }

  loadDashboard(): void {
    this.loading = true;
    const userId = this.authService.getUserId();
    this.budgetService.getDashboard(userId, this.month, this.year)
      .subscribe({
        next: (data) => {
          this.dashboard = { ...data };    // ← spread for new reference
          this.loading = false;
          this.dataLoaded = true;
          this.cdr.detectChanges();        // ← force re-render
        },
        error: () => {
          this.loading = false;
          this.dataLoaded = true;
          this.dashboard = {
            totalSpentThisMonth: 0,
            spentByCategory: {},
            budgetByCategory: {},
            budgetAlerts: {}
          };
          this.cdr.detectChanges();        // ← force re-render on error
        }
      });
  }

  getAlertClass(alert: string): string {
    if (!alert) return 'success';
    if (alert.includes('EXCEEDED')) return 'danger';
    if (alert.includes('WARNING')) return 'warning';
    return 'success';
  }

  objectKeys(obj: any): string[] {
    return obj ? Object.keys(obj) : [];
  }
}
