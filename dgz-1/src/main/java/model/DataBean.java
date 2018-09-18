package model;

import java.io.Serializable;

public class DataBean implements Serializable {

	public String fileName;

	public String data;

	public String operator;

	public String location;

	public String liftid;

	public DataBean(String fileName, String liftid, String location, String operator,String data ) {
		this.fileName = fileName;
		this.liftid = liftid;
		this.location = location;
		this.operator = operator;
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
}
