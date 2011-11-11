package net.wdmsfunc;

import java.util.ArrayList;

public class User {

	private int userid;
	private String firstname;
	private String lastname;
	private String email;
	private String password;
	private String designation;
	private int privilege;
	private ArrayList<Integer> department_ids = new ArrayList<Integer>();
	
	User(int userid, String firstname, String lastname, String email, String password, String designation, int privilege, ArrayList<Integer> department_ids) {
		this.userid = userid;
		this.firstname = firstname;
		this.lastname = lastname;
		this.email = email;
		this.password = password;
		this.designation = designation;
		this.privilege = privilege;
		this.department_ids = department_ids;
	}
	
	public int getUserid()
	{
		return this.userid;
	}
	
	public void setUserid(int userid)
	{
		this.userid = userid;
	}
	
	public String getFirstname()
	{
		return this.firstname;
	}
	
	public void setFirstname(String firstname)
	{
		this.firstname = firstname;
	}
	
	public String getLastname()
	{
		return this.lastname;
	}
	
	public void setLastname(String lastname)
	{
		this.lastname = lastname;
	}
	
	public String getEmail()
	{
		return this.email;
	}
	
	public void setEmail(String email)
	{
		this.email = email;
	}
	
	public String getPassword()
	{
		return this.password;
	}
	
	public void setPassword(String password)
	{
		this.password = password;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}

	public String getDesignation() {
		return designation;
	}

	public void setPrivilege(int privilege) {
		this.privilege = privilege;
	}

	public int getPrivilege() {
		return privilege;
	}

	public void setDepartment_ids(ArrayList<Integer> department_ids) {
		this.department_ids = department_ids;
	}

	public ArrayList<Integer> getDepartment_ids() {
		return department_ids;
	}

}
