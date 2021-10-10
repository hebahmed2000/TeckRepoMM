package com.evision.transaction.service;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.evision.transaction.models.dto.TransactionDto;

@Service
public class TransactionService {

	public final String WITHDRAW_TRANSACTION_TYPE = "withdraw";
    public final String DEPOSIT_TRANSACTION_TYPE = "deposit";

    public void processTransactions(){

    }


    public Predicate<TransactionDto> isDepositTransaction() {
        return p -> DEPOSIT_TRANSACTION_TYPE.equals(p.getTransactionType());
    }
    public Predicate<TransactionDto> isWithdrawTransaction() {
        return p -> WITHDRAW_TRANSACTION_TYPE.equals(p.getTransactionType());
    }

    public int countWithdrawTransactions( List<TransactionDto> transactionList ) {
        return ((List)transactionList.stream().filter(isWithdrawTransaction()).collect(Collectors.<TransactionDto>toList())).size();
    }

    public int countDepositTransactions( List<TransactionDto> transactionList ) {
        return ((List)transactionList.stream().filter(isDepositTransaction()).collect(Collectors.<TransactionDto>toList())).size();
    }

    
}
