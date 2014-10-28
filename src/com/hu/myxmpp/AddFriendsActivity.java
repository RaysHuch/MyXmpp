package com.hu.myxmpp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.ReportedData;
import org.jivesoftware.smackx.ReportedData.Row;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.hu.utils.XmppUtils;

public class AddFriendsActivity extends Activity {

	private Button searchBtn;
	private EditText searchEditText;
	private LinearLayout layout1;
	private LinearLayout layout2;
	private ListView myFriendsListView;
	private ArrayList<HashMap<String, String>> myList = new ArrayList<HashMap<String, String>>();
	private SimpleAdapter mSchedule;

	InputMethodManager manager;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addfriends_page);
		findView();
		setListener();
		manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
	}

	private void findView() {
		layout1 = (LinearLayout) findViewById(R.id.linearlayout1_addfriendspage);
		layout2 = (LinearLayout) findViewById(R.id.linearlayout2_addfriendspage);
		myFriendsListView = (ListView) findViewById(R.id.friends_listview_addfriendspage);
		searchBtn = (Button) findViewById(R.id.search_button_addfriendspage);
		searchEditText = (EditText) findViewById(R.id.search_edittext_addfriendspage);
	}

	private void setListener() {
		myFriendsListView.setOnItemClickListener(new MyItemListener());
		searchBtn.setOnClickListener(new MyClickListener());
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
			if (arg0.equals(searchBtn)) {
				manager.hideSoftInputFromWindow(getCurrentFocus()
						.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
				new Thread(new Runnable() {
					String contentOfEdit = searchEditText.getText().toString()
							.trim();

					@Override
					public void run() {
						// TODO Auto-generated method stub
						try {
							handler.sendEmptyMessage(1);

							ReportedData data = XmppUtils
									.addFriend(contentOfEdit);
							Iterator<Row> it = data.getRows();
							while (it.hasNext()) {
								HashMap<String, String> hashMap = new HashMap<String, String>();
								hashMap.put("ItemTitle",
										it.next().getValues("Username").next()
												.toString());
								hashMap.put("ItemText", "");
								myList.add(hashMap);
								Log.i("AddFriendsActivity.MyClickListener",
										hashMap.get("ItemTitle"));
							}

							canThreadRun = false;

							handler.sendEmptyMessage(0);
							handler.sendEmptyMessage(2);
						} catch (Exception e) {
							Log.d("AddFriendsAcitvity.MyClickListener error",
									"出错");
							e.printStackTrace();
						}
					}
				}).start();

			}
		}
	}

	private void addFriend(String userName,String nickName) {
		
		try {
			Roster roster = XmppUtils.getConnection().getRoster();
			roster.createEntry(userName + "@"
					+ XmppUtils.getConnection().getServiceName(), nickName,
					null);
		} catch (XMPPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Toast.makeText(AddFriendsActivity.this, "success", Toast.LENGTH_SHORT)
				.show();

	}

	private class MyItemListener implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			Toast.makeText(AddFriendsActivity.this, myList.get(arg2) + "",
					Toast.LENGTH_SHORT).show();
			new MDialog(AddFriendsActivity.this, myList.get(arg2).get(
					"ItemTitle")).show();
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
			while (seconds < 5 && canThreadRun) {
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
				handler.sendEmptyMessage(3);
			}

		}
	}

	private class MDialog extends Dialog {
		String userName;
		String nickName;
		Context context;
		private EditText editText;

		public MDialog(Context context, String userName) {
			super(context);
			// TODO Auto-generated constructor stub
			this.userName = userName;
			this.context = context;
			init();
		}

		private void init() {
			MDialog.this.setTitle("确认");

			LinearLayout linearLayout = new LinearLayout(context);
			linearLayout.setOrientation(LinearLayout.VERTICAL);

			LayoutParams MM_LP_MARGINS = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.MATCH_PARENT);
			MM_LP_MARGINS.setMargins(10, 10, 10, 10);
			linearLayout.setLayoutParams(MM_LP_MARGINS);
			linearLayout.setGravity(Gravity.CENTER);

			LinearLayout linearLayout2 = new LinearLayout(context);
			linearLayout2.setOrientation(LinearLayout.HORIZONTAL);
			linearLayout2.setGravity(Gravity.CENTER);

			LayoutParams layoutParams = new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			layoutParams.setMargins(0, 5, 0, 10);

			TextView massageTextview = new TextView(context);
			massageTextview.setText("请确认添加并赋给昵称");
			massageTextview.setTextSize(10.0f);
			massageTextview
					.setTextColor(getResources().getColor(R.color.white));
			massageTextview.setLayoutParams(layoutParams);
			linearLayout.addView(massageTextview);

			TextView textView = new TextView(context);
			textView.setText("昵  称");
			textView.setTextSize(14.0f);
			textView.setTextColor(getResources().getColor(R.color.white));

			editText = new EditText(context);
			LayoutParams MW_LP_EDITTEXT = new LinearLayout.LayoutParams(251,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			editText.setLayoutParams(MW_LP_EDITTEXT);

			linearLayout2.addView(textView);
			linearLayout2.addView(editText);

			LinearLayout linearLayout3 = new LinearLayout(context);
			linearLayout3.setOrientation(LinearLayout.HORIZONTAL);
			linearLayout3.setGravity(Gravity.CENTER);

			LayoutParams MM_LP_BUTTON = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			MM_LP_BUTTON.weight = 1;
			
			Button button = new Button(context);
			button.setLayoutParams(MM_LP_BUTTON);
			button.setText("确定");
			button.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					nickName = editText.getText().toString();
					addFriend(userName,nickName);
					MDialog.this.dismiss();
				}
			});

			Button button2 = new Button(context);
			button2.setLayoutParams(MM_LP_BUTTON);
			button2.setText("取消");
			button2.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					MDialog.this.dismiss();
				}
			});
			linearLayout3.addView(button);
			linearLayout3.addView(button2);

			linearLayout.addView(linearLayout2);
			linearLayout.addView(linearLayout3);

			setContentView(linearLayout);

		}
		
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			super.onCreate(savedInstanceState);
		}
	}

	@SuppressLint("HandlerLeak") 
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case 0:
				mSchedule = new SimpleAdapter(AddFriendsActivity.this,// 没什么解释
						myList,// 数据来源
						R.layout.custom_listitem,// ListItem的XML实现
						new String[] { "ItemTitle", "ItemText" },// 动态数组与ListItem对应的子项
						new int[] { R.id.ItemTitle, R.id.ItemText });// ListItem的XML文件里面的两个TextViewID
				// 添加并且显示
				myFriendsListView.setAdapter(mSchedule);
				break;
			case 1:
				layout1.setVisibility(View.VISIBLE);
				layout2.setVisibility(View.GONE);
				(new MTimeTack()).start();
				break;
			case 2:
				layout1.setVisibility(View.GONE);
				layout2.setVisibility(View.VISIBLE);

				break;
			case 3:
				Toast.makeText(AddFriendsActivity.this, "查询失败",
						Toast.LENGTH_SHORT).show();
			default:
				break;
			}
		};

	};
}
