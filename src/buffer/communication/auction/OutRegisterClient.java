package buffer.communication.auction;

public class OutRegisterClient extends CommBuffer
{
	private boolean m_bSuccess;
	
	public OutRegisterClient()
	{
		SetCommand(RSP_REGISTER_CLIENT);
		CombineBuffer();
	}
	
	public void SetState( boolean bSuccess)
	{
		m_bSuccess = bSuccess;
		CombineBuffer();
	}
	
	public boolean GetState()
	{
		m_bSuccess = ( m_buffer.get(2) == 0 ) ? false: true ;
		return m_bSuccess;
	}
	
	protected void CombineBuffer()
	{
		SetLength((byte)1);
		m_buffer.put(2,(byte)(m_bSuccess? 1: 0 ));
	}
	
	
}
