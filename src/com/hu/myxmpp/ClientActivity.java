package com.hu.myxmpp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.hu.utils.XmppUtils;

public class ClientActivity extends Activity {

	ListView friendsListView;
	private XMPPConnection xMPPConnection;
	private SimpleAdapter mSchedule;
	private ArrayList<HashMap<String, String>> myList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.client_page);
		findView();
		setListener();

	}

	private void findView() {
		friendsListView = (ListView) findViewById(R.id.friends_listview_clientpage);
	}

	private void setListener() {
		friendsListView.setOnItemClickListener(new MyListener());
	}

	private class MyListener implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			Toast.makeText(ClientActivity.this, myList.get(arg2) + "",
					Toast.LENGTH_SHORT).show();
			Intent intent = new Intent(ClientActivity.this, ChatActivity.class);
			Uri userNameUri = Uri.parse(myList.get(arg2).get("ItemText"));
			intent.setData(userNameUri);
			startActivity(intent);
		}
	}

	private List<RosterEntry> getFriendsLists(XMPPConnection xMPPConnection) {
		List<RosterEntry> rosterEntriesList = new ArrayList<RosterEntry>();
		// 获取好友列表
		Collection<RosterEntry> rosters = xMPPConnection.getRoster()
				.getEntries();
		for (RosterEntry rosterEntry : rosters) {
			rosterEntriesList.add(rosterEntry);
			System.out.println("name: " + rosterEntry.getName() + ",jid: "
					+ rosterEntry.getUser());
		}
		return rosterEntriesList;
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		try {
			xMPPConnection = XmppUtils.getConnection();
			List<RosterEntry> rosterEntries = getFriendsLists(xMPPConnection);
			myList = new ArrayList<HashMap<String, String>>();
			for (RosterEntry rosterEntry : rosterEntries) {
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("ItemTitle", rosterEntry.getName());
				map.put("ItemText", rosterEntry.getUser());
				Log.i(rosterEntry.getName(), rosterEntry.getUser());
				myList.add(map);

			}
			mSchedule = new SimpleAdapter(this,// 没什么解释
					myList,// 数据来源
					R.layout.custom_listitem,// ListItem的XML实现
					new String[] { "ItemTitle", "ItemText" },// 动态数组与ListItem对应的子项
					new int[] { R.id.ItemTitle, R.id.ItemText });// ListItem的XML文件里面的两个TextViewID
			// 添加并且显示
			friendsListView.setAdapter(mSchedule);
		} catch (XMPPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		super.onResume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub

		super.onStop();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		xMPPConnection.disconnect();
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	/** 设置菜单栏 */
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.add_friend_item:
			startActivity(new Intent(ClientActivity.this,
					AddFriendsActivity.class));
			break;
		case R.id.null_item:
			break;
		case R.id.menu_exit:
			System.exit(0);
			break;
		}
		return super.onOptionsItemSelected(item);
	}
}
