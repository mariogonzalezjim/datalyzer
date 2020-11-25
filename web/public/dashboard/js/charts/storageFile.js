


var initializationSpline=0;

var dataSpline=[];

var statusGraphSpline=0;

//var aux={value:2};

jQuery(document).ready(function($){

	$(document).ready(function () {


		// SOCKETS
		// socket opened on utils.js
		socket=io();

		var params= getParamsURL();

		//get name log
		console.log(params)
		$("#name-log").html("Press button to download '<i>" +params["name"] + "</i>'")
		$("#link-download-file").attr("href", "/private/download_file?id=" + params["projectId"] + "&filename=" + params["name"] );

		for(var i=0; i< params["socketId"].split(',').length-1 ; i++){
			createDataSocketListenerSpline(socket,params["socketId"].split(',')[i] );
		}

		// attach event to download button


		window.onbeforeunload = function () {
			closeAPP();
		};


	});
});


function createChart(divName, title, nameSeries, data, size ){

	statusGraphSpline=1;

	Highcharts.setOptions({
		global: {
			useUTC: false
		}
	});

	Highcharts.chart(divName, {
		colors: ['#2b908f', '#90ee7e', '#f45b5b', '#7798BF', '#aaeeee', '#ff0066', '#eeaaee',
				 '#55BF3B', '#DF5353', '#7798BF', '#aaeeee'],
		chart: {
			height: size,
			backgroundColor: {
				linearGradient: { x1: 0, y1: 0, x2: 1, y2: 1 },
				stops: [
					[0, '#2a2a2b'],
					[1, '#3e3e40']
				]
			},
			plotBorderColor: '#606063',
			type: 'spline',
			animation: Highcharts.svg, // don't animate in old IE
			marginRight: 10,
			events: {
				load: function () {

					// set up the updating of the chart each second
					var series = this.series[0];
					/*
                  setInterval(function () {
                     var x = (new Date()).getTime(), // current time
                         y = Math.random();
                     series.addPoint([x, y], true, true);
                  }, 1000); */

					setInterval(function (data) {
						console.log(statusGraphSpline);
						if(statusGraphSpline!=0){
							var x = (new Date()).getTime();
							series.addPoint([x, data["value"]], true, true);
							data["value"]=0;
						}
					}, 1000, data); 

				}
			}
		},
		title: {
			text: title,
			style: {
				color: '#E0E0E3',
				/*textTransform: 'uppercase', */
				fontSize: '19px'
			} 
		},
		xAxis: {
			type: 'datetime',
			tickPixelInterval: 150,
			gridLineColor: '#707073',
			labels: {
				style: {
					color: '#E0E0E3'
				}
			},
			lineColor: '#707073',
			minorGridLineColor: '#505053',
			tickColor: '#707073',
			title: {
				style: {
					color: '#A0A0A3'

				}
			}
		},
		yAxis: {
			title: {
				text: 'Value'
			},
			plotLines: [{
				value: 0,
				width: 1,
				color: '#808080'
			}],
			gridLineColor: '#707073',
			labels: {
				style: {
					color: '#E0E0E3'
				}
			},
			lineColor: '#707073',
			minorGridLineColor: '#505053',
			tickColor: '#707073',
			tickWidth: 1,
			title: {
				style: {
					color: '#A0A0A3'
				}
			}
		},
		tooltip: {
			formatter: function () {
				return '<b>' + this.series.name + '</b><br/>' +
					Highcharts.dateFormat('%Y-%m-%d %H:%M:%S', this.x) + '<br/>' +
					Highcharts.numberFormat(this.y, 2);
			},
			backgroundColor: 'rgba(0, 0, 0, 0.85)',
			style: {
				color: '#F0F0F0'
			}
		},
		legend: {
			enabled: false
		},
		exporting: {
			enabled: false
		},
		plotOptions: {
			series: {
				dataLabels: {
					color: '#B0B0B3'
				},
				marker: {
					lineColor: '#333'
				}
			},
			boxplot: {
				fillColor: '#505053'
			},
			candlestick: {
				lineColor: 'white'
			},
			errorbar: {
				color: 'white'
			}
		},
		legend: {
			itemStyle: {
				color: '#E0E0E3'
			},
			itemHoverStyle: {
				color: '#FFF'
			},
			itemHiddenStyle: {
				color: '#606063'
			}
		},
		labels: {
			style: {
				color: '#707073'
			}
		},

		drilldown: {
			activeAxisLabelStyle: {
				color: '#F0F0F3'
			},
			activeDataLabelStyle: {
				color: '#F0F0F3'
			}
		},

		navigation: {
			buttonOptions: {
				symbolStroke: '#DDDDDD',
				theme: {
					fill: '#505053'
				}
			}
		},
		series: [{
			name: nameSeries,
			data: (function () {
				// generate an array of random data
				var data = [],
					time = (new Date()).getTime(),
					i;

				for (i = -19; i <= 0; i += 1) {
					data.push({
						x: time + i * 1000,
						y: 0
					});
				} 
				return data;
			}())
		}],
		// special colors for some of the
		legendBackgroundColor: 'rgba(0, 0, 0, 0.5)',
		background2: '#505053',
		dataLabelsColor: '#B0B0B3',
		textColor: '#C0C0C0',
		contrastTextColor: '#F0F0F3',
		maskColor: 'rgba(255,255,255,0.3)'
	});

}

