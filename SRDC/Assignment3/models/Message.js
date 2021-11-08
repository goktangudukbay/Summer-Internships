const mongoose = require('mongoose');
const Schema = mongoose.Schema;

// Define collection and schema
let Message = new Schema({
   sender_id: {
      type: Schema.ObjectId,
	  ref: 'User'
   },
   receiver_id: {
      type: Schema.ObjectId,
	  ref: 'User'
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
}, {
   collection: 'messages'
})

module.exports = mongoose.model('Message', Message)