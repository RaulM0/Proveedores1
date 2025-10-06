//package com.refaccionaria.proveedoresInternos.usuarios;
//
//import jakarta.enterprise.context.SessionScoped;
//import jakarta.inject.Named;
//import java.io.Serializable;
//import java.util.*;
//
//@Named("actividadBean") // reemplaza a @ManagedBean(name="actividadBean")
//@SessionScoped           // igual funcionalidad pero CDI
//public class ActividadBean implements Serializable {
//
//    private String operacionSeleccionada;
//    private String usuarioSeleccionado;
//    private List<Actividad> listaActividades;
//    private List<Usuario> listaUsuarios;
//
//    public ActividadBean() {
//        // Usuarios simulados
//        listaUsuarios = Arrays.asList(
//            new Usuario("admin", "Administrador"),
//            new Usuario("juan", "Juan Pérez"),
//            new Usuario("maria", "María López")
//        );
//
//        // Actividades simuladas
//        listaActividades = new ArrayList<>();
//        listaActividades.add(new Actividad("2025-10-01", "Venta", "Juan Pérez"));
//        listaActividades.add(new Actividad("2025-10-02", "Pago", "María López"));
//        listaActividades.add(new Actividad("2025-10-03", "Devolución", "Administrador"));
//    }
//
//    public void consultar() {
//        System.out.println("Consultando actividades de: " + usuarioSeleccionado + " - " + operacionSeleccionada);
//    }
//
//    // Getters y setters
//    public String getOperacionSeleccionada() { return operacionSeleccionada; }
//    public void setOperacionSeleccionada(String operacionSeleccionada) { this.operacionSeleccionada = operacionSeleccionada; }
//
//    public String getUsuarioSeleccionado() { return usuarioSeleccionado; }
//    public void setUsuarioSeleccionado(String usuarioSeleccionado) { this.usuarioSeleccionado = usuarioSeleccionado; }
//
//    public List<Actividad> getListaActividades() { return listaActividades; }
//    public List<Usuario> getListaUsuarios() { return listaUsuarios; }
//
//    // Clases internas simuladas
//    public static class Actividad {
//        private String fecha;
//        private String operacion;
//        private String usuario;
//
//        public Actividad(String fecha, String operacion, String usuario) {
//            this.fecha = fecha;
//            this.operacion = operacion;
//            this.usuario = usuario;
//        }
//        public String getFecha() { return fecha; }
//        public String getOperacion() { return operacion; }
//        public String getUsuario() { return usuario; }
//    }
//
//    public static class Usuario {
//        private String usuario;
//        private String nombre;
//
//        public Usuario(String usuario, String nombre) {
//            this.usuario = usuario;
//            this.nombre = nombre;
//        }
//        public String getUsuario() { return usuario; }
//        public String getNombre() { return nombre; }
//    }
//}
