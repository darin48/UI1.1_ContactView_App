package edu.umn.contactviewer;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import edu.umn.contactviewer.ContactListActivity.ContactAdapter;

import android.content.Context;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.widget.ArrayAdapter;

public class WebContactRepository implements ContactRepository
{
	private int nextID = 1;
	private static final String URL_BASE = "http://contacts.tinyapollo.com/";
	private static final String API_KEY = "ui1_1";
	private GetContactsTask getContactsTask = null;
    private UpdateContactsTask updateContactsTask = null;
	private Map<String, Contact> contacts = new HashMap<String, Contact>();

    //THIS IS OUR ASYNC CLASS FOR GETTING CONTACTS
	private class GetContactsTask extends AsyncTask<Void, Void, ServiceResult>
	{
		private ContactListActivity context;
		
		public GetContactsTask(ContactListActivity context)
		{
			this.context = context;
		}

		@Override
		protected ServiceResult doInBackground(Void... params)
		{
			//LinkedList<Contact> result = new LinkedList<Contact>();
			ServiceResult serviceResult = null;
			
			AndroidHttpClient httpClient = AndroidHttpClient.newInstance("Android", null);
			HttpUriRequest request = new HttpGet(URL_BASE + "contacts" + "?key=" + API_KEY);
			try
			{
				HttpResponse response = httpClient.execute(request);
				Gson gson = new Gson();
				serviceResult = gson.fromJson(
						 new InputStreamReader(response.getEntity().getContent()),
						 ServiceResult.class);
			}
            catch (Exception e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
			
			httpClient.close();
			
			//for (ServiceResult.Contact svc : serviceResult.contacts)
			//{
			//	Contact contact = newContact(svc);
			//	contacts.put(contact.getID(), contact);
            //  result.add(contact);
			//}

			return serviceResult;
		}

        @Override
        protected void onPostExecute(ServiceResult serviceResult)
        {
            contacts.clear(); // clear out the original list, so we can reload it
            for (ServiceResult.Contact svc : serviceResult.contacts)
            {
                Contact contact = getContact(svc);
                contacts.put(contact.getID(), contact);
            }

            //We only need to convert to a LinkedList for the ContactAdapter
            ArrayAdapter<Contact> listAdapter = context.new ContactAdapter(context, R.layout.list_item, getAllContacts());
            // initialize the list view
            context.setListAdapter(listAdapter);
        }
    }

    //THIS IS OUR ASYNC CLASS FOR UPDATING/INSERTING/DELETING CONTACTS
    private class UpdateContactsTask extends AsyncTask<Void, Void, ServiceResult>
    {
        private Context context;

        public UpdateContactsTask(Context context)
        {
            this.context = context;
        }

        @Override
        protected ServiceResult doInBackground(Void... params)
        {
            ServiceResult serviceResult = null;
            AndroidHttpClient httpClient = AndroidHttpClient.newInstance("Android", null);

            for (Contact c : contacts.values())
            {
                //*** DELETING CONTACTS - DELETE ***
                if (c.getIsDirty() && c.getIsDeleted() && !c.getIsNew())
                {

                }
                //*** INSERTING CONTACTS - POST ***
                else if (c.getIsDirty() && c.getIsNew())
                {

                }
                //*** UPDATING CONTACTS - PUT ***
                else if (c.getIsDirty())
                {
                    String baseURL = URL_BASE + "contacts/" + c.getID() + "?key=" + API_KEY;
                    String urlParams = "&name=" + c.getName();
                    try
                    {
                        String URLUTF = baseURL + URLEncoder.encode(urlParams, "utf-8");
                        HttpUriRequest request = new HttpPut(URLUTF);
                        HttpResponse response = httpClient.execute(request);
                        Gson gson = new Gson();

                        serviceResult = gson.fromJson(
                                new InputStreamReader(response.getEntity().getContent()),
                                ServiceResult.class);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }

            httpClient.close();

            return null;
        }

        @Override
        protected void onPostExecute(ServiceResult serviceResult)
        {
            // DO NOTHING, Contacts are updated inside the doInBackground
            //serviceResult will always be NULL

            //We only need to convert to a LinkedList for the ContactAdapter
            //ArrayAdapter<Contact> listAdapter = context.new ContactAdapter(context, R.layout.list_item, getAllContacts());
            // initialize the list view
            //context.setListAdapter(listAdapter);
        }
    }

	protected Contact getContact(ServiceResult.Contact svc)
	{
		Contact result = new WebContact(svc._id);
		
		result.setName(svc.name);
		result.setEmail(svc.email);
		result.setTitle(svc.title);
		result.setTwitterId(svc.twitterId);
		result.setPhone(svc.phone);
        result.MarkAsOld();//mark this contact as old since it came in from the data store

		return result;
	}

	@Override
	public void connect(Context context)
	{
		getContactsTask = new GetContactsTask((ContactListActivity)context);
		getContactsTask.execute();
	}

	@Override
	public Contact newContact()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Contact lookupContact(String id)
	{
        Contact result = contacts.get(id);
        return result;
	}

	@Override
	public void deleteContact(String id)
	{
		// TODO Auto-generated method stub
        Contact c = lookupContact(id);
        c.MarkAsDeleted();
        //contacts.remove(id);
	}

	@Override
	public LinkedList<Contact> getAllContacts()
	{
        LinkedList<Contact> contactsList = new LinkedList<Contact>();
        for (Contact contact : contacts.values()) {
            contactsList.add(contact);
        }
        return contactsList;
	}

	@Override
	public void flush(Context context)
	{
        try
        {
            updateContactsTask = new UpdateContactsTask(context);
            updateContactsTask.execute();
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
	}

}
