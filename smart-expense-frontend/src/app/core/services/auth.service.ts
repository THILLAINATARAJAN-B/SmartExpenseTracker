import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { LoginRequest, RegisterRequest, AuthResponse } from '../models/user.model';

@Injectable({ providedIn: 'root' })
export class AuthService {

  private apiUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient, private router: Router) {}

    login(credentials: LoginRequest): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/auth/login`, credentials).pipe(
        tap(response => {
        console.log('Login response:', response); // ← debug
        localStorage.setItem('token', response.token);
        // ✅ Handle both userId and id fields
        const uid = response.userId || response.id || '';
        localStorage.setItem('userId', uid.toString());
        localStorage.setItem('userName', response.name || '');
        localStorage.setItem('userEmail', response.email || '');
        })
    );
    }

  register(data: RegisterRequest): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/users/register`, data);
  }

  logout(): void {
    localStorage.clear();
    this.router.navigate(['/login']);
  }

  isLoggedIn(): boolean {
    return !!localStorage.getItem('token');
  }

getUserId(): number {
  const id = localStorage.getItem('userId');
  console.log('getUserId called, value:', id); // debug
  return id ? Number(id) : 0;
}


  getUserName(): string {
    return localStorage.getItem('userName') || '';
  }
}
