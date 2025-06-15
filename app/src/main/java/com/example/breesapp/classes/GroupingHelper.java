package com.example.breesapp.classes;

import android.content.Context;

import com.example.breesapp.R;
import com.example.breesapp.interfaces.DataItem;
import com.example.breesapp.models.GroupedItem;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class GroupingHelper {
    public List<GroupedItem> groupByDay(List<DataItem> items, Context context) {
        List<GroupedItem> groupedItems = new ArrayList<>();
        String currentHeader = "";

        for (DataItem item : items) {
            String createdAt = "";
            if (item.getItemType() == 0) {
                createdAt = ((TransactionItem)item).getTransaction().getCreatedAt();
            } else {
                createdAt = ((BalanceHistoryItem)item).getBalanceHistory().getCreatedAt();
            }

            String header = getDayHeader(createdAt, context);
            if (!header.equals(currentHeader)) {
                groupedItems.add(new GroupedItem(header));
                currentHeader = header;
            }

            groupedItems.add(new GroupedItem(item));
        }

        return groupedItems;
    }

    private String getDayHeader(String isoDate, Context context) {
        try {
            DateTimeFormatter inputFormatter = DateTimeFormatter.ISO_INSTANT;
            Instant instant = Instant.from(inputFormatter.parse(isoDate));
            LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
            LocalDateTime now = LocalDateTime.now();

            if (localDateTime.toLocalDate().isEqual(now.toLocalDate())) {
                return context.getString(R.string.today);
            } else if (localDateTime.toLocalDate().isEqual(now.minusDays(1).toLocalDate())) {
                return context.getString(R.string.yesterday);
            } else {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy");
                return formatter.format(localDateTime);
            }
        } catch (Exception e) {
            return isoDate;
        }
    }
}
