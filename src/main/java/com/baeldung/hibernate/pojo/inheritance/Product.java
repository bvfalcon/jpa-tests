package com.baeldung.hibernate.pojo.inheritance;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "product_type", discriminatorType = DiscriminatorType.INTEGER)
public class Product
{
	@Id
	private long id;
	
	private String field1;
	private String field2;
	private String field3;
	private String field4;
	
	public long getId()
	{
		return id;
	}
	
	public void setId(long id)
	{
		this.id = id;
	}
	
	public String getField1()
	{
		return field1;
	}
	
	public void setField1(String field1)
	{
		this.field1 = field1;
	}
	
	public String getField2()
	{
		return field2;
	}
	
	public void setField2(String field2)
	{
		this.field2 = field2;
	}
	
	public String getField3()
	{
		return field3;
	}
	
	public void setField3(String field3)
	{
		this.field3 = field3;
	}
	
	public String getField4()
	{
		return field4;
	}
	
	public void setField4(String field4)
	{
		this.field4 = field4;
	}
}