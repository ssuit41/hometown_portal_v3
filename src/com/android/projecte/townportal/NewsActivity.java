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

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.TextView;

final public class NewsActivity extends FeedActivity {

	private String newsSource;
	
	@Override
    protected void onCreate( Bundle savedInstanceState ) {
		
		super.onCreate( savedInstanceState );
		
		newsSource = getString( R.string.newsSource );
		title = getString( R.string.news_text );
		
		((TextView) findViewById( R.id.title ) ).setText( title );
		courtesyText.setText( getString( R.string.newsCourtesy ) );
		
		adapter = new ArrayAdapter<Item>( this, android.R.layout.simple_list_item_2, items ) {
            
            @Override
            // Support shading and two text items
            public View getView( int position, View convertView, ViewGroup parent ) {
                
                // Got some help from http://stackoverflow.com/questions/11722885/what-is-difference-between-android-r-layout-simple-list-item-1-and-android-r-lay
                
            	Item item = (Item) this.getItem( position );
                
                convertView = layoutInflater.inflate( android.R.layout.simple_list_item_2, parent, false );
                
                ( (TextView) convertView.findViewById( android.R.id.text1 ) ).setText( item.title );
                ( (TextView) convertView.findViewById( android.R.id.text2 ) ).setText( item.description );
                
                if( position % 2 != 0 )
                    convertView.setBackgroundResource( R.color.gray );
                
                return convertView;
            }
    	};
        
        list.setAdapter( adapter );
		
		list.setOnItemClickListener( new OnItemClickListener() {

            @Override
            public void onItemClick( AdapterView<?> adapterView, View view, int position, long id ) {

            	viewingItem = true;
                titleText.setText( R.string.returnText );
                
                list.setVisibility( View.GONE );
                divider.setVisibility( View.GONE );
                courtesyText.setVisibility( View.GONE );
                loadingText.setVisibility( View.VISIBLE );
                
                // Load mobile version of article for visibility purposes
                webView.loadUrl( ( (Item) adapterView.getItemAtPosition( position ) ).link.replaceFirst( "www", "m" ) );
                webView.setVisibility( View.VISIBLE );
            }    
            
        });
	}
	
	@Override
	protected List<Item> getItems() {
		
		List<Item> result = new Vector<Item>();

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

        return result;
	}
}
