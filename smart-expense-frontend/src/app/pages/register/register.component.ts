import { Component, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent {
  name = '';
  email = '';
  password = '';
  errorMessage = '';
  successMessage = '';
  loading = false;

  constructor(
    private authService: AuthService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  // ✅ Password strength checker
  get passwordStrength(): { score: number; label: string; color: string } {
    const p = this.password;
    let score = 0;
    if (p.length >= 8)                                                    score++;
    if (/[A-Z]/.test(p))                                                  score++;
    if (/[a-z]/.test(p))                                                  score++;
    if (/[0-9]/.test(p))                                                  score++;
    if (/[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]/.test(p))                 score++;

    const labels = ['', 'Very Weak', 'Weak', 'Fair', 'Strong', 'Very Strong'];
    const colors = ['', 'danger',    'danger', 'warning', 'info', 'success'];
    return { score, label: labels[score] ?? '', color: colors[score] ?? '' };
  }

  get passwordValid(): boolean {
    return this.passwordStrength.score === 5;
  }

  register(): void {
    if (!this.passwordValid) {
      this.errorMessage = 'Password does not meet the requirements.';
      this.cdr.detectChanges();
      return;
    }

    this.loading = true;
    this.errorMessage = '';

    this.authService.register({ name: this.name, email: this.email, password: this.password })
      .subscribe({
        next: () => {
          this.loading = false;
          this.successMessage = 'Account created! Redirecting to login...';
          this.cdr.detectChanges();
          setTimeout(() => this.router.navigate(['/login']), 1500);
        },
        error: (err) => {
          this.loading = false;
          this.errorMessage = err.error?.message || 'Registration failed';
          this.cdr.detectChanges();
        }
      });
  }
}