const API = "/api/v1";
const state = { productores: [], productos: [], toastTimer: null };

const byId = (id) => document.getElementById(id);
const money = new Intl.NumberFormat("es-CO", {
  style: "currency",
  currency: "COP",
  maximumFractionDigits: 0,
});
const dateFormatter = new Intl.DateTimeFormat("es-CO", {
  dateStyle: "medium",
  timeZone: "UTC",
});

function showToast(message, type = "success") {
  const toast = byId("toast");
  toast.textContent = message;
  toast.className = `toast show ${type}`;
  window.clearTimeout(state.toastTimer);
  state.toastTimer = window.setTimeout(() => {
    toast.className = "toast";
  }, 4200);
}

async function request(url, options = {}) {
  const response = await fetch(url, {
    ...options,
    headers: { "Content-Type": "application/json", ...options.headers },
  });
  const text = await response.text();
  let result = null;
  if (text) {
    try {
      result = JSON.parse(text);
    } catch {
      result = text;
    }
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
  document.querySelectorAll(".view").forEach((view) => {
    view.classList.toggle("active", view.id === `view-${viewName}`);
  });
  document.querySelectorAll(".nav-item").forEach((button) => {
    button.classList.toggle("active", button.dataset.view === viewName);
  });
  const activeButton = document.querySelector(`.nav-item[data-view="${viewName}"]`);
  byId("breadcrumb-current").textContent = activeButton?.textContent.trim() || "Resumen";
  window.scrollTo({ top: 0, behavior: "smooth" });
}

function renderProducers() {
  const list = byId("producer-list");
  const empty = byId("producer-empty");
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
    const initials = producer.nombre.trim().split(/\s+/).slice(0, 2).map((part) => part[0]).join("").toUpperCase();
    item.append(makeElement("span", "person-avatar", initials));
    const info = makeElement("div", "person-info");
    info.append(makeElement("strong", "", producer.nombre));
    info.append(makeElement("span", "", producer.ubicacion));
    item.append(info);
    item.append(makeElement("span", "id-label", `#${producer.id}`));
    list.append(item);
  });

  byId("producer-count").textContent = state.productores.length;
  byId("producer-list-count").textContent = state.productores.length;
  empty.hidden = state.productores.length > 0;
  byId("no-producers-note").hidden = state.productores.length > 0;
  byId("product-form").querySelector("button[type='submit']").disabled = state.productores.length === 0;
}

function renderProducts() {
  const list = byId("product-list");
  const empty = byId("product-empty");
  const recentBody = byId("recent-products-body");
  list.replaceChildren();
  recentBody.replaceChildren();

  const products = [...state.productos].reverse();
  products.forEach((product) => {
    const producerName = product.productor?.nombre || "Productor";
    const item = makeElement("article", "product-item");
    item.append(makeElement("span", "product-thumb", "▧"));
    const info = makeElement("div", "product-info");
    info.append(makeElement("strong", "", product.nombre));
    info.append(makeElement("span", "", `${product.categoria} · ${product.cantidad} unidades · ${producerName}`));
    item.append(info);
    item.append(makeElement("span", "product-price", money.format(product.precio || 0)));
    list.append(item);

    if (recentBody.children.length < 5) {
      const row = document.createElement("tr");
      const nameCell = document.createElement("td");
      const productCell = makeElement("div", "product-cell");
      productCell.append(makeElement("span", "product-thumb", "▧"));
      productCell.append(makeElement("span", "", product.nombre));
      nameCell.append(productCell);
      row.append(nameCell);

      const categoryCell = document.createElement("td");
      categoryCell.append(makeElement("span", "category-badge", product.categoria));
      row.append(categoryCell);
      row.append(makeElement("td", "", `${product.cantidad} unidades`));
      row.append(makeElement("td", "", producerName));
      row.append(makeElement("td", "", money.format(product.precio || 0)));
      recentBody.append(row);
    }
  });

  byId("product-count").textContent = state.productos.length;
  byId("product-list-count").textContent = state.productos.length;
  empty.hidden = state.productos.length > 0;
  byId("recent-products-empty").hidden = state.productos.length > 0;
}

async function refreshData(showMessage = false) {
  try {
    const [productores, productos] = await Promise.all([
      request(`${API}/productores`),
      request(`${API}/productos`),
    ]);
    state.productores = Array.isArray(productores) ? productores : [];
    state.productos = Array.isArray(productos) ? productos : [];
    renderProducers();
    renderProducts();
    if (showMessage) showToast("Datos actualizados correctamente.");
  } catch (error) {
    showToast(`No fue posible cargar los datos: ${error.message}`, "error");
  }
}

document.querySelectorAll("[data-view]").forEach((button) => {
  button.addEventListener("click", () => navigate(button.dataset.view));
});
document.querySelectorAll("[data-go]").forEach((button) => {
  button.addEventListener("click", () => navigate(button.dataset.go));
});
byId("refresh-button").addEventListener("click", () => refreshData(true));

byId("producer-form").addEventListener("submit", async (event) => {
  event.preventDefault();
  const form = event.currentTarget;
  if (!form.reportValidity()) return;
  const button = form.querySelector("button[type='submit']");
  button.disabled = true;
  try {
    const data = Object.fromEntries(new FormData(form));
    const producer = await request(`${API}/productores`, {
      method: "POST",
      body: JSON.stringify(data),
    });
    form.reset();
    await refreshData();
    showToast(`Productor ${producer.nombre} registrado correctamente.`);
  } catch (error) {
    showToast(`No se pudo registrar el productor: ${error.message}`, "error");
  } finally {
    button.disabled = state.productores.length === 0;
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
    const data = {
      ...values,
      cantidad: Number(values.cantidad),
      precio: Number(values.precio),
      productorId: Number(values.productorId),
    };
    const product = await request(`${API}/productos`, {
      method: "POST",
      body: JSON.stringify(data),
    });
    form.reset();
    byId("product-form").elements.fechaCosecha.value = new Date().toISOString().slice(0, 10);
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
refreshData();
