package utsuazarashi.app;

import java.io.*;
import java.util.*;
import org.codehaus.jackson.map.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.program.*;
import org.eclipse.swt.widgets.*;
import twitter4j.*;
import twitter4j.auth.*;


public class PinEntryDialog extends Dialog {
    /**
     *
     */
    public PinEntryDialog(Shell parent, int style) {
        super( parent, style );
    }
    /**
     *
     */
    public PinEntryDialog(Shell parent) {
        this( parent, SWT.None );
    }


    /**
     * 
     */
    public int open() {
        createContents();

        this.shell_.open();
        this.shell_.layout();

        Display display = getParent().getDisplay();

        while ( !this.shell_.isDisposed() ) {
            if ( !display.readAndDispatch() ) {
                display.sleep();
            }
        }

        return SWT.OK;
    }


    /**
     *
     */
    private void createContents() {
        this.shell_ = new Shell( getParent(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL );

        this.shell_.setText( "PIN 入力" );
        this.shell_.setSize( 200, 100 );
        this.shell_.setLayout( new GridLayout( 4, false ) );

        this.label_ = new Label( this.shell_, SWT.NULL );
        this.label_.setText( "PIN:" );

        GridData grid_data1 = new GridData();
        grid_data1.horizontalAlignment = GridData.END;
        grid_data1.verticalAlignment = GridData.CENTER;

        this.label_.setLayoutData( grid_data1 );

        this.pin_text_ = new Text( this.shell_, SWT.SINGLE | SWT.BORDER );

        GridData grid_data2 = new GridData();
        grid_data2.horizontalAlignment = GridData.FILL;
        grid_data2.horizontalSpan = 3;
        grid_data2.grabExcessHorizontalSpace = true;
        grid_data2.verticalAlignment = GridData.CENTER;

        this.pin_text_.setLayoutData( grid_data2 );

        this.ok_button_ = new Button( this.shell_, SWT.NULL );
        this.ok_button_.setText( "認証" );
        this.ok_button_.addSelectionListener( new SelectionListener () {
                @Override
                public void widgetSelected(SelectionEvent selectedEvent) {
                    AccessToken access_token = null;

                    String pin = pin_text_.getText();

                    try {
                        if ( pin.length() > 0 ) {
                            access_token = twitter_.getOAuthAccessToken( request_token_, pin );
                        } else {
                            access_token = twitter_.getOAuthAccessToken();
                        }
                    } catch ( TwitterException te ) {
                        if ( 401 == te.getStatusCode() ) {
                            MessageBox message_box = new MessageBox( getParent() );

                            message_box.setMessage( "Unable to get the access token." );
                            message_box.open();

                            return;
                        } else {
                            te.printStackTrace();
                
                            return ;
                        }
                    }

                    try {
                        storeAccessToken( twitter_.verifyCredentials().getId(), access_token );
                    } catch ( TwitterException te ) {
                        te.printStackTrace();
                    }
                }


                @Override
                public void widgetDefaultSelected(SelectionEvent selectedEvent) {
                }
            } );

        GridData grid_data3 = new GridData();
        grid_data3.horizontalSpan = 4;
        grid_data3.horizontalAlignment = GridData.END;
        grid_data3.verticalAlignment = GridData.END;
        grid_data3.widthHint = 40;

        this.ok_button_.setLayoutData( grid_data3 );

        this.twitter_ = new TwitterFactory().getInstance();
        this.twitter_.setOAuthConsumer( TwitterConfig.CONSUMER_KEY, TwitterConfig.CONSUMER_SECRET );
        try {
            this.request_token_ = this.twitter_.getOAuthRequestToken();
        } catch ( TwitterException te ) {
            te.printStackTrace();

            return ;
        }
        Program progn = Program.findProgram( "html" );
        progn.execute( this.request_token_.getAuthorizationURL() );
    }


    /**
     *
     */
    private static void storeAccessToken(long userId, AccessToken accessToken) {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> user_data = new HashMap<String, Object>();

        user_data.put( "userId", userId );
        user_data.put( "accessToken", accessToken.getToken() );
        user_data.put( "accessSecret", accessToken.getTokenSecret() );

        try {
            File path = new File( System.getProperty( "user.home" ) + "/.config/UtsuAzarashi" );

            if ( !path.exists() ) {
                path.mkdir();
            }
            mapper.writeValue( new File( path, "user.json" ), user_data );
        } catch ( IOException ie ) {
            ie.printStackTrace();
        }
    }


    private Shell   shell_;
    private Twitter twitter_;
    private RequestToken request_token_;
    private Label label_;
    private Text pin_text_;
    private Button ok_button_;
}
