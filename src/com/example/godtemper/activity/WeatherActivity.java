package com.example.godtemper.activity;

import net.youmi.android.banner.AdSize;
import net.youmi.android.banner.AdView;

import com.example.godtemper.R;
import com.example.godtemper.service.AutoUpdateService;
import com.example.godtemper.util.HttpCallbackListener;
import com.example.godtemper.util.HttpUtil;
import com.example.godtemper.util.Utility;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class WeatherActivity extends Activity implements OnClickListener {

	private LinearLayout weatherInfoLayout;
	private TextView cityNameText;
	private TextView publishText;
	private TextView weatherDespText;
	private TextView temp1text;
	private TextView temp2text;
	private TextView currentDateText;
	/**
	 * �л����а�ť
	 */
	private Button switchcity;
	/**
	 * ����������ť
	 */
	private Button refreshWeather;
	/**
	 * �����沼��
	 */
	private RelativeLayout main_layout;
	/**
	 * ����״̬
	 */
	private String weatherstate;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_layout);
		// ��ʼ�����ؼ�
		main_layout = (RelativeLayout) findViewById(R.id.main_layout);
		// main_layout.setBackgroundDrawable(getResources().getDrawable(R.drawable.rain));
		weatherInfoLayout = (LinearLayout) findViewById(R.id.weather_info_layout);
		cityNameText = (TextView) findViewById(R.id.city_name);
		publishText = (TextView) findViewById(R.id.publish_text);
		weatherDespText = (TextView) findViewById(R.id.weather_desp);
		temp1text = (TextView) findViewById(R.id.temp1);
		temp2text = (TextView) findViewById(R.id.temp2);
		currentDateText = (TextView) findViewById(R.id.current_data);
		switchcity = (Button) findViewById(R.id.switch_city);
		refreshWeather = (Button) findViewById(R.id.refresh_weather);
		String countyCode = getIntent().getStringExtra("county_code");
		// ʵ���������
		AdView adView = new AdView(this, AdSize.FIT_SCREEN);
		// ��ȡҪǶ�������Ĳ���
		LinearLayout adLayout = (LinearLayout) findViewById(R.id.adLayout);
		// ����������뵽������
		adLayout.addView(adView);

		if (!TextUtils.isEmpty(countyCode)) {
			// ���ؼ�����ʱ��ȥ��ѯ����
			publishText.setText("ͬ����...");
			Log.i("LHD", "countyCode = " + countyCode);
			weatherInfoLayout.setVisibility(View.INVISIBLE);
			cityNameText.setVisibility(View.INVISIBLE);
			queryWeatherCode(countyCode);
		} else {
			// û���ؼ����ž�ֱ����ʾ��������
			showWeather();
		}
		switchcity.setOnClickListener(this);
		refreshWeather.setOnClickListener(this);
	}

	/**
	 * ��ѯ�ؼ���������Ӧ����������
	 * 
	 * @param countyCode
	 */
	private void queryWeatherCode(String countyCode) {
		String address = "http://www.weather.com.cn/data/list3/city"
				+ countyCode + ".xml";
		queryFromServer(address, "countyCode");
	}

	/**
	 * ��ѯ������������Ӧ������
	 * 
	 * @param weatherCode
	 */
	private void queryWeatherInfo(String weatherCode) {
		// ƴ����ַ��Ҫ���У���Ȼ������޷�����
		String address = "http://www.weather.com.cn/data/cityinfo/"
				+ weatherCode + ".html";
		queryFromServer(address, "weatherCode");
	}

	private void queryFromServer(final String address, final String type) {
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {

			@Override
			public void onFinish(final String response) {
				if ("countyCode".equals(type)) {
					if (!TextUtils.isEmpty(response)) {
						// �ӷ��������ص������н�������������
						Log.i("LHD", response.toString());
						String[] array = response.split("\\|");
						if (array != null && array.length == 2) {
							String weatherCode = array[1];
							Log.i("LHD", "weatherCode" + weatherCode);
							queryWeatherInfo(weatherCode);
						}
					}
				}
				if ("weatherCode".equals(type)) {
					Log.i("LHD", "else if weatherCode.equals(type))");
					// ������������ص�������Ϣ
					Utility.handleWeatherResponse(WeatherActivity.this,
							response);
					runOnUiThread(new Runnable() {
						public void run() {
							showWeather();
						}
					});
				}
			}

			@Override
			public void onError(Exception e) {
				runOnUiThread(new Runnable() {
					public void run() {
						publishText.setText("ͬ��ʧ��");
					}
				});
			}
		});
	}

	/**
	 * ��SharedPreferences�ļ��ж�ȡ�洢��������Ϣ������ʾ��������
	 */
	private void showWeather() {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		cityNameText.setText(prefs.getString("cityName", ""));
		temp1text.setText(prefs.getString("temp1", ""));
		temp2text.setText(prefs.getString("temp2", ""));
		weatherDespText.setText(prefs.getString("weather_desp", ""));
		publishText.setText("����" + prefs.getString("publish_time", "") + "����");
		currentDateText.setText(prefs.getString("current_date", ""));
		weatherInfoLayout.setVisibility(View.VISIBLE);
		cityNameText.setVisibility(View.VISIBLE);
		weatherstate = prefs.getString("weather_desp", "");
		weatherpic(weatherstate);
		// Ĭ��������̨�Զ����·���
		 Intent intent = new Intent(this, AutoUpdateService.class);
//		 startActivity(intent);
		 //BUG2 ��������ʹ��startService,��д����startActivity
		 startService(intent);
	}

	private void weatherpic(String weatherState) {
		if("��".equals(weatherState)){
			main_layout.setBackgroundResource(R.drawable.qing);
		}else if ("����".equals(weatherState)) {
			main_layout.setBackgroundResource(R.drawable.duoyun);
		}else if ("С��".equals(weatherState)) {
			main_layout.setBackgroundResource(R.drawable.weimei_de_yu);
		}else if(weatherState.contains("��")){
			main_layout.setBackgroundResource(R.drawable.storm);
		}else if (weatherState.contains("ת")) {
			main_layout.setBackgroundResource(R.drawable.duoyuzhuanqing);
		}else if (weatherState.contains("��")) {
			main_layout.setBackgroundResource(R.drawable.leiyu);
		}
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.switch_city:
			Intent intent = new Intent(this, ChooseAreaActivity.class);
			intent.putExtra("from_weather_activity", true);
			startActivity(intent);
			finish();
			break;
		case R.id.refresh_weather:
			publishText.setText("ͬ����...");
			SharedPreferences prefs = PreferenceManager
					.getDefaultSharedPreferences(this);
			String weatherCode = prefs.getString("weather_code", "");
			if (!TextUtils.isEmpty(weatherCode)) {
				queryWeatherInfo(weatherCode);
			}
			break;
		default:
			break;
		}
	}

}
