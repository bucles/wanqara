/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wanqarausuarios.util;

import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 *
 * @author Marco
 */
public class EnviarEmailControlador {

    private static final String senderEmail = "la.esperanza.aloag";
    private static final String senderPassword = "laEsperanza2019";

    public static void enviarEmailHtml(String emailDestino, String titulo, String cuerpoEmail) throws MessagingException {

        Session session = crearConexionEmail();

        //crear mensaje
        MimeMessage mensaje = new MimeMessage(session);
        prepararMensajeEmail(mensaje, emailDestino, titulo, cuerpoEmail);

        //enviar mensaje
        Transport.send(mensaje);
    }

    private static void prepararMensajeEmail(MimeMessage mensaje, String emailDestino, String titulo, String cuerpoEmail)
            throws MessagingException {
        mensaje.setContent(cuerpoEmail, "text/html; charset=utf-8");
        mensaje.setFrom(new InternetAddress(senderEmail));
        mensaje.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emailDestino));
        mensaje.setSubject(titulo);
    }

    private static Session crearConexionEmail() {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderPassword);
            }
        });
        return session;
    }

}
