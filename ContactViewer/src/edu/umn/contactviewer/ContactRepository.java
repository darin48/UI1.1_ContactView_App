package edu.umn.contactviewer;

public interface ContactRepository {

	public Contact newContact();
	public Contact lookupContact(int id); // may return null
	public void deleteContact(int id); // NOP if non-existent
	
	public int[] getAllContacts(); // not feasible for a real database
	public Contact[] getContacts(int[] ids);
	
	public void flush(); // ensure that the data is synced to the media
	
}
