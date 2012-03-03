package utsuazarashi.app;

import java.io.*;
import java.util.*;

import org.codehaus.jackson.JsonParseException;
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
        this.display_ = new Display();
        this.shell_ = new Shell( this.display_ );

        this.shell_.setLayout( new GridLayout( 1, true ) );

        this.tweet_textbox_ = new Text( this.shell_, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL );

        GridData grid_data1 = new GridData();
        grid_data1.horizontalAlignment = GridData.FILL;
        grid_data1.verticalAlignment = GridData.FILL;
        grid_data1.grabExcessHorizontalSpace = true;
        grid_data1.grabExcessVerticalSpace = true;

        this.tweet_textbox_.setLayoutData( grid_data1 );

        this.tweet_button_ = new Button( this.shell_, SWT.NULL );
        this.tweet_button_.setText( "Update" );
        this.tweet_button_.addSelectionListener( new SelectionListener () {
                @Override
                public void widgetDefaultSelected(SelectionEvent se) {
                    // TODO Auto-generated method stub
                }


                @Override
                public void widgetSelected(SelectionEvent se) {
                    String text = tweet_textbox_.getText();
                    if ( text.length() == 0 ) {
                        MessageBox message_box = new MessageBox( shell_ );

                        message_box.setMessage( "つぶやく内容が無いよ" );
                        message_box.open();

                        return ;
                    }

                    try {
                        /*Status status = */twitter_.updateStatus( text );
                    } catch ( TwitterException e ) {
                        // TODO 自動生成された catch ブロック
                        e.printStackTrace();
                    }
                }
            } );

        GridData grid_data2 = new GridData();
        grid_data2.horizontalAlignment = GridData.END;
        grid_data2.verticalAlignment = GridData.BEGINNING;
        grid_data2.grabExcessHorizontalSpace = false;
        grid_data2.grabExcessVerticalSpace = false;

        this.tweet_button_.setLayoutData( grid_data2 );

        this.shell_.setText( "欝アザラシ" );
        this.shell_.setSize( 320, 240 );
        
        TwitterFactory factory = new TwitterFactory();
        AccessToken access_token = loadAccessToken();

        if ( access_token != null ) {
            this.twitter_ = factory.getInstance();
        }
    }


    /**
     * 
     */
    public void run() {
        createContents();

        this.shell_.open();
        this.shell_.layout();

        while ( !this.shell_.isDisposed() ) {
            if ( !this.display_.readAndDispatch() ) {
                this.display_.sleep();
            }
        }
        this.display_.dispose();
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
    private AccessToken loadAccessToken() {
        File file = new File( System.getProperty( "user.home" ) + "/.config/UtsuAzarashi/user.json" );

        if ( !file.exists() ) {
            PinEntryDialog dialog = new PinEntryDialog( this.shell_ );
            dialog.open();
        }

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> user_data = null;
        try {
            user_data = mapper.readValue( file, Map.class );
        } catch ( JsonParseException e ) {
            // TODO 自動生成された catch ブロック
            e.printStackTrace();
            
            return null;
        } catch ( JsonMappingException e ) {
            // TODO 自動生成された catch ブロック
            e.printStackTrace();
            
            return null;
        } catch ( IOException e ) {
            // TODO 自動生成された catch ブロック
            e.printStackTrace();
            
            return null;
        }

        return new AccessToken( (String)user_data.get( "accessToken" ), (String)user_data.get( "accessSecret" ) );
    }


    private Display display_;
    private Shell shell_;
    private Twitter twitter_;
    private Text tweet_textbox_;
    private Button tweet_button_;
}
