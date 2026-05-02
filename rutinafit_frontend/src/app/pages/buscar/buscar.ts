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
    esAmigo: boolean;
}


@Component({
    selector: 'app-buscar',
    imports: [FormsModule],
    templateUrl: './buscar.html',
    styleUrl: './buscar.scss',
})
export class Buscar implements OnInit {
    private http = inject(HttpClient);
    private authService = inject(AuthService);

    esEntrenador = signal(false);

    usuarios = signal<Usuario[]>([]);
    busqueda = signal('');

    msgError = signal('');
    msgExito = signal('');


    entrenador: number | null = null;

    usuariosFiltrados = computed(() => {
        let filtrados = this.usuarios();
        if(this.esEntrenador()){
            filtrados = filtrados.filter(usuario => usuario.esEntrenador);
        }

        const filtro = this.busqueda().toLowerCase();
        return filtrados.filter((usuario) =>
        usuario.username.toLowerCase().includes(filtro)
        );
    });


    ngOnInit(): void {
        this.authService.comprobarToken();
        this.cargarUsuarios();

        this.entrenador = this.authService.getEntrenador();
    }

    cargarUsuarios(): void{
        this.http.get<Usuario[]>(`${environment.apiUrl}/usuarios/buscar`, { headers: this.authService.getTokenHeader() })
        .subscribe({
            next: (data) => {
                this.usuarios.set(data);
            },
            error: (e) => console.error('No se pudieron cargar los usuarios ', e)
        });
    }

    enviarSolicitudEntrenador(id: number): void{
        const body = { destinatarioId: id, tipo: 'ENTRENAMIENTO'};
        this.http.post(`${environment.apiUrl}/solicitudes/enviar`, body, { headers: this.authService.getTokenHeader() })
            .subscribe({
                next: () => {
                    this.msgExito.set('Solicitud enviada correctamente');
                },
                error: (e) => {
                    console.error('Error al enviar la solicitud ', e);
                    this.msgError.set(e.error.error);
                }
            });
            setTimeout(() => {this.msgExito.set(''); this.msgError.set(''); }, 4000);
    }

    enviarSolicitudAmistad(id: number): void{
        const body = { destinatarioId: id, tipo: 'AMISTAD'};
        this.http.post(`${environment.apiUrl}/solicitudes/enviar`, body, { headers: this.authService.getTokenHeader() })
            .subscribe({
                next: () => {
                    this.msgExito.set('Solicitud enviada correctamente');
                },
                error: (e) => {
                    console.error('Error al enviar la solicitud ', e);
                    this.msgError.set(e.error.error);
                }
            });
            setTimeout(() => {this.msgExito.set(''); this.msgError.set(''); }, 4000);
    }
}