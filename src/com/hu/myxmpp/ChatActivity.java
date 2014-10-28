package com.hu.myxmpp;

import java.util.List;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.hu.utils.XmppUtils;

public class ChatActivity extends Activity {

	private TextView titleNameTextView;
	private Button sendMsgBtn;
	private EditText sendMsgEdit;

	InputMethodManager manager;

	LinearLayout msgsFriendsLinearLayout;
	ScrollView msgsFriendsScrollView;
	LinearLayout loadingLayout;
	private Thread mThread;

	ChatManager chatManager;
	MyMessageListener myMessageListener;

	/**
	 * 设置布局显示属性
	 */
	private LayoutParams mLayoutParams = new LinearLayout.LayoutParams(
			LinearLayout.LayoutParams.WRAP_CONTENT,
			LinearLayout.LayoutParams.WRAP_CONTENT);
	// private ProgressBar progressBar;
	private String mWithJabberID;
	private List<org.jivesoftware.smack.packet.Message> offLineLists;

	// private ChatManager chatmanager;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat_page);
		findView();
		setListener();
		init();
		manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

	}

	private void init() {
		// TODO Auto-generated method stub
		// 线性布局
		LinearLayout layout = new LinearLayout(this);
		// 设置布局 水平方向
		layout.setOrientation(LinearLayout.HORIZONTAL);
		// // 进度条
		// progressBar = new ProgressBar(this);
		// // 进度条显示位置
		// progressBar.setPadding(0, 0, 15, 0);
		// // 把进度条加入到layout中
		// layout.addView(progressBar, mLayoutParams);
		// // 文本内容
		// TextView textView = new TextView(this);
		// textView.setText("加载中...");
		// textView.setGravity(Gravity.CENTER_VERTICAL);
		// // 把文本加入到layout中
		// layout.addView(textView, FFlayoutParams);
		// 设置layout的重力方向，即对齐方式是
		layout.setGravity(Gravity.CENTER);

		// 设置ListView的页脚layout
		loadingLayout = new LinearLayout(this);
		loadingLayout.addView(layout, mLayoutParams);
		loadingLayout.setGravity(Gravity.CENTER);

		// 获得传过来的jid
		mWithJabberID = getIntent().getDataString();// 获取聊天对象的id
		titleNameTextView.setText(mWithJabberID.substring(0,
				mWithJabberID.indexOf("@")));

		
		try {
			chatManager = XmppUtils.getConnection().getChatManager();
		} catch (XMPPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		myMessageListener = new MyMessageListener();
		// chatmanager = XmppUtils.getConnection().getChatManager();
		chatManager.addChatListener(new ChatManagerListener() {
			
			@Override
			public void chatCreated(Chat arg0, boolean arg1) {
				// TODO Auto-generated method stub
				arg0.addMessageListener(myMessageListener);
			}
		});
		
		try {
			offLineLists = XmppUtils.getOffLine();
		} catch (XMPPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(int i=0;i<offLineLists.size();i++){
			Message msg = new Message();
			msg.what=1;
			msg.obj = offLineLists.get(i).toString();
			handler.sendMessage(msg);
		}

	}

	private void findView() {
		titleNameTextView = (TextView) findViewById(R.id.titlename_textview_chatpage);
		// 得到一个ListView用来显示条目
		msgsFriendsLinearLayout = (LinearLayout) findViewById(R.id.msgslist_linearlayout_chatpage);
		msgsFriendsScrollView = (ScrollView) findViewById(R.id.msgslist_scrollview_chatpage);

		sendMsgBtn = (Button) findViewById(R.id.sentmsg_button_chatpage);
		sendMsgBtn.setClickable(false);
		sendMsgEdit = (EditText) findViewById(R.id.sendmsg_edittext_chatpage);
	}

	private void setListener() {
		sendMsgBtn.setOnClickListener(new MyOnClickListener());
		sendMsgEdit.addTextChangedListener(new MyTextChangeListener());
	}

	private class MyMessageListener implements MessageListener {

		@Override
		public void processMessage(Chat arg0,
				org.jivesoftware.smack.packet.Message arg1) {
			// TODO Auto-generated method stub
			String msg = arg1.getBody();  //消息主体  
			Message message = new Message();
			message.what = 1;
			message.obj = msg;
			handler.sendMessage(message);
		}

	}

	private class MyTextChangeListener implements TextWatcher {

		@Override
		public void afterTextChanged(Editable arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {
			// TODO Auto-generated method stub
			if (sendMsgEdit.getText().length() == 0) {
				sendMsgBtn.setClickable(false);
			} else {
				sendMsgBtn.setClickable(true);
			}
		}

	}

//	@Override
//	public boolean onTouchEvent(MotionEvent event) {
//		// TODO Auto-generated method stub
//		manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
//				InputMethodManager.HIDE_NOT_ALWAYS);
//
//		return super.onTouchEvent(event);
//	}

	private class MyOnClickListener implements OnClickListener {
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			if (arg0.equals(sendMsgBtn)) {
				sendMsgBtn.setClickable(false);
				if (mThread == null || !mThread.isAlive()) {
					mThread = new Thread() {
						@Override
						public void run() {
							// try {
							// // 这里放你网络数据请求的方法，我在这里用线程休眠5秒方法来处理
							// Thread.sleep(5000);
							// } catch (InterruptedException e) {
							// e.printStackTrace();
							// }
							String stringMsg = sendMsgEdit.getText().toString();
							Message message = new Message();
							message.what = 2;
							message.obj = stringMsg;
							handler.sendMessage(message);

							try {
								XmppUtils.sendmessage(mWithJabberID, stringMsg,
										chatManager,myMessageListener);
							} catch (XMPPException e) {
								e.printStackTrace();
								handler.sendEmptyMessage(3);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}
					};
					mThread.start();
				}

			}
		}
	}

	@SuppressLint("HandlerLeak") 
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			LayoutParams MM_LP = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.MATCH_PARENT);
			switch (msg.what) {
			case 1:
				LinearLayout linearLayout = new LinearLayout(ChatActivity.this);
				linearLayout.setLayoutParams(MM_LP);
				linearLayout.setGravity(Gravity.LEFT);
				TextView textView = new TextView(getApplicationContext());
				textView.setText(msg.obj.toString());
				textView.setTextColor(getResources().getColor(R.color.black));
				textView.setGravity(Gravity.LEFT);
				linearLayout.addView(textView);
				msgsFriendsLinearLayout.addView(linearLayout);
				// // 重新刷新Listview的adapter里面数据
				// adapter.notifyDataSetChanged();
				msgsFriendsScrollView.post(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						msgsFriendsScrollView.fullScroll(ScrollView.FOCUS_DOWN);
					}
				});
				break;
			case 2:
				LinearLayout linearLayout2 = new LinearLayout(ChatActivity.this);
				linearLayout2.setLayoutParams(MM_LP);
				linearLayout2.setGravity(Gravity.RIGHT);
				TextView textView1 = new TextView(getApplicationContext());
				textView1.setTextColor(getResources().getColor(R.color.black));
				textView1.setText(msg.obj.toString());
				textView1.setGravity(Gravity.RIGHT);
				linearLayout2.addView(textView1);
				msgsFriendsLinearLayout.addView(linearLayout2);
				// // 重新刷新Listview的adapter里面数据
				// adapter.notifyDataSetChanged();
				msgsFriendsScrollView.post(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						msgsFriendsScrollView.fullScroll(ScrollView.FOCUS_DOWN);
					}
				});
				sendMsgEdit.setText("");
				break;
			case 3:
				Toast.makeText(ChatActivity.this, "发送失败", Toast.LENGTH_SHORT)
						.show();
			default:
				break;
			}
		}

	};
}