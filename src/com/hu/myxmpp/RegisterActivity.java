package com.hu.myxmpp;

import org.jivesoftware.smack.XMPPException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.hu.utils.XmppUtils;

public class RegisterActivity extends Activity {

	private LinearLayout layout1;
	private LinearLayout layout2;
	private EditText userNameEditText;
	private EditText userPwdEditText;
	private EditText userPwdEditText2;
	private Button registerBtn;

	InputMethodManager manager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register_page);
		findView();
		setListener();
		manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

	}

	private void findView() {
		layout1 = (LinearLayout) findViewById(R.id.linearlayout1_registerpage);
		layout2 = (LinearLayout) findViewById(R.id.linearlayout2_registerpage);
		userNameEditText = (EditText) findViewById(R.id.username_edittext_registerpage);
		userPwdEditText = (EditText) findViewById(R.id.userpwd_edittext_registerpage);
		userPwdEditText2 = (EditText) findViewById(R.id.userpwd2_edittext_registerpage);
		registerBtn = (Button) findViewById(R.id.register_button_regisgerpage);
	}

	private void setListener() {
		registerBtn.setOnClickListener(new MyClickListener());
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
				InputMethodManager.HIDE_NOT_ALWAYS);

		return super.onTouchEvent(event);
	}

	private class MyClickListener implements OnClickListener {
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			if (arg0.equals(registerBtn)) {
				final String USERID = userNameEditText.getText().toString();
				final String PWD = userPwdEditText.getText().toString();
				final String PWD2 = userPwdEditText2.getText().toString();
				if (USERID == "" || PWD == "" || PWD2 == "") {
					Toast.makeText(getApplicationContext(), "用户名和密码不能为空！",
							Toast.LENGTH_SHORT).show();
				} else if (PWD == PWD2) {
					Toast.makeText(getApplicationContext(), "两次密码不一致！",
							Toast.LENGTH_SHORT).show();
				} else {
					new Thread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							handler.sendEmptyMessage(1);
							try {

								handler.sendEmptyMessage(3);
								XmppUtils.register(getLocalClassName(), USERID,
										PWD);
								Intent intent = new Intent(
										RegisterActivity.this,
										LoginActivity.class);
								intent.putExtra("USERID", USERID);
								startActivity(intent);
								RegisterActivity.this.finish();
							} catch (XMPPException e) {
								XmppUtils.closeConnection();
								handler.sendEmptyMessage(2);
								handler.sendEmptyMessage(4);
							} catch (Exception ex) {
								ex.printStackTrace();
								handler.sendEmptyMessage(4);
							}
						}
					}).start();
				}
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
				break;
			case 2:
				layout1.setVisibility(View.GONE);
				layout2.setVisibility(View.VISIBLE);
				break;
			case 3:
				// 显示“注册成功”的toast
				Toast.makeText(RegisterActivity.this, "注册成功",
						Toast.LENGTH_SHORT).show();
			case 4:
				Toast.makeText(RegisterActivity.this, "注册失败",
						Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}
		};
	};
}
