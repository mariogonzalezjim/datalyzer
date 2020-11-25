
// global variable to store all shapes we want to add on the editor
var array_shapes=[];


var shape_DSLPrimitive = class DSLPrimitive extends draw2d.SetFigure {

	constructor (attr, setter, getter) {

		super( $.extend({stroke:0, bgColor:null, width:100,height:100},attr.name), setter, getter);

		var port;

		if(attr.inputPort=="true"){
			port = this.addPort(new DecoratedInputPort(), new draw2d.layout.locator.XYRelPortLocator(-2.9, 45));
			port.setConnectionDirection(3);
			port.setBackgroundColor("#37B1DE");
			port.setName(attr.inputPortName);
			port.setMaxFanOut(20);
		}
		if (attr.outputPort=="true"){
			port = this.createPort("hybrid", new draw2d.layout.locator.XYRelPortLocator(90, 50));
			port.setConnectionDirection(1);
			port.setBackgroundColor("#37B1DE");
			port.setName(attr.outputPortName);
			port.setMaxFanOut(20);
		}




		this.persistPorts=false;
		this.NAME=attr.name;

		// your special code here
		this.attr({resizeable:false});
		this.attr({selectable:true});
		this.installEditPolicy(new draw2d.policy.canvas.SingleSelectionPolicy());

	}

	createShapeElement ()
	{
		var shape = super.createShapeElement();
		this.originalWidth = 100;
		this.originalHeight= 100;
		return shape; 
	}


	createSet (url)
	{

		this.canvas.paper.setStart();

		var json = arrayShapesObjets[this.NAME];
		this.shape = this.canvas.paper.image(json.imageUrl,0,0,100, 100);
		//debugger
		this.id= globalId;
		json.id=globalId;
		json.nameId= globalId;
		arrayObjectsModel.push(JSON.parse(JSON.stringify(json)));
		this.NAME += "$" + globalId;
		globalId +=1;

		return this.canvas.paper.setFinish();
	}


	addPort (port, locator)
	{

		super.addPort(port, locator)
		return port;
	}

	getParameterSettings ()
	{
		return [
			{
				name:this.NAME,
				label:this.NAME,
				property:{
					type: "blocid"
				}

			}];
	}

	onDragStart (x, y, shiftKey, ctrlKey) {

		super.onDragStart(x, y, shiftKey, ctrlKey);
		super.select(null);
		this.showConfigutarionView(this.generateHTMLConfiguration())
		$("#editDelete").removeClass("disabled");
	} 

	showConfigutarionView(html){
		$( ".config-view-content" ).html( html );
	}

	generateHTMLConfiguration(){
		return "hola"
	}

	onStart()
	{
		// nothing
	}

	calculate (){
		// nothing
	}

	onStop(){
		// nothing
	}
}


