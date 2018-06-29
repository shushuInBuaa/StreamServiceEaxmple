package BUAA.StreamServiceExample;

import static spark.Spark.get;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Properties;



/**
 * Hello world!
 *
 */
public class App 
{
	public static Properties properties=new Properties();
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
    	get("/:time/:ID/:resultReturnURL/:resultKafkaURL/:resultTopic",(req,res)->{
    		System.out.println("----------using service-----------");
			String time=req.params(":time");
			String ID=req.params(":ID");
			String result=req.params(":resultReturnURL");
			String resultKafkaURL=req.params(":resultKafkaURL");
			String resultTopic=req.params(":resultTopic");
			
			init(Long.parseLong(time),ID,result,resultKafkaURL,resultTopic);
			
			
			return "I'm wang"+time+ID+result;
		});
    	
    	System.out.println(System.getProperty("user.dir"));
    	
    }
    
    public static void init(long time,String ID, String resultReturnURL,String resultKafkaURL, String resultTopic)
    {
    	try {  
    		//read properties
    		
    		BufferedReader bf=new BufferedReader(new FileReader(System.getProperty("user.dir")+"/config.properties"));
    		
    		
    		properties.load(bf);
    		
    		//properties.put("topologyName","domain");
    		//properties.put("jarPath","/bin/DomainNameFilter-0.1.jar");
    		//properties.put("mainClass","shushu.DomainNameFilter.TopologyMain");
    		//properties.put("parameters","/usr/local/shushu-storm/domain.log");//���Ҫ�ĳ�����ԴdataSource1��dataSource2�������������ļ���dataSource��ΪkafkaURL��topic������
    		//the following properties are available in services with parameters specified by users
    		//properties.put("resultkafkaURL", "lll");
    		//properties.put("resultTopic", "hello");
            
    		//initiate in three processes
    		//initStorm();
    		Thread resultCollectionThread=initResultCollection(ID, resultReturnURL,resultKafkaURL, resultTopic);
    		initTimer(resultCollectionThread, time);
    		
            
            
            System.out.println("init completed");
  
        } catch (Exception e) {  
        	System.out.println("------error------");  
            e.printStackTrace();  
        }  
    }
    
    public static void initStorm() throws IOException, InterruptedException
    {
    	System.out.println("----------init storm-----------");
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
                
        //更改storm执行语句，添加数据源参数
        //
        //
        //
        //
        //
        
        System.out.println("storm jar "+System.getProperty("user.dir")+properties.get("jarPath")+" "+properties.get("mainClass")+" "+properties.get("parameters"));
        ps=Runtime.getRuntime().exec(new String[]{"/bin/sh","-c" ,"storm jar "+System.getProperty("user.dir")+properties.get("jarPath")+" "+properties.get("mainClass")+" "+properties.get("parameters")});
        ps.waitFor();
        br=new BufferedReader(new InputStreamReader(ps.getInputStream()));  
        System.out.println("execute!");
        while ((line = br.readLine()) != null) {  
        	System.out.println(line);
        }  
    }
    
    public static Thread initResultCollection(String ID, String resultReturnURL, String resultKafkaURL, String resultTopic)
    {
    	System.out.println("----------init result collection-----------");
    	ResultCollection resultCollection=new ResultCollection(resultKafkaURL,resultTopic,ID, resultReturnURL);
    	Thread thread=new Thread(resultCollection);
    	thread.start();
    	return thread;
    }
    
    public static void initTimer(Thread resultCollectionThread, long time)
    {
    	System.out.println("----------init timer-----------");
    	ThreadTimer timer=new ThreadTimer(resultCollectionThread, time);
    	timer.run();
    }
}
