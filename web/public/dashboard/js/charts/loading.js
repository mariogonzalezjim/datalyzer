

var socket; 


jQuery(document).ready(function($){

    $(document).ready(function () {

        socket = io();
        
        // esto lo comento porque ahora cierro el loading en servertable
/*
        socket.on('loading', function(data){
            
            if(data.toString()=="READY")
                // oculto el loading
                closeLoading();
 

            //transparent background
            var footer = document.getElementById("footer");
            footer.style.backgroundColor = "transparent";
            
            //close iframe
            parent.closeNav();
            
        });
*/

    });
});


function closeLoading(){
    //oculto el loading
    $("html").addClass("loadend loadfirst");
    cover();
    setTimeout(function ()
               {

        if( $('.index').length ){
            goodbye.onAnimate();
        }

        $("html").addClass("learnmore");
        if ($("#swfc").size())
        {
            var b = window.navigator.userAgent.toLowerCase();
            if (b.indexOf("msie") !=- 1) {}
            else {
                var a = new swiffy.Stage(document.getElementById("swfc"), swo);
                a.start()
            }
        }
    }, 500);
}

 function close_frame(){
    if(!window.should_close){
        window.should_close=1;
    }else if(window.should_close==1){
        location.reload();
        //or iframe hide or whatever
    }
 }