import { Component, computed, signal, inject, OnInit, ViewChild, ElementRef } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { AuthService } from '../../core/auth.service';
import { cerrarComponenteBS } from '../../core/bootstrap-utils';

interface Rutina {
  id: number;
  nombre: string;
  descripcion: string;
  fotoRutina: string;
}

@Component({
  selector: 'app-rutinas',
  imports: [FormsModule],
  templateUrl: './rutinas.html',
  styleUrl: './rutinas.scss',
})
export class Rutinas implements OnInit {
  private http = inject(HttpClient);
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private authService = inject(AuthService);

  @ViewChild('fileInput') fileInput!: ElementRef<HTMLInputElement>;

  rutinas = signal<Rutina[]>([]);
  busqueda = signal('');
  errorMensaje = signal('');

  // Propiedades alumno
  alumnoId: number | null = null;
  alumnoUsername = '';

  // Propiedades crear/editar Rutina
  rutinaEditandoId: number | null = null;
  nuevaRutinaNombre = '';
  nuevaRutinaDescripcion = '';
  nuevaFotoRutina: File | null = null;

  // Filtro de rutinas
  rutinasFiltradas = computed(() => {
    const filtro = this.busqueda().toLowerCase();
    return this.rutinas().filter((rutina) =>
      rutina.nombre.toLowerCase().includes(filtro)
    );
  });

  ngOnInit(): void {
    this.alumnoId = Number(this.route.snapshot.paramMap.get('alumnoId'));
    this.authService.comprobarToken();

    if(this.alumnoId){
      this.cargarRutinasAlumno();
    }else{
      this.cargarRutinas();
    }
  }

  cargarRutinas(): void {
      this.http
      .get<Rutina[]>(`${environment.apiUrl}/rutinas`, { headers: this.authService.getTokenHeader() })
      .subscribe({
        next: (data) => this.rutinas.set(data),
        error: () => console.error('No se pudieron cargar las rutinas')
      });
  }

  cargarRutinasAlumno(): void {
      this.http
      .get<Rutina[]>(`${environment.apiUrl}/rutinas/alumnos/${this.alumnoId}`, { headers: this.authService.getTokenHeader() })
      .subscribe({
        next: (data) => this.rutinas.set(data),
        error: () => console.error('No se pudieron cargar las rutinas')
      });

      this.http.get(`${environment.apiUrl}/rutinas/${this.alumnoId}/propietario`, { headers: this.authService.getTokenHeader() })
      .subscribe({
        next: (data: any) => this.alumnoUsername = data.propietario,
        error: (e) => console.error('No se pudo cargar el alumno', e)
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
    this.nuevaFotoRutina = null;
    this.limpiarInputArchivo();
    this.errorMensaje.set('');
  }

  abrirModalEditar(rutina: Rutina): void {
    this.rutinaEditandoId = rutina.id;
    this.nuevaRutinaNombre = rutina.nombre;
    this.nuevaRutinaDescripcion = rutina.descripcion;
    this.nuevaFotoRutina = null;
    this.limpiarInputArchivo();
    this.errorMensaje.set('');
  }

  guardarRutina(form: NgForm): void {
    if (form.invalid) return;

    const dto = {
      nombre: this.nuevaRutinaNombre,
      descripcion: this.nuevaRutinaDescripcion
    };
    let apiUrl = '';
    
    if(this.alumnoId && !this.rutinaEditandoId){
      apiUrl = `${environment.apiUrl}/rutinas/alumnos/${this.alumnoId}`;
    }else{
      apiUrl = `${environment.apiUrl}/rutinas`;
    }

    const body = new FormData();

    body.append('rutina', new Blob([JSON.stringify(dto)], { type: 'application/json' }));

    if (this.nuevaFotoRutina) {
        body.append('fotoRutina', this.nuevaFotoRutina);
    }

    if (this.rutinaEditandoId) {
      // EDITAR

      this.http
        .put<Rutina>(`${apiUrl}/${this.rutinaEditandoId}`, body, { headers: { 'Authorization': `Bearer ${this.authService.getToken()}` } })
        .subscribe({
          next: async (rutinaActualizada) => {
            if(this.alumnoId)this.cargarRutinasAlumno();
            else this.cargarRutinas();
            await cerrarComponenteBS('modalNuevaRutina');
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
        .post<Rutina>(`${apiUrl}`, body, { headers: { 'Authorization': `Bearer ${this.authService.getToken()}` } })
        .subscribe({
          next: async (rutina) => {
            if(this.alumnoId)this.cargarRutinasAlumno();
            else this.cargarRutinas();
            await cerrarComponenteBS('modalNuevaRutina');
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

    onFotoRutinaSeleccionada(event: any): void {
      const file: File = event.target.files[0];
      if (file.size > 1024 * 1024) { // 1MB
          this.errorMensaje.set('La imagen no puede superar 1MB');
          event.target.value = '';
          return;
      }
      this.errorMensaje.set('');
      if (!file) return;
      this.nuevaFotoRutina = file;
    }

    limpiarInputArchivo(): void {
      if (this.fileInput) {
        this.fileInput.nativeElement.value = '';
      }
    }

  verDetalle(rutina: Rutina): void {
    this.router.navigate([`/sesiones/${this.alumnoId}/${rutina.id}`]);
  }

}