/*******  DATASOURCES PRIMITIVES  *******/
var shape_DataSource =  class DataSource extends shape_DSLPrimitive {


	constructor (attr, setter, getter) {
		
		attr= {name: attr, inputPort: "false", inputPortName: "", outputPort: "true", outputPortName: "PortDataOut"}
		super(attr, setter, getter)

	}

	generateHTMLConfiguration(){

		var json = getJsonModelFromArray(this.id, arrayObjectsModel);
		var flagModel=false;

		var html = "<h3>Description</h3>" + 
			"<p>"+ json.description +"</p>"

		html += "<h3>Name<font color='red' size='5'>*</font></h3>" + 
			"<p><input id='inputIdentificator"+ json.id+ "' placeholder='Identificator' style='opacity: 1; background: rgb(255, 255, 255);width: 70%;'" 
		if(json.nameId!=null)
			html += " value='" + json.nameId + "' " 

		html += "></p>"


		if(json.params.length!=0 || json.rate!=null)
			html +='<p></p><p><h3>Parameters</h3></p>';

		var index=0;

		for (index = 0; index < json.params.length; ++index) {
			if(json.params[index].type=="String" || json.params[index].type=="string"){
				html += '<p>'+ json.params[index].description +'</p>';
				if(json.params[index].value==""){
					html += '<p><input id="input'+ json.params[index].name +json.id+'" placeholder="Ex: '+ json.params[index].example +'" style="opacity: 1; background: rgb(255, 255, 255);' +
						'width: 70%;"></p>';
				}else{
					html += '<p><input id="input'+ json.params[index].name + json.id+'" value="'+ json.params[index].value +'" placeholder="Ex: '+ json.params[index].example +'" style="opacity: 1; background: rgb(255, 255, 255);' +
						'width: 70%;"></p>';
				}
			}
		}
		if(json.rate!=null){
			html += '<p>Data rate (ms)</p>';
			if(json.rate==""){
				html += '<p><input id="inputRate'+  +json.id+'" placeholder="Ex: 1000" style="opacity: 1; background: rgb(255, 255, 255);' +
					'width: 70%;"></p>';
			}else{
				html += '<p><input id="inputRate'+  +json.id+'" value="'+ json.rate +'" placeholder="Ex: 1000" style="opacity: 1; background: rgb(255, 255, 255);' +
					'width: 70%;"></p>';
			}
		}

		html += '<p></p><p><h3>Fields</h3></p>';
		html += '<div class="datagrid"><table style="width: 43%; style="font-family:arial;"" cellpadding="10">';
		for (index = 0; index < json.values.length; ++index) {
			if(json.values[index].check=="true"){
				html += '<tr><td>'+json.values[index].name +'</td><td><input id="check'+json.values[index].name + json.id+'" name="'+json.values[index].name +'" type="checkbox" checked><br></td></tr>';
			}else{
				html += '<tr><td>'+json.values[index].name +'</td><td><input id="check'+json.values[index].name +json.id+'" name="'+json.values[index].name +'" type="checkbox"><br></td></tr>';
			}

		}
		html += '</tbody></table></div>';

		html += '<button onclick="shape_DataSource.saveData('+  this.id + ')" type="button" class="btn btn-danger btn-lg center">Save</button>'


		// debugger
		return  html;

	}


	static saveData(id){

		//find json by id
		var json = getJsonModelFromArray(id, arrayObjectsModel);
		//debugger
		var index;
		//SAVE PARAMS
		for (index = 0; index < json.params.length; ++index) {
			var input = document.getElementById('input' + json.params[index].name+json.id);
			if(input!=null){
				json.params[index].value=input.value;
			}
		}
		//SAVE VALUES
		for (index = 0; index < json.values.length; ++index) {
			//json.values[index]["check"]="false";
			var check= document.getElementById('check' + json.values[index].name+json.id);
			if(check!=null){
				json.values[index].check=check.checked.toString();
			}
		}
		// SAVE ID
		json.nameId= document.getElementById('inputIdentificator' + json.id).value.toUpperCase();
		// SAVE DATA RATE
		var input = document.getElementById('inputRate' + json.id);
		json.rate = input.value;


	}

	static changeImageChart(select){
		var name = $(select).find(":selected").val()

		var url  = $('option:selected', select).attr('imageurl');;
		//debugger

		$(".img-thumbnail-view").attr("src",url);

	}


	static getType() {
		return "DataSource";
	}

	static getModel(){
		return "shape_DataSource";
	}


	static getName(){
		return "DataSource";
	}



}


