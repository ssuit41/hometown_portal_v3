package com.android.projecte.townportal;

import java.util.List;
import java.util.Vector;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public abstract class FeedActivity extends Activity {

    protected List<Item> items = new Vector<Item>();
    protected ArrayAdapter<Item> adapter;
    protected ListView list;
    protected WebView webView;
    protected TextView courtesyText, titleText, loadingText;
    protected View divider;
    
    protected Boolean viewingItem = false;
    protected String title, viewMoreUrl;
    
    final private Integer MAX_DESC_LENGTH = 200;
    
    @SuppressLint("SetJavaScriptEnabled")
	@Override
    protected void onCreate( Bundle savedInstanceState ) {

        super.onCreate( savedInstanceState );
        
        // Use custom title bar
        requestWindowFeature( Window.FEATURE_CUSTOM_TITLE );
        setContentView( R.layout.activity_feed );
        getWindow().setFeatureInt( Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title );
        
        list = (ListView) findViewById( R.id.feedList );
        webView = (WebView) findViewById( R.id.feedWebView );
        courtesyText = (TextView) findViewById( R.id.feedCourtesy );
        titleText = (TextView) findViewById( R.id.title );
        loadingText = (TextView) findViewById( R.id.loading );
        divider = findViewById( R.id.feedDivider );
        
        adapter = new ArrayAdapter<Item>( this, android.R.layout.simple_list_item_2, items ) {
            
            @Override
            // Support shading and two text items
            public View getView( int position, View convertView, ViewGroup parent ) {
                
                // Got some help from http://stackoverflow.com/questions/11722885/what-is-difference-between-android-r-layout-simple-list-item-1-and-android-r-lay
                
            	Item item = (Item) this.getItem( position );
                
                convertView =  ( (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE) )
                		.inflate( android.R.layout.simple_list_item_2, parent, false );
                
                ( (TextView) convertView.findViewById( android.R.id.text1 ) ).setText( item.title );
                
                // Center see more text
                if ( ( position + 1 ) == items.size() && item.title.equals( "See More" ) ) {
                	
                	( (TextView) convertView.findViewById( android.R.id.text1 ) ).setGravity( Gravity.CENTER );
                	( (TextView) convertView.findViewById( android.R.id.text1 ) )
                		.setTextColor( getContext().getResources().getColor( R.color.darkBlue ) );
                	
                } else if ( position == 0 && item.title.equals( "Refresh" ) ) {
                	
                	( (TextView) convertView.findViewById( android.R.id.text1 ) ).setGravity( Gravity.CENTER );
                	( (TextView) convertView.findViewById( android.R.id.text1 ) )
                		.setTextColor( getContext().getResources().getColor( R.color.darkBlue ) );
                }
                
                // Shorten description
                String description = item.description;
                
                if ( description != null && description.length() > MAX_DESC_LENGTH )
                    description = description.substring( 0, MAX_DESC_LENGTH ) + "\u2026";
                
                ( (TextView) convertView.findViewById( android.R.id.text2 ) ).setText( description );
                
                if( position % 2 != 0 )
                    convertView.setBackgroundResource( R.color.gray );
                
                return convertView;
            }
        };
        
        list.setAdapter( adapter );
        
        webView.setWebViewClient( new WebViewClient() {
            
            @Override
            public boolean shouldOverrideUrlLoading (WebView view, String url) {
                
                // We need to start a new browser otherwise we will keep doing things in the
                // WebView that may be undesirable for the user. The override is necessary
                // because adding a custom WebViewClient takes away this standard behavior
                // without one.
                // Found out how the standard way is done through:
                // http://stackoverflow.com/questions/14665671/android-webview-open-certain-urls-inside-webview-the-rest-externally
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData( Uri.parse( url ) );
                startActivity( intent );
                
                return true;
            }
            
            @Override
            public void onPageFinished( WebView webview, String url ){
                
                super.onPageFinished( webview, url );
                loadingText.setVisibility( View.INVISIBLE );
            }
        });
        
        webView.getSettings().setJavaScriptEnabled( true );
        
        new RssTask().execute();
    }
    
    @Override
    protected void onSaveInstanceState( Bundle outState ) {
        
        webView.saveState( outState );
        outState.putBoolean( "viewingItem", viewingItem );
        super.onSaveInstanceState(outState);     
    }

    @Override
    protected void onRestoreInstanceState( Bundle state ) {
        
        webView.restoreState( state );
        viewingItem = state.getBoolean( "viewingItem" );
        
        // Keep showing article
        if ( viewingItem ) {

            titleText.setText( R.string.returnText ); 
            
            list.setVisibility( View.GONE );
            divider.setVisibility( View.GONE );
            courtesyText.setVisibility( View.GONE );
            webView.setVisibility( View.VISIBLE );
            loadingText.setVisibility( View.VISIBLE );
        }
        
        super.onRestoreInstanceState( state );    
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            
            if ( viewingItem ) {
                
                webView.setVisibility( View.GONE );
                list.setVisibility( View.VISIBLE );
                divider.setVisibility( View.VISIBLE );
                courtesyText.setVisibility( View.VISIBLE );
                
                viewingItem = false;
                titleText.setText( title );
                
                webView.loadUrl("about:blank");
                
                return true;
            }
        }
        
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu ) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate( R.menu.feed, menu );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item ) {

        switch ( item.getItemId() ) {
        case android.R.id.home:
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            NavUtils.navigateUpFromSameTask( this );
            return true;
        }
        return super.onOptionsItemSelected( item );
    }

    protected class RssTask extends AsyncTask<Void, Void, List<Item>> {

        public RssTask() {

        }

        @Override
        protected List<Item> doInBackground( Void... arg0 ) {

            // Some ideas borrowed from
            // http://stackoverflow.com/questions/11879208/how-to-show-a-rss-feed-url-in-android-app-in-eclipse

            List<Item> items = getItems();
            
            return items;
        }
        
        @Override
        
        protected void onPreExecute() {
            
            loadingText.setVisibility( View.VISIBLE );
        }
        
        @Override
        protected void onPostExecute( List<Item> items ) {
            
            adapter.clear();
            
            for ( int i = 0 ; i < items.size(); ++i )
                adapter.add( items.get( i ) );
            
            adapter.notifyDataSetChanged();
            
            loadingText.setVisibility( View.INVISIBLE );
        }
    }

    static protected class Item {

        protected String title, description, link;

        public Item( String title, String description, String link ) {

            this.title = title;
            this.description = description;
            this.link = link;
        }

        @Override
        public String toString() {
            
            return title;
        }
    }

    abstract protected List<Item> getItems();
}
