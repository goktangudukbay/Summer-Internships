const mongoose = require('mongoose');
const Schema = mongoose.Schema;

// Define collection and schema
let AccessLog = new Schema({
   username: {
      type: String
   },
   login_Time: {
	 type: Date
   },
   logout_Time: {
     type: Date
   },
   ip: {
      type: String
   },
   browser: {
	  type: String
   }
}, {
   collection: 'AccessLogs'
})

module.exports = mongoose.model('AccessLog', AccessLog)