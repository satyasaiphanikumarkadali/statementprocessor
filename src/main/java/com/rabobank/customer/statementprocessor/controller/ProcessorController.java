package com.rabobank.customer.statementprocessor.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.rabobank.customer.statementprocessor.model.Record;
import com.rabobank.customer.statementprocessor.model.ResponseData;
import com.rabobank.customer.statementprocessor.parserhandler.ParserService;

@RestController
public class ProcessorController {

	@Autowired
	private ParserService parserService;

	@Autowired
	private ResponseData responseData;

	private Logger log = LoggerFactory.getLogger(ProcessorController.class);

	@PostMapping("/validate")
	public ResponseEntity<?> handleUploadedFile(@RequestParam("file") MultipartFile file) {
		try {
			responseData.getErrors().clear();
			if (file != null) {
				String fileName = file.getOriginalFilename();
				List<Record> inValidRecords = null;
				if (fileName.toLowerCase().contains(".xml")) {
					inValidRecords = parserService.parsexmlFile(file);
				} else if (fileName.toLowerCase().contains(".csv")) {
					inValidRecords = parserService.parsecsvFile(file);
				} else
					ResponseData.addErrors("Invaid file format, please upload csv/xml file only");
				responseData.setInValidRecords(inValidRecords);

			}
		} catch (Exception e) {
			log.error(e.getMessage());
			ResponseData.addErrors("Exception occured while calling parser service");
		}
		return new ResponseEntity<ResponseData>(responseData, HttpStatus.ACCEPTED);

	}

}
