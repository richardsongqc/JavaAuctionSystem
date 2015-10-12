package common.auction;

import static org.junit.Assert.*;

import org.junit.Test;

public class JMSAccessConnTest
{

	@Test
	public void testValidateUser()
	{
		JMSAccessConn access = new JMSAccessConn();
		boolean bValid = access.ValidateUser("ResultSet", "123123");
		System.out.println( bValid == true ? "Valid\n" : "Invalid\n");
		
		bValid = access.ValidateUser("tcruise", "123123");
		System.out.println( bValid == true ? "Valid\n" : "Invalid\n");
	}

}
