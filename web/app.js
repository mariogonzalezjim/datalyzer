// NODEJS CONFIG
var express      = require('express');
var session      = require('express-session');
var app = express();
var port= 8080;
//template engine
var expressHandlebars  = require('express-handlebars');
var path         = require('path');
var favicon      = require('serve-favicon');
// TOOLS
var logger       = require('morgan');
var cookieParser = require('cookie-parser');
var bodyParser   = require('body-parser');
// DATABASE
const APIdatabase = require('./models/api')
var mongoose     = require('mongoose');
var configDB     = require('./config/database.js');
// SOCKETS
var server = require('http').createServer(app);  
var io = require('socket.io')(server);
var hashSocketUsers={};
var hashSocketStreamingApps={};

var dl  = require('delivery')

//PASSPORT LOGIN
const passport   = require('passport');
require('./config/passport')(passport); // pass passport for configuration
var flash        = require('connect-flash');

// for writing files
var fs = require('fs');

// Load the TCP Library
var net = require('net');

//Generate unique IDs
const uuidv1 = require('uuid/v1');

// Module allows you to authenticate using a username and password
const LocalStrategy = require('passport-local').Strategy;


// ENGINE ===============================================================
// view engine setup
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', '.hbs');
app.engine('.hbs', expressHandlebars({
    extname: '.hbs',
    defaultLayout: 'main'
}));

// Configure bodyParser
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: false }));


// Configuration options for express-session
const sess = {
    secret: "DatalyzerS3CR3T",
    resave: false,
    saveUninitialized: true,
};

// init session with a SUPER SECRET password in order to secure your session ids - NEVER SHARE THIS PASSWORD EVER
app.use(session(sess));


// DATABASE ===============================================================
mongoose.connect(configDB.url); // connect to our database

// PASSPORT authentication ===============================================
require('./config/passport')(passport); // pass passport for configuration
app.use(passport.initialize());
app.use(passport.session());
app.use(flash());

// uncomment after placing your favicon in /public
//app.use(favicon(path.join(__dirname, 'public', 'favicon.ico')));

// para ver en la consola todas las peticiones
//app.use(logger('dev'));
app.use(cookieParser());
app.use(express.static(path.join(__dirname, 'public'), {
    etag: false
}));

// CONTROLLERS =============================================
require('./routes/controller.js') (app, passport); // load our routes and pass in our app and fully configured passport



// MODELS ==================================================
var users           = require('./routes/users');
var Project         = require('./models/project');
const Processor     = require('./models/customprocessor'); 
app.use('/users', users);


module.exports = app;


//SOCKETS

