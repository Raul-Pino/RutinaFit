export enum CodigoEjercicio {
    Peso = 1,
    Distancia = 2,
}

export const PrimerParametro: Record<number, string> = {
    [CodigoEjercicio.Peso]: 'Peso (kg)',
    [CodigoEjercicio.Distancia]: 'Distancia (km)',
};