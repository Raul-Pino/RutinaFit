import { HttpClient } from "@angular/common/http";
import { Component, computed, inject, OnInit, signal } from "@angular/core";
import { AuthService } from "../../core/auth.service";
import { FormsModule } from "@angular/forms";
import { environment } from "../../../environments/environment";

interface Usuario{
    id: number;
    username: string;
    fotoPerfil: string;
    esEntrenador: boolean;
    email: string;
}


@Component({
    selector: 'app-social',
    imports: [FormsModule],
    templateUrl: './social.html',
    styleUrl: './social.scss',
})
export class Social implements OnInit {
    private http = inject(HttpClient);
    private authService = inject(AuthService);
    id = this.authService.getId();


    usuarios = signal<Usuario[]>([]);
    busqueda = signal('');

    msgError = signal('');
    msgExito = signal('');


    tieneEntrenador: number | null = null;
    entrenador = signal<Usuario> ({id: 0, username: '', fotoPerfil: '', esEntrenador: true, email: ''});

    usuariosFiltrados = computed(() => {
        const filtro = this.busqueda().toLowerCase();
        return this.usuarios().filter((usuario) =>
        usuario.username.toLowerCase().includes(filtro)
        );
    });


    ngOnInit(): void {
        this.authService.comprobarToken();
        this.cargarAmigos();

        this.tieneEntrenador = this.authService.getEntrenador();
        if(this.tieneEntrenador) this.cargarEntrenador();
    }

    cargarAmigos(): void{
        this.http.get<Usuario[]>(`${environment.apiUrl}/amistades`, { headers: this.authService.getTokenHeader() })
        .subscribe({
            next: (data) => {
                this.usuarios.set(data);
            },
            error: (e) => console.error('No se pudieron cargar los amigos ', e)
        });
    }

    cargarEntrenador(): void{
        this.http.get<Usuario>(`${environment.apiUrl}/usuarios/buscar/${this.tieneEntrenador}`, { headers: this.authService.getTokenHeader() })
        .subscribe({
            next: (data) => {
                this.entrenador.set(data);
            },
            error: (e) => console.error('No se pudo cargar el entrenador ', e)
        });
    }

    eliminarAmistad(id: number): void{
        this.http.delete(`${environment.apiUrl}/amistades/${id}`, { headers: this.authService.getTokenHeader() })
            .subscribe({
                next: () => {
                    this.msgExito.set('Amistad eliminada correctamente');
                    this.cargarAmigos();
                },
                error: (e) => {
                    console.error('No se pudo eliminar la amistad ', e);
                    this.msgError.set(e.error.error);
                }
            });
            setTimeout(() => {this.msgExito.set(''); this.msgError.set(''); }, 4000);
    }

    eliminarEntrenador(): void{
        this.http.post(`${environment.apiUrl}/usuarios/entrenador/eliminar/${this.id}`, {}, { headers: this.authService.getTokenHeader() })
            .subscribe({
                next: () => {
                    this.msgExito.set('Entrenador eliminado correctamente');
                    this.authService.refrescarToken();
                    this.cargarEntrenador();
                },
                error: (e) => {
                    console.error('No se pudo eliminar el entrenador ', e);
                    this.msgError.set(e.error.error);
                }
            });
            setTimeout(() => {this.msgExito.set(''); this.msgError.set(''); }, 4000);
    }
}