import { Component } from '@angular/core';
import { jwtDecode } from 'jwt-decode';

@Component({
  selector: 'app-rutinas',
  templateUrl: './rutinas.html',
  styleUrl: './rutinas.scss',
})
export class Rutinas {
  userData: any;


  ngOnInit() {
    const token = localStorage.getItem('token');
    if (token) {
      try {
        this.userData = jwtDecode(token);
        console.log('Datos del token:', this.userData);
      } catch (error) {
        console.error('El token no es válido', error);
      }
    }else{
      window.location.href = '/';
    }
  }

  cerrarSesion() {
    localStorage.removeItem('token');
    window.location.href = '/';
  }
}
