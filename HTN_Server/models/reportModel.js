const mongoose = require('mongoose');
const reportSchema= new mongoose.Schema({
    name:{
        type: String,
        require: true,
    },
    email:{
        type: String,
        require: true,
    },
    phone_number:{
        type: String,
        require: true,
    },
    date:{
        type: String,
        require: true,
    },
    parking:{
        type: String,
        require: true,
    },
    details:{
        type: String,
        require: true,
    },
    isCheck:{
        type:Boolean,
        require: true,
        default: false
    }
})
module.exports = mongoose.model('Report', reportSchema)