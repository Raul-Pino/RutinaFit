import { Component, computed, signal, inject, OnInit } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { Location } from '@angular/common';
import { environment } from '../../../environments/environment';
import { AuthService } from '../../core/auth.service';
import { cerrarComponenteBS } from '../../core/bootstrap-utils';
import { PrimerParametro } from './primerParametro';
import { SegundoParametro } from './segundoParametro';

interface EjercicioInfo {
    id: number;
    codigo: number; // 1: Fuerza, 2: Cardio
    nombre: string;
}

interface Ejercicio {
    id: number;
    param1: number; // Peso o Distancia
    param2: number; // Repeticiones o Tiempo
    nombreEjercicio: string;
    idEjercicioInfo: number;
    codigoTipo: number | null;
    descripcion: string;
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
    private location = inject(Location);

    // Propiedades alumno
    alumnoId: number | null = null;
    alumnoUsername = '';

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

    ejerciciosFiltrados = computed(() => {
        const filtro = this.busqueda().toLowerCase();
        return this.ejercicios().filter((ej) =>
        ej.nombreEjercicio?.toLowerCase().includes(filtro)
        );
    });

    ngOnInit(): void {
        this.authService.comprobarToken();

        this.alumnoId = Number(this.route.snapshot.paramMap.get('alumnoId'));
        this.sesionId = Number(this.route.snapshot.paramMap.get('sesionId'));
        
        if(this.alumnoId){
            this.cargarNombreAlumno();
        }
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

    cargarNombreAlumno(): void {
        this.http.get(`${environment.apiUrl}/ejercicios/${this.alumnoId}/propietario`, { headers: this.authService.getTokenHeader() })
        .subscribe({
            next: (data: any) => this.alumnoUsername = data.propietario,
            error: (e) => console.error('No se pudo cargar el alumno', e)
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
            this.ejercicios.set(this.ejercicios().filter(e => e.id !== id));
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
        console.log(ejercicio);
        console.log(this.catalogoEjercicios());
        this.ejercicioEditandoId = ejercicio.id;
        this.nuevoEjercicio = {
            ejercicioInfoId: ejercicio.idEjercicioInfo,
            param1: ejercicio.param1,
            param2: ejercicio.param2
        };
        this.ejercicioInfoSeleccionadoId.set(ejercicio.idEjercicioInfo);
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
                await cerrarComponenteBS('modalNuevoEjercicio');
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
                await cerrarComponenteBS('modalNuevoEjercicio');
                form.resetForm();
                this.ejercicioInfoSeleccionadoId.set(null);
                },
                error: () => console.error('No se pudo guardar la serie')
            });
        }
    }

    volver(): void {
        this.location.back();
    }

    cerrarModal(): void {
        cerrarComponenteBS('modalNuevoEjercicio');
    }



    getParam1(id: number): string{
        return PrimerParametro[id] || 'Parámetro 1';
    }

    getParam2(id: number): string{
        return SegundoParametro[id] || 'Parámetro 2';
    }
}