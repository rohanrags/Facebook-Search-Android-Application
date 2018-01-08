package com.example.rohan.hw9;

/**
 * Created by rohan on 4/15/17.
 */

import android.content.Context;
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

public class CustomPostListAdapter extends ArrayAdapter<Post> {

    private Context context;
    private String name, url, empty_posts;

    public CustomPostListAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public CustomPostListAdapter(Context context, int resource, List<Post> posts, String name, String url, String empty_posts) {
        super(context, resource, posts);
        this.context = context;
        this.name = name;
        this.url = url;
        this.empty_posts = empty_posts;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.postrow, null);
        }

        final Post post = getItem(position);

        if (post != null) {
            ImageView im = (ImageView) v.findViewById(R.id.post_photo);
            TextView tt1 = (TextView) v.findViewById(R.id.post_name);
            TextView tt2 = (TextView) v.findViewById(R.id.post_time);
            TextView tt3 = (TextView) v.findViewById(R.id.post_msg);

            if (im != null) {
                Picasso.with(context).load(url).into(im);
            }

            if (tt1 != null) {
                tt1.setText(name);
            }
            if (tt2 != null) {
                tt2.setText(post.getCreated_time());
            }
            if (tt3 != null) {
                tt3.setText(post.getMessage());
            }


        }


//        ImageView details_button_ImageView = (ImageView) v.findViewById(R.id.icon_details);
//
//        details_button_ImageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View arg0) {
//                Intent intent = new Intent(getContext(), DetailsActivity.class);
//                try {
//                    String name = p.getName();
//                    String id = p.getId();
//                    String url = p.getUrl();
//                    String type = p.getType();
//                    if (id != null && id.trim().length() != 0) {
//                        intent.putExtra("DETAILS_NAME", name);
//                        intent.putExtra("DETAILS_ID",id);
//                        intent.putExtra("DETAILS_URL",url);
//                        intent.putExtra("DETAILS_TYPE",type);
//                        getContext().startActivity(intent);
//                    }
//                    getContext().startActivity(intent);
//                }catch (NullPointerException e){
//                    Log.d("ListAdapter",e.toString());
//                    e.printStackTrace();
//                }catch (Exception e){
//                    Log.d("ListAdapter",e.toString());
//                    e.printStackTrace();
//                }
//            }
//        });

        return v;
    }

}
