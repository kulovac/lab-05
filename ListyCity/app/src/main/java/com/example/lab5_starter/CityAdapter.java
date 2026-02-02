package com.example.lab5_starter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CityAdapter extends RecyclerView.Adapter<CityViewHolder> {

    public interface OnCityClickListener {
        void onCityClick(int position);
    }

    private final ArrayList<City> items;
    private final OnCityClickListener listener;

    public CityAdapter(ArrayList<City> data, OnCityClickListener listener) {
        this.items = data;
        this.listener = listener;
    }

    @Override
    public CityViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_city, parent, false);
        return new CityViewHolder(v, listener);
    }

    @Override
    public void onBindViewHolder(CityViewHolder holder, int position) {
        City city = items.get(position);
        holder.nameText.setText(city.getName());
        holder.provinceText.setText(city.getProvince());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
