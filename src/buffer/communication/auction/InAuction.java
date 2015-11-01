package buffer.communication.auction;

public class InAuction extends CommBuffer
{
	public InAuction()
	{
		SetCommand(CMD_ADVERTISING);
	}
	
	public InAuction(CommBuffer buffer)
	{
		m_buffer = buffer.m_buffer;
	}
	
	public void SetProductID(long lProductID)
	{
		m_buffer.put(2, LONGOFFSET);
		m_buffer.putLong(3, lProductID);
		
		SetLength(28+ GetProductName().length());
	}
	
	public long GetProductID()
	{
		long lProductID = m_buffer.getLong(3);
		
		return lProductID;
	}
	
	public void SetProductCount( long lProductCount)
	{
		m_buffer.put(11, LONGOFFSET);
		m_buffer.putLong(12, lProductCount);
		
		SetLength(28+ GetProductName().length());
	}
	
	public long GetProductCount()
	{
		long lProductCount = m_buffer.getLong(12);
		return lProductCount;
	}
	
	public void SetProductPrice( double dblProductPrice)
	{
		m_buffer.put(20, DOUBLEOFFSET);
		m_buffer.putDouble(21, dblProductPrice);
		
		SetLength(28+ GetProductName().length());
	}
	
	public double GetProductPrice()
	{
		double dblProductPrice = m_buffer.getDouble(21);
		
		return dblProductPrice;
	}
	
	public void SetProductName( String strProductName)
	{
		byte szLen = (byte) strProductName.length();
		m_buffer.put(29, szLen );
		PutBuffer(strProductName.getBytes(), 30, szLen );
		SetLength(28+ szLen);
	}
	
	public String GetProductName()
	{
		byte szLen = m_buffer.get(29);
		byte[] szBuf = GetBuffer( 30, szLen);
		String strProductName = new String(szBuf);
		
		return strProductName;
	}
}
