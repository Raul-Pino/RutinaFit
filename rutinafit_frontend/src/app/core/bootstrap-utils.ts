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
      ? bootstrap.Offcanvas.getInstance(el) 
      : bootstrap.Modal.getInstance(el);

    if (!instance) {
      resolve();
      return;
    }

    const limpieza = () => {
      el.removeEventListener('hidden.bs.modal', limpieza);
      el.removeEventListener('hidden.bs.offcanvas', limpieza);
      resolve();
    };

    el.addEventListener('hidden.bs.modal', limpieza, { once: true });
    el.addEventListener('hidden.bs.offcanvas', limpieza, { once: true });

    instance.hide();
  });
}