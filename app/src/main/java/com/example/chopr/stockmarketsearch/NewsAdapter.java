package com.example.chopr.stockmarketsearch;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by chopr on 11/30/2017.
 */

public class NewsAdapter extends BaseAdapter {

    ArrayList<NewsModel> arrayList;
    private static LayoutInflater inflater = null;

    public NewsAdapter(Activity a, ArrayList<NewsModel> arrayList){
        inflater = (LayoutInflater) a.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.arrayList = arrayList;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public NewsModel getItem(int i) {
        return arrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        NewsAdapter.ViewHolder holder;
        if (view != null) {
            holder = (NewsAdapter.ViewHolder) view.getTag();
        } else {
            view = inflater.inflate(R.layout.news_row_list, viewGroup, false);
            holder = new NewsAdapter.ViewHolder(view);
            view.setTag(holder);
        }

        NewsModel newsModel = getItem(i);
        holder.title.setText(newsModel.getName());
        holder.author.setText("Author: "+newsModel.getAuthor());
        holder.date.setText("Date: "+newsModel.getDate());
        holder.link.setText(newsModel.getLink());

        return view;
    }

    static class ViewHolder {
        @BindView(R.id.text_view_title_news)
        TextView title;
        @BindView(R.id.text_view_author)
        TextView author;
        @BindView(R.id.text_view_pub_date)
        TextView date;
        @BindView(R.id.text_view_link)
        TextView link;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
