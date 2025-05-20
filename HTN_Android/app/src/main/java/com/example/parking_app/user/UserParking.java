package com.example.parking_app.user;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.parking_app.Network.ApiService;
import com.example.parking_app.ParkingSpot;
import com.example.parking_app.R;


import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.List;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserParking extends AppCompatActivity {
    ImageButton back;
    ImageView warning1, warning2, warning3, warning4;
    ImageView status1, status2, status3, status4;

    Socket mSocket;
    private Handler handler;
    private final int REFRESH_INTERVAL = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_parking);


        back = findViewById(R.id.usr_parking_back);
        warning1 = findViewById(R.id.p1_warn);
        warning2 = findViewById(R.id.p2_warn);
        warning3 = findViewById(R.id.p3_warn);
        warning4 = findViewById(R.id.p4_warn);

        status1 = findViewById(R.id.p1_sta);
        status2 = findViewById(R.id.p2_sta);
        status3 = findViewById(R.id.p3_sta);
        status4 = findViewById(R.id.p4_sta);

        back.setOnClickListener(view -> finish());

//        try {
//            mSocket = IO.socket("https://a1cc-1-53-82-235.ngrok-free.app"); // Thay bằng URL của server
//            mSocket.connect();
//
//            // Lắng nghe sự kiện từ server
//            mSocket.on("parkingStatusUpdated", onParkingStatusUpdated);
//        } catch (URISyntaxException e) {
//            Log.e("Socket.IO", "Socket Initialization Error: " + e.getMessage());
//        }
        handler = new Handler(Looper.getMainLooper());
        startRealtimeUpdates(); // Bắt đầu cập nhật theo thời gian thực
    }
    private final Emitter.Listener onParkingStatusUpdated = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(() -> {
                try {
                    JSONObject data = (JSONObject) args[0];
                    String spotID = data.getString("spotID");
                    boolean isOccupied = data.getBoolean("isOccupied");
                    boolean isLocked = data.getBoolean("isLocked");
                    boolean isReported = data.getBoolean("isReported");

                    // Cập nhật trạng thái giao diện
                    switch (spotID) {
                        case "spot1":
                            updateParkingView(status1, warning1, isOccupied, isLocked, isReported);
                            break;
                        case "spot2":
                            updateParkingView(status2, warning2, isOccupied, isLocked, isReported);
                            break;
                        case "spot3":
                            updateParkingView(status3, warning3, isOccupied, isLocked, isReported);
                            break;
                        case "spot4":
                            updateParkingView(status4, warning4, isOccupied, isLocked, isReported);
                            break;
                    }
                } catch (JSONException e) {
                    Log.e("Socket.IO", "JSON Parsing Error: " + e.getMessage());
                }
            });
        }
    };

    private void updateStatus() {
        ApiService.apiService.getParkingStatus().enqueue(new Callback<List<ParkingSpot>>() {
            @Override
            public void onResponse(Call<List<ParkingSpot>> call, Response<List<ParkingSpot>> response) {
                if(response.isSuccessful()&& response.body()!=null){
                    List<ParkingSpot> parkingSpots = response.body();
                    for(ParkingSpot spot: parkingSpots){
                        switch (spot.getSpotID()){
                            case "spot1":
                                updateParkingView(status1, warning1, spot.getIsOccupied(), spot.getIsLocked(), spot.getIsReported());
                                break;
                            case "spot2":
                                updateParkingView(status2, warning2, spot.getIsOccupied(), spot.getIsLocked(), spot.getIsReported());
                                break;
                            case "spot3":
                                updateParkingView(status3, warning3, spot.getIsOccupied(), spot.getIsLocked(), spot.getIsReported());
                                break;
                            case "spot4":
                                updateParkingView(status4, warning4, spot.getIsOccupied(), spot.getIsLocked(), spot.getIsReported());
                                break;
                        }

                    }
                }else{
                    Toast.makeText(UserParking.this, "Failed to get parking status", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<ParkingSpot>> call, Throwable t) {

            }
        });

    }
    private void updateParkingView(ImageView statusView, ImageView warningView, Boolean isOccupied, Boolean isLocked, Boolean isReport ){
        if(isOccupied && !isLocked){
            statusView.setVisibility(View.VISIBLE);
            statusView.setImageResource(R.drawable.car);
        }else if (!isOccupied && !isLocked){
            statusView.setVisibility(View.INVISIBLE);
        }
        if(isLocked && !isOccupied){
            statusView.setVisibility(View.VISIBLE);
            statusView.setImageResource(R.drawable.lock);
        }
        else if (!isOccupied && !isLocked){
            statusView.setVisibility(View.INVISIBLE);
        }
        if(isReport){
            warningView.setVisibility(View.VISIBLE);
        }else if (!isLocked){
            warningView.setVisibility(View.INVISIBLE);
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