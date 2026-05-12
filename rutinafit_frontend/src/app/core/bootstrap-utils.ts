declare var bootstrap: any;

export function cerrarComponenteBS(id: string): Promise<void> {
  return new Promise(resolve => {
    const el = document.getElementById(id);
    
    if (!el) {
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
      
      setTimeout(() => {
        document.querySelectorAll('.modal-backdrop, .offcanvas-backdrop').forEach(el => el.remove());
        document.body.classList.remove('modal-open', 'offcanvas-open');
        document.body.style.overflow = '';
        document.body.style.paddingRight = '';
        resolve();
      }, 0);
    };

    el.addEventListener('hidden.bs.modal', limpieza, { once: true });
    el.addEventListener('hidden.bs.offcanvas', limpieza, { once: true });

    instance.hide();
  });
}