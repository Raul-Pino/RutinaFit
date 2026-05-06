import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
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
  private route = inject(ActivatedRoute);

  token = null;
  email = '';
  password = '';
  passwordConfirmacion = '';
  mostrarPassRecuperar = false;
  errorRecuperar = signal('');
  exitoRecuperar = signal('');

  private authService = inject(AuthService);
  ngOnInit(): void {
    if (this.authService.isLoggedIn()) {
      this.router.navigate(['/rutinas']);
    }
    this.token = this.route.snapshot.params['token'];
    if(this.token){
      this.verificarToken();
    }
  }

  verificarToken(){
    this.http.get<boolean>(`${environment.apiUrl}/usuarios/verificar-token/${this.token}`)
      .subscribe({
        next: (data) => {
          console.log("Token valido");
        },
        error: (err: HttpErrorResponse) => {
          this.router.navigate(['']);
        }
      });
  }

  generarToken() {
    this.errorRecuperar.set('');
    this.exitoRecuperar.set('');

    if (!this.email) {
      this.errorRecuperar.set('Por favor, rellena todos los campos.');
      return;
    }
    if (!esEmailValido(this.email)) {
      this.errorRecuperar.set('El formato del email no es válido.');
      return;
    }

    const body = { email: this.email.trim() };

    this.http.post<{ token: string }>(`${environment.apiUrl}/usuarios/generar-token`, body)
      .subscribe({
        next: (data) => {
          this.exitoRecuperar.set('Email enviado correctamente');
          setTimeout(() => {
            this.router.navigate(['']);
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

  cambiarPassword() {
    this.errorRecuperar.set('');
    this.exitoRecuperar.set('');

    if (!this.password || !this.passwordConfirmacion) {
      this.errorRecuperar.set('Por favor, rellena todos los campos.');
      this.limpiarPasswords();
      return;
    }
    if (this.password.length < 8) {
      this.errorRecuperar.set('La contraseña debe tener al menos 8 caracteres.');
      this.limpiarPasswords();
      return;
    }
    if (this.password !== this.passwordConfirmacion) {
      this.errorRecuperar.set('Las contraseñas no coinciden.');
      this.limpiarPasswords();
      return;
    }

    const body = { token: this.token, password: this.password, passwordConfirmacion: this.passwordConfirmacion };

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
    this.password = '';
    this.passwordConfirmacion = '';
  }

  volver(): void {
    this.router.navigate(['']);
  }
}