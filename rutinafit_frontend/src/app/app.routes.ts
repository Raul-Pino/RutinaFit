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
      { path: 'sesiones/:id', component: Sesiones},
      { path: 'sesion/:id', component: Ejercicios},
      { path: 'perfil', component: Perfil},
      { path: 'admin', component: Admin}
    ]
  },
  { path: '**', redirectTo: '' }
];
