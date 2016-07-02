package com.broadchance.entity;

import java.util.List;

public class Contact {
	private String phoneNo;
	private List<String> phoneNumbers;
	private String name;
	private String letter;
	private boolean newAdd;
	private boolean deleteStatus;
	private boolean deleteButton;
	private boolean isDeleteButtonSel;
	private boolean addButton;
	private boolean isAddButtonSel;
	private long ID;

	public boolean isDeleteButtonSel() {
		return isDeleteButtonSel;
	}

	public void setDeleteButtonSel(boolean isDeleteButtonSel) {
		this.isDeleteButtonSel = isDeleteButtonSel;
		this.isAddButtonSel = !isDeleteButtonSel;
	}

	public boolean isAddButtonSel() {
		return isAddButtonSel;
	}

	public void setAddButtonSel(boolean isAddButtonSel) {
		this.isAddButtonSel = isAddButtonSel;
		this.isDeleteButtonSel = !isAddButtonSel;
	}

	public long getID() {
		return ID;
	}

	public void setID(long _ID) {
		this.ID = _ID;
	}

	public Contact() {

	}

	public String getLetter() {
		return letter;
	}

	public void setLetter(String letter) {
		this.letter = letter;
	}

	public boolean isDeleteStatus() {
		return deleteStatus;
	}

	public List<String> getPhoneNumbers() {
		return phoneNumbers;
	}

	public void setPhoneNumbers(List<String> phoneNumbers) {
		this.phoneNumbers = phoneNumbers;
	}

	public void setDeleteStatus(boolean deleteStatus) {
		this.deleteStatus = deleteStatus;
	}

	public boolean isDeleteButton() {
		return deleteButton;
	}

	public void setDeleteButton(boolean deleteButton) {
		this.deleteButton = deleteButton;
	}

	public boolean isAddButton() {
		return addButton;
	}

	public void setAddButton(boolean addButton) {
		this.addButton = addButton;
	}

	public boolean isNewAdd() {
		return newAdd;
	}

	public void setNewAdd(boolean newAdd) {
		this.newAdd = newAdd;
	}

	public String getPhoneNo() {
		return phoneNo;
	}

	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
