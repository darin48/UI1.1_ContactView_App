package edu.umn.contactviewer;

import java.io.Serializable;
import java.util.LinkedList;

import android.content.Context;

public interface ContactRepository extends Serializable {

    public void connect(Context context);
    public Contact newContact();
    public Contact lookupContact(int id); // may return null
    public void deleteContact(int id); // NOP if non-existent

    public LinkedList<Contact> getAllContacts();

    public void flush(Context context); // ensure that the data is synced to the media

}
