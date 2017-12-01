package com.example.chopr.stockmarketsearch;

/**
 * Created by chopr on 11/25/2017.
 */

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import com.facebook.FacebookSdk;

public class TabCurrent extends Fragment {

    private String urlJsonArry1 = "http://pchw8-env.us-west-1.elasticbeanstalk.com/?symbol=";
    private String urlJsonArry2 = "&type=";
    Iterator<String> iterator;
    String url = "http://www-scf.usc.edu/~princec/HW9.html?type=";
    String tempChartType = "Price";
    String symbol;
    HashMap<String, Double> priceHashMap;
    HashMap<String, Double> changeHashMap;
    HashMap<String, Double> changePercentHashMap;

    @BindView(R.id.scroll_view)
    ScrollView scrollView;

    @BindView(R.id.progress_current)
    ProgressBar progressBar;

    @BindView(R.id.table_layout_stock_table)
    TableLayout tableLayout;

    @BindView(R.id.spinner_chart_type)
    Spinner spinnerChartType;

    @BindView(R.id.web_view_chart)
    WebView webViewChart;

    @BindView(R.id.text_view_change_chart)
    TextView changeChartTextView;

    @BindView(R.id.text_view_error)
    TextView errorTextView;

    @BindView(R.id.favourite_toggle_button)
    ToggleButton favouriteButton;

    @BindView(R.id.text_view_stock_symbol)
    TextView stockSymbolTextView;

    @BindView(R.id.text_view_last_price)
    TextView lastPriceTextView;

    @BindView(R.id.text_view_change)
    TextView changeTextView;

    @BindView(R.id.text_view_timestamp)
    TextView timestampTextView;

    @BindView(R.id.text_view_open)
    TextView openTextView;

    @BindView(R.id.text_view_close)
    TextView closeTextView;

    @BindView(R.id.text_view_days_range)
    TextView daysRangeTextView;

    @BindView(R.id.text_view_volume)
    TextView volumeTextView;

    @BindView(R.id.image_button_facebook)
    ImageButton facebookImageButton;

    @BindView(R.id.image_view_change)
    ImageView changeImageView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_current_tab, container, false);

        Bundle bundle = getActivity().getIntent().getExtras();
        ButterKnife.bind(this, rootView);

        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(ScrollView.FOCUS_UP);
            }
        });

        favouriteButton.setClickable(false);
        facebookImageButton.setClickable(false);
        changeHashMap = getHashMap("Change");
        priceHashMap = getHashMap("Price");
        changePercentHashMap = getHashMap("ChangeP");

        symbol = bundle.getString("StockName");

        if (changeHashMap.containsKey(symbol)) {
            favouriteButton.setChecked(true);
        } else favouriteButton.setChecked(false);

        webViewChart.getSettings().setJavaScriptEnabled(true);
        webViewChart.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        Log.e("yelo chart ka url", url + tempChartType + "&symbol=" + symbol);
        webViewChart.loadUrl(url + tempChartType + "&symbol=" + symbol);

        facebookImageButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View view) {
                webViewChart.evaluateJavascript("javascript:fbShare()", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String s) {
                        Log.e("value", s);
                        ShareLinkContent content = new ShareLinkContent.Builder()
                                .setContentUrl(Uri.parse("https://google.com"))
                                .build();
                        ShareDialog shareDialog = new ShareDialog(getActivity());
                        shareDialog.show(content, ShareDialog.Mode.AUTOMATIC);
                    }
                });
            }
        });


//        RatingBar ratingBar = (RatingBar) rootView.findViewById(R.id.ratingBar2);
//        LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
//        stars.getDrawable(2).setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP);
//        stars.getDrawable(0).setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP);
//        stars.getDrawable(1).setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP);

        progressBar.setVisibility(View.VISIBLE);
        tableLayout.setVisibility(View.INVISIBLE);

