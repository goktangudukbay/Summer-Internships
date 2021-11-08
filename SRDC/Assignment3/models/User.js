const mongoose = require('mongoose');
const Schema = mongoose.Schema;
mongoose.set('useFindAndModify', false);
// Define collection and schema

let Message = new Schema({
	sender: {
		type: String
	},
	receiver: {
		type: String
	},
	 time: {
      type: Date
   },
    title: {
      type: String
   },
    message: {
      type: String
   }
})


let User = new Schema({
   username: {
      type: String
   },
   firstname: {
      type: String
   },
   lastname: {
      type: String
   },
   birthdate: {
      type: Date
   },
   email: {
	  type: String
   },
   gender: {
	  type: String
   },
   password: {
	  type: String
   },
   isAdmin: {
      type: Boolean
   },
   authToken: {
	  type: String
   }
}, {
   collection: 'users'
})

	
module.exports = mongoose.model('User', User)