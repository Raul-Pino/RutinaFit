declare var bootstrap: any; 

export function cerrarComponenteBS(id: string): Promise<void> {
  return new Promise(resolve => {
    const el = document.getElementById(id);
    
    if (!el) {
      limpiarResiduos();
      resolve();
      return;
    }

    const isOffcanvas = el.classList.contains('offcanvas');
    const instance = isOffcanvas 
      ? bootstrap.Offcanvas.getOrCreateInstance(el) 
      : bootstrap.Modal.getOrCreateInstance(el);

    const limpieza = () => {
      el.removeEventListener('hidden.bs.modal', limpieza);
      el.removeEventListener('hidden.bs.offcanvas', limpieza);
      limpiarResiduos();
      resolve();
    };

    el.addEventListener('hidden.bs.modal', limpieza, { once: true });
    el.addEventListener('hidden.bs.offcanvas', limpieza, { once: true });

    instance.hide();
    setTimeout(limpieza, 450);
  });
}

function limpiarResiduos() {
  document.querySelectorAll('.modal-backdrop, .offcanvas-backdrop')
    .forEach(el => el.remove());
  document.body.style.overflow = '';
  document.body.style.paddingRight = '';
  document.body.classList.remove('modal-open', 'offcanvas-open');
}