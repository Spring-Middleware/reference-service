import http from 'k6/http';
import { sleep, check } from 'k6';

const BASE_URL = 'http://localhost:8070';
const CATALOG_ID = '10c6a38f-9636-4e12-b4a7-aea37edd92a0';
const ENDPOINT = `${BASE_URL}/catalog/api/v1/catalogs/${CATALOG_ID}/products?page=0&size=20`;

// Ajusta esto al waitDurationInOpenState real de tu circuit-breaker
const WAIT_DURATION_OPEN_SECONDS = 12;

export const options = {
  scenarios: {
    // Fase 1: suficientes llamadas para abrir el circuit-breaker
    open_circuit: {
      executor: 'constant-vus',
      vus: 1,
      duration: '20s',
      exec: 'openCircuit',
      startTime: '0s',
    },

    // Fase 2: esperar a que pase de OPEN a HALF_OPEN
    wait_for_half_open: {
      executor: 'per-vu-iterations',
      vus: 1,
      iterations: 1,
      exec: 'waitForHalfOpen',
      startTime: '20s',
    },

    // Fase 3: probar recuperación
    recovery_test: {
      executor: 'constant-vus',
      vus: 1,
      duration: '10s',
      exec: 'recoveryTest',
      startTime: `${20 + WAIT_DURATION_OPEN_SECONDS}s`,
    },
  },
  thresholds: {
    http_req_duration: ['p(95)<3000'],
  },
};

function logResponse(phase, res) {
  console.log(
    `[${phase}] status=${res.status} duration=${res.timings.duration}ms body=${String(res.body).substring(0, 200)}`
  );
}

export function openCircuit() {
  const res = http.get(ENDPOINT, {
    headers: {
      Accept: 'application/json'
    },
    timeout: '5s',
  });

  logResponse('OPEN_CIRCUIT', res);

  // Aquí no validamos 200, porque precisamente queremos errores
  check(res, {
    'respuesta recibida': (r) => r.status !== 0,
  });

  sleep(0.5);
}

export function waitForHalfOpen() {
   console.log(`[WAIT] start`);
   console.log(`[WAIT] start`);
   sleep(1);
   console.log(`[WAIT] 1s`);
   sleep(1);
   console.log(`[WAIT] 2s`);
   sleep(1);
   console.log(`[WAIT] 3s`);
   sleep(WAIT_DURATION_OPEN_SECONDS - 3);
   console.log(`[WAIT] end`);
}

export function recoveryTest() {
  const res = http.get(ENDPOINT, {
    headers: {
      Accept: 'application/json'
    },
    timeout: '5s',
  });

  logResponse('RECOVERY', res);

  check(res, {
    'status es 200 en recuperación': (r) => r.status === 200,
  });

  sleep(0.5);
}