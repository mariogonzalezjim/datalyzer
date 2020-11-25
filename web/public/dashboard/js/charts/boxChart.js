



var dataChart=[];



var chartBox=null;

jQuery(document).ready(function($){

	$(document).ready(function () {


		// SOCKETS
		// socket opened on utils.js
		socket=io();

		var params= getParamsURL();
		console.log(params);

       
		var numSeriesBox=0;
		for(var i=0; i< params["socketId"].split(',').length-1 ; i++){
       
			createDataSocketListenerBox(socket,params["socketId"].split(',')[i],numSeriesBox, (params["socketId"].split(',').length-1) );
			numSeriesBox++;
		}


		window.onbeforeunload = function () {
			closeAPP();
		};


	});
});


function createChartBox(divName, title, title2, yAxisName, nameSeries, data, size ){

	chartBox = Highcharts.chart(divName, {

		chart: {
			height: size,
			type: 'column'
		},

		title: {
			text: title
		},

		subtitle: {
			text: title2
		},

		legend: {
			align: 'right',
			verticalAlign: 'middle',
			layout: 'vertical'
		},

		xAxis: {
			categories: [yAxisName],
			labels: {
				x: -0,
				style: {
					//color: 'red',
					fontSize: '18px'
				}
			}
		},

		yAxis: {
			allowDecimals: false,
			title: {
				text: 'Amount',
				style: {
					fontSize: '17px'
				} 
			},
			labels: {
				style: {
					//color: 'red',
					fontSize: '14px'
				}
			}
		},

		series: [{
			/*name: yAxisName.split(':')[0], */
			data: [1]
		}
				],

		responsive: {
			rules: [{
				condition: {
					maxWidth: 500
				},
				chartOptions: {
					legend: {
						align: 'center',
						verticalAlign: 'bottom',
						layout: 'horizontal'
					},
					yAxis: {
						labels: {
							align: 'left',
							x: 0,
							y: -5
						},
						title: {
							text: null
						}
					},
					subtitle: {
						text: null
					},
					credits: {
						enabled: false
					}
				}
			}]
		}
	});

	$('#small').click(function () {
		chartBox.setSize(400, 300);
	});

	$('#large').click(function () {
		/* chart.addSeries({                        
         name: 'Christmas Eve',
         data: [6, 7, 8]
      }, true);
      chart.redraw();
      //chart.update();
      */

		chartBox.series[0].setData([chartBox.series[0].data[0].y]);
		// + 200

	});

	setInterval(function (data, chart) {

		chartBox.series[0].setData([chartBox.series[0].data[0].y + data["value"]]);
		data["value"]=0;
		//console.log(chartBox.series[0].data[0].y);
	}, 1000, data, chartBox);


}

function addNewSerietoChartBox(name, data, serie){

	chartBox.addSeries({                        
		data: [2],
		name: name
	});

	setInterval(function (data, chart, serie) {

		chartBox.series[serie].setData([chartBox.series[serie].data[0].y + data["value"]]);
		data["value"]=0;
		//console.log(chartBox.series[0].data[0].y);
	}, 1000, data, chartBox, serie);
	
}


// borrar ?
function createChart2(divName){

	var chart = Highcharts.chart('container', {

		chart: {
			type: 'column'
		},

		title: {
			text: 'Highcharts responsive chart'
		},

		subtitle: {
			text: 'Resize the frame or click buttons to change appearance'
		},

		legend: {
			align: 'right',
			verticalAlign: 'middle',
			layout: 'vertical'
		},

		xAxis: {
			categories: ['Apples'],
			labels: {
				x: -10
			}
		},

		yAxis: {
			allowDecimals: false,
			title: {
				text: 'Amount'
			}
		},

		series: [{
			name: 'test1',
			data: [1]
		}, {
			name: 'Christmas Day before dinner',
			data: [6]
		}, {
			name: 'Christmas Day after dinner',
			data: [8]
		}],

		responsive: {
			rules: [{
				condition: {
					maxWidth: 500
				},
				chartOptions: {
					legend: {
						align: 'center',
						verticalAlign: 'bottom',
						layout: 'horizontal'
					},
					yAxis: {
						labels: {
							align: 'left',
							x: 0,
							y: -5
						},
						title: {
							text: null
						}
					},
					subtitle: {
						text: null
					},
					credits: {
						enabled: false
					}
				}
			}]
		}
	});

	$('#small').click(function () {
		chart.setSize(400, 300);
	});

	$('#large').click(function () {
		/* chart.addSeries({                        
         name: 'Christmas Eve',
         data: [6, 7, 8]
      }, true);
      chart.redraw();
      //chart.update();
      */
		chart.series[0].setData([8]);
		console.log(chart.series[0].data);

		//chart.redraw();
	});


}

function createDataSocketListenerBox(socket, name, numSerie, numSeriesTotal){

	var inialization=0;

	socket.on(name, function(data){	

		//split messages
		for(var i=0; i<(data.split('$#$').length-1) ; i++){
			var json = JSON.parse(data.split('$#$')[i]);

			if(numSerie==0 && inialization==0){
				//create chart
				var accumulator={value:0};
				dataChart.push(accumulator);
				createChartBox('container', "'" + getFirstKeyJson(json) +"' (Total Amount)",  "Update every second", getFirstKeyJson(json),"Data received", dataChart[numSerie], 400);
				inialization++;
			}else if(inialization==0){
				//add serie to existing chart
				console.log(numSerie);
				var accumulator={value:0};
				dataChart.push(accumulator);
				addNewSerietoChartBox(name, dataChart[numSerie], numSerie);
				inialization++;
			}
			
			//add data to accumulator
			if(Number.isInteger(getFirstValueJson(json))==true){
				dataChart[numSerie]["value"]= dataChart[numSerie]["value"]+ getFirstValueJson(json);
			}else{
				dataChart[numSerie]["value"]= dataChart[numSerie]["value"]+ 1;
			}


		}

	});
}
