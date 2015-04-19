import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.model.CreateTopicRequest;
import com.amazonaws.services.sns.model.CreateTopicResult;
import com.amazonaws.services.sns.model.SubscribeRequest;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
//import com.amazonaws.services.sns.model.DeleteTopicRequest;

public class WorkerSNS {
	
	AmazonSNSClient snsClient = null;		                           
	String topicArn = null;
	
	public void createTopic(){
		snsClient = new AmazonSNSClient(new ClasspathPropertiesFileCredentialsProvider());
		snsClient.setRegion(Region.getRegion(Regions.US_EAST_1));
		CreateTopicRequest createTopicRequest = new CreateTopicRequest("TweetInfo");
		CreateTopicResult createTopicResult = snsClient.createTopic(createTopicRequest);
		//print TopicArn
		System.out.println(createTopicResult);
		//get request id for CreateTopicRequest from SNS metadata		
		System.out.println("CreateTopicRequest - " + snsClient.getCachedResponseMetadata(createTopicRequest));
		topicArn = createTopicResult.getTopicArn();
	}
	
	public void subscribeTopic(){
		SubscribeRequest subRequest = new SubscribeRequest(topicArn, "http", "http://twittmapenv-p2f2puxfqf.elasticbeanstalk.com");
		snsClient.subscribe(subRequest);
		//get request id for SubscribeRequest from SNS metadata
		System.out.println("SubscribeRequest - " + snsClient.getCachedResponseMetadata(subRequest));
		//System.out.println("Check your email and confirm subscription.");
	}
	
	public void pushNotification(String msg){
		//String msg = "My text published to SNS topic with email endpoint";
		PublishRequest publishRequest = new PublishRequest(topicArn, msg);
		PublishResult publishResult = snsClient.publish(publishRequest);
		//print MessageId of message published to SNS topic
		System.out.println("MessageId - " + publishResult.getMessageId());
	}
}
