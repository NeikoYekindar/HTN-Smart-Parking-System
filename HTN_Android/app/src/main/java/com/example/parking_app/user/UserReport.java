package com.example.parking_app.user;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.parking_app.Network.ApiService;
import com.example.parking_app.ParkingSpot;
import com.example.parking_app.R;
import com.example.parking_app.ReportModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserReport extends AppCompatActivity {
    Spinner parking_spinner;
    EditText edt_name, edt_email, edt_phone, edt_details;

    Button send;

    ImageButton back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_report);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });

        parking_spinner = findViewById(R.id.parking_spinner);
        edt_name = findViewById(R.id.edt_name);
        edt_email = findViewById(R.id.edt_email);
        edt_phone = findViewById(R.id.edt_phone);
        edt_details = findViewById(R.id.edt_details);
        send = findViewById(R.id.send);
        back = findViewById(R.id.usr_report_back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edt_name.getText().toString().isEmpty()||edt_email.getText().toString().isEmpty()||edt_phone.getText().toString().isEmpty()||edt_details.getText().toString().isEmpty()){
                    Toast.makeText(UserReport.this, "Please fill all the fields", Toast.LENGTH_LONG).show();
                }
                sendReport();
            }
        });
    }
    private void sendReport(){
        String name = edt_name.getText().toString();
        String email = edt_email.getText().toString();
        String phone = edt_phone.getText().toString();
        String parking = parking_spinner.getSelectedItem().toString();
        String details = edt_details.getText().toString();
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        ReportModel reportModel = new ReportModel(name, email, phone, date, parking, details, false);
        if(parking.equals("P1")){
            sendParkingReport("spot1");
        }else if(parking.equals("P2")){
            sendParkingReport("spot2");
        }else if(parking.equals("P3")){
            sendParkingReport("spot3");
        }else if(parking.equals("P4")){
            sendParkingReport("spot4");
        }
        ApiService.apiService.SendReport(reportModel).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Toast.makeText(UserReport.this, "Send report successfully", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });

    }
    public void sendParkingReport(String spot){
        HashMap<String, Object> reportData = new HashMap<>();
        reportData.put("spotID", spot);
        reportData.put("isReported", true);

        ApiService.apiService.SendParkingReport(reportData).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Toast.makeText(UserReport.this, "Send parking report successfully", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }
}