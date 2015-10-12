package common.auction;

import java.io.*;
import java.util.Properties;

public class DBProperty
{
	private String m_strAccessFilePath;
	
	DBProperty()
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
	
	
	
	public static void main(String[] args)
	{
		DBProperty db = new DBProperty();
		
		System.out.println( db.GetAccessFilePath() );

	}

}
