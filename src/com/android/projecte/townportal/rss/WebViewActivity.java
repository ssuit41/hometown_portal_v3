/*
 * House Droidaryen!
 * WebViewActivity launches a webview of the selected feed when either the title or button is clicked.
 * 
 * WebViewActivity was developed using the tutorial http://javatechig.com/android/json-feed-reader-in-android
 * along with the documentation for JSON and Google Feeds api
 */

package com.android.projecte.townportal.rss;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.android.projecte.townportal.R;

public class WebViewActivity extends Activity {
	private WebView webView;

	//loads a webview based on the string provided in the feed to display full articles
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_webview);
		
		webView = (WebView) findViewById(R.id.webView);
		webView.setWebViewClient(new MyWebViewClient());

		Bundle bundle = this.getIntent().getExtras();
		String url = bundle.getString("url");

		if (null != url) {
			webView.getSettings().setJavaScriptEnabled(true);
			webView.loadUrl(url);
		}
	}

	private class MyWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}
	}

}
