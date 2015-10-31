package buffer.communication.auction;

public class OutRegisterClient extends CommBuffer
{
	private boolean m_bSuccess;
	private String  m_strUserName;
	
	public OutRegisterClient()
	{
		SetCommand(RSP_REGISTER_CLIENT);
		CombineBuffer();
	}
	
	public OutRegisterClient( CommBuffer commBuffer)
	{
		m_buffer = commBuffer.m_buffer;
		
		if( GetState() )
		{
			GetUserName();
		}
	}
	
	public void SetState( boolean bSuccess)
	{
		m_bSuccess = bSuccess;
		CombineBuffer();
	}
	
	public boolean GetState()
	{
		m_bSuccess = ( m_buffer.get(3) == 0 ) ? false: true ;
		return m_bSuccess;
	}
	
	public void SetUserName( String strUserName )
	{
		m_strUserName = strUserName;
		CombineBuffer();
	}
	
	public String GetUserName()
	{
		int nOffset = 4;
		byte len = m_buffer.get(nOffset);
		byte[] szUserName = new byte[len];
		
		for( int i = 0; i < len; i ++ )
		{
			szUserName[i] = m_buffer.get(nOffset+i+1);
		}
		
		m_strUserName = new String(szUserName);
		m_strUserName = String.copyValueOf(m_strUserName.toCharArray(), 0, szUserName.length);
		
		return m_strUserName;
	}
	
	protected void CombineBuffer()
	{
		int nUserNameLen = 0;
		// State
		m_buffer.put(2, (byte) 1);
		m_buffer.put(3,(byte)(m_bSuccess? 1: 0 ));
		
		// UserName
		if ( m_bSuccess )
		{
			if( m_strUserName != null )
			{ 
				nUserNameLen = m_strUserName.length();
			}
			
			m_buffer.put(4, (byte)nUserNameLen);
			if( nUserNameLen > 0 )
			{
				byte[] szBuf = m_strUserName.getBytes(); 
				PutBuffer(szBuf, 5, nUserNameLen);
			}
		}
		
		if( m_strUserName == null )
		{
			SetLength( (byte)2 );
		}
		else
		{
			SetLength( (byte) (2 + (m_bSuccess? (m_strUserName.length() + 1):0 ) ) );
		}
	}
	
	
}
