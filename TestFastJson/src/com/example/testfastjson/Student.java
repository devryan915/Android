package com.example.testfastjson;

public class Student {
	
	private int id;
	private String name;
	private int age;
	
	/**
	 * 默认的构造方法必须不能省，不然不能解析
	 */
	
	public Student(){
		
	}
	public Student(int id,String name,int age) {
		this.id = id;
		this.name = name;
		this.age = age;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}
	
	@Override
	public String toString() {
		return "Student [id=" + id + ", name=" + name + ", age=" + age + "]";
	}
	
}