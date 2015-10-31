package buffer.communication.auction;

import java.nio.channels.SelectionKey;

import common.auction.RegisterStateNamePair;

public class KeyBufferPair
{
	public SelectionKey m_key;
	public CommBuffer   m_buffer;
	public RegisterStateNamePair m_state;
	
	public KeyBufferPair(SelectionKey key, CommBuffer buffer )
	{
		m_key	 = key   ;
		m_buffer = buffer;
	}
	
	public SelectionKey GetSelectionKey()
	{
		return m_key;
	}
	
	public CommBuffer GetCommBuffer()
	{
		return m_buffer;
	}
	
	public RegisterStateNamePair GetState()
	{
		return m_state;
	}
	
	public void SetState( RegisterStateNamePair state)
	{
		m_state = state;
	}
	
	
}
