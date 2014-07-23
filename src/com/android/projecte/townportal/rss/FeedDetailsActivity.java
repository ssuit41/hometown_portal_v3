package com.android.projecte.townportal.rss;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.projecte.townportal.R;

public class FeedDetailsActivity extends Activity {

	private FeedItem feed;
	
	//Loads a new view that displays a content snippet along with links in the menu to the full article
	//Clicking on the title will also link to the full article
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feed_details);

		feed = (FeedItem) this.getIntent().getSerializableExtra("feed");

		if (null != feed) {
			ImageView thumb = (ImageView) findViewById(R.id.featuredImg);
			new ImageDownloaderTask(thumb).execute(feed.getAttachmentUrl());

			TextView title = (TextView) findViewById(R.id.title);
			title.setText(feed.getTitle());
			title.setOnClickListener(new OnClickListener()
			{
				public void onClick(View v)
				{
					Intent intent = new Intent(FeedDetailsActivity.this, WebViewActivity.class);
					intent.putExtra("url", feed.getUrl());
					startActivity(intent);
				}
			});
			

			TextView htmlTextView = (TextView) findViewById(R.id.content);
			htmlTextView.setText(Html.fromHtml(feed.getContentPreview(), null, null));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		 switch (item.getItemId()) {
	        case R.id.menu_share:
	        	shareContent();
	            return true;
	        case R.id.menu_view:
	        	Intent intent = new Intent(FeedDetailsActivity.this, WebViewActivity.class);
				intent.putExtra("url", feed.getUrl());
				startActivity(intent);
				
	        	return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

	private void shareContent() {
		Intent sendIntent = new Intent();
		sendIntent.setAction(Intent.ACTION_SEND);
		sendIntent.putExtra(Intent.EXTRA_TEXT, feed.getTitle() + "\n" + feed.getUrl());
		sendIntent.setType("text/plain");
		startActivity(Intent.createChooser(sendIntent, "Share using"));

	}
}
