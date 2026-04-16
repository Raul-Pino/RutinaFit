import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { SidebarComponent } from '../../shared/sidebar/sidebar';
import { NavbarAuthComponent } from '../../shared/navbar-auth/navbar-auth';

@Component({
  selector: 'app-auth-layout',
  imports: [RouterOutlet, SidebarComponent, NavbarAuthComponent],
  templateUrl: './auth-layout.html',
  styleUrl: './auth-layout.scss',
})
export class AuthLayoutComponent {}
