package com.healthy.basket.timeline;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.healthy.basket.R;
import com.github.vipulasri.timelineview.TimelineView;



public class TimeLineViewHolder extends RecyclerView.ViewHolder {

    public final TextView mDate;
    public final TextView mMessage;
    public final TimelineView mTimelineView;

    public TimeLineViewHolder(View itemView, int viewType) {
        super(itemView);

        mDate = itemView.findViewById(R.id.text_timeline_date) ;
        mMessage = itemView.findViewById( R.id.text_timeline_title) ;
        mTimelineView=itemView.findViewById(R.id.time_marker);
        mTimelineView.initLine(viewType);
    }
}