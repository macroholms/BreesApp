package com.example.breesapp.classes;

import com.example.breesapp.interfaces.DataItem;
import com.example.breesapp.models.BalanceHistory;

public class BalanceHistoryItem implements DataItem {
    private BalanceHistory balanceHistory;

    public BalanceHistoryItem(BalanceHistory balanceHistory) {
        this.balanceHistory = balanceHistory;
    }

    public BalanceHistory getBalanceHistory() {
        return balanceHistory;
    }

    @Override
    public int getItemType() {
        return 1;
    }
}