package activity;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.user.dm_3.R;


import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import controller.BaseActivity;
import controller.MyApplication;
import controller.PictureDatabase;
import utils.Calculate;
import view.MySeverityGaiView;

/**
 * Created by user on 2018/9/12.
 */
public class TestActivity extends BaseActivity {
    private LinearLayout mLayout1,mLayout2,mLayout3;
    private Button btn_reset,btn_start,btn_look,btn_sure;
    private EditText ed_setNum,ed_testValue;
    private TextView tv_curNum,tv_curState;
    private ImageView backImage;
    private int totalCount =0 ,curCount = 1;
    ArrayList<String> mArrayList = new ArrayList<>();
    PictureDatabase pictureDB;
    SQLiteDatabase db;
    StringBuilder dataString = new StringBuilder();
    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int value = msg.arg1;
            ed_testValue.setText(value + "");
            mArrayList.add(value + "");
            btn_start.setEnabled(true);

            tv_curState.setText("测试完成");
            if (curCount == totalCount){
                tv_curNum.setText("第" + curCount + "根测试完成," + "点击查看详细数据");
                dataString.append(value+"");
                onSave();
                btn_look.setVisibility(View.VISIBLE);
                btn_start.setEnabled(false);

            } else{
                tv_curNum.setText("第" + curCount + "根测试完成," + "点击测试第" + (curCount + 1) + "根");
                dataString.append(value+",");
                curCount++;
            }

        }
    };
    private int indexDGZ =1;
    private String filename;
    private Calculate calculate = new Calculate();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_test);

        pictureDB=new PictureDatabase(this);
        db=pictureDB.getWritableDatabase();

        mLayout1 = getView(R.id.linear);
        mLayout2 = getView(R.id.linear2);
        mLayout3 = getView(R.id.linear3);

        btn_reset = getView(R.id.btn_reset);
        btn_start = getView(R.id.startTest);
        btn_look = getView(R.id.lookData);
        btn_sure = getView(R.id.btn_sure);

        ed_setNum = getView(R.id.inputNum);
        ed_testValue = getView(R.id.testValue);

        tv_curNum = getView(R.id.curNum);
        tv_curState = getView(R.id.curTishi);

        backImage = getView(R.id.back);

        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TestActivity.this.finish();
            }
        });

        //确认按钮
        btn_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                InputMethodManager imm = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                //判断有无输入根数
                if (ed_setNum.getText().toString().length() != 0) {
                    totalCount = Integer.parseInt(ed_setNum.getText().toString());
                    ed_setNum.setEnabled(false);
                    btn_sure.setEnabled(false);
                    mLayout2.setVisibility(View.VISIBLE);
                    mLayout3.setVisibility(View.VISIBLE);
                    tv_curNum.setText("一共" + totalCount + "根, 当前为第" + curCount + "根");
                }else{
                    Toast.makeText(TestActivity.this,"请输入钢丝绳根数",Toast.LENGTH_SHORT).show();
                }


            }
        });

        //开始测试按钮
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mLayout2.setVisibility(View.VISIBLE);
                    tv_curState.setText("正在测试中....");
                tv_curNum.setText("一共" + totalCount + "根, 正在测试第" + curCount + "根");
                    btn_start.setEnabled(false);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1500);
                            Message msg = new Message();
                            msg.what = 0;
                            msg.arg1 = 200+curCount;
                            mHandler.sendMessage(msg);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();



            }
        });

        //复位按钮
        btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mLayout2.setVisibility(View.GONE);
                mLayout3.setVisibility(View.GONE);
                btn_look.setVisibility(View.GONE);
                curCount =1;
                totalCount = 0;
                ed_setNum.setEnabled(true);
                ed_setNum.setText("");
                btn_sure.setEnabled(true);
                btn_start.setEnabled(true);
                mArrayList = new ArrayList<String>();

            }
        });

        btn_look.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TestActivity.this,AnotherBarActivity.class);
                intent.putStringArrayListExtra("list",mArrayList);
                startActivity(intent);
            }
        });

    }

    //将得到的数据存储
    private void onSave() {
        //获取sd卡目录
        String sdpath = Environment.getExternalStorageDirectory().getAbsolutePath();
        String appName = getString(R.string.app_name);

        String fileDir = sdpath + "/" + appName + "/data";
        File newfileDir = new File(fileDir);
        if (!newfileDir.exists()) {
            boolean isSuccess = newfileDir.mkdirs();
            System.out.println("isSuccess:" + isSuccess);
        }
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String dateStr = formatter.format(curDate);
        String name = dateStr + "-" + String.valueOf(indexDGZ) + ".dgz";

        //保存图片到数据库

        pictureDB.initDataBase(db,  MyApplication.DGZFORCE, name, MainActivity.s_mLiftId, MainActivity.s_mOperator, MainActivity.s_mLocation,dataString.toString());
        // pointsF.clear();

        indexDGZ++;
        filename = fileDir + "/" + name;
        File newfile = new File(filename);
        try {
            newfile.createNewFile();
            calculate.writeSetingsToFile(newfile, dataString.toString());
            dataString.setLength(0);
        } catch (IOException e) {
            e.printStackTrace();
            dataString.setLength(0);
        }

    }

}
