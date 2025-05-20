package com.example.parking_app;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parking_app.Network.ApiService;
import com.example.parking_app.admin.AdminParking;
import com.example.parking_app.user.UserReport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.MyViewHolder> {
    Context context;
    List<ReportModel> list;

    public ReportAdapter(Context context, List<ReportModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ReportAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.report_view, viewGroup, false);
        return new ReportAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportAdapter.MyViewHolder holder, int i) {
        holder.tvParking.setText(list.get(i).getParking());
        holder.tvAuthor.setText(list.get(i).getName());
        holder.tvDate.setText(list.get(i).getDate().toString());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvParking, tvDate, tvAuthor;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvParking = itemView.findViewById(R.id.rp_parking);
            tvAuthor = itemView.findViewById(R.id.rp_author);
            tvDate = itemView.findViewById(R.id.rp_date);
            itemView.setOnClickListener(this); // Đăng ký sự kiện click

        }

        @Override
        public void onClick(View v) {
            String spot;
            int position = getAdapterPosition();
            if(position !=  RecyclerView.NO_POSITION){
                ReportModel clickedItem = list.get(position);
                View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_custom, null);
                TextView dialog_name = dialogView.findViewById(R.id.dialog_name);
                TextView dialog_email = dialogView.findViewById(R.id.dialog_email);
                TextView dialog_parking = dialogView.findViewById(R.id.dialog_parking);
                TextView dialog_date = dialogView.findViewById(R.id.dialog_date);
                TextView dialog_detail = dialogView.findViewById(R.id.dialog_detail);
                Button accept  = dialogView.findViewById(R.id.accept);
                Button reject = dialogView.findViewById(R.id.reject);

                dialog_name.setText(clickedItem.getName());
                dialog_email.setText(clickedItem.getEmail());
                dialog_parking.setText(clickedItem.getParking());
                dialog_date.setText(clickedItem.getDate());
                dialog_detail.setText(clickedItem.getDetails());

                if (clickedItem.getIsCheck()){
                    accept.setVisibility(View.GONE);
                    reject.setVisibility(View.GONE);
                }

                if(clickedItem.getParking().equals("P1")){
                     spot = "spot1";
                }else if(clickedItem.getParking().equals("P2")){
                    spot = "spot2";
                }else if(clickedItem.getParking().equals("P3")){
                    spot = "spot3";
                }else if(clickedItem.getParking().equals("P4")){
                    spot = "spot4";
                } else {
                    spot = "";
                }


                AlertDialog dialog = new AlertDialog.Builder(context)
                        .setView(dialogView)
                        .create();

                accept.setOnClickListener(view->{
                    Toast.makeText(context, "Accepted", Toast.LENGTH_SHORT).show();
                    updateParkingLock(spot);
                    updateParkingReport(spot);
                    updateParkingReportCheck(clickedItem, true);
                    dialog.dismiss(); // Đóng dialog
                });
                reject.setOnClickListener(view -> {
                    Toast.makeText(context, "Rejected", Toast.LENGTH_SHORT).show();
                    updateParkingReport(spot);
                    updateParkingReportCheck(clickedItem, true);
                    dialog.dismiss(); // Đóng dialog
                });

                dialog.show();


            } else {
                spot = "";
            }
        }
        public void updateParkingLock (String spot){
            ParkingSpot updateSpot = new ParkingSpot(spot,false,true,false);
            ApiService.apiService.UpdateParking(updateSpot).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(context, "Parking spot " + spot + " updated successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Failed to update parking spot", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });
        }
        public void updateParkingReport (String spot){
            HashMap<String, Object> reportData = new HashMap<>();
            reportData.put("spotID", spot);
            reportData.put("isReported", false);

            ApiService.apiService.SendParkingReportAd(reportData).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    Toast.makeText(context, "Send parking report successfully", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {

                }
            });
        }
        public void updateParkingReportCheck (ReportModel ClickItem, Boolean isCheck){
            ReportModel reportModel = new ReportModel(ClickItem.getName(), ClickItem.getEmail(), ClickItem.getPhone_number(), ClickItem.getDate(), ClickItem.getParking(), ClickItem.getDetails(), isCheck);
            Toast.makeText(context, ClickItem.getName(), Toast.LENGTH_SHORT).show();

            ApiService.apiService.updateReportStatus(reportModel).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    Toast.makeText(context, "Update report successfully", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {

                }
            });
        }


    }
}
