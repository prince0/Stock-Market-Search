package com.example.chopr.stockmarketsearch;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.Toast;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, COUNTRIES);

        autoCompleteTextView.setAdapter(adapter);
        autoCompleteTextView.setThreshold(1);
        autoCompleteTextView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {



                return false;
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
        spinnerSortBy.setAdapter(sortByAdapter);
        spinnerOrderBy.setAdapter(orderByAdapter);


    }


    @OnClick(R.id.getQuoteTextView)
    public void getQuote() {
        if (autoCompleteTextView.getText().toString().trim().length() == 0) {
            Toast.makeText(getApplicationContext(), "Please enter a stock name or symbol", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Get Quote Clicked", Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.clearTextView)
    public void clear() {
        Toast.makeText(getApplicationContext(), "Clear Clicked", Toast.LENGTH_SHORT).show();
    }

    private static String[] COUNTRIES = new String[]{
            "Belgium", "France", "Italy", "Germany", "Spain"
    };

}
