package edu.umn.contactviewer;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import android.content.Context;

import com.google.gson.Gson;

public class FileContactRepository implements ContactRepository, Serializable {
    private static final long serialVersionUID = 1L;

    private int nextID = 1;
    private String fileName = null;
    private Map<Integer, Contact> contacts = new HashMap<Integer, Contact>();

    public FileContactRepository(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public void connect(Context context) {
        FileInputStream fis = null;
        HashMap<Integer,Contact> newContacts = new HashMap<Integer,Contact>();
        try {
        	fis = context.openFileInput(fileName);
            ObjectInputStream ois = new ObjectInputStream(fis);
            String gsonContacts = (String) ois.readObject();
            Gson gson = new Gson();
            Contact[] readContacts = gson.fromJson(gsonContacts,
                    Contact[].class);
            
            nextID = getMaxID(readContacts) + 1;
            
            for (int i = 0; i < readContacts.length; ++i) {
                Contact contact = readContacts[i];
                 if (contact.getID() <= 0) {
                	Contact c = newContact();
                	c.copyFrom(contact);
                	contact = c;
                }
                newContacts.put(new Integer(contact.getID()), contact);
            }
            contacts = newContacts;
        } catch (Exception e) {
        }
    }
    
    private int getMaxID(Contact[] contacts)
    {
    	int maxID = 0;
        for (int i = 0; i < contacts.length; ++i) {
            Contact contact = contacts[i];
            int id = contact.getID();
             if (maxID < id)
                maxID = id;
        }
        return maxID;
    }

    @Override
    public Contact newContact() {
        int id = nextID++;
        Contact result = new Contact(id);
        contacts.put(new Integer(id), result);
        return result;
    }

    @Override
    public Contact lookupContact(int id) {
        Contact result = contacts.get(new Integer(id));
        return result;
    }

    @Override
    public void deleteContact(int id) {
        contacts.remove(new Integer(id));
    }

    @Override
    public LinkedList<Contact> getAllContacts() {
    	LinkedList<Contact> result = new LinkedList<Contact>();
        for (Contact contact : contacts.values()) {
        	result.add(contact);
        }
        return result;
    }

    @Override
    public void flush(Context context) {
        Contact[] array = new Contact[contacts.size()];
        contacts.values().toArray(array);
        Gson gson = new Gson();
        String gsonContacts = gson.toJson(array);
        FileOutputStream fos;
        try {
            fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(gsonContacts);
        } catch (Exception exc) {
            throw new RuntimeException("Cannot store contacts", exc);
        }
    }

}
