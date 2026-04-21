import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../core/auth.service';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { esEmailValido } from '../../core/utils';

@Component({
    selector: 'app-perfil',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './perfil.html',
    styleUrl: './perfil.scss'
})
export class Perfil {
    private http = inject(HttpClient);
    private router = inject(Router);
    private authService = inject(AuthService);

    username = '';
    email = '';
    fotoPerfil = '';
    esEntrenador = false;
    editError = signal('');
    editSuccess = signal('');

    passwordActual = '';
    passwordNueva = '';
    passwordConfirmar = '';
    passwordError = signal('');
    passwordSuccess = signal('');

    deleteError = signal('');

    loadingPerfil = signal(true);
    loadingEdit = signal(false);
    loadingPassword = signal(false);
    loadingDelete = signal(false);

    ngOnInit(): void {
        this.authService.comprobarToken();
        this.cargarPerfil();
    }

    cargarPerfil(): void {
        this.loadingPerfil.set(true);
        const headers = this.authService.getTokenHeader();
        
        this.http.get<any>(`${environment.apiUrl}/usuarios/perfil`, { headers })
        .subscribe({
            next: (data) => {
                this.username = data.username;
                this.email = data.email;
                this.fotoPerfil = data.fotoPerfil;
                this.esEntrenador = data.esEntrenador;
                this.loadingPerfil.set(false);
            },
            error: (err: HttpErrorResponse) => {
            this.editError.set('Error al cargar el perfil');
            this.loadingPerfil.set(false);
            }
        });
    }

    editarPerfil(): void {
        this.editError.set('');
        this.editSuccess.set('');

        if (!this.username || !this.email) {
            this.editError.set('Por favor, rellena todos los campos.');
        return;
        }

        if (this.username.length < 3 || this.username.length > 20) {
            this.editError.set('El nombre debe tener entre 3 y 20 caracteres');
        return;
        }

        if (!esEmailValido(this.email)) {
            this.editError.set('El formato del email no es válido');
            return;
        }

        this.loadingEdit.set(true);
        const body = {
            username: this.username,
            email: this.email,
            esEntrenador: this.esEntrenador
        };

        this.http.put<any>(`${environment.apiUrl}/usuarios/perfil`, body, { headers: this.authService.getTokenHeader() })
        .subscribe({
            next: (data) => {
            this.editSuccess.set('Perfil actualizado correctamente');
            this.loadingEdit.set(false);
            setTimeout(() => this.editSuccess.set(''), 5000);
            },
            error: (err: HttpErrorResponse) => {
            this.loadingEdit.set(false);
            if (err.status === 400) {
                this.editError.set(err.error?.message || 'Los datos no son válidos');
            } else if (err.status === 409) {
                this.editError.set('El nombre de usuario o email ya están en uso');
            } else {
                this.editError.set('Error al actualizar el perfil');
            }
            }
        });
    }

    cambiarPassword(): void {
        this.passwordError.set('');
        this.passwordSuccess.set('');

        if (!this.passwordActual || !this.passwordNueva || !this.passwordConfirmar) {
        this.passwordError.set('Por favor, rellena todos los campos');
        return;
        }

        if (this.passwordNueva.length < 8) {
            this.passwordError.set('La nueva contraseña debe tener al menos 8 caracteres');
            return;
        }

        if (this.passwordNueva !== this.passwordConfirmar) {
            this.passwordError.set('Las contraseñas no coinciden');
            return;
        }

        if (this.passwordActual === this.passwordNueva) {
            this.passwordError.set('La nueva contraseña no puede ser igual a la actual');
            return;
        }

        this.loadingPassword.set(true);
        const body = {
        passwordActual: this.passwordActual,
        passwordNueva: this.passwordNueva
        };

        this.http.put<any>(`${environment.apiUrl}/usuarios/perfil/password`, body, { headers: this.authService.getTokenHeader() })
        .subscribe({
            next: (data) => {
            this.passwordSuccess.set('Contraseña actualizada correctamente');
            this.passwordActual = '';
            this.passwordNueva = '';
            this.passwordConfirmar = '';
            this.loadingPassword.set(false);
            setTimeout(() => this.passwordSuccess.set(''), 5000);
            },
            error: (err: HttpErrorResponse) => {
            this.passwordActual = '';
            this.passwordNueva = '';
            this.passwordConfirmar = '';
            this.loadingPassword.set(false);
            if (err.status === 400) {
                console.log(err.error);
                this.passwordError.set('La contraseña actual es incorrecta');
            } else {
                this.passwordError.set('Error al cambiar la contraseña');
            }
            }
        });
    }

    eliminarCuenta(): void {
        if (!confirm('¿Estás seguro? Esta acción no se puede deshacer.')) {
        return;
        }

        this.loadingDelete.set(true);

        this.http.delete<any>(`${environment.apiUrl}/usuarios/perfil`, { headers: this.authService.getTokenHeader() })
        .subscribe({
            next: (data) => {
            this.authService.cerrarSesion();
            },
            error: (err: HttpErrorResponse) => {
            this.loadingDelete.set(false);
            this.deleteError.set('Error al eliminar la cuenta');
            }
        });
    }

    volver(): void {
        this.router.navigate(['/rutinas']);
    }
}
