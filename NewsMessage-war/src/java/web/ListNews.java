package web;

import ejb.NewsEntity;
import ejb.NewsEntityFacade;
import ejb.SessionManagerBean;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.Session;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet ListNews — displays all NewsEntity messages from the JMS queue
 * and shows the number of active HTTP sessions.
 */
@WebServlet(name = "ListNews", urlPatterns = {"/ListNews"})
public class ListNews extends HttpServlet {

    // Inject the singleton session‑bean that counts active HTTP sessions
    @EJB
    private SessionManagerBean sessionManagerBean;
    
    @EJB 
    private NewsEntityFacade newsEntityFacade;

    // Inject the JMS ConnectionFactory (as defined in GlassFish: jms/NewMessageFactory)
    @Resource(lookup = "jms/NewMessageFactory")
    private ConnectionFactory connectionFactory;

    // Inject the JMS Queue (as defined in GlassFish: jms/NewMessage)
    @Resource(lookup = "jms/NewMessage")
    private Queue queue;

    /**
     * Processes both GET and POST requests by browsing the JMS queue,
     * rendering any NewsEntity messages, and displaying the active‑session count.
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // ensure an HTTP session exists
        request.getSession(true);

        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html><head><title>Servlet ListNews</title></head><body>");
            out.println("<h1>Servlet ListNews at " + request.getContextPath() + "</h1>");

            // Browse the JMS queue
            try (Connection connection = connectionFactory.createConnection()) {
                Session jmsSession = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                QueueBrowser browser = jmsSession.createBrowser(queue);
                Enumeration<?> msgs = browser.getEnumeration();

                if (!msgs.hasMoreElements()) {
                    out.println("<br><br>No messages in queue<br><br>");
                } else {
                    while (msgs.hasMoreElements()) {
                        ObjectMessage msg = (ObjectMessage) msgs.nextElement();
                        if (msg.getObject() instanceof NewsEntity) {
                            NewsEntity elem = (NewsEntity) msg.getObject();
                            out.println("<b>" + elem.getTitle() + "</b><br/>");
                            out.println(elem.getBody() + "<br/><br/>");
                        }
                    }
                }
            } catch (Exception ex) {
                // JMS errors
                ex.printStackTrace();
            }

            out.println("<a href='PostMessage'>Add new message</a><br/><br>");

            // Display number of active HTTP sessions
            out.println(sessionManagerBean.getActiveSessionsCount()
                        + " user(s) reading the news.");

            out.println("</body></html>");
        } catch (Exception ex) {
            // I/O or other errors
            ex.printStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Lists news from JMS queue and shows active session count";
    }
}
