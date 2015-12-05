package common.auction;

import java.util.Timer;

public class AdvertisingTimer
{
    Timer timer;
    public AdvertisingTimer(int nSeconds)
    {
        timer = new Timer();
        timer.schedule(new AdvertisingTimerTask(), nSeconds * 1000);
    }
    
	public static void main(String[] args)
	{
		// TODO Auto-generated method stub

	}

}
