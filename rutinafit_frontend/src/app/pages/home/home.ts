import { Component, inject, signal } from '@angular/core';
import { NgClass } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { environment } from '../../../environments/environment';

declare const bootstrap: any;

@Component({
  selector: 'app-home',
  imports: [NgClass, FormsModule],
  templateUrl: './home.html',
  styleUrl: './home.scss',
})
export class Home {
  private http = inject(HttpClient);

  // ==============================
  // ESTADO DE VISIBILIDAD DE CONTRASEÑAS
  // ==============================
  mostrarPassRegistro = false;
  mostrarPassRecuperar = false;

  // ==============================
  // CAMPOS DEL FORMULARIO DE LOGIN
  // ==============================
  loginEmail = '';
  loginContrasena = '';

  // ==============================
  // CAMPOS DEL FORMULARIO DE REGISTRO
  // ==============================
  registroNombre = '';
  registroEmail = '';
  registroContrasena = '';
  registroContrasenaRepetida = '';

  // ==============================
  // CAMPOS DEL FORMULARIO DE RECUPERAR CONTRASEÑA
  // ==============================
  recuperarEmail = '';
  recuperarContrasena = '';
  recuperarContrasenaConf = '';

  // ==============================
  // MENSAJES DE ERROR Y ÉXITO (Signals)
  // Angular 21 es zoneless: los signals notifican el cambio directamente
  // a la vista sin necesidad de zone.js, NgZone ni ChangeDetectorRef.
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
    // Blur ANTES de hide para evitar el warning de aria-hidden con foco activo
    (document.activeElement as HTMLElement)?.blur();
    this.obtenerModal(id)?.hide();
  }

  // Abre un modal y limpia todos los formularios/errores
  mostrarModal(id: string): void {
    this.limpiarFormularios();
    this.obtenerModal(id)?.show();
  }

  // Cambia entre modales: cierra el actual y abre el siguiente
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

    this.http.post<{ token: string }>(`${environment.apiUrl}/login`, body)
      .subscribe({
        next: (data) => {
          localStorage.setItem('token', data.token);
          window.location.href = '/rutinas';
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


    this.http.post<{ token: string }>(`${environment.apiUrl}/register`, body)
      .subscribe({
        next: (data) => {
          localStorage.setItem('token', data.token);
          window.location.href = '/rutinas';
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

    // TODO: llamar al back-end aquí
    this.errorRecuperar.set('Ese email no está registrado en el sistema.');
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
