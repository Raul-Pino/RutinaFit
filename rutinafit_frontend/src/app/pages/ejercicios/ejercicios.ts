import { Component, computed, signal, inject, OnInit } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { AuthService } from '../../core/auth.service';
import { cerrarModalGlobal } from '../../core/bootstrap-utils';

interface EjercicioInfo {
    id: number;
    codigo: number; // 1: Fuerza, 2: Cardio
    nombre: string;
    descripcion: string;
}

interface Ejercicio {
    id: number;
    param1: number; // Peso o Distancia
    param2: number; // Repeticiones o Tiempo
    ejercicioInfoId: number;
    nombreEjercicio?: string;
    codigoTipo?: number;
}

@Component({
    selector: 'app-ejercicios',
    standalone: true,
    imports: [FormsModule],
    templateUrl: './ejercicios.html',
    styleUrl: './ejercicios.scss',
})
export class Ejercicios implements OnInit {
    private http = inject(HttpClient);
    private route = inject(ActivatedRoute);
    private authService = inject(AuthService);
    private router = inject(Router);

    sesionId!: number;
    ejercicios = signal<Ejercicio[]>([]);
    catalogoEjercicios = signal<EjercicioInfo[]>([]);
    busqueda = signal('');

    ejercicioEditandoId: number | null = null;
    ejercicioInfoSeleccionadoId = signal<number | null>(null);

    nuevoEjercicio = {
        ejercicioInfoId: null as number | null,
        param1: null as number | null,
        param2: null as number | null
    };

    ejercicioSeleccionado = computed(() => {
        const id = this.ejercicioInfoSeleccionadoId();
        if (!id) return null;
        return this.catalogoEjercicios().find(e => e.id === Number(id)) || null;
    });

    ejerciciosMapeados = computed(() => {
        const catalogo = this.catalogoEjercicios();
        return this.ejercicios().map(ej => {
        const info = catalogo.find(c => c.id === ej.ejercicioInfoId);
        return {
            ...ej,
            nombreEjercicio: info?.nombre || ej.nombreEjercicio || 'Ejercicio Desconocido',
            codigoEjercicio: info?.codigo || ej.codigoTipo || 0
        };
        });
    });

    ejerciciosFiltrados = computed(() => {
        const filtro = this.busqueda().toLowerCase();
        return this.ejerciciosMapeados().filter((ej) =>
        ej.nombreEjercicio?.toLowerCase().includes(filtro)
        );
    });

    ngOnInit(): void {
        this.sesionId = Number(this.route.snapshot.paramMap.get('id'));
        this.cargarCatalogo();
        this.cargarEjercicios();
    }

    cargarCatalogo(): void {
        this.http.get<EjercicioInfo[]>(`${environment.apiUrl}/ejercicios-info`)
        .subscribe({
            next: (data) => this.catalogoEjercicios.set(data),
            error: () => console.error('No se pudo cargar el catálogo de ejercicios')
        });
    }

    cargarEjercicios(): void {
        this.http.get<Ejercicio[]>(`${environment.apiUrl}/ejercicios/sesion/${this.sesionId}`, { headers: this.authService.getTokenHeader() })
        .subscribe({
            next: (data) => this.ejercicios.set(data),
            error: () => console.error('No se pudieron cargar los ejercicios')
        });
    }

    eliminarEjercicio(id: number): void {
        if (!confirm('¿Estás seguro de que quieres eliminar esta serie?')) return;
        
        this.http.delete(`${environment.apiUrl}/ejercicios/${id}`, { headers: this.authService.getTokenHeader() })
        .subscribe({
            next: () => {
            this.ejercicios.update(lista => lista.filter(e => e.id !== id));
            },
            error: () => console.error('No se pudo eliminar la serie')
        });
    }

    abrirModalCrear(): void {
        this.ejercicioEditandoId = null;
        this.nuevoEjercicio = {
            ejercicioInfoId: null,
            param1: null,
            param2: null
        };
        this.ejercicioInfoSeleccionadoId.set(null);
    }

    abrirModalEditar(ejercicio: Ejercicio): void {
        this.ejercicioEditandoId = ejercicio.id;
        this.nuevoEjercicio = {
            ejercicioInfoId: ejercicio.ejercicioInfoId,
            param1: ejercicio.param1,
            param2: ejercicio.param2
        };
        this.ejercicioInfoSeleccionadoId.set(ejercicio.ejercicioInfoId);
    }

    guardarEjercicio(form: NgForm): void {
        if (form.invalid) return;

        const body = {
        ejercicioInfoId: Number(this.nuevoEjercicio.ejercicioInfoId),
        param1: this.nuevoEjercicio.param1,
        param2: this.nuevoEjercicio.param2
        };

        if (this.ejercicioEditandoId) {
            // Modo Edición
            this.http.put<Ejercicio>(`${environment.apiUrl}/ejercicios/${this.ejercicioEditandoId}`, body, { headers: this.authService.getTokenHeader() })
            .subscribe({
                next: async (ejercicio) => {
                this.ejercicios.update(lista => lista.map(e => e.id === this.ejercicioEditandoId ? ejercicio : e));
                await cerrarModalGlobal('modalNuevoEjercicio');
                form.resetForm();
                this.ejercicioInfoSeleccionadoId.set(null);
                },
                error: () => console.error('No se pudo actualizar la serie')
            });
        } else {
            // Modo Creación
            this.http.post<Ejercicio>(`${environment.apiUrl}/ejercicios/sesion/${this.sesionId}`, body, { headers: this.authService.getTokenHeader() })
            .subscribe({
                next: async (ejercicio) => {
                this.ejercicios.update(lista => [...lista, ejercicio]);
                await cerrarModalGlobal('modalNuevoEjercicio');
                form.resetForm();
                this.ejercicioInfoSeleccionadoId.set(null);
                },
                error: () => console.error('No se pudo guardar la serie')
            });
        }
    }

    volver(): void {
        this.router.navigate(['/sesiones', this.sesionId]);
    }

    cerrarModal(): void {
        cerrarModalGlobal('modalNuevoEjercicio');
    }
}