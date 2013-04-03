package edu.umn.contactviewer;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: Darin
 * Date: 4/3/13
 * Time: 12:44 AM
 * To change this template use File | Settings | File Templates.
 */
public class WebContact implements Serializable, Contact {

    private static final long serialVersionUID = 1L;

    private final String _id;
    private Boolean _isNew = Boolean.TRUE;
    private Boolean _isDirty = Boolean.TRUE;
    private Boolean _isDeleted = Boolean.FALSE;
    private String _name = "";
    private String _phone = "";
    private String _title = "";
    private String _email = "";
    private String _twitterId = "";

    protected WebContact(String id)
    {
        _id = id;
        MarkAsNew();// as of now, this is a new object
    }

    public String getID()
    {
        return _id;
    }

    /** Get the IsNew flag for this contact */
    public Boolean getIsNew(){
        return _isNew;
    }

    /** Mark this contact as a "New" contact */
    public Contact MarkAsNew(){
        if (!_isNew.equals(Boolean.TRUE))
            _isDirty = Boolean.TRUE;
        _isNew = Boolean.TRUE;
        return this;
    }

    /** Mark this contact as a "Old" contact */
    public Contact MarkAsOld(){
        _isNew = Boolean.FALSE;
        _isDirty = Boolean.FALSE;
        _isDeleted = Boolean.FALSE;
        return this;
    }

    /** Get the IsDirty flag for this contact */
    public Boolean getIsDirty(){
        return _isDirty;
    }

    /** Get the IsDeleted flag for this contact */
    public Boolean getIsDeleted(){
        return _isDeleted;
    }

    /** Mark this contact as a "Deleted" contact */
    public Contact MarkAsDeleted(){
        if (!_isDeleted.equals(Boolean.TRUE))
            _isDirty = Boolean.TRUE;
        _isDeleted = Boolean.TRUE;
        return this;
    }

    /** Get the contact's name.
     */
    public String getName() {
        return _name;
    }

    /** Set the contact's name.
     */
    public Contact setName(String name) {
        if (!_name.equals(name))
            _isDirty = Boolean.TRUE;
        _name = name;
        return this;
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
        if (!_phone.equals(phone))
            _isDirty = Boolean.TRUE;
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
        if (!_title.equals(title))
            _isDirty = Boolean.TRUE;
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
        if (!_email.equals(email))
            _isDirty = Boolean.TRUE;
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
        if (!_twitterId.equals(twitterId))
            _isDirty = Boolean.TRUE;
        _twitterId = twitterId;
        return this;
    }

    public void copyFrom(Contact contact) {
        _name = contact.getName();
        _phone = contact.getPhone();
        _title = contact.getTitle();
        _email = contact.getEmail();
        _twitterId = contact.getTwitterId();
        _isDirty = contact.getIsDirty();
        _isDeleted = contact.getIsDeleted();
        _isNew = contact.getIsNew();
    }

    public String toString() {
        return _name + " " + _title + " " + _phone + " " + _email + " " + _twitterId + " IsNew:" + _isNew.toString() + " IsDirty:" + _isDirty.toString() + " IsDel:" + _isDeleted.toString();
    }

}