/*******  DATAPROCESSOR PRIMITIVES  *******/
var shape_Pipeline =  class Pipeline extends shape_DSLPrimitive {


	constructor (attr, setter, getter) {
		attr= {name: attr, inputPort: "true", inputPortName: "PortPipelineIn", outputPort: "true", outputPortName: "PortPipelineOut"}
		super(attr, setter, getter)

	}

	generateHTMLConfiguration(){

		var json = getJsonModelFromArray(this.id, arrayObjectsModel);

		var flagModel=false;

		var html = "<h3>Description</h3>" + 
			"<p>The pipeline represents a data flow where you can connect one or more data sources. This object generates a new data model based on the fields you have selected.</p>"

		html += "<h3>Name<font color='red' size='5'>*</font></h3>" + 
			"<p><input id='inputIdentificator"+ json.id+ "' placeholder='Identificator' style='opacity: 1; background: rgb(255, 255, 255);width: 70%;'" 
		if(json.nameId!=null)
			html += " value='" + json.nameId + "' " 

		html += "></p>"

		if(json.connections.length==0){
			html += '<p><strong>Invalid:</strong> This object has any data input connection. </p>';
		}else{

			html += '<div id="table" width="80%">  </div>';
			html += ' <table style="width: 100%;text-align: center;table-layout: fixed;" border="2" cellspacing="10"> <tbody> <tr>'; 

			//check connections
			var index=0;
			for(index= 0; index < json.connections.length ; index++){
				var model = getJsonModelFromArray(json.connections[index].id, arrayObjectsModel);

				var index2=0;
				for(index2=0; index2 < model.values.length ; index2++){
					if(model.values[index2].check=="true"){
						html += '<td style="padding-bottom: 1em;padding-top: 1em;"><b>'+model.nameId +':</b></br>' + model.values[index2].name + '</td> ' ;
						flagModel=true;
					}
				}
			}
			html += '</tr> </tbody> </table>';
			if(flagModel==false){
				html += '<p><strong>Invalid:</strong> This object has any input field connected. </p>';
			}
		}

		html += '<button onclick="shape_Pipeline.saveData('+  this.id + ')" type="button" class="btn btn-danger btn-lg center">Save</button>'


		return  html;

	}


	static saveData(id){

		//find json by id
		var json = getJsonModelFromArray(id, arrayObjectsModel);
		// SAVE ID
		json.nameId= document.getElementById('inputIdentificator' + json.id).value.toUpperCase();



	}

	static changeImageChart(select){
		var name = $(select).find(":selected").val()

		var url  = $('option:selected', select).attr('imageurl');;
		//debugger

		$(".img-thumbnail-view").attr("src",url);

	}


	static getType() {
		return "PipelineSimple";
	}

	static getModel(){
		return "shape_Pipeline";
	}


	static getName(){
		return "Pipeline Merge Data";
	}

	static getImageUrl(){
		return "/resources/editor/assets/images/logos/monitor2.png";
	}



}
array_shapes.push(shape_Pipeline)