// sin utilizar, borrar?
function createChart2(divName){
	Highcharts.setOptions({
		global: {
			useUTC: false
		}
	});

	Highcharts.chart(divName, {
		colors: ['#2b908f', '#90ee7e', '#f45b5b', '#7798BF', '#aaeeee', '#ff0066', '#eeaaee',
				 '#55BF3B', '#DF5353', '#7798BF', '#aaeeee'],
		chart: {
			height: 200,
			backgroundColor: {
				linearGradient: { x1: 0, y1: 0, x2: 1, y2: 1 },
				stops: [
					[0, '#2a2a2b'],
					[1, '#3e3e40']
				]
			},
			plotBorderColor: '#606063',
			type: 'spline',
			animation: Highcharts.svg, // don't animate in old IE
			marginRight: 10,
			events: {
				load: function () {

					// set up the updating of the chart each second
					var series = this.series[0];

					/*
                  setInterval(function () {
                     var x = (new Date()).getTime(), // current time
                         y = Math.random();
                     series.addPoint([x, y], true, true);
                  }, 1000); */


					setInterval(function () {
						var x = (new Date()).getTime();
						series.addPoint([x, 1], true, true);
						splineAccumulator=0;
					}, 1000); 

				}
			}
		},
		title: {
			text: title,
			style: {
				color: '#E0E0E3',
				/*textTransform: 'uppercase', */
				fontSize: '19px'
			} 
		},
		xAxis: {
			type: 'datetime',
			tickPixelInterval: 150,
			gridLineColor: '#707073',
			labels: {
				style: {
					color: '#E0E0E3'
				}
			},
			lineColor: '#707073',
			minorGridLineColor: '#505053',
			tickColor: '#707073',
			title: {
				style: {
					color: '#A0A0A3'

				}
			}
		},
		yAxis: {
			title: {
				text: 'Value'
			},
			plotLines: [{
				value: 0,
				width: 1,
				color: '#808080'
			}],
			gridLineColor: '#707073',
			labels: {
				style: {
					color: '#E0E0E3'
				}
			},
			lineColor: '#707073',
			minorGridLineColor: '#505053',
			tickColor: '#707073',
			tickWidth: 1,
			title: {
				style: {
					color: '#A0A0A3'
				}
			}
		},
		tooltip: {
			formatter: function () {
				return '<b>' + this.series.name + '</b><br/>' +
					Highcharts.dateFormat('%Y-%m-%d %H:%M:%S', this.x) + '<br/>' +
					Highcharts.numberFormat(this.y, 2);
			},
			backgroundColor: 'rgba(0, 0, 0, 0.85)',
			style: {
				color: '#F0F0F0'
			}
		},
		legend: {
			enabled: false
		},
		exporting: {
			enabled: false
		},
		plotOptions: {
			series: {
				dataLabels: {
					color: '#B0B0B3'
				},
				marker: {
					lineColor: '#333'
				}
			},
			boxplot: {
				fillColor: '#505053'
			},
			candlestick: {
				lineColor: 'white'
			},
			errorbar: {
				color: 'white'
			}
		},
		legend: {
			itemStyle: {
				color: '#E0E0E3'
			},
			itemHoverStyle: {
				color: '#FFF'
			},
			itemHiddenStyle: {
				color: '#606063'
			}
		},
		labels: {
			style: {
				color: '#707073'
			}
		},

		drilldown: {
			activeAxisLabelStyle: {
				color: '#F0F0F3'
			},
			activeDataLabelStyle: {
				color: '#F0F0F3'
			}
		},

		navigation: {
			buttonOptions: {
				symbolStroke: '#DDDDDD',
				theme: {
					fill: '#505053'
				}
			}
		},
		series: [{
			name: 'Random data',
			data: (function () {
				// generate an array of random data
				var data = [],
					time = (new Date()).getTime(),
					i;

				for (i = -19; i <= 0; i += 1) {
					data.push({
						x: time + i * 1000,
						y: 0
					});
				} 
				return data;
			}())
		}],
		// special colors for some of the
		legendBackgroundColor: 'rgba(0, 0, 0, 0.5)',
		background2: '#505053',
		dataLabelsColor: '#B0B0B3',
		textColor: '#C0C0C0',
		contrastTextColor: '#F0F0F3',
		maskColor: 'rgba(255,255,255,0.3)'
	});
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

function pauseGraph(){
	statusGraphSpline=0;
}

function resumeGraph(){
	statusGraphSpline=1;
}


function createDataSocketListenerSpline(socket, name){
	socket.on(name, function(data){

		//split messages
		for(var i=0; i<(data.split('$#$').length-1) ; i++){
			var json = JSON.parse(data.split('$#$')[i]);

		}

	});
}