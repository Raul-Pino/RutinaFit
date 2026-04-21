import { Component, computed, signal, inject, OnInit } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { Router } from '@angular/router';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { AuthService } from '../../core/auth.service';
import { cerrarModalGlobal } from '../../core/bootstrap-utils';

interface Rutina {
  id: number;
  nombre: string;
  descripcion: string;
  imagen: string;
}

@Component({
  selector: 'app-rutinas',
  imports: [FormsModule],
  templateUrl: './rutinas.html',
  styleUrl: './rutinas.scss',
})
export class Rutinas implements OnInit {
  private http = inject(HttpClient);
  private router = inject(Router);
  private authService = inject(AuthService);

  rutinas = signal<Rutina[]>([]);
  busqueda = signal('');
  errorMensaje = signal('');

  // Propiedades crear/editar Rutina
  rutinaEditandoId: number | null = null;
  nuevaRutinaNombre = '';
  nuevaRutinaDescripcion = '';

  // Filtro de rutinas
  rutinasFiltradas = computed(() => {
    const filtro = this.busqueda().toLowerCase();
    return this.rutinas().filter((rutina) =>
      rutina.nombre.toLowerCase().includes(filtro)
    );
  });

  ngOnInit(): void {
    this.authService.comprobarToken();
    this.cargarRutinas();
  }

  cargarRutinas(): void {
        this.http
      .get<Rutina[]>(`${environment.apiUrl}/rutinas`, { headers: this.authService.getTokenHeader() })
      .subscribe({
        next: (data) => this.rutinas.set(data),
        error: () => console.error('No se pudieron cargar las rutinas')
      });
  }

  eliminarRutina(id: number): void {
    if (!confirm('¿Estás seguro de que quieres eliminar esta rutina?')) {
      return;
    }
    this.http.delete(`${environment.apiUrl}/rutinas/${id}`, { headers: this.authService.getTokenHeader() })
      .subscribe({
        next: () => {
          this.rutinas.set(this.rutinas().filter((rutina) => rutina.id !== id));
        },
        error: () => console.error('No se pudo eliminar la rutina')
      });
  }

  abrirModalCrear(): void {
    this.rutinaEditandoId = null;
    this.nuevaRutinaNombre = '';
    this.nuevaRutinaDescripcion = '';
  }

  abrirModalEditar(rutina: Rutina): void {
    this.rutinaEditandoId = rutina.id;
    this.nuevaRutinaNombre = rutina.nombre;
    this.nuevaRutinaDescripcion = rutina.descripcion;
  }

  guardarRutina(form: NgForm): void {
    if (form.invalid) return;

    const body = {
      nombre: this.nuevaRutinaNombre,
      descripcion: this.nuevaRutinaDescripcion
    };

    if (this.rutinaEditandoId) {
      // EDITAR

      this.http
        .put<Rutina>(`${environment.apiUrl}/rutinas/${this.rutinaEditandoId}`, body, { headers: this.authService.getTokenHeader() })
        .subscribe({
          next: async (rutinaActualizada) => {
            this.rutinas.update(lista => lista.map(r => r.id === this.rutinaEditandoId ? rutinaActualizada : r));
            await cerrarModalGlobal('modalNuevaRutina');
            form.resetForm();
          },
          error: (err: HttpErrorResponse) => {
            if(err.status === 0){
              this.errorMensaje.set('Hubo un problema al actualizar la rutina.');
            }else{
              this.errorMensaje.set(err.error.error);
            }
          }
        });
    } else {
      // CREAR

      this.http
        .post<Rutina>(`${environment.apiUrl}/rutinas`, body, { headers: this.authService.getTokenHeader() })
        .subscribe({
          next: async (rutina) => {
            this.rutinas.update(lista => [...lista, rutina]);
            await cerrarModalGlobal('modalNuevaRutina');
            form.resetForm();
          },
          error: (err: HttpErrorResponse) => {
            if(err.status === 0){
              this.errorMensaje.set('Hubo un problema al guardar la rutina.');
            }else{
              this.errorMensaje.set(err.error.error);
            }
          }
        });
    }
  }

  verDetalle(rutina: Rutina): void {
    this.router.navigate(['/sesiones', rutina.id]);
  }

}
