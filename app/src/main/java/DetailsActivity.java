package com.example.rohan.hw9;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DetailsActivity extends AppCompatActivity {

    private static String details_name;
    private static String details_id;
    private static String details_url;
    private static String details_type;
    private static String removedOrNot = null;
    public FacebookCallback<Sharer.Result> shareCallBack = new FacebookCallback<Sharer.Result>() {

        @Override
        public void onSuccess(Sharer.Result result) {
            Toast toast = Toast.makeText(getApplicationContext(), "Posted successfully", Toast.LENGTH_SHORT);
            toast.show();
        }

        @Override
        public void onCancel() {
            Toast toast = Toast.makeText(getApplicationContext(), "Not Posted", Toast.LENGTH_SHORT);
            toast.show();
        }

        @Override
        public void onError(FacebookException error) {
            Toast toast = Toast.makeText(getApplicationContext(), "Not Posted - Error", Toast.LENGTH_SHORT);
            toast.show();
        }
    };
    ShareDialog shareDialog;
    CallbackManager callbackManager;
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("More Details");

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container_Details);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(1);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        tabLayout.getTabAt(0).setIcon(R.mipmap.ic_launcher_albums);
        tabLayout.getTabAt(1).setIcon(R.mipmap.ic_launcher_posts);

        Intent intent = getIntent();
        String temp1 = intent.getStringExtra("DETAILS_ID");
        String temp2 = intent.getStringExtra("DETAILS_NAME");
        String temp3 = intent.getStringExtra("DETAILS_URL");
        String temp4 = intent.getStringExtra("DETAILS_TYPE");
        details_id = temp1;
        details_name = temp2;
        details_url = temp3;
        details_type = temp4;
        removedOrNot = null;
        Log.d("Details id", "on create->" + details_id);

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
        shareDialog.registerCallback(callbackManager, shareCallBack);

    }

    public void fb_share() {

        ShareLinkContent linkContent = new ShareLinkContent.Builder()
                .setContentTitle(details_name)
                .setContentDescription("FB SEARCH FROM USC CSCI571")
                .setImageUrl(Uri.parse(details_url))
                .setContentUrl(Uri.parse("https://developers.facebook.com/docs"))
                .build();
        shareDialog.show(linkContent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public boolean checkSharedPreferances(String id) {
        SharedPreferences mPrefs = getSharedPreferences("Favorites", MODE_PRIVATE);
        return mPrefs.contains(id);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_details, menu);
        this.menu = menu;
        checkMenuItems();
        return true;
    }

    private void checkMenuItems() {
        if (checkSharedPreferances(details_id)) {
            Log.d("Menu Item id", details_id + "*" + R.id.Remove_favorites);
            showOption(R.id.Remove_favorites);
            showOption(R.id.Share_fb);
            hideOption(R.id.Add_to_favorites);
        } else {
            showOption(R.id.Add_to_favorites);
            showOption(R.id.Share_fb);
            hideOption(R.id.Remove_favorites);
        }
    }

    private void hideOption(int id) {
        MenuItem item = menu.findItem(id);
        item.setVisible(false);
    }

    private void showOption(int id) {
        MenuItem item = menu.findItem(id);
        item.setVisible(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        Log.d("Menu item Click", id + "");

        //noinspection SimplifiableIfStatement
        if (id == R.id.Add_to_favorites) {
            removedOrNot = "false";
            Log.d("Menu Click Favs", id + "");
            SharedPreferences settings = getSharedPreferences("Favorites", MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            Item newItem = new Item(details_id, details_name, details_url, details_type);
            Gson gson = new Gson();
            String json = gson.toJson(newItem); // myObject - instance of MyObject
            editor.putString(details_id, json);
            Log.d("Shared Object->", json);
            // Commit the edits!
            editor.commit();

            Toast toast = Toast.makeText(getApplicationContext(), "Added to Favorites", Toast.LENGTH_SHORT);
            toast.show();

            checkMenuItems();
        }

        if (id == R.id.Remove_favorites) {
            removedOrNot = "true";
            SharedPreferences settings = getSharedPreferences("Favorites", MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editor.remove(details_id);
            editor.commit();

            Toast toast = Toast.makeText(getApplicationContext(), "Removed from Favorites", Toast.LENGTH_SHORT);
            toast.show();

            checkMenuItems();
        }

        if (id == R.id.Share_fb) {
            fb_share();
        }

        if (id == android.R.id.home) {
            Log.d("BACK", " FROM DETAILS");
            Intent returnIntent = new Intent();
            returnIntent.putExtra("boolean_Value", removedOrNot);
            returnIntent.putExtra("id_Value", details_id);
            returnIntent.putExtra("type", details_type);
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
            super.onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            args.putString("DETAILS_NAME", details_name);
            args.putString("DETAILS_ID", details_id);
            args.putString("DETAILS_URL", details_url);
            args.putString("DETAILS_TYPE", details_type);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            ExpandableListView expandableListView = null;
            ListView postListView = null;
            ExpandableListAdapter expandableListAdapter;
            CustomPostListAdapter postListAdapter;
            final List<String> expandableListTitle = new ArrayList<>();
            final List<String> postsData = new ArrayList<>();
            List<Post> post_temp = new ArrayList<>();
            final HashMap<String, List<String>> expandableListDetail = new HashMap<>();

            Bundle args = getArguments();
            String id, name, url, type;
            String empty_albums = null, empty_posts = null;
            id = args.getString("DETAILS_ID");
            name = args.getString("DETAILS_NAME");
            url = args.getString("DETAILS_URL");
            type = args.getString("DETAILS_TYPE");

            int currentView = args.getInt(ARG_SECTION_NUMBER) - 1;
            Log.d("Current View Details", currentView + "");

            String php_url, high_res_image_1, high_res_image_2;
            php_url = "http://homework8-env.kamd3vgqsa.us-west-2.elasticbeanstalk.com/mysearch.php?id=" + id + "&type=" + type;
            high_res_image_1 = "https://graph.facebook.com/v2.8/";
            high_res_image_2 = "/picture?type(large)&access_token=EAAN340JsZBjQBALk8fkgg8oa03kJfv6CA9DQ9neExHYoShKZBAj9mSYYEuU1Sm0O0WlQ4hY2NrAXZAZA6G1ZCZAIebsM3ujy9Y45zRJJJF1w7ZC9MC0V8mDpE1WcMNMPG5ZBHLcxPcrKMUeZCmUKKIGtFK7ZAy0o53troAlDR0ptRQkQZDZD";

            View rootView = inflater.inflate(R.layout.fragment_details, container, false);
            if (currentView == 0) {
                expandableListView = (ExpandableListView) rootView.findViewById(R.id.expandableListView);
            } else if (currentView == 1) {
                postListView = (ListView) rootView.findViewById(R.id.postsListview);
            }


            Log.d("PHP_URL", php_url);
            //String to place our result in
            String result = null;
            JSONObject primary_object = null;
            //Instantiate new instance of our class
            HttpGetRequest getRequest = new HttpGetRequest();
            //Perform the doInBackground method, passing in our url

            try {
                result = getRequest.execute(php_url).get();
                primary_object = new JSONObject(result);
            } catch (Exception e) {
                Log.d("DetailsActivity", "GENERIC EXCEPTION - NO RESULT");
                e.printStackTrace();
            }

            try {
                Log.d("gh", result);
                if (result != null) {
                    if (primary_object.has("albums")) {
                        JSONObject albums = primary_object.getJSONObject("albums");
                        JSONArray albums_data = albums.getJSONArray("data");
                        for (int i = 0; i < albums_data.length(); i++) {

                            JSONObject secondary_object = albums_data.getJSONObject(i);
                            if (secondary_object.has("name") && secondary_object.has("photos")) {
                                String pic_name = secondary_object.getString("name");
                                expandableListTitle.add(pic_name);

                                JSONObject photos = secondary_object.getJSONObject("photos");
                                JSONArray photos_data = photos.getJSONArray("data");
                                List<String> titles_temp = new ArrayList<>();
                                for (int j = 0; j < photos_data.length(); j++) {
                                    JSONObject tertiary_object = photos_data.getJSONObject(j);
                                    String pic_id = tertiary_object.getString("id");
                                    String pic_url = high_res_image_1 + pic_id + high_res_image_2;
                                    titles_temp.add(pic_url);
                                }

                                expandableListDetail.put(pic_name, titles_temp);
                            } else {
                                if (currentView == 0) {
                                    TextView empty_data = (TextView) rootView.findViewById(R.id.EmptyAlbums);
                                    empty_data.setText("No albums data");
                                }
                            }
                        }
                    }

                    if (currentView == 0) {
                        if (!primary_object.has("albums")) {
                            TextView empty_data = (TextView) rootView.findViewById(R.id.EmptyAlbums);
                            empty_data.setText("No albums data");
                        }
                        expandableListAdapter = new CustomExpandableListAdapter(getContext(), expandableListTitle, expandableListDetail, empty_albums);
                        expandableListView.setAdapter(expandableListAdapter);
                    }
                } else {
                    Log.d("EMP_URL", "Empty result");
                }
            } catch (JSONException e) {
                Log.d("DetailsActivity", "JSON EXCEPTION - ALBUMS");
                e.printStackTrace();
            } catch (Exception e) {
                Log.d("DetailsActivity", "GENERIC EXCEPTION - ALBUMS");
                e.printStackTrace();
            }
            try {
                if (result != null) {
                    if (primary_object.has("posts")) {
                        JSONObject posts = primary_object.getJSONObject("posts");
                        JSONArray posts_data = posts.getJSONArray("data");
                        Log.d("Post_length", posts_data.length() + "");
                        Post post_item;
                        for (int k = 0; k < posts_data.length(); k++) {
                            JSONObject secondary_post_object = posts_data.getJSONObject(k);
                            if (secondary_post_object.has("message") && secondary_post_object.has("created_time")) {
                                String post_msg = secondary_post_object.getString("message");
                                String created_time = secondary_post_object.getString("created_time");
                                post_item = new Post(post_msg, created_time);
                                post_temp.add(post_item);
                            }
                        }
                    }

                    if (currentView == 1) {
                        if (!primary_object.has("posts")) {
                            TextView empty_data = (TextView) rootView.findViewById(R.id.EmptyPosts);
                            empty_data.setText("No Posts data");
                        }
                        postListAdapter = new CustomPostListAdapter(getContext(), R.layout.postrow, post_temp, details_name, details_url, empty_posts);
                        postListView.setAdapter(postListAdapter);
                    }
                } else {
                    Log.d("EMP_URL", "Empty result");
                }
            } catch (JSONException e) {
                Log.d("DetailsActivity", "JSON EXCEPTION - POSTS" + e.toString());
                e.printStackTrace();
            } catch (Exception e) {
                Log.d("DetailsActivity", "GENERIC EXCEPTION - POSTS");
                e.printStackTrace();
            }

            return rootView;
        }

    }


    public static class HttpGetRequest extends AsyncTask<String, Void, String> {
        public static final String REQUEST_METHOD = "GET";
        public static final int READ_TIMEOUT = 15000;
        public static final int CONNECTION_TIMEOUT = 15000;

        @Override
        protected String doInBackground(String... params) {
            String stringUrl = params[0];
            String result;
            String inputLine;
            try {
                //Create a URL object holding our url
                URL myUrl = new URL(stringUrl);
                //Create a connection
                HttpURLConnection connection = (HttpURLConnection)
                        myUrl.openConnection();
                //Set methods and timeouts
                connection.setRequestMethod(REQUEST_METHOD);
                connection.setReadTimeout(READ_TIMEOUT);
                connection.setConnectTimeout(CONNECTION_TIMEOUT);

                //Connect to our url
                connection.connect();
                //Create a new InputStreamReader
                InputStreamReader streamReader = new
                        InputStreamReader(connection.getInputStream());
                //Create a new buffered reader and String Builder
                BufferedReader reader = new BufferedReader(streamReader);
                StringBuilder stringBuilder = new StringBuilder();
                //Check if the line we are reading is not null
                while ((inputLine = reader.readLine()) != null) {
                    stringBuilder.append(inputLine);
                }
                //Close our InputStream and Buffered reader
                reader.close();
                streamReader.close();
                //Set our result equal to our stringBuilder
                result = stringBuilder.toString();
            } catch (IOException e) {
                Log.d("IO EXCEP DO_IN_BACK", e.toString());
                Log.getStackTraceString(e);
                result = null;
            } catch (Exception e) {
                Log.d("GENERIC EXP DO_IN_BACK", e.toString());
                Log.getStackTraceString(e);
                result = null;
            }
            return result;
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            Log.d("GetItem", position + "");
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "ALBUMS";
                case 1:
                    return "POSTS";
            }
            return null;
        }
    }
}
