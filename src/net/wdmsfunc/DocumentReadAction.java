package net.wdmsfunc;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

import net.wdms.crypt.DesEncrypter;
import net.wdmsfunc.Log.DocumentOperation;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.ResultSet;
import com.opensymphony.xwork2.ActionSupport;

import org.apache.struts2.interceptor.SessionAware;
import java.io.InputStream;

public class DocumentReadAction extends ActionSupport implements SessionAware{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2787150030997191720L;
	private Map session;
	private String EncryptionKey;
	private int documentid;
	private String download_filename;
	private InputStream theStream;
	private String mimeType;
	public String BASEDIR = "Read/"; 
	private String download_document() throws SQLException, IOException 
	{
		String dbName = "wdms";
		String userName = "root";
		String passd = "aj";
		String url = "jdbc:mysql://localhost:3306/";
		PreparedStatement ps = null;
		ResultSet rs = null;
		String query = null;
		String Fname_ciphered = "";
		FileOutputStream fos = null;
		int mightbeshared = 1;
		int intrusion = 1;
		
		
		try {
			/** Establish connection to database**/
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = (Connection) DriverManager.getConnection(url+dbName, userName,passd);
			System.out.println("Connected to db");


			/** Save the file in database **/
			
			query = "select doc_name,doc_content,doc_type,doc_isencrypted from document where docid="+ documentid + " and (doc_userid=" + Integer.parseInt(session.get("userid").toString()) + " or user_previlige_level<=" + Integer.parseInt(session.get("previlige").toString()) + ")";
			ps = (PreparedStatement) con.prepareStatement(query);
			rs = (ResultSet) ps.executeQuery();
			while (rs.next()) 
			{
				Fname_ciphered = rs.getString(1); 
				mimeType = rs.getString(3);
				if(rs.getBoolean(4))
				{
					fos = new FileOutputStream(BASEDIR+"read_"+Fname_ciphered);
				}
				else
				{
					fos = new FileOutputStream(BASEDIR+ Fname_ciphered);
				}
				InputStream is  = rs.getBinaryStream(2);
				byte[] buf = new byte[3000000];
				int read = 0;
				while ((read = is.read(buf)) > 0) 
				{
					fos.write(buf, 0, read);
				}
				fos.close();
				is.close();
				mightbeshared = 0;
				intrusion = 0;
			}
			
			if(mightbeshared == 1)
			{
				query = "select document.doc_name,document.doc_content,document.doc_type,document.doc_isencrypted from document,shared_documents where document.docid="+ documentid + " and shared_documents.doc_id=" + documentid + " and shared_documents.shared_userid=" + Integer.parseInt(session.get("userid").toString());
				ps = (PreparedStatement) con.prepareStatement(query);
				rs = (ResultSet) ps.executeQuery();
				while (rs.next()) 
				{
					Fname_ciphered = rs.getString(1); 
					mimeType = rs.getString(3);
					if(rs.getBoolean(4))
					{
						fos = new FileOutputStream(BASEDIR+ "read_"+Fname_ciphered);
					}
					else
					{
						fos = new FileOutputStream(BASEDIR+Fname_ciphered);
					}
					InputStream is  = rs.getBinaryStream(2);
					byte[] buf = new byte[3000000];
					int read = 0;
					while ((read = is.read(buf)) > 0) 
					{
						fos.write(buf, 0, read);
					}
					fos.close();
					is.close();
					mightbeshared = 0;
				}
				intrusion = 0;
			}
			
			if(intrusion == 1)
			{
				return "login";
			}
				
		} 
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		//saving the imag

		return Fname_ciphered;
	}

	public String read_document()
	{
		try 
		{
			File directory = new File(".");
			if(!session.containsKey("logged-in"))
			{
				return "login";
			}
			
			String filename = download_document();
			int userid = Integer.parseInt(session.get("userid").toString());
			if(!filename.isEmpty())
			{
				File output_file = new File(BASEDIR + filename);
				theStream = new FileInputStream(output_file);
				output_file.delete();
				download_filename = filename;
				Log.document_operation(documentid, userid, DocumentOperation.READ, "read_operation", "SUCCESS");
				return SUCCESS;
			}
			else
			{
				Log.document_operation(documentid, userid, DocumentOperation.READ, "read_operation", "FAIL");
				addActionError("ERROR occured while trying to download");
				return ERROR;
			}
		}
		catch (Exception e) 
		{
			int userid = Integer.parseInt(session.get("userid").toString());
			Log.document_operation(documentid, userid, DocumentOperation.READ, "read_operation", "FAIL");
			addActionError(e.getMessage());
			return ERROR;
		}
	}
	
	public String read_encrypted_document()
	{
		
		try 
		{
			if(!session.containsKey("logged-in"))
			{
				return "login";
			}

			int userid = Integer.parseInt(session.get("userid").toString());
			if(EncryptionKey.isEmpty())
			{
				addActionMessage("Please Enter Key Phrase");
				Log.document_operation(documentid, userid, DocumentOperation.READ, "read_operation_encrypted", "INPUT");
				return INPUT;
			}
		
			
			//Create Encryption decryption object
			DesEncrypter encrypter = new DesEncrypter(getEncryptionKey());
			String filename = download_document();
			if(!filename.isEmpty())
			{
				encrypter.decrypt(new FileInputStream(BASEDIR +"read_"+filename),new FileOutputStream(BASEDIR + filename));
				File encrypted = new File(BASEDIR + "Output_"+filename);
				File decrypted = new File(BASEDIR+filename);
				theStream = new FileInputStream(decrypted);
				encrypted.delete();
				decrypted.delete();
				download_filename = filename;
				Log.document_operation(documentid, userid, DocumentOperation.READ, "read_encrypted_operation", "SUCCESS");
				return SUCCESS;
			}
			else
			{
				Log.document_operation(documentid, userid, DocumentOperation.READ, "read_encrypted_operation", "FAIL");
				addActionError("ERROR occured while trying to download");
				return ERROR;
			}
		}
		catch (Exception e) 
		{
			int userid = Integer.parseInt(session.get("userid").toString());
			Log.document_operation(documentid, userid, DocumentOperation.READ, "read_encrypted_operation", "FAIL");
			addActionError(e.getMessage());
			return ERROR;
		}
	}
	
	public String execute() 
	{
		return SUCCESS;
	}

	@Override
	public void setSession(Map session) {
		this.session = session;
	}

	public void setEncryptionKey(String encryptionKey) {
		EncryptionKey = encryptionKey;
	}

	public String getEncryptionKey() {
		return EncryptionKey;
	}

	public void setDocumentid(int documentid) {
		this.documentid = documentid;
	}

	public int getDocumentid() {
		return documentid;
	}


	public void setTheStream(InputStream theStream) {
		this.theStream = theStream;
	}


	public InputStream getTheStream() {
		return theStream;
	}


	public void setDownload_filename(String download_filename) {
		this.download_filename = download_filename;
	}


	public String getDownload_filename() {
		return download_filename;
	}


	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}


	public String getMimeType() {
		return mimeType;
	}

}
