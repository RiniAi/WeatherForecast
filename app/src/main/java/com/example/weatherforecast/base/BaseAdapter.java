package com.example.weatherforecast.base;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherforecast.models.Daily;
import com.example.weatherforecast.models.Hourly;

import java.util.List;

public abstract class BaseAdapter<T, VH extends BaseViewHolder> extends RecyclerView.Adapter<VH> {
    private Context context;
    private List<T> list;

    public BaseAdapter(Context context) {
        this.context = context;
    }

    // TODO: when implementing dependencies for the Adapter-Fragment, without this method, the design is lost (need context of getActivity)
    public void setContext (Context context) {
        this.context = context;
    }

    public void setList(List<T> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public List<T> getList() {
        return list;
    }

    public T getItem(int position) {
        return list.isEmpty() ? null : list.get(position);
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public Context getContext() {
        return context;
    }

    public interface OnItemTodayHourlyClickListener {
        void onItemClick(Hourly hourly);
    }

    public interface OnItemTodayDailyClickListener {
        void onItemClick(Daily daily);
    }
}
