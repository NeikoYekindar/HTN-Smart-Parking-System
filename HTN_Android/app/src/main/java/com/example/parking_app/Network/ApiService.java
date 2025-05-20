package com.example.parking_app.Network;

import com.example.parking_app.ParkingSpot;
import com.example.parking_app.ReportModel;
import com.example.parking_app.admin.Admin;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {
    public static final String BASE_URL  = "https://d8a5-14-169-5-79.ngrok-free.app/api/";
    ApiService apiService = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService.class);
    @POST("admin/sign-in")
    Call<Void> loginAdmin(@Body Admin admin);

    @GET("user/parking-status")
    Call<List<ParkingSpot>>getParkingStatus();

    @POST("admin/update-parking-lock")
    Call<Void>UpdateParking(@Body ParkingSpot parkingSpot);

    @GET("admin/parking-status-ad")
    Call<List<ParkingSpot>>getParkingStatusAdmin();

    @POST("report/create")
    Call<Void>SendReport(@Body ReportModel reportModel);

    @GET("admin/reports")
    Call<List<ReportModel>>getReport();

    @POST("user/parking-report")
    Call<Void>SendParkingReport(@Body HashMap<String, Object> reportData);

    @POST("admin/parkingReportAd")
    Call<Void>SendParkingReportAd(@Body HashMap<String, Object> reportData);

    @POST("admin/update-report-sta")
    Call<Void>updateReportStatus(@Body ReportModel reportModel);

}
