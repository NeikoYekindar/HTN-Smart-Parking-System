const mongoose = require('mongoose');
const adminModel = require('../models/adminModel')
const ParkingSpot = require('../models/parkingModel')
const Report = require('../models/reportModel')

class adminController{
    static async signin(req, res){
        const {adminID} = req.body;
        try{
            const admin  = await adminModel.findOne({adminID});
            if(!admin){
                return res.status(400).send('Invalid ID');
            }

            res.send('Login successful');
        
        }catch(err){
            res.status(500).send('Error logging in: '+ err.message);

        }
    }
    static async signup(req, res){
        const { adminID } = req.body;
        
        if(!/^\d{6}$/.test(adminID)){
            return res.status(400).send('Admin ID must be exactly 6 digits');
        }
        let admin  = new adminModel({
            adminID,
        });
        try{
            admin = await admin.save();
            res.status(201).send('Admin registered successfully');
        }catch(err){
            res.status(400).send('Error registering admin: ' + err.message);
        }
    }
    static async adminUpdateParkingSpot(req,res){
        const { spotID, isOccupied, isLock, isReported} = req.body;
        const io = req.io;
        try{
            let parkingSpot = await ParkingSpot.findOne({spotID});
            if(!parkingSpot){
                return res.status(400).send('Parking spot not found');     
            }
            parkingSpot.isOccupied = isOccupied;
            await parkingSpot.save();

            io.emit('parkingStatusUpdated', {spotID, isOccupied, isLock, isReported});
            res.status(200).send('Parking spot update successfully by admin');
        }catch(err){
            res.status(500).send('error updating spot by admin')
        }
    }
    static async adminUpdateParkingSpotBlock(req, res){
        const {spotID, isOccupied, isLocked, isReported} = req.body;
        const io = req.io;
        try{
            let parkingSpot = await ParkingSpot.findOne({spotID});
            if(!parkingSpot){
                return res.status(400).send('Parking spot not found');     
            }
            parkingSpot.isOccupied = isOccupied;
            parkingSpot.isLocked = isLocked;
            
            await parkingSpot.save();

            io.emit('parkingStatusUpdatedLock', {spotID, isOccupied, isLocked, isReported});
            res.status(200).send('Parking spot update successfully by admin');
        }catch(err){
            res.status(500).send('error updating spot by admin')
        }
    }
    static async adminUpdateParkingReport(req, res){
        const { spotID, isReported} = req.body;
        const io = req.io;
        try{
            let parkingSpot = await ParkingSpot.findOne({spotID});
            if(!parkingSpot){
                return res.status(400).send('Parking spot not found');     
            }
            parkingSpot.isReported = isReported;
            await parkingSpot.save();
            
            io.emit('parkingStatusUpdatedReportAd', {spotID, isReported});
            res.status(200).send('Parking spot update report successfully by admin');
        }catch(err){
            res.status(500).send('error updating report spot by admin')
        }
    }
    static async adminUpdateReportStatus(req, res){
        const { name, email, phone_number, date, parking, details, isCheck } = req.body;
        const io = req.io;
        try{
            let report = await Report.findOne({ name, email, phone_number, date, parking, details });
            if(!report){
                return res.status(400).send('report not found');     
            }
            report.isCheck = isCheck;
            await report.save();
            
            io.emit('parkingStatusReportStatusAd', {name,email, phone_number, date, parking,details, isCheck});
            res.status(200).send('Update report successfully by admin');
        }catch(err){
            res.status(500).send('error updating report by admin')
        }
    }
    static async getReport(req,res){
        try{
            const reports = await Report.find();
            res.status(200).json(reports);
        }catch (err){
            res.status(500).send('Error retrieving reports: ' + err.message);
        }
    } 
    static async getParkingStatusAd(req, res){
        try {
          const parkingSpots = await ParkingSpot.find();
          res.status(200).json(parkingSpots);
        } catch (err) {
          res.status(500).send('Error retrieving parking status: ' + err.message);
        }
    }
    
}
module.exports = adminController