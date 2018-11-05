package com.axeac.android.sdk.utils;

import com.axeac.android.sdk.tools.Property;

public class NavCompBean {
	
	private String navMeip;
	private Property navItems;
	
	public NavCompBean(String navMeip, Property navItems){
		this.navMeip = navMeip;
		this.navItems = navItems;
	}
	
	public String getNavMeip() {
		return navMeip;
	}
	
	public void setNavMeip(String navMeip) {
		this.navMeip = navMeip;
	}
	
	public Property getNavItems() {
		return navItems;
	}
	
	public void setNavItems(Property navItems) {
		this.navItems = navItems;
	}
}