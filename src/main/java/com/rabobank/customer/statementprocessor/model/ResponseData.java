package com.rabobank.customer.statementprocessor.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class ResponseData {

	private List<Record> inValidRecords;
	private static List<String> errors = new ArrayList<String>();
	
	public List<Record> getInValidRecords() {
		return inValidRecords;
	}
	public void setInValidRecords(List<Record> inValidRecords) {
		this.inValidRecords = inValidRecords;
	}
	public static void addErrors(String error) {
		errors.add(error);
	}
	public List<String> getErrors() {
		return errors;
	}
	
	
}
