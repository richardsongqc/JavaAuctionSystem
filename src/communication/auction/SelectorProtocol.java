package communication.auction;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import buffer.communication.auction.CommBuffer;

public class SelectorProtocol implements ITCPProtocol
{

    private int bufSize; // �������ĳ���  
    public SelectorProtocol(int bufSize)
    {  
    	this.bufSize = bufSize;  
    }  
    
	@Override
	public void handleAccept(SelectionKey key) throws IOException
	{
        SocketChannel clntChan = ((ServerSocketChannel) key.channel()).accept();  
        clntChan.configureBlocking(false);  
        //��ѡ����ע�ᵽ���ӵ��Ŀͻ����ŵ�����ָ�����ŵ�keyֵ������ΪOP_READ��ͬʱΪ���ŵ�ָ�������ĸ���  
        clntChan.register(key.selector(), SelectionKey.OP_READ, ByteBuffer.allocate(bufSize));  
	}
	

	
	@Override
	public void handleRead(SelectionKey key, CommBuffer cmdBuf) throws IOException
	{
		SocketChannel clntChan = (SocketChannel) key.channel();  
        //��ȡ���ŵ��������ĸ���������Ϊ������  
        long bytesRead = cmdBuf.ReceiveBuffer(clntChan);
        //���read������������-1��˵���ͻ��˹ر������ӣ���ô�ͻ����Ѿ����յ������Լ������ֽ�����ȵ����ݣ����԰�ȫ�عر�  
        if (bytesRead == -1)
        {   
        	clntChan.close();  
        }
        else if(bytesRead > 0)
        {  
        	//����������ܶ��������ݣ��򽫸��ŵ�����Ȥ�Ĳ�������ΪΪ�ɶ���д  
        	key.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE); 
        	System.out.println("\nServer Received: " +  cmdBuf.PrintBuffer(cmdBuf.GetLength()+2) ); 
        }  
	}

	@Override
	public void handleWrite(SelectionKey key, CommBuffer cmdBuf) throws IOException
	{
	    SocketChannel clntChan = (SocketChannel) key.channel();  
	    //������д�뵽�ŵ���  
	    cmdBuf.SendBufer(clntChan);
	    System.out.println("\nServer Responsed: " +  cmdBuf.PrintBuffer(cmdBuf.GetLength()+2));  
	    if (!cmdBuf.GetByteBuffer().hasRemaining())
	    {   
	    	//����������е������Ѿ�ȫ��д�����ŵ����򽫸��ŵ�����Ȥ�Ĳ�������Ϊ�ɶ�  
	    	key.interestOps(SelectionKey.OP_READ);  
	    }  
	}

}
