package com.htn.budgetbuddy;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.htn.budgetbuddy.models.Suggestion;
import com.htn.budgetbuddy.utils.TinyDB;

import java.net.URL;
import java.util.List;
import java.util.logging.Handler;

public class SuggestionAdapter extends BaseAdapter {

    private final Context context;
    private final List<Suggestion> items;
    private TinyDB tinyDB;

    public SuggestionAdapter(Context context, List<Suggestion> items) {
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
        ImageView before;
        ImageView after;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listview_suggestion, null);
            holder = new ViewHolder();
            final ViewHolder tempFolder = holder;

            holder.name = convertView.findViewById(R.id.listview_suggest_text);
            holder.before = convertView.findViewById(R.id.listview_before_img);
            holder.after = convertView.findViewById(R.id.listview_after_img);

            Thread thread = new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        URL curUrl = new URL(items.get(position).getCurrentURL());
                        Bitmap bmp = BitmapFactory.decodeStream(curUrl.openConnection().getInputStream());
                        tempFolder.before.setImageBitmap(bmp);

                        URL afterUrl = new URL(items.get(position).getSuggestedURL());
                        Bitmap bmp1 = BitmapFactory.decodeStream(afterUrl.openConnection().getInputStream());
                        tempFolder.after.setImageBitmap(bmp1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            thread.start();

            holder.name.setText("Replace " + items.get(position).getCurrentName() + " with " + items.get(position).getSuggestedName() + "!");
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.name.setText("Replace " + items.get(position).getCurrentName() + " with " + items.get(position).getSuggestedName() + "!");

        final ViewHolder tempFolder = holder;

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    URL curUrl = new URL(items.get(position).getCurrentURL());
                    Bitmap bmp = BitmapFactory.decodeStream(curUrl.openConnection().getInputStream());
                    tempFolder.before.setImageBitmap(bmp);

                    URL afterUrl = new URL(items.get(position).getSuggestedURL());
                    Bitmap bmp1 = BitmapFactory.decodeStream(afterUrl.openConnection().getInputStream());
                    tempFolder.after.setImageBitmap(bmp1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();

        return convertView;
    }

    public void addItem(Suggestion item) {
        if (!items.contains(item)) {
            items.add(item);
            notifyDataSetChanged();
        }
    }
}