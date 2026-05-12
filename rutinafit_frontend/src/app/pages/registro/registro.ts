import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../core/auth.service';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { esEmailValido } from '../../core/utils';

@Component({
  selector: 'app-registro',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './registro.html'
})
export class Registro {
  private http = inject(HttpClient);
  private router = inject(Router);
  registroNombre = '';
  registroEmail = '';
  registroContrasena = '';
  registroContrasenaRepetida = '';
  mostrarPassRegistro = false;
  errorRegistro = signal('');

  private authService = inject(AuthService);
  ngOnInit(): void {
    if (this.authService.isLoggedIn()) {
      this.router.navigate(['/rutinas']);
    }
  }

  registrarse() {
    this.errorRegistro.set('');

    if (!this.registroNombre || !this.registroEmail || !this.registroContrasena || !this.registroContrasenaRepetida) {
      this.errorRegistro.set('Por favor, rellena todos los campos.');
      this.limpiarPasswords();
      return;
    }
    if (!esEmailValido(this.registroEmail)) {
      this.errorRegistro.set('El formato del email no es válido.');
      this.limpiarPasswords();
      return;
    }
    if (this.registroContrasena.length < 8) {
      this.errorRegistro.set('La contraseña debe tener al menos 8 caracteres.');
      this.limpiarPasswords();
      return;
    }
    if (this.registroContrasena !== this.registroContrasenaRepetida) {
      this.errorRegistro.set('Las contraseñas no coinciden.');
      this.limpiarPasswords();
      return;
    }

    const body = { username: this.registroNombre, email: this.registroEmail, password: this.registroContrasena };

    this.http.post<{ token: string }>(`${environment.apiUrl}/auth/register`, body)
      .subscribe({
        next: (data) => {
          localStorage.setItem('token', data.token);
          this.router.navigate(['/rutinas']);
        },
        error: (err: HttpErrorResponse) => {
          this.limpiarPasswords();
          if (err.status === 0) {
            this.errorRegistro.set('No se pudo conectar con el servidor. Verifica tu conexión.');
          } else {
            this.errorRegistro.set('La contraseña debe incluir al menos una letra mayúscula, una letra minúscula, un número y un carácter especial.');          }
        }
      });
  }

  limpiarPasswords(): void{
    this.registroContrasena = '';
    this.registroContrasenaRepetida = '';
  }

  volver(): void {
    this.router.navigate(['']);
  }
}