import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import twitter4j.Status;


public class SQLManager {

	Connection conn=null;
	private ArrayList<String> res=new ArrayList<String>();
    private String[] sports={"sports","Soccer","Football","Basketball","Tennis","Volleyball","Baseball","Skating"};
    private String[] IT={"technology","tech","Internet","Cisco","AT&T","AWS","Google","iPhone","iPad","Samsung"};
    private String[] media={"media","CNN","ABC","BBC","NY Times","Time Warner","NBC","Netflix","CBS"};
    

	public void connect(){
		try {
	    	try {
				Class.forName("com.mysql.jdbc.Driver").newInstance();
			} catch (InstantiationException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			} catch (IllegalAccessException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			} catch (ClassNotFoundException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}

			conn=DriverManager.getConnection("jdbc:mysql://aaihx912uftaou.cynqzrqdfvg7.us-east-1.rds.amazonaws.com:3306/tweetmap?" +
			        "user=housy&password=housy3911203-");
			//conn=DriverManager.getConnection("jdbc:mysql://localhost/tweetmap?" +
			//        "user=root&password=");
			System.out.println("Connect!");
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	public String[] getKeys(){
		return concatAll();
	}
	public String[] concatAll(){
		for(int i=0; i<sports.length; i++){
			res.add(sports[i]);
		}
		for(int i=0; i<IT.length; i++){
			res.add(IT[i]);
		}
		for(int i=0; i<media.length; i++){
			res.add(media[i]);
		}
		String[] arr=new String[res.size()];
		for(int i=0; i<arr.length; i++){
			arr[i]=res.get(i);
		}
		return arr;
	}
	public void write(Status status){
	    PreparedStatement ps=null;

		java.sql.Timestamp date=new java.sql.Timestamp(status.getCreatedAt().getTime()+4*60*60*1000);
		String kw=null;
		for (String str : sports){
			if(status.getText().toLowerCase().contains(str.toLowerCase())){
				kw="sports";
				break;
			}
		}
		for (String str : IT){
			if(status.getText().toLowerCase().contains(str.toLowerCase())){
				kw="IT";
				break;
			}
		}
		for (String str : media){
			if(status.getText().toLowerCase().contains(str.toLowerCase())){
				kw="media";
				break;
			}
		}
		
		String sql="insert into tweetinfo values (?,?,?,?,?,?,?)";
		try{
			ps=conn.prepareStatement(sql);
			//ps.setLong(1, usr_id);
			ps.setLong(1, status.getId());
			ps.setString(2, status.getUser().getScreenName());
			ps.setString(3, kw);
			ps.setString(4, status.getText());
			ps.setTimestamp(5, date);
			ps.setDouble(6, status.getGeoLocation().getLongitude());
			ps.setDouble(7, status.getGeoLocation().getLatitude());
			ps.executeUpdate();
		}catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	public ArrayList<TweetData> getData(int interval){
		System.out.println("test!");
		PreparedStatement ps=null;
		TimeZone zone = TimeZone.getTimeZone("UTC");
		Calendar curTime=Calendar.getInstance(zone);
		curTime.add(Calendar.SECOND, -interval);
		java.sql.Timestamp cur = new java.sql.Timestamp(curTime.getTimeInMillis());
		ResultSet rs=null;
		ArrayList<TweetData> geoData=new ArrayList<TweetData>();
		String query="select lat,lon, key_word from tweetinfo where time > ? and key_word IS NOT NULL";
		try {
			ps=conn.prepareStatement(query);
			ps.setTimestamp(1, cur);
			System.out.println(cur);
			rs=ps.executeQuery();
			while(rs.next()){
				//System.out.println("IN");
				Double lat=rs.getDouble(1);
				Double lon=rs.getDouble(2);
				String kw=rs.getString(3);
				TweetData data=new TweetData(lat, lon, kw);
				//System.out.println("Key:"+kw);
				geoData.add(data);
			}
			//System.out.println("Test!!!");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return geoData;
	}
		
	public void close(){
		try {
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
