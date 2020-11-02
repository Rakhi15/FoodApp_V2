package com.healthy.basket.Adapter;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.healthy.basket.Getset.restaurentGetSet;
import com.healthy.basket.R;
import com.healthy.basket.activity.MainActivity;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class restaurentadapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_ITEM = 1;
    public final ArrayList<restaurentGetSet> moviesList;
    private final Activity activity;
    private OnLoadMoreListener onLoadMoreListener;
    private boolean isLoading;


    public restaurentadapter(RecyclerView recyclerView, Activity a, ArrayList<restaurentGetSet> moviesList) {
        activity = a;
        this.moviesList = moviesList;
        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visibleThreshold = linearLayoutManager.getChildCount();
                int totalItemCount = linearLayoutManager.getItemCount();
                int lastVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();
                final int lastItem = lastVisibleItem + visibleThreshold;

                Log.e("Check", isLoading + "  " + "Total Item Count " + totalItemCount + "lastVisibleItem " + lastVisibleItem + "visible threshold " + visibleThreshold);
                if (totalItemCount >= MainActivity.numberOfRecord) {
                    if (!isLoading && totalItemCount == (lastVisibleItem + visibleThreshold)) {
                        if (onLoadMoreListener != null) {
                            onLoadMoreListener.onLoadMore();
                        }
                        isLoading = true;

                    }
                }

            }
        });

    }

    public void addItem(ArrayList<restaurentGetSet> item, int position) {
        if (item.size() != 0) {
            moviesList.addAll(item);
            notifyItemInserted(position);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_home, parent, false);
            return new MyViewHolder(itemView);
        }
        throw new RuntimeException("there is no type that matches the type " + viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyViewHolder) {
            restaurentGetSet data = moviesList.get(position);
            Typeface openSansSemiBold = Typeface.createFromAsset(activity.getAssets(), "fonts/OpenSans-Semibold.ttf");
            ((MyViewHolder) holder).txt_name.setText((data.getName()));
            ((MyViewHolder) holder).txt_name.setTypeface(openSansSemiBold);
            if (data.getCategory() != null) {
                StringBuilder sb = new StringBuilder();
                for (String s : data.getCategory()) {
                    sb.append(s).append(", ");
                }
                String cat = sb.toString().replace("\"", "").replace("[", "").replace("]", "");
                if (cat.endsWith(", ")) {
                    cat = cat.substring(0, cat.length() - 2);
                }
                ((MyViewHolder) holder).txt_category.setText(cat);
            }

            ((MyViewHolder) holder).txt_distance.setText((data.getDelivery_time()).replace("MIN", "").replace("MIn", "").replace("Min", "").replace(" ", ""));
            ((MyViewHolder) holder).txt_rating.setText("" + Float.parseFloat(data.getRatting()));


            double distance = Double.parseDouble(data.getDistance());


            String status = data.getRes_status();
            Log.e("res_status", "" + status);
            if (status.equals("open")) {
                ((MyViewHolder) holder).time.setText("Opens at "+getTime12(data.getOpen_time())+" close at "+getTime12(data.getClose_time()));
                ((MyViewHolder) holder).time.setTextColor(Color.parseColor("#14B457"));
//
//                ((MyViewHolder) holder).txt_status.setText(activity.getString(R.string.open_till));
//                ((MyViewHolder) holder).txt_status.setTextColor(Color.parseColor("#14B457"));
            } else if (status.equals("closed")) {
                ((MyViewHolder) holder).time.setText("Opens at "+getTime12(data.getOpen_time())+" close at "+getTime12(data.getClose_time()));
                ((MyViewHolder) holder).time.setTextColor(Color.parseColor("#F73E46"));
//
//                ((MyViewHolder) holder).txt_status.setText(activity.getString(R.string.closetill));
//                ((MyViewHolder) holder).txt_status.setTextColor(Color.parseColor("#F73E46"));
            }

            String image = data.getImage().replace(" ", "%20");

            AlphaAnimation anim = new AlphaAnimation(0, 1);
            anim.setInterpolator(new LinearInterpolator());
            anim.setRepeatCount(Animation.ABSOLUTE);
            anim.setDuration(1000);
            Picasso.with(activity).load(activity.getResources().getString(R.string.link) + activity.getString(R.string.imagepath) + image).into(((MyViewHolder) holder).imageview);
            ((MyViewHolder) holder).imageview.startAnimation(anim);

            ((MyViewHolder) holder).txt_MimimmAmount.setText(moviesList.get(position).getCurrency() + " " + data.getDelivery_time());
        }


    }
    String  getTime12( String time){
       // final String time = "23:15";
        String date = "";
        try {
            final SimpleDateFormat sdf = new SimpleDateFormat("H:mm");
            final Date dateObj = sdf.parse(time);
            date = new SimpleDateFormat("hh:mm a").format(dateObj);
        } catch (final ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
        this.onLoadMoreListener = mOnLoadMoreListener;
    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }


    @Override
    public int getItemViewType(int position) {
        return TYPE_ITEM;
    }

    public void setLoaded() {
        isLoading = false;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        final TextView txt_name;
        final TextView txt_category;
        final TextView txt_distance;
        final TextView txt_rating;
        final TextView txt_status;
        final TextView time;
        final TextView txt_MimimmAmount;
        final ImageView imageview;

        MyViewHolder(View view) {
            super(view);
            txt_name = view.findViewById(R.id.txt_name);
            txt_category = view.findViewById(R.id.txt_category);
            txt_distance = view.findViewById(R.id.txt_distance);
            txt_rating = view.findViewById(R.id.txt_rating);
            txt_status = view.findViewById(R.id.txt_status);
            txt_MimimmAmount = view.findViewById(R.id.txt_MimimmAmount);
            imageview = view.findViewById(R.id.image);
            time = view.findViewById(R.id.time);
        }
    }


    public interface OnLoadMoreListener {
        void onLoadMore();
    }


}

