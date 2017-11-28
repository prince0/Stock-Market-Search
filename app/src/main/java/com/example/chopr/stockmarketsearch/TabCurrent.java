package com.example.chopr.stockmarketsearch;

/**
 * Created by chopr on 11/25/2017.
 */

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TabCurrent extends Fragment {

    private String urlJsonArry1 = "http://pchw8-env.us-west-1.elasticbeanstalk.com/?symbol=";
    private String urlJsonArry2 = "&type=";
    Iterator<String> iterator;
    String url = "http://www-scf.usc.edu/~princec/HW9.html?type=";
    String tempChartType="Price";
    String symbol;

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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_current_tab, container, false);

        Bundle bundle = getActivity().getIntent().getExtras();
        ButterKnife.bind(this, rootView);

        symbol = bundle.getString("StockName");

        webViewChart.getSettings().setJavaScriptEnabled(true);
        webViewChart.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        Log.e("yelo chart ka url", url + tempChartType + "&symbol=" + symbol);
        webViewChart.loadUrl(url + tempChartType + "&symbol=" + symbol);


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


        // Creating adapter for spinner
        ArrayAdapter<String> chartTypeAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, chartType);

        // Drop down layout style - list view with radio button
        chartTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        spinnerChartType.setAdapter(chartTypeAdapter);

        if (bundle != null) {
            urlJsonArry1 += symbol + urlJsonArry2 + "Price";
        }

        getJSONStockDetails();

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


    @OnClick(R.id.text_view_change_chart)
    public void changeClick() {
        tempChartType = spinnerChartType.getSelectedItem().toString();
        webViewChart.loadUrl(url + tempChartType + "&symbol=" + symbol);
        changeChartTextView.setClickable(false);
        changeChartTextView.setTextColor(Color.GRAY);
    }

    private void getJSONStockDetails() {
        Log.e("url dekho", urlJsonArry1);

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, urlJsonArry1, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("error hai", response.toString());

                        try {
                            iterator = response.getJSONObject("Time Series (Daily)").keys();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        // Parsing json array response
                        // loop through each json object
//                            jsonResponse = "";
//                            for (int i = 0; i < response.length(); i++) {
//
//                                JSONObject person = (JSONObject) response
//                                        .get(i);
//
//                                String name = person.getString("name");
//                                String email = person.getString("email");
//                                JSONObject phone = person
//                                        .getJSONObject("phone");
//                                String home = phone.getString("home");
//                                String mobile = phone.getString("mobile");
//
//                                jsonResponse += "Name: " + name + "\n\n";
//                                jsonResponse += "Email: " + email + "\n\n";
//                                jsonResponse += "Home: " + home + "\n\n";
//                                jsonResponse += "Mobile: " + mobile + "\n\n\n";
//
//                            }

                        Log.e("Next Log", iterator.next());


                        progressBar.setVisibility(View.INVISIBLE);
                        tableLayout.setVisibility(View.VISIBLE);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("error", "Error: " + error.getMessage());
                Toast.makeText(getActivity(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();

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
