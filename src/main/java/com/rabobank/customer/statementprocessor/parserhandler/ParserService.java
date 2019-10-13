package com.rabobank.customer.statementprocessor.parserhandler;

import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.rabobank.customer.statementprocessor.model.Record;
import com.rabobank.customer.statementprocessor.model.ResponseData;
import com.rabobank.customer.statementprocessor.validator.ValidateService;

@Service
public class ParserService {
	Logger log = LoggerFactory.getLogger(ParserService.class);

	@Autowired
	private ValidateService validateService;

	private List<Record> recordsList = null;

	public List<Record> parsexmlFile(MultipartFile file) {
		List<Record> invalidRecords = null;
		try {
			SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
			SAXParser saxParser = saxParserFactory.newSAXParser();
			XMLParserHandler parserHandler = new XMLParserHandler();
			saxParser.parse(file.getInputStream(), parserHandler);
			recordsList = parserHandler.getRecordList();
			invalidRecords = validateService.trancationReferenceValidator(recordsList, true);
		}

		catch (Exception e) {
			log.error("Exception occured while parsing XML file" + e.getMessage());
			ResponseData.addErrors("Exception occured while parsing XML file, please upload correct file in correct format");
		}
		return invalidRecords;
	}

	public List<Record> parsecsvFile(MultipartFile file) {
		List<Record> invalidRecords = null;
		try {
			CSVParser records = CSVFormat.EXCEL.withHeader().parse(new InputStreamReader(file.getInputStream()));
			recordsList = new ArrayList<Record>();
			records.forEach(record -> {
				if (record != null) {
					Record rec = new Record();
					rec.setTransactionReference(Long.parseLong(record.get(0)));
					rec.setAccountNumber(record.get(1));
					rec.setDescription(record.get(2));
					rec.setStartBalance(new BigDecimal(record.get(3)));
					rec.setMutation(new BigDecimal(record.get(4)));
					rec.setEndBalance(new BigDecimal(record.get(5)));
					recordsList.add(rec);
				}
			});
			invalidRecords = validateService.trancationReferenceValidator(recordsList, true);
		} catch (Exception e) {
			log.error("Exception occured while parsing CSV file" + e.getMessage());
			ResponseData.addErrors("Exception occured while parsing CSV file, please upload file in correct format");

		}
		return invalidRecords;
	}

}
