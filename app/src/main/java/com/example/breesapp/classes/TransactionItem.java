package com.example.breesapp.classes;

import com.example.breesapp.interfaces.DataItem;
import com.example.breesapp.models.Transaction;

public class TransactionItem implements DataItem {
    private Transaction transaction;

    public TransactionItem(Transaction transaction) {
        this.transaction = transaction;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    @Override
    public int getItemType() {
        return 0; // Тип элемента для транзакций
    }
}