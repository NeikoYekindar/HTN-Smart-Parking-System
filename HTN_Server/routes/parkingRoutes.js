const express = require('express');
const parkingRouter = express.Router();
const parkingController = require('../controllers/parkingController');


parkingRouter.post('/update', parkingController.updateParking);
parkingRouter.get('/parkingGetStatus', parkingController.getParkingStatus);
module.exports = parkingRouter;