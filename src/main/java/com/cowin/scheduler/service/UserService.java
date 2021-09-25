package com.cowin.scheduler.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.cowin.scheduler.dao.CowinDao;
import com.cowin.scheduler.model.CenterModel;
import com.cowin.scheduler.model.Centers;
import com.cowin.scheduler.model.Session;
import com.cowin.scheduler.util.MailUtil;

@Service
public class UserService {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
	@Autowired
	private CowinDao cowinDao;

	@Autowired
	private MailUtil mailUtil;

	private final static int CHECK_INTERVAL = 10;

	@Scheduled(fixedRate = 1000 * 60 * CHECK_INTERVAL)
	@Scheduled(cron= "0 2,5,10 8,12,16,20 * * * ")
	public void checkCowinSlotAvailibility() {
		LOGGER.info("start check !");
		CenterModel centreModel = new CenterModel();
		centreModel = cowinDao.call();
		Map<Integer, List<String>> cowinData = getAvailableSlot(centreModel);
		if (cowinData != null) {
			LOGGER.info(cowinData.toString());
			mailUtil.sendMail(cowinData);
		}
	}

	private Map<Integer, List<String>> getAvailableSlot(CenterModel centreModel) {
		Map<Integer, List<String>> cowinSlotData = null;
		if (!centreModel.equals(null)) {
			cowinSlotData = new HashMap<Integer, List<String>>();
			for (Centers center : centreModel.getCenters()) {
				if (!center.equals(null) && getFreeSlot(center) == true) {
					List<String> centreInfo = new ArrayList<>();
					centreInfo.add(center.getName());
					centreInfo.add(center.getAddress());
					centreInfo.add(Integer.toString(center.getPincode()));
					List<String> sessionData = new ArrayList<>();
					int doseCapacity = 0;
					for (Session session : center.getSessions()) {
						sessionData.add("date: " + session.getDate());
						sessionData.add("age: " + Integer.toString(session.getMin_age_limit()));
						sessionData.add("doseCapacity: " + Integer.toString(session.getAvailable_capacity_dose2()));
						doseCapacity = doseCapacity + session.getAvailable_capacity_dose2();
					}
					centreInfo.add(sessionData.toString());
					centreInfo.add(Integer.toString(doseCapacity));
					cowinSlotData.put(center.getCenter_id(), centreInfo);
				}

			}
		}
		return cowinSlotData;

	}

	private boolean getFreeSlot(Centers center) {
		boolean slot = false;
		if (center.getFee_type().equals("Free")) {
			for (Session session : center.getSessions()) {
				if (session.getAvailable_capacity() > 0 && session.getAvailable_capacity_dose2() > 0
						&& session.getVaccine().equals("COVISHIELD") && session.getMin_age_limit() == 18) {
					slot = true;
					break;
				}
			}
		}
		return slot;
	}

}
