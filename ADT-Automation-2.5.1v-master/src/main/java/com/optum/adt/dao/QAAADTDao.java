package com.optum.adt.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class QAAADTDao {
	@Autowired
	JdbcTemplate jdbcTempalte;
	@Autowired
	private Environment environment;

	public List<Map<String, Object>> getADTSDRMappingsFromDB() {

		// List<ADTSDRMappingModel> aDTSDRMappingModel = new ArrayList<>();

		List<Map<String, Object>> list = null;
		try {
			list = jdbcTempalte.queryForList("select * from " + environment.getProperty("mapping.table"));
		} catch (Exception e) {

			e.printStackTrace();
		}
		return list;
	}

	public List<Map<String, Object>> getFilesToBeProcessed(String qaResourceName) {
		List<Map<String, Object>> list = null;
		try {
			list = jdbcTempalte.queryForList("select * from test." + environment.getProperty("processConfig.table")
					+ " where status <> 'Completed' and qa_resource_name='" + qaResourceName + "'");
		} catch (Exception e) {

			e.printStackTrace();

		}
		return list;

	}

	public int updateProcessConfig(int total_hl7_transactions, int total_flat_file_transactions,
			int succesful_transactions, int diff_in_transactions, int failed_transactions, int file_id) {

		String query = "UPDATE " + environment.getProperty("processConfig.table") + " SET total_hl7_transactions="
				+ total_hl7_transactions + ",total_flat_file_transactions=" + total_flat_file_transactions
				+ ",successful_transactions=" + succesful_transactions + ",diff_in_transactions=" + diff_in_transactions
				+ ",failed_transactions=" + failed_transactions + ",status='Completed',InsertTimeStamp='" + new Date()
				+ "' where FileId=" + file_id;

		try {
			jdbcTempalte.execute(query);

		} catch (Exception e) {

			e.printStackTrace();
		}

		return failed_transactions;

	}

}
