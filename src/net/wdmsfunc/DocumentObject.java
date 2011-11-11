package net.wdmsfunc;

import java.util.ArrayList;
import java.util.List;

public class DocumentObject {
	private int doc_id;
	private String doc_name;
	private int doc_userid;
	private String doc_author;
	private String doc_create_time;
	private float doc_size;
	private String doc_lastaccess_time;
	private String doc_modify_time;
	private String doc_type;
	private boolean doc_check;
	private String dept_ids_str;
	private ArrayList<Integer> dept_ids_int;
	private int doc_previlige_level;
	private boolean doc_isencrypted;
	private int doc_checked_userid;
	private int share_id;
	private int from_userid;
	private int shared_userid;
	private int read_doc;
	private int update_doc;
	private int check_in;
	private int can_share;
	
	public void setDoc_name(String doc_name) {
		this.doc_name = doc_name;
	}

	public String getDoc_name() {
		return doc_name;
	}

	public void setDoc_userid(int doc_userid) {
		this.doc_userid = doc_userid;
	}

	public int getDoc_userid() {
		return doc_userid;
	}

	public void setDoc_author(String doc_author) {
		this.doc_author = doc_author;
	}

	public String getDoc_author() {
		return doc_author;
	}

	public void setDoc_create_time(String doc_create_time) {
		this.doc_create_time = doc_create_time;
	}

	public String getDoc_create_time() {
		return doc_create_time;
	}

	public void setDoc_size(int doc_size) {
		this.doc_size = doc_size;
	}

	public float getDoc_size() {
		return doc_size/(1024);
	}

	public void setDoc_lastaccess_time(String doc_lastaccess_time) {
		this.doc_lastaccess_time = doc_lastaccess_time;
	}

	public String getDoc_lastaccess_time() {
		return doc_lastaccess_time;
	}

	public void setDoc_modify_time(String doc_modify_time) {
		this.doc_modify_time = doc_modify_time;
	}

	public String getDoc_modify_time() {
		return doc_modify_time;
	}

	public void setDoc_type(String doc_type) {
		this.doc_type = doc_type;
	}

	public String getDoc_type() {
		return doc_type;
	}

	public void setDoc_check(boolean doc_check) {
		this.doc_check = doc_check;
	}

	public boolean isDoc_check() {
		return doc_check;
	}

	public void setDept_ids_str(String dept_ids_str) {
		this.dept_ids_str = dept_ids_str;
	}

	public String getDept_ids_str() {
		return dept_ids_str;
	}

	public void setDept_ids_int(ArrayList<Integer> dept_ids_int) {
		this.dept_ids_int = dept_ids_int;
	}

	public List<Integer> getDept_ids_int() {
		return dept_ids_int;
	}

	public void setDoc_previlige_level(int doc_previlige_level) {
		this.doc_previlige_level = doc_previlige_level;
	}

	public int getDoc_previlige_level() {
		return doc_previlige_level;
	}

	public void setDoc_id(int doc_id) {
		this.doc_id = doc_id;
	}

	public int getDoc_id() {
		return doc_id;
	}

	public void setDoc_isencrypted(boolean doc_isencrypted) {
		this.doc_isencrypted = doc_isencrypted;
	}

	public boolean isDoc_isencrypted() {
		return doc_isencrypted;
	}

	public void setDoc_checked_userid(int doc_checked_userid) {
		this.doc_checked_userid = doc_checked_userid;
	}

	public int getDoc_checked_userid() {
		return doc_checked_userid;
	}

	public void setShare_id(int share_id) {
		this.share_id = share_id;
	}

	public int getShare_id() {
		return share_id;
	}

	public void setFrom_userid(int from_userid) {
		this.from_userid = from_userid;
	}

	public int getFrom_userid() {
		return from_userid;
	}

	public void setShared_userid(int shared_userid) {
		this.shared_userid = shared_userid;
	}

	public int getShared_userid() {
		return shared_userid;
	}

	public void setRead_doc(int read_doc) {
		this.read_doc = read_doc;
	}

	public int getRead_doc() {
		return read_doc;
	}

	public void setUpdate_doc(int update_doc) {
		this.update_doc = update_doc;
	}

	public int getUpdate_doc() {
		return update_doc;
	}

	public void setCheck_in(int check_in) {
		this.check_in = check_in;
	}

	public int getCheck_in() {
		return check_in;
	}

	public void setCan_share(int can_share) {
		this.can_share = can_share;
	}

	public int getCan_share() {
		return can_share;
	}
	
}
