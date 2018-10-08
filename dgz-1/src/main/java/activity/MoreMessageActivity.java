package activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.user.dm_3.R;

import java.util.ArrayList;

import controller.BaseActivity;

/**
 * Created by Administrator on 2016/12/15 0015.
 */
public class MoreMessageActivity extends BaseActivity {
    private ListView listView;
    private ImageView imageBack;
    private ArrayList<String> datalist=new ArrayList<>();
    private ArrayList<String> infolist=new ArrayList<>();
    private String strExt=null;
    private  ArrayList<String> list=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//强制为竖屏
        setContentView(R.layout.data_more);
        listView=getView(R.id.listmessage);
        imageBack=getView(R.id.back);

        Intent intent=getIntent();
        infolist=intent.getStringArrayListExtra("infolist");
        datalist=intent.getStringArrayListExtra("datalist");
        if(infolist != null && infolist.size()>0) {

                list.add("设备编号：  " + infolist.get(1));
                list.add(" 操作员：   " + infolist.get(2));
                list.add(" 地点：     " + infolist.get(3));

        }
        if(datalist != null && datalist.size()>0) {

         for (int i =0 ;i<datalist.size();i++){
             list.add("第"+(i+1)+"根张紧力： "+datalist.get(i)+" N");
         }

        }
        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MoreMessageActivity.this.finish();
            }
        });

        ArrayAdapter<String> adapter=new ArrayAdapter<String>(MoreMessageActivity.this,android.R.layout.simple_list_item_1,list);
        listView.setAdapter(adapter);

    }
}
