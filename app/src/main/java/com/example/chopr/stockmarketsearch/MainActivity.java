package com.example.chopr.stockmarketsearch;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Prince Chopra on 11/16/2017
 */
public class MainActivity extends Activity {

    RequestQueue requestQueue;
    HashMap<String, Double> priceHashMap;
    HashMap<String, Double> changeHashMap;
    HashMap<String, Double> changePercentHashMap;
    FavouriteAdapter favouriteAdapter;
    List<String> sortByList;
    List<String> orderByList;
    LinkedHashSet<String> tempHashSet;

    @BindView(R.id.autoComplete)
    AutoCompleteTextView autoCompleteTextView;

    @BindView(R.id.spinnerOrderBy)
    Spinner spinnerOrderBy;

    @BindView(R.id.spinnerSortBy)
    Spinner spinnerSortBy;

    @BindView(R.id.progress_autocomplete)
    ProgressBar progressBar;

    @BindView(R.id.list_view_favourite)
    ListView favouriteListView;

    @BindView(R.id.progress_favorite)
    ProgressBar favoriteProgress;

    @BindView(R.id.image_view_refresh)
    ImageView refreshImageView;

    @BindView(R.id.switch_favourite)
    Switch favoriteSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        tempHashSet = new LinkedHashSet<>();

        changeHashMap = getHashMap("Change");
        changePercentHashMap = getHashMap("ChangeP");
        priceHashMap = getHashMap("Price");

        saveHashMap("Change", changeHashMap == null ? new HashMap<String, Double>() : changeHashMap);
        saveHashMap("ChangeP", changePercentHashMap == null ? new HashMap<String, Double>() : changePercentHashMap);
        saveHashMap("Price", priceHashMap == null ? new HashMap<String, Double>() : priceHashMap);

        for (String s : priceHashMap.keySet()) {
            tempHashSet.add(s);
        }

