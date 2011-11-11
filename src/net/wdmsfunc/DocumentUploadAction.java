package net.wdmsfunc;

import net.wdms.crypt.DesEncrypter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Date;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts2.interceptor.ServletRequestAware;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.ResultSet;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.validator.annotations.RequiredStringValidator;

import org.apache.struts2.interceptor.SessionAware;
import java.io.InputStream;

public class DocumentUploadAction extends ActionSupport implements
ServletRequestAware, SessionAware {
	/**
	 * Added Default Serial Version
	 */
	private static final long serialVersionUID = 1L;
	private File document;
	private String documentContentType;
	private String documentFileName;
	private HttpServletRequest servletRequest;
	private String EncryptionKey;
	private Map session;
	public String BASEDIR = "Read/";
	
	private boolean save_document(boolean encrypt) throws SQLException, FileNotFoundException 
	{
		
		boolean result_value = false;
		String dbName = "wdms";
		String userName = "root";
		String passd = "aj";
		String url = "jdbc:mysql://localhost:3306/";
		PreparedStatement ps = null;
		ResultSet rs = null;
		String query = null;
		File file = null;

		int userid = Integer.parseInt(session.get("userid").toString());
		int previlige = Integer.parseInt(session.get("previlige").toString());
		String author = "";
		String dpids_str = "";

		try {
			/** Establish connection to database**/
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = (Connection) DriverManager.getConnection(url+dbName, userName,passd);
			System.out.println("Connected to db");

			/** Get current user details **/
			query = "Select * from user where userid=" + userid;
			ps = (PreparedStatement) con.prepareStatement(query);
			rs =  (ResultSet) ps.executeQuery();
			while (rs.next()) {
				author = rs.getString("firstname") + " " +  rs.getString("lastname");
				dpids_str = rs.getString("department_ids");
			} 	
			ps.close();

			Date today = new Date();
			java.sql.Timestamp Timestamp = new java.sql.Timestamp(today.getTime());
			/** Save the file in database **/
			if(encrypt == true)
			{
				file = new File(BASEDIR + "ciphertext_"+this.documentFileName);
			}
			else
			{
				file = this.document;
			}
			
			query = "INSERT INTO document(docid,doc_name,doc_content,doc_size,doc_userid,doc_author,doc_type,department_ids,user_previlige_level,last_access_time,modification_time,doc_isencrypted,doc_checked_userid) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)";

			ps = (PreparedStatement) con.prepareStatement(query);
			ps.setInt(1, 0);
			ps.setString(2, documentFileName);
			ps.setBinaryStream(3, new FileInputStream(file), (int)file.length());
			ps.setInt(4,(int)file.length());
			ps.setInt(5, userid);
			ps.setString(6, author);
			ps.setString(7, documentContentType);
			ps.setString(8,dpids_str);
			ps.setInt(9,previlige);
			ps.setTimestamp(10,Timestamp);
			ps.setTimestamp(11,Timestamp);
			ps.setBoolean(12,encrypt);
			ps.setInt(13,0);
			ps.executeUpdate();
			con.close();

			result_value = true;

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			result_value = false;
		}
		//saving the imag

		return result_value;
	}

	public String upload_document_with_encryption()
	{
		int userid = 0;
		int previlige = 0;

		try 
		{
			if(session.containsKey("logged-in"))
			{
				userid = Integer.parseInt(session.get("userid").toString());
				previlige = Integer.parseInt(session.get("previlige").toString());
			}
			else
			{
				return "login";
			}

			if(EncryptionKey == null || EncryptionKey.isEmpty())
			{
				addActionMessage("Please provide key phrase for encryption");
				return INPUT;
			}

			if(previlige>=3 && previlige<=5)
			{
				//Create Encryption decryption object
				DesEncrypter encrypter = new DesEncrypter(EncryptionKey);
				// Encrypt the file
				encrypter.encrypt(new FileInputStream(this.document),new FileOutputStream( BASEDIR + "ciphertext_"+this.documentFileName));

				if(save_document(true))
				{
					addActionMessage("File Uploaded Successfully!!");
					//Delete the encrypted file
					File delete_file = new File( BASEDIR + "ciphertext_"+this.documentFileName);
					if(delete_file.isFile())
					{
						delete_file.delete();
					}
					return SUCCESS;
				}
				else
				{
					addActionMessage("Failed to upload the file!!");
					//Delete the encrypted file
					File delete_file = new File( BASEDIR + "ciphertext_"+this.documentFileName);
					if(delete_file.isFile())
					{
						delete_file.delete();
					}
					return ERROR;
				}
				
			}
			else
			{
				addActionError("Unauthorized User access please log-in to continue");
				return "login";
			}
		} 
		catch (Exception e) 
		{
			addActionError(e.getMessage());
			return ERROR;
		}
		
	}

	public String upload_document_without_encryption()
	{
		int userid = 0;
		int previlige = 0;

		try 
		{
			if(session.containsKey("logged-in"))
			{
				userid = Integer.parseInt(session.get("userid").toString());
				previlige = Integer.parseInt(session.get("previlige").toString());
			}
			else
			{
				return "login";
			}


			if(previlige>=3 && previlige<=5)
			{

				if(save_document(false))
				{
					addActionMessage("File Uploaded Successfully!!");
					return SUCCESS;
				}
				else
				{
					addActionMessage("Failed to upload the file!!");
					return ERROR;
				}
			}
			else
			{
				addActionError("Unauthorized User access please log-in to continue");
				return "login";
			}
		} 
		catch (Exception e) 
		{
			addActionError(e.getMessage());
			return ERROR;
		}
		
	}
	
	public String execute() {
		return SUCCESS;
	}

	public File getDocument() {
		return document;
	}

	public void setDocument(File document) {
		this.document = document;
	}

	public String getDocumentContentType() {
		return documentContentType;
	}

	public void setDocumentContentType(String documentContentType) {
		this.documentContentType = documentContentType;
	}
	
	@RequiredStringValidator(message="Please enter document location")
	public String getDocumentFileName() {
		return documentFileName;
	}

	public void setdocumentFileName(String documentFileName) {
		this.documentFileName = documentFileName;
	}

	public void setServletRequest(HttpServletRequest servletRequest) {
		this.servletRequest = servletRequest;
	}

	public HttpServletRequest getServletRequest() {
		return servletRequest;
	}

	public void setEncryptionKey(String encryptionKey) {
		EncryptionKey = encryptionKey;
	}

	public String getEncryptionKey() {
		return EncryptionKey;
	}

	@Override
	public void setSession(Map session) {
		this.session = session;

	}
}
