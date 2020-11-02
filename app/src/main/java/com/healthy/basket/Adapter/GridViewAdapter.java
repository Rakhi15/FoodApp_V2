package com.healthy.basket.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.healthy.basket.Getset.menugetset;
import com.healthy.basket.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class GridViewAdapter extends RecyclerView.Adapter<GridViewAdapter.ViewHolder> {
    private List<menugetset> items;
    private Activity activity;
    GridItemClickListener clickListener;
    public GridViewAdapter(Activity activity, List<menugetset> items, GridItemClickListener clickListener) {
        this.activity = activity;
        this.items = items;
        this.clickListener = clickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.main_gridview_items, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(GridViewAdapter.ViewHolder viewHolder, final int position) {
//        viewHolder.imageView.setImageResource(items.get(position).getDrawableId());
        Picasso.with(activity).
                load(activity.getResources().getString(R.string.link) + activity.getString(R.string.imagepath) +items.get(position).getPhoto()).into(viewHolder.imageView);
        viewHolder.textView.setText(items.get(position).getName());
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onClickListener(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    /**
     * View holder to display each RecylerView item
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView textView;

        public ViewHolder(View view) {
            super(view);
            textView = (TextView)view.findViewById(R.id.fragGridViewItemsTitle);
            imageView = (ImageView) view.findViewById(R.id.fragGridViewItemsImage);
        }
    }
}