var shape_PipelineBasic =  class PipelineBasic extends shape_DSLPrimitive {


	constructor (attr, setter, getter) {
		attr= {name: attr, inputPort: "true", inputPortName: "PortPipelineIn", outputPort: "true", outputPortName: "PortPipelineOut"}
		super(attr, setter, getter)

	}

	generateHTMLConfiguration(){

		var json = getJsonModelFromArray(this.id, arrayObjectsModel);

		var flagModel=false;

		var html = "<h3>Description</h3>" + 
			"<p>The pipeline represents a data flow where you can connect one or more data sources.</p>"

		html += "<h3>Name<font color='red' size='5'>*</font></h3>" + 
			"<p><input id='inputIdentificator"+ json.id+ "' placeholder='Identificator' style='opacity: 1; background: rgb(255, 255, 255);width: 70%;'" 
		if(json.nameId!=null)
			html += " value='" + json.nameId + "' " 

		html += "></p>"

		if(json.connections.length==0){
			html += '<p><strong>Invalid:</strong> This object has any data input connection. </p>';
		}else{

			html += '<div id="table" width="80%">  </div>';
			html += ' <table style="width: 100%;text-align: center;table-layout: fixed;" border="2" cellspacing="10"> <tbody> <tr>'; 

			//check connections
			var index=0;
			for(index= 0; index < json.connections.length ; index++){
				if(flagModel==false){
					html += '<td style="padding-bottom: 1em;padding-top: 1em;"><b>Data</td> ' ;
					flagModel=true;
				}
			}
			html += '</tr> </tbody> </table>';
			if(flagModel==false){
				html += '<p><strong>Invalid:</strong> This object has any input field connected. </p>';
			}
		}

		html += '<button onclick="shape_PipelineBasic.saveData('+  this.id + ')" type="button" class="btn btn-danger btn-lg center">Save</button>'


		return  html;

	}


	static saveData(id){

		//find json by id
		var json = getJsonModelFromArray(id, arrayObjectsModel);
		// SAVE ID
		json.nameId= document.getElementById('inputIdentificator' + json.id).value.toUpperCase();



	}

	static changeImageChart(select){
		var name = $(select).find(":selected").val()

		var url  = $('option:selected', select).attr('imageurl');;
		//debugger

		$(".img-thumbnail-view").attr("src",url);

	}


	static getType() {
		return "PipelineBasic";
	}

	static getModel(){
		return "shape_PipelineBasic";
	}


	static getName(){
		return "Basic Pipeline";
	}

	static getImageUrl(){
		return "/resources/editor/assets/images/logos/basic-pipelinev2.png";
	}



}
array_shapes.push(shape_PipelineBasic)


