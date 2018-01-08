package com.example.rohan.hw9;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.rohan.hw9.MainActivity.KEYWORD_DATA;

public class ResultsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final ListAdapter[] customAdapter = new ListAdapter[1];
    private static String keyword;
    private static String favorites = null;
    private static boolean fromFavorites;
    private static String php_url = null;
    private static String removed_Or_Not = null;
    private static String id_back_from_details = null;
    private static String type_of_data = null;
    private static View mainView = null;
    private static HashMap newHashMap = new HashMap();
    private static List<Item> newList = new ArrayList<>();
    private static ArrayList<Item> retrievedItemList_User;
    private static ArrayList<Item> retrievedItemList_Page;
    private static ArrayList<Item> retrievedItemList_Group;
    private static ArrayList<Item> retrievedItemList_Event;
    private static ArrayList<Item> retrievedItemList_Place;
    private static List<Item> ItemArray = null;
    private static Context context;
    private static int startCount;
    private static int finishCount;
    private static String lat = null;
    private static String lng = null;
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

    public static void checkItemRemovedOrNot(Item it) {
//        Log.d("it->id",it.getId());
//        Log.d("id_from_details",id_back_from_details+"");
        String x = it.getId();
        String y = id_back_from_details;
        if (x.equals(y)) {
            if (removed_Or_Not != null) {
                if (removed_Or_Not.equalsIgnoreCase("false")) {
                    Log.d("Not starred", "MAKE STAR");
                    it.setStarred(true);
                } else {
                    Log.d("starred", "NO STAR");
                    it.setStarred(false);
                }
            }
        }
    }

    public static SharedPreferences getSharedPreferences(Context ctxt) {
        return ctxt.getSharedPreferences("Favorites", 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        favorites = null;
        fromFavorites = false;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        tabLayout.getTabAt(0).setIcon(R.mipmap.users);
        tabLayout.getTabAt(1).setIcon(R.mipmap.pages);
        tabLayout.getTabAt(2).setIcon(R.mipmap.events);
        tabLayout.getTabAt(3).setIcon(R.mipmap.places);
        tabLayout.getTabAt(4).setIcon(R.mipmap.groups);

        Intent intent = getIntent();
        String keyword_data = intent.getStringExtra(KEYWORD_DATA);
        String temp1 = intent.getStringExtra(MainActivity.FAVORITES_DATA);
        php_url = intent.getStringExtra("NEXT_URL");
        Log.d("keyword", keyword_data + "");
        if (keyword_data != null) {
            getSupportActionBar().setTitle("Results");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            try {
                keyword_data = URLEncoder.encode(keyword_data, "utf-8");
            } catch (Exception e) {
                Log.d("Encoding exception", "URL Encoding Excpetion");
                e.printStackTrace();
            }
            keyword = keyword_data;

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout1);
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        } else {
            getSupportActionBar().setTitle("Favorites");
            fromFavorites = true;

            if (temp1 != null) {
                favorites = temp1;
            }
            addtoSharedPreferance();
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout1);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.setDrawerListener(toggle);
            toggle.syncState();

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view1);
            navigationView.setNavigationItemSelectedListener(this);
        }

        Log.d("GetLocation1", "Starting Geolocation");
        getLocation();
        Log.d("GetLocation2", "Finish Geolocation");
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Log.d("Back button", "FROM RESULTS");

        if (id == R.id.nav_home) {
            Log.d("Main_Home Clicked", "YES");
            super.onBackPressed();
        }

        if (id == R.id.nav_about_me) {
            Log.d("About me Clicked", "YES");
            Intent intent = new Intent(this, AboutMe.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout1);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void getLocation() {
        Log.d("GetLocation", "Inside Geolocation");

        String locationProvider = LocationManager.GPS_PROVIDER;
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        //Location
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                Log.d("Location Changed", "Location");
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };
        try {
            //lm.requestLocationUpdates(locationProvider, 0, 0, locationListener);
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 10, locationListener);
            Location loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (loc != null) {
                double longitude = loc.getLongitude();
                double latitude = loc.getLatitude();
                if (String.valueOf(longitude) != null && String.valueOf(latitude) != null) {
                    lat = String.valueOf(latitude);
                    lng = String.valueOf(longitude);
                    Log.d("lat1->", lat + "");
                    Log.d("lat1->", lng + "");
                }
            }

        } catch (Exception e) {
            Log.d("Location Exception", "Location not confirmed");
            e.printStackTrace();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                if (fromFavorites) {
                    addtoSharedPreferance();
                    Log.d("From Favorites", "YES - from details to fav is true");


                    int curView = mViewPager.getCurrentItem();
                    String curViewTab = null;
                    if (curView == 0) {
                        curViewTab = "user";
                    } else if (curView == 1) {
                        curViewTab = "page";
                    } else if (curView == 2) {
                        curViewTab = "event";
                    } else if (curView == 3) {
                        curViewTab = "place";
                    } else if (curView == 4) {
                        curViewTab = "group";
                    }
                    Log.d("CurView", curView + "");
                    Log.d("CurViewTab", curViewTab + "");
                    View current_tab = mViewPager.findViewWithTag(curViewTab);
                    ListView newView = (ListView) current_tab.findViewById(R.id.listview);


                    Log.d("Back from details->Fav ", newView + "*" + retrievedItemList_User + "*" + retrievedItemList_Page + "*" + retrievedItemList_Group);
                    ListAdapter customAdapter = null;

                    if (curView == 0) {
                        customAdapter = new ListAdapter(context, R.layout.row, retrievedItemList_User);
                    } else if (curView == 1) {
                        customAdapter = new ListAdapter(context, R.layout.row, retrievedItemList_Page);
                    } else if (curView == 2) {
                        customAdapter = new ListAdapter(context, R.layout.row, retrievedItemList_Event);
                    } else if (curView == 3) {
                        customAdapter = new ListAdapter(context, R.layout.row, retrievedItemList_Place);
                    } else if (curView == 4) {
                        customAdapter = new ListAdapter(context, R.layout.row, retrievedItemList_Group);
                    }

                    newView.setAdapter(customAdapter);


                } else {
                    removed_Or_Not = data.getStringExtra("boolean_Value");
                    id_back_from_details = data.getStringExtra("id_Value");
                    type_of_data = data.getStringExtra("type");
                    if (id_back_from_details != null) {
                        Log.d("res1->", removed_Or_Not + "");
                        Log.d("res2->", id_back_from_details);

                        newList = (ArrayList) newHashMap.get(type_of_data);

                        for (int i = 0; i < newList.size(); i++) {
                            Item new_i = newList.get(i);
                            checkItemRemovedOrNot(new_i);
                        }

                        int curView = mViewPager.getCurrentItem();
                        String curViewTab = null;
                        if (curView == 0) {
                            curViewTab = "user";
                        } else if (curView == 1) {
                            curViewTab = "page";
                        } else if (curView == 2) {
                            curViewTab = "event";
                        } else if (curView == 3) {
                            curViewTab = "place";
                        } else if (curView == 4) {
                            curViewTab = "group";
                        }
                        Log.d("CurView", curView + "");
                        Log.d("CurViewTab", curViewTab + "");
                        View current_tab = mViewPager.findViewWithTag(curViewTab);
                        ListView newView = (ListView) current_tab.findViewById(R.id.listview);
                        if (finishCount > newList.size()) {
                            customAdapter[0] = new ListAdapter(context, R.layout.row, newList.subList(startCount, newList.size()));
                        } else {
                            customAdapter[0] = new ListAdapter(context, R.layout.row, newList.subList(startCount, finishCount));
                        }
                        newView.setAdapter(customAdapter[0]);
                    }
                }

            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }

        }
    }

    public boolean checkSharedPreferances(String id) {
        SharedPreferences mPrefs = getSharedPreferences("Favorites", MODE_PRIVATE);
        return mPrefs.contains(id);
    }

    //adds and updates the view in favorites
    public void addtoSharedPreferance() {

        SharedPreferences mPrefs = getSharedPreferences("Favorites", MODE_PRIVATE);
        Map<String, ?> keys = mPrefs.getAll();
        Gson gson = new Gson();

        retrievedItemList_User = new ArrayList<>();
        retrievedItemList_Page = new ArrayList<>();
        retrievedItemList_Group = new ArrayList<>();
        retrievedItemList_Event = new ArrayList<>();
        retrievedItemList_Place = new ArrayList<>();


        for (Map.Entry<String, ?> entry : keys.entrySet()) {
//            Log.d("map values", entry.getKey() + ": " +
//                    entry.getValue().toString());
            String json = mPrefs.getString(entry.getKey(), entry.getValue().toString());
            Item retrievedobj = gson.fromJson(json, Item.class);
            retrievedobj.setStarred(true);
            Log.d("Type-->", retrievedobj.getType());
            if (retrievedobj.getType().equalsIgnoreCase("user")) {
                retrievedItemList_User.add(retrievedobj);
                Log.d("Retrived Item List", retrievedItemList_User.toString());
            } else if (retrievedobj.getType().equalsIgnoreCase("page")) {
                retrievedItemList_Page.add(retrievedobj);
                Log.d("Retrived Item List", retrievedItemList_Page.toString());
            } else if (retrievedobj.getType().equalsIgnoreCase("event")) {
                retrievedItemList_Event.add(retrievedobj);
                Log.d("Retrived Item List", retrievedItemList_Event.toString());
            } else if (retrievedobj.getType().equalsIgnoreCase("place")) {
                retrievedItemList_Place.add(retrievedobj);
                Log.d("Retrived Item List", retrievedItemList_Place.toString());
            } else if (retrievedobj.getType().equalsIgnoreCase("group")) {
                retrievedItemList_Group.add(retrievedobj);
                Log.d("Retrived Item List", retrievedItemList_Group.toString());
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_results, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            Log.d("Back button", "FROM RESULTS");
            fromFavorites = false;
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
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_results, container, false);
            final Bundle args = getArguments();
            final Button next = (Button) rootView.findViewById(R.id.next_button);
            final Button previous = (Button) rootView.findViewById(R.id.previous_button);
            mainView = rootView;
            context = getContext();

            String type = "";
            if (fromFavorites) {
                next.setVisibility(View.INVISIBLE);
                previous.setVisibility(View.INVISIBLE);
                Log.d("From Favorites", "YES");
                ListView yourListView = (ListView) rootView.findViewById(R.id.listview);
                int currentView = args.getInt(ARG_SECTION_NUMBER) - 1;
                Log.d("Current View ", currentView + "*" + retrievedItemList_User + "*" + retrievedItemList_Page + "*" + retrievedItemList_Group);
                ListAdapter customAdapter = null;

                if (currentView == 0) {
                    rootView.setTag("user");
                    customAdapter = new ListAdapter(getContext(), R.layout.row, retrievedItemList_User);
                } else if (currentView == 1) {
                    rootView.setTag("page");
                    customAdapter = new ListAdapter(getContext(), R.layout.row, retrievedItemList_Page);
                } else if (currentView == 2) {
                    rootView.setTag("event");
                    customAdapter = new ListAdapter(getContext(), R.layout.row, retrievedItemList_Event);
                } else if (currentView == 3) {
                    rootView.setTag("place");
                    customAdapter = new ListAdapter(getContext(), R.layout.row, retrievedItemList_Place);
                } else if (currentView == 4) {
                    rootView.setTag("group");
                    customAdapter = new ListAdapter(getContext(), R.layout.row, retrievedItemList_Group);
                }

                yourListView.setAdapter(customAdapter);

            } else {
                next.setVisibility(View.VISIBLE);
                previous.setVisibility(View.VISIBLE);
                int currentView = args.getInt(ARG_SECTION_NUMBER) - 1;
                Log.d("Current View Results", currentView + "");
                String beanstalk_url = "http://homework8-env.kamd3vgqsa.us-west-2.elasticbeanstalk.com/mysearch.php?keyword=";

//                if(php_url==null) {
                if (currentView == 0) {
                    rootView.setTag("user");
                    type = "user";
                    php_url = beanstalk_url + keyword + "&type=user";
                } else if (currentView == 1) {
                    rootView.setTag("page");
                    type = "page";
                    php_url = beanstalk_url + keyword + "&type=page";
                } else if (currentView == 2) {
                    rootView.setTag("event");
                    type = "event";
                    php_url = beanstalk_url + keyword + "&type=event";
                } else if (currentView == 3) {
                    rootView.setTag("place");
                    type = "place";

                    if (lat != null && lng != null) {
                        php_url = beanstalk_url + keyword + "&type=place&lat=" + lat + "&lng=" + lng;
                    } else {
                        php_url = beanstalk_url + keyword + "&type=place&lat=30&lng=30";
                    }
                } else if (currentView == 4) {
                    rootView.setTag("group");
                    type = "group";
                    php_url = beanstalk_url + keyword + "&type=group";
                } else {
                    type = "user";
                    php_url = beanstalk_url + keyword + "&type=user";
                }
//                }

                Log.d("PHP_URL", php_url);

                final ListView yourListView = (ListView) rootView.findViewById(R.id.listview);

                //String to place our result in
                String result;
                //Instantiate new instance of our class
                HttpGetRequest getRequest = new HttpGetRequest();
                //Perform the doInBackground method, passing in our url
                try {
                    result = getRequest.execute(php_url).get();
                    if (result != null) {
                        Item it;
                        ItemArray = new ArrayList<Item>();
                        JSONObject primary_object = new JSONObject(result);
                        if (primary_object.has("data")) {
                            JSONArray jsonArray = primary_object.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject secondary_object = jsonArray.getJSONObject(i);
                                String name = secondary_object.getString("name");
                                JSONObject pic_url_obj = secondary_object.getJSONObject("picture").getJSONObject("data");
                                String url = pic_url_obj.getString("url");
                                String id = secondary_object.getString("id");

                                it = new Item(id, name, url);
                                SharedPreferences spfs = getSharedPreferences(getContext());
                                if (spfs.contains(it.getId())) {
                                    it.setStarred(true);
                                    Log.d("VALUE", "TRUE");
                                }
                                it.setType(type);
                                checkItemRemovedOrNot(it);
                                ItemArray.add(it);
                            }

                        }

                        Item tempItem = ItemArray.get(0);
                        Log.d("First Item", tempItem.getName());
                        newHashMap.put(tempItem.getType(), ItemArray);

                        final int[] counter = {0};
                        final int[] last = {11};

                        startCount = 0;
                        finishCount = 10;
                        previous.setEnabled(false);
                        if (ItemArray.size() < 10) {
                            customAdapter[0] = new ListAdapter(getContext(), R.layout.row, ItemArray.subList(0, ItemArray.size()));
                            yourListView.setAdapter(customAdapter[0]);
                            next.setEnabled(false);
                            previous.setEnabled(false);
                        } else {
                            customAdapter[0] = new ListAdapter(getContext(), R.layout.row, ItemArray.subList(counter[0], last[0]));
                            yourListView.setAdapter(customAdapter[0]);
                        }

                        previous.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View arg0) {
                                counter[0] = counter[0] - 10;
                                last[0] = last[0] - 10;
                                startCount = startCount - 10;
                                finishCount = finishCount - 10;

                                int currentView = args.getInt(ARG_SECTION_NUMBER) - 1;
                                String type_for_buttons = null;
                                if (currentView == 0) {
                                    type_for_buttons = "user";
                                } else if (currentView == 1) {
                                    type_for_buttons = "page";
                                } else if (currentView == 2) {
                                    type_for_buttons = "event";
                                } else if (currentView == 3) {
                                    type_for_buttons = "place";
                                } else if (currentView == 4) {
                                    type_for_buttons = "group";
                                }
                                Log.d("currentView", currentView + "");
                                List<Item> list = (ArrayList) newHashMap.get(type_for_buttons);

                                if (counter[0] < 1) {
                                    counter[0] = 0;
                                    customAdapter[0] = new ListAdapter(getContext(), R.layout.row, list.subList(counter[0], last[0]));
                                    yourListView.setAdapter(customAdapter[0]);
                                    previous.setEnabled(false);
                                } else {
                                    next.setEnabled(true);
                                    customAdapter[0] = new ListAdapter(getContext(), R.layout.row, list.subList(counter[0], last[0]));
                                    yourListView.setAdapter(customAdapter[0]);
                                }

                                Log.d("Next->", counter[0] + "");
                                Log.d("Last->", last[0] + "");
                                Log.d("Size->", list.size() + "");
                            }
                        });

                        next.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View arg0) {

                                counter[0] = counter[0] + 10;
                                last[0] = last[0] + 10;
                                startCount = startCount + 10;
                                finishCount = finishCount + 10;

                                int currentView = args.getInt(ARG_SECTION_NUMBER) - 1;
                                String type_for_buttons = null;
                                if (currentView == 0) {
                                    type_for_buttons = "user";
                                } else if (currentView == 1) {
                                    type_for_buttons = "page";
                                } else if (currentView == 2) {
                                    type_for_buttons = "event";
                                } else if (currentView == 3) {
                                    type_for_buttons = "place";
                                } else if (currentView == 4) {
                                    type_for_buttons = "group";
                                }
                                Log.d("currentView", currentView + "");
                                List<Item> list = (ArrayList) newHashMap.get(type_for_buttons);

                                if (last[0] > list.size()) {
//                                    last[0]=ItemArray.size();
                                    customAdapter[0] = new ListAdapter(getContext(), R.layout.row, list.subList(counter[0], list.size()));
                                    yourListView.setAdapter(customAdapter[0]);
                                    next.setEnabled(false);
                                } else {
                                    previous.setEnabled(true);
                                    customAdapter[0] = new ListAdapter(getContext(), R.layout.row, list.subList(counter[0], last[0]));
                                    yourListView.setAdapter(customAdapter[0]);
                                }

                                Log.d("Next->", counter[0] + "");
                                Log.d("Last->", last[0] + "");
                                Log.d("Size->", list.size() + "");
                            }
                        });

                    } else {
                        Log.d("EMP_URL", "Empty result");
                        //yourListView.setEmptyView(rootView.findViewById(R.id.empty));
                    }


                } catch (JSONException e) {
                    Log.d("ResultActivity", "JSON EXCEPTION");
                    e.printStackTrace();
                } catch (Exception e) {
                    Log.d("ResultActivity", "GENERIC EXCEPTION");
                    e.printStackTrace();
                }
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
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 5 total pages.
            return 5;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Users";
                case 1:
                    return "Pages";
                case 2:
                    return "Events";
                case 3:
                    return "Places";
                case 4:
                    return "Groups";
            }
            return null;
        }
    }
}
