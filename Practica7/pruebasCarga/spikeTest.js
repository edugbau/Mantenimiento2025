import http from 'k6/http';
import { check, sleep } from 'k6';

export let options = {
    stages: [
        // Rampa de subida rápida al 40% de VUs del punto de rotura en 2 minutos
        { duration: '2m', target: 3200 },
        // Mantenimiento de la carga durante 1 minuto (duración del pico)
        { duration: '1m', target: 3200 },
        // Rampa de bajada rápida a 0 VUs en 1 minuto
        { duration: '1m', target: 0 },
    ],
    thresholds: {
        'http_req_failed': [{ threshold: 'rate<0.005', abortOnFail: true }], // Menos del 0.5% de peticiones fallidas
    },
};

export default function () {
    check(http.get('http://localhost:8080/medico/1'), {
        'Response code was 200': (r) => r.status === 200,
    });
    sleep(1); // Espera 1 segundo entre iteraciones de VU
}