//        webViewChart.loadUrl("http://www.google.com");

        List<String> chartType = new ArrayList<String>();
        chartType.add("Price");
        chartType.add("SMA");
        chartType.add("EMA");
        chartType.add("MACD");
        chartType.add("RSI");
        chartType.add("ADX");
        chartType.add("CCI");
        chartType.add("BBANDS");


        // Creating adapter for spinner
        ArrayAdapter<String> chartTypeAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, chartType);

        // Drop down layout style - list view with radio button
        chartTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        spinnerChartType.setAdapter(chartTypeAdapter);

        String urlStock = "";

        if (bundle != null) {
            urlStock = urlJsonArry1 + symbol + urlJsonArry2 + "Price";
        }

        getJSONStockDetails(urlStock);

        spinnerChartType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (!tempChartType.equals(spinnerChartType.getItemAtPosition(i))) {
                    changeChartTextView.setClickable(true);
                    changeChartTextView.setTextColor(Color.BLACK);
                } else {
                    changeChartTextView.setClickable(false);
                    changeChartTextView.setTextColor(Color.GRAY);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        return rootView;
    }

    public void saveHashMap(String key, Object obj) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(obj);
        editor.putString(key, json);
        editor.apply();     // This line is IMPORTANT !!!
    }

    public HashMap<String, Double> getHashMap(String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Gson gson = new Gson();
        String json = prefs.getString(key, "");
        java.lang.reflect.Type type = new TypeToken<HashMap<String, Double>>() {
        }.getType();
        HashMap<String, Double> obj = gson.fromJson(json, type);
        return obj;
    }

    @OnClick(R.id.favourite_toggle_button)
    public void favouriteButtonClick() {
        if (favouriteButton.isChecked()) {
            saveHashMap("Price", priceHashMap);
            saveHashMap("Change", changeHashMap);
            saveHashMap("ChangeP", changePercentHashMap);
        } else {
            priceHashMap.remove(symbol);
            changePercentHashMap.remove(symbol);
            changeHashMap.remove(symbol);
            saveHashMap("Price", priceHashMap);
            saveHashMap("Change", changeHashMap);
            saveHashMap("ChangeP", changePercentHashMap);
        }
    }

    @OnClick(R.id.text_view_change_chart)
    public void changeClick() {
        tempChartType = spinnerChartType.getSelectedItem().toString();
        webViewChart.loadUrl(url + tempChartType + "&symbol=" + symbol);
        changeChartTextView.setClickable(false);
        changeChartTextView.setTextColor(Color.GRAY);
    }

    @Override
    public void onResume() {
        super.onResume();
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(ScrollView.FOCUS_UP);
            }
        });
    }

    private void getJSONStockDetails(String urlStock) {
        Log.e("url dekho", urlStock);

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, urlStock, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("error hai", response.toString());

                        try {
                            if (response.has("Error Message")) {
                                errorTextView.setVisibility(View.VISIBLE);
                            } else {
                                errorTextView.setVisibility(View.INVISIBLE);
                                iterator = response.getJSONObject("Time Series (Daily)").keys();

                                JSONObject currentObject = response.getJSONObject("Time Series (Daily)").getJSONObject(iterator.next());
                                JSONObject pastObject = response.getJSONObject("Time Series (Daily)").getJSONObject(iterator.next());
//                                Log.e("close value", currentObject.get("4. close").toString());

                                stockSymbolTextView.setText(symbol);

                                double currentPrice = Math.round(Float.parseFloat(currentObject.get("4. close").toString()) * 100.0) / 100.0;
                                lastPriceTextView.setText("" + currentPrice);
                                priceHashMap.put(symbol, currentPrice);

                                double pastPrice = Math.round(Float.parseFloat(pastObject.get("4. close").toString()) * 100.0) / 100.0;

                                double change = Math.round((currentPrice - pastPrice) * 100.0) / 100.0;
                                double changePercent = Math.round((change / pastPrice * 100) * 100.0) / 100.0;

                                changeTextView.setText(change + " (" + changePercent + "%)");
                                if (changePercent >= 0) {

                                    changeImageView.setImageResource(R.drawable.up);

                                    changeTextView.setTextColor(Color.GREEN);
                                } else {

                                    changeImageView.setImageResource(R.drawable.down);
                                    changeTextView.setTextColor(Color.RED);
                                }
                                changeHashMap.put(symbol, change);
                                changePercentHashMap.put(symbol, changePercent);


                                String date = response.getJSONObject("Meta Data").getString("3. Last Refreshed");
                                if (date.length() <= 12) date = date + " 16:00:00";

                                timestampTextView.setText(date + " EDT");

                                double openValue = Math.round(Float.parseFloat(currentObject.get("1. open").toString()) * 100.0) / 100.0;
                                double closeValue = Math.round(Float.parseFloat(currentObject.get("4. close").toString()) * 100.0) / 100.0;
                                double lowValue = Math.round(Float.parseFloat(currentObject.get("3. low").toString()) * 100.0) / 100.0;
                                double highValue = Math.round(Float.parseFloat(currentObject.get("2. high").toString()) * 100.0) / 100.0;

                                openTextView.setText(openValue + "");
                                closeTextView.setText("" + closeValue);
                                daysRangeTextView.setText(lowValue + " - " + highValue);

                                volumeTextView.setText(currentObject.get("5. volume").toString());

                                tableLayout.setVisibility(View.VISIBLE);
                                favouriteButton.setClickable(true);
                                facebookImageButton.setClickable(true);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        progressBar.setVisibility(View.INVISIBLE);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("error", "Error: " + error.getMessage());
                Toast.makeText(getActivity(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
                errorTextView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
            }
        });

        // Adding request to request queue
//        AppController.getInstance().addToRequestQueue(req);
        req.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(getActivity()).add(req);
    }


}
