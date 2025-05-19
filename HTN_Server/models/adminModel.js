const mongoose   =require('mongoose')

const adminSchema  = new mongoose.Schema({
    adminID:{
        type: String,
        require: true,
        match: /^\d{6}$/,
        unique: true,
    }
})

const adminModel = mongoose.model('Admin', adminSchema);
module.exports = adminModel