package edu.umn.contactviewer;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.AdapterView.*;

/** Displays a list of contacts.
 *
 */
public class ContactListActivity extends ListActivity implements OnItemClickListener {
    private ContactStorage storage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list);
        ToolbarConfig toolbar = new ToolbarConfig(this, "Contacts");

	    // setup the about button
	    Button button = toolbar.getToolbarRightButton();
	    button.setText("About");
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Toast.makeText(ContactListActivity.this, "This is a sample application made for SENG 5199-1 in the MSSE program.", Toast.LENGTH_LONG).show();
			}
		});
		
		storage = new LocalContactStorage(ContactListActivity.this);
        storage.loadContacts();
		//ArrayList<Contact> contacts = storage.loadContacts();
        
        // initialize the list view
        setListAdapter(new ContactAdapter(this, R.layout.list_item, storage.getContacts()));
        ListView lv = getListView();
        lv.setTextFilterEnabled(true);


        // handle the item click events
        lv.setOnItemClickListener(this);

    }

 /*   @Override
    protected void onListItemClick(ListView l,  View v,  int position, long id) {
        super.onListItemClick(l, v, position, id);

        Intent intent = new Intent(this, ContactDetailsActivity.class);
        intent.putExtra("contact", storage.getContact(position));
    }*/

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //To change body of implemented methods use File | Settings | File Templates.

        Intent intent = new Intent(this, ContactDetailsActivity.class);
        intent.putExtra("contact", storage.getContact(position));
        startActivity(intent);
    }

    /* We need to provide a custom adapter in order to use a custom list item view.
     */
	public class ContactAdapter extends ArrayAdapter<Contact> {
	
		public ContactAdapter(Context context, int textViewResourceId, List<Contact> objects) {
			super(context, textViewResourceId, objects);
		}
	
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = getLayoutInflater();
			View item = inflater.inflate(R.layout.list_item, parent, false);
			
			Contact contact = getItem(position);
			((TextView)item.findViewById(R.id.item_name)).setText(contact.getName());
			((TextView)item.findViewById(R.id.item_title)).setText(contact.getTitle());
			((TextView)item.findViewById(R.id.item_phone)).setText(contact.getPhone());
			
			return item;
		}
	}
	
/*	private ContactStorage newContactStorage()
	{
		return new ContactStorage() {

			@Override
			public void loadContacts() {
		        
		        // make some contacts
                ArrayList<Contact> contacts = new ArrayList<Contact>();
		        contacts.add(new Contact("Malcom Reynolds 2")
		    		.setEmail("mal@serenity.com")
		    		.setTitle("Captain")
		    		.setPhone("612-555-1234")
		    		.setTwitterId("malcomreynolds"));
		        contacts.add(new Contact("Zoe Washburne")
					.setEmail("zoe@serenity.com")
					.setTitle("First Mate")
					.setPhone("612-555-5678")
					.setTwitterId("zoewashburne"));
		        contacts.add(new Contact("Hoban Washburne")
					.setEmail("wash@serenity.com")
					.setTitle("Pilot")
					.setPhone("612-555-9012")
					.setTwitterId("wash"));
		        contacts.add(new Contact("Jayne Cobb")
					.setEmail("jayne@serenity.com")
					.setTitle("Muscle")
					.setPhone("612-555-3456")
					.setTwitterId("heroofcanton"));
		        contacts.add(new Contact("Kaylee Frye")
					.setEmail("kaylee@serenity.com")
					.setTitle("Engineer")
					.setPhone("612-555-7890")
					.setTwitterId("kaylee"));
		        contacts.add(new Contact("Simon Tam")
					.setEmail("simon@serenity.com")
					.setTitle("Doctor")
					.setPhone("612-555-4321")
					.setTwitterId("simontam"));
		        contacts.add(new Contact("River Tam")
					.setEmail("river@serenity.com")
					.setTitle("Doctor's Sister")
					.setPhone("612-555-8765")
					.setTwitterId("miranda"));
		        contacts.add(new Contact("Shepherd Book")
					.setEmail("shepherd@serenity.com")
					.setTitle("Shepherd")
					.setPhone("612-555-2109")
					.setTwitterId("shepherdbook"));

		        /**
		         * We could initialize the permanent contacts list this way: *//*
				LocalContactStorage storage = new LocalContactStorage(ContactListActivity.this);
                //storage.storeContacts(contacts);
				storage.loadContacts();

		        
		        //return contacts;
			}

			@Override
			public void storeContacts(ArrayList<Contact> contacts) {
			}
			
		};
	}            */
    
}


