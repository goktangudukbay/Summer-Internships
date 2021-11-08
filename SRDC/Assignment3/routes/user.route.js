const express = require('express');
const crypto = require('crypto');
const jwt = require('jsonwebtoken');
const app = express();
const userRoute = express.Router();
var cors = require('cors');
app.use(cors());
dbConfig = require('C:/Program Files/angular-1.8.0/backend/database/db');

app.use(express.json());

var secretNumber = crypto.randomBytes(64).toString('hex');
// Models
let User = require('../models/User');
let Message = require('../models/Message');
let AccessLog = require('../models/AccessLog');






// Login Method(will return information of the user)
userRoute.route('/login').post((req, res, next) => {
  User.findOne({username: req.body.username, password: req.body.password}, (error, data) => {
    if (data == null || error) {
      console.log("cannot find");
	  return next(error);
    }
	else {
	  data.authToken = "";
	  let o = {"username":data.username, "isAdmin":data.isAdmin}
	  var accessToken = jwt.sign(o, secretNumber, {algorithm: "HS256"});
	  console.log(accessToken);
	  User.findOneAndUpdate({'username': req.body.username}, {authToken: accessToken}, {new: true},(err, result) => {
		result = result.toObject();
		console.log(result);
		res.json(result);
      })
    }
  })
})

// Get User Method(will return information of the user) *For Update
userRoute.route('/:username').get((req, res, next) => {
  var receivedToken = req.headers.authorization;
  let p = new Promise((resolve, reject) => {
	  if (receivedToken.startsWith("Bearer ")){
		 receivedToken = receivedToken.substring(7, receivedToken.length);
	  }
	User.findOne({authToken: receivedToken}, (error, data) => {
	if((data == null) || error){
		reject('failed');
		return next(error);
	}
	else
		resolve('success');
	})
  })
  
  p.then((message) => {
	  console.log(req.params.username);
  User.findOne({username: req.params.username}, (error, data) => {
    if (data == null || error) 
	  return next(error);
	else
		res.json(data);
    })
 }).catch((message) => {
	 return next(error);
 })
})



//Send Message (will return a message if it is succesful or not)
userRoute.route('/sendMessage').post((req, res, next) => {
  var receivedToken = req.headers.authorization;
  let p = new Promise((resolve, reject) => {
	  if (receivedToken.startsWith("Bearer ")){
		 receivedToken = receivedToken.substring(7, receivedToken.length);
	  }
	User.findOne({authToken: receivedToken}, (error, data) => {
	if((data == null) || error){
		reject('failed');
		return next(error);
	}
	else
		resolve('success');
	})
  })
  
  p.then((message) => {

	  var receiverId = -1, senderId = -1;
	  let p2 = new Promise((resolve, reject) => {
		  console.log(req.body.senderUsername + req.body.receiverUsername);
		  User.findOne({username: req.body.senderUsername}, (error, data) =>{
		  if(data != null )
			senderId = data._id;
		  else
			  reject('failed2');
		  })  
		  User.findOne({username: req.body.receiverUsername}, (error, data) =>{
		  if(data != null ){
			receiverId = data._id;  
			resolve('success');
		  }
		  else
			  reject('failed3');
		  })
	  })		  
	  p2.then((message) => {
		  		console.log(senderId  + receiverId);				  

		  if(receiverId == -1 || senderId == -1){
			res.status(401).send({ error: "Wrong sender or receiver." });
			console.log("Failed to send the message.");
		  }
		  else{
			  var dt = new Date();
			  var messageToPut = {
				sender_id: senderId,
				receiver_id: receiverId,
				time: dt,
				title: req.body.title, 
				message: req.body.message
			  };
			  
			  console.log(messageToPut.time);
			  Message.create(messageToPut, (error, data) => {
				  if(error)
					  return next(error);
				  else
					  res.json(data);
			  })
		  }
	  }).catch((message2) => {
		  res.status(500).send("Message was not sent.");
		console.log("catch2     "  + message2);
	   })
	
  }).catch((message) => {
	  	console.log("Token Error");
  })
})


