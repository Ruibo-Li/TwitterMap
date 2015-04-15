import java.util.*;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import java.net.URI;
import org.json.*;

public class APIService {
    private static CloseableHttpClient httpClient;
    private static APIService apiService;
    //private String APIKey = "AIzaSyBvdMWKakXD-QBUN4P7c0obKSfGlQQeoxY";
    //private String APIKey = "AIzaSyB076DorHlXHT0vzTsdZ7jBivcdoBX88Gs";
    private String APIKey = "6f818436dd5b175e2eae01dc681460cce5334daf";
    private String base_URL = "http://access.alchemyapi.com/calls/text/TextGetTextSentiment";

    private APIService(String APIKey){
        this.APIKey = APIKey;
        httpClient = HttpClients.custom()
                .setConnectionManager(new PoolingHttpClientConnectionManager())
                .build();
    }
    
    public static APIService getInstanceWithKey(String APIKey){
        if(apiService == null) apiService = new APIService(APIKey);
        return apiService;
    }
    
    public JSONObject AlchemyGetSentiment (String text) throws Exception{
        HttpResponse response;
        HttpEntity responseEntity = null;
        HttpGet httpGet = new HttpGet();
        String serverOutput = null;
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("text", text));
        params.add(new BasicNameValuePair("apikey", APIKey));
        params.add(new BasicNameValuePair("outputMode", "json"));
        httpGet.setURI(URI.create(base_URL + "?" + URLEncodedUtils.format(params, "UTF-8")));
        response = httpClient.execute(httpGet);
        try {
            responseEntity = response.getEntity();
            if (responseEntity != null)
                serverOutput = EntityUtils.toString(responseEntity);
        } finally {
            EntityUtils.consume(responseEntity);
            httpGet.releaseConnection();
        }
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(serverOutput);
        } catch (Exception e){
            e.printStackTrace();
        }
        return jsonObject;
    }
    public static void main(String[] args) throws Exception{
        APIService apiService = APIService.getInstanceWithKey("6f818436dd5b175e2eae01dc681460cce5334daf");
        //JSONObject jsonObject = apiService.searchEntity("bill gates");
        //String s = jsonObject.getJSONObject("/architecture/architectural_structure_owner/structures_owned").getString("valuetype");
        String text = "I love you!";
        JSONObject jsonObject = apiService.AlchemyGetSentiment(text);
        String s = jsonObject.getJSONObject("docSentiment").getString("type");
        System.out.println(s);
    }
}