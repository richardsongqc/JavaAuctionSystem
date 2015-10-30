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
		
		// ����һ���ŵ�������Ϊ������ģʽ
		clntChan = SocketChannel.open();
		clntChan.configureBlocking(false);
		// �����˷�������
		if (!clntChan.connect(new InetSocketAddress(m_strIP, m_nPort)))
		{
			// ���ϵ���ѯ����״̬��ֱ���������
			while (!clntChan.finishConnect())
			{
				// �ڵȴ����ӵ�ʱ�������ִ�����������Գ�ַ��ӷ�����IO���첽����
				// ����Ϊ����ʾ�÷�����ʹ�ã�ֻ��һֱ��ӡ"."
				System.out.print(".");
			}
		}

		// Ϊ��������ӡ��"."������������������з�
		System.out.print("Welcome to Auction System!!!!\n");

		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Please enter your username");
		String strUserID = in.readLine();
		System.out.println("Please enter your password");
		String strPassword = in.readLine();

		InRegisterClient inRegisterClient = new InRegisterClient();
		inRegisterClient.SetUserID(strUserID);
		inRegisterClient.SetUserPassword(strPassword);
		
		// ÿһ�ε���read�����������յ����ֽ���
		CommBuffer outRegisterClient = new CommBuffer();
		
		SendRequest( inRegisterClient, outRegisterClient );

		// �ر��ŵ�
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
