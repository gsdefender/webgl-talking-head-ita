/* FUNZIONI DI SUPPORTO */
function apriFinestraControlli() {
  var left = 100;
  var width = 300;
  var height = 500;
  var top = (screen.height/2)-(height/2);
  return window.open('controlli.html', 'finestraControlli', "menubar=no,toolbar=no,status=no,width="+width+",height="+height+",toolbar=no,left="+left+",top="+top);
}

function creaConnessioneXMLHTTP_POST(url,lunghezzaParametri)
{
	xmlhttp="undefined";
	
	if(url!="" || lunghezzaParametri>=0)
    {
		
		if(window.ActiveXObject)
		{// code for IE6, IE5
			xmlhttp=new ActiveXObject("Microsoft.XMLHTTP"); //roba specifica di IE 
		}
		else // se esiste questo oggetto
		{// code for IE7+, Firefox, Chrome, Opera, Safari
			xmlhttp=new XMLHttpRequest();
		}
    
		xmlhttp.open("POST",url,true); //apriamo l'oggetto (true per richiesta asincrona)
		xmlhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");  //specifichiamo che la richiesta contiene parametri codificati
		xmlhttp.setRequestHeader("Content-length", lunghezzaParametri); //lunghezza dei parametri da codificare
		xmlhttp.setRequestHeader("Connection", "close");
  } 
    
    return xmlhttp;
}

function inizializzazione()
{
	controllaEmozione();
	specificaEmozioneInterattiva(false);
	document.moduloClientWebGl.frase.focus();
}

function controllaEmozione()
{
	var handleEmozione = document.getElementById("emozione");
	var handlePercentuale = document.getElementById("percentuale");
	var handleLabelPercentuale = document.getElementById("labelPercentuale");
	var handleSegnoPercentuale = document.getElementById("segnoPercentuale");
	var emozione=handleEmozione.options[handleEmozione.selectedIndex].value;

	if ( emozione == "neutral" )
	{
		handleLabelPercentuale.style.visibility = "hidden";
		handleSegnoPercentuale.style.visibility = "hidden";
		handlePercentuale.style.visibility = "hidden";
	}
	else
	{ 
		handleLabelPercentuale.style.visibility = "visible";
		handleSegnoPercentuale.style.visibility = "visible";
		handlePercentuale.style.visibility = "visible";
	}
		
}
/* FUNZIONI DI CONVALIDA E INVIO DEL MODULO INPUT */

function validaParametri(handleFrase, handleVelocita)
{
	frase = handleFrase.value;
	velocita = handleVelocita.value;
	
	if (frase == "")
	{
		alert("Inserire almeno un carattere");
		handleFrase.focus();
		return false;
	}
	
	if (frase == "")
	{
		alert("Inserire almeno un carattere");
		handleFrase.focus();
		return false;
	}
	
	if (frase.match(/[\|%]/))  // NON FUNZIONA
	{
		alert("I caratteri \, | e % non sono ammessi");
		handleFrase.focus();
		return false();
	}
	
	if (isNaN(velocita) || velocita <= 0)
	{
		alert("La velocita' e' una quantita' intera positiva non nulla");
		handleVelocita.value="120";
		handleVelocita.focus();
		return false;
	}
	
	return true;
}

function specificaEmozioneInterattiva(booleano)
{
	var handleEmozione = document.getElementById("emozione");
	var handlePercentuale = document.getElementById("percentuale");
	var handleCheck = document.getElementById("emozioneInterattiva");

	handleCheck.checked = booleano;
	handleEmozione.disabled = !booleano;
	handlePercentuale.disabled = !booleano;
	controllaEmozione();
}

function inviaRichiesta()
{	
	if (validaParametri(document.moduloClientWebGl.frase,document.moduloClientWebGl.velocita) == true)
	{
		var frase=document.moduloClientWebGl.frase.value;
		var velocita=document.moduloClientWebGl.velocita.value;
		var handleEmozione = document.getElementById("emozione");
		var handlePercentuale = document.getElementById("percentuale");
		var handleEmozioneInterattiva = document.getElementById("emozioneInterattiva");
		var emozione=handleEmozione.options[handleEmozione.selectedIndex].value;
		var percentuale=handlePercentuale.value;

		var richiestaEmozioneInterattiva = handleEmozioneInterattiva.checked;

		var parametri = "frase="+frase+"&velocita="+velocita+"&emozione="+emozione;

		if (emozione!="neutral")
			parametri=parametri+"&percentuale="+percentuale;

		if (richiestaEmozioneInterattiva==true)
			parametri=parametri+"&emozioneInterattiva=true";
	
		xmlhttp=creaConnessioneXMLHTTP_POST("/ServerWebGl/RiceviRichiestaUtente",parametri.length);
		
		xmlhttp.onreadystatechange=function(){  //passiamo alla funzione riempi_campi la risp della servlet
			if(xmlhttp.readyState==4 && xmlhttp.status==200){
        	
				risposta = xmlhttp.responseText; 
				
				if(risposta == "REQ_OK")
				{
					ottieni_durate();
				}
				else
				{
					alert("Errore durante la connessione al server Text-to-Speech");
					document.moduloClientWebGl.frase.focus();
				}
            
			}  //4 significa richiesta completata - 200 significa richiesta http andata a buon fine
		};
    
		if(parametri!="")
    	{
    		xmlhttp.send(parametri); //manda la richiesta HTTP e attendiamo che si verifichi l'evento onreadystatechange    
    	}
	}
}

function aggiorna_render()
{
		nuovo_modello = new Pupo();		
		
		nuovo_modello.settings =  { 
				url : "/ServerWebGl/Visemi/frase.js", 
				audio_url: "/ServerWebGl/MandaWAV",
				durate_visemi: window.durate_visemi,
				scale:0.23, 
				animating: true};
				
		nuovo_modello.init(nuovo_modello.settings);

	    document.app.removeModel(document.model);
	    document.model = nuovo_modello;
	    document.app.addModel(document.model);
	
 }

/* FUNZIONE PER OTTENERE DURATE VISEMI */

function ottieni_durate()
{
	var MAX_INT=9007199254740992;
	parametri="fileRichiesto=durate&randomizer="+THREE.Math.randInt(-MAX_INT, MAX_INT);
	
	xmlhttp_durate=creaConnessioneXMLHTTP_POST("/ServerWebGl/MandaJSON",parametri.length);
	
	xmlhttp_durate.onreadystatechange=function(){  //passiamo alla funzione riempi_campi la risp della servlet
		if(xmlhttp_durate.readyState==4 && xmlhttp_durate.status==200){
    	
			risposta = xmlhttp_durate.responseText; 
			
			window.opener.durate_visemi=JSON.parse(risposta); 
        
			window.opener.aggiorna_render(); 

		}  //4 significa richiesta completata - 200 significa richiesta http andata a buon fine
	};

	if(parametri!="")
	{
		xmlhttp.send(parametri); //manda la richiesta HTTP e attendiamo che si verifichi l'evento onreadystatechange    
	}

}

