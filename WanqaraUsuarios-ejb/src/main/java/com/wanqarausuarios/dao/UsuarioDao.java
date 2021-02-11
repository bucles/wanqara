/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wanqarausuarios.dao;

import com.wanqarausuarios.generico.Generico;
import com.wanqarausuarios.modelo.Usuario;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;

/**
 *
 * @author Marco
 */
@LocalBean
@Stateless
public class UsuarioDao extends Generico<Usuario> implements Serializable {

    public Usuario obtenerUsuarioPorUserNameOPorEmail(String userName, String email) {
        try {
            Query q;
            q = getEntityManager().createQuery("SELECT u FROM Usuario u "
                    + "WHERE u.usuUsuario =:userName OR u.usuEmail =:email");
            q.setMaxResults(1);
            q.setParameter("userName", userName);
            q.setParameter("email", email);
            return (Usuario) q.getSingleResult();
        } catch (Exception e) {
            System.err.println(e);
            return null;
        }
    }

    public Usuario obtenerUsuarioPorUserName(String userName) {
        try {
            Query q;
            q = getEntityManager().createQuery("SELECT u FROM Usuario u "
                    + "WHERE u.usuUsuario =:userName");
            q.setMaxResults(1);
            q.setParameter("userName", userName);
            return (Usuario) q.getSingleResult();
        } catch (Exception e) {
            System.err.println(e);
            return null;
        }
    }

    public Usuario obtenerUsuarioPorEmailPorPassword(String email, String password) {
        try {
            Query q;
            q = getEntityManager().createQuery("SELECT u FROM Usuario u "
                    + " WHERE u.usuEmail =:email "
                    + " AND u.usuPassword =:password");
            q.setMaxResults(1);
            q.setParameter("email", email);
            q.setParameter("password", password);
            return (Usuario) q.getSingleResult();
        } catch (Exception e) {
            System.err.println(e);
            return null;
        }
    }

    public Usuario obtenerUsuarioPorUsuarioPorPassword(String userName, String password) {
        try {
            Query q;
            q = getEntityManager().createQuery("SELECT u FROM Usuario u "
                    + " WHERE u.usuUsuario =:userName "
                    + " AND u.usuPassword =:password");
            q.setMaxResults(1);
            q.setParameter("userName", userName);
            q.setParameter("password", password);
            return (Usuario) q.getSingleResult();
        } catch (Exception e) {
            System.err.println(e);
            return null;
        }
    }

    public List<Usuario> listarUsuarios() {
        try {
            Query q;
            q = getEntityManager().createQuery("SELECT u FROM Usuario u");
            return q.getResultList();
        } catch (Exception e) {
            System.err.println(e);
            return new ArrayList<>();
        }

    }

    public Usuario obtenerUsuarioPorEmail(String email) {
        try {
            Query q;
            q = getEntityManager().createQuery("SELECT u FROM Usuario u "
                    + " WHERE u.usuEmail =:email ");
            q.setMaxResults(1);
            q.setParameter("email", email);
            return (Usuario) q.getSingleResult();
        } catch (Exception e) {
            System.err.println(e);
            return null;
        }
    }
}
