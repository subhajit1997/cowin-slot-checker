package com.cowin.scheduler.dao;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.cowin.scheduler.model.CenterModel;
import com.cowin.scheduler.util.HttpClient;
import com.cowin.scheduler.util.JsonUtil;

@Component
public class CowinDao {

	private static final Logger LOGGER = LoggerFactory.getLogger(CowinDao.class);

	@Value("${cowin.availablity.host}")
	public String hostName;

	@Value("${cowin.availablity.district.path}")
	public String hostDistrictPath;

	@Value("${cowin.availablity.district.district_id}")
	public String districtId;

	@Autowired
	private HttpClient httpClient;
	
	public URL getSlotData() {
		URL url = null;

		try {
			url = new URL("https://" + hostName + hostDistrictPath + "?" + "district_id=" + districtId + "&" + "date="
					+ getTodaysDate());
			LOGGER.info(url.toString());
		} catch (Exception e) {

		}
		return url;
	}

	private String getTodaysDate() {
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
		Date date = new Date();
		return formatter.format(date);
	}

	public CenterModel call() {
		String jsonResponse = null;
		try {
			jsonResponse = httpClient.httpGet(getSlotData());
		} catch (Exception e) {
			LOGGER.error("Error getting response",e);
		}
		return JsonUtil.convertToEntityObject(jsonResponse, CenterModel.class);
	}
}
