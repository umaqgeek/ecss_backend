/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package oms.servlet;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

// Extend HttpServlet class
public class OMSServlet extends HttpServlet {

    private String message;

    public void init() throws ServletException {
        // Do required initialization
        message = "Hello World";



    }

    public void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
        // Set response content type
        response.setContentType("text/html");

        // Actual logic goes here.
        PrintWriter out = response.getWriter();
        out.println("<h1>" + message + "</h1>");
    }

    public void destroy() {
        // do nothing.
    }
//private static Context getContext(String serverURL) throws NamingException {
//        Properties p = new Properties();
//        p.put(Context.INITIAL_CONTEXT_FACTORY, "org.jnp.interfaces.NamingContextFactory");
//        p.put(Context.URL_PKG_PREFIXES, "jboss.naming:org.jnp.interfaces");
//        p.put(Context.PROVIDER_URL, serverURL);
//        return new InitialContext(p);
//    }
}