// Add a connect listener
io.on('connection', function(socket) {

    console.log('Client connected.');

    // Disconnect listener
    socket.on('disconnect', function() {
        //console.log('Client disconnected.');
    });

    // when a user load the DSL on the browser ask for info
    socket.on('loadInfoAPP', function(message) {

        //LOAD INFO FROM DATABASE
        // get all APIs 

        APIdatabase.find({}, function(err, list) {

            if (err) {
                //console.log('Error al recuperar los APIs');
            }
            if (!list) {
                //console.log('No existe APIs');
            }
            //console.log(list.toString().replace('_', ''));

            for (var i in list) {
                io.emit("loadAPI", JSON.stringify(list[i]));
            }

            io.emit("loadRequestAPI", '');
            io.emit("loadDataPipeline", '');
        });


    });

    // receive the application model from the DSL
    socket.on('sendmodeltoserver', function(message) {
        //get appid
        var appid= message.split('appid="')[1].split('"')[0];
        // save model in database
        // search project by appid
        Project.findOne({ 'id' :  appid }, function(err, project) { 
            project.model=message;
            project.save(function(err) {
                if (err)
                    throw err;
                console.log("app model saved in db");
                return;
            });   

        });

        //console.log(message)
        if(process.platform=="win32"){
            fs.writeFile(".\\files\\" +appid + ".txt", message, function(err) {
                if(err) {
                    return //console.log(err);
                    io.emit("CodeGenerated", 'ERR');
                }
                console.log("The file was saved!");

                generateCode(appid);

            });
        }else{
            fs.writeFile("./files/" +appid + ".txt", message, function(err) {
                if(err) {
                    return //console.log(err);
                    io.emit("CodeGenerated", 'ERR');
                }
                console.log("The file was saved!");

                generateCode(appid);

            });
        }
    });

    // receive the DSL model and store in database
    socket.on('modelDSL', function(message) {
        //get appid
        console.log(message.modelDSL)

        // update database 
        // search project by appid
        Project.findOne({ 'id' :  message.appid }, function(err, project) { 
            project.modelDSL=message.modelDSL;
            console.log(message.modelDSL)
            project.save(function(err) {
                if (err)
                    throw err;
                console.log("modelDSL saved in db");
                return;
            });   

        });

    });

    // STREAMING APPLICATION COMMANDS
    socket.on('startAPP', function(message) {

        var appid= message;
        console.log("sending ping to app " + appid);
        if(isAppRunning(appid)==false){
            console.log("starting app..." + appid);
            startApp(appid);
        }
    });

    socket.on('closeAPP', function(message) {

        if(hashSocketStreamingApps[message]!=null){
            hashSocketStreamingApps[message].write("exit\n");
            console.log("estoy aqui en closeAPP")
        }
    });

    socket.on('RESUMEAPP', function(message) {
        if(hashSocketStreamingApps[message]!=null)
            hashSocketStreamingApps[message].write("resume\n");
    });

    socket.on('PAUSEAPP', function(message) {
        console.log("estoy aqui");
        if(hashSocketStreamingApps[message]!=null)
            hashSocketStreamingApps[message].write("pause\n");
    });

    socket.on('downloadFile', function(message) {

        // download file


    });

    socket.on('requestCustomProcessor', function() {
        Processor.find({}, function(err, list) {

            io.emit("loadCustomProcessor", list);
        });
    });


});



// Start a TCP Server to connect with users apps
net.createServer(function (socketTCP) {

    // Identify this client // cambiar en el futuro por ids
    socketTCP.name = socketTCP.remoteAddress + ":" + socketTCP.remotePort;

    console.log("Aplicación conectada\n");

    //send message to javascript web to load dashboard.html
    io.emit("loading", 'READY');


    var appidSocket=0;

    // Handle incoming messages from clients.
    socketTCP.on('data', function (data) {
        //console.log(data.toString())
        if(appidSocket==0){
            appidSocket= data.toString().split("$#$")[0];
            console.log(appidSocket)
            hashSocketStreamingApps[appidSocket]=socketTCP;
        }else{
            //console.log(data.toString().split("$#$").length);
            var messages = data.toString().split("$#$")
            messages.forEach( function(valor, indice, array) {
                //console.time('test');
                // message: "appid_data"
                var array= valor.split("_");
                var id= array[0];
                array.splice(0,1);
                //console.log(id)
              
                io.emit(id, array[0] + "$#$");
                
                //console.timeEnd('test');
                //console.log("\n\n");
            });
             

        }
    });

    // Remove the client from the list when it leaves
    socketTCP.on('end', function () {
        console.log("Aplicación desconectada\n");
        io.emit("disconnect", appidSocket);
        // clients.splice(clients.indexOf(socketTCP), 1);
        hashSocketStreamingApps[appidSocket]=null;
    });


}).listen(443);


// read database and launch ==================================================================
server.listen(port);



// catch exceptions
process.on('uncaughtException', function(err) {
    console.log('Caught exception: ' + err);
});






// FUNCTIONS ================================================================================

