import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../../core/auth.service';
import { environment } from '../../../environments/environment';
import { FormsModule, NgForm } from '@angular/forms';



interface EjercicioInfo {
    id: number;
    codigo: number; // 1: Fuerza, 2: Cardio
    nombre: string;
    descripcion: string;
}

@Component({
    selector: 'app-admin',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './admin.html',
    styleUrl: './admin.scss'
})
export class Admin implements OnInit {
    private http = inject(HttpClient);
    private authService = inject(AuthService);


    catalogoEjercicios = signal<EjercicioInfo[]>([]);
    busqueda = signal('');
    errorForm = signal('');
    errorBorrar = signal('');

    // Propiedades crear/editar Ejercicio
    ejercicioEditandoId: number | null = null;
    nuevoEjercicioNombre = '';
    nuevoEjercicioDescripcion = '';
    nuevoEjercicioCodigo = 0;

    
    ejerciciosFiltrados = computed(() => {
    const filtro = this.busqueda().toLowerCase();
    return this.catalogoEjercicios().filter((rutina) =>
        rutina.nombre.toLowerCase().includes(filtro)
        );
    });


    ngOnInit(): void {
        this.authService.esAdmin();
        this.cargarCatalogo();
    }

    cargarCatalogo(): void {
        this.http.get<EjercicioInfo[]>(`${environment.apiUrl}/ejercicios-info`)
        .subscribe({
            next: async (data) => {
                this.catalogoEjercicios.set(data)
                await this.odernarPorCodigo();
            },
            error: () => console.error('No se pudo cargar el catálogo de ejercicios')
        });
    }

    odernarPorCodigo(): void {
        this.catalogoEjercicios.update(lista => [...lista].sort((a, b) => {
            if( a.codigo - b.codigo !== 0) return a.codigo - b.codigo;
            return a.nombre.localeCompare(b.nombre);
        }))
    }

    editarEjercicio(ejercicio: EjercicioInfo): void {
        this.ejercicioEditandoId = ejercicio.id;
        this.nuevoEjercicioNombre = ejercicio.nombre;
        this.nuevoEjercicioDescripcion = ejercicio.descripcion;
        this.nuevoEjercicioCodigo = ejercicio.codigo;
    }

    eliminarEjercicio(id: number): void {
        if (!confirm('¿Estás seguro de que quieres eliminar este ejercicio?')) {
            return;
        }
        this.http.delete(`${environment.apiUrl}/ejercicios-info/${id}`, { headers: this.authService.getTokenHeader() })
        .subscribe({
            next: () => {
                this.catalogoEjercicios.set(this.catalogoEjercicios().filter((ej) => ej.id !== id));
            },
            error: (e) => {
                console.error('No se pudo eliminar el ejercicio', e)
                this.errorBorrar.set('No se pudo eliminar el ejercicio. Inténtalo de nuevo más tarde.');
            }
        });
    }

    cancelarFormulario(): void{
        this.ejercicioEditandoId = null;
        this.nuevoEjercicioNombre = '';
        this.nuevoEjercicioDescripcion = '';
        this.nuevoEjercicioCodigo = 0;
    }

    guardarEjercicio(form: NgForm): void {
        if (form.invalid) return;
        
        const body = {
        nombre: this.nuevoEjercicioNombre,
        descripcion: this.nuevoEjercicioDescripcion,
        codigo: this.nuevoEjercicioCodigo
        };

        if(this.ejercicioEditandoId){
            // EDITAR

            this.http.
                put<EjercicioInfo>(`${environment.apiUrl}/ejercicios-info/${this.ejercicioEditandoId}`, body, { headers: this.authService.getTokenHeader() })
                .subscribe({
                    next: (data) => {
                        const index = this.catalogoEjercicios().findIndex(e => e.id === this.ejercicioEditandoId);
                        if (index !== -1) {
                            const actualizada = [...this.catalogoEjercicios()];
                            actualizada[index] = data;
                            this.catalogoEjercicios.set(actualizada);
                        }
                        this.cancelarFormulario();
                    },
                    error: (e) => {
                        console.error('No se pudo actualizar el ejercicio', e);
                        this.errorForm.set('No se pudo actualizar el ejercicio. Inténtalo de nuevo más tarde.');
                    }
                });

        }else{
            // CREAR

            this.http.
                post<EjercicioInfo>(`${environment.apiUrl}/ejercicios-info`, body, { headers: this.authService.getTokenHeader() })
                .subscribe({
                    next: (data) => {
                        this.catalogoEjercicios.update(lista => [...lista, data]);
                        this.cancelarFormulario();
                    },
                    error: (e) => {
                        console.error('No se pudo crear el ejercicio', e);
                        this.errorForm.set('No se pudo crear el ejercicio. Inténtalo de nuevo más tarde.');
                    }
                });
        }
    }
}
