package buffer.communication.auction;

public class OutAuction extends CommBuffer
{
	public OutAuction()
	{
		SetCommand(RSP_ADVERTISING);
	}
	
	public OutAuction(CommBuffer buffer)
	{
		m_buffer = buffer.m_buffer;
	}
	
	public void SetState( boolean bSuccess)
	{
		m_buffer.put(2, (byte) 1);
		m_buffer.put(3, (byte) (bSuccess? 1: 0));
		
		SetLength(11);
	}
	
	public boolean GetState()
	{
		byte btState = m_buffer.get(3);
		return btState == 0 ? false: true ;
	}
	
	public void SetMaxBidPrice( double dblMaxBidPrice)
	{
		m_buffer.put(4, DOUBLEOFFSET);
		m_buffer.putDouble(5, dblMaxBidPrice);
		
		SetLength(11);
	}
	
	public double GetMaxBidPrice()
	{
		double dblMaxBidPrice = m_buffer.getDouble(5);
		
		return dblMaxBidPrice;
	}
	
}
