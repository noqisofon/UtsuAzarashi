package utsuazarashi.app;

import java.io.*;
import java.util.*;
import org.codehaus.jackson.map.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.widgets.*;
import twitter4j.*;
import twitter4j.auth.*;


/**
 * @author rihine
 * 
 */
public class UtsuAzarashi {
    /**
     *
     */
    public UtsuAzarashi() {

    }


    /**
     * 
     */
    private void createContents() {
        TwitterFactory factory = new TwitterFactory();
        display = new Display();
        main_window = new Shell( display );
        tweet_text = new Text( main_window, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL );
        twitter = factory.getInstance();

        // 
        // main_window
        // 
        main_window.setLayout( new GridLayout( 1, true ) );
        main_window.setText( "欝アザラシ" );
        main_window.setSize( 320, 240 );

        // 
        // tweet_text
        // 
        GridData grid_data1 = new GridData();
        grid_data1.horizontalAlignment = GridData.FILL;
        grid_data1.verticalAlignment = GridData.FILL;
        grid_data1.grabExcessHorizontalSpace = true;
        grid_data1.grabExcessVerticalSpace = true;
        tweet_text.setLayoutData( grid_data1 );

        // 
        // tweet_button
        // 
        tweet_button = new Button( main_window, SWT.NULL );
        tweet_button.setText( "ついーと" );
        tweet_button.addSelectionListener( new SelectionListener() {
            @Override
            public void widgetDefaultSelected(SelectionEvent se) {
                // TODO Auto-generated method stub
            }


            @Override
            public void widgetSelected(SelectionEvent se) {
                String text = tweet_text.getText();
                if ( text.length() == 0 ) {
                    showMessageBox( "つぶやく内容が無いよ" );

                    return ;
                }
                tweet( text );
                tweet_text.setText( "" );
            }
        } );
        GridData grid_data2 = new GridData();
        grid_data2.horizontalAlignment = GridData.END;
        grid_data2.verticalAlignment = GridData.BEGINNING;
        grid_data2.grabExcessHorizontalSpace = false;
        grid_data2.grabExcessVerticalSpace = false;
        tweet_button.setLayoutData( grid_data2 );

        // 
        // twitter
        //
        twitter.setOAuthConsumer( TwitterConfig.CONSUMER_KEY, TwitterConfig.CONSUMER_SECRET );

        File path = new File( System.getProperty( "user.home" ) + "/.config/UtsuAzarashi" );
        File file = new File( path, "user.json" );

        AccessToken access_token = null;

        if ( !file.exists() ) {
            PinEntryDialog dialog = new PinEntryDialog( main_window );

            access_token = dialog.open();
        } else {
            access_token = emisssionAccessToken( path );
        }
        twitter.setOAuthAccessToken( access_token );
    }


    /**
     * 
     */
    public void run() {
        createContents();

        main_window.open();
        main_window.layout();

        while ( !main_window.isDisposed() ) {
            if ( !this.display.readAndDispatch() ) {
                this.display.sleep();
            }
        }
        this.display.dispose();
    }


    /**
     *
     */
    public static void main(String[] args) {
        UtsuAzarashi progn = new UtsuAzarashi();

        progn.run();
    }


    /**
     * 
     */
    private int showMessageBox(String text) {
        MessageBox message_box = new MessageBox( main_window );

        message_box.setMessage( text );

        return message_box.open();
    }


    /**
     * 
     */
    private Status tweet(String tweeting_text) {
        Status status = null;
        try {
            status = twitter.updateStatus( tweeting_text );
        } catch ( TwitterException e ) {
            e.printStackTrace();

            return null;
        }
        return status;
    }


    /**
     * 
     */
    private static AccessToken emisssionAccessToken(File path) {
        AccessToken access_token = null;
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> data = null;

        File file = new File( path, "user.json" );
        if ( file.exists() ) {
            try {
                data = mapper.readValue( file, Map.class );
            } catch ( IOException ie ) {
                ie.printStackTrace();

                return null;
            }
        } else {
            return null;
        }

        String screen_name = (String)data.get( "screenName" );

        System.out.println( "screen name is " + screen_name );

        file = new File( path, screen_name + ".json" );
        if ( file.exists() ) {
            try {
                data = mapper.readValue( file, Map.class );
            } catch ( IOException ie ) {
                ie.printStackTrace();

                return null;
            }
        } else {
            return null;
        }

        access_token = new AccessToken( (String)data.get( "token" ), (String)data.get( "tokenSecret" ) );

        return access_token;
    }

    private Display display;
    private Shell   main_window;
    private Text    tweet_text;
    private Button  tweet_button;
    private Twitter twitter;
}
