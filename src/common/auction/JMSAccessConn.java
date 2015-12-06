package common.auction;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import common.auction.DBProperty;;

public class JMSAccessConn
{
	private String m_strDBPath;
	private String m_strConnString;
	private final String m_strConnStringPrefix = "jdbc:odbc:Driver={Microsoft Access Driver (*.mdb, *.accdb)};;DBQ=";
	
	private Connection m_conn;
	private Statement m_statement;
	public Singleton singleton = Singleton.getInstance(); 
	private ArrayList<Product> m_listProduct;
	
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
	
	JMSAccessConn(DBProperty db)
	{
		m_strConnString = m_strConnStringPrefix + db.GetAccessFilePath() + ";";
		
		try
		{
			Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
			m_strConnString = m_strConnStringPrefix + db.GetAccessFilePath() + ";";
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
		DBProperty db = singleton.GetProperty();
		m_strDBPath = db.GetAccessFilePath();
		return m_strDBPath;
	}
	
	protected void finalize( )
	{
		// finalization code here
	}
	
	public RegisterStateNamePair ValidateUser( String strUserID, String strPassword)
	{
		RegisterStateNamePair pair = new RegisterStateNamePair();

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
				pair.SetValid(true); 
				
				rs.first();
				
				pair.SetName( rs.getString("Name") );
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		
		return pair;
	}
	
	public ArrayList<Product> GetProductList( long lUserID)
	{
		try
		{
			String strSQL = "SELECT Product.ProductID as ProductID, ProductCategory.ProductName as ProductName, Product.Price as Price FROM Product INNER JOIN ProductCategory ON Product.ProductID = ProductCategory.ProductID WHERE UserID = " + lUserID + ";";
			
			m_statement.execute(strSQL);
			
			ResultSet rs = m_statement.getResultSet();
			
			m_listProduct = new ArrayList<Product> ();
			while( rs.next())
			{
				long lProductID = rs.getLong("ProductID");
				String strName = rs.getString("ProductName");
				double dblPrice = rs.getDouble("Price");
				Product product = new Product(lProductID, strName, 1, dblPrice);
				
				m_listProduct.add(product);
			}
		}
		catch( Exception ex)
		{
			ex.printStackTrace();
		}
		
		return m_listProduct;
	}
	
	public ArrayList<Product> GetProductList( String strUserID )
	{
		try
		{
			String strSQL = "SELECT ProductCategory.CategoryID as CategoryID, ProductCategory.CategoryName as CategoryName, Product.Price as Price, User.Name as UserName FROM (Product INNER JOIN [User] ON Product.UserID = User.UserID) INNER JOIN ProductCategory ON Product.ProductID = ProductCategory.CategoryID  WHERE User.LoginName = '" + strUserID + "';";
			
			m_statement.execute(strSQL);
			
			ResultSet rs = m_statement.getResultSet();
			
			m_listProduct = new ArrayList<Product> ();
			while( rs.next())
			{
				long lProductID = rs.getLong("CategoryID");
				String strName = rs.getString("CategoryName");
				double dblPrice = rs.getDouble("Price");
				Product product = new Product(lProductID, strName, 1, dblPrice);
				
				m_listProduct.add(product);
			}
		}
		catch( Exception ex)
		{
			ex.printStackTrace();
		}
		
		return m_listProduct;
	}
	
	public long GetCurrentAuctionID()
	{
		long lAuctionID = 0 ;
		
		try
		{
			String strSQL = "SELECT MAX(AuctionID) AS MaxAuctionID FROM Auction";
			
			m_statement.execute(strSQL);
			
			ResultSet rs = m_statement.getResultSet();
		
			while( rs.next())
			{
				lAuctionID = rs.getLong("MaxAuctionID");
			}
		}
		catch( Exception ex)
		{
			ex.printStackTrace();
		}
		
		return lAuctionID;			
	}
	
	public double GetMaxBidPrice( long lAuctionID)
	{
		double dblMaxBidPrice = 0;
		
		try
		{
			String strSQL = "SELECT MAX(Price) AS BidPrice FROM Auction WHERE AuctionID = " + lAuctionID;
			
			m_statement.execute(strSQL);
			
			ResultSet rs = m_statement.getResultSet();
		
			while( rs.next())
			{
				dblMaxBidPrice = rs.getLong("BidPrice");
			}
		}
		catch( Exception ex)
		{
			ex.printStackTrace();
		}
		
		return dblMaxBidPrice;	
	}
	
	public Date GetRecentTransactionTime(long lAuctionID)
	{
		Date dtTime = new Date();
		
		try
		{
			String strSQL = "SELECT MAX(Time) AS TransactionTime FROM Auction WHERE AuctionID = " + lAuctionID;
			
			m_statement.execute(strSQL);
			
			ResultSet rs = m_statement.getResultSet();
		
			while( rs.next())
			{
				dtTime = rs.getDate("TransactionTime");
			}
		}
		catch( Exception ex)
		{
			ex.printStackTrace();
		}
		
		return dtTime;	
	}
	
	public void SetBidTransaction( 
			long lAuctionID, 
			String strUserID, 
			long lProductID,
			long lCount,
			double dblBidPrice)
	{
		PreparedStatement preparedStatementInsert = null;
		
		try
		{
			m_conn.setAutoCommit(false); 
			
			String strTxTime = new SimpleDateFormat("YYYYMMDD hh:mm:ss").format(new Date());
			String strSQL = "INSERT INTO Auction(AuctionID, UserID, ProductID, Count, Price, Time ) VALUES( " +
					lAuctionID + ", '" + strUserID + "'," + lProductID + ", " + lCount + ", " + dblBidPrice + ", '" +
					strTxTime + "');";
			
			preparedStatementInsert = m_conn.prepareStatement(strSQL);
			preparedStatementInsert.executeUpdate();
			
			m_conn.commit();
		}
		catch( Exception ex)
		{
			ex.printStackTrace();
		}
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
