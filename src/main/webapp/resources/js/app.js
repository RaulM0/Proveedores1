// Productos simulados (pueden venir de tu API REST)
const productos = [
    { id: 1, nombre: "Producto 1", stock: 15, precio: 150, imagen: "/resources/img/imagen1.png"
 },
    { id: 2, nombre: "Producto 2", stock: 8, precio: 350, imagen: "https://via.placeholder.com/100" },
    { id: 3, nombre: "Producto 3", stock: 20, precio: 500, imagen: "https://via.placeholder.com/100" },
    { id: 4, nombre: "Producto 4", stock: 5, precio: 1200, imagen: "https://via.placeholder.com/100" },
    { id: 5, nombre: "Producto 5", stock: 30, precio: 250, imagen: "https://via.placeholder.com/100" },
    { id: 6, nombre: "Producto 6", stock: 2, precio: 999, imagen: "https://via.placeholder.com/100" }
];

// Renderizar productos
function renderProductos(lista) {
    const contenedor = document.getElementById("productos");
    contenedor.innerHTML = "";
    lista.forEach(p => {
        const card = document.createElement("div");
        card.className = "card";
        card.innerHTML = `
            <img src="${p.imagen}" alt="${p.nombre}">
            <h3>${p.nombre}</h3>
            <p><b>Stock:</b> ${p.stock}</p>
            <p><b>Precio:</b> $${p.precio}</p>
        `;
        contenedor.appendChild(card);
    });
}

// Buscar productos
document.getElementById("btnBuscar").addEventListener("click", () => {
    const texto = document.getElementById("txtBuscar").value.toLowerCase();
    const filtrados = productos.filter(p => p.nombre.toLowerCase().includes(texto));
    renderProductos(filtrados);
});

// Inicial
renderProductos(productos);

// Toggle menú lateral
document.getElementById("btnToggle").addEventListener("click", () => {
    document.getElementById("sidebar").classList.toggle("collapsed");
});
