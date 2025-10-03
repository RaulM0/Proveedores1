/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.refaccionaria.proveedoresInternos;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import java.io.Serializable;

@Named("loginBean")
@SessionScoped
public class LoginBean implements Serializable {

    private String usuario;
    private String password;

    // getters y setters
    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    // Método de login de prueba
    public String login() {
        if ("admin".equals(usuario) && "1234".equals(password)) {
            return "dashboard.xhtml?faces-redirect=true";
        } else {
            return "login.xhtml?faces-redirect=true";
        }
    }
}