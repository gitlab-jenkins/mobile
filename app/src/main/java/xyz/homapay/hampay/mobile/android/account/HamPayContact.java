package xyz.homapay.hampay.mobile.android.account;

public class HamPayContact {
	
	public String name;
	public String lastName;
	public String phone;
	public String mail;
	public long id;
	
	public HamPayContact(String name, String lastName, String mail, String phone) {
		this.name = name;
		this.lastName = lastName;
		this.mail = mail;
		this.phone = phone;
	}
}
