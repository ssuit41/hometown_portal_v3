package com.android.projecte.townportal.rss;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Vector;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import com.android.projecte.townportal.R;

public class FeedListActivity extends Activity {

	private ArrayList<FeedItem> feedList = null;
	private ProgressBar progressbar = null;
	private ListView feedListView = null;
	private String findFeed = "https://ajax.googleapis.com/ajax/services/feed/find?v=1.0&q=";
	private String loadFeed = "https://ajax.googleapis.com/ajax/services/feed/load?v=1.0&q=";
	private ArrayList<String> aggregateList;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_posts_list);
		progressbar = (ProgressBar) findViewById(R.id.progressBar);
		String url = findFeed + "Chicago";
		new DownloadFilesTask().execute(url);
	}

	//launches the custom listview to display the populated list of news feeds
	public void updateList() {
		feedListView= (ListView) findViewById(R.id.custom_list);
		feedListView.setVisibility(View.VISIBLE);
		progressbar.setVisibility(View.GONE);
		
		feedListView.setAdapter(new CustomListAdapter(this, feedList));
		feedListView.setOnItemClickListener(new OnItemClickListener() {

			//when an item is click in the listview it launches the feeddetailsactivity class to display the article
			@Override
			public void onItemClick(AdapterView<?> a, View v, int position,	long id) {
				Object o = feedListView.getItemAtPosition(position);
				FeedItem newsData = (FeedItem) o;
				
				Intent intent = new Intent(FeedListActivity.this, FeedDetailsActivity.class);
				intent.putExtra("feed", newsData);
				startActivity(intent);
			}
		});
	}
	
	//Downloads data for rss feed on a background thread
	//Once finished in the postExecute protocol is runs updateList
	private class DownloadFilesTask extends AsyncTask<String, Integer, Void> {
		
		
		@Override
		protected void onProgressUpdate(Integer... values) {
		}

		@Override
		protected void onPostExecute(Void result) {
			if (null != feedList) {
				updateList();
			}
		}

		@Override
		protected Void doInBackground(String... params) {
			String url = params[0];

			// Get list of aggregate feeds
			Log.i("URL", url);
			JSONObject json = getJSONFromUrl(url);
			parseAggregateFeeds(json);
			Log.i("AggFeed", "Finish");
			// Parse each feed that returned from the query
			feedList = new ArrayList<FeedItem>();
			for(int i = 0; i < aggregateList.size(); ++i)
			{
				Log.i("Loop", "Looping");
				String link = loadFeed + aggregateList.get(i);
				Log.i("Data", link);
				JSONObject feeds = getJSONFromUrl(link);
				parseJson(feeds);
			}
			return null;
		}
	}

	
	public JSONObject getJSONFromUrl(String url) {
		//InputStream is = null;
		JSONObject jObj = null;
		String json = null;

		// Making HTTP request
		try {
			// defaultHttpClient
			/*
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);

			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();
			is = httpEntity.getContent();

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			*/
			
			URL site = new URL(url);
			URLConnection connection = site.openConnection();
			connection.addRequestProperty("Referer", "https://github.com/ssuit41/hometown_portal_v3");
			
			String line = null;
			StringBuilder sb = new StringBuilder();
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
				//sb.append(line);
			}
			//is.close();
			//json = sb.toString().substring(0, sb.toString().length() - 1);
			json = sb.toString();
			//Log.i("Msg", sb.toString() );
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			jObj = new JSONObject(json);
		} catch (JSONException e) {
			Log.e("JSON Parser", "Error parsing data " + e.toString());
		}

		// return JSON String
		return jObj;

	}
	
	public void parseAggregateFeeds(JSONObject json)
	{
		// parsing json object
		aggregateList = new ArrayList<String>();
		try{
			
			if (json.getString("responseDetails").equalsIgnoreCase("null")) {
				
				//json = json.getJSONObject("entries");
				//Log.i("JSON", json.toString());
				Log.i("JSON", json.getJSONObject("responseData").getString("query"));
				JSONArray feeds = json.getJSONObject("responseData").getJSONArray("entries");
				
				for(int i = 0; i < feeds.length(); ++i)
				{
					JSONObject feed = (JSONObject) feeds.getJSONObject(i);
					String url = new String();
					url = feed.getString("url");
					Log.i("URL", url);
					aggregateList.add(url);
				}
			}
		}
		catch (JSONException e) {
			e.printStackTrace();
		}
				
	}
	
	public void parseJson(JSONObject json) {
		try {

			// parsing json feed object
			if (json.getString("responseDetails").equalsIgnoreCase("null")) {
				JSONArray posts = json.getJSONObject("responseData").getJSONObject("feed").getJSONArray("entries");

				for (int i = 0; i < posts.length(); i++) {
					JSONObject post = (JSONObject) posts.getJSONObject(i);
					FeedItem item = new FeedItem();
					item.setTitle(post.getString("title"));
					item.setDate(post.getString("publishedDate"));
					item.setContentPreview(post.getString("contentSnippet"));
					item.setUrl(post.getString("link"));
					item.setContent(post.getString("content"));
					JSONArray attachments = post.optJSONArray("mediaGroups");

					if (null != attachments && attachments.length() > 0) {
						JSONObject attachment = attachments.getJSONObject(0);
						if (attachment != null)
							item.setAttachmentUrl(attachment.getString("url"));
					}

					feedList.add(item);
					Log.i("Data", feedList.toString());
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}