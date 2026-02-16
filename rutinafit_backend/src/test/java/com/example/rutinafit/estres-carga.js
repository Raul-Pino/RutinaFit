import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
    stages: [
        { duration: '30s', target: 100 }, // Sube rápido a 100 usuarios
        { duration: '1m', target: 100 },  // Mantener 100 usuarios conectados
        { duration: '20s', target: 0 },
    ],
    thresholds: {
        http_req_failed: ['rate<0.01'],   // El test falla si hay más de un 1% de errores
        http_req_duration: ['p(95)<500'], // El 95% de las peticiones deben bajar de 500ms
    },
};

export default function () {
    const res = http.get('http://localhost:8080/usuarios/buscar?username=paco');

    check(res, {
        'status es 200': (r) => r.status === 200,
    });
    sleep(0.1);
}