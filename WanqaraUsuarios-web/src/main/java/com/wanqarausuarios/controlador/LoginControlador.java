/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wanqarausuarios.controlador;

import com.wanqarausuarios.commons.Constantes;
import com.wanqarausuarios.dao.UsuarioDao;
import com.wanqarausuarios.modelo.Usuario;
import com.wanqarausuarios.util.EncryptAES;
import com.wanqarausuarios.util.EnviarEmailControlador;
import java.io.IOException;
import java.io.Serializable;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpSession;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Marco
 */
@Named(value = "loginControlador")
@SessionScoped
public class LoginControlador implements Serializable {

    @EJB
    private UsuarioDao usuarioDao;

    @Getter
    @Setter
    private Usuario usuario;

    @Getter
    @Setter
    private String usuUsuario;

    @Getter
    @Setter
    private String usuPassword;

    @Getter
    @Setter
    private String emailDestino;

    @Getter
    @Setter
    private HttpSession session;

    public LoginControlador() {
        usuario = new Usuario();
        usuUsuario = "";
        usuPassword = "";
    }

    public void login() throws IOException {
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            usuario = usuarioDao.obtenerUsuarioPorUserName(usuUsuario);
            if (usuario != null) {
                //boolean passwordCorrecta = encryptPBKDF2Controlador.validarPasswordCifrada(usuPassword, usuario.getUsuPassword());
                boolean passwordCorrecta = usuPassword.equals(EncryptAES.decrypt(usuario.getUsuPassword(), Constantes.KEY));
                if (passwordCorrecta) {
                    //datos de sesion: primera alternativa
                    context.getExternalContext().getSessionMap().put("user", usuario);
                    //datos de sesion: segunda alternativa (con HttpSession)
                    session = (HttpSession) context.getCurrentInstance().getExternalContext().getSession(true);
                    session.setAttribute("user", usuario);
                    // redirigir
                    context.addMessage(null, new FacesMessage("éxito", "Bienvenido"));
                    context.getCurrentInstance().getExternalContext().redirect(FacesContext.getCurrentInstance().getExternalContext().getApplicationContextPath() + "/paginas/verUsuario.xhtml");
                } else {
                    FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Contraseña incorrecta", null);
                    context.addMessage(null, message);
                }

            } else {
                usuario = new Usuario();
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Usuario incorrecto", null);
                context.addMessage(null, message);
            }
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    public void logout() throws IOException {
        //forma 1
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        usuario = new Usuario();
        //con HttpSession
        //session.removeAttribute("user");
        //redirigir
        FacesContext.getCurrentInstance().getExternalContext().redirect(FacesContext.getCurrentInstance().getExternalContext().getApplicationContextPath() + "/login.xhtml");
    }

    public void enviarEmailUsuario() {
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            Usuario usuarioEmail = usuarioDao.obtenerUsuarioPorEmail(emailDestino);
            if (usuarioEmail != null) {
                String titulo = "Wanqara Test";
                String password = EncryptAES.decrypt(usuarioEmail.getUsuPassword(), Constantes.KEY);
                String mensaje = "Estimado " + usuarioEmail.getUsuNombre().trim()
                        + " " + usuarioEmail.getUsuApellido().trim() + "<br><br>"
                        + "Sus credenciales de acceso son: <br><br><br>"
                        + "Usuario: " + usuarioEmail.getUsuUsuario().trim() + "<br>"
                        + "Contraseña: " + password;

                EnviarEmailControlador.enviarEmailHtml(emailDestino,
                        titulo,
                        mensaje);
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Email enviado", null);
                context.addMessage(null, message);

            } else {
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "No se encontro usuario con el email ingresado", null);
                context.addMessage(null, message);
            }

        } catch (Exception e) {
            System.err.println(e);
        }

    }

    public void limpiarEnviarEmail() {
        emailDestino = "";
    }

}
