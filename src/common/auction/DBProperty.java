package common.auction;

import java.io.*;
import java.util.Properties;

public class DBProperty
{
	private String m_strAccessFilePath;
	private String m_strServerIP;
	private long m_lServerPort;
	
	public DBProperty()
	{
		loadFromConfigFile();
	}

	public void loadFromConfigFile()
	{
		Properties prop = new Properties();
		FileInputStream input = null;


		try
		{
			String strPath = "D:\\Richard\\workspace\\JavaAuctionSystem\\config.properties";
			input = new FileInputStream(strPath);
			//input = this.getClass().getClassLoader().getResourceAsStream(strPath);

			if( input != null )
			{
				// load a properties file
				prop.load(input);
				m_strAccessFilePath = prop.getProperty("AccessFilePath");
				m_strServerIP = prop.getProperty("ServerIP");
				m_lServerPort = Long.parseLong(prop.getProperty("ServerPort"));
			}

		}
		catch (IOException ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			if (input != null)
			{
				try
				{
					input.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	public String GetAccessFilePath()
	{
		return m_strAccessFilePath;
	}
	
	public String GetServerIP()
	{
		return m_strServerIP;
	}
	
	public long GetServerPort()
	{ 
		return m_lServerPort;
	}
	
	public static void main(String[] args)
	{
		DBProperty db = new DBProperty();
		
		System.out.println( db.GetAccessFilePath() );

	}

}
