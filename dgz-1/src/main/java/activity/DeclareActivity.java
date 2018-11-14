package activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.dm_3.R;

import controller.BaseActivity;
import utils.BluetoothState;
import utils.MyService;

/**
 * Created by Administrator on 16-10-26.
 */
public class DeclareActivity extends BaseActivity {
    private Button btn_decForce,btn_decDis,btn_backMain,btn_reset,btn_150N,btn_300N;
    private TextView tv_tishi;
    private MyService.DiscoveryBinder mBinder;

    private int count = 0;
    //标定零点的处理
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            String message = (String) msg.obj;
            if(message.equals("E1")) {
                btn_decDis.setTextColor(Color.BLACK);
                count++;
                if (count < 3){
                    Toast.makeText(DeclareActivity.this, "第"+count+"次标定已经完成！", Toast.LENGTH_SHORT).show();
                 }else{
                    Toast.makeText(DeclareActivity.this, "三次标定已经完成！", Toast.LENGTH_SHORT).show();
                    count = 0;
                }

                }
        }
    };
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
    private Intent bindIntent;
    private MyReceiver receiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//强制为竖屏
        setContentView(R.layout.activity_declare);
        bindIntent = new Intent(this, MyService.class);
        bindService(bindIntent, connection, BIND_AUTO_CREATE);

        receiver = new MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.ceshiActivity");
        DeclareActivity.this.registerReceiver(receiver, filter);

        btn_decForce=getView(R.id.btn_decDisZero);
        btn_decDis=getView(R.id.btn_decZero);
        btn_backMain=getView(R.id.btn_backMain);
        btn_reset = getView(R.id.btn_reset);
        btn_decForce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DeclareActivity.this, DecForceActivity.class));
            }
        });

        btn_decDis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(DeclareActivity.this, DecDisActivity.class));
                mBinder.sendMessage("E1", BluetoothState.CESHIACTIVITY);
                btn_decDis.setTextColor(Color.RED);
            }
        });
        btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count = 0;
                btn_decDis.setTextColor(Color.BLACK);
                Toast.makeText(DeclareActivity.this, "重置成功！", Toast.LENGTH_SHORT).show();

            }
        });
        btn_backMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        unbindService(connection);
    }
    public class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            if (intent.getAction().equals("android.intent.action.ceshiActivity")) {
                Bundle bundle = intent.getExtras();
                String message1 = bundle.getString("msg");
                Message message = new Message();
                message.what = 0;
                message.obj = message1;
                handler.sendMessage(message);
            }
        }
    }
}
