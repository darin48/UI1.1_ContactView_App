package edu.umn.contactviewer;

import java.io.Serializable;

import android.content.Context;

public interface ContactRepository extends Serializable {

    public void connect(Context context);
    public Contact newContact();
    public Contact lookupContact(int id); // may return null
    public void deleteContact(int id); // NOP if non-existent

    public int[] getAllContacts(); // not feasible for a real database
    public Contact[] getContacts(int[] ids);

    public void flush(Context context); // ensure that the data is synced to the media

}
