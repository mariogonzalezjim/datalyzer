// load up models
var User       		= require('../models/user');
var Project         = require('../models/project');
var Processor       = require('../models/customprocessor');
var APIdatabase     = require('../models/api');
//Generate unique IDs
const uuidv1 = require('uuid/v1');

module.exports = function(app, passport) {

	// =====================================
	// HOME PAGE (with login links) ========
	// =====================================
	app.get('/', function(req, res) {
		res.render('index', { title: 'Datalyzer' }); 
	});

	// =====================================
	// LOGIN ===============================
	// =====================================
	// show the login form
	app.get('/login', function(req, res) {

		// render the page and pass in any flash data if it exists
		res.render('login', {loginMessage:req.flash('error')});
	});

	// process the login form
	app.post('/login', passport.authenticate('local-login', {
		successRedirect : '/dashboard', // redirect to the secure profile section
		failureRedirect : '/login', // redirect back to the signup page if there is an error
		failureFlash : true // allow flash messages
	}));


	// =====================================
	// SIGNUP ==============================
	// =====================================
	// show the login form
	app.get('/signup', function(req, res) {
		// render the page and pass in any flash data if it exists
		res.render('signup', {
			message :   req.flash('error'),
			name    :   req.flash('name'),
			lastname:   req.flash('lastname'),
			email   :   req.flash('email')
		});
	});

	// process the login form
	app.post('/signup', passport.authenticate('local-signup', {
		successRedirect : '/dashboard', // redirect to the secure profile section
		failureRedirect : '/signup', // redirect back to the signup page if there is an error
		failureFlash : true // allow flash messages
	}));



	// =====================================
	// DASHBOARD HOME ======================
	// =====================================
	app.get('/dashboard', isLoggedIn, function(req, res) {
		// get user model
		res.render('dashboard', {
			user : req.user , // get the user out of session and pass to template,
			layout: 'dashboard'
		});
	});


	// =====================================
	// PROFILE =============================
	// =====================================
	app.get('/profile', isLoggedIn, function(req, res) {
		// get user model
		res.render('profile', {
			user : req.user , // get the user out of session and pass to template,
			layout: 'dashboard'
		});
	});

	//update personal data
	app.post('/update_account', function(req, res) {
		// Get POST values, It's easy
		var userName = req.body.name;
		var lastName= req.body.lastname;
		var email = req.body.email;      
		User.findOne({ 'local.email' :  email }, function(err, user) {
			user.local.name= userName;
			user.local.lastname=lastName;         
			// save the user
			user.save(user=function(err) {
				if (err)
					throw err;
				return;
			});      
		});
		//get user updated model
		// get user model
		req.user.local.name=userName;
		req.user.local.lastname=lastName;
		res.render('profile', {
			user : req.user , // get the user out of session and pass to template,
			message : "Profile updated",   
			layout: 'dashboard'
		});
	});

	//change password
	app.post('/change_password', function(req, res) {
		// Get POST values
		var password = req.body.currentpassword;
		var newPassword= req.body.newpassword;
		var newPasswordRepeat = req.body.newpasswordrepeat;
		var email = req.body.email;
		User.findOne({ 'local.email' :  email },  function(err, user) {
			//check if pass is correct
			if(user.validPassword(password)==false){
				res.render('profile', {
					user : user , // get the user out of session and pass to template,
					messageError : "Password is incorrect",   
					layout: 'dashboard'
				});
				return false;
			}

			if(newPassword!=newPasswordRepeat){
				res.render('profile', {
					user : user , // get the user out of session and pass to template,
					messageError : "Password doesn't match",   
					layout: 'dashboard'
				});
				return false;
			}
			//chnage pass
			user.local.password= user.generateHash(newPassword);
			// save the user
			user.save(user=function(err) {
				if (err)
					throw err;
				return;
			});
			//render page
			res.render('profile', {
				user : req.user , // get the user out of session and pass to template,
				message : "Password update",   
				layout: 'dashboard'
			});
		});
		console.log("he finalizado")

	});



	// =====================================
	// PRIVATE LIST PROJECTS ===============
	// =====================================
	app.get('/private/workspace/list', isLoggedIn, function(req, res) {
		//get projects from database
		Project.find({'user' : req.user.local.email } , function(err, project){
			res.render('workspace_list', {
				user : req.user , // get the user out of session and pass to template,
				projects : project,
				layout: 'dashboard'
			});
		});

	});


	// =====================================
	// CREATE PROJECT ======================
	// =====================================
	app.get('/private/workspace/create', isLoggedIn, function(req, res) {
		// get user model
		res.render('create_project', {
			user : req.user , // get the user out of session and pass to template,
			layout: 'dashboard'
		});
	});

	app.post('/private/workspace/create', function(req, res) {

		//check empty name
		if(req.body.name == null || req.body.name==""){
			res.render('create_project', {
				user : req.user , // get the user out of session and pass to template,
				messageError: "Error: Empty name",
				layout: 'dashboard'
			});
			return;
		}

		// check if name is unique by user
		Project.findOne({ 'user' :  req.user.local.email, 'name' : req.body.name }, function(err, project) { 
			//check existing project
			if(project!=null){
				res.render('create_project', {
					user : req.user , // get the user out of session and pass to template,
					messageError: "Error: Existing name",
					layout: 'dashboard'
				});
				return false;
			}
			// create Project
			var newProject              = new Project();
			newProject.dateCreated      = getDateTime();
			newProject.dateLastModified = getDateTime();
			newProject.id               = uuidv1();
			newProject.user             = req.user.local.email;
			newProject.name             = req.body.name;
			newProject.public           = false;
			newProject.description      = req.body.description;
			newProject.model            = ""; //empty project
			newProject.modelDSL         = "";
			newProject.compiled         = false;
			newProject.running          = false;
			// save the user
			newProject.save(function(err) {
				if (err)
					throw err;
				//redirect to new page
				res.redirect('/private/project?id='+newProject.id);
				return null;
			});
		});

	});  


	// =====================================
	// PROJECT INFO ========================
	// =====================================
	app.get('/private/project', isLoggedIn, function(req, res) {
		Project.findOne({ 'id' :  req.query.id }, function(err, project) { 

			if(project==null){
				//error
			}
			// get user model
			res.render('project_info', {
				user : req.user , // get the user out of session and pass to template,
				project : project,
				layout: 'dashboard'
			});
		});

	});

	// =====================================
	// DELETE PROJECT ======================
	// =====================================
	app.post('/private/delete_project', function(req, res) {

		/*
    Project.findOne({ 'id' :  req.query.id }, function(err, project) { 

      if(project==null){
        //error
      }
      // get user model
      res.redirect('/private/workspace/list');
    });*/

		Project.remove({ 'id' :  req.query.id }, function(err) {
			if (err) throw err;
			console.log("1 document deleted");
			res.redirect('/private/workspace/list');
		});

	});

	// =====================================
	// DSL  ================================
	// =====================================
	app.get('/private/edit_project', isLoggedIn, function(req, res) {

		Project.findOne({ 'id' :  req.query.id }, function(err, project) { 

			if(project==null){
				//error
				res.redirect('/dashboard');
			}
			// get user model
			res.render('edit_project', {
				user : req.user , // get the user out of session and pass to template,
				project : project,
				layout: 'dashboard'
			});
		});

	});


	// =====================================
	// DASHBOARD  ==========================
	// =====================================

	app.get('/private/dashboard',  function(req, res) {
		console.log(req.query.id );

		Project.findOne({ 'id' :  req.query.id }, function(err, project) { 

			if(project==null){
				//error
				res.redirect('/dashboard');
			}

			//read model
			var model= project.modelDSL;
			var array = JSON.parse(model)
			var index;
			var dashboardWidgets=[];
			for(index=0; index<array.length ; index++){
				if(array[index].url!=null && (array[index].type=="Results" || array[index].type=="Storage") ){

					//var param= array[index].url + '?';
					var widget= {url: array[index].url } ;
					widget["appId"]= project.id
					widget["name"]=array[index].nameId
					widget["socketId"]="";
					widget["projectId"]=project.id;
					for(var r=0; r< array[index].connections.length; r++ ){
						var socketId= project.id + '-' + array[index].connections[r].id;
						widget["socketId"]= widget["socketId"] + socketId + ','

					}
					if(array[index].configuration!=null)
						widget["configuration"]=JSON.stringify(array[index].configuration);

					dashboardWidgets.push(widget);
				}
			}
			//console.log(dashboardWidgets)

			// get user model
			res.render('dashboardProject', {
				user    : req.user , // get the user out of session and pass to template,
				project : project,
				widgets : dashboardWidgets,
				layout: 'dashboardMini'
			});
		});

	}); 


	// =====================================
	// DOWNLOAD FILE   =====================
	// =====================================
	app.get('/private/download_file', isLoggedIn, function(req, res) {
		//res.sendFile( path.resolve('src/app/index.html') );
		//res.sendFile( 'validator.html', { root: './public/' });
		var path= "./StreamingApplications/" + req.query.id + "/target/logs/" + req.query.filename + ".txt"
		res.download( path);

	});

	// =====================================
	// NEW CUSTOM PROCESSOR ================
	// =====================================

	app.get('/public/customprocessor',  function(req, res) {



		// get user model
		res.render('customprocessor', {
			user    : req.user , // get the user out of session and pass to template,
			layout: 'dashboard'
		});


	}); 

	// =====================================
	// NEW CUSTOM PROCESSOR ================
	// =====================================

	app.get('/public/newdatasource',  function(req, res) {



		// get user model
		res.render('addapi', {
			user    : req.user , // get the user out of session and pass to template,
			layout: 'dashboard'
		});


	}); 

	//update personal data
	app.post('/update_newdatasource', function(req, res) {
		/*
		// Get POST values, It's easy
		var userName = req.body.name;
		var lastName= req.body.lastname;
		var email = req.body.email;      
		User.findOne({ 'local.email' :  email }, function(err, user) {
			user.local.name= userName;
			user.local.lastname=lastName;         
			// save the user
			user.save(user=function(err) {
				if (err)
					throw err;
				return;
			});      
		});
		//get user updated model
		// get user model
		req.user.local.name=userName;
		req.user.local.lastname=lastName;
		res.render('profile', {
			user : req.user , // get the user out of session and pass to template,
			message : "Profile updated",   
			layout: 'dashboard'
		}); */
		var json= JSON.parse(req.body.message);
		console.log(json.name);
		APIdatabase.find({}, function(err, list) {

			var api              = new APIdatabase();
			api.name = json.name;
			api.description = json.name;
            api.params = json.params;
            api.url = json.url;
  			api.protocol = json.protocol;
  			api.values = json.values;
			api.type = json.type;
			api.imageUrl = json.imageUrl;
			api.auth = json.auth;
			if(json.rate!=null)
				api.rate="";
			api.acessKind= json.acessKind;
			console.log(api)
			//save
				
			api.save(function(err) {
				if (err)
					throw err;
				//redirect to new page
				res.redirect('dashboard');
				return null;
			}); 

		});


	});

	// =====================================
	// SERVICES        =====================
	// =====================================
	app.get('/services/createproject',  function(req, res) {


		//console.log(req._parsedOriginalUrl.query)
		var params= getParamsUrlToArray(req._parsedOriginalUrl.query)
		console.log(params["title"])

		// create Project
		var newProject              = new Project();
		newProject.dateCreated      = getDateTime();
		newProject.dateLastModified = getDateTime();
		newProject.id               = uuidv1();
		newProject.user             = params["email"];
		newProject.name             = "BOT PROJECT: " +params["title"] ;
		newProject.public           = false;
		newProject.description      = "testing";
		newProject.model            = ""; //empty project
		newProject.modelDSL         = "";
		newProject.compiled         = false;
		newProject.running          = false;


		// save the user
		newProject.save(function(err) {
			if (err)
				throw err;
			//redirect to new page
			//res.redirect('/private/project?id='+newProject.id);
			res.send(newProject.id);
			return null;
		});


	});


	app.get('/services/saveproject',  function(req, res) {

		const fs = require('fs');
		var params= getParamsUrlToArray(req._parsedOriginalUrl.query)
		var appid= params["appid"];
		var model = params["xmi"];
		model = decodeURIComponent(model);
		//model = model.replace(/+/g, " ");



		//save xmi in disk
		fs.writeFile("C:\\Users\\taray\\Desktop\\eclipse\\workspace\\PracticaAcceleo\\sara.xmi", model, function(err) {
			if(err) {
				return console.log(err);
			}

			console.log("The file was saved!");

			// call transformation
			var command=  "cd C:\\Users\\taray\\Desktop\\eclipse\\workspace\\PracticaAcceleo\\ && java -jar transformacion.jar sara.xmi ." ;
			var exec = require('child_process').exec;
			exec(command, function(error, stdout, stderr) {
				console.log(error);
				console.log(stdout);
				console.log(stderr);

				//read file and save in database
				fs.readFile('C:\\Users\\taray\\Desktop\\eclipse\\workspace\\PracticaAcceleo\\out.txt', 'utf8', function(err, contents) {
					//console.log(contents);
					Project.findOne({ 'id' :  appid }, function(err, project) { 
						project.model=contents;
						project.save(function(err) {
							if (err)
								throw err;
							console.log("app model saved in db");
							return;
						});   
					});

				});
				//read file and save in database
				fs.readFile('C:\\Users\\taray\\Desktop\\eclipse\\workspace\\PracticaAcceleo\\out2.txt', 'utf8', function(err, contents) {
					//console.log(contents);
					Project.findOne({ 'id' :  appid }, function(err, project) { 
						project.modelDSL=contents;
						project.save(function(err) {
							if (err)
								throw err;
							console.log("app model saved in db");
							return;
						});   
					});

				});

				// rename out.txt to appid.txt
				var command=  "cd C:\\Users\\taray\\Desktop\\eclipse\\workspace\\PracticaAcceleo\\ && rename out.txt " + appid +".txt"  ;
				var exec = require('child_process').exec;
				exec(command, function(error, stdout, stderr) {

					// move file to datalyzer folder
					var command="cd C:\\Users\\taray\\Desktop\\eclipse\\workspace\\PracticaAcceleo\\ && move "+appid +".txt C:\\Users\\taray\\Documents\\keystone\\datalyzer\\files";
					var exec = require('child_process').exec;
					exec(command, function(error, stdout, stderr) {

						//copye template folder
						var copyFolder="xcopy \"C:\\Users\\taray\\Documents\\keystone\\datalyzer\\StreamingApplications\\KafkaProjectGeneratedTemplate\" \"C:\\Users\\taray\\Documents\\keystone\\datalyzer\\StreamingApplications\\" + appid + "\" /s /e /y /i";
						exec(copyFolder, function(error, stdout, stderr) {

							// detele .java files if exist
							var deleteFiles = "cd C:\\Users\\taray\\Documents\\keystone\\datalyzer\\StreamingApplications\\" + appid + "\\src\\main\\java\\com && del /S .\\* /Q";
							var exec2 = require('child_process').exec;
							exec2(deleteFiles, function(error, stdout, stderr) {

								//generate .java files
								var generateFiles=  "cd CodeGeneration\\ && java -jar CodeGeneration.jar C:\\Users\\taray\\Documents\\keystone\\datalyzer" + " " + appid + "";
								var exec3 = require('child_process').exec;
								exec3(generateFiles, function(error, stdout, stderr) {
									console.log(error);
									console.log(stdout);
									console.log(stderr);

									// compile project
									var compile = cmd = "cd  C:\\Users\\taray\\Documents\\keystone\\datalyzer\\StreamingApplications\\"+appid  +" && mvn compile assembly:single";
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
										//io.emit("CodeGenerated", 'OK');
										res.send("OK");
									});		 

								});
							});
						});

					});
				});


			});

		}); 

	} );


	app.get('/services/startproject',  function(req, res) {
		//res.send("OK");
		//return;

		//console.log(req._parsedOriginalUrl.query)
		var params= getParamsUrlToArray(req._parsedOriginalUrl.query)
		var appid= params["appid"];
		console.log(params["appid"])
		var exec = require('child_process').exec;
		var cmd = "";
		cmd = "cd C:\\Users\\taray\\Documents\\keystone\\datalyzer\\StreamingApplications\\" +appid + "\\target\\ && java -jar KafkaAPIClient-1.0-SNAPSHOT-jar-with-dependencies.jar";
		//return;
		exec(cmd, function(error, stdout, stderr) {
			console.log(stdout);
			console.log(error);
			console.log(stderr);
			res.send("OK");
		}); 


	});


	app.get('/services/testing',  function(req, res) {
		res.send("Hello world!");
	});

	app.get('/services/createcustomprocessor',  function(req, res) {


		//console.log(req._parsedOriginalUrl.query)
		var params= getParamsUrlToArray(req._parsedOriginalUrl.query)
		var json = JSON.parse(decodeURIComponent(params["json"]));
		var parameters = []
		var borrar = {name: "hola", paramType : "int"}
		parameters.push(borrar);


		var newProcessor = new Processor();
		newProcessor.name = json.name;
		newProcessor.description = json.description;
		newProcessor.code = encodeURIComponent(json.codeProcess);
		newProcessor.parameters = json.parameters;
		console.log(newProcessor.parameters)
		newProcessor.save(function(err) {
			if (err)
				throw err;
			//redirect to new page
			//res.redirect('/private/project?id='+newProject.id);
			res.send("OK");
			return null;
		});
		/*
		// create Project
		var newProject              = new Project();
		newProject.dateCreated      = getDateTime();
		newProject.dateLastModified = getDateTime();
		newProject.id               = uuidv1();
		newProject.user             = params["email"];
		newProject.name             = "BOT PROJECT: " +params["title"] ;
		newProject.public           = false;
		newProject.description      = "testing";
		newProject.model            = ""; //empty project
		newProject.modelDSL         = "";
		newProject.compiled         = false;
		newProject.running          = false;


		// save the user
		newProject.save(function(err) {
			if (err)
				throw err;
			//redirect to new page
			//res.redirect('/private/project?id='+newProject.id);
			res.send(newProject.id);
			return null;
		});
		*/
		//res.send("OK");

	});

};




