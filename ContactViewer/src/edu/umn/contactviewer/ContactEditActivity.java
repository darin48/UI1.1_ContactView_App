package edu.umn.contactviewer;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created with IntelliJ IDEA.
 * User: dward
 * Date: 2/26/13
 * Time: 1:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class ContactEditActivity extends Activity {
    private Contact contact;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit);

        contact = (Contact)getIntent().getSerializableExtra("contact");

        EditText nameView = (EditText)findViewById(R.id.name_value);
        EditText titleView = (EditText)findViewById(R.id.title_value);
        EditText phoneView = (EditText)findViewById(R.id.phone_value);
        EditText emailView = (EditText)findViewById(R.id.email_value);
        EditText twitterIdView = (EditText)findViewById(R.id.twitterId_value);
        nameView.setText(contact.getName());
        titleView.setText(contact.getTitle());
        phoneView.setText(contact.getPhone());
        emailView.setText(contact.getEmail());
        twitterIdView.setText(contact.getTwitterId());

        ToolbarConfig toolbar = new ToolbarConfig(this, contact.getName());

        Button rightButton = toolbar.getToolbarRightButton();
        rightButton.setText("Save");
        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //*** WE NEED TO CHANGE THIS TO SEND OUR RESULTS BACK  ***
                finish();
            }
        });

        Button backButton = toolbar.getToolbarLeftButton();
        backButton.setText("Back");
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Button clearTextButton = (Button)findViewById(R.id.bottom_button);
        //clearTextButton.setVisibility(clearTextButton.GONE);
        clearTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText nameView = (EditText)findViewById(R.id.name_value);
                EditText titleView = (EditText)findViewById(R.id.title_value);
                EditText phoneView = (EditText)findViewById(R.id.phone_value);
                EditText emailView = (EditText)findViewById(R.id.email_value);
                EditText twitterIdView = (EditText)findViewById(R.id.twitterId_value);
                nameView.setText("");
                titleView.setText("");
                phoneView.setText("");
                emailView.setText("");
                twitterIdView.setText("");

                //finish();
            }
        });
    }
}