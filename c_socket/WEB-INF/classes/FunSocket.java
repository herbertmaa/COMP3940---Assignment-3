
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


public class FunSocket extends HttpServlet{
/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		PrintWriter out = response.getWriter();

		response.setContentType("text/html");
		StringBuilder sb = new StringBuilder();
		
		sb.append(generateOpeningHtml());
		sb.append("<form>");
		sb.append("<input id=\"message\" type=\"text\">");
		sb.append("<input onclick=\"wsSendMessage();\" value=\"Echo\" type=\"button\">");
		sb.append("<input onclick=\"wsCloseConnection();\" value=\"Disconnect\" type=\"button\">");
		sb.append("</form>");
		sb.append("<textarea id=\"echoText\" rows=\"5\" cols=\"30\"></textarea>");
		
		
		sb.append(generateClosingHtml());
		out.println(sb.toString());
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

	}
	
	private String generateOpeningHtml() {
		
		StringBuilder sb = new StringBuilder();
		sb.append("<!DOCTYPE html>");
		sb.append("<html lang=\"en\">");
		sb.append("<head>");
		
		sb.append("<meta charset=\"utf-8\">");
		sb.append("<title> Socket Client </title>");
		sb.append("</head>");
		sb.append("<body>");
		return sb.toString();
	}
	
	private String generateClosingHtml() {
		StringBuilder sb = new StringBuilder();
		sb.append("</body>");
		sb.append("<script src=\"static/js/connection.js\"></script>");
		sb.append("</html>");
		return sb.toString();
	}
	
}
