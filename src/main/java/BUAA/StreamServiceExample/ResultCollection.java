package BUAA.StreamServiceExample;

import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

public class ResultCollection implements Runnable{
	String kafkaUrl;
	String topic;
	String ID;
	String resultReturnURL;
	
	public ResultCollection(String kafkaUrl, String topic, String ID, String resultReturnURL){
		this.kafkaUrl=kafkaUrl;
		this.topic=topic;
		this.ID=ID;
		this.resultReturnURL=resultReturnURL;
	}
	
	public void run(){
		//kafka获取数据
		//使用现有程序往里面压数据
		//使用api读取
		
		CloseableHttpClient client=HttpClients.createDefault();
		CloseableHttpResponse response=null;
		
		RequestConfig config=RequestConfig.custom().setConnectTimeout(35000)
				.setConnectionRequestTimeout(35000)
				.setSocketTimeout(60000)
				.build();
		try
		{
			URIBuilder builder=new URIBuilder(resultReturnURL);
			
			Properties properties=new Properties();
			properties.put("bootstrap.servers",kafkaUrl);
			properties.put("group.id","group-result");
			properties.put("enable.auto.commit", "true");
			properties.put("auto.commit.interval.ms","1000");
			properties.put("auto.offset.reset", "earliest");
			properties.put("session.timeout.ms","30000");
			properties.put("key.deserializer","org.apache.kafka.common.serialization.StringDeserializer");
			properties.put("value.deserializer","org.apache.kafka.common.serialization.StringDeserializer");
			
			KafkaConsumer<String, String> consumer=new KafkaConsumer<>(properties);
			consumer.subscribe(Arrays.asList(topic));
			
			while(true)
			{
				ConsumerRecords<String, String> records=consumer.poll(100);
				for(ConsumerRecord<String, String> record:records)
				{
					SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					ResultMessage message=new ResultMessage(ID, df.format(new Date()),record.value());
					
					HttpPost post=new HttpPost(builder.build());
					post.setConfig(config);
					post.addHeader("Content-Type","application/json;charset=utf-8");
					post.setEntity(new StringEntity(message.toJson(), Charset.forName("UTF-8")));
					
					response=client.execute(post);
					HttpEntity entity=response.getEntity();
					int status=response.getStatusLine().getStatusCode();
					if(status !=HttpStatus.SC_OK)
					{
						System.out.println("failed to send result to resultReturnURL");
					}
					else
					{
						System.out.println("succeeded to sent result to resultReturnURL");
					}
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
}
