package com.safe_keep.app;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * ViewHolder for the Calendar RecyclerView.
 */
public class CalendarViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public final TextView dayOfMonth; // TextView to display the day of the month
    private final CalendarAdapter.OnItemListener onItemListener; // Listener for item click events

    /**
     * Constructor for the ViewHolder.
     * @param itemView The view for each item in the RecyclerView
     * @param onItemListener Listener for item click events
     */
    public CalendarViewHolder(@NonNull View itemView, CalendarAdapter.OnItemListener onItemListener) {
        super(itemView);
        dayOfMonth = itemView.findViewById(R.id.cellDayText);
        this.onItemListener = onItemListener;
        // Set click listener to handle item clicks
        itemView.setOnClickListener(this);
    }

    /**
     * Called when a view has been clicked.
     * @param view The view that was clicked.
     */
    @Override
    public void onClick(View view) {
        // Notify the listener of the item click event
        onItemListener.onItemClick(getAdapterPosition(), (String) dayOfMonth.getText());
    }
}
