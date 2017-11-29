package com.example.chopr.stockmarketsearch;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Prince Chopra on 11/16/2017
 */
public class MainActivity extends Activity {

    @BindView(R.id.autoComplete)
    AutoCompleteTextView autoCompleteTextView;

    @BindView(R.id.spinnerOrderBy)
    Spinner spinnerOrderBy;

    @BindView(R.id.spinnerSortBy)
    Spinner spinnerSortBy;

    @BindView(R.id.progress_autocomplete)
    ProgressBar progressBar;

    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

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


        List<String> sortByList = new ArrayList<String>();
        sortByList.add("Default");
        sortByList.add("Symbol");
        sortByList.add("Price");
        sortByList.add("Change");
        sortByList.add("Change Percent");
        List<String> orderByList = new ArrayList<String>();
        orderByList.add("Default");
        orderByList.add("Symbol");
        orderByList.add("Price");
        orderByList.add("Change");
        orderByList.add("Change Percent");


        // Creating adapter for spinner
        ArrayAdapter<String> sortByAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, sortByList);

        ArrayAdapter<String> orderByAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, orderByList);

        // Drop down layout style - list view with radio button
        sortByAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        orderByAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner

        spinnerOrderBy.setPrompt("Hello");
        spinnerSortBy.setAdapter(sortByAdapter);
        spinnerOrderBy.setAdapter(orderByAdapter);


    }

    @OnClick(R.id.getQuoteTextView)
    public void getQuote() {
        if (autoCompleteTextView.getText().toString().trim().length() == 0) {
            Toast.makeText(getApplicationContext(), "Please enter a stock name or symbol", Toast.LENGTH_SHORT).show();
        } else {
            String temp = autoCompleteTextView.getText().toString();
            Toast.makeText(getApplicationContext(), "Get Quote Clicked", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, StockDetail.class);
            intent.putExtra("StockName", temp.substring(0, temp.contains("-") ? temp.indexOf("-") - 1 : temp.length()));
            startActivity(intent);
        }
    }

    @OnClick(R.id.clearTextView)
    public void clear() {
        Toast.makeText(getApplicationContext(), "Clear Clicked", Toast.LENGTH_SHORT).show();
        autoCompleteTextView.setText("");
    }

    public void getDataFromServer() {

    }

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
