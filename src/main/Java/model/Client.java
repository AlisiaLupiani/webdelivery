package model;

import framework.data.DataItem;

public interface Client extends User, DataItem<Integer> {

	public String getPhone();
	public void setPhone(String phone);

	public String getAddress();
	public void setAddress(String address);
	
}

