package com.example.lazy_man_client;

import java.util.ArrayList;


import android.widget.Button;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Button;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class AdDetailActivity extends Activity {
    private Button button1;
    
    private TextView User;
    private TextView DDL;
    private TextView Coins;
    private TextView Address;
    private TextView Adtime;
    private TextView infoPhoneOrderer;
    private TextView infoOrderNum;
    private TextView task_size;
    private TextView task_place;
    private TextView task_Tele;
    private TextView task_State;
    private TextView task_Content;
    
	private Handler mHandler;//信息接收
	private Handler handler;//toast专用
	MyReceiver receiver;
	Task task = new Task();
	
	private String TaskId;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
	       super.onCreate(savedInstanceState);
	       setContentView(R.layout.activity_ad_detail);
	       
	       receiver = new MyReceiver(); // 注册广播
	       IntentFilter filter = new IntentFilter();
	       filter.addAction("android.intent.action.AdDetailActivity"); //此处改成自己activity的名字
	       registerReceiver(receiver, filter);
	       
	       init();
	       
	       Bundle bundle = this.getIntent().getExtras();
	       TaskId = bundle.getString("TaskId");
	       
	       //发送查询任务信息
	       String sendstr = "&56&00" + TaskId;
	       sent(sendstr);
	       mHandler = new Handler() {
				public void handleMessage(android.os.Message msg) {
					String strall = msg.obj.toString();
					String str1 = strall.substring(1, 3);
					String str2 = strall.substring(1, 7);
					if (str1.equals("56")) { // 接收成功
						task.Initial(strall);	
						ShowMessage();	
					}	
					if (str2.equals("57&000")) { // 任务完成发送成功
						showToast("发送成功");
						//完成按钮不可再点击
						button1.setEnabled(false);
					}	
					if (str2.equals("57&003")||str2.equals("57&004")||str2.equals("57&002")) { // 任务完成发送成功
						showToast("发送失败");
						//完成按钮可再点击
						button1.setEnabled(true);
					}	
				};
			};	
	       button1.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					new AlertDialog.Builder(AdDetailActivity.this)
					.setTitle("温馨提示")
					.setMessage("是否确定完成任务？ 确认后将无法取消")
					.setPositiveButton("确定",new DialogInterface.OnClickListener(){
							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								/*发送确认任务完成字符串*/
								byte[] msgBuffer = null;
								String sendstr = "&57&00" + TaskId;
								sent(sendstr);	
								button1.setEnabled(false);
							}						
					})
					.setNegativeButton("取消",null)
					.create()
					.show();
				}
			});
	       
    }
    
    public void init(){
	    button1 = (Button) findViewById(R.id.AchieveTask);//任务完成
	    User = (TextView)findViewById(R.id.infoOrderer);//甲方用户
	    infoPhoneOrderer = (TextView)findViewById(R.id.infoPhoneOrderer);//甲方电话
	    DDL = (TextView)findViewById(R.id.infoArriTime);//送达时间
	    Coins = (TextView)findViewById(R.id.infoCoinsNum);//悬赏金额
	    Address = (TextView)findViewById(R.id.infoAddress);//送货地址
	    Adtime = (TextView)findViewById(R.id.infoOrderTime);//下单时间 
	    infoOrderNum = (TextView)findViewById(R.id.infoOrderNum);//订单号
	    
	    task_size = (TextView)findViewById(R.id.task_size);//物件大小
	    task_place = (TextView)findViewById(R.id.task_place);//取快递地址
	    task_Tele = (TextView)findViewById(R.id.task_Tele);//取货电话
	    task_State = (TextView)findViewById(R.id.task_State);//任务状态
	    task_Content = (TextView)findViewById(R.id.task_Content);//任务描述
    }

	//****************两个与netservice沟通的接口，收发数据，，字符串格式	
	public void sent(String bs) { // 通过Service发送数据
		Intent intent = new Intent();// 创建Intent对象
		intent.setAction("android.intent.action.cmd");
		intent.putExtra("value", bs);
		sendBroadcast(intent);// 发送广播
	}
	
	public void ShowMessage(){
		User.setText(task.GetUsr1Name());
		DDL.setText(String.valueOf(task.GetOutTime()[0])+"月"+String.valueOf(task.GetOutTime()[1])+"日"+String.valueOf(task.GetOutTime()[2])+"时 - "+String.valueOf(task.GetOutTime()[3])+"时");
		Coins.setText(String.valueOf(task.GetCoins()));
		Address.setText(Data_all.Address[(task.GetOutAddress())[0] ][(task.GetOutAddress())[1] ] );				
		//更新时间信息
		Adtime.setText(String.valueOf(task.GetInTime()[0])+"月"+String.valueOf(task.GetInTime()[1])+"日"+String.valueOf(task.GetInTime()[2])+"时 - "+String.valueOf(task.GetInTime()[3])+"时");
		infoPhoneOrderer.setText(task.GetUsr1Tele());
		infoOrderNum.setText(String.valueOf(task.GetTNO()));
		task_size.setText(String.valueOf(task.GetSize()));
		task_place.setText(Data_all.Address[(task.GetInAddress())[0] ][(task.GetInAddress())[1] ]);
		task_Tele.setText(task.GetLast4Tele());
		task_Content.setText(task.GetContent());
		
		if(task.GetTaskstate()==0){
			button1.setEnabled(true);
			task_State.setText("未被领取");
		}
		if(task.GetTaskstate()==1){
			button1.setEnabled(true);
			task_State.setText("进行中");
		}
		if(task.GetTaskstate()==2){
			button1.setEnabled(false);
			task_State.setText("已申请完成");
		}
		if(task.GetTaskstate()==3){
			button1.setEnabled(false);
			task_State.setText("已完成");
		}
		if(task.GetTaskstate()==4){
			button1.setEnabled(false);
			task_State.setText("已撤销");
		}
	}
	
    private class MyReceiver extends BroadcastReceiver { // 接收service传来的信息
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (intent.getAction().equals("android.intent.action.AdDetailActivity")) {
				Bundle bundle = intent.getExtras();
				String str = bundle.getString("str");
				Message msg = new Message();
				msg.obj = str;
				mHandler.sendMessage(msg);
			}
		}
	}
    
    public void showToast(final String str) {
		handler = new Handler(Looper.getMainLooper());
		handler.post(new Runnable() {
			public void run() {
				Toast.makeText(getApplicationContext(), str,
						Toast.LENGTH_LONG).show();
			}
		});
	}
}
