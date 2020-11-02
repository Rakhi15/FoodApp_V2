package com.healthy.basket.Adapter;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.healthy.basket.Getset.MedicalOrderModel;
import com.healthy.basket.R;
import com.healthy.basket.utils.TouchImageView;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MedicalOrderAdapter extends RecyclerView.Adapter<MedicalOrderAdapter.MyViewHolder> {

    private List<MedicalOrderModel.UserDetail> items;
    private Activity activity;
    public MedicalOrderAdapter(Activity activity, List<MedicalOrderModel.UserDetail> items) {
        this.activity = activity;
        this.items = items;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.row_medical_order,parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Log.e("MedicalOrderAdapter",activity.getResources().getString(R.string.link) +
                activity.getString(R.string.imagepath) +items.get(position).getImage());
        Picasso.with(activity).
                load(activity.getResources().getString(R.string.link) +
                        activity.getString(R.string.imagepath) +items.get(position).getImage()).error(R.drawable.no_image_available).into(holder.imageView);
        String str = items.get(position).getDate();
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date(Long.parseLong(str)*1000);
        holder.date.setText(sf.format(date));
        holder.note.setText(items.get(position).getNote());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        private ImageView imageView;
        private TextView date;
        private TextView note;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.rowMedicalOrderImage);
            date = itemView.findViewById(R.id.rowMedicalOrderDate);
            note = itemView.findViewById(R.id.rowMedicalOrderNote);
        }
    }
}
