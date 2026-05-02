import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../core/auth.service';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { esEmailValido } from '../../core/utils';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './login.html'
})
export class Login {
  private http = inject(HttpClient);
  private router = inject(Router);
  loginEmail = '';
  loginContrasena = '';
  errorLogin = signal('');

  private authService = inject(AuthService);
  ngOnInit(): void {
    if (this.authService.isLoggedIn()) {
      this.router.navigate(['/rutinas']);
    }
  }

  iniciarSesion(): void {
    this.errorLogin.set('');

    if (!this.loginEmail || !this.loginContrasena) {
      this.errorLogin.set('Por favor, rellena todos los campos.');
      this.loginContrasena = '';
      return;
    }
    if (!esEmailValido(this.loginEmail)) {
      this.errorLogin.set('El formato del email no es válido.');
      this.loginContrasena = '';
      return;
    }

    const body = { email: this.loginEmail, password: this.loginContrasena };

    this.http.post<{ token: string }>(`${environment.apiUrl}/auth/login`, body)
      .subscribe({
        next: (data) => {
          localStorage.setItem('token', data.token);
          this.router.navigate(['/rutinas']);
        },
        error: (err: HttpErrorResponse) => {
          this.loginContrasena = '';
          if (err.status === 0) {
            this.errorLogin.set('No se pudo conectar con el servidor. Verifica tu conexión.');
          } else {
            this.errorLogin.set('Email o contraseña incorrectos.');
          }
        }
      });
  }

  volver(): void {
    this.router.navigate(['']);
  }
}