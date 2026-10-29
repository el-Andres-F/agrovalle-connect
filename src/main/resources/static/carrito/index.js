import { API, ADDR_KEY, money, mountHeader, mountFooter, getCart, saveCart, addToCart, isFav, toggleFav, getFavs, leer, guardar } from "/shared/tienda.js";
import { arteDe, claveCategoria, iniciales, ubicacionDe, municipioDe } from "/shared/producto-art.js";

const byId = (id) => document.getElementById(id);
const make = (tag, className, text) => { const node = document.createElement(tag); if (className) node.className = className; if (text !== undefined) node.textContent = text; return node; };
let cart = getCart();
let catalog = [];
let busy = false;

mountHeader({ active: "inicio" });
mountFooter();

function persist() { saveCart(cart); render(); }
function shortName(name) {
  const parts = String(name || "").trim().split(/\s+/);
  return parts.length > 1 ? `${parts[0][0]}. ${parts[1].length < 3 && parts[2] ? parts[2] : parts[1]}` : parts[0] || "Productor";
}

function cartLine(item, index) {
  const art = arteDe({ nombre: item.name, categoria: item.category });
  const row = make("article", "cart-line");
  const tile = make("div", "line-art", art.emoji); tile.style.background = art.bg;

  const info = make("div", "line-info");
  info.append(make("h3", "", item.name), make("p", "", `${item.producer || "Productor local"}${item.place ? ` · ${municipioDe(item.place)}` : ""}`));
  const fav = make("button", "fav-toggle", isFav(item.id) ? "♥ Guardado en favoritos" : "♡ Guardar en favoritos");
  fav.type = "button";
  fav.addEventListener("click", () => { toggleFav(item.id); render(); });
  info.append(fav);

  const qty = make("div", "qty");
  const minus = make("button", "", "−"); minus.type = "button"; minus.setAttribute("aria-label", `Quitar unidad de ${item.name}`);
  minus.addEventListener("click", () => changeQuantity(index, -1));
  const plus = make("button", "", "+"); plus.type = "button"; plus.setAttribute("aria-label", `Agregar unidad de ${item.name}`);
  plus.disabled = Number(item.quantity) >= Number(item.available);
  plus.addEventListener("click", () => changeQuantity(index, 1));
  qty.append(minus, make("span", "", String(item.quantity)), plus);

  const price = make("div", "line-price");
  price.append(make("strong", "", money.format(Number(item.price) * Number(item.quantity))), make("small", "", `${money.format(Number(item.price))} c/u`));

  const remove = make("button", "remove-line", "🗑"); remove.type = "button"; remove.setAttribute("aria-label", `Quitar ${item.name} del carrito`);
  remove.addEventListener("click", () => { cart.splice(index, 1); persist(); });
  row.append(tile, info, qty, price, remove);
  return row;
}

function miniCard(product, kind) {
  const art = arteDe(product);
  const card = make("article", "mini-card");
  const tile = make(kind === "fav" ? "button" : "div", "mini-art", art.emoji); tile.style.background = art.bg;
  if (kind === "fav") { tile.type = "button"; tile.title = "Agregar al carrito"; tile.setAttribute("aria-label", `Agregar ${product.nombre} al carrito`); tile.addEventListener("click", () => { addToCart(product); cart = getCart(); render(); }); }
  const foot = make("div", "mini-foot");
  const text = make("div"); text.append(make("strong", "", product.nombre), make("small", "", money.format(Number(product.precio) || 0)));
  foot.append(text);
  if (kind === "fav") {
    const heart = make("button", "heart on", "♥"); heart.type = "button"; heart.setAttribute("aria-label", `Quitar ${product.nombre} de favoritos`);
    heart.addEventListener("click", () => { toggleFav(product.id); render(); });
    foot.append(heart);
  } else {
    const add = make("button", "mini-add", "+"); add.type = "button"; add.setAttribute("aria-label", `Agregar ${product.nombre} al carrito`);
    add.addEventListener("click", () => { addToCart(product); cart = getCart(); render(); });
    foot.append(add);
  }
  card.append(tile, foot);
  return card;
}

