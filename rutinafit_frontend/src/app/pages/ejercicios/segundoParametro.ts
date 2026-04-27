export enum CodigoEjercicio {
    Repeticiones = 1,
    Tiempo = 2,
}

export const SegundoParametro: Record<number, string> = {
    [CodigoEjercicio.Repeticiones]: 'Repeticiones',
    [CodigoEjercicio.Tiempo]: 'Tiempo (min)',
};