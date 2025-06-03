import http from 'k6/http';
import { check, sleep } from 'k6';



export let options = {
    stages: [
        { duration: '10m', target: 70000 },
    ],
    thresholds: {
        'http_req_failed': [{ threshold: 'rate<=0.01', abortOnFail: true }],
    },
};

export default function () {
    const BASE_URL = 'http://localhost:8080/medico/1';
    let res = http.get(BASE_URL);
    check(res, {
        'status was 200': (r) => r.status === 200,
    });
    sleep(1);
}

//Interrumpido con 9157 VUs