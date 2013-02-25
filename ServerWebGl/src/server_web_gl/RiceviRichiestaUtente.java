package server_web_gl;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Servlet implementation class ServerWebGl
 */
@WebServlet("/RiceviRichiestaUtente")
public class RiceviRichiestaUtente extends HttpServlet {
	private static final long serialVersionUID = 1L;
     private Sintetizzatore sintetizzatore;
     private Fonemi2VisemiIta fonemi_visemi;
     private String serverRoot;
     
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RiceviRichiestaUtente() {
        super();
        // per potere richiamare programmi esterni abbiamo bisogno di costruire un path assoluto
        // ma la visibilit� � ordinariamente limitata alla directory radice della gerarchia del progetto
        // per questo utilizziamo la funzione gi� disponibile per farci restituire il path assoluto della radice stessa
        // da concatenare a tutto il resto.
    	sintetizzatore=new Sintetizzatore();
    	fonemi_visemi=new Fonemi2VisemiIta();
    	
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
    

	public Boolean onWindows() {
		if (System.getProperty("os.name").startsWith("Windows")) {
			return true;
		}

		return false;
	}
    
	private String leggi_emozione_da_file(String nomeFile)
	{
		 String emozione = null;
		  
		try {
			  File fileEmozione = new File(nomeFile);
			  DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			  DocumentBuilder db = dbf.newDocumentBuilder();
			  Document doc = db.parse(fileEmozione);
			  doc.getDocumentElement().normalize();
			  NodeList nodeLst = doc.getElementsByTagName("emozione");


			  Node fstNode = nodeLst.item(0);
  
			  
			  if (fstNode.getNodeType() == Node.ELEMENT_NODE) {
			  
			      Element fstElmnt = (Element) fstNode;
			      NodeList fstNmElmntLst = fstElmnt.getElementsByTagName("tipo");
			      Element fstNmElmnt = (Element) fstNmElmntLst.item(0);
			      NodeList fstNm = fstNmElmnt.getChildNodes();
			      NodeList lstNmElmntLst = fstElmnt.getElementsByTagName("percentuale");
			      Element lstNmElmnt = (Element) lstNmElmntLst.item(0);
			      NodeList lstNm = lstNmElmnt.getChildNodes();
			      emozione = ((Node) fstNm.item(0)).getNodeValue() + ((Node) lstNm.item(0)).getNodeValue();
			      System.out.println(emozione);

			    }

			  } catch (Exception e) {
			    e.printStackTrace();
			  }
		
		return emozione;
	}
	
	private Boolean scrivi_file_json_visemi(String durate)
	{
		String nome_file_durate_visemi_utilizzati=serverRoot + "Visemi/durate_visemi_utilizzati.json";
		
		File file_durate_visemi_utilizzati = new File(nome_file_durate_visemi_utilizzati);
		
		file_durate_visemi_utilizzati.delete();
    
		scrivi_file_json(nome_file_durate_visemi_utilizzati, durate);
		
		if (file_durate_visemi_utilizzati.exists())
			return true;
		
		return false;
	}
	
	private void scrivi_file_json(String nomeFile, String contenuto)
	{
		try {
			PrintWriter scrittore_file = new PrintWriter(new FileWriter(nomeFile));
			
			scrittore_file.print(contenuto);
			scrittore_file.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private JSONObject leggi_file_json(String nomeFile)
	{
	    JSONParser parser = new JSONParser();
		JSONObject json_parsato = null; 
	    
			try {
				
					json_parsato = (JSONObject)parser.parse(new FileReader(nomeFile));
				} catch (ParseException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
								
			 
			  
			 return json_parsato;
	}
	
	void genera_file_json_fonemi_utilizzati(String[] fonemi)
	{
		JSONArray fonemi_utilizzati = new JSONArray();
		
		for(int i=0;i<fonemi.length;i++)
			fonemi_utilizzati.add(fonemi[i]);
		
		scrivi_file_json(getServletContext().getRealPath("Visemi/fonemi_utilizzati.json"),fonemi_utilizzati.toJSONString());
	}
	
	void genera_file_json_con_morphTargets(LinkedList<String> lista_visemi_utilizzati) // String emozione, String percentuale
	{
		String nome_file_visema_silenzio = "";
		
		int i = 1;	
		
		JSONWriter scrittore_json_ritorno_a_capo = new JSONWriter();
		JSONObject JSON_visema_silenzio = null;
		
		if (this.onWindows()) nome_file_visema_silenzio = getServletContext().getRealPath("Visemi\\silence.js");
		else nome_file_visema_silenzio=getServletContext().getRealPath("Visemi/silence.js");
		
	
		
		  JSON_visema_silenzio = leggi_file_json(nome_file_visema_silenzio);  
		  
		  ListIterator<String> itr = lista_visemi_utilizzati.listIterator();
		  
		  JSONArray morphTargets = new JSONArray();
		  
		  while (itr.hasNext()) // considero tutti i visemi
		  {
			  String nome_file_visema_corrente = itr.next();
			  nome_file_visema_corrente = getServletContext().getRealPath(nome_file_visema_corrente);
			  
			  JSONObject JSON_visema_corrente = leggi_file_json(nome_file_visema_corrente);
			  
			  JSONArray vertici = (JSONArray) JSON_visema_corrente.get("vertices");
			  
			  JSONObject morphTarget = new JSONObject();
			  
			  String targetName = "target"+String.valueOf(i);
			  
			  morphTarget.put("vertices", (Object)vertici);
			  morphTarget.put("name", (Object)targetName);
			  
			  morphTargets.add(morphTarget); 
			  
			  i++;
		  }
		 
	 
		  
		 JSON_visema_silenzio.remove("morphTargets");
		 JSON_visema_silenzio.put("morphTargets", morphTargets);
		 try {
			JSON_visema_silenzio.writeJSONString(scrittore_json_ritorno_a_capo);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		 if (this.onWindows()) scrivi_file_json(getServletContext().getRealPath("Visemi\\frase.js"), scrittore_json_ritorno_a_capo.toString());
		 else scrivi_file_json(getServletContext().getRealPath("Visemi/frase.js"), scrittore_json_ritorno_a_capo.toString());
		 
		 try {
			scrittore_json_ritorno_a_capo.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private Boolean aggiungiSilenzioWebGl(File output_wav)
	{
		File silenzio_wav = new File(serverRoot + "Sintetizzatore/silenzio.wav");
		File temp_wav = new File(serverRoot + "Sintetizzatore/temp.wav");
		
		temp_wav.delete();
		
		WavAppender.fondiWav(silenzio_wav, output_wav, temp_wav);
		
		output_wav.delete();
		temp_wav.renameTo(output_wav);
			
		return output_wav.exists();
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.serverRoot=getServletContext().getRealPath("/WEB-INF");
		
		if (this.onWindows()) serverRoot = serverRoot + "\\";
		else serverRoot=serverRoot + "/";
				
		String frase = request.getParameter("frase");
		String velocita = request.getParameter("velocita");
		String emozione = null; 
		String emozioneInterattiva = request.getParameter("emozioneInterattiva");
		
		File output_pho = new File(serverRoot + "Sintetizzatore/output.pho");
		File output_wav = new File(serverRoot + "Sintetizzatore/output.wav");
		
		if (!output_pho.exists())
			output_pho.createNewFile();
		
		if (!output_wav.exists())
			output_wav.createNewFile();
		
		LinkedList<String> lista_visemi_utilizzati = new LinkedList<String>();		
		
		JSONArray durate_visemi_utilizzati = new JSONArray();
		
	  	response.setContentType(""); // scegliere content type
	   	PrintWriter out = response.getWriter(); // serve per scrivere materialmente la risposta per la chiamante
	   
	   	String s = "";

		if (emozioneInterattiva!=null)
		{
			emozione=request.getParameter("emozione");
			if (emozione.compareTo("neutral") != 0) {
				String percentuale = request.getParameter("percentuale");
				emozione = emozione+percentuale;
			}
		}
		else
			emozione=leggi_emozione_da_file(getServletContext().getRealPath("emozioni.xml"));
		
		if (frase!=null && velocita != null) 
		{
			sintetizzatore.setRootDirectory(serverRoot);
			fonemi_visemi.setRootDirectory(serverRoot);
			
			sintetizzatore.sintetizza(frase, Integer.parseInt(velocita));
			
			int[] durate = sintetizzatore.getDurate();
			String[] fonemi = sintetizzatore.getFonemi();
			
			for (int i = 0; i < durate.length; i++) {

				// Converte il fonema in visema (path del visema)
				String visema = fonemi_visemi.converti(fonemi[i]);

				// cerca il path del visema e restituisce l'indice relativo
				// all'array di caricamento costruito nel costruttore
				//int temp = Fonemi2VisemiIta.getvisIndex(visema);
				
				lista_visemi_utilizzati.addLast(visema);				
				durate_visemi_utilizzati.add(durate[i]); // aggiunge in coda
			}
	
			// l'emozione è l'ultimo morphtarget			

			if (emozione!=null && emozione.compareTo("neutral")!=0)
			{
				String visema_emozione=fonemi_visemi.converti(emozione);
				lista_visemi_utilizzati.addLast(visema_emozione);
			}	
				
			genera_file_json_con_morphTargets(lista_visemi_utilizzati);
			genera_file_json_fonemi_utilizzati(fonemi);
			
			if (output_pho.exists() && output_wav.exists() && scrivi_file_json_visemi(durate_visemi_utilizzati.toJSONString())
) // Se i file sono stati generati � andato
			{
				if (aggiungiSilenzioWebGl(output_wav))
				{
					out.print("REQ_OK");
					return;
				}
			}
			else
			{
				BufferedReader espeakStandardErrorStreamReader = new BufferedReader(new 
			             InputStreamReader(sintetizzatore.getEspeakStandardErrorStream()));
				
				BufferedReader espeakStandardOutputStreamReader = new BufferedReader(new 
			             InputStreamReader(sintetizzatore.getEspeakStandardOutputStream()));
				
				BufferedReader mbrolaStandardErrorStreamReader = new BufferedReader(new 
			             InputStreamReader(sintetizzatore.getMbrolaStandardErrorStream()));
				
				BufferedReader mbrolaStandardOutputStreamReader = new BufferedReader(new 
			             InputStreamReader(sintetizzatore.getMbrolaStandardOutputStream()));
				
				while ( (s = espeakStandardOutputStreamReader.readLine()) != null )
				{
					out.println(s);
				}
				
				while ( (s = espeakStandardErrorStreamReader.readLine()) != null )
				{
					out.println(s);
				}
				
				while ( (s = mbrolaStandardOutputStreamReader.readLine()) != null )
				{
					out.println(s);
				}
				
				while ( (s = mbrolaStandardErrorStreamReader.readLine()) != null )
				{
					out.println(s);
				}
			}
		}
		else
			out.println("Errore nella richiesta POST");
		
	}

}
