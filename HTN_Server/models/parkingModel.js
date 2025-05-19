const mongoose = require('mongoose');
const parkingSchema = new mongoose.Schema({
    spotID:{
        type: String,
        require: true,
        unique: true
    },
    isOccupied:{
        type: Boolean,
        require: true
    },
    isLocked:{
        type: Boolean,
        require: true,
        default: false, 
    },
    isReported: {
        type: Boolean,
        required: true,
        default: false,
    },
});
const ParkingSpot = mongoose.model('ParkingSpot',parkingSchema );
module.exports = ParkingSpot;
 