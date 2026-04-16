import { Component, inject, signal, OnInit } from '@angular/core';
import { NgClass } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Router } from '@angular/router';
import { environment } from '../../../environments/environment';
import { AuthService } from '../../core/auth.service';
import { cerrarModalGlobal } from '../../core/bootstrap-utils';

declare const bootstrap: any;

@Component({
  selector: 'app-home',
  imports: [NgClass, FormsModule],
  templateUrl: './home.html',
  styleUrl: './home.scss',
})
export class Home implements OnInit {
  private http = inject(HttpClient);
  private router = inject(Router);
  private authService = inject(AuthService);

  ngOnInit(): void {
    if (this.authService.isLoggedIn()) {
      this.router.navigate(['/rutinas']);
    }
  }

  // ==============================
  // ESTADO DE VISIBILIDAD DE CONTRASEÑAS
  // ==============================
  mostrarPassRegistro = false;
  mostrarPassRecuperar = false;

  // ==============================
  // CAMPOS DEL FORMULARIO
  // ==============================
  loginEmail = '';
  loginContrasena = '';

  registroNombre = '';
  registroEmail = '';
  registroContrasena = '';
  registroContrasenaRepetida = '';

  recuperarEmail = '';
  recuperarContrasena = '';
  recuperarContrasenaConf = '';

  // ==============================
  // MENSAJES DE ERROR
  // ==============================
  errorLogin    = signal('');
  errorRegistro = signal('');
  errorRecuperar = signal('');
  exitoRecuperar = signal('');

  // ==============================
  // MODALES
  // ==============================

  private obtenerModal(id: string): any {
    const elemento = document.getElementById(id);
    return elemento ? bootstrap.Modal.getOrCreateInstance(elemento) : null;
  }

  private cerrarModal(id: string): void {
    (document.activeElement as HTMLElement)?.blur();
    this.obtenerModal(id)?.hide();
  }

  mostrarModal(id: string): void {
    this.limpiarFormularios();
    this.obtenerModal(id)?.show();
  }

  cambiarModal(cerrar: string, abrir: string): void {
    this.cerrarModal(cerrar);
    setTimeout(() => this.mostrarModal(abrir), 400);
  }

  // ==============================
  // INICIAR SESIÓN
  // ==============================

  iniciarSesion(): void {
    this.errorLogin.set('');

    if (!this.loginEmail || !this.loginContrasena) {
      this.errorLogin.set('Por favor, rellena todos los campos.');
      return;
    }
    if (!this.esEmailValido(this.loginEmail)) {
      this.errorLogin.set('El formato del email no es válido.');
      return;
    }

    const body = { email: this.loginEmail, password: this.loginContrasena };

    this.http.post<{ token: string }>(`${environment.apiUrl}/auth/login`, body)
      .subscribe({
        next: async (data) => {
          localStorage.setItem('token', data.token);
          await cerrarModalGlobal('modalLogin');
          this.router.navigate(['/rutinas']);
        },
        error: (err: HttpErrorResponse) => {
          if (err.status === 0) {
            this.errorLogin.set('No se pudo conectar con el servidor. Verifica tu conexión.');
          } else {
            this.errorLogin.set('Email o contraseña incorrectos.');
          }
        }
      });
  }

  // ==============================
  // REGISTRARSE
  // ==============================
  registrarse(): void {
    this.errorRegistro.set('');

    if (!this.registroNombre || !this.registroEmail || !this.registroContrasena || !this.registroContrasenaRepetida) {
      this.errorRegistro.set('Por favor, rellena todos los campos.');
      return;
    }
    if (!this.esEmailValido(this.registroEmail)) {
      this.errorRegistro.set('El formato del email no es válido.');
      return;
    }
    if (this.registroContrasena.length < 8) {
      this.errorRegistro.set('La contraseña debe tener al menos 8 caracteres.');
      return;
    }
    if (this.registroContrasena !== this.registroContrasenaRepetida) {
      this.errorRegistro.set('Las contraseñas no coinciden.');
      return;
    }

    const body = { username: this.registroNombre, email: this.registroEmail, password: this.registroContrasena };


    this.http.post<{ token: string }>(`${environment.apiUrl}/auth/register`, body)
      .subscribe({
        next: async (data) => {
          localStorage.setItem('token', data.token);
          await cerrarModalGlobal('modalRegistro');
          this.router.navigate(['/rutinas']);
        },
        error: (err: HttpErrorResponse) => {
          if (err.status === 0) {
            this.errorRegistro.set('No se pudo conectar con el servidor. Verifica tu conexión.');
          } else {
            this.errorRegistro.set(err.error.error);
          }
        }
      });
  }

  // ==============================
  // RECUPERAR CONTRASEÑA
  // ==============================
  recuperarContrasenaEnviar(): void {
    this.errorRecuperar.set('');
    this.exitoRecuperar.set('');

    if (!this.recuperarEmail || !this.recuperarContrasena || !this.recuperarContrasenaConf) {
      this.errorRecuperar.set('Por favor, rellena todos los campos.');
      return;
    }
    if (!this.esEmailValido(this.recuperarEmail)) {
      this.errorRecuperar.set('El formato del email no es válido.');
      return;
    }
    if (this.recuperarContrasena.length < 8) {
      this.errorRecuperar.set('La contraseña debe tener al menos 8 caracteres.');
      return;
    }
    if (this.recuperarContrasena !== this.recuperarContrasenaConf) {
      this.errorRecuperar.set('Las contraseñas no coinciden.');
      return;
    }

    const body = { email: this.recuperarEmail, password: this.recuperarContrasena, passwordConfirmacion: this.recuperarContrasenaConf };

    console.log(body);

    this.http.post<{ token: string }>(`${environment.apiUrl}/usuarios/recuperar-password`, body)
      .subscribe({
        next: (data) => {
          this.exitoRecuperar.set('Contraseña recuperada correctamente');
          setTimeout(() => {
            window.location.href = '';
          }, 1000);
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

  // ==============================
  // MÉTODOS AUXILIARES
  // ==============================

  private esEmailValido(email: string): boolean {
    const patron = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return patron.test(email);
  }

  private limpiarFormularios(): void {
    this.loginEmail = '';
    this.loginContrasena = '';
    this.registroNombre = '';
    this.registroEmail = '';
    this.registroContrasena = '';
    this.registroContrasenaRepetida = '';
    this.recuperarEmail = '';
    this.recuperarContrasena = '';
    this.recuperarContrasenaConf = '';
    this.errorLogin.set('');
    this.errorRegistro.set('');
    this.errorRecuperar.set('');
    this.exitoRecuperar.set('');
  }
}
