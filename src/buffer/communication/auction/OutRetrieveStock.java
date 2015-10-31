package buffer.communication.auction;

import java.util.ArrayList;

import common.auction.Product;

public class OutRetrieveStock extends CommBuffer
{
	public OutRetrieveStock()
	{
		SetCommand(RSP_RETRIEVE_STOCK_OF_CLIENT);
	}
	
	public OutRetrieveStock(CommBuffer buffer)
	{
		m_buffer = buffer.m_buffer;
		m_buffer.rewind();
	}
	
	protected void CombineBuffer()
	{
		
	}
	
	public ArrayList<Product> GetListProduct()
	{
		ArrayList<Product> listProduct = new ArrayList<Product>();
		
		int nLen = GetLength();
		int position = 2;
		
		while( nLen > 0 )
		{
			int nProductLen = m_buffer.get(position);
			position ++;
			
			int nProductIDLen = m_buffer.get(position);
			position ++;
			long lProductID = m_buffer.getLong(position);
			position += nProductIDLen;
			
			int nProductNameLen = m_buffer.get(position);
			position ++;
			
			byte[] szBuf = new byte[nProductNameLen];
			szBuf = GetBuffer( position, nProductNameLen);
			String strProductName = new String(szBuf);
			position += nProductNameLen;
			
			int nCoundLen = m_buffer.get(position);
			position ++;
			long lCount = m_buffer.getLong(position);
			position += nCoundLen;
			
			int nPriceLen = m_buffer.get(position);
			position ++;
			double dblPrice = m_buffer.getDouble(position);
			position += nPriceLen;
			
			Product product = new Product(lProductID, strProductName, lCount, dblPrice );
			
			listProduct.add(product);
			
			nLen -= nProductLen;
		}
		
		return listProduct;
	}
	
	public void SetListProduct( ArrayList<Product> listProduct )
	{
		int nTotalSize = 0;
		int position = 2;
		final byte LONGOFFSET = 8;
		final byte DOUBLEOFFSET = 8;
		
		for( Product product : listProduct)
		{
			int nProductSize = 8*3 + product.GetName().length();
			nProductSize += 4;
			
			m_buffer.put( position, (byte) nProductSize);
			position++;
			
			m_buffer.put(position, LONGOFFSET );
			position++;
			m_buffer.putLong(position, product.GetProductID());
			position += LONGOFFSET;
			
			int nProductNameLen = product.GetName().length();
			m_buffer.put(position, (byte) nProductNameLen );
			position++;
			PutBuffer(product.GetName().getBytes(), position, nProductNameLen);
			position += nProductNameLen;
		
			m_buffer.put(position, LONGOFFSET );
			position++;
			m_buffer.putLong(position, product.GetCount());
			position += LONGOFFSET;
			
			m_buffer.put(position, DOUBLEOFFSET );
			position++;
			m_buffer.putDouble(position, product.GetPrice());
			position += DOUBLEOFFSET;
			
			
			nTotalSize += nProductSize;
		}
		
		this.SetLength((byte)nTotalSize);
	}
}
