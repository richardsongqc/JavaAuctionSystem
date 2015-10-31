package buffer.communication.auction;

public class InRetrieveStock extends CommBuffer
{
	public InRetrieveStock()
	{
		SetCommand(CMD_RETRIEVE_STOCK_OF_CLIENT);
	}
	
	public InRetrieveStock(CommBuffer buffer)
	{
		// TODO Auto-generated constructor stub
		m_buffer = buffer.m_buffer;
	}

	protected void CombineBuffer()
	{
		
	}
	
	public String GetUserID()
	{
		int nUserIDLen = m_buffer.get(2);
		byte[] szBuf = new byte[nUserIDLen];
		szBuf = GetBuffer(3, nUserIDLen);
		String strUserID = new String(szBuf);
		return strUserID;
	}
	
	public void SetUserID( String strUserID )
	{
		int nUserIDLen = strUserID.length();
		m_buffer.put(2, (byte) nUserIDLen);
		PutBuffer(strUserID.getBytes(), 3, nUserIDLen);
		
		SetLength(nUserIDLen + 1);
	}
	
}
