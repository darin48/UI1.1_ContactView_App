package edu.umn.contactviewer;

import android.app.Activity;
import android.app.ListActivity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;

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

        ToolbarConfig toolbar = new ToolbarConfig(this, "Contacts");
        contact = (Contact)getIntent().getSerializableExtra("contact");
        TextView nameView = (TextView)findViewById(R.id.name_value);
        TextView titleView = (TextView)findViewById(R.id.title_value);
        TextView phoneView = (TextView)findViewById(R.id.phone_value);
        TextView emailView = (TextView)findViewById(R.id.email_value);
        TextView twitterIdView = (TextView)findViewById(R.id.twitterId_value);
        nameView.setText(contact.getName());
    }

 //   @Override
 //   public boolean onCreateOptionsMenu(Menu menu) {
 //       getMenuInflater().inflate(android.R.menu.detail, menu);
 //       return true;
 //   }
}