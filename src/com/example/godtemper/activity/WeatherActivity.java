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
	 * 切换城市按钮
	 */
	private Button switchcity;
	/**
	 * 更新天气按钮
	 */
	private Button refreshWeather;
	/**
	 * 主界面布局
	 */
	private RelativeLayout main_layout;
	/**
	 * 天气状态
	 */
	private String weatherstate;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_layout);
		// 初始化各控件
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
		// 实例化广告条
		AdView adView = new AdView(this, AdSize.FIT_SCREEN);
		// 获取要嵌入广告条的布局
		LinearLayout adLayout = (LinearLayout) findViewById(R.id.adLayout);
		// 将广告条加入到布局中
		adLayout.addView(adView);

		if (!TextUtils.isEmpty(countyCode)) {
			// 有县级代号时就去查询天气
			publishText.setText("同步中...");
			Log.i("LHD", "countyCode = " + countyCode);
			weatherInfoLayout.setVisibility(View.INVISIBLE);
			cityNameText.setVisibility(View.INVISIBLE);
			queryWeatherCode(countyCode);
		} else {
			// 没有县级代号就直接显示本地天气
			showWeather();
		}
		switchcity.setOnClickListener(this);
		refreshWeather.setOnClickListener(this);
	}

	/**
	 * 查询县级代号所对应的天气代号
	 * 
	 * @param countyCode
	 */
	private void queryWeatherCode(String countyCode) {
		String address = "http://www.weather.com.cn/data/list3/city"
				+ countyCode + ".xml";
		queryFromServer(address, "countyCode");
	}

	/**
	 * 查询天气代号所对应的天气
	 * 
	 * @param weatherCode
	 */
	private void queryWeatherInfo(String weatherCode) {
		// 拼接网址不要换行，不然会出错，无法访问
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
						// 从服务器返回的数据中解析出天气代号
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
					// 处理服务器返回的天气信息
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
						publishText.setText("同步失败");
					}
				});
			}
		});
	}

	/**
	 * 从SharedPreferences文件中读取存储的天气信息，并显示到界面上
	 */
	private void showWeather() {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		cityNameText.setText(prefs.getString("cityName", ""));
		temp1text.setText(prefs.getString("temp1", ""));
		temp2text.setText(prefs.getString("temp2", ""));
		weatherDespText.setText(prefs.getString("weather_desp", ""));
		publishText.setText("今天" + prefs.getString("publish_time", "") + "发布");
		currentDateText.setText(prefs.getString("current_date", ""));
		weatherInfoLayout.setVisibility(View.VISIBLE);
		cityNameText.setVisibility(View.VISIBLE);
		weatherstate = prefs.getString("weather_desp", "");
		weatherpic(weatherstate);
		// 默认启动后台自动更新服务
		 Intent intent = new Intent(this, AutoUpdateService.class);
//		 startActivity(intent);
		 //BUG2 启动服务使用startService,误写成了startActivity
		 startService(intent);
	}

	private void weatherpic(String weatherState) {
		if("晴".equals(weatherState)){
			main_layout.setBackgroundResource(R.drawable.qing);
		}else if ("多云".equals(weatherState)) {
			main_layout.setBackgroundResource(R.drawable.duoyun);
		}else if ("小雨".equals(weatherState)) {
			main_layout.setBackgroundResource(R.drawable.weimei_de_yu);
		}else if(weatherState.contains("风")){
			main_layout.setBackgroundResource(R.drawable.storm);
		}else if (weatherState.contains("转")) {
			main_layout.setBackgroundResource(R.drawable.duoyuzhuanqing);
		}else if (weatherState.contains("雷")) {
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
			publishText.setText("同步中...");
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
