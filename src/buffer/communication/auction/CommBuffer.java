package buffer.communication.auction;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class CommBuffer
{
	protected final byte CMD_REGISTER_CLIENT 			= 1;
	protected final byte CMD_RETRIEVE_STOCK_OF_CLIENT	= 2;
	protected final byte CMD_ADVERTISING 				= 3;
	protected final byte CMD_BID 						= 4;
	//protected final byte CMD_BROADCAST_PRICE 			= 5;
	
	protected final byte RSP_REGISTER_CLIENT 			= 21;
	protected final byte RSP_RETRIEVE_STOCK_OF_CLIENT 	= 22;
	protected final byte RSP_ADVERTISING 				= 23;
	protected final byte RSP_BID 						= 24;
	
	protected final byte CMD_BROADCAST_PRICE			= 99;
	protected final byte CMD_BROADCAST_STATUS	    	= 100;
	final byte LONGOFFSET 								= 8;
	final byte DOUBLEOFFSET 							= 8;
	
	protected ByteBuffer m_buffer;
	
	public CommBuffer()
	{
		m_buffer =ByteBuffer.allocate(1024);
		m_buffer.clear();
		
	}
	
	public CommBuffer(ByteBuffer buffer)
	{
		m_buffer =ByteBuffer.allocate(1024);
		m_buffer = buffer;
	}
	
	public CommBuffer(CommBuffer buffer)
	{
		m_buffer = buffer.m_buffer;
	}
	
	public byte[] GetByteArray()
	{
		return m_buffer.array();
	}
	
	public void Cloning( ByteBuffer buffer)
	{
		m_buffer = buffer;
	}
	
	////////////////////////////////////////////////////////////////////////////
	public void PutBuffer( byte[] szBuf, int index, int nLen)
	{
		//m_buffer.rewind();
		for( int i = 0; i < nLen; i++ )
		{
			m_buffer.put(index + i,szBuf[i]);
		}
	}
	
	public byte[] GetBuffer( int index, int nLen)
	{
		//m_buffer.flip();
		byte[] szBuf = new byte[nLen];
		for( int i = 0; i < nLen; i++ )
		{
			szBuf[i]=m_buffer.get(index + i);
		}
		
		return szBuf;
	}
	
	////////////////////////////////////////////////////////////////////////////
	
	public void SetCommand( byte nCmd)
	{
		m_buffer.put(0,nCmd );
	}
	
	public byte GetCommand()
	{
		return m_buffer.get(0);
	}
	
	public void SetLength( int i)
	{
		m_buffer.put(1, (byte)i);
	}
	
	public byte GetLength()
	{
		return m_buffer.get(1);
	}

	public void SetData( byte[] szData)
	{
		PutBuffer(szData, 2, szData.length );
	}
	
	public byte[] GetData()
	{
		byte[] szData = new byte[m_buffer.get(1)];
		szData = GetBuffer( 2, szData.length );
		return szData;
	}
	
	public int SendBufer( SocketChannel socketChann) throws IOException
	{
		int nWriteLen = socketChann.write(m_buffer);
		if( nWriteLen > 0 )
		{
			System.out.printf( "Send Buffer Length = %d\n%s\n", nWriteLen, PrintBuffer(m_buffer.get(1)+2) ); 
		}
		
		return nWriteLen;
	}
	
	public int ReceiveBuffer( SocketChannel socketChann) throws IOException
	{
		int nReadLen = socketChann.read(m_buffer);
		if( nReadLen > 0 )
		{
			System.out.printf( "Read Buffer Length = %d\n%s\n", nReadLen, PrintBuffer(m_buffer.get(1)+2) ); 
		}
		
		return nReadLen;
	}
	
	public String PrintBuffer(int nLen)
	{
		String str = "";
		
		byte[] buffer = m_buffer.array();
		for( int i = 0; i < nLen; i++)
		{
			str += String.format("%02X ", buffer[i]) ;
		}
		
		return str;
		
	}
	
	public ByteBuffer GetByteBuffer()
	{
		return m_buffer;
	}
	
	public static void main(String[] args)
	{
		// TODO Auto-generated method stub

	}

}