// route middleware to make sure
function isLoggedIn(req, res, next) {

	// if user is authenticated in the session, carry on
	if (req.isAuthenticated())
		return next();

	// if they aren't redirect them to the home page
	res.redirect('/');
}



// get server time
function getDateTime() {

	var date = new Date();

	var hour = date.getHours();
	hour = (hour < 10 ? "0" : "") + hour;

	var min  = date.getMinutes();
	min = (min < 10 ? "0" : "") + min;

	var sec  = date.getSeconds();
	sec = (sec < 10 ? "0" : "") + sec;

	var year = date.getFullYear();

	var month = date.getMonth() + 1;
	month = (month < 10 ? "0" : "") + month;

	var day  = date.getDate();
	day = (day < 10 ? "0" : "") + day;

	return day + "/" + month + "/" + year + "  " + hour + ":" + min + ":" + sec;

}



function getParamsUrlToArray(params){

	var array= [];
	var paramsArray = params.split("&");
	for(var i=0 ; i<paramsArray.length ; i++){
		array[paramsArray[i].split("=")[0]]=paramsArray[i].split("=")[1]
	}
	return array;


}














/*
var express = require('express');
var router = express.Router();

var passport = require('passport');


// ********** GET  *************
router.get('/', function(req, res, next) {
  res.render('index', { title: 'Datalyzer' });
});

router.get('/signup', function(req, res) {
    res.render('signup', {welcome: 'Hey'});
});

router.get('/login', function(req, res) {
    res.render('login', {welcome: 'Hey'});
});


//******** POST ********* 
router.post("/login", passport.authenticate('local-login', {
		successRedirect : '/profile', // redirect to the secure profile section
		failureRedirect : '/login', // redirect back to the signup page if there is an error
		failureFlash : true // allow flash messages
	}));

module.exports = router;

 */
