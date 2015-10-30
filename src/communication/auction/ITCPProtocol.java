package communication.auction;

import java.io.IOException;
import java.nio.channels.SelectionKey;

import buffer.communication.auction.CommBuffer;

public interface ITCPProtocol
{
    //accept I/O��ʽ  
    void handleAccept(SelectionKey key) throws IOException;  
    //read I/O��ʽ  
    void handleRead(SelectionKey key, CommBuffer cmdBuf) throws IOException;  
    //write I/O��ʽ  
    void handleWrite(SelectionKey key, CommBuffer cmdBuf) throws IOException;  
}
