import http from 'k6/http';
import { check, sleep } from 'k6';

export let options = {
    scenarios: {
        breakpoint_with_executor_scenario: {
            executor: 'ramping-arrival-rate',
            preAllocatedVUs: 7500,
            maxVUs: 1e7,
            stages: [
                { duration: '10m', target: 100000 }// AJUSTA ESTE TARGET RATE
            ],
        },
    },
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
    sleep(0.1);
}