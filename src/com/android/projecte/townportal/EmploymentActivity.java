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

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class EmploymentActivity extends FeedActivity {
    
	private String jobsSource;
	
    final private Integer MAX_DESC_LENGTH = 200;
    
    @Override
    protected void onCreate( Bundle savedInstanceState ) {

        super.onCreate( savedInstanceState );
        
        jobsSource = getString( R.string.jobsRss );
		title = getString( R.string.empl_text );
		
		((TextView) findViewById( R.id.title ) ).setText( title );
		courtesyText.setText( getString( R.string.emplCourtesy ) );
        
        adapter = new ArrayAdapter<Item>( this, android.R.layout.simple_list_item_2, items ) {
            
            @Override
            // Support shading and two text items
            public View getView( int position, View convertView, ViewGroup parent ) {
                
                // Got some help from http://stackoverflow.com/questions/11722885/what-is-difference-between-android-r-layout-simple-list-item-1-and-android-r-lay
                
            	Item jobItem = (Item) this.getItem( position );
                
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
                
                // Transform link to mobile version for visibility purposes
                // http://stackoverflow.com/questions/1277157/java-regex-replace-with-capturing-group helped
                String url = ( (Item) adapterView.getItemAtPosition( position ) ).link.replace( "jobview", "m" );
                
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

    @Override
    protected List<Item> getItems() {
        
        List<Item> result = new Vector<Item>();

        // Create Document from RSS content
        try {
            
            Document rssDoc = Jsoup.parse( getWebContents( this.jobsSource ), "UTF-8", "", Parser.xmlParser() );
            
            Elements jobItems = rssDoc.select("item");
            
            for ( Element element : jobItems ) {
                
                String title = element.select( "title" ).get( 0 ).text();
                String description = element.select( "description" ).get( 0 ).text();
                String link = element.select( "link" ).get( 0 ).text();

                if ( title != null && link != null && description != null )
                    result.add( new Item( title, description, link ) );
            }
            
        } catch ( IOException e ) {
            
            // TODO Auto-generated catch block
            e.printStackTrace();
            
        }

        return result;
    }
}
