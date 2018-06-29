package BUAA.StreamServiceExample;

import static spark.Spark.get;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;



/**
 * Hello world!
 *
 */
public class App 
{
	public static HashMap<String, String> properties=new HashMap<>(); 
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
    	get("/:time/:ID/:result",(req,res)->{
			String time=req.params(":time");
			String ID=req.params(":ID");
			String result=req.params(":resultReturnURL");
			
			init(Long.parseLong(time),ID,result);
			
			
			return "I'm wang"+time+ID+result;
		});
    	
    	System.out.println(System.getProperty("user.dir"));
    	
    	//init();
    }
    
    public static void init(long time,String ID, String resultReturnURL)
    {
    	try {  
    		//read properties
    		
    		properties.put("topologyName","domain");
    		properties.put("jarPath","/bin/DomainNameFilter-0.1.jar");
    		properties.put("mainClass","shushu.DomainNameFilter.TopologyMain");
    		properties.put("parameters","/usr/local/shushu-storm/domain.log");//这个要改成数据源dataSource1、dataSource2。。。在配置文件中dataSource分为kafkaURL和topic两部分
    		properties.put("resultkafkaURL", "lll");
    		properties.put("resultTopic", "hello");//这个topic需要自动生成
            
    		initStorm();
    		Thread resultCollectionThread=initResultCollection(ID, resultReturnURL);
    		initThreadTimer(resultCollectionThread, time);
    		
            
            
            System.out.println("init completed");
  
        } catch (Exception e) {  
        	System.out.println("------error------");  
            e.printStackTrace();  
        }  
    }
    
    public static void initStorm() throws IOException, InterruptedException
    {
    	String[] cmd = new String[] { "/bin/sh", "-c", "storm list" };  
        Process ps = Runtime.getRuntime().exec(cmd);  

        BufferedReader br = new BufferedReader(new InputStreamReader(ps.getInputStream()));  

        String line;  
        boolean read=false;
        while ((line = br.readLine()) != null) {  
        	if(line.startsWith("---"))
        		read=true;
        	if(read)
        	{
        		System.out.println(line);
        		if(line.split(" ")[0].equals(properties.get("topologyName")))
        		{
        			ps=Runtime.getRuntime().exec(new String[]{"/bin/sh","-c" ,"storm kill "+properties.get("topologyName")});
        			System.out.println("killing duplicated topology");
        			Thread.sleep(35000);
        		}

        	}
        }  
        System.out.println("storm jar "+System.getProperty("user.dir")+properties.get("jarPath")+" "+properties.get("mainClass")+" "+properties.get("parameters"));
        ps=Runtime.getRuntime().exec(new String[]{"/bin/sh","-c" ,"storm jar "+System.getProperty("user.dir")+properties.get("jarPath")+" "+properties.get("mainClass")+" "+properties.get("parameters")});
        ps.waitFor();
        br=new BufferedReader(new InputStreamReader(ps.getInputStream()));  
        System.out.println("execute!");
        while ((line = br.readLine()) != null) {  
        	System.out.println(line);
        }  
    }
    
    public static Thread initResultCollection(String ID, String resultReturnURL)
    {
    	ResultCollection resultCollection=new ResultCollection(properties.get("resultkafkaURL"),properties.get("resultTopic"));
    	Thread thread=new Thread(resultCollection);
    	thread.start();
    	return thread;
    }
    
    public static void initTimer(Thread resultCollectionThread, long time)
    {
    	ThreadTimer timer=new ThreadTimer(resultCollectionThread, time);
    	
    }
}
