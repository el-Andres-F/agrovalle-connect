// Utilidades compartidas por las páginas de la tienda: carrito, favoritos, encabezado, pie y avisos.
import { iniciales } from "./producto-art.js";

export const API = "/api/v1";
export const CART_KEY = "agrovalle.client.cart.v1";
export const FAV_KEY = "agrovalle.client.favs.v1";
export const LOC_KEY = "agrovalle.client.location.v1";
export const ADDR_KEY = "agrovalle.client.address.v1";
export const money = new Intl.NumberFormat("es-CO", { style: "currency", currency: "COP", maximumFractionDigits: 0 });

export function leer(key, fallback = []) {
  try { const value = JSON.parse(localStorage.getItem(key)); return value ?? fallback; } catch { return fallback; }
}
export function guardar(key, value) {
  try { localStorage.setItem(key, JSON.stringify(value)); return true; } catch { return false; }
}
export function getCart() {
  const cart = leer(CART_KEY);
  return Array.isArray(cart) ? cart.filter((item) => item && Number(item.quantity) > 0) : [];
}
export function saveCart(cart) { guardar(CART_KEY, cart); updateCartBadge(); }
export function cartUnits() { return getCart().reduce((sum, item) => sum + Number(item.quantity), 0); }
export function updateCartBadge() {
  const badge = document.getElementById("cart-count");
  if (!badge) return;
  const units = cartUnits();
  badge.textContent = units;
  badge.hidden = units === 0;
}
export function addToCart(product) {
  const cart = getCart();
  const item = cart.find((entry) => Number(entry.id) === Number(product.id));
  if (item) {
    if (item.quantity >= Number(product.cantidad)) { toast("Ya agregaste toda la cantidad disponible."); return false; }
    item.quantity += 1;
  } else {
    cart.push({
      id: product.id, name: product.nombre, price: Number(product.precio) || 0, quantity: 1,
      available: Number(product.cantidad) || 0, producer: product.productor?.nombre || "Productor local",
      producerId: product.productor?.id ?? null, place: product.ubicacion || product.productor?.ubicacion || "",
      category: product.categoria || "",
    });
  }
  saveCart(cart);
  toast(`${product.nombre} se agregó a tu carrito.`);
  return true;
}
export function getFavs() { const favs = leer(FAV_KEY); return Array.isArray(favs) ? favs.map(Number) : []; }
export function isFav(id) { return getFavs().includes(Number(id)); }
export function toggleFav(id) {
  const favs = getFavs();
  const index = favs.indexOf(Number(id));
  if (index >= 0) favs.splice(index, 1); else favs.push(Number(id));
  guardar(FAV_KEY, favs);
  return index < 0;
}

let toastTimer;
export function toast(text) {
  let node = document.getElementById("shop-toast");
  if (!node) { node = document.createElement("div"); node.id = "shop-toast"; node.className = "shop-toast"; node.setAttribute("role", "status"); document.body.append(node); }
  node.textContent = text;
  node.classList.add("show");
  clearTimeout(toastTimer);
  toastTimer = setTimeout(() => node.classList.remove("show"), 3200);
}

export async function loadSession() {
  try {
    const response = await fetch(`${API}/auth/session`, { credentials: "same-origin" });
    return await response.json();
  } catch { return { autenticado: false }; }
}

const NAV = [
  ["inicio", "/clientes/", "Inicio"],
  ["favoritos", "/clientes/?favoritos=1", "Favoritos"],
  ["pedidos", "/pedidos/", "Mis pedidos"],
];

