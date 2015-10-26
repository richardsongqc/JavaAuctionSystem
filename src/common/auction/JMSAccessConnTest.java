package common.auction;

import static org.junit.Assert.*;
import java.util.ArrayList;
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
	
	@Test
	public void testGetProductList()
	{
		JMSAccessConn access = new JMSAccessConn();
		ArrayList<Product> listProduct = access.GetProductList(1);
		
		for( Product product : listProduct)
		{
			System.out.println("Product ID: " + product.GetProductID());
			System.out.println("Product Name: " + product.GetName());
			System.out.println("Product Count: " + product.GetCount());
			System.out.println("Product Price: " + product.GetPrice());
		}
		
		System.out.println("======================================\n");
		listProduct = access.GetProductList("Tom Cruise");
		for( Product product : listProduct)
		{
			System.out.println("Product ID: " + product.GetProductID());
			System.out.println("Product Name: " + product.GetName());
			System.out.println("Product Count: " + product.GetCount());
			System.out.println("Product Price: " + product.GetPrice());
		}
	}
}
