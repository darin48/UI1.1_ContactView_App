package edu.umn.contactviewer;

import java.util.ArrayList;

/**
 * For now you can only load all of the Contacts
 * or replace them all with a new list. Upgrades
 * to this interface would be updating a single
 * Contact, removing a Contact or creating a new Contact.
 * @author khopps
 *
 */
public interface ContactStorage {

	public ArrayList<Contact> loadContacts();
	public void storeContacts(ArrayList<Contact> contacts);
	
}
