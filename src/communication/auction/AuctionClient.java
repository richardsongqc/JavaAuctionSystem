package communication.auction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Scanner;

import buffer.communication.auction.CommBuffer;
import buffer.communication.auction.InAdvertising;
import buffer.communication.auction.InAuction;
import buffer.communication.auction.InQueryBid;
import buffer.communication.auction.InRegisterClient;
import buffer.communication.auction.InRetrieveStock;
import buffer.communication.auction.OutAdvertising;
import buffer.communication.auction.OutAuction;
import buffer.communication.auction.OutQueryBid;
import buffer.communication.auction.OutRegisterClient;
import buffer.communication.auction.OutRetrieveStock;
//import common.auction.Singleton;
import common.auction.Product;
import common.auction.Singleton;

public class AuctionClient
{
	public Singleton singleton = Singleton.getInstance();
	private String m_strIP;
	private int m_nPort;
	private String m_strUserID;
	private long m_lProductID;
	private SocketChannel clntChan;
	private ArrayList<Product> m_listProduct;
	
	private String GetServerIP()
	{
		return singleton.GetProperty().GetServerIP();
	}
    
	private int GetServerPort()
	{
		return (int) singleton.GetProperty().GetServerPort();
	}

	public AuctionClient() throws IOException
	{
		
		m_strIP = GetServerIP();
		m_nPort = GetServerPort();
		
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



		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Please enter your username");
		m_strUserID = in.readLine();
		System.out.println("Please enter your password");
		String strPassword = in.readLine();

		InRegisterClient inRegisterClient = new InRegisterClient();
		inRegisterClient.SetUserID(m_strUserID);
		inRegisterClient.SetUserPassword(strPassword);
		
		// 每一次调用read（）方法接收到的字节数
		CommBuffer out = new CommBuffer();
		
		SendRequest( inRegisterClient, out );
		
		OutRegisterClient rsp = new OutRegisterClient(out);
		
		if( rsp.GetState() )
		{
			System.out.println("--------------------------------------------------------------\n" );
			
			System.out.printf("Welcome to Auction System!!!!\n\t*** %s *** \n", rsp.GetUserName());
			
			Scanner input = null;
			
			try
			{
				input =new Scanner (System.in);
				while(true)
				{
					System.out.println("Please choose the number of your next Step:");
					System.out.println("\t1.Offer iterms for selling.\n\t2.Bidding for iterms.\n\t3.Exit.");
					
					
					int nSelection = input.nextInt();
					switch(nSelection)
					{
					case 1:
						// Offer items for selling
						// enumerate all items in your stocks.
						
						m_listProduct = RetrieveStock( m_strUserID );
						System.out.println("\tPlease choose the Product ID to avertise it:\n\t0. Return the root menu.");
						m_lProductID = nSelection = input.nextInt();
						for( Product product: m_listProduct)
						{
							if(product.GetProductID() == nSelection )
							{
								//System.out.println("\t");
								
								//给输入你要的起拍价格
								System.out.println("Please input your advertising price:\n");
								product.SetPrice(Double.parseDouble(in.readLine())); 
								
								
								Advertising(product);
								
								break;
							}
						}
						
						break;
					case 2:   
						Product product = new Product(0,"", 0, 0);
						long lStatus = QueryBidStatus(product);
						switch((int)lStatus)
						{
						case 0: break;
						case 1: break;
						case 2: 
							if( product.GetProductID() == 0 )
							{
								System.out.printf("NO Auction!\n");
							}
							else
							{
								System.out.printf("\tAt present, %s is in progress..\n", product.GetName());
								System.out.printf("\t\t\t%f(%d)..\n", product.GetPrice(), product.GetCount());
								System.out.printf("\tBid Price:\n", product.GetPrice(), product.GetCount());
								product.SetPrice(Double.parseDouble(in.readLine())); 
								Auction(product);
							}
							break;
						case 3: break;
						}

						
						break;
					case 3:   
						// Exit;
						clntChan.close();
						System.exit(0);
						break;
					default:  
						break;
					}
					System.out.println("--------------------------------------------------------------\n" );
				}
				
				
			}
			finally 
			{
			    if(input!=null)
			    {
			    	input.close();
			    }
			}
		}
		
		// 关闭信道
		clntChan.close();
	}
	
