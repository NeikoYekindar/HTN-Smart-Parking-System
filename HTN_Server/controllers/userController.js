const ParkingSpot = require('../models/parkingModel')
const io = require('../server');

exports.getParkingStatus = async (req, res) => {
    try {
      const parkingSpots = await ParkingSpot.find();
      res.status(200).json(parkingSpots);
    } catch (err) {
      res.status(500).send('Error retrieving parking status: ' + err.message);
    }
  };
exports.reportParking = async(req, res)=>{
  const { spotID, isReported} = req.body;
  const io = req.io;
  try{
      let parkingSpot = await ParkingSpot.findOne({spotID});
      if(!parkingSpot){
          return res.status(400).send('Parking spot not found');     
      }
      parkingSpot.isReported = isReported;
      await parkingSpot.save();
      
      io.emit('parkingStatusUpdatedReport', {spotID, isReported});
      res.status(200).send('Parking spot update report successfully by user');
  }catch(err){
      res.status(500).send('error updating report spot by user')
  }
}

