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
		m_buffer.put(3, (byte) (bSuccess? 1: 0));
	}
	
	public boolean GetState()
	{
		byte btState = m_buffer.get(3);
		return btState == 0 ? false: true ;
	}
}
