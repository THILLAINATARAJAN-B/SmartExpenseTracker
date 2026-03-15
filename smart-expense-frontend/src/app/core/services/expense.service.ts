import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Expense } from '../models/expense.model';

@Injectable({ providedIn: 'root' })
export class ExpenseService {

  private apiUrl = 'http://localhost:8080/api/expenses';

  constructor(private http: HttpClient) {}

  getExpenses(userId: number): Observable<Expense[]> {
    return this.http.get<Expense[]>(`${this.apiUrl}/user/${userId}`);
  }

  addExpense(expense: Expense): Observable<Expense> {
    return this.http.post<Expense>(this.apiUrl, expense);
  }

  updateExpense(id: number, expense: Expense): Observable<Expense> {
    return this.http.put<Expense>(`${this.apiUrl}/${id}`, expense);
  }

  deleteExpense(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  exportCsv(userId: number): Observable<Blob> {
    return this.http.get(
      `http://localhost:8080/api/export/expenses/${userId}`,
      { responseType: 'blob' }
    );
  }
}
