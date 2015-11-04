package communication.auction;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import buffer.communication.auction.BroadcastProduct;
import buffer.communication.auction.CommBuffer;
import buffer.communication.auction.InAdvertising;
import buffer.communication.auction.InAuction;
import buffer.communication.auction.InRegisterClient;
import buffer.communication.auction.InRetrieveStock;
import buffer.communication.auction.KeyBufferPair;
import buffer.communication.auction.OutAdvertising;
import buffer.communication.auction.OutAuction;
import buffer.communication.auction.OutRegisterClient;
import buffer.communication.auction.OutRetrieveStock;
import common.auction.JMSAccessConn;
import common.auction.Product;
import common.auction.RegisterStateNamePair;
import common.auction.Singleton;

public class AuctionServer
{
	// 缓冲区的长度
	private static final int BUFSIZE = 256;
	// select方法等待信道准备好的最长时间
	private static final int TIMEOUT = 3000;
	private ITCPProtocol protocol;
	private Selector selector;
	private long m_lAuctionID;
	
	private Singleton singleton = Singleton.getInstance();

	private Vector<KeyBufferPair> m_listInBuffer;
	private Vector<KeyBufferPair> m_listOutBuffer;
	
	private int GetServerPort()
	{
		return (int) singleton.GetProperty().GetServerPort();
	}
	
	private JMSAccessConn GetDBConn()
	{
		return singleton.GetDBConn();
	}
	
	public AuctionServer() throws IOException
	{
		m_listInBuffer = new Vector<KeyBufferPair>();
		m_listOutBuffer = new Vector<KeyBufferPair>();
		
		int nPort = GetServerPort();

		selector = Selector.open();

		// 实例化一个信道
		ServerSocketChannel listnChannel = ServerSocketChannel.open();
		// 将该信道绑定到指定端口
		listnChannel.socket().bind(new InetSocketAddress(nPort));
		// 配置信道为非阻塞模式
		listnChannel.configureBlocking(false);
		// 将选择器注册到各个信道
		listnChannel.register(selector, SelectionKey.OP_ACCEPT);
		
		////////////////////////////////////////////////////////////////////////
		// 创建一个实现了协议接口的对象
		protocol = new SelectorProtocol(BUFSIZE);

		Thread arbiter = new ArbiterThread();
		arbiter.start();
		
		Thread dispatcher = new DispatcherThread();
		dispatcher.start();
		
		System.out.println("Auction Server has already been initialized!\n");

		
		// 不断轮询select方法，获取准备好的信道所关联的Key集
		while (true)
		{
			// 一直等待,直至有信道准备好了I/O操作
			if (selector.select(TIMEOUT) == 0)
			{
				// 在等待信道准备的同时，也可以异步地执行其他任务，
				// 这里只是简单地打印"."
				System.out.print(".");
				continue;
			}

			// 获取准备好的信道所关联的Key集合的iterator实例
			Iterator<SelectionKey> keyIter = selector.selectedKeys().iterator();
			// 循环取得集合中的每个键值
			CommBuffer inBuf = new CommBuffer();
			//CommBuffer outBuf = new CommBuffer();

			while (keyIter.hasNext())
			{
				SelectionKey key = keyIter.next();
				// 如果服务端信道感兴趣的I/O操作为accept
				if (key.isAcceptable())
				{
					protocol.handleAccept(key);
				}

				// 如果客户端信道感兴趣的I/O操作为read
				if (key.isReadable())
				{
					protocol.handleRead(key, inBuf);
					m_listInBuffer.addElement(new KeyBufferPair(key, inBuf));
				}
					
				//// 如果该键值有效，并且其对应的客户端信道感兴趣的I/O操作为write
				//if (key.isValid() && key.isWritable())
				//{
				//	protocol.handleWrite(key, outBuf);
				//}
				
				
				// 这里需要手动从键集中移除当前的key
				keyIter.remove();
			}
		}
	}

	public class ArbiterThread extends Thread
	{
		public ArbiterThread()
		{
		}

