import { CATEGORIAS, arteDe, claveCategoria, etiquetaCategoria, iniciales, municipioDe, ubicacionDe } from "/shared/producto-art.js";

const API = "/api/v1";
const state = { productores: [], productos: [], resumen: null, categoria: "TODAS", toastTimer: null };

const byId = (id) => document.getElementById(id);
const money = new Intl.NumberFormat("es-CO", { style: "currency", currency: "COP", maximumFractionDigits: 0 });
const percent = new Intl.NumberFormat("es-CO", { minimumFractionDigits: 1, maximumFractionDigits: 1 });
const monthName = (offset = 0) => {
  const date = new Date(); date.setDate(1); date.setMonth(date.getMonth() + offset);
  return new Intl.DateTimeFormat("es-CO", { month: "long" }).format(date);
};
const compactMoney = (value) => (Number(value) >= 1_000_000 ? `$ ${new Intl.NumberFormat("es-CO", { minimumFractionDigits: 2, maximumFractionDigits: 2 }).format(Number(value) / 1_000_000)} M` : money.format(Number(value) || 0));
const ORDER_STATES = {
  PAGO_PENDIENTE: ["Pendiente", ""], PAGO_INICIADO: ["Pago iniciado", "status-way"], CONFIRMADO: ["Confirmado", "status-way"],
  PAGADO: ["Pagado", "status-way"], PREPARANDO: ["Preparando", "status-way"], EN_CAMINO: ["En camino", "status-way"], ENTREGADO: ["Entregado", "status-ok"],
};

function showToast(message, type = "success") {
  const toast = byId("toast");
  toast.textContent = message;
  toast.className = `toast show ${type}`;
  window.clearTimeout(state.toastTimer);
  state.toastTimer = window.setTimeout(() => { toast.className = "toast"; }, 4200);
}

async function request(url, options = {}) {
  const response = await fetch(url, { ...options, headers: { "Content-Type": "application/json", ...options.headers } });
  const text = await response.text();
  let result = null;
  if (text) {
    try { result = JSON.parse(text); } catch { result = text; }
  }
  if (!response.ok) {
    const message = typeof result === "string" ? result : result?.message;
    throw new Error(message || `La solicitud falló (${response.status}).`);
  }
  return result;
}

function makeElement(tag, className, text) {
  const element = document.createElement(tag);
  if (className) element.className = className;
  if (text !== undefined) element.textContent = text;
  return element;
}

function navigate(viewName) {
  document.querySelectorAll(".view").forEach((view) => view.classList.toggle("active", view.id === `view-${viewName}`));
  document.querySelectorAll(".nav-item").forEach((button) => button.classList.toggle("active", button.dataset.view === viewName));
  const activeButton = document.querySelector(`.nav-item[data-view="${viewName}"]`);
  byId("breadcrumb-current").textContent = activeButton?.textContent.trim() || "Resumen";
  window.scrollTo({ top: 0, behavior: "smooth" });
}

const productTown = (product) => municipioDe(ubicacionDe(product));
const productsOf = (producer) => state.productos.filter((product) => product.productor?.id === producer.id);

