package com.htn.budgetbuddy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class SuggestionAdapter extends BaseAdapter {

    private final Context context;
    private final List<String> items;

    public SuggestionAdapter(Context context, List<String> items) {
        this.context = context;
        this.items = items;
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
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listview_suggestion, null);
            holder = new ViewHolder();

            holder.name = convertView.findViewById(R.id.text1);

            holder.name.setText(items.get(position));
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.name.setText(items.get(position));

        return convertView;
    }

    public void addItem(String item) {
        if (!items.contains(item)) {
            items.add(item);
            notifyDataSetChanged();
        }
    }
}