import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { AuthService } from './auth.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
    const router = inject(Router);
    const authService = inject(AuthService);

    return next(req).pipe(
        catchError((error: HttpErrorResponse) => {
        // Si el backend devuelve 401 o 403 el token es inválido o expiró
        if (error.status === 401) {
            authService.cerrarSesion();
        }else if (error.status === 403) {
            router.navigate(['']);
        }
        return throwError(() => error);
        })
    );
};