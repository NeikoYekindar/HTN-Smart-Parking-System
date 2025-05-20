package com.example.parking_app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.parking_app.admin.SignIn;
import com.example.parking_app.user.UserDashBoard;

public class MainActivity extends AppCompatActivity {
    Button adminlogin, userlogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
        adminlogin = findViewById(R.id.admin_login);
        userlogin = findViewById(R.id.user_login);
        adminlogin.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, SignIn.class);
            startActivity(intent);
        });
        userlogin.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, UserDashBoard.class);
            startActivity(intent);
        });
    }
}