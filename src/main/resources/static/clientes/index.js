import { API, money, mountHeader, mountFooter, setLocationLabel, leer, LOC_KEY, addToCart, isFav, toggleFav } from "/shared/tienda.js";
import { CATEGORIAS, arteDe, claveCategoria, etiquetaCategoria, iniciales, municipioDe, normalizar, ubicacionDe } from "/shared/producto-art.js";

const params = new URLSearchParams(window.location.search);
const state = {
  products: [], category: "TODOS", search: params.get("q") || "", location: leer(LOC_KEY, ""),
  price: "", onlyStock: false, onlyFavs: params.get("favoritos") === "1", sort: "recent",
};
const byId = (id) => document.getElementById(id);
const make = (tag, className, text) => { const node = document.createElement(tag); if (className) node.className = className; if (text !== undefined) node.textContent = text; return node; };

mountHeader({
  active: state.onlyFavs ? "favoritos" : "inicio", search: state.search,
  onSearch: (value) => { state.search = value; render(); },
  onLocationClick: () => { byId("filtros").scrollIntoView({ block: "center" }); byId("filter-location").focus(); },
});
mountFooter();

function priceMatches(price) {
  if (state.price === "low") return price <= 2500;
  if (state.price === "mid") return price > 2500 && price <= 5000;
  if (state.price === "high") return price > 5000;
  return true;
}

function visibleProducts() {
  const query = normalizar(state.search.trim());
  const list = state.products.filter((product) => {
    const price = Number(product.precio) || 0;
    const text = normalizar(`${product.nombre} ${etiquetaCategoria(product.categoria)} ${product.productor?.nombre} ${ubicacionDe(product)}`);
    return (state.category === "TODOS" || claveCategoria(product.categoria) === state.category)
      && (!state.location || municipioDe(ubicacionDe(product)) === state.location)
      && priceMatches(price) && (!state.onlyStock || Number(product.cantidad) > 0)
      && (!state.onlyFavs || isFav(product.id)) && (!query || text.includes(query));
  });
  const price = (product) => Number(product.precio) || 0;
  if (state.sort === "price-asc") list.sort((a, b) => price(a) - price(b));
  else if (state.sort === "price-desc") list.sort((a, b) => price(b) - price(a));
  else if (state.sort === "name") list.sort((a, b) => String(a.nombre).localeCompare(b.nombre, "es"));
  else list.sort((a, b) => Number(b.id) - Number(a.id));
  return list;
}

function productCard(product) {
  const art = arteDe(product);
  const stock = Number(product.cantidad) || 0;
  const card = make("article", "product-card");
  const tile = make("div", "product-art"); tile.style.background = art.bg;
  const illustration = make("span", "produce-mark");
  illustration.setAttribute("aria-hidden", "true");
  illustration.innerHTML = '<svg viewBox="0 0 56 56"><path d="M11 46c1-13 9-23 27-32-1 18-8 29-20 31-4 1-7 1-7 1Zm0 0c7-8 14-14 23-20M22 30C10 30 7 22 8 15c11 0 17 4 18 12m1-3c0-9 5-14 13-15"/></svg>';
  tile.append(illustration);
  const heart = make("button", `heart${isFav(product.id) ? " on" : ""}`, isFav(product.id) ? "♥" : "♡");
  heart.type = "button";
  heart.setAttribute("aria-label", `Guardar ${product.nombre} en favoritos`);
  heart.setAttribute("aria-pressed", String(isFav(product.id)));
  heart.addEventListener("click", () => {
    const on = toggleFav(product.id);
    if (state.onlyFavs) return render();
    heart.classList.toggle("on", on); heart.textContent = on ? "♥" : "♡"; heart.setAttribute("aria-pressed", String(on));
  });
  tile.append(heart); card.append(tile);

  const body = make("div", "product-body");
  body.append(make("h3", "", product.nombre));
  const place = ubicacionDe(product);
  body.append(make("p", "product-origin", `${product.productor?.nombre || "Productor local"}${place ? ` · ${municipioDe(place)}` : ""}`));
  body.append(make("p", `product-stock${stock < 1 ? " out" : stock <= 5 ? " low" : ""}`, stock < 1 ? "Agotado" : stock <= 5 ? `Quedan ${stock}` : `${stock} disponibles`));
  const foot = make("div", "product-foot");
  const price = make("span", "price", money.format(Number(product.precio) || 0));
  price.append(make("small", "", " / unidad"));
  const add = make("button", "add-button", "Agregar");
  add.type = "button"; add.disabled = stock < 1;
  add.addEventListener("click", () => addToCart(product));
  foot.append(price, add); body.append(foot); card.append(body);
  return card;
}

