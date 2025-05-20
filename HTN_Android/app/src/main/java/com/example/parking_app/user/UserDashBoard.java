package com.example.parking_app.user;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.parking_app.R;

public class UserDashBoard extends AppCompatActivity {
    CardView paking, report;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_dashboard);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
        paking = findViewById(R.id.usr_parking_btn);
        report = findViewById(R.id.usr_report_btn);
        paking.setOnClickListener(view -> {
            Intent intent = new Intent(UserDashBoard.this, UserParking.class);
            startActivity(intent);
        });
        report.setOnClickListener(view -> {
            Intent intent = new Intent(UserDashBoard.this, UserReport.class);
            startActivity(intent);
        });
    }
}