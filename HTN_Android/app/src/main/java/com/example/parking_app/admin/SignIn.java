package com.example.parking_app.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.parking_app.Network.ApiService;
import com.example.parking_app.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignIn extends AppCompatActivity {
    ImageButton back;
    Button login;
    EditText IDinput;
    ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_in);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
        back =findViewById(R.id.sign_in_back);
        login = findViewById(R.id.signin);
        IDinput = findViewById(R.id.ID);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Signin();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    private void Signin(){
        String adminID = IDinput.getText().toString().trim();
        if (adminID.isEmpty() || adminID.length() != 6) {
            Toast.makeText(SignIn.this, "Please enter a valid 6-digit Admin ID", Toast.LENGTH_SHORT).show();
            return;
        }
        Admin admin  = new Admin(adminID);
        ApiService.apiService.loginAdmin(admin).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()){
                    Toast.makeText(SignIn.this, "Login Successfull", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SignIn.this, AdminDashboard.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(SignIn.this, "Invalid Admin ID", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(SignIn.this, "Login Fail", Toast.LENGTH_SHORT).show();

            }
        });

    }
}