        autoCompleteTextView.setThreshold(1);

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                progressBar.setVisibility(View.VISIBLE);
                if (s.length() != 0) {
                    if (autoCompleteTextView.getText().toString().trim().length() != 0) {
                        jsonRequest(autoCompleteTextView.getText().toString());
                    } else autoCompleteTextView.dismissDropDown();
                } else autoCompleteTextView.dismissDropDown();
//                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (autoCompleteTextView.getText().toString().trim().length() == 0) {
                    autoCompleteTextView.dismissDropDown();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                progressBar.setVisibility(View.GONE);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(autoCompleteTextView.getWindowToken(), 0);
            }
        });

        initializeSpinner();
    }

    private void initializeSpinner() {
        final int[] posOrder = {0};
        final int[] posSort = {0};

        sortByList = new ArrayList<String>();
        sortByList.add("Sort by");
        sortByList.add("Default");
        sortByList.add("Symbol");
        sortByList.add("Price");
        sortByList.add("Change");
        sortByList.add("Change Percent");

        orderByList = new ArrayList<String>();
        orderByList.add("Order");
        orderByList.add("Ascending");
        orderByList.add("Descending");

        // Creating adapter for spinner
        ArrayAdapter<String> sortByAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, sortByList) {
            @Override
            public boolean isEnabled(int position) {
                return super.isEnabled(position);
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View mView = super.getDropDownView(position, convertView, parent);
                TextView mTextView = (TextView) mView;
                if (position == 0 || posSort[0] == position) {
                    mTextView.setTextColor(Color.GRAY);
                    return mView;
                } else {
                    return super.getDropDownView(position, convertView, parent);
                }
            }
        };

        ArrayAdapter<String> orderByAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, orderByList) {
            @Override
            public boolean isEnabled(int position) {
                return super.isEnabled(position);
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View mView = super.getDropDownView(position, convertView, parent);
                TextView mTextView = (TextView) mView;
                if (position == 0 || posOrder[0] == position) {
                    mTextView.setTextColor(Color.GRAY);
                    return mView;
                } else {
                    return super.getDropDownView(position, convertView, parent);
                }
            }
        };

        // Drop down layout style - list view with radio button
        sortByAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        orderByAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner

        spinnerOrderBy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                posOrder[0] = i;
                view.setEnabled(false);

                if (!spinnerSortBy.getSelectedItem().equals("Sort by") && !spinnerSortBy.getSelectedItem().equals("Default")) {
                    TreeMap<Double, String> treeMap;
                    LinkedHashSet<String> tempLinkedHashSet = new LinkedHashSet<>();
                    if (spinnerSortBy.getSelectedItem().equals("Symbol")) {
                        TreeSet<String> treeSet;
                        if (spinnerOrderBy.getSelectedItem().equals("Descending")) {
                            treeSet = new TreeSet<>(Collections.reverseOrder());
                        } else treeSet = new TreeSet<>();

                        treeSet.addAll(priceHashMap.keySet());
                        tempLinkedHashSet.addAll(treeSet);
                        makeFavoriteList(tempLinkedHashSet);
                    }
                    if (spinnerSortBy.getSelectedItem().equals("Price")) {
                        if (spinnerOrderBy.getSelectedItem().equals("Descending")) {
                            treeMap = new TreeMap<>(Collections.reverseOrder());
                        } else treeMap = new TreeMap<>();

                        for (String s : priceHashMap.keySet()) {
                            treeMap.put(priceHashMap.get(s), s);
                        }
                        for (Double s : treeMap.keySet()) {
                            tempLinkedHashSet.add(treeMap.get(s));
                        }
                        makeFavoriteList(tempLinkedHashSet);
                    }
                    if (spinnerSortBy.getSelectedItem().equals("Change")) {
                        if (spinnerOrderBy.getSelectedItem().equals("Descending")) {
                            treeMap = new TreeMap<>(Collections.reverseOrder());
                        } else treeMap = new TreeMap<>();

                        for (String s : changeHashMap.keySet()) {
                            treeMap.put(changeHashMap.get(s), s);
                        }
                        for (Double s : treeMap.keySet()) {
                            tempLinkedHashSet.add(treeMap.get(s));
                        }
                        makeFavoriteList(tempLinkedHashSet);
                    }
                    if (spinnerSortBy.getSelectedItem().equals("Change Percent")) {
                        if (spinnerOrderBy.getSelectedItem().equals("Descending")) {
                            treeMap = new TreeMap<>(Collections.reverseOrder());
                        } else treeMap = new TreeMap<>();

                        for (String s : changePercentHashMap.keySet()) {
                            treeMap.put(changePercentHashMap.get(s), s);
                        }
                        for (Double s : treeMap.keySet()) {
                            tempLinkedHashSet.add(treeMap.get(s));
                        }
                        makeFavoriteList(tempLinkedHashSet);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spinnerSortBy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                posSort[0] = i;
                view.setEnabled(false);

                if (!spinnerOrderBy.getSelectedItem().equals("Order")) {
                    TreeMap<Double, String> treeMap;
                    LinkedHashSet<String> tempLinkedHashSet = new LinkedHashSet<>();
                    if (spinnerSortBy.getSelectedItem().equals("Symbol")) {
                        TreeSet<String> treeSet;
                        if (spinnerOrderBy.getSelectedItem().equals("Descending")) {
                            treeSet = new TreeSet<>(Collections.reverseOrder());
                        } else treeSet = new TreeSet<>();

                        treeSet.addAll(priceHashMap.keySet());
                        tempLinkedHashSet.addAll(treeSet);
                        makeFavoriteList(tempLinkedHashSet);
                    }
                    if (spinnerSortBy.getSelectedItem().equals("Price")) {
                        if (spinnerOrderBy.getSelectedItem().equals("Descending")) {
                            treeMap = new TreeMap<>(Collections.reverseOrder());
                        } else treeMap = new TreeMap<>();

                        for (String s : priceHashMap.keySet()) {
                            treeMap.put(priceHashMap.get(s), s);
                        }
                        for (Double s : treeMap.keySet()) {
                            tempLinkedHashSet.add(treeMap.get(s));
                        }
                        makeFavoriteList(tempLinkedHashSet);
                    }
                    if (spinnerSortBy.getSelectedItem().equals("Change")) {
                        if (spinnerOrderBy.getSelectedItem().equals("Descending")) {
                            treeMap = new TreeMap<>(Collections.reverseOrder());
                        } else treeMap = new TreeMap<>();

                        for (String s : changeHashMap.keySet()) {
                            treeMap.put(changeHashMap.get(s), s);
                        }
                        for (Double s : treeMap.keySet()) {
                            tempLinkedHashSet.add(treeMap.get(s));
                        }
                        makeFavoriteList(tempLinkedHashSet);
                    }
                    if (spinnerSortBy.getSelectedItem().equals("Change Percent")) {
                        if (spinnerOrderBy.getSelectedItem().equals("Descending")) {
                            treeMap = new TreeMap<>(Collections.reverseOrder());
                        } else treeMap = new TreeMap<>();

                        for (String s : changePercentHashMap.keySet()) {
                            treeMap.put(changePercentHashMap.get(s), s);
                        }
                        for (Double s : treeMap.keySet()) {
                            tempLinkedHashSet.add(treeMap.get(s));
                        }
                        makeFavoriteList(tempLinkedHashSet);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spinnerSortBy.setAdapter(sortByAdapter);
        spinnerOrderBy.setAdapter(orderByAdapter);
    }

    public void saveHashMap(String key, Object obj) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(obj);
        editor.putString(key, json);
        editor.apply();     // This line is IMPORTANT !!!
    }

    public HashMap<String, Double> getHashMap(String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Gson gson = new Gson();
        String json = prefs.getString(key, "");
        java.lang.reflect.Type type = new TypeToken<HashMap<String, Double>>() {
        }.getType();
        HashMap<String, Double> obj = gson.fromJson(json, type);
        return obj;
    }

    @OnClick(R.id.image_view_refresh)
    public void refreshClicked(){

        favoriteProgress.setVisibility(View.VISIBLE);
        // Create a Handler instance on the main thread
        final Handler handler = new Handler();

// Create and start a new Thread
        new Thread(new Runnable() {
            public void run() {
                try{
                    Thread.sleep(5000);
                }
                catch (Exception e) { } // Just catch the InterruptedException

                // Now we use the Handler to post back to the main thread
                handler.post(new Runnable() {
                    public void run() {
                        // Set the View's visibility back on the main UI Thread
                        favoriteProgress.setVisibility(View.INVISIBLE);
                    }
                });
            }
        }).start();
    }

    @OnClick(R.id.switch_favourite)
    public void switchClicked(){
//        if (favoriteSwitch.isEnabled()){
//            final Handler ha=new Handler();
//            ha.postDelayed(new Runnable() {
//
//                @Override
//                public void run() {
//                    //call function
//                    favoriteProgress.setVisibility(View.VISIBLE);
//
//                    ha.postDelayed(this, 5000);
//                    favoriteProgress.setVisibility(View.INVISIBLE);
//                }
//            }, 5000);
//        }else favoriteProgress.setVisibility(View.INVISIBLE);
    }

    @OnClick(R.id.getQuoteTextView)
    public void getQuote() {
        if (autoCompleteTextView.getText().toString().trim().length() == 0) {
            Toast.makeText(getApplicationContext(), "Please enter a stock name or symbol", Toast.LENGTH_SHORT).show();
        } else {
            String temp = autoCompleteTextView.getText().toString();
//            Toast.makeText(getApplicationContext(), "Get Quote Clicked", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, StockDetail.class);
            intent.putExtra("StockName", temp.substring(0, temp.contains("-") ? temp.indexOf("-") - 1 : temp.length()));
            startActivity(intent);
        }
    }

    @OnClick(R.id.clearTextView)
    public void clear() {
//        Toast.makeText(getApplicationContext(), "Clear Clicked", Toast.LENGTH_SHORT).show();
        autoCompleteTextView.setText("");
    }

    @Override
    protected void onResume() {
        super.onResume();

        favouriteListView.invalidate();
        changeHashMap = getHashMap("Change");
        changePercentHashMap = getHashMap("ChangeP");
        priceHashMap = getHashMap("Price");
        tempHashSet.removeAll(tempHashSet);
        for (String s : priceHashMap.keySet()) {
            tempHashSet.add(s);
        }

        makeFavoriteList(tempHashSet);

    }

    public void makeFavoriteList(LinkedHashSet<String> tempHashSet) {
        ArrayList<FavouriteModel> list = new ArrayList<FavouriteModel>();

        favouriteListView.invalidate();

        for (String s : tempHashSet) {
            FavouriteModel favouriteModel = new FavouriteModel();

            favouriteModel.setSymbol(s);
            favouriteModel.setChange(changeHashMap.get(s));
            favouriteModel.setChangePercent(changePercentHashMap.get(s));
            favouriteModel.setPrice(priceHashMap.get(s));

            list.add(favouriteModel);
        }


        favouriteAdapter = new FavouriteAdapter(this, list);
        favouriteListView.setAdapter(favouriteAdapter);
        registerForContextMenu(favouriteListView);
        favouriteAdapter.notifyDataSetChanged();

        favouriteListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {

//                Cursor cursor = (Cursor) adapterView.getItemAtPosition(i);
//                String posID = cursor.getString(0);
//                final int getID = Integer.parseInt(posID);
//
//                AlertDialog.Builder alert = new AlertDialog.Builder(this);
//                alert.setTitle("Delete entry");
//                alert.setMessage("Are you sure you want to delete?");
//                alert.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//
//
//                    public void onClick(DialogInterface dialog, int which) {
//                        // continue with delete
//                        String symbol = favouriteAdapter.remove(i).getSymbol();
//                        changeHashMap.remove(symbol);
//                        changePercentHashMap.remove(symbol);
//                        priceHashMap.remove(symbol);
//                        saveHashMap("Change", changeHashMap);
//                        saveHashMap("ChangeP", changePercentHashMap);
//                        saveHashMap("Price", priceHashMap);
//                    }
//                });
//                alert.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        // close dialog
//                        dialog.cancel();
//                    }
//                });
//                alert.show();

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                String symbol = favouriteAdapter.remove(i).getSymbol();
                                changeHashMap.remove(symbol);
                                changePercentHashMap.remove(symbol);
                                priceHashMap.remove(symbol);
                                saveHashMap("Change", changeHashMap);
                                saveHashMap("ChangeP", changePercentHashMap);
                                saveHashMap("Price", priceHashMap);
                                favouriteAdapter.notifyDataSetChanged();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("Remove from Favorite?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();

                return true;
            }
        });

        favouriteListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String symbol = ((TextView) view.findViewById(R.id.text_view_favourite_symbol)).getText().toString();
                Intent intent = new Intent(MainActivity.this, StockDetail.class);
                intent.putExtra("StockName", symbol);
                startActivity(intent);
            }
        });
    }

