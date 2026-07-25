async function parseBody(res) {
  const text = await res.text();
  if (!text) return null;
  try {
    return JSON.parse(text);
  } catch {
    return text;
  }
}

/**
 * @param {string} path - e.g. /api/products
 * @param {RequestInit & { token?: string | null }} options
 */
const API_BASE = "https://fashion-store-api.onrender.com";

export async function apiFetch(path, options = {}) {
  const { token, ...init } = options;
  const headers = {
    "Content-Type": "application/json",
    ...(init.headers || {}),
  };
  if (token) {
    headers.Authorization = `Bearer ${token}`;
  }

  const res = await fetch(`${API_BASE}${path}`, {
    ...init,
    headers,
  });

  const data = await parseBody(res);
  if (!res.ok) {
    let msg =
      (data && typeof data === "object" && data.error) ||
      (typeof data === "string" ? data : null) ||
      `Request failed (${res.status})`;
    if (typeof data === "object" && data && !data.error) {
      const first = Object.values(data).find((v) => typeof v === "string");
      if (first) msg = first;
    }
    const err = new Error(msg);
    err.status = res.status;
    err.body = data;
    throw err;
  }
  return data;
}