/* ---------- Resumen ---------- */
function renderSummary() {
  const summary = state.resumen;
  const towns = new Set(state.productores.map((producer) => municipioDe(producer.ubicacion)).filter(Boolean));
  const categories = new Set(state.productos.map((product) => claveCategoria(product.categoria)));
  byId("producer-count").textContent = state.productores.length;
  byId("product-count").textContent = state.productos.length;
  byId("kpi-producers-foot").textContent = `${state.productores.length ? `en ${towns.size} ${towns.size === 1 ? "municipio" : "municipios"}` : "Sin registros"}`;
  byId("kpi-products-foot").textContent = state.productos.length ? `en ${categories.size} ${categories.size === 1 ? "categoría" : "categorías"}` : "Sin publicaciones";
  byId("sales-eyebrow").textContent = `VENTAS DEL MES · ${monthName().toUpperCase()}`;
  if (!summary) return;

  byId("sales-total").textContent = money.format(Number(summary.ventasMes) || 0);
  byId("kpi-sales").textContent = compactMoney(summary.ventasMes);
  const delta = byId("sales-delta");
  delta.replaceChildren();
  const orders = `${summary.pedidosMes} ${summary.pedidosMes === 1 ? "pedido" : "pedidos"}`;
  const kpiFoot = byId("kpi-sales-foot");
  if (summary.variacionMes === null || summary.variacionMes === undefined) {
    delta.append(`Sin ventas en ${monthName(-1)} para comparar · ${orders}`);
    kpiFoot.textContent = "Sin mes anterior"; kpiFoot.className = "stat-foot";
  } else {
    const down = summary.variacionMes < 0;
    const badge = makeElement("b", down ? "down" : "", `${down ? "▼" : "▲"} ${percent.format(Math.abs(summary.variacionMes))}%`);
    delta.append(badge, ` frente a ${monthName(-1)} · ${orders}`);
    kpiFoot.textContent = `${down ? "▼" : "▲"} ${percent.format(Math.abs(summary.variacionMes))}% vs. mes anterior`;
    kpiFoot.className = `stat-foot ${down ? "down" : "up"}`;
  }
  byId("kpi-orders").textContent = summary.pedidosActivos;
  byId("kpi-orders-foot").textContent = summary.pedidosSinPago ? `${summary.pedidosSinPago} pendientes de pago` : "Todo al día";

  const week = (summary.ventasSemana || []).map(Number);
  const max = Math.max(...week, 0);
  byId("week-bars").replaceChildren(...["L", "M", "M", "J", "V", "S", "D"].map((letter, index) => {
    const bar = makeElement("div", `week-bar${max > 0 && week[index] === max ? " top" : ""}`);
    const fill = makeElement("i"); fill.style.height = `${max ? Math.max(4, (week[index] / max) * 100) : 4}%`;
    bar.title = `${money.format(week[index] || 0)}`;
    bar.append(fill, makeElement("span", "", letter));
    return bar;
  }));

  const body = byId("recent-orders-body");
  body.replaceChildren(...(summary.pedidosRecientes || []).map((order) => {
    const row = document.createElement("tr");
    const [label, style] = ORDER_STATES[order.estado] || [String(order.estado || "").replaceAll("_", " "), ""];
    const status = document.createElement("td"); status.append(makeElement("span", `status-badge ${style}`.trim(), label));
    row.append(makeElement("td", "", `#${order.id}`), makeElement("td", "", order.cliente), makeElement("td", "", order.producto), makeElement("td", "", money.format(Number(order.total) || 0)), status);
    return row;
  }));
  byId("recent-orders-empty").hidden = (summary.pedidosRecientes || []).length > 0;

  byId("top-list").replaceChildren(...(summary.masVendidos || []).map((item) => {
    const art = arteDe({ nombre: item.nombre });
    const row = makeElement("article", "top-item");
    const tile = makeElement("span", "art-tile", art.emoji); tile.style.background = art.bg;
    const info = makeElement("div"); info.append(makeElement("strong", "", item.nombre), makeElement("span", "", `${item.unidades} u vendidas`));
    row.append(tile, info, makeElement("span", "qty", `${item.unidades} u`));
    return row;
  }));
  byId("top-empty").hidden = (summary.masVendidos || []).length > 0;
}

/* ---------- Productores ---------- */
function renderProducers() {
  const list = byId("producer-list");
  const select = byId("product-producer");
  list.replaceChildren();
  select.replaceChildren();

  const placeholder = makeElement("option", "", state.productores.length ? "Selecciona un productor" : "Registra un productor primero");
  placeholder.value = "";
  select.append(placeholder);

  state.productores.forEach((producer) => {
    const option = makeElement("option", "", `${producer.nombre} · ${producer.ubicacion}`);
    option.value = producer.id;
    select.append(option);

    const item = makeElement("article", "directory-item");
    item.append(makeElement("span", "person-avatar", iniciales(producer.nombre)));
    const info = makeElement("div", "person-info");
    info.append(makeElement("strong", "", producer.nombre));
    info.append(makeElement("span", "", [municipioDe(producer.ubicacion), producer.contacto].filter(Boolean).join(" · ")));
    item.append(info, makeElement("span", "id-label", `#${producer.id}`));
    list.append(item);
  });

  byId("producer-list-count").textContent = state.productores.length;
  byId("producer-empty").hidden = state.productores.length > 0;
  byId("no-producers-note").hidden = state.productores.length > 0;
  byId("product-form").querySelector("button[type='submit']").disabled = state.productores.length === 0;
  renderProducerPanel();
}

