package web;

import ejb.NewsEntity;

import java.io.IOException;
import java.io.PrintWriter;

import javax.annotation.Resource;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet PostMessage — accepts title/body, wraps them into a NewsEntity,
 * and sends it as an ObjectMessage to the JMS queue.
 */
@WebServlet(name = "PostMessage", urlPatterns = {"/PostMessage"})
public class PostMessage extends HttpServlet {

    // Inject the JMS ConnectionFactory (as configured in GlassFish)
    @Resource(lookup = "jms/NewMessageFactory")
    private ConnectionFactory connectionFactory;

    // Inject the JMS Queue (as configured in GlassFish)
    @Resource(lookup = "jms/NewMessage")
    private Queue queue;

    /**
     * Processes both HTTP GET and POST by sending a JMS message if title/body
     * are present, then rendering the HTML form.
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        String title = request.getParameter("title");
        String body  = request.getParameter("body");

        // If form was submitted, send JMS message
        if (title != null && body != null) {
            try (Connection connection = connectionFactory.createConnection()) {
                Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                MessageProducer producer = session.createProducer(queue);

                ObjectMessage message = session.createObjectMessage();
                NewsEntity e = new NewsEntity();
                e.setTitle(title);
                e.setBody(body);
                message.setObject(e);

                producer.send(message);
                producer.close();

                // After sending, redirect back to list
                response.sendRedirect("ListNews");
                return;
            } catch (JMSException ex) {
                ex.printStackTrace();
            }
        }

        // Otherwise, render the HTML form
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html><head><title>PostMessage</title></head><body>");
            out.println("<h1>Servlet PostMessage at " + request.getContextPath() + "</h1>");
            out.println("<form method='post'>");
            out.println("Title: <input type='text' name='title'><br/>");
            out.println("Message: <textarea name='body'></textarea><br/>");
            out.println("<input type='submit' value='Send'><br/>");
            out.println("</form>");
            out.println("</body></html>");
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
        return "Posts a NewsEntity to JMS queue";
    }
}
