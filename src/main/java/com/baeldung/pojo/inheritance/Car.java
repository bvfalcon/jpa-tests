package com.baeldung.pojo.inheritance;

import javax.persistence.Entity;

@Entity
public class Car extends Vehicle implements Item
{
	private String field5;
	private String field6;
	private String field7;
	private String field8;
	
	public String getField5()
	{
		return field5;
	}
	
	public void setField5(String field5)
	{
		this.field5 = field5;
	}
	
	public String getField6()
	{
		return field6;
	}
	
	public void setField6(String field6)
	{
		this.field6 = field6;
	}
	
	public String getField7()
	{
		return field7;
	}
	
	public void setField7(String field7)
	{
		this.field7 = field7;
	}
	
	public String getField8()
	{
		return field8;
	}
	
	public void setField8(String field8)
	{
		this.field8 = field8;
	}
}