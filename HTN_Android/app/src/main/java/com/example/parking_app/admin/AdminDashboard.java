package com.example.parking_app.admin;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.parking_app.R;

public class AdminDashboard extends AppCompatActivity {
    CardView parking, report;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_dashboard);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
        parking = findViewById(R.id.admin_parking_btn);
        report = findViewById(R.id.admin_report_btn);
        parking.setOnClickListener(view -> {
            Intent intent = new Intent(AdminDashboard.this, AdminParking.class);
            startActivity(intent);
        });
        report.setOnClickListener(view -> {
            Intent intent = new Intent(AdminDashboard.this, AdminReport.class);
            startActivity(intent);
        });
    }
}