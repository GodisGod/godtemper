package com.example.godtemper.util;

import com.example.godtemper.db.GodTemperDB;
import com.example.godtemper.model.City;
import com.example.godtemper.model.County;
import com.example.godtemper.model.Province;

import android.R.bool;
import android.text.TextUtils;

public class Utility {
	/**
	 * 解析和处理服务器返回的省级数据 服务器返回的省市县数据都是“代号|城市，代号|城市”这种格式的，需要处理
	 */
	public synchronized static boolean handleProvincesResponse(
			GodTemperDB godTemperDB, String response) {
		if (!TextUtils.isEmpty(response)) {
			String[] allProvince = response.split(",");// 设置分隔符
			if (allProvince != null && allProvince.length > 0) {
				for (String p : allProvince) {
					String[] array = p.split("\\|");
					Province province = new Province();
					province.setProvinceCode(array[0]);
					province.setProvinceName(array[1]);
					// 将解析出来的数据存储到Province表
					godTemperDB.saveProvince(province);
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * 解析和处理服务器返回的市级数据
	 */
	public static boolean handleCitiesResponse(GodTemperDB godTemperDB,
			String response, int provinceId) {
		if (!TextUtils.isEmpty(response)) {
			String[] allcities = response.split(",");
			if (allcities != null && allcities.length > 0) {
				for (String c : allcities) {
					String[] array = c.split("\\|");
					City city = new City();
					city.setCityCode(array[0]);
					city.setCityName(array[1]);
					city.setProvinceId(provinceId);
					// 将解析出来的数据存储到City表
					godTemperDB.saveCity(city);
				}
				return true;
			}
		}
		return true;
	}

	/**
	 * 解析和处理服务器返回的县级数据
	 */
	public static boolean handleCountiesResponse(GodTemperDB godTemperDB,
			String response, int cityId) {
		if (!TextUtils.isEmpty(response)) {
			String[] allCounties = response.split(",");
			if (allCounties != null && allCounties.length > 0) {
				for (String c : allCounties) {
					String[] array = c.split("\\|");
					County county = new County();
					county.setCountyCode(array[0]);
					county.setCountyName(array[1]);
					county.setCityId(cityId);
					// 将解析出来的数据存储到County表
					godTemperDB.saveCounty(county);
				}
				return true;
			}
		}
		return false;
	}

}
