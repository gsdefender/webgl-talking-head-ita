function CohenMassaro(fon, durate) {
	var centri;
	var cumsum;
	var theta;
	var alpha;
	var fonemi;


var fillArray = function(array, value) {
	
		  var arr = new Array();
		  var i = array.length;
		  
		  while (i--) {
		    arr[i] = value;
		  }
		  return arr;		
}

var calcAlpha = function() {
		// Questo metodo va modificato in base alle esigenze implementative
		// In questo lavoro tutti i pesi alpha verranno posti pari ad 1.

		alpha = fillArray(alpha, 1);

	}

var calcTheta = function() {
		/*
		 * Questo metodo pu� essere modificato in base alle esigenze
		 * implementative Se due vocali sono adiacenti si assegnano delle theta
		 * maggiori per separarle in maniera pi� distinta evitando di
		 * coarticolarle
		 */

		var ln2 = Math.log(2);
		var ln5 = Math.log(5);
		var ln3 = Math.log(3);

		var regexp = "[aeiouEOjwI{QVU@]|a1|e1|i1|o1|u1|E1|O1|j1|i:|eI|aI|oI|u:|@U|aU|3:|A:|O:|I@|e@|U@";
		for (var i = 0; i < theta.length; i++) {

			if (fonemi[i].match(regexp) != null)
				theta[i] = (ln2) / Math.abs(cumsum[i] - centri[i]);
			else
				theta[i] = (ln3) / Math.abs(cumsum[i] - centri[i]);

			// Se due vocali sono adiacenti separale nettamente
			if ((i > 1) && fonemi[i].match(regexp)!=null
					&& fonemi[i - 1].match(regexp)!=null) {
				theta[i] = (ln5) / Math.abs(cumsum[i] - centri[i]);
				theta[i - 1] = (ln5) / Math.abs(cumsum[i - 1] - centri[i - 1]);
				theta[i - 2] = (ln5) / Math.abs(cumsum[i - 1] - centri[i - 1]);
			}

		}
	}

this.getCentri = function() {
		return centri;
	}

this.getTheta = function() {
		return theta;

	}

this.getAlpha = function() {
		return alpha;
	}

this.getWeights = function(t) {

		// Effettua i calcoli relativi al tempo t, secondo gli esponenziali di
		// coh-mas
		// Stabilendo in quale zona dell'array centri ci troviamo, sulla base di
		// cumsum

		pesi = new Array();
		pesi.length = centri.length;
		// Indice del visema corrente al tempo t
		var actpos = getPosition(t);
		pesi = fillArray(pesi, 0);

		// Calcola i pesi del visema corrente e dei visemi adiacenti (3 visemi
		// in totale)
		if (actpos == centri.length - 1) {
			pesi[actpos] = alpha[actpos]
					* Math.exp(-Math.abs(t - centri[actpos]) * theta[actpos]);
			pesi[actpos - 1] = alpha[actpos]
					* Math.exp(-Math.abs(t - centri[actpos - 1])
							* theta[actpos - 1]);
		} else if (actpos == 0) {
			pesi[actpos] = alpha[actpos]
					* Math.exp(-Math.abs(t - centri[actpos]) * theta[actpos]);
			pesi[actpos + 1] = alpha[actpos]
					* Math.exp(-Math.abs(t - centri[actpos + 1])
							* theta[actpos + 1]);

		} else if (actpos > 0) {
			pesi[actpos] = alpha[actpos]
					* Math.exp(-Math.abs(t - centri[actpos]) * theta[actpos]);
			pesi[actpos - 1] = alpha[actpos]
					* Math.exp(-Math.abs(t - centri[actpos - 1])
							* theta[actpos - 1]);
			pesi[actpos + 1] = alpha[actpos]
					* Math.exp(-Math.abs(t - centri[actpos + 1])
							* theta[actpos + 1]);
		}

		// Normalizza tra 0 ed 1 i pesi
		return normalize(pesi);
	}

var getPosition = function(t) {

		// Calcola a quale visema/fonema appartenga l'istante temporale t
		var index = cumsum.length - 1;
		for (var i = 0; i < cumsum.length - 1; i++) {
			if (t < cumsum[i + 1]) {
				return index = i;
			}
		}
		return index;

	}

var normalize = function(arr) {
		// Normalizza la somma dei pesi di arr
		temp = new Array();
		temp.length = array.length;
		var sum = 0;

		for (var i = 0; i < arr.length; i++) {
			sum = sum + arr[i];
		}
		for (var i = 0; i < arr.length; i++) {
			temp[i] = arr[i] / sum;
		}

		return temp;

	}



	centri = new Array();
	cumsum = new Array();
	theta = new Array();
	alpha = new Array();

	centri.length = durate.length;
	cumsum.length = durate.length;
	theta.length = durate.length;
	alpha.length = durate.length;

	fonemi = fon;

	centri[0] = durate[0] / 2;

	for (var i = 1; i < durate.length; i++) {
			// Somme cumulative delle durate dei fonemi
			cumsum[i] = cumsum[i - 1] + durate[i - 1];

			// centri temporali dei fonemi
			centri[i] = cumsum[i] + (durate[i] / 2);

	}

		// calcola alpha e theta ed assegnali ai vettori alpha e theta
	calcTheta();
	calcAlpha();


}
