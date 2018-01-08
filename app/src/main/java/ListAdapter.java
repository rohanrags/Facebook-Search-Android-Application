package com.example.rohan.hw9;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by rohan on 4/11/17.
 */

public class ListAdapter extends ArrayAdapter<Item> {

    private Context context;
    private int counter = 1;
    private List<Item> items;

    public ListAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public ListAdapter(Context context, int resource, List<Item> items) {
        super(context, resource, items);
        this.context = context;
        Log.d("Items size", "in Adapter->" + items.size());
        this.items = items;

    }

//    @Override
//    public int getCount() {
//        if(counter*10 > items.size()){
//            return items.size();
//        }else{
//            return counter*20;
//        }
//    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        ResultsActivity r = new ResultsActivity();

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.row, null);
        }

        final Item p = getItem(position);
        ImageView forStar = (ImageView) v.findViewById(R.id.icon_empty_star);
        ImageView details_button_ImageView = (ImageView) v.findViewById(R.id.icon_details);

        if (p != null) {
            ImageView im = (ImageView) v.findViewById(R.id.icon);
            TextView tt1 = (TextView) v.findViewById(R.id.id);

            if (im != null) {
                Picasso.with(context).load(p.getUrl()).into(im);
            }

            if (tt1 != null) {
                tt1.setText(p.getName());
            }

            if (p.isStarred()) {
                forStar.setImageResource(R.mipmap.ic_yellow_star);
            } else {
                forStar.setImageResource(R.mipmap.ic_launcher_empty_star);
            }
        }

        details_button_ImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(getContext(), DetailsActivity.class);
                try {
                    String name = p.getName();
                    String id = p.getId();
                    String url = p.getUrl();
                    String type = p.getType();
                    if (id != null && id.trim().length() != 0) {
                        intent.putExtra("DETAILS_NAME", name);
                        intent.putExtra("DETAILS_ID", id);
                        intent.putExtra("DETAILS_URL", url);
                        intent.putExtra("DETAILS_TYPE", type);
                        ((ResultsActivity) context).startActivityForResult(intent, 1);

                    }
                    Log.d("Started", "Details Activity");
                } catch (NullPointerException e) {
                    Log.d("ListAdapter", e.toString());
                    e.printStackTrace();
                } catch (Exception e) {
                    Log.d("ListAdapter", e.toString());
                    e.printStackTrace();
                }
            }
        });


        return v;
    }
}
