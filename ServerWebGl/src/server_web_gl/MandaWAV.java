package server_web_gl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class MandaWAV
 */
@WebServlet("/MandaWAV")
public class MandaWAV extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
   
    public MandaWAV() {
        super();
        // TODO Auto-generated constructor stub
    }

    
    public Boolean onWindows() {
		if (System.getProperty("os.name").startsWith("Windows")) {
			return true;
		}

		return false;
	}
    
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		 // Get the absolute path of the image
		String serverRoot=getServletContext().getRealPath("/WEB-INF");  //serve per far funzionare i path anche se si trasporta l'applicazione in un altro file system
		String filename = "";
		
		if (this.onWindows())
		{
			serverRoot = serverRoot + "\\";
			filename = serverRoot + "Sintetizzatore\\output.wav";
		    
		}
		else 
		{
			serverRoot=serverRoot + "/";
			filename = serverRoot + "Sintetizzatore/output.wav";
			
		}
				
		
        
        // Get the MIME type of the image
        String mimeType = getServletContext().getMimeType(filename); //la prima funzione mi restituisce un oggetto di servletContext che possiede le proprietà di questa servlet. La seconda funzione determina il tipo del file che gli sto passando alla doGet
        if (mimeType == null) {
        	getServletContext().log("Could not get MIME type of "+filename);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }
    
        // Set content type
        response.setContentType(mimeType);
    
        // Set content size
        File file = new File(filename);
        response.setContentLength((int)file.length());
    
        // Open the file and output streams
        FileInputStream in = new FileInputStream(file);
        OutputStream out = response.getOutputStream();
    
        // Copy the contents of the file to the output stream
        byte[] buf = new byte[1024];
        int count = 0;
        while ((count = in.read(buf)) >= 0) {
            out.write(buf, 0, count);
        }
        in.close();
        out.close();
	
	
	}

	
	

}
