/**
 * 
 */
package edu.uk.practice;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import edu.uk.dromm.ECGController;
import edu.uk.dromm.fcl.Diagnosis;

/**
 * @author magian
 * 
 */
@MultipartConfig
public class SubirArchivo extends HttpServlet {
	private static final long serialVersionUID = -61993371764899695L;

	protected void processRequest(final HttpServletRequest request,
			final HttpServletResponse response) throws ServletException,
			IOException {

		final Boolean isOk = Boolean.TRUE;
		final String path = getServletContext().getRealPath(File.separator);
		final Part filePart = request.getPart("file");
		final String fileName = getFileName(filePart);

		OutputStream out = null;
		InputStream filecontent = null;
		response.setContentType("text/html");
		final PrintWriter writer = response.getWriter();

		try {
			final File outFile = new File(path + File.separator + fileName);
			out = new FileOutputStream(outFile);
			filecontent = filePart.getInputStream();

			int read = 0;
			final byte[] bytes = new byte[1024];

			while ((read = filecontent.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}
			out.close();

			final ECGController controller = new ECGController();
			final Diagnosis diagnosis = controller.start(outFile
					.getAbsolutePath());
			messageResponse(path, diagnosis, writer, isOk);
		} catch (final Exception fne) {
			fne.printStackTrace();
			messageResponse(path,
					new Diagnosis("Error", fne.getLocalizedMessage(), ""),
					writer, !isOk);
		} finally {
			if (out != null) {
				out.close();
			}
			if (filecontent != null) {
				filecontent.close();
			}
			if (writer != null) {
				writer.close();
			}
		}
	}

	private String getFileName(final Part part) {
		for (final String content : part.getHeader("content-disposition")
				.split(";")) {
			if (content.trim().startsWith("filename")) {
				return content.substring(content.indexOf('=') + 1).trim()
						.replace("\"", "");
			}
		}
		return null;
	}

	private void messageResponse(final String path, final Diagnosis diagnosis,
			final PrintWriter writer, final Boolean isOk) {
		writer.println("<html>");
		writer.println(String.format("<head><title>%s</title>",
				diagnosis.getTitle()));
		writer.println("<script src=\"resources/js/script.js\" type=\"text/javascript\"></script>");
		writer.println("<link rel=\"stylesheet\" href=\""
				+ "resources/css/style.css\" media=\"screen\"/>");
		writer.println("</head>");
		writer.println("<body id=\"background\">");
		if (isOk) {
			writer.println(String.format("<h1>%s</h1>",
					diagnosis.getDescription()));
		} else {
			writer.println(String.format("<h3>%s</h3>", diagnosis.getDetail()));
		}
		writer.println("</body></html>");
	}

	@Override
	protected void doPost(final HttpServletRequest request,
			final HttpServletResponse response) throws ServletException,
			IOException {
		processRequest(request, response);
	}

}
