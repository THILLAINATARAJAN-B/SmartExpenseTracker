import { Component } from '@angular/core';
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

  constructor(private authService: AuthService, private router: Router) {}

  register(): void {
    this.loading = true;
    this.errorMessage = '';

    this.authService.register({ name: this.name, email: this.email, password: this.password })
      .subscribe({
        next: () => {
          this.loading = false;
          this.successMessage = 'Account created! Redirecting to login...';
          setTimeout(() => this.router.navigate(['/login']), 1500);
        },
        error: (err) => {
          this.loading = false;
          this.errorMessage = err.error?.message || 'Registration failed';
        }
      });
  }
}
