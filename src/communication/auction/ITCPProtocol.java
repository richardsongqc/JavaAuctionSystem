package communication.auction;

import java.io.IOException;
import java.nio.channels.SelectionKey;

import buffer.communication.auction.CommBuffer;

public interface ITCPProtocol
{
    //accept I/O形式  
    void handleAccept(SelectionKey key) throws IOException;  
    //read I/O形式  
    void handleRead(SelectionKey key, CommBuffer cmdBuf) throws IOException;  
    //write I/O形式  
    void handleWrite(SelectionKey key, CommBuffer cmdBuf) throws IOException;  
}
