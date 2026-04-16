import { Routes } from '@angular/router';
import { Home } from './pages/home/home';
import { Rutinas } from './pages/rutinas/rutinas';
import { AuthLayoutComponent } from './layouts/auth-layout/auth-layout';

export const routes: Routes = [
  { path: '', component: Home },
  {
    path: '', 
    component: AuthLayoutComponent,
    children: [
      { path: 'rutinas', component: Rutinas }
    ]
  },
  { path: '**', redirectTo: '' }
];

