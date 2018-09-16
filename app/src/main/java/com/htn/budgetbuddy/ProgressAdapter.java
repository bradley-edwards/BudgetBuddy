package com.htn.budgetbuddy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.htn.budgetbuddy.utils.TinyDB;

import java.util.List;

public class ProgressAdapter extends BaseAdapter {

    private final Context context;
    private final List<String> items;
    private TinyDB tinyDB;

    public ProgressAdapter(Context context, List<String> items) {
        this.context = context;
        this.items = items;
        tinyDB = new TinyDB(context);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder {
        TextView name;
        ProgressBar bar;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listview_home, null);
            holder = new ViewHolder();

            holder.name = convertView.findViewById(R.id.listview_suggest_text);
            holder.bar = convertView.findViewById(R.id.ProgressBar);

            String namePos = items.get(position);

            holder.name.setText(namePos);
            if (position == 0) {
                holder.name.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_local_dining, 0, 0, 0);
                holder.bar.setProgress((int) ((tinyDB.getDouble("Food and Dining", 0) / tinyDB.getDouble("foodBudget", 1)) * 100));
                System.out.println((int) ((tinyDB.getDouble("Food and Dining", 0) / tinyDB.getDouble("foodBudget", 1)) * 100));
            } else if (position == 1) {
                holder.name.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_shopping_basket, 0, 0, 0);
                holder.bar.setProgress((int) ((tinyDB.getDouble("Home", 0) / tinyDB.getDouble("shoppingBudget", 1)) * 100));
            } else if (position == 2) {
                holder.name.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_videogame_asset, 0, 0, 0);
                holder.bar.setProgress((int) ((tinyDB.getDouble("Entertainment", 0) / tinyDB.getDouble("entertainmentBudget", 1)) * 100));
            } else if (position == 3) {
                holder.name.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_directions_transit, 0, 0, 0);
                holder.bar.setProgress((int) ((tinyDB.getDouble("Auto and Transport", 0) / tinyDB.getDouble("transportationBudget", 1)) * 100));

            }
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        return convertView;
    }
}