function renderCategories() {
  const present = new Set(state.products.map((product) => claveCategoria(product.categoria)));
  const order = [...Object.keys(CATEGORIAS).filter((key) => present.has(key)), ...[...present].filter((key) => !CATEGORIAS[key])];
  const wrap = byId("category-filters");
  wrap.replaceChildren();
  [["TODOS", "Todos"], ...order.map((key) => [key, etiquetaCategoria(key)])].forEach(([key, label]) => {
    const chip = make("button", `chip${state.category === key ? " active" : ""}`, label);
    chip.type = "button"; chip.dataset.category = key;
    wrap.append(chip);
  });
}

function renderLocations() {
  const counts = new Map();
  state.products.forEach((product) => { const town = municipioDe(ubicacionDe(product)); if (town) counts.set(town, (counts.get(town) || 0) + 1); });
  const select = byId("filter-location");
  select.replaceChildren(new Option("Todo el Valle", ""));
  [...counts.keys()].sort((a, b) => a.localeCompare(b, "es")).forEach((town) => select.append(new Option(town, town)));
  if (state.location && !counts.has(state.location)) state.location = "";
  select.value = state.location;
  setLocationLabel(state.location);
}

function renderHighlights() {
  const towns = new Map();
  state.products.forEach((product) => { const town = municipioDe(ubicacionDe(product)); if (town) towns.set(town, (towns.get(town) || 0) + 1); });
  const top = [...towns.entries()].sort((a, b) => b[1] - a[1]).slice(0, 3).map(([town]) => town);
  byId("hero-origin").textContent = top.length ? `Directo de fincas de ${top.length > 1 ? `${top.slice(0, -1).join(", ")} y ${top.at(-1)}` : top[0]}.` : "Directo de fincas del Valle del Cauca.";
  const producers = new Map();
  state.products.forEach((product) => {
    if (!product.productor) return;
    const entry = producers.get(product.productor.id) || { producer: product.productor, items: [] };
    entry.items.push(product); producers.set(product.productor.id, entry);
  });
  const best = [...producers.values()].sort((a, b) => b.items.length - a.items.length).slice(0, 3);
  const grid = byId("producer-grid");
  grid.replaceChildren();
  best.forEach(({ producer, items }) => {
    const card = make("article", "producer-card");
    const head = make("header");
    const who = make("div");
    who.append(make("strong", "", producer.nombre), make("small", "", municipioDe(producer.ubicacion)));
    head.append(make("span", "initials", iniciales(producer.nombre)), who);
    const offers = make("p", "offers"); offers.append("Ofrece: ", make("b", "", items.slice(0, 3).map((item) => item.nombre).join(", ")));
    card.append(head, make("p", "", `${items.length} ${items.length === 1 ? "producto publicado" : "productos publicados"}.`), offers);
    grid.append(card);
  });
  byId("comunidad").hidden = best.length === 0;
}

function render() {
  const list = visibleProducts();
  const grid = byId("catalog-grid");
  grid.replaceChildren(...list.map(productCard));
  byId("result-count").textContent = `${list.length} ${list.length === 1 ? "producto" : "productos"}${state.onlyFavs ? " en tus favoritos" : ""}`;
  const empty = list.length === 0;
  byId("catalog-empty").hidden = !empty;
  if (empty) {
    const noData = state.products.length === 0;
    byId("empty-title").textContent = state.onlyFavs ? "Aún no tienes favoritos" : noData ? "Todavía no hay productos publicados" : "No encontramos productos";
    byId("empty-text").textContent = state.onlyFavs ? "Toca el corazón de un producto para guardarlo aquí." : noData ? "Vuelve pronto: los productores están publicando sus cosechas." : "Prueba con otra búsqueda o categoría.";
  }
  document.querySelectorAll("#category-filters .chip").forEach((chip) => chip.classList.toggle("active", chip.dataset.category === state.category));
  byId("filter-favs").setAttribute("aria-pressed", String(state.onlyFavs));
  byId("filter-stock").setAttribute("aria-pressed", String(state.onlyStock));
}

async function loadProducts() {
  try {
    const response = await fetch(`${API}/productos`);
    if (!response.ok) throw new Error();
    state.products = await response.json();
  } catch {
    state.products = [];
    byId("result-count").textContent = "No fue posible cargar el catálogo. Intenta de nuevo en un momento.";
  }
  renderCategories(); renderLocations(); renderHighlights(); render();
}

byId("category-filters").addEventListener("click", (event) => {
  const chip = event.target.closest("[data-category]");
  if (!chip) return;
  state.category = chip.dataset.category; render();
});
byId("filter-location").addEventListener("change", (event) => { state.location = event.target.value; setLocationLabel(state.location); render(); });
byId("filter-price").addEventListener("change", (event) => { state.price = event.target.value; render(); });
byId("sort").addEventListener("change", (event) => { state.sort = event.target.value; render(); });
byId("filter-stock").addEventListener("click", () => { state.onlyStock = !state.onlyStock; render(); });
byId("filter-favs").addEventListener("click", () => { state.onlyFavs = !state.onlyFavs; render(); });

loadProducts();
