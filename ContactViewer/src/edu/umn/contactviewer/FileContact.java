package edu.umn.contactviewer;

import java.io.Serializable;

/** This is my comment. Model class for storing a single contact.
 *
 */
public class FileContact implements Serializable, Contact {
	
	private static final long serialVersionUID = 1L;
	
	private final String _id;
	private String _webID = "";
	private String _name = "";
	private String _phone = "";
	private String _title = "";
	private String _email = "";
	private String _twitterId = "";
	
	protected FileContact(int id)
	{
        Integer newID = new Integer(id);
		_id = newID.toString();
	}
	
	public String getID()
	{
		return _id;
	}
	
	/** Set the contact's name.
	 */
	public Contact setName(String name) {
		_name = name;
		return this;
	}

	/** Get the contact's name.
	 */
	public String getName() {
		return _name;
	}

	/**
	 * @return the contact's phone number
	 */
	public String getPhone() {
		return _phone;
	}

	/** Set's the contact's phone number.
	 */
	public Contact setPhone(String phone) {
		_phone = phone;
		return this;
	}
	
	/**
	 * @return The contact's title
	 */
	public String getTitle() {
		return _title;
	}

	/** Sets the contact's title.
	 */
	public Contact setTitle(String title) {
		_title = title;
		return this;
	}
	
	/**
	 * @return the contact's e-mail address
	 */
	public String getEmail() {
		return _email;
	}

	/** Sets the contact's e-mail address.
	 */
	public Contact setEmail(String email) {
		_email = email;
		return this;
	}

	/**
	 * @return the contact's Twitter ID
	 */
	public String getTwitterId() {
		return _twitterId;
	}

	/** Sets the contact's Twitter ID.
	 */
	public Contact setTwitterId(String twitterId) {
		_twitterId = twitterId;
		return this;
	}

	public void copyFrom(Contact contact) {
		_name = contact.getName();
		_phone = contact.getPhone();
		_title = contact.getTitle();
		_email = contact.getEmail();
		_twitterId = contact.getTwitterId();
	}
	
	public String toString() {
		return _name + " " + _title + " " + _phone + " " + _email + " " + _twitterId;
	}
}

