package common.auction;

import java.util.Timer;

public class BidTimer
{
    Timer timer;
    public BidTimer(int nSeconds)
    {
        timer = new Timer();
        timer.schedule(new BidTimerTask(), nSeconds * 1000);
    }
}
