import { Routes } from '@angular/router';
import { Home } from './pages/home/home';
import { Rutinas } from './pages/rutinas/rutinas';
import { AuthLayoutComponent } from './layouts/auth-layout/auth-layout';
import { Sesiones } from './pages/sesiones/sesiones';
import { Ejercicios } from './pages/ejercicios/ejercicios';

export const routes: Routes = [
  { path: '', component: Home },
  {
    path: '', 
    component: AuthLayoutComponent,
    children: [
      { path: 'rutinas', component: Rutinas },
      { path: 'sesiones/:id', component: Sesiones},
      { path: 'sesion/:id', component: Ejercicios}
    ]
  },
  { path: '**', redirectTo: '' }
];

