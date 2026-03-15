import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  {
    path: 'login',
    loadComponent: () =>
      import('./pages/login/login.component').then(m => m.LoginComponent)
  },
  {
    path: 'register',
    loadComponent: () =>
      import('./pages/register/register.component').then(m => m.RegisterComponent)
  },
  {
    path: 'dashboard',
    loadComponent: () =>
      import('./pages/dashboard/dashboard.component').then(m => m.DashboardComponent),
    canActivate: [authGuard]
  },
  {
    path: 'expenses',
    loadComponent: () =>
      import('./pages/expenses/expenses.component').then(m => m.ExpensesComponent),
    canActivate: [authGuard]
  },
  {
    path: 'budgets',
    loadComponent: () =>
      import('./pages/budgets/budgets.component').then(m => m.BudgetsComponent),
    canActivate: [authGuard]
  },
  { path: '**', redirectTo: 'login' }
];
