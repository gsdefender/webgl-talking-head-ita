//Custom JSON model class
Pupo = function()
{
	Sim.Object.call(this);
}

Pupo.prototype = new Sim.Object();

Pupo.prototype.init = function(param)
{
	var group = new THREE.Object3D;

	var that = this;

	var MAX_INT=9007199254740992; // anti-cache
	var url = param.url+'?'+THREE.Math.randInt(-MAX_INT, MAX_INT) || "";
	if (!url)
		return;

	var scale = param.scale || 1;
	
	this.scale = new THREE.Vector3(scale, scale, scale);
	
	
	// per l'audio
	this.audio_url = param.audio_url;	
	this.durate_visemi = param.durate_visemi;
	
	var loader = new THREE.JSONLoader();

// PORT DA THREE.JS R46 A R55: GEOMETRY E MATERIAL ORA SONO DISTINTI

	//loader.load( url, function( data ) { 
//		that.handleLoaded(data); } );


	loader.load( url, function( geometry, materials ) { 
		that.handleLoaded(geometry, materials); } );
	
    // Tell the framework about our object
    this.setObject3D(group);
    
    
    // per l'animazione
    this.animating = false;
    this.clock = new THREE.Clock();
	this.animating = param.animating;
	
}


Pupo.prototype.handleLoaded = function(loadedGeometry, loadedMaterials)
{
	if (loadedGeometry instanceof THREE.Geometry)
	{
		//PORT A THREE.JS R55: var geometry = data;		
		var geometry = loadedGeometry;
		var materials = loadedMaterials;

		// Just in case model doesn't have normals
		geometry.computeVertexNormals();
	
		// for preparing animation
		// senza questo for non vengono attivati i morphTargetInfluences
		
// PORT A THREE.JS R55:	
/*	 for (var i = 0; i < geometry.materials.length; i++)
  		geometry.materials[i].morphTargets = true; */

		 for (var i = 0; i < materials.length; i++)
  			materials[i].morphTargets = true;  				

		var material = new THREE.MeshFaceMaterial(materials);

		var mesh = new THREE.MorphAnimMesh( geometry, material  ); // Mesh nel libro
		
		// SOMMA DELLE DURATE EFFETTUATA COL MAP REDUCE
		if (this.durate_visemi.length > 0) 
		{
			mesh.duration = this.durate_visemi.reduce(function(previousValue, currentValue, index, array){
			  return previousValue + currentValue;
		});	
		} 
		

		
		// Funzione di animazione personalizzata, scritta in modo tale da aggiungere la
		// possibilit� di impedire il loop
		this.indice_visema_corrente = 0;
		
		mesh.updateAnimation = function ( delta )
		{
			var frameTime = this.duration / ( this.geometry.morphTargets.length - 1 ); // Aggiornamento non basato sulle durate dei visemi (updateAnimation � chiamato di continuo, anche quando non riproduciamo)
			
			if ( this.durate_visemi )
				 frameTime = this.durate_visemi[this.indice_visema_corrente];  
			
			this.time += delta;

			// Alla fine dell'animazione fermati
			
				    if ( this.time > this.duration) {
			    	this.time = this.duration; 
					
			    	// WORKAROUND: in teoria basta impostare this.time a this.duration per
			    	// interrompere l'animazione, ma con alcune vocali lunghe
			    	// ci sono problemi a riportare la mesh alla configurazione originaria:
			    	
			    	// Esempio: la parola "casa" pronunciata "c aaa saaa " produce un artefatto
			    	// alla fine della riproduzione
			    	
			    	this.morphTargetInfluences[this.morphTargetInfluences.length - 3]=0;	
			    	
			    	// tolgo dolcemente l'emozione	
			    	var decremento=0.020;
			    	this.morphTargetInfluences[this.morphTargetInfluences.length - 1]=Math.max(this.morphTargetInfluences[this.morphTargetInfluences.length - 1]-decremento,0);		
			    	return;
			    }
			    if ( this.time < 0 ) this.time = 0; 

			// LOOP DELL'ANIMAZIONE PER TEST: 
			//this.time = this.time % this.duration;
			

		var keyframe = THREE.Math.clamp( Math.floor( this.time / frameTime ), 0, this.geometry.morphTargets.length - 1 );

			if ( keyframe != this.currentKeyframe ) { // attendi la transizione di frame brusca appena l'if � soddisfatto

				this.morphTargetInfluences[ this.lastKeyframe ] = 0;
				this.morphTargetInfluences[ this.currentKeyframe ] = 1;

				this.morphTargetInfluences[ keyframe ] = 0;

				this.lastKeyframe = this.currentKeyframe;
				this.currentKeyframe = keyframe;
				
				if (this.durate_visemi && this.indice_visema_corrente != this.durate_visemi.length)
					this.indice_visema_corrente++;
			}
	    
			// emozione attiva
			this.morphTargetInfluences[this.morphTargetInfluences.length - 1]=1;

			var mix = ( this.time % frameTime ) / frameTime;

				this.morphTargetInfluences[ this.currentKeyframe ] = mix;
				this.morphTargetInfluences[ this.lastKeyframe ] = 1 - mix;
		};
		
		this.faceMesh = mesh;
		
		//caricamento file audio
				
		this.audio = new Audio();
		this.audio.setAttribute('src', this.audio_url);
		this.audio.preload = 'auto';
		this.audio.load(); 
			    
		mesh.scale.copy(this.scale);
		this.object3D.add( mesh );	
	}

}

Pupo.prototype.update = function(data)
{
	if(this.animating && this.faceMesh)
	{	
		
		var delta = this.clock.getDelta();
		
		  if (this.audio.paused) {
			this.audio.play();  
		}

		this.faceMesh.updateAnimation(1000*delta);

	}
	
	Sim.Object.prototype.update.call(this);
	
}
