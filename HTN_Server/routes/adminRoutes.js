const express = require('express')
const adminController = require('../controllers/adminController')
const adminRouter = express.Router()

adminRouter.post('/sign-in',adminController.signin)
adminRouter.post('/sign-up',adminController.signup)
adminRouter.post('/update-parking', adminController.adminUpdateParkingSpot)
adminRouter.post('/update-parking-lock', adminController.adminUpdateParkingSpotBlock)
adminRouter.get('/reports', adminController.getReport)
adminRouter.get('/parking-status-ad', adminController.getParkingStatusAd)
adminRouter.post('/parkingReportAd', adminController.adminUpdateParkingReport)
adminRouter.post('/update-report-sta', adminController.adminUpdateReportStatus)
module.exports = adminRouter;