//read inbox
userRoute.route('/inbox/:username').get((req, res, next) => {
	var receivedToken = req.headers.authorization;
	console.log(receivedToken);
	let p = new Promise((resolve, reject) => {
	  if (receivedToken.startsWith("Bearer ")){
		 receivedToken = receivedToken.substring(7, receivedToken.length);
	  }
	User.findOne({authToken: receivedToken}, (error, data) => {
	if((data == null) || error){
		reject('failed');
		return next(error);
	}
	else
		resolve('success');
	})
  })
  p.then((message) => {	
	console.log("akjhkhj");
	usernameOfUser = req.params.username;
	console.log(usernameOfUser)
	Message.aggregate([
	{ 
		$lookup:
		{
			from: 'users',
			localField: 'sender_id',
			foreignField: '_id',
			as: 'messageDetailsSender'
		}
	},
	{ $unwind: "$messageDetailsSender"},
	{
		$lookup:
		{
			from: 'users',
			localField: 'receiver_id',
			foreignField: '_id',
			as: 'messageDetailsReceiver'
		}
	},
	{$unwind: "$messageDetailsReceiver"},
	
	//condition
	{
		$match:{
			$and:[{"messageDetailsReceiver.username" : usernameOfUser}]
		}
	},
		
		
		//which fields
{ 
		$project:{
			_id : 1,
			senderUsername: "$messageDetailsSender.username",
			senderFirstName: "$messageDetailsSender.firstname",
			senderLastName: "$messageDetailsSender.lastname",
			receiverUsername: "$messageDetailsReceiver.username",
			receiverFirstName: "$messageDetailsReceiver.firstname",
			receiverLastName: "$messageDetailsReceiver.lastname",
			time: 1,
			title: 1,
			message: 1,
		}
	} 
	], (error, result) => {
		if(error)
			res.status(500).send("Error");
		else{
			res.status(200).json(result);
		}
	}
	)
  }).catch((message) => {
	  	console.log("Token error");
  })
})


//read outbox
userRoute.route('/outbox/:username').get((req, res, next) => {
	var receivedToken = req.headers.authorization;
	let p = new Promise((resolve, reject) => {
	  if (receivedToken.startsWith("Bearer ")){
		 receivedToken = receivedToken.substring(7, receivedToken.length);
	  }
	User.findOne({authToken: receivedToken}, (error, data) => {
	if((data == null) || error){
		reject('failed');
		return next(error);
	}
	else
		resolve('success');
	})
  })
  p.then((message) => {
	var usernameOfUser = req.params.username;
	console.log("slsamsa" + req.params.username);
	Message.aggregate([
	{ 
		$lookup:
		{
			from: 'users',
			localField: 'sender_id',
			foreignField: '_id',
			as: 'messageDetailsSender'
		}
	},
	{ $unwind: "$messageDetailsSender"},
	{
		$lookup:
		{
			from: 'users',
			localField: 'receiver_id',
			foreignField: '_id',
			as: 'messageDetailsReceiver'
		}
	},
	{$unwind: "$messageDetailsReceiver"},
	
	//condition
	{
		$match:{
			$and:[{"messageDetailsSender.username" : usernameOfUser}]
		}
	},
		
		
		//which fields
{ 
		$project:{
			_id : 1,
			senderUsername: "$messageDetailsSender.username",
			senderFirstName: "$messageDetailsSender.firstname",
			senderLastName: "$messageDetailsSender.lastname",
			receiverUsername: "$messageDetailsReceiver.username",
			receiverFirstName: "$messageDetailsReceiver.firstname",
			receiverLastName: "$messageDetailsReceiver.lastname",
			time: 1,
			title: 1,
			message: 1,
		}
	} 
	], (error, result) => {
		if(error)
			return next(error);
		else{
			res.json(result);
		}
	}
	)
  }).catch((message) => {
	  	console.log("Token error");
  })
})


// Add User
userRoute.route('/').post((req, res, next) => {
  var receivedToken = req.headers.authorization;
  console.log(receivedToken);
  console.log(req.body);
  	  console.log("adsdasaddssad");

 
  
  let p = new Promise((resolve, reject) => {
	  if (receivedToken.startsWith("Bearer ")){
		 receivedToken = receivedToken.substring(7, receivedToken.length);
	  }
	User.findOne({authToken: receivedToken}, (error, data) => {
	if((data == null) || error  || !data.isAdmin){
		reject('failed');
		return next(error);
	}
	else
		resolve('success');
	})
  })
  p.then((message) => {
	  var userToBeInserted = req.body;
	  userToBeInserted.birthdate = new Date(userToBeInserted.birthdate.substring(0, 10));
	  console.log("dsa" + userToBeInserted);
  User.create(userToBeInserted, (error, data) => {
    if (error) {
		return next(error);
    } else {
	  console.log("1kjasdkjaskj");
      res.json(data);
    }
  })
  }).catch((message) => {
	  console.log("2");
  })
})



