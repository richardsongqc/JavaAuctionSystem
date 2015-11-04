package common.auction;

public class RegisterStateNamePair
{
	private boolean m_bValidUser;
	private String  m_strUserName;
	private String  m_strUserID;
	
	public RegisterStateNamePair(  )
	{
		m_bValidUser  = false ;
		m_strUserName = "";
		m_strUserID   = "";
	}
	
	public RegisterStateNamePair( boolean bValidUser, String strUserName)
	{
		m_bValidUser = bValidUser;
		m_strUserName = strUserName;
	}
	
	public boolean GetValid()
	{
		return m_bValidUser;
	}
	
	public void SetValid( boolean bValid)
	{
		m_bValidUser = bValid;
	}
	
	public String GetName()
	{
		return m_strUserName;
	}
	
	public void SetName( String strUserName)
	{
		m_strUserName = strUserName;
	}
	
	public String GetUserID()
	{
		return m_strUserID;
	}
	
	public void SetUserID( String strUserID)
	{
		m_strUserID = strUserID;
	}
	
	
}
