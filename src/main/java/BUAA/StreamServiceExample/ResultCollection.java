package BUAA.StreamServiceExample;

public class ResultCollection implements Runnable{
	String kafkaUrl;
	String topic;
	public ResultCollection(String kafkaUrl, String topic){
		this.kafkaUrl=kafkaUrl;
		this.topic=topic;
	}
	
	public void run(){
		
	}
}
