package com.example.parking_app.admin;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parking_app.Network.ApiService;
import com.example.parking_app.R;
import com.example.parking_app.ReportAdapter;
import com.example.parking_app.ReportModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminReport extends AppCompatActivity {
    ImageButton back, reload;
    RecyclerView recyclerView;
    ReportAdapter reportAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_report);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
        back = findViewById(R.id.ad_report_back);
        reload = findViewById(R.id.reload_report);
        recyclerView = findViewById(R.id.recyclerView);
        back.setOnClickListener(view -> {
            finish();
        });
        reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getReport();
            }
        });
        getReport();


    }
    public void getReport(){
        ApiService.apiService.getReport().enqueue(new Callback<List<ReportModel>>() {
            @Override
            public void onResponse(Call<List<ReportModel>> call, Response<List<ReportModel>> response) {
                if(response.isSuccessful()&& response.body()!=null){
                    List<ReportModel> reportModels  = response.body();
                    showAdapter(reportModels);
                }
            }

            @Override
            public void onFailure(Call<List<ReportModel>> call, Throwable t) {

            }
        });
    }
    public void showAdapter( List<ReportModel> reportModels){
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        reportAdapter = new ReportAdapter(this,reportModels);
        recyclerView.setAdapter(reportAdapter);



    }
}