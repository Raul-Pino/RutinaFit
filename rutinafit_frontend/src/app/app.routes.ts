import { Routes } from '@angular/router';
import { Home } from './pages/home/home';
import { Rutinas } from './pages/rutinas/rutinas';
import { AuthLayoutComponent } from './layouts/auth-layout/auth-layout';
import { Sesiones } from './pages/sesiones/sesiones';
import { Ejercicios } from './pages/ejercicios/ejercicios';
import { Login } from './pages/login/login';
import { Registro } from './pages/registro/registro';
import { RecuperarPassword } from './pages/recuperar/recuperar-password';
import { Perfil } from './pages/perfil/perfil';
import { Admin } from './pages/admin/admin';
import { Alumnos } from './pages/alumnos/alumnos';

export const routes: Routes = [
  { path: '', component: Home },
  { path: 'login', component: Login },
  { path: 'registro', component: Registro },
  { path: 'recuperar-password', component: RecuperarPassword },
  {
    path: '', 
    component: AuthLayoutComponent,
    children: [
      { path: 'rutinas', component: Rutinas },
      { path: 'rutinas/:alumnoId', component: Rutinas },
      { path: 'sesiones/:rutinaId', component: Sesiones},
      { path: 'sesiones/:alumnoId/:rutinaId', component: Sesiones},
      { path: 'sesion/:sesionId', component: Ejercicios},
      { path: 'sesion/:alumnoId/:sesionId', component: Ejercicios},
      { path: 'perfil', component: Perfil},
      { path: 'admin', component: Admin},
      { path: 'alumnos', component: Alumnos}
    ]
  },
  { path: '**', redirectTo: '' }
];
