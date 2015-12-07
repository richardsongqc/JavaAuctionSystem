package buffer.communication.auction;

public class OutQueryBid extends CommBuffer
{
	public OutQueryBid()
	{
		SetCommand(RSP_QUERY_BID_PRICE);
	}
	
	public OutQueryBid(CommBuffer buffer)
	{
		m_buffer = buffer.m_buffer;
	}
	
	
	public void SetStatus(long lStatus)
	{
		m_buffer.put(2, LONGOFFSET);
		m_buffer.put(3, (byte) lStatus);
		
		SetLength(28+ GetProductName().length());
	}
	
	public long GetStatus()
	{
		long lStatus = (long)m_buffer.get(3);
		
		return lStatus;
	}
	
	public void SetProductID(long lProductID)
	{
		m_buffer.put(4, LONGOFFSET);
		m_buffer.putLong(5, lProductID);
		
		SetLength(30 + GetProductName().length());
	}
	
	public long GetProductID()
	{
		long lProductID = m_buffer.getLong(5);
		
		return lProductID;
	}
	
	public void SetProductCount( long lProductCount)
	{
		m_buffer.put(13, LONGOFFSET);
		m_buffer.putLong(14, lProductCount);
		
		SetLength(30+ GetProductName().length());
	}
	
	public long GetProductCount()
	{
		long lProductCount = m_buffer.getLong(14);
		return lProductCount;
	}
	
	public void SetProductPrice( double dblProductPrice)
	{
		m_buffer.put(22, DOUBLEOFFSET);
		m_buffer.putDouble(23, dblProductPrice);
		
		SetLength(30+ GetProductName().length());
	}
	
	public double GetProductPrice()
	{
		double dblProductPrice = m_buffer.getDouble(23);
		
		return dblProductPrice;
	}
	
	public void SetProductName( String strProductName)
	{
		byte szLen = (byte) strProductName.length();
		m_buffer.put(31, szLen );
		PutBuffer(strProductName.getBytes(), 32, szLen );
		SetLength(30+ szLen);
	}
	
	public String GetProductName()
	{
		byte szLen = m_buffer.get(31);
		byte[] szBuf = GetBuffer( 32, szLen);
		String strProductName = new String(szBuf);
		
		return strProductName;
	}
}
