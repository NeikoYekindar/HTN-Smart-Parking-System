package com.example.parking_app.admin;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.parking_app.Network.ApiService;
import com.example.parking_app.ParkingSpot;
import com.example.parking_app.R;

import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminParking extends AppCompatActivity {
    ImageButton back;
    Switch swt1, swt2, swt3, swt4;
    private Handler handler;
    ImageView status1, status2, status3, status4;

    private final int REFRESH_INTERVAL = 5000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_parking);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
        back = findViewById(R.id.ad_parking_back);
        swt1 = findViewById(R.id.swt1);
        swt2 = findViewById(R.id.swt2);
        swt3 = findViewById(R.id.swt3);
        swt4 = findViewById(R.id.swt4);
        status1 = findViewById(R.id.p1_sta_ad);
        status2 = findViewById(R.id.p2_sta_ad);
        status3 = findViewById(R.id.p3_sta_ad);
        status4 = findViewById(R.id.p4_sta_ad);


        back.setOnClickListener(view -> {
            finish();
        });
        handler = new Handler(Looper.getMainLooper());
        startRealtimeUpdates();
        swt1.setOnCheckedChangeListener((buttonView, isChecked) -> updateParkingSpot("spot1", isChecked));
        swt2.setOnCheckedChangeListener((buttonView, isChecked) -> updateParkingSpot("spot2", isChecked));
        swt3.setOnCheckedChangeListener((buttonView, isChecked) -> updateParkingSpot("spot3", isChecked));
        swt4.setOnCheckedChangeListener((buttonView, isChecked) -> updateParkingSpot("spot4", isChecked));
    }
    public void updateParkingSpot(String spotID, Boolean isLocked){
        Boolean isOccupied = false;
        if(Objects.equals(spotID, "spot1")){
            status1.setVisibility(View.VISIBLE);
            status1.setImageResource(R.drawable.lock);
        }else if(Objects.equals(spotID, "spot2")){
            status2.setVisibility(View.VISIBLE);
            status2.setImageResource(R.drawable.lock);
        }else if(Objects.equals(spotID, "spot3")){
            status3.setVisibility(View.VISIBLE);
            status3.setImageResource(R.drawable.lock);
        }else if(Objects.equals(spotID, "spot4")){
            status4.setVisibility(View.VISIBLE);
            status4.setImageResource(R.drawable.lock);
        }
        ParkingSpot updateSpot = new ParkingSpot(spotID, isOccupied, isLocked, false);

        ApiService.apiService.UpdateParking(updateSpot).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AdminParking.this, "Parking spot " + spotID + " updated successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AdminParking.this, "Failed to update parking spot", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(AdminParking.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });


    }
    private void updateStatus(){
        ApiService.apiService.getParkingStatusAdmin().enqueue(new Callback<List<ParkingSpot>>() {
            @Override
            public void onResponse(Call<List<ParkingSpot>> call, Response<List<ParkingSpot>> response) {
                if(response.isSuccessful()&& response.body()!=null){
                    List<ParkingSpot> parkingSpots = response.body();
                    for(ParkingSpot spot: parkingSpots){
                        switch (spot.getSpotID()){
                            case "spot1":
                                updateParkingView(status1 , spot.getIsOccupied(), spot.getIsLocked());
                                swt1.setChecked(spot.getIsLocked());
                                break;
                            case "spot2":
                                updateParkingView(status2 , spot.getIsOccupied(), spot.getIsLocked());
                                swt2.setChecked(spot.getIsLocked());
                                break;
                            case "spot3":
                                updateParkingView(status3 , spot.getIsOccupied(), spot.getIsLocked());
                                swt3.setChecked(spot.getIsLocked());
                                break;
                            case "spot4":
                                updateParkingView(status4 , spot.getIsOccupied(), spot.getIsLocked());
                                swt4.setChecked(spot.getIsLocked());
                                break;
                        }

                    }
                }else{
                    Toast.makeText(AdminParking.this, "Failed to get parking status", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<ParkingSpot>> call, Throwable t) {

            }
        });
    }
    private void updateParkingView(ImageView statusView, Boolean isOccupied, Boolean isLocked ){
        if(isOccupied){
            statusView.setVisibility(View.VISIBLE);
            statusView.setImageResource(R.drawable.car);
        }else{
            statusView.setVisibility(View.INVISIBLE);
        }
        if(isLocked){
            statusView.setVisibility(View.VISIBLE);
            statusView.setImageResource(R.drawable.lock);
        }
    }



    private void startRealtimeUpdates() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateStatus();
                handler.postDelayed(this, REFRESH_INTERVAL);
            }
        }, REFRESH_INTERVAL);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }
}