var shape_CustomProcessor =  class CustomProcessor extends shape_DSLPrimitive {


	constructor (attr, setter, getter) {
		attr= {name: attr, inputPort: "true", inputPortName: "PortProcessorIn", outputPort: "true", outputPortName: "PortPipelineOut"}
		super(attr, setter, getter)
		socket = io();
		var thisClass= this
		socket.on('loadCustomProcessor', function(array){

			thisClass.modelsProcessor = array
			shape_CustomProcessor.modelsProcessor = array


		});
		socket.emit("requestCustomProcessor" , ""); 

	}

	generateHTMLConfiguration(){

		var json = getJsonModelFromArray(this.id, arrayObjectsModel);

		var flagModel=false;

		if(json.nameProcessor==null){
			// initialization
			json.nameProcessor = this.modelsProcessor[0].name

		}
		if(json.fieldToFilter==null){
			// initialization
			json.fieldToFilter = ""

		}
		if(json.fieldReductor==null)
			json.fieldReductor= false;
		if(json.parameters==null){
			json.parameters = this.modelsProcessor[0].parameters

		}



		var html = "<h3>Description</h3>" + 
			"<p>With this primitive you can add a custom filter developed in Java. </p>"

		html += "<h3>Name<font color='red' size='5'>*</font></h3>" + 
			"<p><input id='inputIdentificator"+ json.id+ "' placeholder='Identificator' style='opacity: 1; background: rgb(255, 255, 255);width: 70%;'" 
		if(json.nameId!=null)
			html += " value='" + json.nameId + "' " 

		html += "></p>"


		// separador
		html += "<p></p><h3>List of processors</h3>"
		html += "<select name='processorSelect' onchange='shape_CustomProcessor.changeProcessor(this);' id='" + this.id + "' >" 
		for (var i=0; i< this.modelsProcessor.length ; i++){
			html += "<option imageurl='null'  value='"+ this.modelsProcessor[i].name + "'" 
			if(json.nameProcessor==this.modelsProcessor[i].name){
				html += "selected";

			} 
			html += ">" + this.modelsProcessor[i].name + "</option>" 
		} 
		html += "</select><p></p>"
		var model = getJsonModelFromArrayByName(json.nameProcessor, this.modelsProcessor)
		html += "<h3>Processor description</h3>" +    "<p id='descriptionProcessor' >"+ model.description +" </p>"

		html += "<h3>Parameters</h3>" 
		html += "<div id='parameters' >"

		for (var i=0; i< json.parameters.length ; i++){
			html += '<p>'+ json.parameters[i].name +'</p>';
			html += '<p><input id="input'+ json.parameters[i].name +'" placeholder="" style="opacity: 1; background: rgb(255, 255, 255);' +
				'width: 70%;"  value="'+ json.parameters[i].value +'"></p>';
		}
		html += "</div>"
		//debugger

		html += "<h3>Field to filter</h3>"

		if(json.connections.length==0){
			html += '<p><strong>Invalid:</strong> This object has any data input connection. </p>';
		}else{

			html += "<select name='filterField' id='" + this.id + "' >" 

			//check connections
			var index=0;
			for(index= 0; index < json.connections.length ; index++){
				var model = getJsonModelFromArray(json.connections[index].id, arrayObjectsModel);

				var index3=0;
				for(index3= 0; index3 < model.connections.length ; index3++){
					var model2= getJsonModelFromArray(model.connections[index3].id, arrayObjectsModel);
					var index2=0;
					for(index2=0; index2 < model2.values.length ; index2++){
						if(model2.values[index2].check=="true"){
							// html += '<td style="padding-bottom: 1em;padding-top: 1em;"><b>'+model2.nameId +':</b></br>' + model2.values[index2].name + '</td> ' ;
							flagModel=true;

							html += "<option imageurl='null'  value='"+ model2.nameId +': ' +  model2.values[index2].name + "'" 
							debugger
							var name= model2.nameId +': ' + model2.values[index2].name
							if(json.fieldToFilter==name){
								html += "selected";
							}
							html += ">" + model2.nameId +': </b></br>' +  model2.values[index2].name + "</option>" 
						}
					}
				}




			}
			html += "</select><p></p>"
			if(flagModel==false){
				html += '<p><strong>Invalid:</strong> This object has any input field connected. </p>';
			}
		}

		html += "<h3>Field reduction</h3>" 
		html += "<p><input  type='checkbox' id='inputFieldReductor"+ json.id+ "'  style='opacity: 1; background: rgb(255, 255, 255);'" 
		if(json.fieldReductor==true)
			html += " value='' target='spline' checked></p>"
		else
			html += " value='' target='spline'></p>"



		html += '<button onclick="shape_CustomProcessor.saveData('+  this.id + ')" type="button" class="btn btn-danger btn-lg center">Save</button>'


		return  html;

	}


	static saveData(id){

		//find json by id
		var json = getJsonModelFromArray(id, arrayObjectsModel);
		// SAVE ID
		json.nameId= document.getElementById('inputIdentificator' + json.id).value.toUpperCase();
		// SAVE CODE
		// read model selected 
		var name = $('select[name="processorSelect"] option:selected').val()
		var model = getJsonModelFromArrayByName(name, shape_CustomProcessor.modelsProcessor)
		json.nameProcessor= name;
		json.codeProcessor= model.code;
		// real field selected
		name = $('select[name="filterField"] option:selected').val()
		json.fieldToFilter= name;
		json.fieldReductor = $("#inputFieldReductor" + json.id).is(':checked');
		// save parameters
		json.parameters= model.parameters
		for (var i=0; i< model.parameters.length ; i++){
			json.parameters[i].value= $('#input' +model.parameters[i].name  ).val();
			
		}
		



	}

	static changeProcessor(select){

		var name = $(select).find(":selected").val()
		var model = getJsonModelFromArrayByName(name, shape_CustomProcessor.modelsProcessor)
		$("#descriptionProcessor").html(model.description);

		var html = ""
		for (var i=0; i< model.parameters.length ; i++){
			html += '<p>'+ model.parameters[i].name +'</p>';
			html += '<p><input id="input'+ model.parameters[i].name +'" placeholder="" style="opacity: 1; background: rgb(255, 255, 255);' +
				'width: 70%;"></p>';
		}
		$("#parameters").html(html);



		// $(".img-thumbnail-view").attr("src",url);

	}


	static getType() {
		return "CustomProcessor";
	}

	static getModel(){
		return "shape_CustomProcessor";
	}


	static getName(){
		return "Custom Processor";
	}

	static getImageUrl(){
		return "/resources/editor/assets/images/logos/custom-processor-icon2.png";
	}



}
array_shapes.push(shape_CustomProcessor)

