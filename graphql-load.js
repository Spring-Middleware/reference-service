import http from 'k6/http';
import { check, sleep } from 'k6';
import { SharedArray } from 'k6/data';

export const options = {
  scenarios: {
    warmup: {
      executor: 'ramping-vus',
      startVUs: 0,
      stages: [
        { duration: '10s', target: 2 },
        { duration: '20s', target: 2 },
        { duration: '5s', target: 0 },
      ],
      gracefulRampDown: '5s',
    },
    load_test: {
      executor: 'ramping-vus',
      startTime: '35s',
      startVUs: 0,
      stages: [
        { duration: '20s', target: 5 },
        { duration: '30s', target: 10 },
        { duration: '30s', target: 20 },
        { duration: '20s', target: 0 },
      ],
      gracefulRampDown: '5s',
    },
  },
  thresholds: {
    http_req_failed: ['rate<0.01'],
    http_req_duration: ['p(90)<2000', 'p(95)<3000', 'p(99)<5000'],
    checks: ['rate>0.99'],
  },
};

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8060/graphql';
const AUTH_TOKEN = __ENV.AUTH_TOKEN || '';
const QUERY_FILE = __ENV.QUERY_FILE || './query.graphql';
const VARIABLES_FILE = __ENV.VARIABLES_FILE || '';

const queryHolder = new SharedArray('graphql-query', function () {
  return [open(QUERY_FILE)];
});

const variablesHolder = new SharedArray('graphql-variables', function () {
  if (!VARIABLES_FILE) {
    return [{}];
  }
  return [JSON.parse(open(VARIABLES_FILE))];
});

const query = queryHolder[0];
const variables = variablesHolder[0];

export default function () {
  const payload = JSON.stringify({
    query,
    variables,
    operationName: null,
  });

  const headers = {
    'Content-Type': 'application/json',
  };

  if (AUTH_TOKEN) {
    headers.Authorization = `Bearer ${AUTH_TOKEN}`;
  }

  const res = http.post(BASE_URL, payload, {
    headers,
    tags: {
      operation: 'graphql_catalogs',
      endpoint: 'graphql',
    },
  });

  const ok = check(res, {
    'status is 200': (r) => r.status === 200,
    'response has no GraphQL errors': (r) => {
      try {
        const body = JSON.parse(r.body);
        return !body.errors || body.errors.length === 0;
      } catch (e) {
        return false;
      }
    },
    'response contains data': (r) => {
      try {
        const body = JSON.parse(r.body);
        return !!body?.data;
      } catch (e) {
        return false;
      }
    },
  });

  if (!ok) {
    console.error(`Unexpected response: status=${res.status}, body=${res.body.substring(0, 1000)}`);
  }

  sleep(1);
}