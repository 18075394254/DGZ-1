package activity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

import com.example.user.dm_3.R;

public class ParaSettingActivity extends Activity
{

	
	EditText idEdit=null;
	EditText nameEdit=null;
	EditText locationEdit=null;
	EditText companyEdit =null;
	EditText supplementEdit =null;

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//强制为竖屏
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_para_setting);
		idEdit= (EditText) findViewById(R.id.liftid);
		nameEdit = (EditText) findViewById(R.id.operatorname);
		locationEdit = (EditText)findViewById(R.id.location);
		companyEdit = (EditText) findViewById(R.id.company);
		supplementEdit = (EditText) findViewById(R.id.supplement);
		loadConfig();
		
	}
	
	public void onSave(View view) 
	{
		//获取控件id值

		String strID = idEdit.getText().toString();
		String strName = nameEdit.getText().toString();
		String strLocation = locationEdit.getText().toString();
		String strCompany = companyEdit.getText().toString();
		String strSupplement = supplementEdit.getText().toString();
		SharedPreferences sp = getSharedPreferences("info", Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putString("liftid", strID);
		editor.putString("operator", strName);
		editor.putString("location", strLocation);
		editor.putString("company", strCompany);
		editor.putString("supplement", strSupplement);
		editor.commit();
		
		MainActivity.s_mLiftId = strID;
		MainActivity.s_mOperator = strName;
		MainActivity.s_mLocation = strLocation;
		MainActivity.s_mCompany = strCompany;
		MainActivity.s_mSupplement = strSupplement;
		finish();

	}
	
	public void onCancel(View view) 
	{
		finish();
	}
	public void loadConfig() 
	{	
		idEdit.setText(MainActivity.s_mLiftId);
		nameEdit.setText(MainActivity.s_mOperator);
		locationEdit.setText(MainActivity.s_mLocation);
		companyEdit.setText(MainActivity.s_mCompany);
		supplementEdit.setText(MainActivity.s_mSupplement);
	}

}
