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
import java.io.Serializable;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.model.file.UploadedFile;

/**
 *
 * @author Marco
 */
@Named(value = "crearUsuarioControlador")
@ViewScoped
public class CrearUsuarioControlador implements Serializable {

    @Getter
    @Setter
    private EncryptAES encryptAES;

    @EJB
    private UsuarioDao usuarioDao;

    @Getter
    @Setter
    private Usuario usuario;

    @Getter
    @Setter
    private String passwordAux;

    @Getter
    @Setter
    private UploadedFile imagenUsuario;

    public CrearUsuarioControlador() {
        usuario = new Usuario();
        passwordAux = "";
        imagenUsuario = null;
        encryptAES = new EncryptAES();
    }

    public void crearUsuario() {
        try {

            FacesContext context = FacesContext.getCurrentInstance();
            if (validarPassword()) {
                Usuario usu = usuarioDao.obtenerUsuarioPorUserNameOPorEmail(usuario.getUsuUsuario().trim(),
                        usuario.getUsuEmail());
                if (usu == null) {
                    usuario.setUsuPassword(EncryptAES.encrypt(usuario.getUsuPassword(), Constantes.KEY));
                    if (imagenUsuario != null) {
                        if (imagenUsuario.getSize() < 65000) {
                            subirImagenUsuario(imagenUsuario);
                            usuarioDao.create(usuario);
                            enviarEmailUsuario(usuario);
                            limpiar();
                            context.addMessage(null, new FacesMessage("éxito", "Usuario creado"));
                        } else {
                            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "No se pudo crear el Usuario. Imagen muy grande", null);
                            context.addMessage(null, message);
                        }
                    } else {
                        usuarioDao.create(usuario);
                        enviarEmailUsuario(usuario);
                        limpiar();
                        context.addMessage(null, new FacesMessage("éxito", "Usuario creado"));
                    }

                } else {
                    FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Usuario ya existe", null);
                    context.addMessage(null, message);
                }
            } else {
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Las contraseñas no coinciden", null);
                context.addMessage(null, message);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public boolean validarPassword() {
        if (passwordAux.trim().equals(usuario.getUsuPassword().trim())) {
            return true;
        } else {
            return false;
        }
    }

    public void limpiar() {
        usuario = new Usuario();
        imagenUsuario = null;
    }

    public void subirImagenUsuario(UploadedFile file) {
        try {
            String fileName = file.getFileName();
            String contentType = file.getContentType();
            byte[] contents = file.getContent();
            usuario.setUsuImagen(contents);
        } catch (Exception ex) {
            System.err.println(ex);
        }
    }

    public void enviarEmailUsuario(Usuario usuario) {
        try {
            String emailDdestino = usuario.getUsuEmail();
            String titulo = "Wanqara Test";
            String password = EncryptAES.decrypt(usuario.getUsuPassword(), Constantes.KEY);
            String mensaje = "Estimado " + usuario.getUsuNombre().trim()
                    + " " + usuario.getUsuApellido().trim() + "<br><br>"
                    + "Su nuevo usuario ha sido creado, sus credenciales de acceso son: <br><br><br>"
                    + "Usuario: " + usuario.getUsuUsuario().trim() + "<br>"
                    + "Contraseña: " + password;

            EnviarEmailControlador.enviarEmailHtml(emailDdestino,
                    titulo,
                    mensaje);
        } catch (Exception e) {
            System.err.println(e);
        }

    }

}
