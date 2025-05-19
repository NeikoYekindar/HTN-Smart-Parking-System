const express = require('express');
const router = express.Router();
const userController = require('../controllers/userController');

router.get('/parking-status', userController.getParkingStatus);
router.post('/parking-report', userController.reportParking);
module.exports = router;