function generateCode(appid){


    if(process.platform=="win32"){
        var exec = require('child_process').exec;
        //copye template folder
        var copyFolder="xcopy \"StreamingApplications\\KafkaProjectGeneratedTemplate\" \"StreamingApplications\\" + appid + "\" /s /e /y /i";
        //var copyFolder="cp -r \"StreamingApplications/KafkaProjectGeneratedTemplate\" \"StreamingApplications/" + appid + "\" ";
        exec(copyFolder, function(error, stdout, stderr) {
            console.log(stdout);
            console.log(stderr);
            // detele .java files if exist
            var deleteFiles = "cd StreamingApplications\\" + appid + "\\src\\main\\java\\com && del /S .\\* /Q";
            //var deleteFiles = "cd StreamingApplications/" + appid + "/src/main/java/com && rm -r *";
            var exec2 = require('child_process').exec;
            exec2(deleteFiles, function(error, stdout, stderr) {
                console.log(stdout);
                console.log(stderr);
                //generate .java files
                var generateFiles=  "cd CodeGeneration\\ && java -jar CodeGeneration.jar " +__dirname + " " + appid + ""; 
                //var generateFiles=  "cd CodeGeneration && java -jar CodeGeneration.jar " + appid + ""; 
                var exec3 = require('child_process').exec;
                exec3(generateFiles, function(error, stdout, stderr) {
                    console.log(error);
                    console.log(stdout);
                    console.log(stderr);
                    compileCode(appid);
                });

            });
        });
    }else if (process.platform=="linux"){
        var exec = require('child_process').exec;
        //copye template folder
        var copyFolder="cp -r \"StreamingApplications/KafkaProjectGeneratedTemplate\" \"StreamingApplications/" + appid + "\" ";
        exec(copyFolder, function(error, stdout, stderr) {
            console.log(stdout);
            console.log(stderr);
            // detele .java files if exist
            var deleteFiles = "cd StreamingApplications/" + appid + "/src/main/java/com && rm -r *";
            var exec2 = require('child_process').exec;
            exec2(deleteFiles, function(error, stdout, stderr) {
                console.log(stdout);
                console.log(stderr);
                //generate .java files
                var generateFiles=  "cd CodeGeneration && java -jar CodeGeneration.jar " +__dirname + " " + appid + ""; 
                var exec3 = require('child_process').exec;
                exec3(generateFiles, function(error, stdout, stderr) {
                    console.log(error);
                    console.log(stdout);
                    console.log(stderr);
                    compileCode(appid);
                });

            });
        });
    }
    else{
        return;
    }



}

function compileCode(appid){

    var exec = require('child_process').exec;
    var cmd;
    if(process.platform=="win32"){
        cmd = "cd StreamingApplications\\"+appid  +" && mvn compile assembly:single";
    }else{
        cmd = "cd StreamingApplications/"+appid  +" && mvn compile assembly:single";
    }


    exec(cmd, {maxBuffer: 1024 * 500}, function(error, stdout, stderr) {
        console.log(stdout);
        console.log(stderr);
        console.log(error);

        //save flag in database
        Project.findOne({ 'id' :  appid }, function(err, project) { 

            project.compiled=true;
            project.save(function(err) {
                if (err)
                    throw err;
                console.log("project update in db");
                return;
            });   


        });
        io.emit("CodeGenerated", 'OK');
    });

}

function startApp(appid){
   
    return;
    var exec = require('child_process').exec;
    var cmd = "";
    if(process.platform=="win32"){
        cmd = "cd StreamingApplications\\" +appid + "\\target\\ && java -jar KafkaAPIClient-1.0-SNAPSHOT-jar-with-dependencies.jar";
    }else{
        cmd = "cd StreamingApplications/" +appid + "/target/ && java -jar KafkaAPIClient-1.0-SNAPSHOT-jar-with-dependencies.jar";
    }
    //return;
    exec(cmd, function(error, stdout, stderr) {
        console.log(stdout);
        console.log(error);
        console.log(stderr);
    }); 

}

function generateAPPID(){
    return Math.floor(Math.random() * 200001);
}

function isAppRunning(appid){
    if(hashSocketStreamingApps[appid]==null){
        console.log("App is not already running")
        return false;
    }
    console.log("App is already running");
    return true;
}



















