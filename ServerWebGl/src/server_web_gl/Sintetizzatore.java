package server_web_gl;
import java.io.*;
import java.util.*;


public class Sintetizzatore {
	/* Classe che si occupa della sintetizzazione dell'audio */

	// lista dei fonemi letti
	public String fonemi[];

	// durata di ogni fonema
	public int durate[];

	// path del file audio salvato
	public String audiofile;
	private BufferedReader in;
	
	// MODIFICA NOSTRA
	private Runtime runtime;
	private Process processo;
	private InputStream espeakStandardOutputStream, espeakStandardErrorStream;
	private InputStream mbrolaStandardOutputStream, mbrolaStandardErrorStream;
	
	private String rootDirectory;
	
	public Sintetizzatore() {
		fonemi = null;
		durate = null;
		in = null;
		
		runtime = Runtime.getRuntime();
		processo = null;
		rootDirectory=null; // per la costruzione del path assoluto
	}

	
	
	public void setRootDirectory(String rootDirectory) {
		this.rootDirectory = rootDirectory;
	}



	public Boolean onWindows() {
		if (System.getProperty("os.name").startsWith("Windows")) {
			return true;
		}

		return false;
	}

	public void sintetizza(String frase, int veloc) {
		/*
		 * Riempie i campi fonemi e durate invocando MBROLA, � modificabile
		 * qualora si volesse usare un diverso sintetizzatore
		 */

		// Correzione di eventuali errori nel testo di input, delegati alla
		// classe astratta textpatcher.
		// Facilmente customizzabile secondo gli errori riscontrati in fase di
		// debug.
		frase = TextPatcher.Correggi(frase);

		// Contiene l'intero file .pho in stringa
		String temp_read = "";
		String temp_file = "";
		int counter = 0;

		
		
		try {

			File fpho = null;
					
			if (this.onWindows())
			{
				fpho = new File(rootDirectory + "Sintetizzatore\\output.pho");
	
			}	// cancella il vecchio .pho
			else {
				fpho = new File(rootDirectory + "Sintetizzatore/output.pho");
				
			}
			
			fpho.delete();

			// invoca la sintetizzazione audio di espeak
			if (this.onWindows()) {
				
				processo = runtime.exec(rootDirectory + "Sintetizzatore\\espeak.exe -v mb-it3 -s " + veloc
								+ " --phonout=" + rootDirectory + "Sintetizzatore\\output.pho" + " "
								+ "\"" + frase + "\""); 
			} else {
				
				// DA ESPEAK 1.44 IN POI MBROLA VIENE CHIAMATO AUTOMATICAMENTE: NON È POSSIBILE
			//	processo = runtime.exec(new String[] {"sh", "-l", "-c", "/usr/bin/espeak -v mb-it3 -s " + veloc + " " + "\"" + frase + "\" " + "- >" + rootDirectory + "Sintetizzatore/output.pho"});
			
				processo = runtime.exec(new String[] {"sh", "-l", "-c", "/usr/bin/espeak -v mb-it3 -s " + veloc + " --pho " + "\"" + frase + "\" " + " >" + rootDirectory + "Sintetizzatore/output.pho"});

			
				espeakStandardOutputStream = processo.getInputStream();
				espeakStandardErrorStream = processo.getErrorStream();
				
			}

				processo.waitFor(); // attende il completamento

			if (fpho.exists()) {
			
			if (this.onWindows())
			{
				in = new BufferedReader(
					new FileReader(rootDirectory + "Sintetizzatore\\output.pho"));
			}
			else
			{
				in = new BufferedReader(
						new FileReader(rootDirectory + "Sintetizzatore/output.pho"));
					
			}
				
			// legge tutto il file .pho per ottenere le informazioni sui fonemi
			while (true) {

				if ((temp_read = in.readLine()) != null) {

					if (temp_read.compareTo("\n") != -1) {
						temp_file += temp_read + "\n";
						counter++;
					}
				} else {
					break;
				}
			}

			fonemi = new String[counter];
			durate = new int[counter];

			// tokenizza per ritorni a capo
			StringTokenizer s = new StringTokenizer(temp_file, "\n");

			for (int i = 0, count = 0; i < counter; i++) {
				String tmp = s.nextToken();
				if (tmp.compareTo("") != 0) {

					// tokenizza con il TAB
					StringTokenizer temp = new StringTokenizer(tmp, "\t");

					fonemi[count] = temp.nextToken();
					durate[count] = Integer.parseInt(temp.nextToken());
					count++;
				}

			}

			in.close();
			
			// unisce il doppio silenzio iniziale
			mergeSilence();

			generaAudio();
		
			}
		} catch (Exception E) {
			E.printStackTrace();
		}

	}

	private void mergeSilence() {

		int cddurate[] = new int[durate.length - 1];
		String cdfonemi[] = new String[durate.length - 1];

		if (fonemi[0].equals(fonemi[1])) {
			for (int i = 1; i < durate.length; i++) {
				cdfonemi[i - 1] = fonemi[i];
				cddurate[i - 1] = durate[i];
			}
			cddurate[0] += durate[0];

			durate = cddurate;
			fonemi = cdfonemi;
		}
	}

	private void generaAudio() {
		/* Genera il file audio .wav. */

		try {
			File fwav=null;
			if (this.onWindows()) fwav = new File(rootDirectory + "Sintetizzatore\\output.wav");
			else fwav = new File(rootDirectory + "Sintetizzatore/output.wav");
			
			fwav.delete();
			// Ritarda il rilascio della risorsa finch� non ha generato il file
			// wav.

			if (this.onWindows()) {
				processo = runtime.exec(rootDirectory + "Sintetizzatore\\mbrola.exe " + rootDirectory + "Sintetizzatore\\it3 " + rootDirectory + "Sintetizzatore\\output.pho " + rootDirectory + "Sintetizzatore\\output.wav");	
				processo.waitFor();
				audiofile = rootDirectory + "Sintetizzatore\\output.wav";
			} else {
				processo = runtime.exec(new String[] {"/usr/bin/mbrola", rootDirectory + "Sintetizzatore/it3", rootDirectory + "Sintetizzatore/output.pho", rootDirectory + "Sintetizzatore/output.wav"});
				processo.waitFor();
				audiofile = rootDirectory + "Sintetizzatore/output.wav";
			}
	
			mbrolaStandardOutputStream = processo.getInputStream();
			mbrolaStandardErrorStream = processo.getErrorStream();

		} catch (Exception E) {
			E.printStackTrace();
		}

	}

	public String getAudioFile() {
		return audiofile;
	}

	public String[] getFonemi() {
		return fonemi;
	}

	public int[] getDurate() {
		return durate;
	}

	public int getDurataTot() {
		int Tot = 0;

		for (int i = 0; i < durate.length; i++) {
			Tot += durate[i];
		}
		return Tot;
	}



	public InputStream getEspeakStandardOutputStream() {
		return espeakStandardOutputStream;
	}



	public InputStream getEspeakStandardErrorStream() {
		return espeakStandardErrorStream;
	}



	public InputStream getMbrolaStandardOutputStream() {
		return mbrolaStandardOutputStream;
	}



	public InputStream getMbrolaStandardErrorStream() {
		return mbrolaStandardErrorStream;
	}

}