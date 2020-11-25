

var inializationDhasboard=0;
var systemStatus=0;
var socket;
var numBytes=0;
var intervalBytes;
var appid;

jQuery(document).ready(function($){

    $(document).ready(function () {


        var params = getParamsURL();
        console.log(params);

        appid = params["id"]

        //debugger


        // SOCKETS
        socket=io();
        console.log("Inicializado");
        socket.emit("startAPP" , appid); 

        socket.on('disconnect', function(data){
            console.log(data)
            console.log(appid)
            if(data==appid){
                setSystemStatus(false);
                inializationDhasboard=0;
            }
        });




        //createDashboardElements(params);
        resizeIframe();

        var arraySocketIdUnique={};
        for(r=0; r < arraySocketsId.length ; r++ )
            for(var i=0; i< arraySocketsId[r].split(',').length-1 ; i++){
                arraySocketIdUnique[arraySocketsId[r].split(',')[i]] = arraySocketsId[r].split(',')[i]

            }

        
        for(var i in arraySocketIdUnique){

            createSocketDataListenerDashboard(i );
        }

        window.onbeforeunload = function () {
            //closeAPP();
        };

    });
});


function setSystemStatus(bool){

    if(bool==true){
        var htmlServerStatus = document.getElementById("serverStatus");
        htmlServerStatus.style.backgroundColor= "rgb(33, 200, 17)";
        systemStatus=1;
    }else{
        var htmlServerStatus = document.getElementById("serverStatus");
        htmlServerStatus.style.backgroundColor= "#cb0606";
        systemStatus=0;

    }

}

function startBandwithUsage(div){

    clearInterval(intervalBytes);
    intervalBytes=setInterval(function(div){ 

        div.innerHTML= numBytes + " B/s";
        numBytes=0;

    }, 1000, div);
}

function getParamsURLDashboard(url){
    var params = [];

    if (location.search) {
        var parts = ('?' + atob(location.search.toString().split('?')[1])).substring(1).split('&');
        //var parts = location.search.substring(1).split('&');

        for (var i = 0; i < parts.length; i++) {
            var nv = parts[i].split(':');
            if (!nv[0]) continue;
            params[nv[0]] = nv[1] || true;
        }
    }
    console.log(params)
    return params;
}


function createDashboardElements(widgets){

    var html= document.getElementById("dashboardElements");

    for(var i=0 ; i< widgets.length -1 ; i++){
        // <p id="callback">   </p>
        html.innerHTML += '<div class="col-xs-12"><div class="card"> <div class="">'  +
            ' <iframe class="iframe-dashboard" src="./'+ widgets[i].split('%').join('&') +'" width="100%" scrolling="no"></iframe> ' +
            '</div></div> </div>'
        console.log(widgets[i].split('%').join('&'));
        var socketsDataId= widgets[i].split('%').join('&').split('?')[1];
        var params = [];
        var parts = socketsDataId.split('&');
        for(var r=0; r<parts.length-1; r++){
            createSocketDataListenerDashboard(parts[r].split('=')[1]);	
        }

    }

}


function resizeIframe(){
    /*
			 * If you do not understand what the code below does, then please just use the
			 * following call in your own code.
			 *
			 *   iFrameResize({log:true});
			 *
			 * Once you have it working, set the log option to false.
			 */

    iFrameResize({
        log                     : false,                  // Enable console logging
        inPageLinks             : true,
        resizedCallback         : function(messageData){ // Callback fn when resize is received
            $('p#callback').html(
                '<b>Frame ID:</b> '    + messageData.iframe.id +
                ' <b>Height:</b> '     + messageData.height +
                ' <b>Width:</b> '      + messageData.width +
                ' <b>Event type:</b> ' + messageData.type
            );

            var iframe = document.getElementById(messageData.id);
            iframe["height"]=messageData.height;
            //debugger;
            //console.log(messageData)
        },
        messageCallback         : function(messageData){ // Callback fn when message is received
            $('p#callback').html(
                '<b>Frame ID:</b> '    + messageData.iframe.id +
                ' <b>Message:</b> '    + messageData.message
            );
            alert(messageData.message);
            document.getElementsByTagName('iframe')[0].iFrameResizer.sendMessage('Hello back from parent page');
        },
        closedCallback         : function(id){ // Callback fn when iFrame is closed
            $('p#callback').html(
                '<b>IFrame (</b>'    + id +
                '<b>) removed from page.</b>'
            );
        }
    });
}

// start app here
function createSocketDataListenerDashboard(id){

    socket.on( id, function(data){
        //console.log(id)
        if(inializationDhasboard==0){
            setSystemStatus(true);
            startBandwithUsage(document.getElementById("bandwidth"));
            inializationDhasboard=1;
        }
        numBytes += byteCount(data.length);

    });
}