import { Component, inject } from '@angular/core';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { SidebarComponent } from '../sidebar/sidebar';
import { AuthService } from '../../core/auth.service';

@Component({
  selector: 'app-navbar-auth',
  imports: [SidebarComponent, RouterLink, RouterLinkActive],
  templateUrl: './navbar-auth.html',
  styleUrl: './navbar-auth.scss',
})
export class NavbarAuthComponent {
  private router = inject(Router);
  private authService = inject(AuthService);

  nombre = this.authService.getNombre();

  cerrarSesion(): void {
    this.authService.cerrarSesion();
  }
}
