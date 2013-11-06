package com.android.projecte.townportal;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
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
    private TextView courtesyText;
    
    private Boolean viewingArticle = false;
    
    @Override
    protected void onCreate( Bundle savedInstanceState ) {

        super.onCreate( savedInstanceState );
        
        // Use custom title bar
        requestWindowFeature( Window.FEATURE_CUSTOM_TITLE );
        setContentView( R.layout.activity_news );
        getWindow().setFeatureInt( Window.FEATURE_CUSTOM_TITLE, R.layout.news_title );
        
        newsList = (ListView) findViewById( R.id.newsList );
        webView = (WebView) findViewById( R.id.webView );
        courtesyText = (TextView) findViewById( R.id.courtesy );
        
        adapter = new ArrayAdapter<NewsItem>( this, android.R.layout.simple_list_item_1, newsItems );
        newsList.setAdapter( adapter );
        newsList.setOnItemClickListener( new OnItemClickListener() {

            @Override
            public void onItemClick( AdapterView<?> adapterView, View view, int position, long id ) {

                viewingArticle = true;
                
                newsList.setVisibility( View.GONE );
                courtesyText.setVisibility( View.GONE );
                
                webView.loadUrl( ( (NewsItem) adapterView.getItemAtPosition( position ) ).link.replace( "www", "m" ) );
                webView.setVisibility( View.VISIBLE );
            }    
            
        });
        
        new RssTask().execute();
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            
            if ( viewingArticle ) {
                
                webView.setVisibility( View.GONE );
                newsList.setVisibility( View.VISIBLE );
                courtesyText.setVisibility( View.VISIBLE );
                
                viewingArticle = false;
                
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
        protected void onPostExecute( List<NewsItem> newsItems ) {
            
            adapter.clear();
            
            for ( int i = 0 ; i < newsItems.size(); ++i )
                adapter.add( newsItems.get( i ) );
            
            adapter.notifyDataSetChanged();
        }
    }

    private class NewsItem {

        private String title;
        private String link;

        public NewsItem(String title, String link) {

            this.title = title;
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
    
    private List<NewsItem> getNewsItems() {
        
        List<NewsItem> result = new Vector<NewsItem>();
        
        InputStream webResult = getWebContents( getString( R.string.rssUrl ) );
        
        if ( webResult != null ) {

            Document xmlDoc = null;
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();

            try {
                
                // Create Document from XML content
                DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
                xmlDoc = docBuilder.parse( webResult );

                // First node is RSS
                NodeList nodes = xmlDoc.getChildNodes().item( 0 ).getChildNodes();

                // Find channel node
                for ( int i = 0; i < nodes.getLength(); i++ ) {

                    if ( nodes.item( i ).getNodeName().equalsIgnoreCase( "channel" ) ) {

                        NodeList channelNodes = nodes.item( i ).getChildNodes();

                        for ( int j = 0; j < channelNodes.getLength(); j++ ) {

                            // Only add item nodes
                            if ( channelNodes.item( j ).getNodeName().equalsIgnoreCase( "item" ) ) {

                                String title = null;
                                String link = null;

                                NodeList itemNodes = channelNodes.item( j ).getChildNodes();

                                // Collect title and link of news item
                                for ( int k = 0; k < itemNodes.getLength(); k++ ) {

                                    if ( itemNodes.item( k ).getNodeName().equalsIgnoreCase( "title" ) )
                                        title = itemNodes.item( k ).getTextContent();

                                    if ( itemNodes.item( k ).getNodeName().equalsIgnoreCase( "link" ) )
                                        link = itemNodes.item( k ).getTextContent();
                                }
                                
                                // Add the item if we are able to pull both title and link
                                if ( title != null && link != null )
                                    result.add( new NewsItem( title, link ) );
                            }
                        }
                        
                        // Don't need to search anymore nodes
                        break;
                    }

                }

                } catch ( ParserConfigurationException e ) {
                    e.printStackTrace();
                    
                } catch ( SAXException e ) {
                    e.printStackTrace();
                    
                } catch ( IOException e ) {
                    e.printStackTrace();
                }
        }

        return result;
    }
}
