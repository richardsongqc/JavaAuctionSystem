package buffer.communication.auction;

public class BroadcastEndAuction extends CommBuffer
{
	public BroadcastEndAuction()
	{
		SetCommand(CMD_BROADCAST_AUCTION_END);
	}
	
	public BroadcastEndAuction(CommBuffer buffer)
	{
		m_buffer = buffer.m_buffer;
	}
}
