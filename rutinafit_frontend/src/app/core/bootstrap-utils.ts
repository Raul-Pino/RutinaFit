declare const bootstrap: any;

export function cerrarModalGlobal(idModal: string): Promise<void> {
  return new Promise(resolve => {
    const modalEl = document.getElementById(idModal);
    if (!modalEl) {
      resolve();
      return;
    }

    const instance = bootstrap.Modal.getOrCreateInstance(modalEl);

    const limpiezaEmergencia = () => {
      document.querySelectorAll('.modal-backdrop').forEach(el => el.remove());
      document.body.classList.remove('modal-open');
      document.body.style.overflow = '';
      document.body.style.paddingRight = '';
      resolve();
    };

    modalEl.addEventListener('hidden.bs.modal', limpiezaEmergencia, { once: true });
    instance.hide();

    setTimeout(() => {
      modalEl.removeEventListener('hidden.bs.modal', limpiezaEmergencia);
      limpiezaEmergencia();
    }, 400);
  });
}
