package communication.auction;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import buffer.communication.auction.CommBuffer;

public class SelectorProtocol implements ITCPProtocol
{

    private int bufSize; // 缓冲区的长度  
    public SelectorProtocol(int bufSize)
    {  
    	this.bufSize = bufSize;  
    }  
    
	@Override
	public void handleAccept(SelectionKey key) throws IOException
	{
        SocketChannel clntChan = ((ServerSocketChannel) key.channel()).accept();  
        clntChan.configureBlocking(false);  
        //将选择器注册到连接到的客户端信道，并指定该信道key值的属性为OP_READ，同时为该信道指定关联的附件  
        clntChan.register(key.selector(), SelectionKey.OP_READ, ByteBuffer.allocate(bufSize));  
	}
	

	
	@Override
	public void handleRead(SelectionKey key, CommBuffer cmdBuf) throws IOException
	{
		SocketChannel clntChan = (SocketChannel) key.channel();  
        //获取该信道所关联的附件，这里为缓冲区  
        long bytesRead = cmdBuf.ReceiveBuffer(clntChan);
        //如果read（）方法返回-1，说明客户端关闭了连接，那么客户端已经接收到了与自己发送字节数相等的数据，可以安全地关闭  
        if (bytesRead == -1)
        {   
        	clntChan.close();  
        }
        else if(bytesRead > 0)
        {  
        	//如果缓冲区总读入了数据，则将该信道感兴趣的操作设置为为可读可写  
        	key.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE); 
        	System.out.println("\nServer Received: " +  cmdBuf.PrintBuffer(cmdBuf.GetLength()+2) ); 
        }  
	}

	@Override
	public void handleWrite(SelectionKey key, CommBuffer cmdBuf) throws IOException
	{
	    SocketChannel clntChan = (SocketChannel) key.channel();  
	    //将数据写入到信道中  
	    cmdBuf.SendBufer(clntChan);
	    System.out.println("\nServer Responsed: " +  cmdBuf.PrintBuffer(cmdBuf.GetLength()+2));  
	    if (!cmdBuf.GetByteBuffer().hasRemaining())
	    {   
	    	//如果缓冲区中的数据已经全部写入了信道，则将该信道感兴趣的操作设置为可读  
	    	key.interestOps(SelectionKey.OP_READ);  
	    }  
	}

}
