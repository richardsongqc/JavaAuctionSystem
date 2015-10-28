package server.auction;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import common.auction.Singleton;

public class Server
{
	private ServerSocket ss;
	private HashSet<Socket> hs;
	private HashMap<String,String> users=new HashMap<String,String>();
	private Map<String,Socket> userSockets=new HashMap<String,Socket>();
	public Singleton singleton = Singleton.getInstance(); 
	
	//private String GetServerIP()
	//{
	//	return singleton.GetProperty().GetServerIP();
	//}
	
	private int GetServerPort()
	{
		return (int)singleton.GetProperty().GetServerPort();
	}
	
	public Server()
	{	
		do
		{	
			try 
			{
				//BufferedReader in=new BufferedReader( new InputStreamReader(System.in)); 
				ss=new ServerSocket(GetServerPort());
				System.out.println("SocketServer has already been initialized!");
				//new SaleSystem().start();
				
				//BufferedReader bf=new BufferedReader(new InputStreamReader(new FileInputStream(userFile)));
				//while(bf.ready())
				//{
				//	String userPwd=bf.readLine();
				//	String[] userP=userPwd.split(":"); 
				//	users.put(userP[0], userP[1]);
				//}
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
		}
		while(ss==null);
		
		hs=new HashSet<Socket>();
		while(true)
		{
			try 
			{
				Socket s = ss.accept();
				hs.add(s);
				//new ServerThread(s).start();
				//In server Thread, we should define our logic.
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
			
		
	}
	
	//class SaleSystem extends Thread
	//{
	//	public SaleSystem()
	//	{		
	//	}
	//	
	//	public void run()
	//	{
	//		while(true)
	//		{
	//			System.out.println("请输入操作(sale)");
	//			BufferedReader in=new BufferedReader(new InputStreamReader(System.in));
	//			try 
	//			{
	//				String action=in.readLine();
	//				if(action.equals("sale"))
	//				{
	//					System.out.println("请输入拍卖物品的名称：");
	//					String sname=in.readLine();
	//					System.out.println("请输入拍卖物品的数量：");
	//					String snum=in.readLine();
	//					System.out.println("请输入拍卖物品的介绍：");
	//					String sinfo=in.readLine();
	//					System.out.println("请输入拍卖几分钟后结束：");
	//					String send=in.readLine();
	//					StringBuffer sb=new StringBuffer();
	//					sb.append("拍卖物品的名称：");
	//					sb.append(sname);
	//					sb.append("\n");
	//					sb.append("拍卖物品的数量：");
	//					sb.append(snum);
	//					sb.append("\n");
	//					sb.append("拍卖物品的介绍：");
	//					sb.append(sinfo);
	//					sb.append("\n");
	//					
	//					SaleInfo saleIn=new SaleInfo(sname,snum,sinfo,send);
	//					sb.append("拍卖结束时间：");
	//					Calendar end=saleIn.getEnd();
	//					sb.append(end.get(Calendar.YEAR)+"-");
	//					sb.append((end.get(Calendar.MONTH)+1)+"-");
	//					sb.append(end.get(Calendar.DAY_OF_MONTH)+" ");
	//					sb.append(end.get(Calendar.HOUR)+":");
	//					sb.append(end.get(Calendar.MINUTE)+":");
	//					sb.append(end.get(Calendar.SECOND));
	//					
	//					sb.append("\n");
	//					setSaleInfo(saleIn);
	//					saleInfo_=sb.toString();
	//					printMessage(sb.toString());
	//				}
	//			} 
	//			catch (IOException e) 
	//			{
	//				// TODO Auto-generated catch block
	//				e.printStackTrace();
	//			}
	//		}
	//	}
	//	
	//	//private void printMessage(String mes)
	//	//{
	//	//	//System.out.println(mes);
	//	//	try 
	//	//	{
	//	//		for(Object obj:hs)
	//	//		{
	//	//			Socket temp=(Socket)obj;
	//	//			PrintStream ps=new PrintStream(temp.getOutputStream());
	//	//			Calendar c=new GregorianCalendar();
	//	//			StringBuffer sb=new StringBuffer();
	//	//			sb.append(c.get(Calendar.YEAR)+"-");
	//	//			sb.append((c.get(Calendar.MONTH)+1)+"-");
	//	//			sb.append(c.get(Calendar.DAY_OF_MONTH)+" ");
	//	//			sb.append(c.get(Calendar.HOUR)+":");
	//	//			sb.append(c.get(Calendar.MINUTE)+":");
	//	//			sb.append(c.get(Calendar.SECOND));
	//	//			ps.println("系统拍卖消息"+"("+sb.toString()+")->:"+"\n"+mes);
	//	//			ps.flush();
	//	//		}
	//	//	} 
	//	//	catch (IOException e) 
	//	//	{
	//	//		e.printStackTrace();
	//	//	}
	//	//}
	//}
	
	//class ServerThread extends Thread
	//{
	//	private Socket s;
    //
	//	public ServerThread(Socket s)
	//	{
	//		this.s=s;
	//	}
	//	
	//	public void run()
	//	{	 
	//		try 
	//		{
	//			while(true)
	//			{
	//				BufferedReader br=new BufferedReader(new InputStreamReader(s.getInputStream()));
	//				String str=br.readLine();
	//				System.out.println(str.charAt(0));
	//				if(str.startsWith("%"))
	//				{
	//					System.out.println("action->"+str);///
	//					String[] action=str.split("%");
	//					
	//					if (action[1].equals("exitreq")) 
	//					{
	//						hs.remove(s);
	//						//printMessage(str.split("%")[2] + ",离开聊天室！");
	//						s.close();
	//						break;
	//					} 
	//					else if (action[1].endsWith("loginreq")) 
	//					{
	//						String username = action[2];
	//						String pwd = action[3];
	//						String password = users.get(username);
	//						if (password != null && password.equals(pwd)) 
	//						{
	//							PrintStream ps = new PrintStream(s.getOutputStream());
	//							ps.println("%loginres%true");
	//							System.out.println(username + "登录成功！");
	//							printMessage("欢迎您，登录本拍卖系统。");
	//							userSockets.put(username, s);
	//							if(saleInfo_!=null&&!saleInfo_.equals(""))
	//							{
	//								printMessage(saleInfo_);
	//							}
	//							sendSaleMes(username,s);
	//						} 
	//						else 
	//						{
	//							PrintStream ps = new PrintStream(s.getOutputStream());
	//							ps.println("%loginres%false");
	//							System.out.println(username + "登录失败！");
	//						}
    //
	//					}
	//					else if(action[1].endsWith("buyreq"))
	//					{
	//						String name=action[2];
	//						Double sprice=new Double(action[3]);
	//						int snum=new Integer(action[4]);
	//						SaleRecord sr=new SaleRecord(s,sprice,name,snum);
	//						System.out.println(sr.getName()+"-"+sr.getNum()+"-"+sr.getPrice());///
	//						saleRecords.put(sr.getPrice(), sr);
	//					}
	//				}
	//				else
	//				{//"%EXIT%"+name
	//					printMessage(str);
	//				}
	//			}
	//		} 
	//		catch (IOException e) 
	//		{
	//			e.printStackTrace();	
	//		}
	//	}
	//	
	//	private void printActionMessage(String mes) 
	//	{
	//		try 
	//		{
	//			PrintStream ps = new PrintStream(s.getOutputStream());
	//			ps.println(mes);
	//			ps.flush();
	//		} 
	//		catch (IOException e) 
	//		{
	//			// TODO Auto-generated catch block
	//			e.printStackTrace();
	//		}
	//	}
	//	
	//	private void printMessage(String mes)
	//	{
	//		System.out.println(mes);
	//		try 
	//		{		
	//			PrintStream ps=new PrintStream(s.getOutputStream());
	//			Calendar c=new GregorianCalendar();
	//			StringBuffer sb=new StringBuffer();
	//			sb.append(c.get(Calendar.YEAR)+"-");
	//			sb.append((c.get(Calendar.MONTH)+1)+"-");
	//			sb.append(c.get(Calendar.DAY_OF_MONTH)+" ");
	//			sb.append(c.get(Calendar.HOUR)+":");
	//			sb.append(c.get(Calendar.MINUTE)+":");
	//			sb.append(c.get(Calendar.SECOND));
	//			ps.println(mes+"("+sb.toString()+")");
	//			ps.flush();
	//			
	//		} 
	//		catch (IOException e) 
	//		{
	//			e.printStackTrace();
	//		}
	//	}
	//	
	//	private void sendSaleMes(String username, Socket s) 
	//	{
	//		SaleMes mes = saleMes.get(username);
	//		if (mes != null) 
	//		{
	//			StringBuffer sb = new StringBuffer();
	//			List meses = mes.getMes();
	//			Iterator it = meses.iterator();
	//			while (it.hasNext()) 
	//			{
	//				sb.append(it.next());
	//				sb.append("\n");
	//			}
	//			
	//			if (mes != null && !mes.equals("")) 
	//			{
	//				printMessage(sb.toString());
	//			}
	//		}
	//	}
	//}
	//
	//public static void main(String[] args) 
	//{
	//	new Server();
	//}
    //
	//public void setSaleInfo(SaleInfo saleInfo) 
	//{
	//	this.saleInfo = saleInfo;
	//	final long l=saleInfo.getSometime()*60*1000;
	//	new Thread()
	//	{
	//		public void run()
	//		{
	//			try {
	//				Thread.sleep(l);
	//				System.out.println("拍卖结束!");
	//				printActionMessage("%exitres%0");
	//				Thread.sleep(1000);
	//				saleout();
	//			} 
	//			catch (InterruptedException e) 
	//			{
	//				// TODO Auto-generated catch block
	//				e.printStackTrace();
	//			}
	//		}
	//	}.start();
	//}
	//
	//public void saleout()
	//{
	//	//System.out.println("out0");
	//	saleInfo_=null;
	//	Iterator it=saleRecords.keySet().iterator();
	//	Iterator it0=saleRecords.keySet().iterator();
	//	//double price=0.0;
	//	int num=0;
	//	int left=saleInfo.getLeft();
	//	int left0=saleInfo.getLeft();
	//	while(left0>0&&it0.hasNext())
	//	{
	//		//System.out.println("out0-");
	//		SaleRecord saleRecord=saleRecords.get(it0.next());
	//		left0=left0-saleRecord.getNum();
	//		if(left0<=0||!it0.hasNext())
	//		{
	//			saleInfo.setRealPrice(saleRecord.getPrice());
	//		}
	//	}
	//	
	//	//System.out.println("left"+left);
	//	System.out.println("saleInfo.getRealPrice()->"+saleInfo.getRealPrice());
	//	while(left>0&&it.hasNext())
	//	{
	//		//System.out.println("out21");
	//		SaleRecord saleRecord=saleRecords.get(it.next());
	//		//price=saleRecord.getPrice();
	//		num=num+saleRecord.getNum();
	//		//System.out.println("left0"+left);
	//		//System.out.println("saleRecord.getNum()"+saleRecord.getNum());
	//		left=left-saleRecord.getNum();
	//		//System.out.println("left1"+left);
	//		String uname=saleRecord.getName();
	//		double price=saleRecord.getPrice();
	//		Socket s=userSockets.get(uname);
	//		if(left>0)
	//		{
	//		//	System.out.println("out22");
	//			if (!s.isClosed()) 
	//			{
	//				ServerThread st=new ServerThread(s);
	//				String sm="恭喜您!在本次的拍中()您成功的拍得的数量为" + saleRecord.getNum()
	//				+ "单价为" + saleInfo.getRealPrice();
	//				st.printMessage(sm);
	//			}
	//			else 
	//			{
	//				SaleMes uMes = saleMes.get(uname);
	//				System.out.println("add sale Message!");
	//				if (uMes != null) 
	//				{
	//					uMes.addMes("恭喜您!在上次的拍中()您成功的拍得的数量为"
	//							+ saleRecord.getNum() + "单价为"
	//							+ saleInfo.getRealPrice());
	//				} 
	//				else 
	//				{
	//					SaleMes uMes0=new SaleMes();
	//					
	//					uMes0.addMes("恭喜您!在上次的拍中()您成功的拍得的数量为"
	//							+ saleRecord.getNum() + "单价为"
	//							+ saleInfo.getRealPrice());
	//					saleMes.put(uname, uMes0);
	//				}
	//			}
	//		}
	//		else
	//		{
	//		//	System.out.println("out3");
	//			if (!s.isClosed()) 
	//			{
	//				ServerThread st=new ServerThread(s);
	//				int num0=saleRecord.getNum()+left;
	//				String sm="恭喜您!在本次的拍中()您成功的拍得的数量为" + num0
	//				+ "单价为" + saleInfo.getRealPrice();
	//				st.printMessage(sm);
	//			} 
	//			else 
	//			{
	//				SaleMes uMes = saleMes.get(uname);
	//				System.out.println("add sale Message!");
	//				if (uMes != null) 
	//				{
	//					int num0 = saleRecord.getNum() + left;
	//					uMes.addMes("恭喜您!在上次的拍中()您成功的拍得的数量为" + num0 + "单价为"
	//							+ saleInfo.getRealPrice());
	//				} 
	//				else 
	//				{
	//					SaleMes uMes0 = new SaleMes();
    //
	//					int num0 = saleRecord.getNum() + left;
	//					uMes0.addMes("恭喜您!在上次的拍中()您成功的拍得的数量为" + num0 + "单价为"
	//							+ saleInfo.getRealPrice());
	//					saleMes.put(uname, uMes0);
	//				}
	//			}
	//		}
	//	}
	//}

	private void printActionMessage(String mes) 
	{
		// System.out.println(mes);
		try 
		{
			for (Object obj : hs) 
			{
				Socket temp = (Socket) obj;
				PrintStream ps = new PrintStream(temp.getOutputStream());
				ps.println(mes);
				ps.flush();
			}
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}

}
