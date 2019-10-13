package com.rabobank.customer.statementprocessor.validator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.rabobank.customer.statementprocessor.model.Record;

@Service
public class ValidateService {

	List<Record> inValidRecords = null;
	private Logger log = LoggerFactory.getLogger(ValidateService.class);

	public List<Record> trancationReferenceValidator(List<Record> recordsList, boolean endBalanceValidator) {
		log.info("Invalid records found in transaction reference validator :");
		inValidRecords = new ArrayList<Record>();
		Set<Long> uniqueReferenceIds = new HashSet<Long>();
		recordsList.forEach(record -> {
			if (!uniqueReferenceIds.add(record.getTransactionReference())) {
				inValidRecords.add(record);
				log.info(record.toString());
			}

		});
		if (endBalanceValidator) {
			recordsList.removeAll(inValidRecords);
			return endBalanceValidator(recordsList);
		}
		return inValidRecords;
	}

	public List<Record> endBalanceValidator(List<Record> recordsList) {
		if (inValidRecords == null)
			inValidRecords = new ArrayList<Record>();
		log.info("Invalid records found in endbalance validator :");
		recordsList.forEach(record -> {
			if (record != null) {
				BigDecimal totalBalance = record.getStartBalance().add(record.getMutation());
				if (record.getEndBalance().compareTo(totalBalance) != 0) {
					inValidRecords.add(record);
					log.info(record.toString());

				}
			}
		});

		return inValidRecords;
	}

}
