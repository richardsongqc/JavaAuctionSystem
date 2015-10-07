package common.auction;

public class Product 
{
	public long productID;
	public String name;
	public long count;
	public double price;
	
	public   Product(long P_D, String N_M, long C_T,double P_E )
	{
		productID=P_D;
		name=N_M;
		count=C_T;
		price=P_E;	
	}
	public long GetProductID()
	{
		return productID;
		
	}
	public String GetName()
	{
		return name;
	}
	public long GetCount()
	{
		return count;
	}
	public double GetPrice()
	{
		return price;
	}
	public void SetProductID(long P_ID)
	{
		productID=P_ID;
	}
	public void SetName(String N_M)
	{
		name=N_M;
	}
	public void SetCount(long C_T)
	{
		count=C_T;
	}
	public void SetPrice(double P_E)
	{
		price=P_E;
	}
	
}
