const API = "/api/v1/auth";
const form = document.getElementById("profile-form");
const message = document.getElementById("profile-message");
const fields = form.elements;

function notify(text, error = false) {
  message.textContent = text;
  message.className = `profile-message show${error ? " error" : ""}`;
}

async function request(url, options = {}) {
  const response = await fetch(url, { credentials: "same-origin", ...options, headers: { ...(options.body ? { "Content-Type": "application/json" } : {}), ...options.headers } });
  const result = response.status === 204 ? null : await response.json().catch(() => ({}));
  if (!response.ok) throw new Error(result?.message || "No fue posible completar la solicitud.");
  return result;
}

async function loadProfile() {
  try {
    const profile = await request(`${API}/profile`);
    ["nombre", "apellido", "correo", "telefono"].forEach((key) => {
      if (fields.namedItem(key)) fields.namedItem(key).value = profile[key] || "";
    });
    const phoneField = document.getElementById("phone-field");
    phoneField.hidden = profile.tipo === "ADMIN";
    phoneField.querySelector("input").required = profile.tipo === "COMPRADOR";
    if (profile.tipo === "COMPRADOR") loadOrders();
    else {
      const ordersMessage = document.getElementById("orders-message");
      ordersMessage.textContent = "El historial de compras está disponible para cuentas de comprador.";
      ordersMessage.classList.add("show");
    }
  } catch {
    window.location.replace("/acceso/index.html?next=%2Fcuenta%2F");
  }
}

form.addEventListener("submit", async (event) => {
  event.preventDefault();
  if (!form.reportValidity()) return;
  const button = form.querySelector("button[type='submit']");
  button.disabled = true;
  const data = Object.fromEntries(new FormData(form));
  try {
    const profile = await request(`${API}/profile`, { method: "POST", body: JSON.stringify(data) });
    notify(`Cambios guardados para ${profile.nombre}.`);
    form.elements.contrasenaActual.value = "";
    form.elements.contrasenaNueva.value = "";
  } catch (error) {
    notify(error.message, true);
  } finally {
    button.disabled = false;
  }
});

// El cierre de sesión lo maneja el encabezado compartido (/shared/tienda.js).
loadProfile();

async function loadOrders() {
  const messageNode = document.getElementById("orders-message");
  const list = document.getElementById("orders-list");
  const currency = new Intl.NumberFormat("es-CO", { style: "currency", currency: "COP", maximumFractionDigits: 0 });
  try {
    const orders = await request("/api/v1/pedidos/mios");
    list.replaceChildren();
    if (!orders.length) {
      messageNode.textContent = "Todavía no tienes pedidos registrados.";
      messageNode.classList.add("show");
      return;
    }
    messageNode.classList.remove("show", "error");
    orders.forEach(order => {
      const card = document.createElement("article");
      card.className = "order-history-item";
      const heading = document.createElement("div");
      heading.className = "order-history-heading";
      const title = document.createElement("strong");
      title.textContent = order.referencia;
      const status = document.createElement("span");
      status.className = "order-status";
      status.textContent = String(order.estado || "Pendiente").replaceAll("_", " ");
      heading.append(title, status);
      const detail = document.createElement("p");
      detail.textContent = order.detalle || "Productos del pedido";
      const meta = document.createElement("p");
      meta.className = "order-history-meta";
      const date = new Date(order.fechaCreacion);
      meta.textContent = `${Number.isNaN(date.getTime()) ? order.fechaCreacion : date.toLocaleString("es-CO")} · ${currency.format(Number(order.total) || 0)}`;
      card.append(heading, detail, meta);
      list.append(card);
    });
  } catch (error) {
    messageNode.textContent = error.message || "No fue posible cargar tu historial.";
    messageNode.classList.add("show", "error");
  }
}
