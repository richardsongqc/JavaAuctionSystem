package common.auction;

import java.sql.*;
import common.auction.DBProperty;;

public class JMSAccessConn
{
	private String m_strDBPath;
	private String m_strConnString;
	private final String m_strConnStringPrefix = "jdbc:odbc:Driver={Microsoft Access Driver (*.mdb, *.accdb)};;DBQ=";
	
	private Connection m_conn;
	private Statement m_statement;
	
	JMSAccessConn()
	{
		m_strConnString = m_strConnStringPrefix + GetDBProperty() + ";";
		
		try
		{
			Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
			m_strConnString = m_strConnStringPrefix + GetDBProperty() + ";";
			m_conn = DriverManager.getConnection(  m_strConnString, "", "");
			m_statement = m_conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	protected String GetDBProperty()
	{
		DBProperty db = new DBProperty();
		m_strDBPath = db.GetAccessFilePath();
		return m_strDBPath;
	}
	
	protected void finalize( )
	{
		// finalization code here
	}
	
	public boolean ValidateUser( String strUserID, String strPassword)
	{
		boolean bValidPassword = false;

		try
		{
			String strSQL = "SELECT * FROM USER WHERE LoginName = '" + strUserID + "' AND Password = '" + strPassword + "';";
			
			m_statement.execute(strSQL);
			
			ResultSet rs = m_statement.getResultSet();
			rs.last();
			int size = rs.getRow();
			rs.beforeFirst();
			
			if(size >0 )
			{
				bValidPassword = true;
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		
		return bValidPassword;
	}
	
	public static void main(String[] args)
	{
		//try
		//{
		//	Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
		//	m_strConnString = m_strConnStringPrefix + GetDBProperty() + ";";
		//	Connection conn = DriverManager.getConnection(  m_strConnString, "", "");
		//	Statement s = conn.createStatement();
        //
		//	// create a table
		//	String tableName = "myTable" + String.valueOf((int) (Math.random() * 1000.0));
		//	String createTable = "CREATE TABLE " + tableName + " (id Integer, name Text(32))";
		//	s.execute(createTable);
        //
		//	// enter value into table
		//	for (int i = 0; i < 25; i++)
		//	{
		//		String addRow = "INSERT INTO " + tableName + " VALUES ( "
		//		        + String.valueOf((int) (Math.random() * 32767)) + ", 'Text Value "
		//		        + String.valueOf(Math.random()) + "')";
		//		s.execute(addRow);
		//	}
        //
		//	// Fetch table
		//	String selTable = "SELECT * FROM " + tableName;
		//	s.execute(selTable);
		//	ResultSet rs = s.getResultSet();
		//	while ((rs != null) && (rs.next()))
		//	{
		//		System.out.println(rs.getString(1) + " : " + rs.getString(2));
		//	}
        //
		//	// drop the table
		//	String dropTable = "DROP TABLE " + tableName;
		//	s.execute(dropTable);
        //
		//	// close and cleanup
		//	s.close();
		//	conn.close();
		//}
		//catch (Exception ex)
		//{
		//	ex.printStackTrace();
		//}
	}

}