

var socket; 


jQuery(document).ready(function($){

	$(document).ready(function () {


		socket = io();        




	});
});




function openNav() {
	document.getElementById("myNav").style.height = "100%";
}

function closeNav() {
	console.log("ESTOY CERRANDO");
	document.getElementById("myNav").style.height = "0%";
}

function pauseAPP(button){
	if(systemStatus==0){
		showPopUpError('System Status is Off');
		return;
	}
	var title= document.getElementById("infoPauseAPP");
	console.log(title.innerHTML);
	if(button.className=="pauseAPP"){
		// pause app
		// send message through socket
		socket.emit("PAUSEAPP", appid);
		console.log("he pinchado en pausar");
		button.className="resume";
		button.innerHTML='<i class="ti-control-play" style="color: #ba1616;"></i>';
		title.innerHTML='<p>Press button to </p>Resume';
		// send pause message to iframes (widgets)
		var iframes = document.getElementsByClassName("iframe-dashboard");
		for(var ii=0; ii<iframes.length; ii++){
			iframes[ii].contentWindow.postMessage("Pause", '*');	
		}


	}else{
		//pause app
		// send message through socket
		socket.emit("RESUMEAPP", appid);
		console.log("he pinchado en renaudar");
		button.className="pauseAPP";
		button.innerHTML='<i class="ti-control-pause" style="color: #41B883;"></i>';
		title.innerHTML='<p>Press button to </p>Pause';
		// send resume message to iframes (widgets)
		var iframes = document.getElementsByClassName("iframe-dashboard");
		for(var ii=0; ii<iframes.length; ii++){
			iframes[ii].contentWindow.postMessage("Resume", '*');	
		}

	}

}

function stopAPP(button){
	if(systemStatus==1){
		console.log("Estoy cerrando");
		socket.emit("closeAPP" , appid);
		// send stop message to iframes (widgets)
		var iframes = document.getElementsByClassName("iframe-dashboard");
		for(var ii=0; ii<iframes.length; ii++){
			iframes[ii].contentWindow.postMessage("Stop", '*');	
		}

	}else{
		showPopUpError('System Status is Off');
	}

}

function closeAPP(){
	console.log("Estoy cerrando");
	//testing
	socket.emit("closeAPP" , appid);

	//close tab
	window.top.location.reload(true);
	window.top.close();
}

function getFirstKeyJson(jsonObj){
	var firstProp;
	var firstKey;
	for(var key in jsonObj) {
		if(jsonObj.hasOwnProperty(key)) {
			firstProp = jsonObj[key];
			firstKey=key;
			break;
		}
	}
	return firstKey;
}

function getFirstValueJson(jsonObj){
	var firstProp;
	var firstKey;
	for(var key in jsonObj) {
		if(jsonObj.hasOwnProperty(key)) {
			firstProp = jsonObj[key];
			firstKey=key;
			break;
		}
	}
	return firstProp;
}

function showPopUpError(msg){

	var notify = $.notify({
		title: "Error:",
		message: msg

	},{
		//settings
		type: 'error',
		delay: 2500,
		placement: {
			from: 'top',
			align: 'center'
		},

	});
}

function byteCount(string) {
	return encodeURI(string).split(/%..|./).length - 1;
}


function getParamsURL(){
	var params = [];

	if (location.search) {
		var parts = location.search.substring(1).split('&');

		for (var i = 0; i < parts.length; i++) {
			var nv = parts[i].split('=');
			if (!nv[0]) continue;
			params[nv[0]] = nv[1] || true;
		}
	}

	return params;
}
