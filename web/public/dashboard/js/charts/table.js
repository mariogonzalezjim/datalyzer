

var initialization=0;

var myRecords = [];
var indexTable=0;
//var socket;

jQuery(document).ready(function($){

	$(document).ready(function () {

		//socket = io();        


		var params= getParamsURL();
		console.log(params)
		$("#name-log").html("Table '" +params["name"] + "'")
        

		for(var i=0; i< params["socketId"].split(',').length-1 ; i++){
            
			createDataSocketListenerTable(socket,params["socketId"].split(',')[i] );
		}

		
		window.onbeforeunload = function () {
			closeAPP();
		};

	});
});


function createTable(myRecords){
	var dynatable = $('#my-final-table').bind('dynatable:preinit', function(e, dynatable) {
		dynatable.utility.textTransform.columnNameStyle = function(text) {
			return text
			;
		};
	}).dynatable({
		table: {
			defaultColumnIdStyle: 'columnNameStyle'
		},
		dataset: {
			records: myRecords
		}, 
		features: {
			search: false
		}
	}).data('dynatable');
}

function setIntervalTable(){ 
	setInterval(function() {


		var test = JSON.parse(JSON.stringify(myRecords));
		var dynatable = $('#my-final-table').dynatable({
			dataset: {
				records: test
			}
		}).data('dynatable');
		dynatable.settings.dataset.originalRecords = test;
		dynatable.process();



	}, 3000);   
}



function createDataSocketListenerTable(socket, name){

	socket.on(name, function(data){
		
		
		if(initialization==0){
			//build model and table schema
			var thead= document.getElementById("table-thead");
			var json =  JSON.parse(data.split('$#$')[0]);
			var html="";

			console.log(json);

			for (var key in json){
				html += "<th data-dynatable-no-sort >" + key +"</th>"; 
			}


			thead.innerHTML = html;
			//inicializar tabla
			createTable(myRecords);

			initialization=1;
			myRecords.push(json);
			setIntervalTable();

		}
		else{ //add data to table

			for(var i=0; i<(data.split('$#$').length-1) ; i++){
				var json = JSON.parse(data.split('$#$')[i]);
				json['dynatable-original-index']=indexTable;
				indexTable++;
				myRecords.push(json);
			}

			       
		}
		
	});
}