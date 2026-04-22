import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private router = inject(Router);
  private http = inject(HttpClient);


  getToken(): string | null {
    return localStorage.getItem('token');
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  getEntrenador(): number | null{
      return this.buscarEnToken('entrenador');
    }

  esEntrenador(): boolean{
    if(this.buscarEnToken('esEntrenador')) return true;
    return false;
  }

  getRol(): string | null {
    return this.buscarEnToken('rol');
  }

  getId(): number | null {
    return this.buscarEnToken('id');
  }

  getNombre(): string | null{
    return this.buscarEnToken('sub');
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

  refrescarToken(): void{
      this.http.post<any>(`${environment.apiUrl}/auth/refresh-token`, {}, { headers: this.getTokenHeader() })
      .subscribe({
          next: (data) => {
              console.log(data);
              localStorage.setItem('token', data.token);
              window.location.reload(); // Recargar para actualizar el nombre en el navbar y demás componentes
          },
          error: (err: HttpErrorResponse) => {
              console.error('Error al refrescar token', err);
          }
      });
  }


  private buscarEnToken(clave: string): any {
      const token = this.getToken();
      if (!token) return null;
      try {
        const payload = JSON.parse(atob(token.split('.')[1]));
        return payload[clave] ?? null;
      } catch {
        return null;
      }
  }
}
