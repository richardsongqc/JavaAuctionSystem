package buffer.communication.auction;

public class InQueryBid extends CommBuffer
{
	public InQueryBid()
	{
		SetCommand(CMD_QUERY_BID_PRICE);
	}
	
	public InQueryBid(CommBuffer buffer)
	{
		m_buffer = buffer.m_buffer;
	}
}
