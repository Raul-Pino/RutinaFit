import { HttpHeaders } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Router } from '@angular/router';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private router = inject(Router);

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }


  esEntrenador(): boolean{
    const token = this.getToken();
    if (!token) return false;
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      return payload.esEntrenador ?? false;
    } catch {
      return false;
    }
  }

  getRol(): string | null {
    const token = this.getToken();
    if (!token) return null;
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      return payload.rol ?? null;
    } catch {
      return null;
    }
  }

  getNombre(): string | null{
    const token = this.getToken();
    if (!token) return null;
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      return payload.sub ?? null;
    } catch {
      return null;
    }
  }

  cerrarSesion(): void {
    localStorage.removeItem('token');
    this.router.navigate(['']);
  }

  getTokenHeader(): HttpHeaders {
    const token = this.getToken();
    if (!token) return new HttpHeaders();
    return new HttpHeaders({ 'Authorization': `Bearer ${token}` });
  }

  comprobarToken(): void{
    const token = this.getToken();
    if (!token) this.router.navigate(['']);
  }

  esAdmin(): void{
    this.comprobarToken();
    if (this.getRol() !== 'ADMIN') {
      this.router.navigate(['']);
    }
  }

  refrescarToken(nuevoToken: string): void {
    localStorage.setItem('token', nuevoToken);
  }
}
