import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Budget } from '../models/budget.model';
import { Dashboard } from '../models/dashboard.model';

@Injectable({ providedIn: 'root' })
export class BudgetService {

  private apiUrl = 'http://localhost:8080/api/budgets';

  constructor(private http: HttpClient) {}

  setBudget(budget: Budget): Observable<Budget> {
    return this.http.post<Budget>(this.apiUrl, budget);
  }

  deleteBudget(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  getDashboard(userId: number, month: number, year: number): Observable<Dashboard> {
    return this.http.get<Dashboard>(
      `${this.apiUrl}/dashboard/${userId}?month=${month}&year=${year}`
    );
  }
  getBudgets(userId: number): Observable<Budget[]> {
  const month = new Date().getMonth() + 1;
  const year = new Date().getFullYear();
  return this.http.get<Budget[]>(
    `${this.apiUrl}/user/${userId}?month=${month}&year=${year}`
  );
}


}
