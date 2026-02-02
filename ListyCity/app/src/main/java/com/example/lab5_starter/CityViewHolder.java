package com.example.lab5_starter;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class CityViewHolder extends RecyclerView.ViewHolder {

    TextView nameText;
    TextView provinceText;

    public CityViewHolder(View itemView, CityAdapter.OnCityClickListener listener) {
        super(itemView);

        nameText = itemView.findViewById(R.id.textCityName);
        provinceText = itemView.findViewById(R.id.textCityProvince);

        itemView.setOnClickListener(v -> {
            int pos = getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                listener.onCityClick(pos);
            }
        });
    }
}
