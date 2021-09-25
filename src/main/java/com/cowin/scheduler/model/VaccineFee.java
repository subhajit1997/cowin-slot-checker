package com.cowin.scheduler.model;

public class VaccineFee {
    private String vaccine;
    private String fee;
    
	public String getVaccine() {
		return vaccine;
	}
	public void setVaccine(String vaccine) {
		this.vaccine = vaccine;
	}
	public String getFee() {
		return fee;
	}
	public void setFee(String fee) {
		this.fee = fee;
	}
	@Override
	public String toString() {
		return "VaccineFee [vaccine=" + vaccine + ", fee=" + fee + "]";
	}
    
    
}
