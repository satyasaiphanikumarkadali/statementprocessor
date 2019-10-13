package com.rabobank.customer.statementprocessor.parserhandler;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.rabobank.customer.statementprocessor.model.Record;

public class XMLParserHandler extends DefaultHandler {

	private List<Record> recordList = new ArrayList<Record>();
	private Record record = null;
	private StringBuilder data = null;

	public List<Record> getRecordList() {
		return recordList;
	}

	boolean isAccountNumber = false;
	boolean isDescription = false;
	boolean isStartBalance = false;
	boolean isMutation = false;
	boolean isEndBalance = false;

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (qName.equalsIgnoreCase("record")) {
			String reference = attributes.getValue("reference");
			record = new Record();
			record.setTransactionReference(Long.parseLong(reference));
		} else if (qName.equalsIgnoreCase("accountNumber")) {
			isAccountNumber = true;
		} else if (qName.equalsIgnoreCase("description")) {
			isDescription = true;
		} else if (qName.equalsIgnoreCase("startBalance")) {
			isStartBalance = true;
		} else if (qName.equalsIgnoreCase("mutation")) {
			isMutation = true;
		} else if (qName.equalsIgnoreCase("endBalance")) {
			isEndBalance = true;
		}
		this.data = new StringBuilder();
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {

		if (isAccountNumber) {
			record.setAccountNumber(data.toString());
			isAccountNumber = false;
		} else if (isDescription) {
			record.setDescription(data.toString());
			isDescription = false;
		} else if (isStartBalance) {
			record.setStartBalance(new BigDecimal(data.toString()));
			isStartBalance = false;
		} else if (isMutation) {
			record.setMutation(new BigDecimal(data.toString()));
			isMutation = false;
		} else if (isEndBalance) {
			record.setEndBalance(new BigDecimal(data.toString()));
			isEndBalance = false;
		}

		if (qName.equalsIgnoreCase("record")) {
			recordList.add(record);
		}
	}

	@Override
	public void characters(char ch[], int start, int length) throws SAXException {
		data.append(new String(ch, start, length));
	}
}
