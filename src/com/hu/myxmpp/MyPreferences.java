package com.hu.myxmpp;

import android.content.Context;
import android.content.SharedPreferences;

public class MyPreferences {
	static SharedPreferences sharedPreferences;

	public MyPreferences(Context context) {
		// TODO Auto-generated constructor stub
		sharedPreferences = context.getSharedPreferences(
				"com.hu.myxmpp_prefere", Context.MODE_APPEND);

	}

	public void setValueAndKey(String key, String value) {
		// ��ȡ���༭����
		SharedPreferences.Editor edit = sharedPreferences.edit();
		edit.putString(key, value);
		// �ύ.
		edit.commit();
	}

	public String getValueOfKey(String key) {
		return sharedPreferences.getString(key, "");
	}
}
