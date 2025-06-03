import http from 'k6/http';
import { check, sleep } from 'k6';

export let options = {
    stages: [
        // Rampa de subida al 80% de VUs del punto de rotura en 3 minutos
        { duration: '3m', target: 7400 },
        // Mantenimiento de la carga durante 3 minutos
        { duration: '3m', target: 7400 },
        // Rampa de bajada a 0 VUs en 2 minutos
        { duration: '2m', target: 0 },
    ],
    thresholds: {
        'http_req_failed': [{ threshold: 'rate<=0.01', abortOnFail: true }], // Menos del 1% de peticiones fallidas
        'http_req_duration': [{ threshold: 'avg<1000', abortOnFail: true }], // Duración promedio de petición inferior a 1000ms
    },
};

export default function () {
    check(http.get('http://localhost:8080/medico/1'), {
        'Response code was 200': (r) => r.status === 200,
    });
    sleep(1); // Espera 1 segundo entre iteraciones de VU
}

//Aguanta bien