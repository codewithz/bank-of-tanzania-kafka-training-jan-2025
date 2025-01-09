package com.codewithz.services;

import com.codewithz.model.Transaction;
import org.springframework.stereotype.Service;

@Service
public class TransactionValidationService {

    public Transaction validateAndTransform(Transaction transaction) {
        // Validate amount
        if (transaction.getAmount() <= 100) {
            throw new IllegalArgumentException("Transaction amount must be greater than 100 USD");
        }

        // Transform transaction type code to type
        String txType;
        switch (transaction.getTxTypeCode()) {
            case "1":
                txType = "Savings";
                break;
            case "2":
                txType = "Credit";
                break;
            case "3":
                txType = "Checking";
                break;
            default:
                throw new IllegalArgumentException("Invalid transaction type code");
        }
        transaction.setTxTypeCode(txType);

        return transaction;
    }
}
