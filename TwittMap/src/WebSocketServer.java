import java.io.IOException;
//import java.nio.ByteBuffer;
import java.util.ArrayList;

import javax.servlet.http.HttpServlet;
//import javax.websocket.EncodeException;
import javax.websocket.OnMessage;
//import javax.websocket.OnOpen;
import javax.websocket.Session;
//import javax.websocket.server.ServerEndpoint;

import com.google.gson.*;

//@ServerEndpoint("/map")
public class WebSocketServer extends HttpServlet{
	public static SQLManager sqlmng;
	//public static SQLManager test;
	/*
	@OnOpen
	public void test(){
		System.out.println("Open Success!");
		test=new SQLManager();
		test.connect();
		System.out.println("test successfully!");
		test.close();
	}	*/
	
    @OnMessage
    public void echoTextMessage(Session session, int interval) {

    	sqlmng=new SQLManager();
    	sqlmng.connect();
    	ArrayList<TweetData> data=new ArrayList<TweetData>();
        data=sqlmng.getData(interval);
    	
        try {
        	//System.out.println("Interval: "+interval);
            if (session.isOpen()) {
            	System.out.println(data.size());
				for (int i=0; i<data.size(); i++){
					Gson gson=new Gson();
					String mes=gson.toJson(data.get(i));
					session.getBasicRemote().sendText(mes);
					System.out.print(true);
				}
            }
        }catch (IOException e) {
        	e.printStackTrace();
        	/*
            try {
                //session.close();
            } catch (IOException e1) {
                // Ignore
            }*/
        }finally{
        	sqlmng.close();
        }
    }
}