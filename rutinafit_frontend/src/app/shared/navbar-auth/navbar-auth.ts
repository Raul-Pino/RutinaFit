import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';
import { SidebarComponent } from '../sidebar/sidebar';
import { AuthService } from '../../core/auth.service';

@Component({
  selector: 'app-navbar-auth',
  imports: [SidebarComponent],
  templateUrl: './navbar-auth.html',
  styleUrl: './navbar-auth.scss',
})
export class NavbarAuthComponent {
  private router = inject(Router);
  private authService = inject(AuthService);

  cerrarSesion(): void {
    this.authService.cerrarSesion();
    this.router.navigate(['/']);
  }
}
