package common.auction;

public class Singleton
{
	private DBProperty m_dbProperty;
	private JMSAccessConn m_dbConn;
	
	// Private constructor prevents instantiation from other classes
	private Singleton() 
	{
		m_dbProperty = new DBProperty();
		m_dbConn = new JMSAccessConn(m_dbProperty);
	}
	
	/**
	 * SingletonHolder is loaded on the first execution of Singleton.getInstance() 
	 * or the first access to SingletonHolder.INSTANCE, not before.
	 */
	private static class SingletonHolder 
	{ 
		private static final Singleton INSTANCE = new Singleton();
	}
	
	public static Singleton getInstance() 
	{
		return SingletonHolder.INSTANCE;
	}
	  
	public DBProperty GetProperty()
	{
		return m_dbProperty;
	}
	
	public JMSAccessConn GetDBConn()
	{
		return m_dbConn;
	}
	
	public static void main(String[] args)
	{
		// TODO Auto-generated method stub
	}

}
