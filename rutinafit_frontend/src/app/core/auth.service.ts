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

  getRol(): string | null {
    const token = this.getToken();
    if (!token) return null;
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      return payload.rol ?? payload.role ?? payload.roles?.[0] ?? null;
    } catch {
      return null;
    }
  }

  cerrarSesion(): void {
    localStorage.removeItem('token');
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
}
