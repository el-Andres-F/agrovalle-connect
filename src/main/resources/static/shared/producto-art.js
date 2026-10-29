// Ilustración (emoji + color de fondo) y nombres de categoría compartidos por la tienda y el panel de gestión.
export const CATEGORIAS = {
  FRUTAS: "Frutas", VERDURAS: "Verduras", TUBERCULOS: "Tubérculos", GRANOS: "Granos", LACTEOS: "Lácteos",
  HIERBAS: "Hierbas", CAFE: "Café", CEREALES: "Cereales", LEGUMBRES: "Legumbres", OTROS: "Otros",
};
const CATEGORIA_ARTE = {
  FRUTAS: ["🍎", "#F7E3D5"], VERDURAS: ["🥦", "#E3EBDD"], TUBERCULOS: ["🥔", "#F2E3C8"], GRANOS: ["🌾", "#F4EFC8"],
  LACTEOS: ["🥛", "#EAF0F3"], HIERBAS: ["🌿", "#E3EBDD"], CAFE: ["☕", "#E9E1D3"], CEREALES: ["🌾", "#F4EFC8"],
  LEGUMBRES: ["🫘", "#F0E4D7"], OTROS: ["🌱", "#E9E1D3"],
};
const REGLAS = [
  [/aguacate|palta/, "🥑", "#E7E3D3"], [/tomate/, "🍅", "#F5D9D0"], [/coco/, "🥥", "#F5E6C4"],
  [/platano|banano|guineo/, "🍌", "#F9EFC4"], [/zanahoria/, "🥕", "#F7DCC6"], [/maiz|choclo/, "🌽", "#F2EFC9"],
  [/pina/, "🍍", "#F8EDC6"], [/lechuga|espinaca|acelga|repollo/, "🥬", "#E7E1D2"], [/limon/, "🍋", "#F6F0C8"],
  [/cebolla|ajo/, "🧅", "#F2E8DC"], [/papa|yuca|batata/, "🥔", "#F2E3C8"], [/naranja|mandarina/, "🍊", "#F9E2CB"],
  [/mango/, "🥭", "#F9E5BF"], [/fresa|frutilla/, "🍓", "#F7D9DD"], [/cafe/, "☕", "#E9E1D3"],
  [/frijol|lenteja|arveja/, "🫘", "#F0E4D7"], [/leche|queso/, "🥛", "#EAF0F3"], [/cilantro|albahaca|menta/, "🌿", "#E3EBDD"],
];

export function normalizar(valor) {
  return String(valor || "").normalize("NFD").replace(/[\u0300-\u036f]/g, "").toLowerCase();
}
export function claveCategoria(categoria) {
  return normalizar(categoria).trim().toUpperCase();
}
export function etiquetaCategoria(categoria) {
  return CATEGORIAS[claveCategoria(categoria)] || categoria || "Otros";
}
export function arteDe(producto) {
  const nombre = normalizar(producto.nombre || producto.name || "");
  const regla = REGLAS.find(([patron]) => patron.test(nombre));
  if (regla) return { emoji: regla[1], bg: regla[2] };
  const [emoji, bg] = CATEGORIA_ARTE[claveCategoria(producto.categoria)] || CATEGORIA_ARTE.OTROS;
  return { emoji, bg };
}
export function iniciales(nombre) {
  return String(nombre || "?").trim().split(/\s+/).slice(0, 2).map((parte) => parte[0]).join("").toUpperCase();
}
export function ubicacionDe(producto) {
  return producto.ubicacion || producto.productor?.ubicacion || "";
}
// Municipio de una ubicación como "Palmira, Valle del Cauca".
export function municipioDe(texto) {
  return String(texto || "").split(",")[0].trim();
}
