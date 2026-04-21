import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../core/auth.service';
import { environment } from '../../../environments/environment';

interface Alumno {
    id: number;
    username: string;
    email: string;
    fotoPerfil: string;
    rol: string;
    esEntrenador: boolean;
}

@Component({
    selector: 'app-alumnos',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './alumnos.html',
    styleUrl: './alumnos.scss'
})
export class Alumnos implements OnInit {
    private http = inject(HttpClient);
    private router = inject(Router);
    private authService = inject(AuthService);
    
    busqueda = signal('');
    alumnos = signal<Alumno[]>([]);
    

    alumnosFiltrados = computed(() => {
        const filtro = this.busqueda().toLowerCase();
        return this.alumnos()
    });

    ngOnInit(): void {
        this.authService.comprobarToken();
        this.cargarAlumnos();
    }

    cargarAlumnos(): void {
        this.http
        .get<Alumno[]>(`${environment.apiUrl}/usuarios/alumnos`, { headers: this.authService.getTokenHeader() })
        .subscribe({
            next: (data) => {
                this.alumnos.set(data)
            },
            error: (e) => console.error('No se pudieron cargar los alumnos ', e)
        });
    }


    verRutinas(id: number): void{
        this.router.navigate(['/rutinas/', id]);
    }

    eliminarAlumno(id: number): void{
        if (!confirm('¿Estás seguro de que deseas eliminar este alumno?')) return;
        
        this.http.
        post(`${environment.apiUrl}/usuarios/entrenador/eliminar/${id}`, {}, { headers: this.authService.getTokenHeader() })
        .subscribe({
            next: () => {
                alert('Alumno eliminado correctamente');
                this.alumnos.set(this.alumnos().filter(a => a.id !== id));
            },
            error: (e) => {
                console.error('Error al eliminar el alumno ', e);
                alert('No se pudo eliminar el alumno');
            }
        });
    }
}