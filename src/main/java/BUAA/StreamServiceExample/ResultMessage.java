package BUAA.StreamServiceExample;

import com.google.gson.Gson;

public class ResultMessage {
	String ID;
	String time;
	String result;
	
	public ResultMessage(String ID, String time, String result)
	{
		this.ID=ID;
		this.time=time;
		this.result=result;
	}
	
	public String toJson()
	{
		Gson gson=new Gson();

		return gson.toJson(this);
	}
}
