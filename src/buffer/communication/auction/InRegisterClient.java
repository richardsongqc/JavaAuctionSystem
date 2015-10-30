package buffer.communication.auction;

public class InRegisterClient extends CommBuffer
{
	private String m_strUserID;
	private String m_strPassword;
	
	public InRegisterClient()
	{
		SetCommand(CMD_REGISTER_CLIENT);
		CombineBuffer();
	}
	
	public InRegisterClient(String strUserID, String strPwd )
	{
		SetCommand(CMD_REGISTER_CLIENT);
		m_strUserID = strUserID;
		m_strPassword = strPwd ;
		CombineBuffer();
	}
	
	public InRegisterClient(CommBuffer buffer)
	{
		m_buffer = buffer.m_buffer;
	}

	public void SetUserID( String strUserID )
	{
		m_strUserID = strUserID;
		CombineBuffer();
	}
	
	public String GetUserID()
	{
		byte len = m_buffer.get(2);
		byte[] szUserID = new byte[len];
		
		for( int i = 0; i < len; i ++ )
		{
			szUserID[i] = m_buffer.get(3+i);
		}
		
		m_strUserID = new String(szUserID);
		m_strUserID = String.copyValueOf(m_strUserID.toCharArray(), 0, szUserID.length);
		return m_strUserID;
	}
	
	public void SetUserPassword( String strPwd)
	{
		m_strPassword = strPwd;
		CombineBuffer();
	}
	
	public String GetUserPassword( )
	{
		int nOffset = 3+m_strUserID.length();
		byte len = m_buffer.get(nOffset);
		byte[] szPassword = new byte[len];
		
		for( int i = 0; i < len; i ++ )
		{
			szPassword[i] = m_buffer.get(nOffset+i+1);
		}
		
		m_strPassword = new String(szPassword);
		m_strPassword = String.copyValueOf(m_strPassword.toCharArray(), 0, szPassword.length);
		
		return m_strPassword;
	}
	
	protected void CombineBuffer()
	{
		int nUserIDLen = 0;
		int nPwdLen = 0;
		
		if( m_strUserID != null )
		{ 
			nUserIDLen = m_strUserID.length();
		}
		
		if( m_strPassword != null )
		{
			nPwdLen = m_strPassword.length();
		}
		
		m_buffer.put(2, (byte)nUserIDLen );
		if( nUserIDLen > 0 )
		{
			byte[] szBuf = m_strUserID.getBytes(); 
			PutBuffer(szBuf, 3, nUserIDLen );
		}
		
		m_buffer.put(3+nUserIDLen, (byte)nPwdLen);
		if( nPwdLen > 0 )
		{
			byte[] szBuf = m_strPassword.getBytes(); 
			PutBuffer(szBuf, 4+nUserIDLen, nPwdLen);
		}
		
		SetLength((byte)(nUserIDLen+nPwdLen+2));
	}
}
