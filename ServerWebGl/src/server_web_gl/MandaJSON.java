package server_web_gl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class MandaJSON
 */
@WebServlet(description = "Invia al browser gli URL delle mesh OPPURE le durate", urlPatterns = { "/MandaJSON" })
public class MandaJSON extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MandaJSON() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    public Boolean onWindows() {
		if (System.getProperty("os.name").startsWith("Windows")) {
			return true;
		}

		return false;
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		String filename = "";
		String fileRichiesto = request.getParameter("fileRichiesto");
		
		 PrintWriter pw = response.getWriter();  //var che serve per scrivere l'output
		
		if (fileRichiesto != null)
		{
			if (fileRichiesto.compareTo("durate")==0) {
				filename = "durate_visemi_utilizzati.json";
			}
			else if (fileRichiesto.compareTo("fonemi")==0) {
				filename = "fonemi_utilizzati.json";			
			}
			else
			{
				pw.print("REQ_ERR");
				return;
			}
			
		
		if (this.onWindows())
		{
			filename = "WEB-INF\\Visemi\\" + filename;
		    
		}
		else 
		{
			filename = "WEB-INF/Visemi/" + filename;
			
		}
				
		
       
        String mimeType = "application/json"; // settato manualmente
        String text = "";
    
        // Set content type
        response.setContentType(mimeType);
       
        // Open the file and output streams
        InputStreamReader isr = new InputStreamReader(getServletContext().getResourceAsStream(filename));
        BufferedReader reader = new BufferedReader(isr);
       

        while ( (text=reader.readLine()) != null )
        	pw.println(text);
		}
	}
		
	

}
