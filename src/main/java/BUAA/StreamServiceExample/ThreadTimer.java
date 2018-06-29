package BUAA.StreamServiceExample;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;


public class ThreadTimer {
	Timer timer;
	Thread resultCollectionThread;
	long seconds;
	
	public ThreadTimer(Thread resultCollectionThread, long seconds)
	{
		this.timer=new Timer(true);
		this.resultCollectionThread=resultCollectionThread;
		this.seconds=seconds;
	}
	
	public void run()
	{
		System.out.println("start timing, kill process after "+seconds);
		timer.schedule(new TimerTask(){
			public void run()
			{
				//kill resultCollection
				resultCollectionThread.interrupt();
				System.out.println("resultCollectionthread is killed");
				
				//kill storm
//				try {
//					String[] commands= new String[]{"/bin/sh","-c","storm kill -w 0"};
//					Process ps=Runtime.getRuntime().exec(commands);
				//System.out.println("storm is killed");
//				}
//				catch(IOException e)
//				{
//					e.printStackTrace();
//				}
			}
		}, seconds);
	}
}
