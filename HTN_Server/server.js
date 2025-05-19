const express  = require('express')
const mongoose  = require('mongoose')
const cors  = require('cors')
const database = require( './config/database');
const adminRouter = require('./routes/adminRoutes');
const parkingRouter = require('./routes/parkingRoutes');
const reportRouter = require('./routes/reportRoutes');
const userRouter = require('./routes/userRoutes');
require('dotenv').config();
const http = require('http');
const { Server } = require('socket.io')

const app = express();
app.use(express.json());

app.use((req,res, next) => [
    res.setHeader('Access-Control-Allow-Origin', '*'),
    res.setHeader('Access-Control-Allow-Methods', 'GET, POST, PUT, DELETE'),
    res.setHeader('Access-Control-Allow-Headers', 'Content-Type, Authorization'),
    next()
]);
database.connect();
mongoose.set('strictQuery', true);

const server = http.createServer(app);
const io = new Server(server);

app.use((req, res, next) => {
    req.io = io; // Gắn đối tượng io vào request
    next();
  });

io.on('connection', (socket)=>{
    console.log('A client connected');
    socket.on('disconnect', ()=>{
        console.log('A client disconnect');
    });
});



app.use('/api/admin', adminRouter)
app.use('/api/parking', parkingRouter)
app.use('/api/report', reportRouter)
app.use('/api/user', userRouter)

app.listen(process.env.PORT, ()=>{
    console.log(`Server is running on port ${process.env.PORT}`);

})
module.exports = io;

