const Report = require('../models/reportModel');

exports.createReport = async (req, res)=>{
    const {name, email, phone_number, date, parking, details, isCheck} = req.body;

    if (!name || !email || !phone_number || !date || !parking || !details) {
        return res.status(400).send('All fields are required');
    }
    try{
        const report  = new Report({
            name,
            email,
            phone_number,
            date,
            parking,
            details,
            isCheck
        });

        await report.save();

        res.status(201).send('Report created successfully');

        
    }catch(err){
        res.status(500).send('Error report created');

    }
};