package edu.umn.contactviewer;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

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
        try {
            fis = context.openFileInput(fileName);
            ObjectInputStream ois = new ObjectInputStream(fis);
            String gsonContacts = (String) ois.readObject();
            Gson gson = new Gson();
            Contact[] readContacts = gson.fromJson(gsonContacts,
                    Contact[].class);
            int maxID = readContacts[0].getID();
            for (int i = 0; i < readContacts.length; ++i) {
                Contact contact = readContacts[i];
                int id = contact.getID();
                if (maxID < id)
                    maxID = id;
                contacts.put(new Integer(id), contact);
            }
            nextID = maxID + 1;
        } catch (Exception e) {
        }
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
    public int[] getAllContacts() {
        int[] result = new int[contacts.size()];
        int n = 0;
        for (Integer key : contacts.keySet()) {
            result[n++] = key.intValue();
        }
        return result;
    }

    @Override
    public Contact[] getContacts(int[] ids) {
        Contact[] result = new Contact[ids.length];
        for (int i = 0; i < ids.length; ++i) {
            result[i] = lookupContact(ids[i]);
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
