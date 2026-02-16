import http from 'k6/http';
import { check, sleep } from 'k6';
import { uuidv4 } from 'https://jslib.k6.io/k6-utils/1.4.0/index.js';

export const options = {
    stages: [
        { duration: '30s', target: 50 },
        { duration: '1m', target: 50 },
        { duration: '20s', target: 0 },
    ],
};

export default function () {
    const url = 'http://localhost:8080/auth/register';

    const randomId = uuidv4().substring(0, 8);
    const token = JSON.stringify({
        username: `user_${randomId}`,
        email: `test_${randomId}@correo.com`,
        password: 'password1234',
    });

    const params = {
        headers: {
            'Content-Type': 'application/json',
        },
    };

    const res = http.post(url, token, params);

    check(res, {
        'registro exitoso (201 o 200)': (r) => r.status === 201 || r.status === 200,
        'tiempo registro < 1000ms': (r) => r.timings.duration < 1000,
    });

    sleep(1);
}