/*******  RESULTS PRIMITIVES  *******/

var shape_Charts = class Chart extends shape_DSLPrimitive {


	constructor (attr, setter, getter) {
		attr= {name: attr, inputPort: "true", inputPortName: "PortResultsIn", outputPort: "false", outputPortName: ""}
		super(attr, setter, getter)

		this.charts = shape_Charts.getModelCharts()

		/*
        this.charts=[];
        var chart = {name: "Spline", url: "charts/spline.html"}
        chart["imageUrl"]="https://assets.highcharts.com/images/demo-thumbnails/highcharts/dynamic-update-default.svg";
        this.charts.push(chart)
        chart = {name: "Box", url: "charts/box.html"}
        chart["imageUrl"]="https://assets.highcharts.com/images/demo-thumbnails/highcharts/chart-update-default.svg";

        this.charts.push(chart)
        */

	}

	generateHTMLConfiguration(){

		var json = getJsonModelFromArray(this.id, arrayObjectsModel);
		if(json.configuration==null)
			json.configuration= {};

		var imageurl= this.charts[0].imageUrl
		var html = "<h3>Description</h3>" + 
			"<p>Choose between the different chart models to visualize the data generated by the pipeline.</p>"

		html += "<h3>Name<font color='red' size='5'>*</font></h3>" + 
			"<p><input id='inputIdentificator"+ json.id+ "' placeholder='Identificator' style='opacity: 1; background: rgb(255, 255, 255);width: 70%;'" 
		if(json.nameId!=null)
			html += " value='" + json.nameId + "' " 

		html += "></p>"

		html += "<p></p><h3>Charts</h3>"
		html += "<select name='charts' onchange='shape_Charts.changeImageChart(this);' id='" + this.id + "' >"
		for (var i=0; i<this.charts.length ; i++){
			html += "<option imageurl="+ this.charts[i].imageUrl  +"  value='"+ this.charts[i].name + "'" 
			if(json.chart==this.charts[i].name){
				html += 'selected'
				imageurl = this.charts[i].imageUrl
			}
			html += ">" + this.charts[i].name + "</option>"
		}
		html += "</select>"

		html += "<p></p><h3>Preview</h3>"

		html += '<img class="img-thumbnail-view"  src="'+  imageurl + '" style="">' 

		// interval in seconds
		html += "<p></p><h3 id='titleTimeInterval"+ json.id+ "' >Time interval (seconds)</h3>"
		html += "<p><input id='inputTimeInterval"+ json.id+ "' placeholder='In seconds' style='opacity: 1; background: rgb(255, 255, 255);width: 70%;'" 
		if(json.configuration.timeInterval==null)
			html += " value='' target='spline'></p>"
		else
			html += " value='" + json.configuration.timeInterval   +"' target='spline'></p>"

		//update with interval
		html += "<p></p><h3 id='titleIntervalFixed"+ json.id+ "' >Update with fixed interval</h3>"
		html += "<p><input  type='checkbox' id='inputIntervalFixed"+ json.id+ "'  style='opacity: 1; background: rgb(255, 255, 255);'" 
		if(json.configuration.intervalFixed==true)
			html += " value='' target='spline' checked></p>"
		else
			html += " value='' target='spline' ></p>"
		// print quantity
		html += "<p></p><h3 id='titleFrecuency"+ json.id+ "' >Print Frecuency</h3>"
		html += "<p><input  type='checkbox' id='inputFrecuency"+ json.id+ "'  style='opacity: 1; background: rgb(255, 255, 255);'" 
		if (json.configuration.frecuency == true )
			html += " value='' target='spline' checked > </p>"
		else
			html += " value='' target='spline' > </p>"


		html += '<button onclick="shape_Charts.saveData('+  this.id + ')" type="button" class="btn btn-danger btn-lg center">Save</button>'


		// debugger
		return  html;

	}


	static saveData(id){

		//find json by id
		var json = getJsonModelFromArray(id, arrayObjectsModel);
		// read model selected 
		var selected = $('select[name="charts"] option:selected').val()
		//get chart model 
		var chart = getJsonModelFromArrayByName(selected, shape_Charts.getModelCharts())
		//save properties
		json.chart= chart.name;
		json.url=chart.url;
		// SAVE ID
		json.nameId= document.getElementById('inputIdentificator' + json.id).value.toUpperCase();

		json.configuration["intervalFixed"] = $("#inputIntervalFixed" + json.id).is(':checked');
		json.configuration["frecuency"] = $("#inputFrecuency" + json.id).is(':checked');
		json.configuration["timeInterval"] = $('#inputTimeInterval' + json.id).val();



	}

	static changeImageChart(select){
		var name = $(select).find(":selected").val()

		var url  = $('option:selected', select).attr('imageurl');;
		//debugger

		$(".img-thumbnail-view").attr("src",url);

		//buscar atributo spline
		var id =  $(select).attr('id')
		console.log(name)
		if(name!="Spline"){
			$("#titleTimeInterval" + id).hide();
			$("#inputTimeInterval" + id).hide();
			$("#titleIntervalFixed" + id).hide();
			$("#inputIntervalFixed" + id).hide();
			$("#titleFrecuency" + id).hide();
			$("#inputFrecuency" + id).hide();
		}else{
			$("#titleTimeInterval" + id).show()
			$("#inputTimeInterval" + id).show();
			$("#titleIntervalFixed" + id).show();
			$("#inputIntervalFixed" + id).show();
			$("#titleFrecuency" + id).show();
			$("#inputFrecuency" + id).show();
		}

	}


	static getType() {
		return "Results";
	}

	static getModel(){
		return "shape_Charts";
	}

	static getUrl(){
		return "charts/spline.html"
	}

	static getName(){
		return "Charts";
	}

	static getImageUrl(){
		return "/resources/editor/assets/images/logos/online_results_data.png";
	}

	static getModelCharts(){
		var charts=[]
		// 1 spline model
		var chart = {name: "Spline", url: "charts/spline.html"}
		chart["imageUrl"]="/resources/editor/assets/images/dynamic-update-default.svg";
		charts.push(chart)
		// 2 box model
		chart = {name: "Box", url: "charts/box.html"}
		chart["imageUrl"]="/resources/editor/assets/images/chart-update-default.svg";
		charts.push(chart)
		return charts;
	}

}
array_shapes.push(shape_Charts)

