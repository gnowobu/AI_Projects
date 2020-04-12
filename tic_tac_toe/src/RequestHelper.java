import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class RequestHelper {
    public static String url = "https://www.notexponential.com/aip2pgaming/api/index.php";
    public static String apikey = "7be903e6c97bac69ae00";
    public static String userId = "857";

    public static String sendGet(String param){
        String result = "";
        HttpURLConnection conn;
        try {
            conn = (HttpURLConnection) new URL(url+"?"+param).openConnection();
            conn.addRequestProperty("x-api-key", apikey);
            conn.addRequestProperty("userid", userId);
            conn.addRequestProperty("Content-type", "application/x-www-form-urlencoded");
            conn.connect();

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String sendPost(String param){
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        HttpURLConnection conn;
        try {
            conn = (HttpURLConnection) new URL(url+"?"+param).openConnection();
            conn.addRequestProperty("x-api-key", apikey);
            conn.addRequestProperty("userid", userId);
            conn.addRequestProperty("Content-type", "application/x-www-form-urlencoded");

            conn.setDoOutput(true);
            conn.setDoInput(true);
            out = new PrintWriter(conn.getOutputStream());
            out.print(param);
            out.flush();

            in = new BufferedReader(new InputStreamReader(conn.getInputStream(),"utf-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally{
            try{
                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
            }
            catch(IOException ex){
                ex.printStackTrace();
            }
        }

        return result;
    }

    public static void main(String[] args) throws Exception{

		String result = RequestHelper.sendGet("type=team&teamId=1228");
		JSONObject json = new JSONObject(result);

        System.out.println(json.get("code"));
        System.out.println(json.get("userIds"));

//		String result = RequestHelper.sendGet("type=boardMap&gameId=76");
//		System.out.print(result);
//		JSONObject json = new JSONObject(result);

//        String s1 = RequestHelper.sendPost("type=removeMember&teamId=1206&userId=0");
//        System.out.println(s1);
    }

}