//    @Override
//    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//        super.onCreateContextMenu(menu, v, menuInfo);
//        menu.setHeaderTitle("Remove from Favorites?");
//        menu.add(0, v.getId(), 0, "Yes");//groupId, itemId, order, title
//        menu.add(0, v.getId(), 0, "No");
//    }
//
//    @Override
//    public boolean onContextItemSelected(MenuItem item) {
//
//        if(item.getTitle()=="Yes"){
//            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
//            Log.d("delete item", "removing item pos=" + info.position);
//            mAdapter.remove(info.position);
//            return true;
//            Toast.makeText(getApplicationContext(),"calling code",Toast.LENGTH_LONG).show();
//        }
//        else if(item.getTitle()=="No"){
//            Toast.makeText(getApplicationContext(),"sending sms code",Toast.LENGTH_LONG).show();
//        }else{
//            return false;
//        }
//        return true;
//    }

    public void jsonRequest(String symbol) {
        autoCompleteTextView.dismissDropDown();
        progressBar.setVisibility(View.VISIBLE);
        if (symbol.contains("-")) {
            progressBar.setVisibility(View.GONE);
            autoCompleteTextView.dismissDropDown();
            return;
        }
        String url = "http://pchw8-env.us-west-1.elasticbeanstalk.com/lookup/" + symbol;
        JsonArrayRequest jsObjRequest = new JsonArrayRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        try {

                            String[] altArray = new String[Math.min(5, response.length())];
                            int len = altArray.length;

                            for (int i = 0; i < len; i++) {
                                JSONObject temp = response.getJSONObject(i);
                                altArray[i] = temp.getString("Symbol") + " - " + temp.getString("Name") + " (" + temp.getString("Exchange") + ")";
                            }
                            final ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this,
                                    android.R.layout.simple_dropdown_item_1line, altArray);
                            progressBar.setVisibility(View.GONE);
                            autoCompleteTextView.setAdapter(adapter);
                            autoCompleteTextView.showDropDown();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        Log.e("error", error.toString());
                        autoCompleteTextView.dismissDropDown();
                        progressBar.setVisibility(View.GONE);
                    }
                });

        requestQueue.add(jsObjRequest);
    }

}
