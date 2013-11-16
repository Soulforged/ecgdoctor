package edu.uk.practice;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author magian
 * 
 */
public class SimpleHttpServlet extends HttpServlet {
  private static final long serialVersionUID = -5639909541614322570L;

  @Override
  protected void doGet(final HttpServletRequest request,
      final HttpServletResponse response) throws ServletException, IOException {

    // It's the headers it contains several informations e.g what browser is
    // being used
    // final String contentLength = request.getHeader("Content-Length");

    // it serves for send a file or another tyoe of raw of stream
    // final InputStream requestBodyInput = request.getInputStream();

    // It's session it mantain information about a given user between requests
    // final HttpSession session = request.getSession();

    // It contains the meta information about the web application
    // final ServletContext context = request.getSession().getServletContext();

    doPost(request, response);
  }

  @Override
  protected void doPost(final HttpServletRequest request,
      final HttpServletResponse response) throws ServletException, IOException {

    // To send HTML back to the browser, you have to obtain the a PrintWriter
    // from the HttpResponse object
    // final PrintWriter writer = response.getWriter();
    // writer.write("<html><body>GET/POST response</body></html>");

    // idem request
    // response.setHeader("Header-Name", "Header Value");

    // he Content-Type header is a response header that tells the browser the
    // type of the content you are sending back to it. For instance, the content
    // type for HTML is text/html.
    // response.setHeader("Content-Type", "text/html");

    // You can write text back to the browser instead of HTML
    // response.setHeader("Content-Type", "text/plain");
    // final PrintWriter writerHeader = response.getWriter();
    // writerHeader.write("This is just plain text");

    // The Content-Length header tells the browser how many bytes your servlet
    // is sending back.
    // response.setHeader("Content-Length", "31642");

    // Ver bien este que creo que es el que más nos sirve
    // It can write binary data back to the browser instead of text. For
    // instance, you can send an image back, a PDF file or a Flash file or
    // something like that.
    // First have to set the Content-Type (the content type for a PNG image is
    // image/png.)
    // We can search "mime types" for the list
    // We have to use the OutputStream obtained from the
    // response.getOutputStream()

    // final OutputStream outputStream = response.getOutputStream();
    // This is the form -> outputStream.write(...);

    // Redirecting to a different URL
    // response.sendRedirect("http://jenkov.com");

    response.getWriter().write("GET/POST response");
  }
}