// Pinta el encabezado en #site-header. Opciones: active, onSearch (filtro en vivo), onLocationClick, search (valor inicial).
export function mountHeader({ active = "", onSearch = null, onLocationClick = null, search = "" } = {}) {
  const host = document.getElementById("site-header");
  const location = leer(LOC_KEY, "");
  const next = encodeURIComponent(window.location.pathname);
  host.className = "site-header";
  host.innerHTML = `
    <a class="site-brand" href="/clientes/" aria-label="AgroValle, tienda"><span class="brand-mark">A<span>V</span></span><strong>AgroValle</strong></a>
    <form class="site-search" id="site-search-form" role="search"><span class="site-search-icon" aria-hidden="true"><svg viewBox="0 0 24 24"><circle cx="11" cy="11" r="7"/><path d="m16 16 4 4"/></svg></span>
      <input id="site-search" type="search" placeholder="Busca frutas, verduras, productores..." aria-label="Buscar productos" autocomplete="off"></form>
    <a class="loc-pill" id="loc-pill" href="/clientes/#filtros" title="Filtrar por ubicación"><svg class="loc-icon" aria-hidden="true" viewBox="0 0 24 24"><path d="M20 10c0 5-8 12-8 12S4 15 4 10a8 8 0 1 1 16 0Z"/><circle cx="12" cy="10" r="2.5"/></svg><span id="loc-text"></span><small aria-hidden="true">⌄</small></a>
    <nav class="site-nav" aria-label="Secciones de la tienda">
      ${NAV.map(([key, href, label]) => `<a class="site-nav-link${key === active ? " active" : ""}" href="${href}">${label}</a>`).join("")}
      <a class="site-nav-link" id="admin-link" href="/admin.html" hidden>Gestión</a>
    </nav>
    <a class="cart-btn" href="/carrito/" aria-label="Abrir carrito"><svg class="cart-icon" aria-hidden="true" viewBox="0 0 24 24"><path d="M3 4h2l2.4 11.2a2 2 0 0 0 2 1.6h8.8a2 2 0 0 0 1.9-1.4L22 9H6"/><circle cx="10" cy="20" r="1"/><circle cx="18" cy="20" r="1"/></svg><span class="cart-badge" id="cart-count" hidden>0</span></a>
    <a class="avatar" id="avatar" href="/acceso/index.html?next=${next}" aria-label="Iniciar sesión" title="Iniciar sesión"><img src="/iconos/usuario.png" alt=""></a>
    <button class="logout-btn" id="logout-button" type="button" aria-label="Cerrar sesión" title="Cerrar sesión" hidden><img src="/iconos/salir.png" alt=""></button>`;
  host.querySelector("#loc-text").textContent = location || "Todo el Valle";
  const input = host.querySelector("#site-search");
  input.value = search;
  host.querySelector("#site-search-form").addEventListener("submit", (event) => {
    event.preventDefault();
    if (onSearch) return onSearch(input.value);
    window.location.assign(`/clientes/?q=${encodeURIComponent(input.value.trim())}`);
  });
  if (onSearch) input.addEventListener("input", () => onSearch(input.value));
  if (onLocationClick) host.querySelector("#loc-pill").addEventListener("click", (event) => { event.preventDefault(); onLocationClick(); });
  host.querySelector("#logout-button").addEventListener("click", async () => {
    try { await fetch(`${API}/auth/logout`, { method: "POST", credentials: "same-origin" }); }
    finally { window.location.replace("/clientes/"); }
  });
  updateCartBadge();
  const session = loadSession().then((data) => {
    host.querySelector("#admin-link").hidden = !(data.autenticado && data.tipo === "ADMIN");
    host.querySelector("#logout-button").hidden = !data.autenticado;
    if (data.autenticado) {
      const avatar = host.querySelector("#avatar");
      avatar.href = "/cuenta/index.html";
      avatar.textContent = iniciales(data.nombre || "Cuenta");
      avatar.title = avatar.ariaLabel = `Mi cuenta, ${String(data.nombre || "").trim().split(/\s+/)[0]}`;
      avatar.classList.add("has-initials");
    }
    return data;
  });
  return { session };
}
export function setLocationLabel(text) {
  const node = document.getElementById("loc-text");
  if (node) node.textContent = text || "Todo el Valle";
  guardar(LOC_KEY, text || "");
}
export function mountFooter() {
  const footer = document.createElement("footer");
  footer.className = "site-footer";
  footer.innerHTML = `<a class="footer-brand" href="/clientes/">AgroValle <span>CONNECT</span></a><span>Frescura local, comunidad fuerte.</span><span>© 2026 AgroValle Connect</span>`;
  document.body.append(footer);
}
