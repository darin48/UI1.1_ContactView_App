package edu.umn.contactviewer;

public class ServiceResult
{
	public String status;
	public String message;
	public Group group;
	public Contact[] contacts;
	public Contact contact;
	
	public static class Group
	{
		public String key;
		public String name;
		public String _id;
	};
	
	public static class Contact
	{
		public String groupId;
		public String name;
		public String title;
		public String email;
		public String phone;
		public String twitterId;
		public String _id;
	};
}