//List Users
userRoute.route('/').get((req, res, next) => {
	var receivedToken = req.headers.authorization;
	var wrongToken = false;
	let p = new Promise((resolve, reject) => {
	  if (receivedToken.startsWith("Bearer ")){
		 receivedToken = receivedToken.substring(7, receivedToken.length);
	  }
	User.findOne({authToken: receivedToken}, (error, data) => {
	if((data == null) || error || !data.isAdmin){
		reject('failed');
		return next(error);
	}
	else
		resolve('success');
	})
  })
  
  p.then((Message) => {
	  var sort = {};
	  sort[req.query.sortField] = req.query.sortType;
	User.find((error, data) => {
    if (error) {
      return next(error);
    } else {
      res.json(data)
    }
	})
	  
  }).catch((Message) => {
		console.log("Token error");
  })
})


//Update User
userRoute.route('/').put((req, res, next) => {
	
	console.log("update girdi");
	var receivedToken = req.headers.authorization;
	let p = new Promise((resolve, reject) => {
	  if (receivedToken.startsWith("Bearer ")){
		 receivedToken = receivedToken.substring(7, receivedToken.length);
	  }
	User.findOne({authToken: receivedToken}, (error, data) => {
	if((data == null) || error  || !data.isAdmin){
		reject('failed');
		return next(error);
	}
	else
		resolve('success');
	})
  })
  
  p.then((Message) => {
	var userToBeInserted = req.body;
	userToBeInserted.birthdate = new Date(userToBeInserted.birthdate.substring(0, 10));  
	
	console.log(userToBeInserted);
	

	User.findOneAndUpdate({username: userToBeInserted.username}, {$set:userToBeInserted},
	function(error, result){
		if(error)
			return next(error);
		else{
			console.log("daskhjasdjkh");
			res.json(result);
			
		}
	});
  }).catch((Message) => {
	  	console.log("Token error");
  })
})

//Remove User
userRoute.route('/:username').delete((req, res, next) => {
	console.log("delete");
	var receivedToken = req.headers.authorization;
	console.log("aaja" + receivedToken);
	let p = new Promise((resolve, reject) => {
	  if (receivedToken.startsWith("Bearer ")){
		 receivedToken = receivedToken.substring(7, receivedToken.length);
	  }
	User.findOne({authToken: receivedToken}, (error, data) => {
	if((data == null) || error  || !data.isAdmin){
		reject('failed');
		return next(error);
	}
	else
		resolve('success');
	})
  })
  
  p.then((Message) => {
	User.deleteOne({ username: req.params.username}, (error, result) => {
		if(error)
			return next(error);
		else
			res.json(result);
  })
  }).catch((Message) => {
		console.log("Token error");
  })
})

//Post to access Log 
userRoute.route('/log').post((req, res, next) => {
	console.log("access");
  var receivedToken = req.headers.authorization;
  console.log(receivedToken);
  console.log(req.body);
  
  let p = new Promise((resolve, reject) => {
	  if (receivedToken.startsWith("Bearer ")){
		 receivedToken = receivedToken.substring(7, receivedToken.length);
	  }
	User.findOne({authToken: receivedToken}, (error, data) => {
	if((data == null) || error){
		reject('failed');
		return next(error);
	}
	else
		resolve('success');
	})
  })
  p.then((message) => {
  AccessLog.create(req.body, (error, data) => {
    if (error) {
		return next(error);
    } else {
      res.json(data);
    }
  })
  }).catch((message) => {
	  return next(error);
  })
})

//Get Access Log
userRoute.route('/log').get((req, res, next) => {
	var receivedToken = req.headers.authorization;
	var wrongToken = false;
	let p = new Promise((resolve, reject) => {
	  if (receivedToken.startsWith("Bearer ")){
		 receivedToken = receivedToken.substring(7, receivedToken.length);
	  }
	User.findOne({authToken: receivedToken}, (error, data) => {
	if((data == null) || error || !data.isAdmin){
		reject('failed');
		return next(error);
	}
	else
		resolve('success');
	})
  })
  
  p.then((Message) => {
	console.log("geldi");
	AccessLog.find((error, data) => {
    if (error) {
      return next(error);
    } else {
      res.json(data)
    }
	})
	  
  }).catch((Message) => {
		console.log("Token error");
  })
})



module.exports = userRoute;
