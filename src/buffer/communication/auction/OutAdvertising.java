package buffer.communication.auction;

public class OutAdvertising extends CommBuffer
{
	public OutAdvertising()
	{
		SetCommand(RSP_ADVERTISING);
	}
	
	public OutAdvertising(CommBuffer buffer)
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
