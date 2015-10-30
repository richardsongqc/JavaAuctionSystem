package communication.auction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.channels.SocketChannel;

import buffer.communication.auction.CommBuffer;
import buffer.communication.auction.InRegisterClient;
import buffer.communication.auction.OutRegisterClient;
//import common.auction.Singleton;

public class AuctionClient
{
	//public Singleton singleton = Singleton.getInstance();
	private String m_strIP;
	private int m_nPort;
	private SocketChannel clntChan;
	//private String GetServerIP()
	//{
	//	return singleton.GetProperty().GetServerIP();
	//}
    //
	//private int GetServerPort()
	//{
	//	return (int) singleton.GetProperty().GetServerPort();
	//}

	public AuctionClient( String strIP, int nPort) throws IOException
	{
		
		m_strIP = strIP;
		m_nPort = nPort;
		
		// 创建一个信道，并设为非阻塞模式
		clntChan = SocketChannel.open();
		clntChan.configureBlocking(false);
		// 向服务端发起连接
		if (!clntChan.connect(new InetSocketAddress(m_strIP, m_nPort)))
		{
			// 不断地轮询连接状态，直到完成连接
			while (!clntChan.finishConnect())
			{
				// 在等待连接的时间里，可以执行其他任务，以充分发挥非阻塞IO的异步特性
				// 这里为了演示该方法的使用，只是一直打印"."
				System.out.print(".");
			}
		}

		// 为了与后面打印的"."区别开来，这里输出换行符
		System.out.print("Welcome to Auction System!!!!\n");

		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Please enter your username");
		String strUserID = in.readLine();
		System.out.println("Please enter your password");
		String strPassword = in.readLine();

		InRegisterClient inRegisterClient = new InRegisterClient();
		inRegisterClient.SetUserID(strUserID);
		inRegisterClient.SetUserPassword(strPassword);
		
		// 每一次调用read（）方法接收到的字节数
		CommBuffer outRegisterClient = new CommBuffer();
		
		SendRequest( inRegisterClient, outRegisterClient );

		// 关闭信道
		clntChan.close();
	}

	public void SendRequest(CommBuffer inBuf, CommBuffer outBuf)
	{
		try
		{
			inBuf.SendBufer(clntChan);
			while (true)
			{
				int readBytes = outBuf.ReceiveBuffer(clntChan);
				if (readBytes > 0)
				{
					break;
				}
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception
	{	
		if( args.length != 2 )
		{
			System.out.print("Parameters: <HOST> <PORT>/n");
			return;
		}
		
		new AuctionClient(args[0], Integer.parseInt(args[1]));
	}

}
