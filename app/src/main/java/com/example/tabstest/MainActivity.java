// Android DropDown Menu/Popup Menu Tutorial with Example
package com.example.tabstest;

import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tabstest.Objects.MonitorObj;
import com.example.tabstest.Objects.MonitorObject;
import com.example.tabstest.Objects.TagsObj;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements MenuItem.OnMenuItemClickListener {
    Button showMenu;
    ArrayList<MonitorObject> monitorObjectArrayList = new ArrayList<>();
    LinearLayout layout2 ;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LinearLayout layout = (LinearLayout) findViewById(R.id.linearlayout);
        layout2 = (LinearLayout) findViewById(R.id.linearlayout2);
        showMenu = (Button) findViewById(R.id.show_dropdown_menu);
        getJson();


    }
    public void getJson(){
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("your_name_input", "your_value")
                .build();

        Request request = new Request.Builder()
                .url("https://3294c784-38b0-494b-96ee-d933fa6d7808.mock.pstmn.io/config")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String yourResponse = response.body().string();
                if(response.isSuccessful()){

                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            JSONObject Jobject = null;
                            try {
                                Jobject = new JSONObject(yourResponse);
                                JSONArray MonitorTypeArray = Jobject.getJSONArray("MonitorType");
                                for(int i=0;i<MonitorTypeArray.length();i++){
                                    MonitorObject monitorObject = new MonitorObject();
                                    JSONObject jsonObject = MonitorTypeArray.getJSONObject(i);
                                    monitorObject.Id = jsonObject.getString("Id");
                                    monitorObject.Name = jsonObject.getString("Name");
                                    monitorObject.LegendId = jsonObject.getInt("LegendId");
                                    monitorObject.description = jsonObject.getString("description");
                                    monitorObjectArrayList.add(monitorObject);
                                    Arrays.sort(new ArrayList[]{monitorObjectArrayList}, (a, b) -> monitorObject.Id.compareTo(monitorObject.Id)); // Keep the array sorted

                                }
                                MonitorTypeArray = Jobject.getJSONArray("Legends");
                                MonitorObject monitorObject = new MonitorObject();
                                monitorObject.TagsArray = new ArrayList<>();
                                for(int i=0;i<MonitorTypeArray.length();i++){
                                    JSONArray tempJSONArray = MonitorTypeArray.getJSONObject(i).getJSONArray("tags");
                                    for(int k=0;k<tempJSONArray.length();k++){
                                       JSONObject tempJsonObj = tempJSONArray.getJSONObject(k);
                                        TagsObj tagsObj = new TagsObj();
                                        monitorObject = monitorObjectArrayList.get(MonitorTypeArray.getJSONObject(i).getInt("Id"));
                                        tagsObj.Color = tempJsonObj.getString("Color");
                                        tagsObj.Label = tempJsonObj.getString("Label");
                                        monitorObject.TagsArray.add(tagsObj);
                                    }



                                }
                                MonitorTypeArray = Jobject.getJSONArray("Monitor");
                                for(int i=0;i<MonitorTypeArray.length();i++){
                                    MonitorObj monitorObj = new MonitorObj();
                                    monitorObject = new MonitorObject();
                                    JSONObject jsonObject = MonitorTypeArray.getJSONObject(i);
                                    monitorObject = monitorObjectArrayList.get(jsonObject.getInt("MonitorTypeId"));
                                    monitorObj.Name = jsonObject.getString("Name");
                                    monitorObj.Desc = jsonObject.getString("Desc");
                                    monitorObj.MonitorTypeId = jsonObject.getString("MonitorTypeId");
                                    monitorObject.MonitorArray.add(monitorObj);
                                }
                            } catch (JSONException e) {
                                Log.d("Dor","Error in json "+e.toString());
                                e.printStackTrace();
                            }
                            setButtons();
                        }
                    });

                }
            }
        });

    }
    public void setButtons(){
        LinearLayout layout = (LinearLayout) findViewById(R.id.linearlayout);
        for(int i=0;i<monitorObjectArrayList.size();i++) {
            Button myButton = new Button(this);
            Button myTextView = new Button(this);
            myTextView.setText(monitorObjectArrayList.get(i).Name);
            myButton.setText(monitorObjectArrayList.get(i).Name);
            myButton.setTag(i);
            int finalI = i;
            int MonitorArraySize = monitorObjectArrayList.get(i).TagsArray.size();
            myButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    layout2.removeAllViews();
                    PopupMenu dropDownMenu = new PopupMenu(getApplicationContext(), showMenu);
                    dropDownMenu.getMenuInflater().inflate(R.menu.drop_down_menu, dropDownMenu.getMenu());
                    int MonitorArraySize = monitorObjectArrayList.get((Integer) view.getTag()).MonitorArray.size();
                    for(int j=0;j<MonitorArraySize;j++){
                        dropDownMenu.getMenu().add(finalI,j,0,monitorObjectArrayList.get(finalI).MonitorArray.get(j).Name);
                    }

                    dropDownMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            int size = monitorObjectArrayList.get(menuItem.getGroupId()).TagsArray.size();
                            for(int i = 0  ; i < size ; i++){
                                TextView myTextView = new TextView(getApplicationContext());
                                Spannable spannable = new SpannableString(monitorObjectArrayList.get(menuItem.getGroupId()).TagsArray.get(i).Label+"   ");
                                myTextView.setText(Html.fromHtml(monitorObjectArrayList.get(menuItem.getGroupId()).TagsArray.get(i).Label + "<font color="+monitorObjectArrayList.get(menuItem.getGroupId()).TagsArray.get(i).Color+">" +" [] " + "</font>"));
                                layout2.addView(myTextView);
                            }
                            return true;
                        }
                    });
                    dropDownMenu.show();
                }
            });
            layout.addView(myButton);
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        return false;
    }
}