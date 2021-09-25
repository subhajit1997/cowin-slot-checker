package com.cowin.scheduler.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
	
	@Value("${cowin.user.doseNumber}")
	private int userVaccineDoseNum;
	
	@Value("${cowin.user.fee_type}")
	private String userVaccineFeeType;
	
	@Value("${cowin.user.vaccineType}")
	private String userVaccineType;
	
	@Value("${cowin.user.minAge}")
	private int userMinAge;

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
				if (!center.equals(null) && getSlot(center) == true) {
					List<String> centreInfo = new ArrayList<>();
					centreInfo.add(center.getName());
					centreInfo.add(center.getAddress());
					centreInfo.add(Integer.toString(center.getPincode()));
					List<String> sessionData = new ArrayList<>();
					int getDoseCapacity=0;
					int totalDose=0;
					for (Session session : center.getSessions()) {
						
						if(userVaccineDoseNum==1)
							getDoseCapacity=session.getAvailable_capacity_dose1();
						else
							getDoseCapacity=session.getAvailable_capacity_dose2();
						sessionData.add("date: " + session.getDate());
						sessionData.add("age: " + Integer.toString(session.getMin_age_limit()));
						sessionData.add("doseCapacity: " + getDoseCapacity);
						totalDose +=getDoseCapacity;
					}
					centreInfo.add(sessionData.toString());
					centreInfo.add(Integer.toString(totalDose));
					cowinSlotData.put(center.getCenter_id(), centreInfo);
				}

			}
		}
		return cowinSlotData;

	}

	private boolean getSlot(Centers center) {
		boolean slot = false;
		if (center.getFee_type().equals(userVaccineFeeType)) {
			for (Session session : center.getSessions()) {
				int getDoseCapacity=0;
				if(userVaccineDoseNum==1)
					getDoseCapacity=session.getAvailable_capacity_dose1();
				else
					getDoseCapacity=session.getAvailable_capacity_dose2();
				if (session.getAvailable_capacity() > 0 && getDoseCapacity > 0
						&& session.getVaccine().equals(userVaccineType) && session.getMin_age_limit() >= userMinAge) {
					slot = true;
					break;
				}
			}
		}
		return slot;
	}

}
