package com.cowin.scheduler.model;

import java.util.List;


public class CenterModel {
	private List<Centers> centers;

	public List<Centers> getCenters() {
		return centers;
	}

	public void setCenters(List<Centers> centers) {
		this.centers = centers;
	}

	@Override
	public String toString() {
		return "CenterModel [centers=" + centers + "]";
	}
	

	
}
