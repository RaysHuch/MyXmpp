package com.hu.myxmpp;

import org.jivesoftware.smack.XMPPException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.hu.utils.XmppUtils;

public class LoginActivity extends Activity {
	private Button registerBtn;
	private Button loginBtn;
	private EditText userNameEditText;
	private EditText userPwdEditText;
	private LinearLayout layout1, layout2;

	InputMethodManager manager;

	MyPreferences myPreferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_page);
		findView();
		setListener();
		manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		myPreferences = new MyPreferences(this);
		System.out.println(myPreferences.getValueOfKey("user_name"));
		System.out.println(myPreferences.getValueOfKey("user_pwd"));
		userNameEditText
				.setText(myPreferences.getValueOfKey("user_name") != "" ? myPreferences
						.getValueOfKey("user_name") : "");
		userPwdEditText
				.setText(myPreferences.getValueOfKey("user_pwd") != "" ? myPreferences
						.getValueOfKey("user_pwd") : "");
	}

	/**
	 * 绑定控件
	 */
	private void findView() {
		userNameEditText = (EditText) findViewById(R.id.username_editText_loginpage);
		userPwdEditText = (EditText) findViewById(R.id.userpwd_editText_loginpage);
		registerBtn = (Button) findViewById(R.id.register_button_loginpage);
		loginBtn = (Button) findViewById(R.id.login_button_loginpage);
		layout1 = (LinearLayout) findViewById(R.id.linearlayout1_loginpage);
		layout2 = (LinearLayout) findViewById(R.id.linearlayout2_loginpage);
	}

	/**
	 * 设置监听
	 */
	private void setListener() {
		registerBtn.setOnClickListener(new MyClickListener());
		loginBtn.setOnClickListener(new MyClickListener());
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		//隐藏虚拟键盘
		manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
				InputMethodManager.HIDE_NOT_ALWAYS);

		return super.onTouchEvent(event);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	private class MyClickListener implements OnClickListener {
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			if (arg0.equals(registerBtn)) {
				startActivity(new Intent(LoginActivity.this,
						RegisterActivity.class));
				// LoginActivity.this.finish();
			} else if (arg0.equals(loginBtn)) {
				final String USERID = userNameEditText.getText().toString();
				final String PWD = userPwdEditText.getText().toString();
				if (USERID == "" || PWD == "") {
					Toast.makeText(getApplicationContext(), "用户名和密码不能为空！",
							Toast.LENGTH_SHORT).show();
				} else {
					//隐藏虚拟键盘
					manager.hideSoftInputFromWindow(getCurrentFocus()
							.getWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);
					new Thread(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							handler.sendEmptyMessage(1);
							try {
								//登录
								XmppUtils.login(getLocalClassName(), USERID,
										PWD);
								handler.sendEmptyMessage(3);
								//保存账户密码
								myPreferences.setValueAndKey("user_name",
										USERID);
								myPreferences.setValueAndKey("user_pwd", PWD);
								Intent intent = new Intent(LoginActivity.this,
										ClientActivity.class);
								intent.putExtra("USERID", USERID);
								canThreadRun = false;

								startActivity(intent);
								LoginActivity.this.finish();
							} catch (XMPPException e) {
								XmppUtils.closeConnection();
								handler.sendEmptyMessage(2);
								Log.i(getLocalClassName(),
										"catch xmppexception");
								handler.sendEmptyMessage(4);
							} catch (Exception e) {
								// TODO: handle exception
								XmppUtils.closeConnection();
								handler.sendEmptyMessage(2);
								Log.i(getLocalClassName(), "catch exception");
								handler.sendEmptyMessage(4);
								e.printStackTrace();
							}
						}
					}).start();
				}
			}
		}
	}

	private boolean canThreadRun = true;

	class MTimeTack extends Thread {
		int seconds;
		public MTimeTack() {
			// TODO Auto-generated constructor stub
			init();
		}
		public void init() {
			seconds = 0;
			canThreadRun = true;
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			while (seconds < 10 && canThreadRun) {
				seconds++;
				System.out.println(seconds);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (canThreadRun) {
				handler.sendEmptyMessage(2);
				handler.sendEmptyMessage(4);
			}

		}
	}

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				layout1.setVisibility(View.VISIBLE);
				layout2.setVisibility(View.GONE);
				(new MTimeTack()).start();
				break;
			case 2:
				//显示progressbar的布局部分
				layout1.setVisibility(View.GONE);
				layout2.setVisibility(View.VISIBLE);
				break;
			case 3:
				// 显示“登陆成功”的toast
				Toast.makeText(LoginActivity.this, "登陆成功", Toast.LENGTH_SHORT)
						.show();
				break;
			case 4:
				Toast.makeText(LoginActivity.this, "登陆失败", Toast.LENGTH_SHORT)
						.show();
				canThreadRun = false;
			default:
				break;
			}
		};
	};

}
