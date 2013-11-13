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

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

final public class NewsActivity extends FeedActivity {

	private String newsSource;
	
	@Override
    protected void onCreate( Bundle savedInstanceState ) {
		
		super.onCreate( savedInstanceState );
		
		newsSource = getString( R.string.newsSource );
		title = getString( R.string.news_text );
		viewMoreUrl = getString( R.string.newsViewMore );
		
		((TextView) findViewById( R.id.title ) ).setText( title );
		courtesyText.setText( getString( R.string.newsCourtesy ) );
		
		list.setOnItemClickListener( new OnItemClickListener() {

            @Override
            public void onItemClick( AdapterView<?> adapterView, View view, int position, long id ) {

            	Item item = (Item) adapterView.getItemAtPosition( position );
            	
            	if ( ( position + 1 ) == items.size() ) {
            		
            		Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData( Uri.parse( viewMoreUrl ) );
                    startActivity( intent );
                    
                    return;
                    
            	} else if ( position == 0 && item.title.equals( "Refresh" ) ) {
            		
            		new RssTask().execute();
            		return;
            	}
            	
            	viewingItem = true;
                titleText.setText( R.string.returnText );
                
                list.setVisibility( View.GONE );
                divider.setVisibility( View.GONE );
                courtesyText.setVisibility( View.GONE );
                loadingText.setVisibility( View.VISIBLE );
                
                // Load mobile version of article for visibility purposes
                webView.loadUrl( item.link.replaceFirst( "www", "m" ) );
                webView.setVisibility( View.VISIBLE );
            }    
            
        });
	}
	
	@Override
	protected List<Item> getItems() {
		
		List<Item> result = new Vector<Item>();

		// Allow user to refresh
		result.add( new Item( "Refresh", null, null ) );
		
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
                    result.add( new Item( title, description, link ) );
            }
            
        } catch ( IOException e ) {
            
            // TODO Auto-generated catch block
            e.printStackTrace();
            
        } catch ( URISyntaxException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        // Let user see more articles
        result.add( new Item( "See More", null, null ) );

        return result;
	}
}
