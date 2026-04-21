import { Component, computed, signal, inject, OnInit } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { DatePipe, Location } from '@angular/common';
import { environment } from '../../../environments/environment';
import { AuthService } from '../../core/auth.service';
import { cerrarModalGlobal } from '../../core/bootstrap-utils';

interface Sesion {
  id: number;
  fecha: string;
  rutinaId: number;
}

@Component({
  selector: 'app-sesiones',
  standalone: true,
  imports: [FormsModule, DatePipe],
  templateUrl: './sesiones.html',
  styleUrl: './sesiones.scss',
})
export class Sesiones implements OnInit {
  private http = inject(HttpClient);
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private authService = inject(AuthService);
  private location = inject(Location);

  sesiones = signal<Sesion[]>([]);
  busqueda = signal('');
  rutinaId!: number;

  errorMensaje = signal('');
  nuevaSesionFecha = '';

  // Propiedades alumno
  alumnoId: number | null = null;
  alumnoUsername = '';

  sesionesFiltradas = computed(() => {
    const filtro = this.busqueda().toLowerCase();
    return this.sesiones().filter((sesion) =>
      sesion.fecha.toLowerCase().includes(filtro)
    );
  });

  ngOnInit(): void {   
    this.alumnoId = Number(this.route.snapshot.paramMap.get('alumnoId'));
    this.rutinaId = Number(this.route.snapshot.paramMap.get('rutinaId'));
    this.authService.comprobarToken();

    if(this.alumnoId){
      this.cargarNombreAlumno();
    }
    this.cargarSesiones();
  }
  
  cargarSesiones(): void {
    this.http
    .get<Sesion[]>(`${environment.apiUrl}/sesiones/rutina/${this.rutinaId}`, { headers: this.authService.getTokenHeader() })
    .subscribe({
      next: async (data) => {
        this.sesiones.set(data);
        await this.ordenarPorFecha();
      },
      error: (e) => console.error('No se pudieron cargar las sesiones')
    });
  }

  cargarNombreAlumno(): void {
      this.http.get(`${environment.apiUrl}/sesiones/${this.alumnoId}/propietario`, { headers: this.authService.getTokenHeader() })
      .subscribe({
        next: (data: any) => this.alumnoUsername = data.propietario,
        error: (e) => console.error('No se pudo cargar el alumno', e)
      });
  }

  eliminarSesion(id: number): void {
    if (!confirm('¿Estás seguro de que quieres eliminar esta sesión?')) {
      return;
    }
    this.http.delete(`${environment.apiUrl}/sesiones/${id}`, { headers: this.authService.getTokenHeader() })
      .subscribe({
        next: () => {
          this.sesiones.set(this.sesiones().filter((sesion) => sesion.id !== id));
        },
        error: () => console.error('No se pudo eliminar la sesión')
      });
  }

  abrirModalCrear(): void {
    const hoy = new Date();
    this.nuevaSesionFecha = hoy.toISOString().split('T')[0];
    this.errorMensaje.set('');
  }

  guardarSesion(form: NgForm): void {
    if (form.invalid) return;

    const body = { fecha: this.nuevaSesionFecha };

    this.http
      .post<Sesion>(`${environment.apiUrl}/sesiones/rutina/${this.rutinaId}`, body, { headers: this.authService.getTokenHeader() })
      .subscribe({
        next: async (sesion) => {
          this.sesiones.update(lista => [...lista, sesion]);
          await cerrarModalGlobal('modalNuevaSesion');
          form.resetForm();
          this.ordenarPorFecha();
        },
          error: (err: HttpErrorResponse) => {
            if(err.status === 0){
              this.errorMensaje.set('Hubo un problema al crea la sesión.');
            }else{
              this.errorMensaje.set(err.error.error);
            }
          }
      });
  }

  verDetalle(sesion: Sesion): void {
    this.router.navigate([`/sesion/${this.alumnoId}/${sesion.id}`]);
  }

  volver(): void {
    this.location.back();
  }

  ordenarPorFecha(): void {
    this.sesiones.update(lista => [...lista].sort((a, b) => new Date(b.fecha).getTime() - new Date(a.fecha).getTime()));
  }

  cerrarModal(): void {
    cerrarModalGlobal('modalNuevaSesion');
  }
}