package server_web_gl;

public abstract class TextPatcher {
	/*
	 * Classe con il metodo statico Correggi(string) che modifica il testo in
	 * ingresso alla testa parlante per evitare problemi di parsing con Mbrola.
	 */

	public static String Correggi(String testo) {

		String res = testo.replaceAll(" ha", " a");
		res = res.replaceAll("ha ", "a ");
		res = res.replaceAll(" Ha", " a");
		res = res.replaceAll("Ha ", "a ");
		// aggiunge un silenzio iniziale
		res = ",, ".concat(res);
		return res;

	}
}
