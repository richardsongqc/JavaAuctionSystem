package buffer.communication.auction;

public class BroadcastStatus extends CommBuffer
{
	public BroadcastStatus()
	{
		SetCommand(CMD_BROADCAST_STATUS);
	}
	
	public BroadcastStatus(CommBuffer buffer)
	{
		m_buffer = buffer.m_buffer;
	}
	
	public void SetStatus( long lStatus )
	{
		m_buffer.put(3, (byte) lStatus);
		SetLength(2);
	}
	
	public long GetStatus()
	{
		return m_buffer.get(3) ;
	}	
	
}