var shape_Table = class Table extends shape_DSLPrimitive {


	constructor (attr, setter, getter) {

		attr= {name: attr, inputPort: "true", inputPortName: "PortResultsIn", outputPort: "false", outputPortName: ""}
		super(attr, setter, getter)

	}

	generateHTMLConfiguration(){
		debugger
		var json = getJsonModelFromArray(this.id, arrayObjectsModel);

		var html = "<h3>Description</h3>" + 
			"<p>Show the data geterated by the pipeline in a table in the dashboard.</p>"

		html += "<h3>Name<font color='red' size='5'>*</font></h3>" + 
			"<p><input id='inputIdentificator"+ json.id+ "' placeholder='Identificator' style='opacity: 1; background: rgb(255, 255, 255);width: 70%;'" 
		if(json.nameId!=null)
			html += " value='" + json.nameId + "' " 

		html += "></p>"

		html += '<button onclick="shape_Table.saveData('+  this.id + ')" type="button" class="btn btn-danger btn-lg center">Save</button>'

		return  html;

	}

	static saveData(id){

		//find json by id
		var json = getJsonModelFromArray(id, arrayObjectsModel);
		// SAVE ID
		json.nameId= document.getElementById('inputIdentificator' + json.id).value.toUpperCase();

	}

	static getType() {
		return "Results";
	}

	static getModel(){
		return "shape_Table";
	}

	static getUrl(){
		return "charts/table.html"
	}

	static getName(){
		return "Table";
	}

	static getImageUrl(){
		return "/resources/editor/assets/images/logos/online_results_table.png";
	}
}
array_shapes.push(shape_Table)

