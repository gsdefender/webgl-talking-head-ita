package server_web_gl;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.StringTokenizer;

public class Fonemi2VisemiIta {

	private  String[][] fon2vis;
	private  String[] visemi;
	private  String rootDirectory;
	
	public void setRootDirectory(String rootDirectory)
	{
		this.rootDirectory = rootDirectory;
	}
	
	private void leggiVisemi() {

		String temp_read = "";
		String temp_file = "";
		int counter = 0;
		BufferedReader in;
		StringTokenizer s;

		String filename = "";
		
		rootDirectory=rootDirectory + "/";
		filename = rootDirectory + "Visemi/fon2vis.txt";
				
		try {
			in = new BufferedReader(new FileReader(filename));
			
			while (true) {

				if ((temp_read = in.readLine()) != null) {

					temp_file += temp_read + "\n";
					counter++;

				} else {
					break;
				}
			}
			in.close();
			fon2vis = new String[counter][2];

			s = new StringTokenizer(temp_file, "[\n\t]");

			for (int i = 0; i < counter; i++) {
				fon2vis[i][0] = s.nextToken();
				fon2vis[i][1] = s.nextToken();

			}
		} catch (Exception E) {
			;
		}

		// Parte relativa ai visemi solamente
		counter = 0;
		temp_file = "";
		try {
			in = new BufferedReader(new FileReader("Visemi/nomi_visemi.txt"));
			
			while (true) {

				if ((temp_read = in.readLine()) != null) {
					temp_file += temp_read + "\n";
					counter++;
				} else {
					break;
				}
			}
			in.close();
			visemi = new String[counter];
			s = new StringTokenizer(temp_file, "[\n\t]");
			for (int i = 0; i < counter; i++) {
				visemi[i] = s.nextToken();
			}

		} catch (Exception E) {
			;
		}
	}

	public  String converti(String fonema) {

		// Case che converte i fonemi in visemi

		String conv = null;

		if (fon2vis == null) {
			leggiVisemi();
		}
		for (int i = 0; i < fon2vis.length; i++) {
			if (fon2vis[i][0].compareTo(fonema) == 0) {

				return conv = fon2vis[i][1];
			}
		}
		return conv;

	}

	public  int getvisIndex(String visema) {
		// Restituisce l'indice relativo al determinato fonema, nella tabella
		// dei visemi
		int temp = -1;
		if (fon2vis == null) {
			leggiVisemi();
		}
		for (int i = 0; i < visemi.length; i++) {
			if (visemi[i].compareTo(visema) == 0) {
				return temp = i;
			}
		}
		return temp;
	}
}