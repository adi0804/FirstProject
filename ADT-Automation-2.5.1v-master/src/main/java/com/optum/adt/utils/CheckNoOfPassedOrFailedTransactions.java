package com.optum.adt.utils;

import java.util.List;

import org.springframework.stereotype.Component;

import com.optum.adt.models.TransactionObjectModel;
import com.optum.adt.models.TransactionResponseModel;

@Component
public class CheckNoOfPassedOrFailedTransactions {

	public int checkNoOfSuccessfulTransactions(
			List<TransactionResponseModel> transactionResponseModelList) {
		
		int successfulTransactions = 0;

		if (transactionResponseModelList != null
				&& !transactionResponseModelList.isEmpty()) {

			for (TransactionResponseModel transactionResponseModel : transactionResponseModelList) {

				List<TransactionObjectModel> transactionObjectModelList = transactionResponseModel
						.getTransactionObjectModelList();

				TransactionObjectModel transactionObjectModel = transactionObjectModelList
						.stream().filter(x -> x.getStatus().equals("F"))
						.findAny().orElse(null);

				if (transactionObjectModel == null) {
					successfulTransactions += 1;
				}

			}
		}

		return successfulTransactions;

	}

}
