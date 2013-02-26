package edu.umn.contactviewer;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created with IntelliJ IDEA.
 * User: Darin
 * Date: 2/24/13
 * Time: 10:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class ContactDetailsActivity extends Activity {
    private Contact contact;
    private ContactStorage contacts = new LocalContactStorage(ContactDetailsActivity.this);
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail);

        contact = (Contact)getIntent().getSerializableExtra("contact");
        TextView nameView = (TextView)findViewById(R.id.name_value);
        TextView titleView = (TextView)findViewById(R.id.title_value);
        TextView phoneView = (TextView)findViewById(R.id.phone_value);
        TextView emailView = (TextView)findViewById(R.id.email_value);
        TextView twitterIdView = (TextView)findViewById(R.id.twitterId_value);
        nameView.setText(contact.getName());
        titleView.setText(contact.getTitle());
        phoneView.setText(contact.getPhone());
        emailView.setText(contact.getEmail());
        twitterIdView.setText(contact.getTwitterId());
        ToolbarConfig toolbar = new ToolbarConfig(this, contact.getName());
        Button rightButton = toolbar.getToolbarRightButton();
        rightButton.setText("Edit");
        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ContactDetailsActivity.this, "This would edit contact " + contact.getName(), Toast.LENGTH_LONG).show();
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

        Button deleteButton = (Button)findViewById(R.id.bottom_button);
        deleteButton.setText("Delete");
        //deleteButton.setVisibility(deleteButton.GONE);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("contact", contact);
                returnIntent.putExtra("contactIndex", getIntent().getIntExtra("contactIndex", -1));
                ContactDetailsActivity.this.setResult(RESULT_OK, returnIntent);
                finish();
            }
        });
    }

 //   @Override
 //   public boolean onCreateOptionsMenu(Menu menu) {
 //       getMenuInflater().inflate(android.R.menu.detail, menu);
 //       return true;
 //   }
}