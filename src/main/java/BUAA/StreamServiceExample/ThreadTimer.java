package BUAA.StreamServiceExample;
import java.util.Timer;
import java.util.TimerTask;

public class ThreadTimer {
	Timer timer;
	Thread resultCollectionThread;
	
	public ThreadTimer(Thread resultCollectionThread, long seconds)
	{
		this.timer=new Timer(true);
		this.resultCollectionThread=resultCollectionThread;
		timer.schedule(new TimerTask(){
			public void run()
			{
				//kill 
			}
		}, seconds);
	}
	
	
}
