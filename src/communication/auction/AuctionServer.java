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
	// �������ĳ���
	private static final int BUFSIZE = 256;
	// select�����ȴ��ŵ�׼���õ��ʱ��
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

		// ʵ����һ���ŵ�
		ServerSocketChannel listnChannel = ServerSocketChannel.open();
		// �����ŵ��󶨵�ָ���˿�
		listnChannel.socket().bind(new InetSocketAddress(nPort));
		// �����ŵ�Ϊ������ģʽ
		listnChannel.configureBlocking(false);
		// ��ѡ����ע�ᵽ�����ŵ�
		listnChannel.register(selector, SelectionKey.OP_ACCEPT);
		
		////////////////////////////////////////////////////////////////////////
		// ����һ��ʵ����Э��ӿڵĶ���
		protocol = new SelectorProtocol(BUFSIZE);

		Thread arbiter = new ArbiterThread();
		arbiter.start();
		
		Thread dispatcher = new DispatcherThread();
		dispatcher.start();
		
		System.out.println("Auction Server has already been initialized!\n");

		
		// ������ѯselect��������ȡ׼���õ��ŵ���������Key��
		while (true)
		{
			// һֱ�ȴ�,ֱ�����ŵ�׼������I/O����
			if (selector.select(TIMEOUT) == 0)
			{
				// �ڵȴ��ŵ�׼����ͬʱ��Ҳ�����첽��ִ����������
				// ����ֻ�Ǽ򵥵ش�ӡ"."
				System.out.print(".");
				continue;
			}

			// ��ȡ׼���õ��ŵ���������Key���ϵ�iteratorʵ��
			Iterator<SelectionKey> keyIter = selector.selectedKeys().iterator();
			// ѭ��ȡ�ü����е�ÿ����ֵ
			CommBuffer inBuf = new CommBuffer();
			//CommBuffer outBuf = new CommBuffer();

			while (keyIter.hasNext())
			{
				SelectionKey key = keyIter.next();
				// ���������ŵ�����Ȥ��I/O����Ϊaccept
				if (key.isAcceptable())
				{
					protocol.handleAccept(key);
				}

				// ����ͻ����ŵ�����Ȥ��I/O����Ϊread
				if (key.isReadable())
				{
					protocol.handleRead(key, inBuf);
					m_listInBuffer.addElement(new KeyBufferPair(key, inBuf));
				}
					
				//// ����ü�ֵ��Ч���������Ӧ�Ŀͻ����ŵ�����Ȥ��I/O����Ϊwrite
				//if (key.isValid() && key.isWritable())
				//{
				//	protocol.handleWrite(key, outBuf);
				//}
				
				
				// ������Ҫ�ֶ��Ӽ������Ƴ���ǰ��key
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
						//��������߼۸񱣳�5���ӣ���������Ϳ��Խ����ˡ�
						//ʵ�����������ǻ���Ҫһ���߳�ȥ�������5�������û���¼۸��������
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
				
				// ����ü�ֵ��Ч���������Ӧ�Ŀͻ����ŵ�����Ȥ��I/O����Ϊwrite
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
