import twitter4j.FilterQuery;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterException;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;

import org.json.*;

/**
 * <p>This is a code example of Twitter4J Streaming API - sample method support.<br>
 * Usage: java twitter4j.examples.PrintSampleStream<br>
 * </p>
 *
 * @author Yusuke Yamamoto - yusuke at mac.com
 */
public class TweetGet {
    /**
     * Main entry of this application.
     *
     * @param args
     */
	public static SQLManager sqlmng = null;
	public static SQSManager sqsmng = null;
	
	
    public static void main(String[] args) throws TwitterException {
    	//just fill this
    	 ConfigurationBuilder cb = new ConfigurationBuilder();
         cb.setDebugEnabled(true)
           .setOAuthConsumerKey("")
           .setOAuthConsumerSecret("")
           .setOAuthAccessToken("")
           .setOAuthAccessTokenSecret("");
         
         sqlmng = new SQLManager();
         sqsmng = new SQSManager();
         sqsmng.createQueue();
         TwitterStream twitterStream = new TwitterStreamFactory(cb.build()).getInstance();
         
         
         StatusListener listener = new StatusListener() {
        	long usr_id=0;
        	@Override
            
            public void onStatus(Status status) {
            	
            	sqlmng.write(status);
            	String kw = sqlmng.getKw(status);
            	double lon = status.getGeoLocation().getLongitude();
            	double lat = status.getGeoLocation().getLatitude();
            	JSONObject jo = new JSONObject();
            	try{
                	jo.put("lon", lon);
                	jo.put("lat", lat);
                	jo.put("kw", kw);
                	jo.put("text", status.getText());
            	}catch (Exception e){
            		e.printStackTrace();
            	}

            	String message = jo.toString();
            	sqsmng.sendMessage(message);
            	System.out.println("usrID:"+usr_id);
            	usr_id++;


                //System.out.println(usr_id+" @" + status.getUser().getScreenName() + " - " + status.getText());
            }

            @Override
            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
                //System.out.println("Got a status deletion notice id:" + statusDeletionNotice.getStatusId());
            }
            @Override
            public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
                //System.out.println("Got track limitation notice:" + numberOfLimitedStatuses);
            }

            @Override
            public void onScrubGeo(long userId, long upToStatusId) {
                //System.out.println("Got scrub_geo event userId:" + userId + " upToStatusId:" + upToStatusId);
            }

            @Override
            public void onStallWarning(StallWarning warning) {
                //System.out.println("Got stall warning:" + warning);
            }

            @Override
            public void onException(Exception ex) {
                //ex.printStackTrace();
            }
        };
        sqlmng.connect();
        twitterStream.addListener(listener);
       
        // Filter
        FilterQuery filter = new FilterQuery();
        String[] keywordsArray = sqlmng.getKeys();
        filter.track(keywordsArray);
        twitterStream.filter(filter);
    }
}
