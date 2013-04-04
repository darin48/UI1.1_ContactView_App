package edu.umn.contactviewer;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import android.app.ProgressDialog;
import android.os.Handler;
import android.widget.ProgressBar;
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
	private Map<String, WebContact> contacts = new HashMap<String, WebContact>();
	private ArrayList<Listener> listeners = new ArrayList<Listener>();

    private static final int PROGRESS = 0x1;
    private ProgressDialog mProgressDialog;
    private int mProgressStatus = 0;
    private Handler mHandler = new Handler();
    private Context myContext;

    //THIS IS OUR ASYNC CLASS FOR GETTING CONTACTS
	private class GetContactsTask extends AsyncTask<Void, Void, ServiceResult>
	{
        @Override
        protected void onPreExecute() {
            mProgressDialog = new ProgressDialog(myContext);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage("Downloading contacts ...");
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.show();
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
                WebContact contact = (WebContact)getContact(svc);
                contacts.put(contact.getID(), contact);
            }
            mProgressDialog.dismiss();
            notifyListeners();
        }
    }

    //THIS IS OUR ASYNC CLASS FOR UPDATING/INSERTING/DELETING CONTACTS
    private class UpdateContactsTask extends AsyncTask<Void, Void, ServiceResult>
    {

        @Override
        protected void onPreExecute() {
            mProgressDialog = new ProgressDialog(myContext);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage("Synchronizing server ...");
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.show();
        }

        @Override
        protected ServiceResult doInBackground(Void... params)
        {
            AndroidHttpClient httpClient = AndroidHttpClient.newInstance("Android", null);

            ArrayList<String> toDelete = new ArrayList<String>();
            ArrayList<WebContact> toAdd = new ArrayList<WebContact>();
            ServiceResult serviceResult = null; // out here just to be visible during debugging
            Gson gson = new Gson();
            for (WebContact c : contacts.values())
            {
            	if (!c.getIsDirty())
            		continue;

            	try
				{
		            HttpResponse response = null;

		            if (c.getIsDeleted())
					{
						toDelete.add(c.getID());
						
						if (!c.getIsNew())
						{
							String url = URL_BASE + "contacts/" + c.getID() + "?key=" + API_KEY;
							HttpUriRequest request = new HttpDelete(url);
							response = httpClient.execute(request);
						    serviceResult = gson.fromJson(
						            new InputStreamReader(response.getEntity().getContent()),
						            ServiceResult.class);
						}
					}
					else if (c.getIsNew())
					{
					    //*** INSERTING CONTACTS - POST ***
						String baseURL = URL_BASE + "contacts?key=" + API_KEY;
					    String urlParams = "&name=" + URLEncoder.encode(c.getName(), "utf-8")
					    		+ "&title=" + URLEncoder.encode(c.getTitle(), "utf-8")
					    		+ "&email=" + URLEncoder.encode(c.getEmail(), "utf-8")
					    		+ "&phone=" + URLEncoder.encode(c.getPhone(), "utf-8")
					    		+ "&twitterId=" + URLEncoder.encode(c.getTwitterId(), "utf-8");
					    String url = baseURL + urlParams;
					    HttpUriRequest request = new HttpPost(url);
					    response = httpClient.execute(request);
					    serviceResult = gson.fromJson(
					            new InputStreamReader(response.getEntity().getContent()),
					            ServiceResult.class);
					    toDelete.add(c.getID());
					    c.setID(serviceResult.contact._id);
					    c.MarkAsOld();
					    toAdd.add(c);
					}
					else
					{
					    //*** UPDATING CONTACTS - PUT ***
					    String baseURL = URL_BASE + "contacts/" + c.getID() + "?key=" + API_KEY;
					    String urlParams = "&name=" + URLEncoder.encode(c.getName(), "utf-8")
					    		+ "&title=" + URLEncoder.encode(c.getTitle(), "utf-8")
					    		+ "&email=" + URLEncoder.encode(c.getEmail(), "utf-8")
					    		+ "&phone=" + URLEncoder.encode(c.getPhone(), "utf-8")
					    		+ "&twitterId=" + URLEncoder.encode(c.getTwitterId(), "utf-8");
					    String url = baseURL + urlParams;
					    HttpUriRequest request = new HttpPut(url);
					    response = httpClient.execute(request);
					    serviceResult = gson.fromJson(
					            new InputStreamReader(response.getEntity().getContent()),
					            ServiceResult.class);
					     
					    c.MarkAsOld();
					}
				}
				catch (Exception e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
             }

            httpClient.close();
            
            for (String id : toDelete)
            {
            	contacts.remove(id);
            }
            
            for (WebContact c : toAdd)
            {
            	contacts.put(c.getID(), c);
            }

            return null;
        }

        @Override
        protected void onPostExecute(ServiceResult serviceResult)
        {
            mProgressDialog.dismiss();
            notifyListeners();
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
        myContext = context;
		refresh();
	}

	@Override
	public Contact newContact()
	{
		WebContact result = new WebContact();
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
            updateContactsTask = new UpdateContactsTask();
            updateContactsTask.execute();
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
	}

	@Override
	public void addListener(Listener listener)
	{
		listeners.add(listener);
	}

	@Override
	public void notifyListeners()
	{
		for (Listener listener : listeners)
			listener.notifyRepositoryChanged();
	}
	
	@Override
	public void refresh()
	{
		getContactsTask = new GetContactsTask();
		getContactsTask.execute();
	}

}
