package com.example.chopr.stockmarketsearch;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by chopr on 11/25/2017.
 */

public class TabHistorical extends Fragment {

    @BindView(R.id.web_view_historical)
    WebView historicalWebView;

    String url = "http://www-scf.usc.edu/~princec/HW9.html?type=historical&symbol=";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_historical_tab, container, false);

        Bundle bundle = getActivity().getIntent().getExtras();

        ButterKnife.bind(this, rootView);

        historicalWebView.getSettings().setJavaScriptEnabled(true);
        historicalWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        historicalWebView.loadUrl(url + bundle.getString("StockName"));
        return rootView;
    }
}
