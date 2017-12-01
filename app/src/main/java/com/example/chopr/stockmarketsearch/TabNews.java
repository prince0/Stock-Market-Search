package com.example.chopr.stockmarketsearch;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by chopr on 11/25/2017.
 */


public class TabNews extends Fragment {

    @BindView(R.id.list_view_news)
    ListView newsListView;

    @BindView(R.id.progress_news)
    ProgressBar progressBar;

    ArrayList<NewsModel> arrayList;
    NewsAdapter newsAdapter;
    String symbol;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_news_tab, container, false);

        ButterKnife.bind(this, rootView);

        Bundle bundle = getActivity().getIntent().getExtras();
        symbol = bundle.getString("StockName");
        progressBar.setVisibility(View.VISIBLE);

        arrayList = new ArrayList<>();

        String urlNews = "http://shrushtpwebtechhw8-env.us-east-2.elasticbeanstalk.com/news/" + symbol + "?symbol=" + symbol;

        getJSONNewsDetails(urlNews);

        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String link = ((TextView) view.findViewById(R.id.text_view_link)).getText().toString();
                Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                startActivity(myIntent);
            }
        });

        return rootView;
    }

    private void getJSONNewsDetails(String urlNews) {
        Log.e("url dekho news", urlNews);

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, urlNews, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("error hai news", response.toString());

                        try {

                            JSONObject temp = response.getJSONObject("rss").getJSONArray("channel").getJSONObject(0);
                            JSONArray jsonArray = (JSONArray) temp.get("item");

                            int count = 0;
                            for (int i = 0; i < jsonArray.length() && count < 5; i++) {
                                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                                String link = (String) jsonObject.getJSONArray("link").get(0);
                                if (link.contains("article")) {
                                    String title = (String) jsonObject.getJSONArray("title").get(0);
                                    String author = (String) jsonObject.getJSONArray("sa:author_name").get(0);
//                                    String author = authorArray[0];
                                    String pub = (String) jsonObject.getJSONArray("pubDate").get(0);
                                    pub = pub.replace(" -0500","");

//                                    String pub = pubArray[0];

                                    NewsModel newsModel = new NewsModel();
                                    newsModel.setAuthor(author);
                                    newsModel.setDate(pub);
                                    newsModel.setName(title);
                                    newsModel.setLink(link);

                                    arrayList.add(newsModel);
                                    count++;
                                }
                            }
                            newsAdapter = new NewsAdapter(getActivity(), arrayList);
                            newsListView.setAdapter(newsAdapter);

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

    @Override
    public void onResume() {
        super.onResume();

    }
}
