package com.android.projecte.townportal;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Vector;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class NewsActivity extends Activity {

    private List<NewsItem> newsItems = new Vector<NewsItem>();
    private ArrayAdapter<NewsItem> adapter;
    private ListView newsList;
    private WebView webView;
    private TextView courtesyText, titleText, loadingText;
    private View divider;
    
    private Boolean viewingArticle = false;
    private String newsSource;
    
    private LayoutInflater layoutInflater;
    
    @Override
    protected void onCreate( Bundle savedInstanceState ) {

        super.onCreate( savedInstanceState );
        
        // Use custom title bar
        requestWindowFeature( Window.FEATURE_CUSTOM_TITLE );
        setContentView( R.layout.activity_news );
        getWindow().setFeatureInt( Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title );
        ((TextView) findViewById( R.id.title ) ).setText( R.string.news_text );
        
        layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
        newsList = (ListView) findViewById( R.id.newsList );
        webView = (WebView) findViewById( R.id.webView );
        courtesyText = (TextView) findViewById( R.id.courtesy );
        titleText = (TextView) findViewById( R.id.title );
        loadingText = (TextView) findViewById( R.id.loading );
        divider = findViewById( R.id.divider );
        newsSource = getString( R.string.newsSource );
        
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
        
        adapter = new ArrayAdapter<NewsItem>( this, android.R.layout.simple_list_item_2, newsItems ) {
            
            @Override
            // Support shading and two text items
            public View getView( int position, View convertView, ViewGroup parent ) {
                
                // Got some help from http://stackoverflow.com/questions/11722885/what-is-difference-between-android-r-layout-simple-list-item-1-and-android-r-lay
                
                NewsItem newsItem = (NewsItem) this.getItem( position );
                
                convertView = layoutInflater.inflate( android.R.layout.simple_list_item_2, parent, false );
                
                ( (TextView) convertView.findViewById( android.R.id.text1 ) ).setText( newsItem.title );
                ( (TextView) convertView.findViewById( android.R.id.text2 ) ).setText( newsItem.description );
                
                if( position % 2 != 0 )
                    convertView.setBackgroundResource( R.color.gray );
                
                return convertView;
            }
        };
        
        newsList.setAdapter( adapter );
        newsList.setOnItemClickListener( new OnItemClickListener() {

            @Override
            public void onItemClick( AdapterView<?> adapterView, View view, int position, long id ) {

                viewingArticle = true;
                titleText.setText( R.string.returnText );
                
                newsList.setVisibility( View.GONE );
                divider.setVisibility( View.GONE );
                courtesyText.setVisibility( View.GONE );
                loadingText.setVisibility( View.VISIBLE );
                
                // Load mobile version of article for visibility purposes
                webView.loadUrl( ( (NewsItem) adapterView.getItemAtPosition( position ) ).link.replaceFirst( "www", "m" ) );
                webView.setVisibility( View.VISIBLE );
            }    
            
        });
        
        new RssTask().execute();
    }
    
    @Override
    protected void onSaveInstanceState( Bundle outState ) {
        
        webView.saveState( outState );
        outState.putBoolean( "viewingArticle", viewingArticle );
        super.onSaveInstanceState(outState);     
    }

    @Override
    protected void onRestoreInstanceState( Bundle state ) {
        
        webView.restoreState( state );
        viewingArticle = state.getBoolean( "viewingArticle" );
        
        // Keep showing article
        if ( viewingArticle ) {

            titleText.setText( R.string.returnText ); 
            
            newsList.setVisibility( View.GONE );
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
            
            if ( viewingArticle ) {
                
                webView.setVisibility( View.GONE );
                newsList.setVisibility( View.VISIBLE );
                divider.setVisibility( View.VISIBLE );
                courtesyText.setVisibility( View.VISIBLE );
                
                viewingArticle = false;
                titleText.setText( R.string.news_text );
                
                webView.loadUrl("about:blank");
                
                return true;
            }
        }
        
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu ) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate( R.menu.news, menu );
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

    private class RssTask extends AsyncTask<Void, Void, List<NewsItem>> {

        public RssTask() {

        }

        @Override
        protected List<NewsItem> doInBackground( Void... arg0 ) {

            // Some ideas borrowed from
            // http://stackoverflow.com/questions/11879208/how-to-show-a-rss-feed-url-in-android-app-in-eclipse

            List<NewsItem> newsItems = getNewsItems();
            
            return newsItems;
        }
        
        @Override
        
        protected void onPreExecute() {
            
            loadingText.setVisibility( View.VISIBLE );
        }
        
        @Override
        protected void onPostExecute( List<NewsItem> newsItems ) {
            
            adapter.clear();
            
            for ( int i = 0 ; i < newsItems.size(); ++i )
                adapter.add( newsItems.get( i ) );
            
            adapter.notifyDataSetChanged();
            
            loadingText.setVisibility( View.INVISIBLE );
        }
    }

    private class NewsItem {

        private String title, description, link;

        public NewsItem( String title, String description, String link ) {

            this.title = title;
            this.description = description;
            this.link = link;
        }

        @Override
        public String toString() {
            
            return title;
        }
    }

    private List<NewsItem> getNewsItems() {
        
        List<NewsItem> result = new Vector<NewsItem>();

        // Create Document from XML content
        try {
            
            Document htmlDoc = Jsoup.connect( String.format( newsSource + "default.aspx?section=%s", 
                    getString( R.string.topNews ) ) ).get();
            
            Elements newsItems = htmlDoc.select("li[data-icon]");
            
            for ( Element element : newsItems ) {
                
                String title = element.select( "div[id=storySummary] h1, h2, h3, h4, h5, h6" ).get( 0 ).text();
                String description = element.select( "div[id=storySummary] p" ).get( 0 ).text();
                String link = null;
                
                // Get true link
                List<NameValuePair> uriPairs = URLEncodedUtils.parse( new URI( newsSource + element.select( "a" ).get( 0 ).attr( "href" ) ),
                        "UTF-8" );
                for ( NameValuePair nvp : uriPairs ) {
                    
                    if ( nvp.getName().equalsIgnoreCase( "link" ) ) {
                        
                        link = nvp.getValue();
                        break;
                    }
                }

                if ( title != null && link != null && description != null )
                    result.add( new NewsItem( title, description, link ) );
            }
            
        } catch ( IOException e ) {
            
            // TODO Auto-generated catch block
            e.printStackTrace();
            
        } catch ( URISyntaxException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return result;
    }
}
