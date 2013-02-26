package edu.umn.contactviewer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import com.google.gson.Gson;

import android.app.Activity;
import android.content.Context;

public class LocalContactStorage implements ContactStorage {
	
	private static final String FILENAME = "Contacts.txt";
	private Context context;
    private ArrayList<Contact> contacts;
	
	public LocalContactStorage(Context context) {
		this.context = context;
	}

	@Override
	public boolean loadContacts() {
		ArrayList<Contact> result = new ArrayList<Contact>();
		
		try {
			FileInputStream fis = context.openFileInput(FILENAME);
            contacts = new ArrayList<Contact>();
			ObjectInputStream ois = new ObjectInputStream(fis);
			String gsonContacts = (String)ois.readObject();
			Gson gson = new Gson();
			Contact[] readContacts = gson.fromJson(gsonContacts, Contact[].class);
			for (int i = 0; i < readContacts.length; ++i)
				contacts.add(readContacts[i]);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return true;
	}

	@Override
	public void storeContacts(ArrayList<Contact> contacts) {
		int size = contacts.size();
		if (size > 0) {
			try {
				FileOutputStream fos = context.openFileOutput(FILENAME, Context.MODE_PRIVATE);
				ObjectOutputStream oos = new ObjectOutputStream(fos);
				Gson gson = new Gson();
				Contact[] array = new Contact[size];
				contacts.toArray(array);
				String gsonContacts = gson.toJson(array);
				oos.writeObject(gsonContacts);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
    @Override
    public Contact getContact(int contactIndex) {
        if (contacts == null) {
            loadContacts();
        }
        if (contacts.isEmpty()) {
            return null;
        } else {
            return contacts.get(contactIndex);
        }
    }
    @Override
    public ArrayList<Contact> getContacts() {
        if (contacts == null) {
            loadContacts();
        }
        return contacts;
    }
}