		public void run()
		{
			while(true)
			{	
				if( m_listInBuffer.size() == 0)
				{
					continue;
				}
				
				KeyBufferPair keyPair = m_listInBuffer.firstElement();
				CommBuffer buffer = keyPair.GetCommBuffer();
				switch(buffer.GetCommand())
				{
				case 1:
					// Register Client
					InRegisterClient inRegisterClient = new InRegisterClient(buffer);
					
					String strUserID = inRegisterClient.GetUserID();
					String strPassword = inRegisterClient.GetUserPassword();
					
					RegisterStateNamePair statePair = GetDBConn().ValidateUser(strUserID, strPassword);
					statePair.SetUserID(strUserID);
					OutRegisterClient outBuffer = new OutRegisterClient();
					
					keyPair.SetState(statePair);
					
					outBuffer.SetState(statePair.GetValid());
					if( statePair.GetValid() )
					{
						outBuffer.SetUserName(statePair.GetName());
					}
					
					m_listOutBuffer.addElement( new KeyBufferPair(keyPair.GetSelectionKey(), outBuffer));
					
					break;
				case 2:
					// Retrieve the stock
					InRetrieveStock inRetrieveStock = new InRetrieveStock(buffer);
					strUserID = inRetrieveStock.GetUserID();
					ArrayList<Product> listProduct = GetDBConn().GetProductList(strUserID);
					
					OutRetrieveStock outRetrieveStock = new OutRetrieveStock();
					outRetrieveStock.SetListProduct(listProduct);
					
					m_listOutBuffer.addElement( new KeyBufferPair(keyPair.GetSelectionKey(), outRetrieveStock));
					break;
				case 3: 
					// Advertising
					InAdvertising inAdvertising= new InAdvertising();
						
					long lProductID = inAdvertising.GetProductID();
					String strProductName = inAdvertising.GetProductName();
					long lProductCount = inAdvertising.GetProductCount();
					double dblProductPrice = inAdvertising.GetProductPrice();
					
					// Broadcast the packages to all the clients( except the current client) that should be registered successfully.
					BroadcastProduct broadcast = new BroadcastProduct();
					
					broadcast.SetProductID(lProductID);
					broadcast.SetProductCount(lProductCount);
					broadcast.SetProductPrice(dblProductPrice);
					broadcast.SetProductName(strProductName);
					
					for( SelectionKey key : selector.selectedKeys() )
					{
						if( key != keyPair.GetSelectionKey()  && keyPair.GetState().GetValid()  )
						{
							m_listOutBuffer.addElement( new KeyBufferPair(key, broadcast));
						}
					}
					
					m_lAuctionID = GetDBConn().GetCurrentAuctionID();
					m_lAuctionID ++ ;
					
					OutAdvertising outAdvertising = new OutAdvertising();
					outAdvertising.SetState(true);
					m_listOutBuffer.addElement( new KeyBufferPair(keyPair.GetSelectionKey(), outAdvertising));
					
					
					// *****************************************************************************
					// After 5 minutes, the Auction should be started and the bids will be allowed.
					
					
					
					
					
					
					//******************************************************************************
					break;
				case 4:
					// Auction
					InAuction inAuction= new InAuction();
					
					// Check the max bid price
					double dblBidPrice = inAuction.GetProductPrice();
					double dblMaxBidPrice = GetDBConn().GetMaxBidPrice(m_lAuctionID);
					
					OutAuction outAuction = new OutAuction();
					
					if( dblBidPrice > dblMaxBidPrice )
					{
						GetDBConn().SetBidTransaction(
								m_lAuctionID, 
								keyPair.GetState().GetUserID(),
								inAuction.GetProductID(), 
								inAuction.GetProductCount(),
								inAuction.GetProductPrice());
						
						BroadcastProduct bid = new BroadcastProduct();
						
						bid.SetProductID(inAuction.GetProductID());
						bid.SetProductCount(inAuction.GetProductCount());
						bid.SetProductPrice(inAuction.GetProductPrice());
						bid.SetProductName(inAuction.GetProductName());
						
						for( SelectionKey key : selector.selectedKeys() )
						{
							if( key != keyPair.GetSelectionKey()  && keyPair.GetState().GetValid()  )
							{
								m_listOutBuffer.addElement( new KeyBufferPair(key, bid) );
							}
						}
						
						outAuction.SetState(true);
						
						//*********************************************************
						//如果这个最高价格保持5分钟，这个拍卖就可以结束了。
						//实现起来，我们还需要一个线程去检查在这5分钟里，有没有新价格高于它。
						//*********************************************************
					}
					else
					{
						outAuction.SetState(false);
						outAuction.SetMaxBidPrice(dblMaxBidPrice);
					}
					
					m_listOutBuffer.addElement( new KeyBufferPair(keyPair.GetSelectionKey(), outAuction));
					
					
					break;
				case 5: 
					break;
				}
				
				m_listInBuffer.removeElementAt(0);
			}
		}
	}
	
	public class DispatcherThread extends Thread
	{
		public DispatcherThread()
		{
		}

		public void run()
		{
			while(true)
			{	
				if( m_listOutBuffer.size() == 0)
				{
					continue;
				}	
			
				KeyBufferPair keyPair = m_listOutBuffer.firstElement();
				SelectionKey key = keyPair.GetSelectionKey();
				
				// 如果该键值有效，并且其对应的客户端信道感兴趣的I/O操作为write
				if (key.isValid() && key.isWritable())
				{
					try
					{
						protocol.handleWrite(key, keyPair.GetCommBuffer());
					}
					catch (IOException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				m_listOutBuffer.removeElementAt(0);
				
			}
		}
	}
	
	
	
	public static void main(String[] args) throws IOException
	{
		new AuctionServer();
	}

}
