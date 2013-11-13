package com.android.projecte.townportal;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import android.annotation.SuppressLint;
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

@SuppressLint ( "SetJavaScriptEnabled")
public class EmploymentActivity extends Activity {

    private List<JobItem> jobItems = new Vector<JobItem>();
    private ArrayAdapter<JobItem> adapter;
    private ListView jobList;
    private WebView webView;
    private TextView courtesyText, titleText, loadingText;
    private View divider;
    
    private Boolean viewingJob = false;
    private String jobsSource;
    
    private LayoutInflater layoutInflater;
    
    final private Integer MAX_DESC_LENGTH = 200;
    
    @Override
    protected void onCreate( Bundle savedInstanceState ) {

        super.onCreate( savedInstanceState );
        
        // Use custom title bar
        requestWindowFeature( Window.FEATURE_CUSTOM_TITLE );
        setContentView( R.layout.activity_employment );
        getWindow().setFeatureInt( Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title );
        ((TextView) findViewById( R.id.title ) ).setText( R.string.empl_text );
        
        layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
        jobList = (ListView) findViewById( R.id.jobList );
        webView = (WebView) findViewById( R.id.emplWebView );
        courtesyText = (TextView) findViewById( R.id.emplCourtesy );
        titleText = (TextView) findViewById( R.id.title );
        loadingText = (TextView) findViewById( R.id.loading );
        divider = findViewById( R.id.emplDivider );
        jobsSource = getString( R.string.jobsRss );
        
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
        
        adapter = new ArrayAdapter<JobItem>( this, android.R.layout.simple_list_item_2, jobItems ) {
            
            @Override
            // Support shading and two text items
            public View getView( int position, View convertView, ViewGroup parent ) {
                
                // Got some help from http://stackoverflow.com/questions/11722885/what-is-difference-between-android-r-layout-simple-list-item-1-and-android-r-lay
                
                JobItem jobItem = (JobItem) this.getItem( position );
                
                convertView = layoutInflater.inflate( android.R.layout.simple_list_item_2, parent, false );
                
                ( (TextView) convertView.findViewById( android.R.id.text1 ) ).setText( jobItem.title );
                
                // Shorten description
                String description = jobItem.description;
                
                if ( description.length() > MAX_DESC_LENGTH )
                    description = description.substring( 0, MAX_DESC_LENGTH ) + "\u2026";
                
                ( (TextView) convertView.findViewById( android.R.id.text2 ) ).setText( description );
                
                if( position % 2 != 0 )
                    convertView.setBackgroundResource( R.color.gray );
                
                return convertView;
            }
        };
        
        jobList.setAdapter( adapter );
        jobList.setOnItemClickListener( new OnItemClickListener() {

            @Override
            public void onItemClick( AdapterView<?> adapterView, View view, int position, long id ) {

                viewingJob = true;
                titleText.setText( R.string.returnText );
                
                jobList.setVisibility( View.GONE );
                divider.setVisibility( View.GONE );
                courtesyText.setVisibility( View.GONE );
                loadingText.setVisibility( View.VISIBLE );
                
                // Transform link to mobile version for visibility purposes
                // http://stackoverflow.com/questions/1277157/java-regex-replace-with-capturing-group helped
                String url = ( (JobItem) adapterView.getItemAtPosition( position ) ).link.replace( "jobview", "m" );
                
                Pattern pattern = Pattern.compile(".com/.*-([0-9]+)\\.aspx");
                Matcher matcher = pattern.matcher( url );
                
                // Should always return true
                if ( matcher.find() )
                    url = matcher.replaceFirst( ".com/" + matcher.group( 1 ) );
                
                webView.loadUrl( url );
                webView.setVisibility( View.VISIBLE );
            }    
            
        });
        
        new RssTask().execute();
    }
    
    @Override
    protected void onSaveInstanceState( Bundle outState ) {
        
        webView.saveState( outState );
        outState.putBoolean( "viewingJob", viewingJob );
        super.onSaveInstanceState(outState);     
    }

    @Override
    protected void onRestoreInstanceState( Bundle state ) {
        
        webView.restoreState( state );
        viewingJob = state.getBoolean( "viewingJob" );
        
        // Keep showing article
        if ( viewingJob ) {

            titleText.setText( R.string.returnText ); 
            
            jobList.setVisibility( View.GONE );
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
            
            if ( viewingJob ) {
                
                webView.setVisibility( View.GONE );
                jobList.setVisibility( View.VISIBLE );
                divider.setVisibility( View.VISIBLE );
                courtesyText.setVisibility( View.VISIBLE );
                
                viewingJob = false;
                titleText.setText( R.string.empl_text );
                
                webView.loadUrl("about:blank");
                
                return true;
            }
        }
        
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu ) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate( R.menu.employment, menu );
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

    private class RssTask extends AsyncTask<Void, Void, List<JobItem>> {

        public RssTask() {

        }

        @Override
        protected List<JobItem> doInBackground( Void... arg0 ) {

            // Some ideas borrowed from
            // http://stackoverflow.com/questions/11879208/how-to-show-a-rss-feed-url-in-android-app-in-eclipse

            List<JobItem> jobItems = getJobItems();
            
            return jobItems;
        }
        
        @Override
        
        protected void onPreExecute() {
            
            loadingText.setVisibility( View.VISIBLE );
        }
        
        @Override
        protected void onPostExecute( List<JobItem> jobItems ) {
            
            adapter.clear();
            
            for ( int i = 0 ; i < jobItems.size(); ++i )
                adapter.add( jobItems.get( i ) );
            
            adapter.notifyDataSetChanged();
            
            loadingText.setVisibility( View.INVISIBLE );
        }
    }

    private class JobItem {

        private String title, description, link;

        public JobItem( String title, String description, String link ) {

            this.title = title;
            this.description = description;
            this.link = link;
        }

        @Override
        public String toString() {
            
            return title;
        }
    }
    
    private InputStream getWebContents( String url ) {
        
        DefaultHttpClient client = new DefaultHttpClient();
        HttpGet get = new HttpGet( url );

        HttpResponse response = null;

        try {
            response = client.execute( get );
            HttpEntity message = response.getEntity();
            return message.getContent();
            
        } catch ( ClientProtocolException e ) {
            e.printStackTrace();
        } catch ( IOException e ) {
            e.printStackTrace();
        }
       
        return null;
    }

    private List<JobItem> getJobItems() {
        
        List<JobItem> result = new Vector<JobItem>();

        // Create Document from RSS content
        try {
            
            Document rssDoc = Jsoup.parse( getWebContents( this.jobsSource ), "UTF-8", "", Parser.xmlParser() );
            
            Elements jobItems = rssDoc.select("item");
            
            for ( Element element : jobItems ) {
                
                String title = element.select( "title" ).get( 0 ).text();
                String description = element.select( "description" ).get( 0 ).text();
                String link = element.select( "link" ).get( 0 ).text();

                if ( title != null && link != null && description != null )
                    result.add( new JobItem( title, description, link ) );
            }
            
        } catch ( IOException e ) {
            
            // TODO Auto-generated catch block
            e.printStackTrace();
            
        }

        return result;
    }
}
