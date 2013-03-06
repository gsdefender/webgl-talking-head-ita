// Constructor
PupoApp = function()
{
	Sim.App.call(this);
};

// Subclass Sim.App
PupoApp.prototype = new Sim.App();

// Our custom initializer
PupoApp.prototype.init = function(param)
{
	// Call superclass init code to set up scene, renderer, default camera
	Sim.App.prototype.init.call(this, param);
	
	// Sostituiamo la telecamera di default
	
	/*PROBLEMA CON LA CAMERA PROSPETTICA: 
	 * Utilizzavamo di default la PerspectiveCamera e il fattore di scale (document.model.settings.scale) impostato a 0.05
	 * questo dava il problema della "visuale deformata"  cioè del DIstance Ratio impostato troppo basso.
	 * Per risolvere questo problema, si può utilizzare la OrthographicCamera, impostando il fattore scale corretto cioè 0.25.
	 * 
	 * PROBLEMA METTENDO IL FATTORE DI SCALE A 0.25: 
	 * Chissà per quale bug o problema che ci sfugge utilizzando qualsiasi  tipo di camera
	 * si risolve il problema finchè l'utente non invia il testo: appena invia il testo improvvisamente l'animazione 
	 * parte con la testa zoommata e riproponendo inoltre il problema del distance ratio.
	 * 
	 * PROBLEMA CON LA CAMERA ORTOGRAPHICA: risolve i problemi precedenti ma la testa visualizzata è un pò troppo lontana
	 * dalla visuale della canvas...Non c'è stato modo di avvicinarla
	 * 
	 * SOLUZIONE: Abbiamo utilizzato la CombinedCamera che permette la definizione di entrambe le camere precedenti, impostando dei valori
	 * condivisi da entrambe le camera e permette di passare da una camera all'altra tramite la funzione toPerspesctive() o toOrthographic().
	 * Questo tipo di camera è risultato utile in quanto possiede una funzione setZoom(), per cui uso la camera Ortografica e faccio lo zoom
	 * risolvendo anche il problema della distanza
	 * NB: una volta che quella Prespective non viene utilizzata alleggerisco il codice evitando di definirla
	 * senza avere alcun problema con la CombinedCamera
	 * 
	 * 
	 */
	
	
	this.scene.remove(camera); //leviamo la camera di default di THREE.js che è di tipo Prospettico
	//camera = new THREE.OrthographicCamera( window.innerWidth / - 2, window.innerWidth / 2, window.innerHeight / 2, window.innerHeight / - 2, - 5000, 10000 );
	  

	
	/*camera = new THREE.PerspectiveCamera(
    	    20,         // Field of view
    	    this.container.offsetWidth / this.container.offsetHeight,  // Aspect ratio
    	    .1,         // Near
    	    100000000       // Far
    	    
    	);*/
	
	camera=new THREE.CombinedCamera(window.innerWidth, window.innerHeight, 1, 70, 1000, -1000, 1000, 1000);
	
	//width, height, fov, near, far, orthonear, orthofar 
	camera.toOrthographic(); //dico di mettere quella ortografica altrimenta usa quella di default che è di tipo Prospettiva
	camera.setZoom(5,false);
	camera.position.set( -2, -5, 50);
	
	this.camera = camera;
	this.scene.add(camera);
	
    // Create a headlight to show off the model
	this.headlight = new THREE.DirectionalLight( 0xffffff, 1);
	this.headlight.position.set(0, 0, 1);
	this.scene.add(this.headlight);	
	
	var amb = new THREE.AmbientLight( 0xffffff );
	this.scene.add(amb);
	
	this.createCameraControls();
};

PupoApp.prototype.addModel = function(model)
{
    this.addObject(model);    
};

PupoApp.prototype.removeModel = function(model)
{	
    this.removeObject(model);    
};


PupoApp.prototype.createCameraControls = function()
{
// PORT DA THREE.JS R46 A R55: TrackballControls Ã¨ stato reimplementato esternamente (script incluso in index.html)
	var controls = new THREE.TrackballControls( this.camera, this.renderer.domElement );
	var radius = PupoApp.CAMERA_RADIUS;
	
	controls.rotateSpeed = PupoApp.ROTATE_SPEED;
	controls.zoomSpeed = PupoApp.ZOOM_SPEED;
	controls.panSpeed = PupoApp.PAN_SPEED;
	controls.dynamicDampingFactor = PupoApp.DAMPING_FACTOR;
	controls.noZoom = false;
	controls.noPan = false;
	controls.staticMoving = false;

	controls.minDistance = radius * PupoApp.MIN_DISTANCE_FACTOR;
	controls.maxDistance = radius * PupoApp.MAX_DISTANCE_FACTOR;

	this.controls = controls; 
};

PupoApp.prototype.update = function()
{
	// Update the camera controls
	if (this.controls)
	{
		this.controls.update();
	}
	
	// Update the headlight to point at the model
	var normcamerapos = this.camera.position.clone().normalize();
	this.headlight.position.copy(normcamerapos);

	Sim.App.prototype.update.call(this);
};

PupoApp.CAMERA_RADIUS = 5;
PupoApp.MIN_DISTANCE_FACTOR = 1.1;
PupoApp.MAX_DISTANCE_FACTOR = 10;
PupoApp.ROTATE_SPEED = 1.0;
PupoApp.ZOOM_SPEED = 3;
PupoApp.PAN_SPEED = 0.2;
PupoApp.DAMPING_FACTOR = 0.3;

