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
	
	public LocalContactStorage(Context context) {
		this.context = context;
	}

	@Override
	public ArrayList<Contact> loadContacts() {
		ArrayList<Contact> result = new ArrayList<Contact>();
		
		try {
			FileInputStream fis = context.openFileInput(FILENAME);
			ObjectInputStream ois = new ObjectInputStream(fis);
			String gsonContacts = (String)ois.readObject();
			Gson gson = new Gson();
			Contact[] contacts = gson.fromJson(gsonContacts, Contact[].class);
			for (int i = 0; i < contacts.length; ++i)
				result.add(contacts[i]);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
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

}
