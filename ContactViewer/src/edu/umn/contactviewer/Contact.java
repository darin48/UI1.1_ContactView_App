package edu.umn.contactviewer;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: Darin
 * Date: 4/3/13
 * Time: 12:31 AM
 * To change this template use File | Settings | File Templates.
 */
public interface Contact {
    /** This is my comment. Model class for storing a single contact.
     *
     */
     public String getID();

     /** Set the contact's name.
     */
     public Contact setName(String name);

     /** Get the contact's name.
      */
     public String getName();

     /**
      * @return the contact's phone number
      */
     public String getPhone();

     /** Set's the contact's phone number.
      */
     public Contact setPhone(String phone);

    /**
     * @return The contact's title
     */
    public String getTitle();

    /** Sets the contact's title.
     */
    public Contact setTitle(String title);

    /**
     * @return the contact's e-mail address
     */
    public String getEmail();

    /** Sets the contact's e-mail address.
     */
    public Contact setEmail(String email);

    /**
     * @return the contact's Twitter ID
     */
    public String getTwitterId();

    /** Sets the contact's Twitter ID.
     */
    public Contact setTwitterId(String twitterId);

    public void copyFrom(Contact contact);

    public String toString();
}
