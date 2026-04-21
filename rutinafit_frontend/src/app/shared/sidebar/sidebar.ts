import { Component, OnInit, OnDestroy, inject } from '@angular/core';
import { RouterLink, RouterLinkActive, Router, NavigationStart } from '@angular/router';
import { Subscription } from 'rxjs';
import { filter } from 'rxjs/operators';
import { AuthService } from '../../core/auth.service';

declare const bootstrap: any;

@Component({
  selector: 'app-sidebar',
  imports: [RouterLink, RouterLinkActive],
  templateUrl: './sidebar.html',
  styleUrl: './sidebar.scss',
})
export class SidebarComponent implements OnInit, OnDestroy {
  private router = inject(Router);
  private authService = inject(AuthService);
  private routerSub?: Subscription;

  rol: string | null = null;
  esEntrenador: boolean = false;

  ngOnInit(): void {
    this.rol = this.authService.getRol();
    this.esEntrenador = this.authService.esEntrenador();

    this.routerSub = this.router.events
      .pipe(filter(e => e instanceof NavigationStart))
      .subscribe(() => {
        const offcanvasEl = document.getElementById('sidebarOffcanvas');
        if (offcanvasEl) {
          bootstrap.Offcanvas.getInstance(offcanvasEl)?.hide();
        }
      });
  }

  ngOnDestroy(): void {
    this.routerSub?.unsubscribe();
  }
}
