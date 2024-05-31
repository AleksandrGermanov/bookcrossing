package out;


import book.controller.BookController;
import bookrequest.controller.BookRequestController;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import user.controller.UserController;

import javax.servlet.Servlet;

public class TomcatStarter {
    private static final String TOMCAT_PORT = System.getenv("TOMCAT_PORT") != null
            ? System.getenv("TOMCAT_PORT")
            : "8080";

    public static void start() throws LifecycleException {
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(Integer.parseInt(TOMCAT_PORT));
        Context context = tomcat.addContext("", null);

        registerServlet(context, new UserController(), "users");
        registerServlet(context, new BookController(), "books");
        registerServlet(context, new BookRequestController(), "requests");

        tomcat.start();
        tomcat.getServer().await();
    }

    private static void registerServlet(Context context, Servlet servlet, String servletName) {
        Tomcat.addServlet(context, servletName, servlet);
        context.addServletMapping("/" + servletName + "/*", servletName);
    }
}
