package model;

import java.io.Serializable;

public class DataBean implements Serializable {

	public String fileName;

	public String data;

	public String operator;

	public String location;

	public String liftid;

	public String company;

	public String supplement;

	public DataBean(String fileName, String liftid, String location, String operator, String company,String supplement,String data ) {
		this.fileName = fileName;
		this.liftid = liftid;
		this.location = location;
		this.operator = operator;
		this.company = company;
		this.supplement = supplement;
		this.data = data;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getLiftid() {
		return liftid;
	}

	public void setLiftid(String liftid) {
		this.liftid = liftid;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getSupplement() {
		return supplement;
	}

	public void setSupplement(String supplement) {
		this.supplement = supplement;
	}
}
