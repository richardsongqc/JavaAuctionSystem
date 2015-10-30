package buffer.communication.auction;

import java.nio.channels.SelectionKey;

public class KeyBufferPair
{
	public SelectionKey m_key;
	public CommBuffer   m_buffer;
	
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
}
