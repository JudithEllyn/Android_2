package hk.edu.cuhk.ie.iems5722.a2_1155162647;

import static java.lang.System.out;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toolbar;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.*;

import hk.edu.cuhk.ie.iems5722.a2_1155162647.R;


public class MainActivity extends AppCompatActivity {

    private ListView chatrlistview;
    private ArrayList<ChatroomEntity>myChatRooms = new ArrayList<>();
    private ArrayAdapter myChatRoomsAdapter;
    private String ApiUrl = "http://18.217.125.61/api/a3/get_chatrooms";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //设置ListView显示
        this.chatrlistview = (ListView)findViewById(R.id.chatroomlv);
        myChatRoomsAdapter = new ArrayAdapter(this,R.layout.item_chatroom,myChatRooms);
        this.chatrlistview.setAdapter(myChatRoomsAdapter);
        new GetChatroom().execute(ApiUrl);

        //点击每个ChatRoom事件
        this.chatrlistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ChatroomEntity room = (ChatroomEntity)chatrlistview.getItemAtPosition(i);
                int rmid = room.getId();
                String rmname = room.getName();
                Intent intent = new Intent(MainActivity.this,ChatActivity.class);
                intent.putExtra("id",rmid);
                intent.putExtra("name",rmname);
                startActivity(intent);
            }
        });

    }

    private class GetChatroom extends AsyncTask<String, Void, String> {

        InputStream is = null;
        private Exception exception;

        @Override
        protected String doInBackground(String... urls) {

            String results = "";
            try {
//                out.println(urls[0]);
                URL url = new URL(urls[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);

                //进行连接
                conn.connect();
                int response = conn.getResponseCode();
                if(response!=200){
                    out.println("Error");
                }
//                out.println(response);
                is = conn.getInputStream();

                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                while ((line=br.readLine()) != null) {
                    results += line;
                }

            } catch (Exception e) {
                this.exception = e;
                return null;
            }
//            out.println(results);
            return results;
        }


        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                try {
                    JSONObject json = new JSONObject(result);
                    String status = json.getString("status");
                    if (!status.equals("OK")) {

                    } else {
                        JSONArray array = json.getJSONArray("data");
                        for (int i = 0; i < array.length(); i++) {
                            int id = array.getJSONObject(i).getInt("id");
                            String name = array.getJSONObject(i).getString("name");
//                            out.println(id);
//                            out.println(name);
                            ChatroomEntity chatroom = new ChatroomEntity(id,name);
//                            out.println(chatroom);
                            myChatRooms.add(chatroom);
                            myChatRoomsAdapter.notifyDataSetChanged();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}