var shape_DataViewer = class DataViewer extends shape_DSLPrimitive {


	constructor (attr, setter, getter) {

		attr= {name: attr, inputPort: "true", inputPortName: "PortResultsIn", outputPort: "false", outputPortName: ""}
		super(attr, setter, getter)

	}

	generateHTMLConfiguration(){
		debugger
		var json = getJsonModelFromArray(this.id, arrayObjectsModel);

		var html = "<h3>Description</h3>" + 
			"<p>Show the data geterated by the pipeline in a table in the dashboard.</p>"

		html += "<h3>Name<font color='red' size='5'>*</font></h3>" + 
			"<p><input id='inputIdentificator"+ json.id+ "' placeholder='Identificator' style='opacity: 1; background: rgb(255, 255, 255);width: 70%;'" 
		if(json.nameId!=null)
			html += " value='" + json.nameId + "' " 

		html += "></p>"

		html += '<button onclick="shape_Table.saveData('+  this.id + ')" type="button" class="btn btn-danger btn-lg center">Save</button>'

		return  html;

	}

	static saveData(id){

		//find json by id
		var json = getJsonModelFromArray(id, arrayObjectsModel);
		// SAVE ID
		json.nameId= document.getElementById('inputIdentificator' + json.id).value.toUpperCase();

	}

	static getType() {
		return "Results";
	}

	static getModel(){
		return "shape_Table";
	}

	static getUrl(){
		return "charts/viewer.html"
	}

	static getName(){
		return "Data Viewer";
	}

	static getImageUrl(){
		return "/resources/editor/assets/images/logos/viewer_icon.png";
	}
}
array_shapes.push(shape_DataViewer)


var shape_Storage = class Storage extends shape_DSLPrimitive {


	constructor (attr, setter, getter) {
		attr= {name: attr, inputPort: "true", inputPortName: "PortProcessorIn", outputPort: "false", outputPortName: ""}
		super(attr, setter, getter)

	}

	generateHTMLConfiguration(){
		var json = getJsonModelFromArray(this.id, arrayObjectsModel);
		var html = "<h3>Description</h3>" + 
			"<p>Store the data geterated by the pipeline in logs that you can download anytime.</p>"

		html += "<h3>Name<font color='red' size='5'>*</font></h3>" + 
			"<p><input id='inputIdentificator"+ json.id+ "' placeholder='Identificator' style='opacity: 1; background: rgb(255, 255, 255);width: 70%;'" 
		if(json.nameId!=null)
			html += " value='" + json.nameId + "' " 

		html += "></p>"

		html += '<button onclick="shape_Storage.saveData('+  this.id + ')" type="button" class="btn btn-danger btn-lg center">Save</button>'

		return  html;

	}

	static saveData(id){
		debugger
		//find json by id
		var json = getJsonModelFromArray(id, arrayObjectsModel);
		// SAVE ID
		json.nameId= document.getElementById('inputIdentificator' + json.id).value.toUpperCase();

	}

	static getType() {
		return "Storage";
	}

	static getModel(){
		return "shape_Storage";
	}

	static getUrl(){
		return "charts/storagefile.html"
	}

	static getName(){
		return "Storage";
	}

	static getImageUrl(){
		return "/resources/editor/assets/images/logos/storage_icon.png";
	}
}
array_shapes.push(shape_Storage)