package edu.umn.contactviewer;

import java.io.Serializable;
import java.util.LinkedList;

import android.content.Context;

public interface ContactRepository extends Serializable {
	
	public static interface Listener
	{
		public void notifyRepositoryChanged();
	}

    public void connect(Context context);
    public Contact newContact();
    public Contact lookupContact(String id); // may return null
    public void deleteContact(String id); // NOP if non-existent

    public LinkedList<Contact> getAllContacts();

    public void flush(Context context); // ensure that the data is synced to the media
    
    public void addListener(Listener listener);
    
    public void notifyListeners();
    
    public void refresh();

}
