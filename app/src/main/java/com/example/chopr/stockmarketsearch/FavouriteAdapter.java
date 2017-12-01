package com.example.chopr.stockmarketsearch;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by chopr on 11/29/2017.
 */

public class FavouriteAdapter extends BaseAdapter {

    private static LayoutInflater inflater = null;
    private ArrayList<FavouriteModel> favList;

    public FavouriteAdapter(Activity a, ArrayList<FavouriteModel> list) {

        inflater = (LayoutInflater) a.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.favList = list;

    }

    @Override
    public int getCount() {
        return favList.size();
    }

    @Override
    public FavouriteModel getItem(int i) {
        return favList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public FavouriteModel remove(int position){
        return favList.remove(position);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view != null) {
            holder = (ViewHolder) view.getTag();
        } else {
            view = inflater.inflate(R.layout.favourite_list, viewGroup, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }

        FavouriteModel item = getItem(i);
        String symbol = item.getSymbol();
        Double price = item.getPrice();
        Double change = item.getChange();
        Double changePercent = item.getChangePercent();

        holder.symbol.setText(symbol);
        holder.price.setText(price + "");
        holder.change.setText(change + " (" + changePercent + "%)");
        if (changePercent>=0){
            holder.change.setTextColor(Color.GREEN);
        }else
            holder.change.setTextColor(Color.RED);

        return view;
    }

    static class ViewHolder {
        @BindView(R.id.text_view_favourite_symbol)
        TextView symbol;
        @BindView(R.id.text_view_favourite_price)
        TextView price;
        @BindView(R.id.text_view_favourite_change)
        TextView change;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