function render() {
  cart = cart.filter((item) => item && Number(item.quantity) > 0);
  const subtotal = cart.reduce((sum, item) => sum + Number(item.price) * Number(item.quantity), 0);
  const producers = [...new Set(cart.map((item) => item.producer || "Productor local"))];
  byId("cart-lines").replaceChildren(...cart.map(cartLine));
  byId("item-count").textContent = `${cart.length} ${cart.length === 1 ? "producto" : "productos"} de ${producers.length} ${producers.length === 1 ? "productor" : "productores"}`;
  byId("cart-empty").hidden = cart.length > 0;
  byId("clear-cart").hidden = cart.length === 0;
  byId("subtotal").textContent = money.format(subtotal);
  byId("grand-total").textContent = money.format(subtotal);
  byId("pay-label").textContent = cart.length ? `Pagar ${money.format(subtotal)} con PSE` : "Pagar con PSE";
  byId("pay-button").disabled = cart.length === 0 || busy;
  byId("producer-chips").replaceChildren(...producers.map((name) => { const chip = make("span", "producer-chip"); chip.append(make("b", "", iniciales(name)), shortName(name)); return chip; }));
  renderSuggestions();
  saveCart(cart);
}

function renderSuggestions() {
  const inCart = new Set(cart.map((item) => Number(item.id)));
  const favs = catalog.filter((product) => getFavs().includes(Number(product.id)));
  byId("favs-grid").replaceChildren(...favs.map((product) => miniCard(product, "fav")));
  byId("favs-card").hidden = favs.length === 0;
  const cartCategories = new Set(cart.map((item) => claveCategoria(item.category)));
  const recs = catalog
    .filter((product) => !inCart.has(Number(product.id)) && !getFavs().includes(Number(product.id)) && Number(product.cantidad) > 0)
    .sort((a, b) => Number(cartCategories.has(claveCategoria(b.categoria))) - Number(cartCategories.has(claveCategoria(a.categoria))) || Number(b.id) - Number(a.id))
    .slice(0, 4);
  byId("recs-grid").replaceChildren(...recs.map((product) => miniCard(product, "rec")));
  byId("recs-card").hidden = recs.length === 0;
}

function changeQuantity(index, delta) {
  const item = cart[index]; if (!item) return;
  const next = Number(item.quantity) + delta;
  if (next < 1) cart.splice(index, 1); else item.quantity = Math.min(next, Number(item.available) || next);
  persist();
}

function message(text, success = false) {
  const node = byId("payment-message");
  node.textContent = text;
  node.classList.toggle("success", success);
  node.classList.add("show");
}

byId("clear-cart").addEventListener("click", () => { cart = []; persist(); });
byId("address").value = leer(ADDR_KEY, "");
byId("address").addEventListener("input", (event) => guardar(ADDR_KEY, event.target.value));

byId("pay-button").addEventListener("click", async () => {
  if (!cart.length || busy) return;
  busy = true; byId("pay-button").disabled = true;
  try {
    const response = await fetch(`${API}/pagos/pse`, {
      method: "POST", credentials: "same-origin", headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ items: cart.map((item) => ({ productoId: Number(item.id), cantidad: Number(item.quantity) })), direccion: byId("address").value.trim() }),
    });
    const result = await response.json().catch(() => ({}));
    if (response.status === 401) { window.location.assign("/acceso/index.html?next=%2Fcarrito%2F"); return; }
    if (!response.ok) throw new Error(result.message || "No se pudo iniciar el pago PSE.");
    if (!result.pagoDisponible) {
      cart = []; persist();
      message(`${result.mensaje} Referencia: ${result.referencia}.`, true);
      return;
    }
    if (!result.urlPago) throw new Error("No se recibió el enlace para completar el pago PSE.");
    window.location.assign(result.urlPago);
  } catch (error) {
    message(error.message);
  } finally {
    busy = false; byId("pay-button").disabled = cart.length === 0;
  }
});

render();
fetch(`${API}/productos`).then((response) => (response.ok ? response.json() : [])).then((products) => { catalog = Array.isArray(products) ? products : []; renderSuggestions(); }).catch(() => {});
