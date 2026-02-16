import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
    stages: [
        { duration: '30s', target: 20 },
        { duration: '1m', target: 20 },
        { duration: '20s', target: 0 },
    ],
};

export default function () {
    const url = 'http://localhost:8080/usuarios/buscar?username=paco';
    const res = http.get(url);

    check(res, {
        'status es 200': (r) => r.status === 200,
        'tiempo respuesta < 500ms': (r) => r.timings.duration < 500,
    });

    sleep(0.1);
}