	public ArrayList<Product> RetrieveStock( String StrUserID )
	{
		InRetrieveStock inBuffer= new InRetrieveStock();
		inBuffer.SetUserID(StrUserID);

		
		// 每一次调用read（）方法接收到的字节数
		CommBuffer out = new CommBuffer();
		
		SendRequest( inBuffer, out );
		
		ArrayList<Product> listProduct = new ArrayList<Product>();
		listProduct.clear();
		
		OutRetrieveStock outBuffer = new OutRetrieveStock(out);
		
		listProduct = outBuffer.GetListProduct();
		
		System.out.printf("You have the following products:\n" );
		for( Product product: listProduct )
		{
			System.out.printf("\tProduct ID    : %d\n", product.GetProductID() );
			System.out.printf("\tProduct Name  : %s\n", product.GetName() );
			System.out.printf("\tProduct Count : %d\n", product.GetCount() );
			System.out.printf("\tProduct Price : %.2f\n", product.GetPrice() );
			System.out.println("\t----------------------------\n" );
		}
		
		return listProduct;
	}
	
	public void Advertising( Product product)
	{
		InAdvertising inBuffer= new InAdvertising();

		inBuffer.SetProductID(product.GetProductID());
		inBuffer.SetProductName(product.GetName());
		inBuffer.SetProductCount(product.GetCount());
		inBuffer.SetProductPrice(product.GetPrice());
		
		CommBuffer out = new CommBuffer();
		
		SendRequest( inBuffer, out );
		
		OutAdvertising outBuffer = new OutAdvertising(out);
		outBuffer.GetState();
	}
	
	public void Auction( Product product )
	{
		InAuction inBuffer= new InAuction();

		inBuffer.SetProductID(product.GetProductID());
		inBuffer.SetProductName(product.GetName());
		inBuffer.SetProductCount(product.GetCount());
		inBuffer.SetProductPrice(product.GetPrice());
		
		CommBuffer out = new CommBuffer();
		
		SendRequest( inBuffer, out );
		
		OutAuction outBuffer = new OutAuction(out);
		boolean bSuccess = outBuffer.GetState();
		double dblMaxBidPrice = outBuffer.GetMaxBidPrice();
		
		if( bSuccess )
		{
			System.out.printf("\tCongratuation, the price $ %.2f that you give is the highest!\n", dblMaxBidPrice);
		}
		else
		{
			System.out.printf("\tAt present, the maximum bid price is $ %.2f \n", dblMaxBidPrice);
			System.out.println("\tIn order to obtain it, you give the higher price!\n" );
		}
	}
	
	public long QueryBidStatus(Product product)
	{
		InQueryBid inBuffer = new InQueryBid();
		
		CommBuffer out = new CommBuffer();
		
		SendRequest( inBuffer, out );
		
		
		OutQueryBid outBuffer = new OutQueryBid(out);
		
		product.SetProductID( outBuffer.GetProductID() );
		product.SetName( outBuffer.GetProductName() );
		product.SetCount( outBuffer.GetProductCount() );
		product.SetPrice(outBuffer.GetProductPrice() ) ;
		
		return outBuffer.GetStatus();
	}
	
	public int SendRequest(CommBuffer inBuf, CommBuffer outBuf)
	{
		int nRet = 0;
		try
		{
			inBuf.SendBufer(clntChan);
			while (true)
			{
				int readBytes = outBuf.ReceiveBuffer(clntChan);
				if (readBytes > 0)
				{
					nRet = 0;	
					break;
				}
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		return nRet;
	}

	public static void main(String[] args) throws Exception
	{	
		//if( args.length != 2 )
		//{
		//	System.out.print("Parameters: <HOST> <PORT>/n");
		//	return;
		//}
		//
		//new AuctionClient(args[0], Integer.parseInt(args[1]));
		new AuctionClient();
	}

}
