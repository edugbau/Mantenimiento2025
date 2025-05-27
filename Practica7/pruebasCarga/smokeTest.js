import http from 'k6/http';
import { check, sleep } from 'k6';


// method : POST(http://localhost:8080/medico)
// raw : {
//   "id": 1,
//   "dni": "1234",
//   "nombre": "Patrick Bateman",
//   "especialidad": "catapultas"
// }
// cd cd pruebasCarga/
//  k6 run --out web-dashboard=export=report-test.html smokeTest.js
export const options = {
    vus: 5,
    duration: '1m',
    thresholds: {
        http_req_failed: ['rate==0'], // que no falle tampoco dios mio
        http_req_duration: ['avg<100'],
    },
};

export default function () {
    check(http.get('http://localhost:8080/medico/1'), {
        'Response code was 200': (r) => r.status === 200,
    });
    sleep(1);
}
