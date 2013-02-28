package edu.umn.contactviewer;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.AdapterView.*;

/** Displays a list of contacts.
 *
 */
public class ContactListActivity extends ListActivity implements OnItemClickListener {
    private static ContactRepository storage = null;
//    private List<Contact> contacts;
    private static final int DETAILS_REQUEST = 13;
    private static final int ADD_CONTACT = 21;
    public static final String CONTACT_ID = "contactID";
    public static final String REPOSITORY = "repository";
    ArrayAdapter<Contact> listAdapter = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        storage = new FileContactRepository("Contacts.txt");
         setContentView(R.layout.list);
        ToolbarConfig toolbar = new ToolbarConfig(this, "Contacts");
        
        EditText searchText = (EditText) findViewById(R.id.searchText);
        
        storage.connect(this);
//        contacts = storage.getAllContacts();

        // setup the about button
        Button button = toolbar.getToolbarRightButton();
        button.setText(R.string.about);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ContactListActivity.this, "This is a sample application made for SENG 5199-1 in the MSSE program.", Toast.LENGTH_LONG).show();
            }
        });
        Button leftButton = toolbar.getToolbarLeftButton();
        // Could change this to Add when we have the edit screen done
        leftButton.setText(R.string.addNew);
        leftButton.setOnClickListener(new View.OnClickListener() {
        	@Override
			public void onClick(View v) {
				//Toast.makeText(this, "Edit Button clicked!", Toast.LENGTH_LONG).show();
				Intent intent = new Intent(ContactListActivity.this, ContactEditActivity.class);
				intent.putExtra(CONTACT_ID, storage.newContact().getID());
				//intent.putExtra("contact", storage.newContact());
				//intent.putExtra(REPOSITORY, storage);
				startActivityForResult(intent, ADD_CONTACT);
			}
        });
        
        listAdapter = new ContactAdapter(this, R.layout.list_item, storage.getAllContacts());
        // initialize the list view
        setListAdapter(listAdapter);
        ListView lv = getListView();
        lv.setTextFilterEnabled(true);

        searchText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            	listAdapter.getFilter().filter(s);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                    int after) {
            }
            
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}
        }); 
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
        intent.putExtra(CONTACT_ID, ((Contact)view.getTag()).getID());
        //intent.putExtra(REPOSITORY, storage);
        startActivityForResult(intent, DETAILS_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);    //To change body of overridden methods use File | Settings | File Templates.
        //Contact contact;
        //int position;
        switch (requestCode) {
            case DETAILS_REQUEST:
                if (resultCode == RESULT_OK) {
                	//contacts = storage.getAllContacts();
                }
                break;
            case ADD_CONTACT:
                if (resultCode == RESULT_OK) {
                	listAdapter = new ContactAdapter(this, R.layout.list_item, getStorage().getAllContacts());
                    // initialize the list view
                    setListAdapter(listAdapter);
                }    
        }
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
            if (contact != null ) {
                ((TextView)item.findViewById(R.id.item_name)).setText(contact.getName());
                ((TextView)item.findViewById(R.id.item_title)).setText(contact.getTitle());
                ((TextView)item.findViewById(R.id.item_phone)).setText(contact.getPhone());
            }
            
            item.setTag(contact);
            return item;
        }
    }

    public static ContactRepository getStorage() {
        return storage;
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

    @Override
    public void onResume() {
        super.onResume();
        listAdapter.notifyDataSetChanged();

    }
}