function renderProducerPanel() {
  const towns = new Set(state.productores.map((producer) => municipioDe(producer.ubicacion)).filter(Boolean));
  byId("panel-producers").textContent = state.productores.length;
  byId("panel-towns").textContent = towns.size;
  byId("panel-products").textContent = state.productos.length;

  const counts = new Map();
  state.productos.forEach((product) => { const town = productTown(product); if (town) counts.set(town, (counts.get(town) || 0) + 1); });
  const sorted = [...counts.entries()].sort((a, b) => b[1] - a[1]);
  const max = sorted[0]?.[1] || 1;
  byId("town-bars").replaceChildren(...sorted.map(([town, count]) => {
    const row = makeElement("div", "town-row");
    const track = makeElement("div", "town-track"); const fill = makeElement("i"); fill.style.width = `${(count / max) * 100}%`; track.append(fill);
    row.append(makeElement("span", "", town), track, makeElement("b", "", String(count)));
    return row;
  }));

  byId("producer-table").replaceChildren(...state.productores.map((producer) => {
    const total = productsOf(producer).length;
    const row = document.createElement("tr");
    const status = document.createElement("td"); status.append(makeElement("span", `status-badge ${total ? "status-ok" : ""}`.trim(), total ? "Activo" : "En pausa"));
    row.append(makeElement("td", "", producer.nombre), makeElement("td", "", municipioDe(producer.ubicacion)), makeElement("td", "", producer.contacto || "—"), makeElement("td", "", String(total)), status);
    return row;
  }));
  byId("producer-panel").hidden = state.productores.length === 0;
}

/* ---------- Productos ---------- */
function renderCategoryOptions() {
  const select = byId("product-category");
  if (select.options.length > 1) return;
  Object.entries(CATEGORIAS).forEach(([key, label]) => { const option = makeElement("option", "", label); option.value = key; select.append(option); });
}

function renderProducts() {
  const list = byId("product-list");
  const present = [...new Set(state.productos.map((product) => claveCategoria(product.categoria)))];
  const keys = [...Object.keys(CATEGORIAS).filter((key) => present.includes(key)), ...present.filter((key) => !CATEGORIAS[key])];
  if (state.categoria !== "TODAS" && !keys.includes(state.categoria)) state.categoria = "TODAS";
  byId("product-filters").replaceChildren(...[["TODAS", "Todos"], ...keys.map((key) => [key, etiquetaCategoria(key)])].map(([key, label]) => {
    const chip = makeElement("button", state.categoria === key ? "active" : "", label);
    chip.type = "button"; chip.dataset.category = key;
    return chip;
  }));

  const products = [...state.productos].reverse().filter((product) => state.categoria === "TODAS" || claveCategoria(product.categoria) === state.categoria);
  list.replaceChildren(...products.map((product) => {
    const art = arteDe(product);
    const card = makeElement("article", "product-card");
    const tile = makeElement("div", "art", art.emoji); tile.style.background = art.bg;
    const body = makeElement("div", "body");
    const place = productTown(product);
    body.append(makeElement("span", "cat", etiquetaCategoria(product.categoria)), makeElement("h3", "", product.nombre),
      makeElement("p", "meta", `${product.productor?.nombre || "Productor"}${place ? ` · ${place}` : ""} · ${product.cantidad} u`));
    const row = makeElement("div", "price-row");
    const price = makeElement("strong", "", money.format(product.precio || 0)); price.append(makeElement("small", "", " / unidad"));
    const repeat = makeElement("button", "repeat", "+");
    repeat.type = "button"; repeat.title = "Publicar otra cosecha de este producto"; repeat.setAttribute("aria-label", `Publicar otra cosecha de ${product.nombre}`);
    repeat.addEventListener("click", () => prefillProduct(product));
    row.append(price, repeat); body.append(row); card.append(tile, body);
    return card;
  }));

  byId("product-list-count").textContent = state.productos.length;
  byId("product-empty").hidden = state.productos.length > 0;
  renderProducerPanel();
}

function prefillProduct(product) {
  const form = byId("product-form");
  form.elements.nombre.value = product.nombre || "";
  form.elements.categoria.value = claveCategoria(product.categoria);
  form.elements.precio.value = product.precio ?? "";
  form.elements.productorId.value = product.productor?.id ?? "";
  form.elements.ubicacion.value = product.ubicacion || "";
  form.elements.cantidad.value = "";
  form.scrollIntoView({ behavior: "smooth", block: "start" });
  form.elements.cantidad.focus({ preventScroll: true });
  showToast("Cargamos los datos del producto. Indica la cantidad y la fecha de cosecha.");
}

