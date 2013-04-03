package edu.umn.contactviewer;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
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
	private Map<Integer, Contact> contacts = new HashMap<Integer, Contact>();

	private class GetContactsTask extends AsyncTask<String, Void, LinkedList<Contact> >
	{
		private ContactListActivity context;
		
		public GetContactsTask(ContactListActivity context)
		{
			this.context = context;
		}

		@Override
		protected LinkedList<Contact> doInBackground(String... params)
		{
			LinkedList<Contact> result = new LinkedList<Contact>();
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
			catch (JsonSyntaxException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (JsonIOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (IllegalStateException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			httpClient.close();
			
			for (ServiceResult.Contact svc : serviceResult.contacts)
			{
				Contact contact = newContact(svc);
				contacts.put(contact.getID(), contact);
                result.add(contact);
			}
			
			return result;
		}
		
		@Override
		protected void onPostExecute(LinkedList<Contact> contacts)
		{
			ArrayAdapter<Contact> listAdapter = context.new ContactAdapter(context, R.layout.list_item, contacts);
	        // initialize the list view
	        context.setListAdapter(listAdapter);
	        
	        //WebContactRepository.this.contacts = contacts;
		}
		
	}
	
	protected Contact newContact(ServiceResult.Contact svc)
	{
		Contact result = new Contact(nextID++);
		
		result.setName(svc.name);
		result.setEmail(svc.email);
		result.setTitle(svc.title);
		result.setTwitterId(svc.twitterId);
		result.setPhone(svc.phone);
		result.setWebID(svc._id);
		
		return result;
	}

	@Override
	public void connect(Context context)
	{
		getContactsTask = new GetContactsTask((ContactListActivity)context);
		getContactsTask.execute("unused");
	}

	@Override
	public Contact newContact()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Contact lookupContact(int id)
	{
        Contact result = contacts.get(new Integer(id));
        return result;
	}

	@Override
	public void deleteContact(int id)
	{
		// TODO Auto-generated method stub

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
		// TODO Auto-generated method stub

	}

}
