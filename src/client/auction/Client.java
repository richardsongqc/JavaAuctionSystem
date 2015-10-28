package client.auction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Date;
import common.auction.*;
import  java.util.*;

public class Client
{
	private String name;
	private Long freeTimeLast=new Date().getTime();
	private Socket s;
	private boolean isLogon=false;
	public Singleton singleton = Singleton.getInstance(); 
	
	private String GetServerIP()
	{
		return singleton.GetProperty().GetServerIP();
	}
	
	private int GetServerPort()
	{
		return (int)singleton.GetProperty().GetServerPort();
	}
	
	public Client()
	{
		BufferedReader inBuffer =new BufferedReader(new InputStreamReader(System.in));
		
		//Connect Server in 2 minutes
		try 
		{
			s=new Socket( GetServerIP(), GetServerPort() );
			new Thread()
			{
				public void run ()
				{
					while(true)
					{
						boolean b=new Date().getTime()-freeTimeLast>=2*60*1000;
						if(b)
						{
							System.exit(1);
						}
					}
				}
			}.start();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			System.exit(0);
		}
		

		Scanner in=new Scanner (System.in);
		System.out.println("Welcome to Auction System!!!!");
		
		// login 
		// 100% rewrite
		new Thread()
		{
			public void run()
			{
				//BufferedReader br=null;
				//try 
				//{
				//	br=new BufferedReader(new InputStreamReader(s.getInputStream()));
				//	while(true)
				//	{
				//		String str=br.readLine();
				//		if(str.startsWith("%"))
				//		{
				//			String[] action=str.split("%");
				//			if(action[1].equals("loginres"))
				//			{
				//				if(action[2].endsWith("true"))
				//				{
				//					isLogon=true;
				//					System.out.println("登录成功！");
				//				}
				//				else
				//				{
				//					System.out.println("登录失败！");
				//				}
				//				
				//			}
				//			else if(action[1].equals("exitres"))
				//			{
				//				if(action[2].equals("0"))
				//				{
				//					System.out.println("本次拍卖已结束，系统自动关闭！");
				//					//printActionMessage("%exitreq");
				//					System.exit(1);
				//				}
				//			}
				//		}
				//		else
				//		{
				//			System.out.println(str+"\n");
				//		}
				//	}
				//} 
				//catch (IOException e) 
				//{
				//	e.printStackTrace();
				//}
			}
		}.start();
		
		// operation
		while (true) 
		{
			try 
			{
				//System.out.println("Welcome! Dear "+username);
				System.out.println("Please choose the number of your next Step:");
				System.out.println("\t1.Offer iterms for selling.\n\t2.Bidding for iterms.\n\t3.Exit.");
				
				int choose_number=in.nextInt();
				switch(choose_number)
				{
				case 1:
					// Offer items for selling
					// enumerate all items in your stocks.
					//
					break;
				case 2:   
					// Bidding for items
					// 
					break;
				case 3:   
					// Exit;
					System.exit(0);
					break;
				default:  
					break;
				}
				
				System.out.println("请选择操作：(buy/exit)");
				String action = inBuffer.readLine();
				if (action.equals("buy")) 
				{
					//System.out.println("请输入你的出价:");
					//String str = inBuffer.readLine();
					//System.out.println("请输入你的数量：");
					//String str0 = inBuffer.readLine();
				} 
				else if (action.equals("exit")) 
				{
					System.exit(0);
				}
			
			} 
			catch (IOException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	//public void resetTime()
	//{
	//	freeTimeLast=new Date().getTime();
	//}
	//
	//public void printActionMessage(String mes)
	//{
	//	try 
	//	{
	//		PrintStream ps=new PrintStream(s.getOutputStream());
	//		ps.println("%exitreq");
	//		ps.flush();
	//	} 
	//	catch (IOException e) 
	//	{
	//		// TODO Auto-generated catch block
	//		e.printStackTrace();
	//	}
	//}
	
	public static void main(String[] args) 
	{
		new Client();
	}
}
