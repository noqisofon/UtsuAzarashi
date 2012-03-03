package utsuazarashi.app;

import java.io.*;
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
    public AccessToken open() {
        createContents();

        main_window.open();
        main_window.layout();

        Display display = getParent().getDisplay();

        while ( !main_window.isDisposed() ) {
            if ( !display.readAndDispatch() ) {
                display.sleep();
            }
        }

        return access_token;
    }


    /**
     *
     */
    private void createContents() {
        main_window = new Shell( getParent(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL );

        main_window.setText( "PIN 入力" );
        main_window.setSize( 200, 100 );
        main_window.setLayout( new GridLayout( 4, false ) );

        label = new Label( main_window, SWT.NULL );
        label.setText( "PIN:" );

        GridData grid_data1 = new GridData();
        grid_data1.horizontalAlignment = GridData.END;
        grid_data1.verticalAlignment = GridData.CENTER;

        label.setLayoutData( grid_data1 );

        pin_text = new Text( main_window, SWT.SINGLE | SWT.BORDER );

        GridData grid_data2 = new GridData();
        grid_data2.horizontalAlignment = GridData.FILL;
        grid_data2.horizontalSpan = 3;
        grid_data2.grabExcessHorizontalSpace = true;
        grid_data2.verticalAlignment = GridData.CENTER;

        pin_text.setLayoutData( grid_data2 );

        ok_button = new Button( main_window, SWT.NULL );
        ok_button.setText( "認証" );
        ok_button.addSelectionListener( new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent selectedEvent) {
                String pin = pin_text.getText();

                try {
                    if ( pin.length() > 0 ) {
                        access_token = twitter.getOAuthAccessToken( request_token, pin );
                    } else {
                        access_token = twitter.getOAuthAccessToken();
                    }
                } catch ( TwitterException te ) {
                    if ( 401 == te.getStatusCode() ) {
                        MessageBox message_box = new MessageBox( getParent() );

                        message_box.setMessage( "Unable to get the access token." );
                        message_box.open();

                        return;
                    } else {
                        te.printStackTrace();

                        return;
                    }
                }

                try {
                    storeAccessToken( twitter.verifyCredentials(), access_token );
                } catch ( TwitterException te ) {
                    te.printStackTrace();
                }
                main_window.close();
            }


            @Override
            public void widgetDefaultSelected(SelectionEvent selectedEvent) {}
        } );

        GridData grid_data3 = new GridData();
        grid_data3.horizontalSpan = 4;
        grid_data3.horizontalAlignment = GridData.END;
        grid_data3.verticalAlignment = GridData.END;
        grid_data3.widthHint = 40;

        ok_button.setLayoutData( grid_data3 );

        twitter = new TwitterFactory().getInstance();
        twitter.setOAuthConsumer( TwitterConfig.CONSUMER_KEY, TwitterConfig.CONSUMER_SECRET );
        try {
            request_token = twitter.getOAuthRequestToken();
        } catch ( TwitterException te ) {
            te.printStackTrace();

            return;
        }
        Program progn = Program.findProgram( "html" );
        progn.execute( request_token.getAuthorizationURL() );
    }


    /**
     *
     */
    private static void storeAccessToken(User user, AccessToken accessToken) {
        ObjectMapper mapper = new ObjectMapper();
        File path = new File( System.getProperty( "user.home" ) + "/.config/UtsuAzarashi" );

        try {
            mapper.writeValue( new File( path, "user.json" ), user );
            mapper.writeValue( new File( path, user.getScreenName() + ".json" ), accessToken );
        } catch ( IOException ie ) {
            ie.printStackTrace();
        }
    }

    private Shell        main_window;
    private Twitter      twitter;
    private RequestToken request_token;
    private AccessToken  access_token;
    private Label        label;
    private Text         pin_text;
    private Button       ok_button;
}
