const API = "/api/v1/auth";
const query = new URLSearchParams(window.location.search);
let pending = false;
const form = document.getElementById("login-form");
const message = document.getElementById("access-message");

function showMessage(text) {
    message.textContent = text;
    message.className = "access-message show error";
}

form.addEventListener("submit", async event => {
    event.preventDefault();
    if (pending || !form.reportValidity()) return;
    pending = true;

    const button = form.querySelector("button[type='submit']");
    button.disabled = true;

    try {
        const data = Object.fromEntries(new FormData(form));
        const response = await fetch(`${API}/login`, {
            method: "POST",
            credentials: "same-origin",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ ...data, tipo: "AUTO" })
        });

        const result = await response.json().catch(() => ({}));
        if (!response.ok) throw new Error(result.message || "Usuario o contraseña incorrectos.");

        const next = query.get("next");
        const allowed = ["/carrito/", "/cuenta/", "/admin.html"];
        window.location.assign(next && allowed.includes(next) ? next : result.tipo === "ADMIN" ? "/admin.html" : "/clientes/");
    } catch (error) {
        showMessage(error.message);
    } finally {
        pending = false;
        button.disabled = false;
    }
});

if (query.get("registro") === "ok") {
    message.textContent = "Cuenta creada. Ya puedes iniciar sesión.";
    message.className = "access-message show";
}
