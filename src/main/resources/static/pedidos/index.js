import { API, money, mountHeader, mountFooter, addToCart, toast } from "/shared/tienda.js";
import { arteDe, iniciales, municipioDe, normalizar } from "/shared/producto-art.js";

const byId = (id) => document.getElementById(id);
const make = (tag, className, text) => { const node = document.createElement(tag); if (className) node.className = className; if (text !== undefined) node.textContent = text; return node; };
const dateFmt = new Intl.DateTimeFormat("es-CO", { day: "2-digit", month: "short", year: "numeric" });
const timeFmt = new Intl.DateTimeFormat("es-CO", { hour: "numeric", minute: "2-digit" });
const STEPS = ["Recibido", "Preparando", "En camino", "Entregado"];
// estado del backend -> [paso actual, texto, estilo de la etiqueta]
const STATES = {
  PAGO_PENDIENTE: [0, "Pago pendiente", ""], PAGO_INICIADO: [0, "Pago iniciado", "badge-way"], CONFIRMADO: [1, "Confirmado", "badge-way"],
  PAGADO: [1, "Pagado", "badge-way"], PREPARANDO: [1, "Preparando", "badge-way"], EN_CAMINO: [2, "En camino", "badge-way"], ENTREGADO: [3, "Entregado", "badge-ok"],
};
const stateInfo = (estado) => STATES[estado] || [0, String(estado || "").replaceAll("_", " ").toLowerCase().replace(/^./, (c) => c.toUpperCase()), ""];
const isActive = (order) => !["ENTREGADO", "CANCELADO"].includes(order.estado);
const shortRef = (order) => `#${String(order.referencia).replace(/^AV-/, "").slice(0, 6).toUpperCase()}`;

function parseDetail(detail) {
  const [products, delivery] = String(detail || "").split(" | Entrega: ");
  const items = products.split(", ").map((part) => {
    const cut = part.lastIndexOf(" x ");
    return cut < 0 ? { name: part.trim(), qty: 1 } : { name: part.slice(0, cut).trim(), qty: Number(part.slice(cut + 3)) || 1 };
  }).filter((item) => item.name);
  return { items, delivery: delivery || "" };
}

let orders = [];
let catalog = [];
let selected = null;
let showAll = false;

const findProduct = (name) => catalog.find((product) => normalizar(product.nombre) === normalizar(name));
function producersOf(order) {
  const names = new Map();
  parseDetail(order.detalle).items.forEach((item) => { const product = findProduct(item.name); if (product?.productor) names.set(product.productor.id, product.productor); });
  return [...names.values()];
}
const shortName = (name) => { const parts = String(name).trim().split(/\s+/); return parts.length > 1 ? `${parts[0][0]}. ${parts[1]}` : parts[0]; };

function renderTracking() {
  const order = selected;
  if (!order) { byId("tracking-card").hidden = true; return; }
  byId("tracking-card").hidden = false;
  const [step, label, style] = stateInfo(order.estado);
  const created = new Date(order.fechaCreacion);
  byId("tracking-title").textContent = `Seguimiento del pedido ${shortRef(order)}`;
  byId("tracking-sub").textContent = `Registrado el ${dateFmt.format(created)} a las ${timeFmt.format(created)}`;
  const badge = byId("tracking-badge"); badge.textContent = label; badge.className = `badge ${style}`.trim();
  byId("steps").replaceChildren(...STEPS.map((name, index) => {
    const state = order.estado === "ENTREGADO" || index < step ? "done" : index === step ? "current" : "future";
    const item = make("li", state);
    item.append(make("span", "step-dot", state === "done" ? "✓" : state === "current" && index === 2 ? "🚚" : state === "current" ? "●" : "○"), make("strong", "", name),
      make("small", "", index === 0 ? timeFmt.format(created) : state === "current" ? "Ahora" : state === "future" ? "Pendiente" : ""));
    return item;
  }));
  byId("route-truck").style.left = `calc(${(order.estado === "ENTREGADO" ? 3 : step) / 3 * 88 + 4}% )`;
  const { items, delivery } = parseDetail(order.detalle);
  byId("order-items").replaceChildren(...items.map((item) => { const chip = make("span"); chip.append(`${arteDe({ nombre: item.name }).emoji} ${item.name} `, make("b", "", `×${item.qty}`)); return chip; }));
  byId("order-total").textContent = `Total ${money.format(Number(order.total) || 0)}`;
  const note = byId("delivery-note"); note.hidden = !delivery; note.textContent = delivery ? `Entrega: ${delivery}` : "";
}

