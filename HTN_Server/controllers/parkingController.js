const ParkingSpot = require('../models/parkingModel')

exports.updateParking = async(req, res)=>{
    // const { parkingSpots} = req.body;
    // const io = req.io;

    // try{
    //     const updatePromises = parkingSpots.map(async(spot)=>{
    //         let parkingSpot = await ParkingSpot.findOne({spotID: spot.spotID});
    //         if(!parkingSpot){
    //             parkingSpot = new ParkingSpot({spotID: spot.spotID, isOccupied: spot.isOccupied, isLocked: spot.isLocked, isReported: spot.isReported});
    //         }else{
    //             parkingSpot.isOccupied = spot.isOccupied;
    //         }
    //         return  parkingSpot.save();
    //     });
    //     await Promise.all(updatePromises);

    //     io.emit('parkingStatusUpdated', parkingSpots);

    //     res.status(200).send('Parking spots updated successfully');

    // }catch (err){
    //     res.status(500).send('error updating parking spot');
    // }
    const { spotID,isOccupied } = req.body;
    const io = req.io;
    try{
        let parkingSpot = await ParkingSpot.findOne({spotID});
        if(!parkingSpot){
            return res.status(400).send('Parking spot not found');     
        }
        parkingSpot.isOccupied = isOccupied;
        await parkingSpot.save();
        
        io.emit('parkingStatusUpdatedReport', {spotID, isOccupied});
        res.status(200).send('Parking spot update report successfully by user');
    }catch(err){
        res.status(500).send('error updating report spot by user')
    }


};
exports.getParkingStatus = async (req, res) => {
    try {
      const parkingSpots = await ParkingSpot.find();
      res.status(200).json(parkingSpots);
    } catch (err) {
      res.status(500).send('Error retrieving parking status: ' + err.message);
    }
};
