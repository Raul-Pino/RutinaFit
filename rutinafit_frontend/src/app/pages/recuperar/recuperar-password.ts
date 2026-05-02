import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../core/auth.service';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { esEmailValido } from '../../core/utils';

@Component({
  selector: 'app-recuperar-password',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './recuperar-password.html'
})
export class RecuperarPassword {
  private http = inject(HttpClient);
  private router = inject(Router);
  recuperarEmail = '';
  recuperarContrasena = '';
  recuperarContrasenaConf = '';
  mostrarPassRecuperar = false;
  errorRecuperar = signal('');
  exitoRecuperar = signal('');

  private authService = inject(AuthService);
  ngOnInit(): void {
    if (this.authService.isLoggedIn()) {
      this.router.navigate(['/rutinas']);
    }
  }

  recuperarContrasenaEnviar() {
    this.errorRecuperar.set('');
    this.exitoRecuperar.set('');

    if (!this.recuperarEmail || !this.recuperarContrasena || !this.recuperarContrasenaConf) {
      this.errorRecuperar.set('Por favor, rellena todos los campos.');
      this.limpiarPasswords();
      return;
    }
    if (!esEmailValido(this.recuperarEmail)) {
      this.errorRecuperar.set('El formato del email no es válido.');
      this.limpiarPasswords();
      return;
    }
    if (this.recuperarContrasena.length < 8) {
      this.errorRecuperar.set('La contraseña debe tener al menos 8 caracteres.');
      this.limpiarPasswords();
      return;
    }
    if (this.recuperarContrasena !== this.recuperarContrasenaConf) {
      this.errorRecuperar.set('Las contraseñas no coinciden.');
      this.limpiarPasswords();
      return;
    }

    const body = { email: this.recuperarEmail, password: this.recuperarContrasena, passwordConfirmacion: this.recuperarContrasenaConf };

    this.http.post<{ token: string }>(`${environment.apiUrl}/usuarios/recuperar-password`, body)
      .subscribe({
        next: (data) => {
          this.exitoRecuperar.set('Contraseña recuperada correctamente');
          setTimeout(() => {
            this.router.navigate(['/login']); // Te redirigimos al login usando el router de Angular
          }, 1500);
        },
        error: (err: HttpErrorResponse) => {
          if (err.status === 0) {
            this.errorRecuperar.set('No se pudo conectar con el servidor. Verifica tu conexión.');
          } else {
            this.errorRecuperar.set(err.error.error);
          }
        }
      });
  }

  limpiarPasswords(): void{
    this.recuperarContrasena = '';
    this.recuperarContrasenaConf = '';
  }

  volver(): void {
    this.router.navigate(['']);
  }
}