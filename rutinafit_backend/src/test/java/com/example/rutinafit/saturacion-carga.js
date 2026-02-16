import http from 'k6/http';
import { check, sleep } from 'k6';

// 2. SECCIÓN DE OPCIONES
export const options = {
    // STAGES: control de TIEMPO y la CANTIDAD DE USUARIOS
    stages: [
        { duration: '5m', target: 500 },  // DURACIÓN: 5 minuto | Mantiene 500 usuarios
    ],

    // THRESHOLDS: (Opcional) Metas de rendimiento. 
    // Ej: "Falla la prueba si el 95% de peticiones tarda más de 500ms"
    thresholds: {
        http_req_duration: ['p(95)<500'],
    },
};

// 3. FUNCIÓN POR DEFECTO
// Si tienes 100 usuarios, cada uno de ellos ejecutará este bloque una y otra vez.
export default function () {
    // La petición real
    const res = http.get('http://localhost:8080/usuarios/buscar?username=paco');

    // La validación
    check(res, { 'status es 200': (r) => r.status === 200 });

    // FRECUENCIA: El sleep indica cuánto espera el usuario antes de repetir el ciclo.
    // Sin sleep -> Peticiones infinitas a máxima velocidad.
    // sleep(1) -> 1 petición por segundo por cada usuario.
    sleep(0.1);
}