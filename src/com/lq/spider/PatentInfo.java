package com.lq.spider;

public class PatentInfo {
	private int id;
	private String sysid;
	public PatentInfo(int id, String an) {
		super();
		this.id = id;
		this.an = an;
	}
	public PatentInfo() {
		super();
	}
	public PatentInfo(int id, String sysid, String an, String ad, String pn, String pd, String db, String shencha) {
		super();
		this.id = id;
		this.sysid = sysid;
		this.an = an;
		this.ad = ad;
		this.pn = pn;
		this.pd = pd;
		this.db = db;
		this.shencha = shencha;
	}
	private String an;
	private String ad;
	private String pn;
	private String pd;
	private String db;
	private String shencha;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getSysid() {
		return sysid;
	}
	public void setSysid(String sysid) {
		this.sysid = sysid;
	}
	public String getAn() {
		return an;
	}
	public void setAn(String an) {
		this.an = an;
	}
	public String getAd() {
		return ad;
	}
	public void setAd(String ad) {
		this.ad = ad;
	}
	public String getPn() {
		return pn;
	}
	public void setPn(String pn) {
		this.pn = pn;
	}
	public String getPd() {
		return pd;
	}
	public void setPd(String pd) {
		this.pd = pd;
	}
	public String getDb() {
		return db;
	}
	public void setDb(String db) {
		this.db = db;
	}
	public String getShencha() {
		return shencha;
	}
	public void setShencha(String shencha) {
		this.shencha = shencha;
	}
	
}
