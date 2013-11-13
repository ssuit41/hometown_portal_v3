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

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

public class EmploymentActivity extends FeedActivity {
    
	private String jobsSource;
    
    @Override
    protected void onCreate( Bundle savedInstanceState ) {

        super.onCreate( savedInstanceState );
        
        jobsSource = getString( R.string.jobsRss );
		title = getString( R.string.empl_text );
		viewMoreUrl = getString( R.string.jobsViewMore );
		
		((TextView) findViewById( R.id.title ) ).setText( title );
		courtesyText.setText( getString( R.string.emplCourtesy ) );
        
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
                
                // Transform link to mobile version for visibility purposes
                // http://stackoverflow.com/questions/1277157/java-regex-replace-with-capturing-group helped
                String url = item.link.replace( "jobview", "m" );
                
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
        
        // Allow user to refresh
        result.add( new Item( "Refresh", null, null ) );

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

        // Let user see more jobs
        result.add( new Item( "See More", null, null ) );
        
        return result;
    }
}
