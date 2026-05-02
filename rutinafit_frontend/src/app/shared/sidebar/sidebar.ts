import { Component, OnInit, OnDestroy, inject, Renderer2 } from '@angular/core';
import { RouterLink, RouterLinkActive, Router, NavigationStart } from '@angular/router';
import { Subscription } from 'rxjs';
import { filter } from 'rxjs/operators';
import { AuthService } from '../../core/auth.service';
import { cerrarComponenteBS } from '../../core/bootstrap-utils';

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
  private renderer = inject(Renderer2);

  rol: string | null = null;
  esEntrenador: boolean = false;

ngOnInit(): void {
    this.rol = this.authService.getRol();
    this.esEntrenador = this.authService.esEntrenador();

    this.routerSub = this.router.events
      .pipe(filter(e => e instanceof NavigationStart))
      .subscribe(() => {
        this.cerrarMenuSeguro();
      });


    const offcanvasEl = document.getElementById('sidebarOffcanvas');
    if (offcanvasEl) {
      offcanvasEl.addEventListener('hidden.bs.offcanvas', () => {
        this.limpiarResiduosManuales();
      });
    }
  }


  async cerrarMenuSeguro() {
    await cerrarComponenteBS('sidebarOffcanvas');
  }

  private limpiarResiduosManuales() {
    const backdrops = document.querySelectorAll('.offcanvas-backdrop, .modal-backdrop');
    backdrops.forEach(b => this.renderer.removeChild(document.body, b));
    
    this.renderer.removeStyle(document.body, 'overflow');
    this.renderer.removeStyle(document.body, 'padding-right');
    this.renderer.removeClass(document.body, 'offcanvas-open');
  }

  ngOnDestroy(): void {
    this.routerSub?.unsubscribe();
    this.limpiarResiduosManuales();
  }
}
