package com.example.testfastjson;

import java.util.List;

public class Teacher {
	
	private int id;
	private String name;
	
	private List<Student> students;
	

	/**
	 * 默认的构造方法必须不能省，不然不能解析
	 */
	public Teacher() {
		
	}
	public Teacher(int id,String name) {
		this.id = id;
		this.name = name;
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

	public List<Student> getStudents() {
		return students;
	}
	public void setStudents(List<Student> students) {
		this.students = students;
	}
	@Override
	public String toString() {
		return "Teacher [id=" + id + ", name=" + name + ", mStudents="
				+ students + "]";
	}
	
	
}