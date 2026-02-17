import http from 'k6/http';
import { check, sleep } from 'k6';

// SECCIÓN DE OPCIONES
export const options = {
    // STAGES: control de TIEMPO y la CANTIDAD DE USUARIOS
    stages: [
        { duration: '10s', target: 1000 }, // Sube rápido de 0 a 1000 en 10s
        { duration: '20s', target: 1000 }, // Se mantiene estable con 1000 usuarios por 20s
        { duration: '5s',  target: 0 },    // Baja a 0 usuarios
    ],

    // THRESHOLDS: Opcional
    // Ej: "Falla la prueba si el 95% de peticiones tarda más de 500ms"
    thresholds: {
        http_req_duration: ['p(95)<500'],
    },
};

// FUNCIÓN POR DEFECTO
// Cada usuario ejecutará este bloque una y otra vez.
export default function () {
    // Petición
    const res = http.get('http://localhost:8080/usuarios/buscar?username=paco');

    // Validación
    check(res, { 'status es 200': (r) => r.status === 200 });

    // FRECUENCIA: El sleep indica cuánto espera el usuario antes de repetir el ciclo.
    // Sin sleep -> Peticiones infinitas a máxima velocidad.
    sleep(0.1);
}