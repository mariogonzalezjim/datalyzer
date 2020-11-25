// app/models/user.js
// load the things we need
var mongoose = require('mongoose');
var bcrypt   = require('bcrypt-nodejs');

// define the schema for our user model
var processorSchema = mongoose.Schema({

        name        : String,
        description : String,
        code        : String,
		parameters  : [{ name: String, paramType: String, value : String }]
  
});

// methods ======================
processorSchema.methods.createProcessor = function(req) {
  //console.log("estoy aqui")
  
  return null;
};

// create the model for users and expose it to our app
module.exports = mongoose.model('Processor', processorSchema);
