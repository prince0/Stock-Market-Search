package com.example.chopr.stockmarketsearch;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by chopr on 11/25/2017.
 */

public class TabHistorical extends Fragment {

    @BindView(R.id.web_view_historical)
    WebView historicalWebView;

    @BindView(R.id.text_view_error_historical)
    TextView errorTextView;

    String url = "http://www-scf.usc.edu/~princec/HW9.html?type=historical&symbol=";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_historical_tab, container, false);

        Bundle bundle = getActivity().getIntent().getExtras();

        ButterKnife.bind(this, rootView);

        String symbol = bundle.getString("StockName");
        String url1 = "http://pchw8-env.us-west-1.elasticbeanstalk.com/lookup/" + symbol;
        JsonArrayRequest jsObjRequest = new JsonArrayRequest
                (Request.Method.GET, url1, null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        if (response.length()==0){
                            errorTextView.setVisibility(View.VISIBLE);
                        }else {
                            errorTextView.setVisibility(View.INVISIBLE);
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        Log.e("error", error.toString());
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(jsObjRequest);

        historicalWebView.getSettings().setJavaScriptEnabled(true);
        historicalWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        historicalWebView.loadUrl(url + symbol);
        return rootView;
    }
}
