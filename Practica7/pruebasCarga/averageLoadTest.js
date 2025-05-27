import http from 'k6/http';
import { check, sleep } from 'k6';

export let options = {
    stages: [
        // Rampa de subida al 50% de VUs del punto de rotura en 3 minutos
        { duration: '3m', target: 4000 },
        // Mantenimiento de la carga durante 3 minutos
        { duration: '3m', target: 4000 },
        // Rampa de bajada a 0 VUs en 2 minutos
        { duration: '2m', target: 0 },
    ],
    thresholds: {
        'http_req_failed': [{ threshold: 'rate<0.01', abortOnFail: true }], // Menos del 1% de peticiones fallidas
    },
};

export default function () {
    check(http.get('http://localhost:8080/medico/1'), {
        'Response code was 200': (r) => r.status === 200,
    });
    sleep(1); // Espera 1 segundo entre iteraciones de VU
}