import java.util.ArrayList;


public class Test {
	public static SQLManager mng;
	public static ArrayList<TweetData> test;
	public static void main(String[] args){
		mng=new SQLManager();
		mng.connect();
		test = mng.getData(100000);
		for(int i=0; i<test.size(); i++){
			System.out.println("Get key word: "+test.get(i).kw);
		}
		mng.close();
	}
}
