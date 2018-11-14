package activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import java.util.List;

import controller.MyApplication;
import controller.PictureDatabase;
import utils.Calculate;

public class AnotherBarActivity extends Activity {

    private BarChart mBarChart;
    private ImageView backImage;
    private Button btn_share;
    private Button btn_details;
    Calculate calculate = new Calculate();
    private ArrayList<String> datalist = new ArrayList<>();
    private ArrayList<String> infolist = new ArrayList<>();
    PictureDatabase pd;
    SQLiteDatabase sd;
    float sum = 0;
    float average;
    float upAve = 0;
    float downAve = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_another);
        //获取打开的文件所在的路径
        Intent data = this.getIntent();
        datalist = data.getStringArrayListExtra("list");

        pd=new PictureDatabase(this);
        sd = pd.getWritableDatabase();
        infolist=pd.getLastDatas(sd, MyApplication.DGZFORCE);
        initView();
        click();

    }

    private void click() {

        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnotherBarActivity.this.finish();
            }
        });

        btn_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AnotherBarActivity.this,MoreMessageActivity.class);
                intent.putStringArrayListExtra("infolist", infolist);
                intent.putStringArrayListExtra("datalist", datalist);

                startActivity(intent);
            }
        });

        btn_share.setOnClickListener(new View.OnClickListener() {
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


                            calculate.GenPDF(AnotherBarActivity.this, wordPath, "DGZ-1S", infolist.get(0).substring(0, 16), infolist.get(2), infolist.get(3), infolist.get(1),infolist.get(4), infolist.get(5),datalist, bitmap);
                            Log.i("mtag", "生成pdf文件后");
                            shareWordFile(wordPath);


                    } else {
                        Toast.makeText(AnotherBarActivity.this, "保存图片失败", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(AnotherBarActivity.this, "写入文件出错", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(AnotherBarActivity.this, "当前手机版本过低，不能使用分享功能！", Toast.LENGTH_SHORT).show();

                }

            }

        });
    }

    //初始化
    private void initView() {

        //基本控件

        backImage = (ImageView) findViewById(R.id.back);
        btn_details = (Button) findViewById(R.id.btn_showDetails);
        btn_share = (Button) findViewById(R.id.btn_share);


        mBarChart = (BarChart) findViewById(R.id.mBarChart);

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
    }

    //设置数据
    private void setData() {
        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();

        for (int i = 0; i < datalist.size(); i++) {

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

            /*//显示顶点值
            case R.id.btn_show_values:
                for (IDataSet set : mBarChart.getData().getDataSets())
                    set.setDrawValues(!set.isDrawValuesEnabled());

                mBarChart.invalidate();
                break;
            //x轴动画
            case R.id.btn_anim_x:
                mBarChart.animateX(3000);
                break;
            //y轴动画
            case R.id.btn_anim_y:
                mBarChart.animateY(3000);
                break;
            //xy轴动画
            case R.id.btn_anim_xy:
                mBarChart.animateXY(3000, 3000);
                break;
            //保存到sd卡
            case R.id.btn_save_pic:
                if (mBarChart.saveToGallery("title" + System.currentTimeMillis(), 50)) {
                    Toast.makeText(getApplicationContext(), "保存成功",
                            Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(getApplicationContext(), "保存失败",
                            Toast.LENGTH_SHORT).show();


                break;
            //切换自动最大最小值
            case R.id.btn_auto_mix_max:
                mBarChart.setAutoScaleMinMaxEnabled(!mBarChart.isAutoScaleMinMaxEnabled());
                mBarChart.notifyDataSetChanged();
                break;
            //高亮显示
            case R.id.btn_actionToggleHighlight:
                if (mBarChart.getData() != null) {
                    mBarChart.getData().setHighlightEnabled(
                            !mBarChart.getData().isHighlightEnabled());
                    mBarChart.invalidate();
                }
                break;
            //显示边框
            case R.id.btn_show_border:
                for (IBarDataSet set : mBarChart.getData().getDataSets())
                    ((BarDataSet) set).setBarBorderWidth(set.getBarBorderWidth() == 1.f ? 0.f : 1.f);
                mBarChart.invalidate();
                break;*/


    public void shareWordFile(String filepath) {
        // String imagePath2 = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+ "Pictures"+"/"+"taobao"+"/"+"191983953.jpg";
        //由文件得到uri
        Uri fileUri = Uri.fromFile(new File(filepath));
        Log.d("share", "uri:" + fileUri);  //输出：file:///storage/emulated/0/test.jpg

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
        shareIntent.setType("application/msword");
        startActivity(Intent.createChooser(shareIntent, "分享到"));
    }



}