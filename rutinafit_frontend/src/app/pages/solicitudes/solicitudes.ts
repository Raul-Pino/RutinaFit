import { HttpClient } from "@angular/common/http";
import { Component, computed, inject, OnInit, signal } from "@angular/core";
import { AuthService } from "../../core/auth.service";
import { FormsModule } from "@angular/forms";
import { environment } from "../../../environments/environment";

interface Solicitud{
    id: number;
    remitente: {
        id: number;
        username: string;
        fotoPerfil: string;
        esEntrenador: boolean;
        email: string;
    },
    tipo: string;
}


@Component({
    selector: 'app-solicitudes',
    imports: [FormsModule],
    templateUrl: './solicitudes.html',
    styleUrl: './solicitudes.scss',
})
export class Solicitudes implements OnInit {
    private http = inject(HttpClient);
    private authService = inject(AuthService);

    solicitudes = signal<Solicitud[]>([]);
    busqueda = signal('');

    msgError = signal('');
    msgExito = signal('');


    entrenador: number | null = null;

    solicitudesAmistadFiltradas = computed(() => {
        const filtro = this.busqueda().toLowerCase();
        return this.solicitudes().filter((solicitud) =>
        solicitud.remitente.username.toLowerCase().includes(filtro) && solicitud.tipo === 'AMISTAD'
        );
    });
    solicitudesEntrenamientoFiltradas = computed(() => {
        const filtro = this.busqueda().toLowerCase();
        return this.solicitudes().filter((solicitud) =>
        solicitud.remitente.username.toLowerCase().includes(filtro) && solicitud.tipo === 'ENTRENAMIENTO'
        );
    });


    ngOnInit(): void {
        this.authService.comprobarToken();
        this.cargarSolicitudes();

        this.entrenador = this.authService.getEntrenador();
    }

    cargarSolicitudes(): void{
        this.http.get<Solicitud[]>(`${environment.apiUrl}/solicitudes/pendientes`, { headers: this.authService.getTokenHeader() })
        .subscribe({
            next: (data) => {
                this.solicitudes.set(data);
            },
            error: (e) => console.error('No se pudieron cargar las solicitudes ', e)
        });
    }

    aceptarSolicitud(id: number): void{
        this.http.post(`${environment.apiUrl}/solicitudes/${id}/aceptar`, {}, { headers: this.authService.getTokenHeader() })
            .subscribe({
                next: () => {
                    this.msgExito.set('Solicitud aceptada correctamente');
                    this.cargarSolicitudes();
                },
                error: (e) => {
                    console.error('No se pudo aceptar la solicitud ', e);
                    this.msgError.set(e.error.error);
                }
            });
+            setTimeout(() => {this.msgExito.set(''); this.msgError.set(''); }, 4000);
    }

    rechazarSolicitud(id: number): void{
        this.http.post(`${environment.apiUrl}/solicitudes/${id}/rechazar`, {}, { headers: this.authService.getTokenHeader() })
            .subscribe({
                next: () => {
                    this.msgExito.set('Solicitud rechazada correctamente');
                    this.cargarSolicitudes();
                },
                error: (e) => {
                    console.error('No se pudo rechazar la solicitud ', e);
                    this.msgError.set(e.error.error);
                }
            });
+            setTimeout(() => {this.msgExito.set(''); this.msgError.set(''); }, 4000);
    }
}