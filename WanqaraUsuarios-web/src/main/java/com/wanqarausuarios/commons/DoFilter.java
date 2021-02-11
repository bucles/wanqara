/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wanqarausuarios.commons;

import com.wanqarausuarios.controlador.LoginControlador;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Marco
 */
@WebFilter
public class DoFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        // Set response headers to no-cache
        HttpServletResponse res = (HttpServletResponse) response;
        HttpServletRequest req = (HttpServletRequest) request;
        res.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
        res.setHeader("Pragma", "no-cache"); // HTTP 1.0.
        res.setDateHeader("Expires", 0); // Proxies.

        // Check if user logged in, if not redirect to login.xhtml
        HttpSession session = req.getSession(false);

        String loginURI = req.getContextPath() + "/login.xhtml";

        boolean loggedIn = session != null && session.getAttribute("user") != null;
        boolean loginRequest = req.getRequestURI().equals(loginURI);
        if (loggedIn || loginRequest) {
            chain.doFilter(request, response);
        } else {
            res.sendRedirect(loginURI);
        }

        //ALTERNATIVA 2//
//        if (!loggedIn || loginRequest) {
//            boolean isAjax = "XMLHttpRequest".equals(req.getHeader("X-Requested-With"));
//
//            if (!isAjax) {
//                res.sendRedirect(req.getContextPath() + "/login.xhtml");
//            } else {
//                // Redirecting an ajax request has to be done in the following way:
//                // http://javaevangelist.blogspot.dk/2013/01/jsf-2x-tip-of-day-ajax-redirection-from.html
//                String redirectURL = res.encodeRedirectURL(req.getContextPath() + "/login.xhtml");
//                StringBuilder sb = new StringBuilder();
//                sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?><partial-response><redirect url=\"").append(redirectURL).append("\"></redirect></partial-response>");
//                res.setCharacterEncoding("UTF-8");
//                res.setContentType("text/xml");
//                PrintWriter pw = response.getWriter();
//                pw.println(sb.toString());
//                pw.flush();
//            }
//        } else {
//            // Let chain of filters continue;
//            chain.doFilter(request, response);
//        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void destroy() {

    }

}