function renderProducers() {
  const counts = new Map();
  orders.filter((order) => order.estado !== "CANCELADO").forEach((order) => producersOf(order).forEach((producer) => {
    const entry = counts.get(producer.id) || { producer, orders: 0 }; entry.orders += 1; counts.set(producer.id, entry);
  }));
  const top = [...counts.values()].sort((a, b) => b.orders - a.orders).slice(0, 4);
  byId("producer-list").replaceChildren(...top.map(({ producer, orders: n }) => {
    const li = make("li"); const info = make("div");
    info.append(make("strong", "", producer.nombre), make("small", "", `${municipioDe(producer.ubicacion)} · ${n} ${n === 1 ? "pedido" : "pedidos"}`));
    li.append(make("span", "initials", iniciales(producer.nombre)), info); return li;
  }));
  byId("producers-empty").hidden = top.length > 0;
  byId("stat-orders").textContent = orders.length;
  byId("stat-total").textContent = money.format(orders.filter((o) => o.estado !== "CANCELADO").reduce((sum, o) => sum + (Number(o.total) || 0), 0));
}

function reorder(order) {
  let added = 0;
  parseDetail(order.detalle).items.forEach((item) => {
    const product = findProduct(item.name);
    if (!product || Number(product.cantidad) < 1) return;
    for (let i = 0; i < Math.min(item.qty, Number(product.cantidad)); i += 1) if (!addToCart(product)) break;
    added += 1;
  });
  if (!added) return toast("Esos productos ya no están disponibles.");
  window.location.assign("/carrito/");
}

function renderHistory() {
  const rows = showAll ? orders : orders.slice(0, 5);
  byId("history-body").replaceChildren(...rows.map((order) => {
    const [, label, style] = stateInfo(order.estado);
    const tr = make("tr");
    const ref = make("td", "", shortRef(order)); ref.title = order.referencia;
    const producers = producersOf(order).map((p) => shortName(p.nombre)).join(", ");
    const badgeCell = make("td"); badgeCell.append(make("span", `badge ${style}`.trim(), label));
    const actionCell = make("td"); const action = make("button", "action", isActive(order) ? "Seguir" : "Volver a comprar"); action.type = "button";
    action.addEventListener("click", () => { if (isActive(order)) { selected = order; renderTracking(); window.scrollTo({ top: 0, behavior: "smooth" }); } else reorder(order); });
    actionCell.append(action);
    tr.append(ref, make("td", "muted", dateFmt.format(new Date(order.fechaCreacion))), make("td", "muted", producers || "—"), make("td", "", money.format(Number(order.total) || 0)), badgeCell, actionCell);
    return tr;
  }));
  byId("history-empty").hidden = orders.length > 0;
  byId("toggle-history").hidden = orders.length <= 5;
  byId("toggle-history").textContent = showAll ? "Ver menos" : "Ver todos";
}

function gate(title, text, hideLogin = false) {
  byId("orders-gate").hidden = false;
  byId("gate-title").textContent = title; byId("gate-text").textContent = text;
  if (hideLogin) byId("gate-link").remove();
}

async function init() {
  mountHeader({ active: "pedidos" });
  mountFooter();
  try {
    const [ordersResponse, productsResponse] = await Promise.all([fetch(`${API}/pedidos/mios`, { credentials: "same-origin" }), fetch(`${API}/productos`)]);
    if (ordersResponse.status === 401) return gate("Inicia sesión para ver tus pedidos", "Aquí verás el seguimiento y el historial de tus compras. Los pedidos están disponibles para cuentas de comprador.");
    if (!ordersResponse.ok) throw new Error();
    orders = await ordersResponse.json();
    catalog = productsResponse.ok ? await productsResponse.json() : [];
  } catch { return gate("No pudimos cargar tus pedidos", "Intenta de nuevo en un momento.", true); }
  byId("orders-content").hidden = false;
  selected = orders.find(isActive) || orders[0] || null;
  renderTracking(); renderProducers(); renderHistory();
}

byId("toggle-history").addEventListener("click", () => { showAll = !showAll; renderHistory(); });
init();
