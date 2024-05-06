package util.out;


import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import user.controller.UserController;

import javax.servlet.Servlet;

public class TomcatStarter {

    public static void main(String[] args) throws Exception {
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(8080);
        Context context = tomcat.addContext("", null);

        Servlet userServlet = new UserController();
        Tomcat.addServlet(context, "users", userServlet);
        context.addServletMapping("/users/*", "users");

        tomcat.start();
        tomcat.getServer().await();
    }
}
