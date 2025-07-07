import http from "k6/http";
import { check, sleep } from "k6";

const BASE_URL = "http://localhost:8080";

const imageFileContent = open("fake-image.jpg", "b");

export let options = {
  scenarios: {
    sellers: {
      executor: "ramping-vus",
      startTime: "0s",
      stages: [
        { duration: "30s", target: 50 }, 
        { duration: "1m", target: 100 },
        { duration: "1m", target: 0 },
      ],
      exec: "sellerFlow",
    },
    buyers: {
      executor: "ramping-vus",
      startTime: "20s",
      stages: [
        { duration: "30s", target: 150 }, 
        { duration: "1m", target: 250 },
        { duration: "1m", target: 0 },
      ],
      exec: "buyerFlow",
    },
  },
};

function registerUser(user) {
  return http.post(`${BASE_URL}/auth/register`, JSON.stringify(user), {
    headers: { "Content-Type": "application/json" },
  });
}

function loginUser(email, password) {
  const res = http.post(
    `${BASE_URL}/auth/login`,
    JSON.stringify({ email, password }),
    {
      headers: { "Content-Type": "application/json" },
    }
  );
  return res.json("token");
}

function createProduct(token) {
  const res = http.post(
    `${BASE_URL}/api/stockService/seller/create`,
    {
      request: http.file(
        JSON.stringify({
          productName: `Producto Stress ${Math.random().toFixed(5)}`,
          description: "Descripcion",
          price: 99.99,
          qty: 100,
          category: "Tech",
          minimumStock: 1,
          location: "MX",
        }),
        "request.json",
        "application/json"
      ),
      image: http.file(imageFileContent, "fake-image.jpg", "image/jpeg"),
    },
    {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    }
  );

  check(res, {
    "Producto creado": (r) => r.status === 200,
  });

  return res;
}

export function sellerFlow() {
  const userId = `seller-${__VU}-${__ITER}`;
  const user = {
    name: userId,
    email: `${userId}@test.com`,
    password: "123456",
    role: "SELLER",
  };

  const regRes = registerUser(user);
  check(regRes, {
    "Registro vendedor": (r) => r.status === 200 || r.status === 400,
  });

  const token = loginUser(user.email, user.password);
  check(token, { "Token recibido vendedor": (t) => !!t });

  createProduct(token);

  sleep(1);
}

export function buyerFlow() {
  const userId = `buyer-${__VU}-${__ITER}`;
  const user = {
    name: userId,
    email: `${userId}@test.com`,
    password: "123456",
    role: "BUYER",
  };

  const regRes = registerUser(user);
  check(regRes, {
    "Registro comprador": (r) => r.status === 200 || r.status === 400,
  });

  const token = loginUser(user.email, user.password);
  check(token, { "Token recibido comprador": (t) => !!t });

  const authHeaders = {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  };

  const productsRes = http.get(
    `${BASE_URL}/api/stockService?page=0&size=5`,
    authHeaders
  );
  check(productsRes, { "Productos disponibles": (r) => r.status === 200 });

  const products = productsRes.json();
  if (products.length > 0) {
    const product = products[Math.floor(Math.random() * products.length)];
    const code = product.productCode;

    http.get(`${BASE_URL}/api/stockService/${code}`, authHeaders);
    http.get(`${BASE_URL}/api/stockService/image/${code}`, authHeaders);

    const order = {
      items: [
        {
          productCode: code,
          productName: product.name,
          qty: 1,
        },
      ],
      total: product.price,
      shippingAddress: "Dirección demo",
    };

    const orderRes = http.post(
      `${BASE_URL}/api/orderService/placeOrder`,
      JSON.stringify(order),
      {
        ...authHeaders,
        headers: {
          ...authHeaders.headers,
          "Content-Type": "application/json",
        },
      }
    );

    check(orderRes, { "Orden colocada": (r) => r.status === 202 });

    http.get(`${BASE_URL}/api/orderService/orders/`, authHeaders);
  }

  sleep(1);
}
