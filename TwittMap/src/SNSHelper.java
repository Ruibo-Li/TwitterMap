//import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
//import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.ConfirmSubscriptionRequest;
import com.amazonaws.services.sns.model.ConfirmSubscriptionResult;

//import edu.columbia.cc.elPonePelis.model.SNSMessage;

public enum SNSHelper {
	INSTANCE;
	

	private AWSCredentials credentials =  new ProfileCredentialsProvider("default").getCredentials();

	//private AWSCredentials credentials = new BasicAWSCredentials("access", "secret");//insert ACCESS, //insert SECRET
	private AmazonSNSClient amazonSNSClient = new AmazonSNSClient(credentials);
	
	public void confirmTopicSubmission(SNSMessage message) {
		ConfirmSubscriptionRequest confirmSubscriptionRequest = new ConfirmSubscriptionRequest()
		 							.withTopicArn(message.getTopicArn())
									.withToken(message.getToken());
		ConfirmSubscriptionResult resutlt = amazonSNSClient.confirmSubscription(confirmSubscriptionRequest);
		System.out.println("subscribed to " + resutlt.getSubscriptionArn());
		
	}
	
}