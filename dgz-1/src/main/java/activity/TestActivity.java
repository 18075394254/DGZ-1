package activity;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
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
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import controller.BaseActivity;
import controller.MyApplication;
import controller.PictureDatabase;
import utils.BluetoothState;
import utils.Calculate;
import utils.DataTrans;
import utils.MyService;

/**
 * Created by user on 2018/9/12.
 */
public class TestActivity extends BaseActivity {
    private LinearLayout mLayout1,mLayout2,mLayout3;
    private Button btn_reset,btn_start,btn_stop,btn_look,btn_sure;
    private EditText ed_setNum,ed_setCount,ed_testValue;
    private TextView tv_curNum,tv_curState,tv_curTest;
    private ImageView backImage;
                  //总根数        当前根数       测试总次数      当前测试次数
    private int totalCount =0 ,curCount = 1, testCount = 1,curTestCount = 1;
    ArrayList<String> mArrayList = new ArrayList<>();
    PictureDatabase pictureDB;
    SQLiteDatabase db;
    StringBuilder dataString = new StringBuilder();
    private boolean isConnect=true;
    private boolean isStart=false;
    private boolean cantest=true;
    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String message = (String) msg.obj;
            if (message.equals("OK")) {

            } else {
                float value = Float.parseFloat((String)msg.obj)/10;
                //保留一位小数
                BigDecimal b = new BigDecimal(value);

                 value = b.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
                ed_testValue.setText(value + "");
                mArrayList.add(value + "");
                btn_start.setEnabled(true);
                if (curCount == totalCount) {
                    if (testCount > 1) {

                        tv_curState.setText("测试完成");
                        if (curTestCount == testCount) {
                            tv_curNum.setText("第" + curTestCount + "次测试完成," + "点击查看详细数据");
                            dataString.append(value + ",");
                            curCount++;
                            curTestCount = 1;
                            onSave();
                            btn_look.setVisibility(View.VISIBLE);
                            btn_start.setEnabled(false);
                        } else {
                            tv_curNum.setText("第" + curTestCount + "次测试完成," + "点击测试第" + (curTestCount + 1) + "次");
                            dataString.append(value + ",");
                            //  curCount++;
                            curTestCount++;
                        }

                   /* tv_curNum.setText("第" + curCount + "根第" + curTestCount + "次测试完成," + "点击查看详细数据");
                    dataString.append(value + "");*/

                    } else {
                        tv_curState.setText("测试完成");
                        tv_curNum.setText("第" + curCount + "根测试完成," + "点击查看详细数据");
                        dataString.append(value + "");
                        onSave();
                        btn_look.setVisibility(View.VISIBLE);
                        btn_start.setEnabled(false);
                    }


                } else {

                    if (testCount > 1) {
                        tv_curState.setText("测试完成");
                        if (curTestCount == testCount) {
                            tv_curNum.setText("第" + curTestCount + "次测试完成," + "点击测试第" + (curCount + 1) + "根");
                            dataString.append(value + ",");
                            curCount++;
                            curTestCount = 1;
                        } else {
                            tv_curNum.setText("第" + curTestCount + "次测试完成," + "点击测试第" + (curTestCount + 1) + "次");
                            dataString.append(value + ",");
                            //  curCount++;
                            curTestCount++;
                        }


                    } else if (testCount == 1) {

                        tv_curState.setText("测试完成");
                        if (curCount == totalCount) {
                            tv_curNum.setText("第" + curCount + "根测试完成," + "点击查看详细数据");
                            dataString.append(value + "");
                            onSave();
                            btn_look.setVisibility(View.VISIBLE);
                            btn_start.setEnabled(false);

                        } else {
                            tv_curNum.setText("第" + curCount + "根测试完成," + "点击测试第" + (curCount + 1) + "根");
                            dataString.append(value + ",");
                            curCount++;
                        }

                    }
                }
            }
        }
    };
    private MyService.DiscoveryBinder mBinder;


    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //获取Service端的Messenger
            mBinder =(MyService.DiscoveryBinder)service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }


    };
    private int indexDGZ =1;
    private String filename;
    private Calculate calculate = new Calculate();
    private Intent bindIntent;
    private MyReceiver receiver;
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
        btn_stop = getView(R.id.stopTest);

        ed_setNum = getView(R.id.inputNum);
        ed_setCount = getView(R.id.inputCount);
        ed_testValue = getView(R.id.testValue);

        tv_curNum = getView(R.id.curNum);
        tv_curState = getView(R.id.curTishi);
        tv_curTest = getView(R.id.curTest);

        backImage = getView(R.id.back);
        bindIntent = new Intent(this, MyService.class);
        bindService(bindIntent, connection, BIND_AUTO_CREATE);

        receiver = new MyReceiver();
        IntentFilter filter=new IntentFilter();
        filter.addAction("android.intent.action.ontestActivity");
        filter.addAction("android.intent.action.connect");
        filter.addAction("android.intent.action.disconnect");
        filter.addAction("android.intent.action.connectfailed");
        TestActivity.this.registerReceiver(receiver, filter);

        IntentFilter filter1=new IntentFilter();
        filter1.addAction("android.bluetooth.device.action.ACL_CONNECTED");
        filter1.addAction("android.bluetooth.device.action.ACL_DISCONNECTED");
        TestActivity.this.registerReceiver(mReceiver, filter1);

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

                if (isConnect) {

                        //判断有无输入根数
                        if (ed_setNum.getText().toString().length() != 0 && ed_setNum.getText().toString().length() != 0) {
                            totalCount = Integer.parseInt(ed_setNum.getText().toString());
                            testCount = Integer.parseInt(ed_setCount.getText().toString());
                            ed_setNum.setEnabled(false);
                            ed_setCount.setEnabled(false);
                            btn_sure.setEnabled(false);
                            mLayout2.setVisibility(View.VISIBLE);
                            mLayout3.setVisibility(View.VISIBLE);
                            tv_curTest.setText("一共" + totalCount + "根, 当前为第" + curCount + "根");
                            //mBinder.sendbytes(DataTrans.sendSetDataBytes(MainActivity.s_mLiftId, MainActivity.s_mOperator, MainActivity.s_mLocation, totalCount, testCount), BluetoothState.ONTESTACTIVITY);
                        } else {
                            Toast.makeText(TestActivity.this, "请输入设置参数", Toast.LENGTH_SHORT).show();
                        }

                }else {
                    Toast.makeText(TestActivity.this, "未连接蓝牙设备", Toast.LENGTH_SHORT).show();
                }

            }
        });

        //开始测试按钮
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isConnect) {
                    if (cantest) {
                        isStart = true;
                        mBinder.sendMessage("A1", BluetoothState.ONTESTACTIVITY);
                        mLayout2.setVisibility(View.VISIBLE);
                        tv_curState.setText("正在测试中....");
                        if (curTestCount == testCount){
                        tv_curTest.setText("一共" + totalCount + "根, 当前为第"+curCount+"根");
                        tv_curNum.setText("当前为第"+curTestCount+"次测试");
                    }else{
                        tv_curTest.setText("一共" + totalCount + "根, 当前为第"+curCount+"根");
                        tv_curNum.setText("当前为第"+curTestCount+"次测试");
                     }

                        btn_start.setEnabled(false);
                   }

                }else {
                    Toast.makeText(TestActivity.this, "未连接蓝牙设备", Toast.LENGTH_SHORT).show();
                }

            }
        });

        btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isStart) {
                    mBinder.sendMessage("B1", BluetoothState.ONTESTACTIVITY);
                    btn_start.setTextColor(Color.BLACK);
                    btn_stop.setTextColor(Color.RED);
                    Log.i("mtag", "发送B1的时间 " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis())));
                    isStart=false;
                }else{
                    Toast.makeText(TestActivity.this, "还未开始测试", Toast.LENGTH_SHORT).show();

                }
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
                curTestCount = 1;
                ed_setNum.setEnabled(true);
                ed_setCount.setEnabled(true);
                ed_setNum.setText("");
                ed_setCount.setText("1");

                ed_testValue.setText("");
                btn_sure.setEnabled(true);
                btn_start.setEnabled(true);

                dataString.setLength(0);
                mArrayList = new ArrayList<String>();

            }
        });

        btn_look.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    Intent intent = new Intent(TestActivity.this, AnotherBarActivity.class);
                    intent.putStringArrayListExtra("list", mArrayList);
                    startActivity(intent);

            }
        });

    }

    private ArrayList<String> aveArrayList(ArrayList<String> arrayList, int testCount) {
        ArrayList<String> list = new ArrayList<>();
        float value = 0;
        dataString.setLength(0);
        for (int i = 0; i < arrayList.size(); i += testCount){
            for (int j = i ;j < testCount+i;j++){
                Log.i("mtag","数值 = "+arrayList.get(j));
                value = value + Float.parseFloat(arrayList.get(j));
                Log.i("mtag","value = "+value);
            }
            value = value/testCount;
            //保留一位小数
            BigDecimal b = new BigDecimal(value);

            value = b.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
            list.add(value+"");
            dataString.append(value + ",");
            value = 0;
        }

        return list;
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

        //当测试次数大于1时，调整数组
        if (testCount > 1){
            mArrayList = aveArrayList(mArrayList,testCount);
        }
        //保存数据到数据库

        pictureDB.initDataBase(db,MyApplication.DGZFORCE, name, MainActivity.s_mLiftId, MainActivity.s_mOperator, MainActivity.s_mLocation,dataString.toString());
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
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive (Context context, Intent intent) {
            String action = intent.getAction();

            if (action.equals(BluetoothDevice.ACTION_ACL_CONNECTED)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.d("aaa", device.getName() + " ACTION_ACL_CONNECTED");
            } else if (action.equals(BluetoothDevice.ACTION_ACL_DISCONNECTED)) {
                Log.d("aaa", " ACTION_ACL_DISCONNECTED");
                //String message1="蓝牙断开连接";
                //handler.obtainMessage(2, 1, -1, message1).sendToTarget();
                TestActivity.this.finish();
            }
        }

    };

    public class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            if (intent.getAction().equals("android.intent.action.ontestActivity")) {
                Bundle bundle = intent.getExtras();
                String message = bundle.getString("msg");

                mHandler.obtainMessage(0, 1, -1, message).sendToTarget();
            }else if(intent.getAction().equals("android.intent.action.connect")){
                Bundle bundle = intent.getExtras();
                String message=bundle.getString("msg");
                mHandler.obtainMessage(1, 1, -1, message).sendToTarget();
            }else if(intent.getAction().equals("android.intent.action.disconnect")){

            }else if(intent.getAction().equals("android.intent.action.connectfailed")){
                Bundle bundle = intent.getExtras();
                String message=bundle.getString("msg");
                mHandler.obtainMessage(3, 1, -1, message).sendToTarget();
            }
        }
    }

}
