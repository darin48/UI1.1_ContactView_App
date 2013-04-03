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
    private Map<String, Contact> contacts = new HashMap<String, Contact>();

    public FileContactRepository(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public void connect(Context context) {
        FileInputStream fis = null;
        HashMap<String,Contact> newContacts = new HashMap<String,Contact>();
        try {
        	fis = context.openFileInput(fileName);
            ObjectInputStream ois = new ObjectInputStream(fis);
            String gsonContacts = (String) ois.readObject();
            Gson gson = new Gson();
            FileContact[] readContacts = gson.fromJson(gsonContacts,
                    FileContact[].class);
            
            nextID = getMaxID(readContacts) + 1;
            
            for (int i = 0; i < readContacts.length; ++i) {
                Contact contact = readContacts[i];
                try {
                    int contactID = Integer.parseInt(contact.getID());
                    if (contactID <= 0) {
                        Contact c = newContact();
                        c.copyFrom(contact);
                        contact = c;
                    }
                } catch (NumberFormatException e) {
                    Contact c = newContact();
                    c.copyFrom(contact);
                    contact = c;
                }
                newContacts.put(contact.getID(), contact);
            }
            contacts = newContacts;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private int getMaxID(Contact[] contacts)
    {
    	int maxID = 0;
        for (int i = 0; i < contacts.length; ++i) {
            Contact contact = contacts[i];
            if (FileContact.class.isAssignableFrom(contact.getClass())) {
                int id = Integer.parseInt(contact.getID());
                if (maxID < id)
                    maxID = id;
            }
        }
        return maxID;
    }

    @Override
    public Contact newContact() {
        int id = nextID++;
        Contact result = new FileContact(id);
        contacts.put(result.getID(), result);
        return result;
    }

    @Override
    public Contact lookupContact(String id) {
        Contact result = contacts.get(id);
        return result;
    }

    @Override
    public void deleteContact(String id) {
        contacts.remove(id);
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
