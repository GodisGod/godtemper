package com.example.godtemper.util;

import com.example.godtemper.db.GodTemperDB;
import com.example.godtemper.model.City;
import com.example.godtemper.model.County;
import com.example.godtemper.model.Province;

import android.R.bool;
import android.text.TextUtils;

public class Utility {
	/**
	 * �����ʹ�����������ص�ʡ������ ���������ص�ʡ�������ݶ��ǡ�����|���У�����|���С����ָ�ʽ�ģ���Ҫ����
	 */
	public synchronized static boolean handleProvincesResponse(
			GodTemperDB godTemperDB, String response) {
		if (!TextUtils.isEmpty(response)) {
			String[] allProvince = response.split(",");// ���÷ָ���
			if (allProvince != null && allProvince.length > 0) {
				for (String p : allProvince) {
					String[] array = p.split("\\|");
					Province province = new Province();
					province.setProvinceCode(array[0]);
					province.setProvinceName(array[1]);
					// ���������������ݴ洢��Province��
					godTemperDB.saveProvince(province);
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * �����ʹ�����������ص��м�����
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
					// ���������������ݴ洢��City��
					godTemperDB.saveCity(city);
				}
				return true;
			}
		}
		return true;
	}

	/**
	 * �����ʹ�����������ص��ؼ�����
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
					// ���������������ݴ洢��County��
					godTemperDB.saveCounty(county);
				}
				return true;
			}
		}
		return false;
	}

}
