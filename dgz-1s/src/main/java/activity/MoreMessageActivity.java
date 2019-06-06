package activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.user.dm_3.R;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import controller.BaseActivity;

/**
 * Created by Administrator on 2016/12/15 0015.
 */
public class MoreMessageActivity extends BaseActivity {
    private ListView listView;
    private ImageView imageBack;
    private ArrayList<String> datalist = new ArrayList<>();
    private ArrayList<String> infolist = new ArrayList<>();
    private String strExt = null;
    private ArrayList<String> list = new ArrayList<>();
    private float sum = 0;
    private float average = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//强制为竖屏
        setContentView(R.layout.data_more);
        listView = getView(R.id.listmessage);
        imageBack = getView(R.id.back);

        Intent intent = getIntent();
        infolist = intent.getStringArrayListExtra("infolist");
        datalist = intent.getStringArrayListExtra("datalist");
        if (datalist != null && datalist.size() > 0) {

            for (int i = 0; i < datalist.size(); i++) {
                sum = sum + Float.parseFloat(datalist.get(i));
            }
            average = sum / datalist.size();
        }

        if (infolist != null && infolist.size() > 0) {

            list.add("电梯编号：  " + infolist.get(1));
            list.add("测试人员：  " + infolist.get(2));
            list.add("测试地点：  " + infolist.get(3));
            list.add("测试单位：  " + infolist.get(4));
            list.add("补充信息：  " + infolist.get(5));

        }
        if (datalist != null && datalist.size() > 0) {

            for (int i = 0; i < datalist.size(); i++) {
                float dataValue = Float.parseFloat(datalist.get(i));
                float biasValue = (dataValue - average) * 100 / average;//偏差量值
                //保留两位小数
                BigDecimal b = new BigDecimal(biasValue);

                float biasValueSet = b.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
                list.add("第" + (i + 1) + "根测试值： " + dataValue + "  偏差量：" + biasValueSet + "%");
            }

        }
        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MoreMessageActivity.this.finish();
            }
        });

       // ArrayAdapter<String> adapter = new ArrayAdapter<String>(MoreMessageActivity.this, android.R.layout.simple_list_item_1, list);
        ViewHolderAdapter adapter = new ViewHolderAdapter(list,this);
        listView.setAdapter(adapter);

    }

    public class ViewHolderAdapter extends BaseAdapter {
        private List<String> mData;
        private LayoutInflater mInflater;

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public Object getItem(int position) {
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.viewholder_item, null);
                holder.title = (TextView) convertView.findViewById(R.id.textView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.title.setText(mData.get(position));
            holder.title.setTextColor(Color.BLACK);
            Log.i("ViewHolderAdapter","positon = "+position);
            if (position > 4) {
                float dataValue = Float.parseFloat(datalist.get(position-5));
                float biasValue = (dataValue - average) * 100 / average;//偏差量值
                Log.i("ViewHolderAdapter","biasValue = "+biasValue);
                if (Math.abs(biasValue) > 5) {
                    holder.title.setTextColor(Color.RED);

                } else {
                    holder.title.setTextColor(Color.BLACK);
                }

            }
            return convertView;
        }

        public ViewHolderAdapter(List<String> mData, Context context) {
            this.mData = mData;
            mInflater = LayoutInflater.from(context);
        }

        public final class ViewHolder {
            public ImageView img;
            public TextView title;
        }

    }
}
