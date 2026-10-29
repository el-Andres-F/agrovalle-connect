const API = "/api/v1/auth";
const form = document.getElementById("register-form");
const message = document.getElementById("register-message");
let pending = false;

function showMessage(text, error = false) {
    message.textContent = text;
    message.className = `access-message show ${error ? 'error' : ''}`;
}

form.addEventListener("submit", async event => {
    event.preventDefault();
    if (pending || !form.reportValidity()) return;
    pending = true;

    const button = form.querySelector("button[type='submit']");
    button.disabled = true;

    try {
        const formData = new FormData(form);
        const data = {
            nombre: formData.get("nombre"),
            apellido: formData.get("apellido"),
            correo: formData.get("correo"),
            telefono: formData.get("telefono") || null,
            contrasena: formData.get("contrasena"),
            tipo: "COMPRADOR"
        };

        const response = await fetch(`${API}/register`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(data)
        });

        const result = await response.json().catch(() => ({}));

        if (!response.ok) {
    throw new Error(result.error || result.message || `Error ${response.status}: no fue posible crear la cuenta.`);
}

        // Registro exitoso, redirigir al login
        window.location.assign("/acceso/index.html?registro=ok");

    } catch (error) {
        showMessage(error.message, true);
    } finally {
        pending = false;
        button.disabled = false;
    }
});
