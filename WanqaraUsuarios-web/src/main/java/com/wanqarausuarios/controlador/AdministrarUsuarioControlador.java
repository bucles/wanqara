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
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import javax.imageio.ImageIO;

import javax.servlet.http.HttpSession;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.file.UploadedFile;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

/**
 *
 * @author Marco
 */
/*
@Named(value = "administrarUsuarioControlador")
@RequestScoped
 */
@ManagedBean(name = "administrarUsuarioControlador")
@RequestScoped
public class AdministrarUsuarioControlador implements Serializable {

    @EJB
    private UsuarioDao usuarioDao;

    @Getter
    @Setter
    private List<Usuario> listaUsuarios;

    @Getter
    @Setter
    private Usuario usuario;

    @Getter
    @Setter
    private HttpSession session;

    @Getter
    @Setter
    private UploadedFile imagenUsuario;

    @Getter
    @Setter
    private String passwordAux;

    @Getter
    @Setter
    private StreamedContent scImagenUsuario;

    @Getter
    @Setter
    private String emailDestino;

    public AdministrarUsuarioControlador() {
        listaUsuarios = new ArrayList<>();
        usuario = new Usuario();
        imagenUsuario = null;
        passwordAux = "";
        emailDestino = "";
    }

    @PostConstruct
    public void iniciar() {
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            //recuperar datos de sesion: primera alternativa
            session = (HttpSession) context.getCurrentInstance().getExternalContext().getSession(true);
            usuario = (Usuario) session.getAttribute("user");
            usuario.setUsuPassword(EncryptAES.decrypt(usuario.getUsuPassword(), Constantes.KEY));

            // recuperar datos de sesion: segunda alternativa
            Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();

        } catch (Exception e) {
            System.err.println(e);
        }

    }

    public void editarUsuario() {
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            if (validarPassword()) {
                Usuario usu = usuarioDao.obtenerUsuarioPorUserNameOPorEmail(usuario.getUsuUsuario().trim(),
                        usuario.getUsuEmail());
                if ((usu.getUsuId() == usuario.getUsuId()) || usu == null) {
                    usuario.setUsuPassword(EncryptAES.encrypt(usuario.getUsuPassword(), Constantes.KEY));
                    if (imagenUsuario != null) {
                        if (imagenUsuario.getSize() < 65000) {
                            subirImagenUsuario(imagenUsuario);
                            usuarioDao.edit(usuario);
                            enviarEmailUsuario(usuario);
                            context.addMessage(null, new FacesMessage("éxito", "Usuario editado"));
                        } else {
                            usuario = usuarioDao.find(new Usuario(), usuario.getUsuId());
                            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "No se pudo editar el Usuario. Imagen muy grande", null);
                            context.addMessage(null, message);
                        }
                    } else {
                        usuarioDao.edit(usuario);
                        enviarEmailUsuario(usuario);
                        context.addMessage(null, new FacesMessage("éxito", "Usuario editado"));
                    }

                } else {
                    FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ya existe usuario", null);
                    context.addMessage(null, message);

                }
            } else {
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Las contraseñas no coinciden", null);
                context.addMessage(null, message);
            }
        } catch (Exception e) {
            System.err.println(e);
        }

    }

    public boolean validarPassword() {
        if (passwordAux.trim().equals(usuario.getUsuPassword().trim())) {
            return true;
        } else {
            return false;
        }
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

    public StreamedContent getImagen() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();

        if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
            return new DefaultStreamedContent();
        } else {

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            BufferedImage img = ImageIO.read(context.getExternalContext()
                    .getResourceAsStream("/images/p.jpg"));
            int w = img.getWidth(null);
            int h = img.getHeight(null);

            int scale = 2;

            BufferedImage bi = new BufferedImage(w * scale, h * scale,
                    BufferedImage.TYPE_INT_ARGB);
            Graphics g = bi.getGraphics();

            g.drawImage(img, 10, 10, w * scale, h * scale, null);

            ImageIO.write(bi, "png", bos);

            return new DefaultStreamedContent(new ByteArrayInputStream(
                    bos.toByteArray()), "image/png");

        }
    }

    public StreamedContent getImagenDB() throws IOException {

        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        byte[] image = null;
        try {
            image = usuario.getUsuImagen();

            return new DefaultStreamedContent(new ByteArrayInputStream(image),
                    "image/png");

        } catch (Exception e) {
            return null;

        }
//        FacesContext context = FacesContext.getCurrentInstance();
//        if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
//            return new DefaultStreamedContent();
//        } else {
//
//            ByteArrayOutputStream bos = new ByteArrayOutputStream();
//
//            byte[] image = null;
//            try {
//                image = usuario.getUsuImagen();
//            } catch (Exception e) {
//
//            }
//
//            return new DefaultStreamedContent(new ByteArrayInputStream(image),
//                    "image/png");
//
//        }
    }

    public void prepararEditar() {
        usuario = usuarioDao.find(new Usuario(), usuario.getUsuId());
        usuario.setUsuPassword(EncryptAES.decrypt(usuario.getUsuPassword(), Constantes.KEY));
        passwordAux = usuario.getUsuPassword();
        imagenUsuario = null;
    }

    public void enviarEmailUsuario(Usuario usuario) {
        try {
            String emailDdestino = usuario.getUsuEmail();
            String titulo = "Wanqara Test";
            String password = EncryptAES.decrypt(usuario.getUsuPassword(), Constantes.KEY);
            String mensaje = "Estimado " + usuario.getUsuNombre().trim()
                    + " " + usuario.getUsuApellido().trim() + "<br><br>"
                    + "Su usuario ha sido actualizado, sus credenciales de acceso son: <br><br><br>"
                    + "Usuario: " + usuario.getUsuUsuario().trim() + "<br>"
                    + "Contraseña: " + password;

            EnviarEmailControlador.enviarEmailHtml(emailDdestino,
                    titulo,
                    mensaje);
        } catch (Exception e) {
            System.err.println(e);
        }

    }

    public void reenviarEmailUsuario() {
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
        emailDestino = usuario.getUsuEmail();
    }
}
