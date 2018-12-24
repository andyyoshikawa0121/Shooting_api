import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

public class MyAsnycTask extends AsyncTask<Void,Void,String> {
    public String accountData = "";
    private CallBackTask callbacktask;
    @Override
    protected void onPreExecute(){
        super.onPreExecute();
    }
    @Override
    protected String doInBackground(Void... params){
        try{
            this.accountData = getGET();
            System.out.println("a" + this.accountData);
        }catch(IOException e){
            System.out.println("missIo");
            e.printStackTrace();
        } catch (JSONException e) {
            System.out.println("missJson");
            e.printStackTrace();
        }
        System.out.println("b" + accountData);
        return accountData;
    }

    protected void onPostExecute(String result){
        super.onPostExecute(result);
        callbacktask.CallBack(result);
    }

    public String readInputStream(InputStream in) throws IOException, UnsupportedEncodingException {
        StringBuffer sb = new StringBuffer();
        String st = "";
        BufferedReader buffer = new BufferedReader(new InputStreamReader(in,"UTF-8"));
        while((st = buffer.readLine())!=null){
            sb.append(st);
        }
        try{
            in.close();
        }catch(Exception e){
            e.printStackTrace();
        }

        return sb.toString();
    }

    public String getGET() throws IOException, JSONException {
        final String json = "{\"user\":{" +
                "\"name\":\"masa\" " +
                "}}";

        String buffer = "";
        HttpURLConnection con = null;
        URL url = new URL("http://localhost:3000/users/create");
        con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setInstanceFollowRedirects(false);
        con.setRequestProperty("Accept-Language", "jp");
        con.setDoOutput(true);
        con.setDoInput(true);
        con.setRequestProperty("Content-Type", "application/json; charset=utf-8");
        OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream());
        out.write(json);
        out.flush();
        con.connect();
        InputStream in = con.getInputStream();
        String readSt = readInputStream(in);
        return readSt;

    }
    public static class CallBackTask{
        public void CallBack(String result){

        }
    }

    public void setOnCallBack(CallBackTask _cbj) {
        callbacktask = _cbj;
    }



}
