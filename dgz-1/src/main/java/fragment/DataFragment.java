package fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.dm_3.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import activity.MoreMessageActivity;
import controller.MyApplication;
import controller.PictureDatabase;
import utils.Calculate;

/**
 * Created by Administrator on 2017/1/20 0020.
 */
public class DataFragment extends Fragment {
    private String name;
    private ImageView imageView,backImage,shareImage;
    private TextView textForce,textDis,titleText;
    private Button moveToPrevious,moveToNext,moreMessage;
    private PictureDatabase pictureDB;
    private SQLiteDatabase db;
    ArrayList<String> datalist=new ArrayList<>();
    ArrayList<String> infolist=new ArrayList<>();
    private View view;
    private onChangeListener mCallback;
    private int position;
    private Bitmap bitmap;
    private Activity activity;
    private String dataList= "";
    private String s_mLiftId ="";
    private String s_mOperator = "";
    private String s_mLocation = "";
    Calculate calculate =new Calculate();
    private BarChart mBarChart;
    float sum = 0;
    float average;
    float upAve = 0;
    float downAve = 0;
    @Override

    public void onAttach(Activity activity)

    {

        super.onAttach(activity);

        try {

            mCallback = (onChangeListener) activity;

        } catch (ClassCastException e)

        {
            throw new ClassCastException(activity.toString() +" must implement OnHeadlineSelectedListener");

        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i("Fragment", "Fragment被关闭了！");

    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
       /* outState.putString("data", mData + "_save");*/

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        datalist.clear();
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle args=getArguments();
        name = args.getString("name");
        position=args.getInt("position");


        pictureDB = new PictureDatabase(getContext());
        db = pictureDB.getWritableDatabase();

        view=inflater.inflate(R.layout.dataitem, container, false);
        //imageView=(ImageView)view.findViewById(R.id.pinchImageView);
        textForce=(TextView)view.findViewById(R.id.open_force);
        textDis=(TextView)view.findViewById(R.id.open_dis);
        titleText=(TextView)view.findViewById(R.id.titleText);
        backImage=(ImageView)view.findViewById(R.id.backBtn);
        shareImage=(ImageView)view.findViewById(R.id.shareBtn);
        moveToNext=(Button)view.findViewById(R.id.next);
        moveToPrevious=(Button)view.findViewById(R.id.previous);
        moreMessage=(Button)view.findViewById(R.id.allmessage);

        mBarChart = (BarChart) view.findViewById(R.id.mBarChart);
        click();
        titleText.setText(name);
        String strExt;
        //获取文件名的后两位
        if (name.length() > 3) {
            strExt = name.substring(name.length() - 3);
        }else{
            strExt=name+"1";
        }
        if(strExt.equals("dgz")) {

            infolist = pictureDB.getInfos(db, MyApplication.DGZFORCE, name);
            if (infolist != null && infolist.size() > 0 ) {
                String[] strings = infolist.get(infolist.size() -1).split(",");
                for (int i =0; i < strings.length ;i++){
                    datalist.add(strings[i]);
                }

            }

        }

        mBarChart.getDescription().setEnabled(false);
        mBarChart.setMaxVisibleValueCount(60);
        mBarChart.setPinchZoom(false);
        mBarChart.setDrawBarShadow(false);
        mBarChart.setDrawGridBackground(false);

        XAxis xAxis = mBarChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setLabelCount(datalist.size());//设置标签显示的个数

        for (int i = 0 ;i < datalist.size();i++){
            sum = sum+Float.parseFloat(datalist.get(i));
        }
        average = sum/datalist.size();
        upAve = (float) (average*1.05);
        downAve = (float) (average*0.95);
        LimitLine ll0 = new LimitLine(average, "average");
        ll0.setLineWidth(2f);
        ll0.setLineColor(Color.BLUE);
        // ll0.setTypeface(tf);
        //  ll0.enableDashedLine(10f, 10f, 0f);
        ll0.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_TOP);
        ll0.setTextSize(10f);

        LimitLine ll1 = new LimitLine(upAve, "up5%");
        ll1.setLineWidth(2f);
        ll1.enableDashedLine(10f, 10f, 0f);
        ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        ll1.setTextSize(10f);

        LimitLine ll2 = new LimitLine(downAve, "down5%");
        ll2.setLineWidth(2f);
        ll2.enableDashedLine(10f, 10f, 0f);
        ll2.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        ll2.setTextSize(10f);
        YAxis leftAxis = mBarChart.getAxisLeft();
        //重置所有限制线,以避免重叠线
        leftAxis.removeAllLimitLines();
        leftAxis.addLimitLine(ll0);
        //设置优秀线
        leftAxis.addLimitLine(ll1);
        //设置及格线
        leftAxis.addLimitLine(ll2);
        //y轴最大
        // leftAxis.setAxisMaximum(200f);
        //y轴最小
        // leftAxis.setAxisMinimum(0f);
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setDrawZeroLine(false);

        mBarChart.getAxisLeft().setDrawGridLines(false);
        mBarChart.animateY(1000);
        mBarChart.getLegend().setEnabled(false);

        setData();
        for (IDataSet set : mBarChart.getData().getDataSets())
            set.setDrawValues(!set.isDrawValuesEnabled());
        return view;

    }

