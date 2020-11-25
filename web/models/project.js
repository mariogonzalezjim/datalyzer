// app/models/user.js
// load the things we need
var mongoose = require('mongoose');
var bcrypt   = require('bcrypt-nodejs');

// define the schema for our user model
var projectSchema = mongoose.Schema({

        name        : String,
        id          : String, 
        description : String,
        model       : String,
        modelDSL    : String, 
        dateCreated : String,
        dateLastModified : String, 
        user        : String,
        public      : Boolean,
        compiled    : Boolean,
        running     : Boolean
  
});

// methods ======================
projectSchema.methods.createProject = function(req) {
  console.log("estoy aqui")
  
  return null;
};

// create the model for users and expose it to our app
module.exports = mongoose.model('Project', projectSchema);
