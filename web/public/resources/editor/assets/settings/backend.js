// This configuration file will be overridden if the application
// is provided by a node.js server. In this case we store all
// circuits and shapes on the local node.js server instead of
// using the global available repository.
// Check the special route in the ./server/main.js for this.
//
// This is useful if you want run the DigitalTrainingStudio local on
// RaspberryPi or another IoT device
//
//
var conf={
    fileSuffix : ".datalyzer",
    designer: {
        url:"http://freegroup.github.io/draw2d_js.app.shape_designer/"
    },
    /* not necessary */
    backend: {
        oauth     : "",
        isLoggedIn: "",
        file:{
            list  : "",
            get   : "",
            del   : "",
            save  : "",
            rename: "",
            image : ""
        },
        // registry of RF24 registered devices. Only available if we use
        // a node.js server and a connected RF24 receiver (e.g. Raspi or arduino with a RF24 receiver)
        //
        bloc: {
            list: null
        }
    },
    shapes :{
        url: "/resources/editor/assets/shapes/"
    },
    issues: {
        url: "mario.gonzalezj@estudiante.uam.es"
    },
    color:{
        high: "#C21B7A",
        low:  "#0078F2"
    }
};