    //设置数据
    private void setData() {
        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();

        for (int i = 0; i < datalist.size(); i++) {
            // float mult = 50;
            float val = Float.parseFloat(datalist.get(i));
            yVals1.add(new BarEntry(i+1, val));
        }
        BarDataSet set1;
        if (mBarChart.getData() != null &&
                mBarChart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) mBarChart.getData().getDataSetByIndex(0);
            set1.setValues(yVals1);
            mBarChart.getData().notifyDataChanged();
            mBarChart.notifyDataSetChanged();
        } else {
            set1 = new BarDataSet(yVals1, "日期设置");
            //设置多彩 也可以单一颜色
            set1.setColors(ColorTemplate.VORDIPLOM_COLORS);
            set1.setDrawValues(false);

            ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
            dataSets.add(set1);

            BarData data = new BarData(dataSets);
            mBarChart.setData(data);
            mBarChart.setFitBars(true);
        }
        mBarChart.invalidate();
    }
    public void click(){
        backImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                getActivity().finish();

            }
        });

        shareImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 19) {
                    //获取sd卡目录
                    String sdpath = Environment.getExternalStorageDirectory().getAbsolutePath();
                    String appName = getString(R.string.app_name);


                    String wordPath = sdpath + "/" + appName + "/测试报告.pdf";
                    String picPath = sdpath + "/" + appName + "/data.png";

                    FileOutputStream out = null;
                    try {
                        //calculate.CreatePng(picPath);
                        Log.i("mtag", Environment.getExternalStorageDirectory().getPath()
                                + "/" + appName + "/" + "data"
                                + ".png");
                        if (mBarChart.saveToPath("data", "/" + appName)) {
                            FileInputStream fis = null;
                            try {
                                fis = new FileInputStream(picPath);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                            Bitmap bitmap = BitmapFactory.decodeStream(fis);
                            Log.i("mtag", "生成pdf文件前");

                            calculate.GenPDF(getActivity(), wordPath, "DGZ-1S", infolist.get(0).substring(0, 16), infolist.get(2), infolist.get(3), infolist.get(1),infolist.get(4),infolist.get(5), datalist, bitmap);
                            Log.i("mtag", "生成pdf文件后");
                            shareWordFile(wordPath);
                        } else {
                            Toast.makeText(getActivity(), "保存图片失败", Toast.LENGTH_SHORT).show();
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), "写入文件出错", Toast.LENGTH_SHORT).show();
                    } finally {
                        if (out != null) {
                            try {
                                out.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }

                    }
                }else{
                    Toast.makeText(activity, "当前手机版本过低，不能使用分享功能！", Toast.LENGTH_SHORT).show();


                }
            }
        });

        moreMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ArrayList<String> infolist=new ArrayList<String>();
                //ArrayList<String> datalist=new ArrayList<String>();
               /* String strExt = name.substring(name.length() - 3);
                if(strExt.equals("dgz")) {
                    infolist = pictureDB.getInfos(db, MyApplication.DGZFORCE,titleText.getText().toString());
                }*/
                /*if (infolist.size() != 0){
                    String[] strings = infolist.get(infolist.size() -1).split(",");
                    for (int i =0;i < strings.length;i++){
                        datalist.add(strings[i]);
                    }
                }*/
                Intent intent=new Intent(getActivity(), MoreMessageActivity.class);
                intent.putStringArrayListExtra("datalist", datalist);
                intent.putStringArrayListExtra("infolist", infolist);
               // intent.putExtra("strExt",strExt);
                startActivity(intent);
            }
        });

        moveToPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("ppp", "previousPosition  " + position);
                mCallback.onChange(position - 1, 0);
            }
        });

        moveToNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("ppp", "NextPosition  " + position);
                mCallback.onChange(position + 1, 0);
            }
        });
    }

    DialogInterface.OnClickListener dialoglistener = new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    mCallback.onChange(position,1);
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    break;
                default:
                    break;
            }

        }
    };
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
                 super.onActivityCreated(savedInstanceState);
                  activity = getActivity();

             }

    public interface onChangeListener
    {
        public void onChange(int position, int what);

    }

    //分享单张图片
    public void shareWordFile(String filepath) {
        // String imagePath2 = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+ "Pictures"+"/"+"taobao"+"/"+"191983953.jpg";
        //由文件得到uri
        Uri imageUri = Uri.fromFile(new File(filepath));
        Log.d("share", "uri:" + imageUri);  //输出：file:///storage/emulated/0/test.jpg

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        shareIntent.setType("application/msword");
        startActivity(Intent.createChooser(shareIntent, "分享到"));
    }



}
