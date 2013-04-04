package edu.umn.contactviewer;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
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
            AndroidHttpClient httpClient = AndroidHttpClient.newInstance("Android", null);

            ArrayList<Contact> toDelete = new ArrayList<Contact>();
            ServiceResult serviceResult = null; // out here just to be visible during debugging
            for (Contact c : contacts.values())
            {
            	if (c.getIsDirty())
            	{
 					try
					{
 		               	HttpResponse response = null;

 		               	if (c.getIsDeleted())
						{
							toDelete.add(c);
							
							if (!c.getIsNew())
							{
								String url = URL_BASE + "contacts/" + c.getID() + "?key=" + API_KEY;
								HttpUriRequest request = new HttpDelete(url);
								response = httpClient.execute(request);
							}
						}
						else if (c.getIsNew())
						{
						    //*** INSERTING CONTACTS - POST ***
							String baseURL = URL_BASE + "contacts?key=" + API_KEY;
						    String urlParams = "&name=" + c.getName()
						    		+ "&title=" + c.getTitle()
						    		+ "&email=" + c.getEmail()
						    		+ "&phone=" + c.getPhone()
						    		+ "&twitterId=" + c.getTwitterId();
						    String url = baseURL + URLEncoder.encode(urlParams, "utf-8");
						    HttpUriRequest request = new HttpPost(url);
						    response = httpClient.execute(request);
						}
						else
						{
						    //*** UPDATING CONTACTS - PUT ***
						    String baseURL = URL_BASE + "contacts/" + c.getID() + "?key=" + API_KEY;
						    String urlParams = "&name=" + c.getName()
						    		+ "&title=" + c.getTitle()
						    		+ "&email=" + c.getEmail()
						    		+ "&phone=" + c.getPhone()
						    		+ "&twitterId=" + c.getTwitterId();
						    String URLUTF = baseURL + URLEncoder.encode(urlParams, "utf-8");
						    HttpUriRequest request = new HttpPut(URLUTF);
						    response = httpClient.execute(request);
						     
						    c.MarkAsOld();
						}
						
						if (response != null)
						{
						    Gson gson = new Gson();

						    serviceResult = gson.fromJson(
						            new InputStreamReader(response.getEntity().getContent()),
						            ServiceResult.class);

						}
					}
					catch (Exception e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
            	}
             }

            httpClient.close();
            
            for (Contact c : toDelete)
            {
            	contacts.remove(c.getID());
            }

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
		Contact result = new WebContact();
		contacts.put(result.getID(), result);
		return result;
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
