package common.auction;

public class Product
{
	public long m_productID;
	public String m_strName;
	public long m_lCount;
	public double m_dblPrice;

	public Product(long productID, String strName, long lCount, double dblPrice)
	{
		m_productID = productID;
		m_strName = strName;
		m_lCount = lCount;
		m_dblPrice = dblPrice;
	}

	public long GetProductID()
	{
		return m_productID;

	}

	public String GetName()
	{
		return m_strName;
	}

	public long GetCount()
	{
		return m_lCount;
	}

	public double GetPrice()
	{
		return m_dblPrice;
	}

	public void SetProductID(long P_ID)
	{
		m_productID = P_ID;
	}

	public void SetName(String strName)
	{
		m_strName = strName;
	}

	public void SetCount(long lCount)
	{
		m_lCount = lCount;
	}

	public void SetPrice(double dblPrice)
	{
		m_dblPrice = dblPrice;
	}

}
