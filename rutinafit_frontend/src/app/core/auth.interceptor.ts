import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
    const router = inject(Router);

    return next(req).pipe(
        catchError((error: HttpErrorResponse) => {
        // Si el backend devuelve 401 o 403 el token es inválido o expiró
        if (error.status === 401 || error.status === 403) {
            localStorage.removeItem('token');
            router.navigate(['']);
        }
        return throwError(() => error);
        })
    );
};