async function refreshData(showMessage = false) {
  try {
    const [productores, productos] = await Promise.all([request(`${API}/productores`), request(`${API}/productos`)]);
    state.productores = Array.isArray(productores) ? productores : [];
    state.productos = Array.isArray(productos) ? productos : [];
    state.resumen = await request(`${API}/admin/resumen`).catch(() => null);
    renderProducers();
    renderProducts();
    renderSummary();
    if (showMessage) showToast("Datos actualizados correctamente.");
  } catch (error) {
    showToast(`No fue posible cargar los datos: ${error.message}`, "error");
  }
}

document.querySelectorAll("[data-view]").forEach((button) => button.addEventListener("click", () => navigate(button.dataset.view)));
document.querySelectorAll("[data-go]").forEach((button) => button.addEventListener("click", () => navigate(button.dataset.go)));
byId("refresh-button").addEventListener("click", () => refreshData(true));
byId("product-filters").addEventListener("click", (event) => {
  const chip = event.target.closest("[data-category]");
  if (!chip) return;
  state.categoria = chip.dataset.category;
  renderProducts();
});
byId("product-producer").addEventListener("change", (event) => {
  const producer = state.productores.find((item) => String(item.id) === event.target.value);
  byId("product-location").placeholder = producer?.ubicacion || "Ej. Tuluá, Valle del Cauca";
});

async function loadAdminIdentity() {
  try {
    const session = await request(`${API}/auth/session`);
    if (!session.autenticado || session.tipo !== "ADMIN") {
      window.location.replace("/acceso/index.html?next=%2Fadmin.html");
      return;
    }
    const name = String(session.nombre || "Cuenta").trim();
    const parts = name.split(/\s+/);
    byId("admin-initials").textContent = parts.length > 1 ? iniciales(name) : name.slice(0, 2).toUpperCase();
    byId("admin-account").setAttribute("aria-label", `Mi cuenta, ${parts[0]}`);
    byId("admin-account").title = `Mi cuenta, ${parts[0]}`;
  } catch {
    window.location.replace("/acceso/index.html?next=%2Fadmin.html");
  }
}
loadAdminIdentity();
byId("admin-logout").addEventListener("click", async () => {
  try {
    await fetch(`${API}/auth/logout`, { method: "POST", credentials: "same-origin" });
  } finally {
    window.location.replace("/clientes/index.html");
  }
});

byId("producer-form").addEventListener("submit", async (event) => {
  event.preventDefault();
  const form = event.currentTarget;
  if (!form.reportValidity()) return;
  const button = form.querySelector("button[type='submit']");
  button.disabled = true;
  try {
    const data = Object.fromEntries(new FormData(form));
    data.contacto = String(data.contacto || "").trim();
    const producer = await request(`${API}/productores`, { method: "POST", body: JSON.stringify(data) });
    form.reset();
    await refreshData();
    showToast(`Productor ${producer.nombre} registrado correctamente.`);
  } catch (error) {
    showToast(`No se pudo registrar el productor: ${error.message}`, "error");
  } finally {
    button.disabled = false;
  }
});

byId("product-form").addEventListener("submit", async (event) => {
  event.preventDefault();
  const form = event.currentTarget;
  if (!form.reportValidity()) return;
  const button = form.querySelector("button[type='submit']");
  button.disabled = true;
  try {
    const values = Object.fromEntries(new FormData(form));
    const data = { ...values, cantidad: Number(values.cantidad), precio: Number(values.precio), productorId: Number(values.productorId) };
    const product = await request(`${API}/productos`, { method: "POST", body: JSON.stringify(data) });
    form.reset();
    form.elements.fechaCosecha.value = new Date().toISOString().slice(0, 10);
    byId("product-location").placeholder = "Ej. Tuluá, Valle del Cauca";
    await refreshData();
    showToast(`Producto ${product.nombre} publicado correctamente.`);
  } catch (error) {
    showToast(`No se pudo publicar el producto: ${error.message}`, "error");
  } finally {
    button.disabled = state.productores.length === 0;
  }
});

byId("today-label").textContent = new Intl.DateTimeFormat("es-CO", { dateStyle: "full" }).format(new Date());
byId("product-form").elements.fechaCosecha.value = new Date().toISOString().slice(0, 10);
renderCategoryOptions();
refreshData();
