package com.example.breesapp.adapters;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.breesapp.R;
import com.example.breesapp.classes.BalanceHistoryItem;
import com.example.breesapp.classes.NumberFormatter;
import com.example.breesapp.classes.SessionManager;
import com.example.breesapp.classes.SupabaseClient;
import com.example.breesapp.classes.TransactionItem;
import com.example.breesapp.interfaces.DataItem;
import com.example.breesapp.models.BalanceHistory;
import com.example.breesapp.models.GroupedItem;
import com.example.breesapp.models.ProfileResponse;
import com.example.breesapp.models.Transaction;
import com.google.gson.Gson;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GroupedTransactionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public interface onTransacClickListener{
        void OnItemClick(DataItem item, String profileName);
    }

    private onTransacClickListener onItemClickListener;
    private List<GroupedItem> originalItems;
    private List<GroupedItem> filteredByDateItems;
    private List<GroupedItem> groupedItems;
    private Context context;
    private ProfileResponse[] profiles;
    private String currentQuery = "";

    public GroupedTransactionAdapter(List<GroupedItem> groupedItems, Context context,ProfileResponse[] profiles, onTransacClickListener listener) {
        this.originalItems = new ArrayList<>(groupedItems);
        this.filteredByDateItems = new ArrayList<>(groupedItems);
        this.groupedItems = new ArrayList<>(groupedItems);
        this.context = context;
        this.onItemClickListener = listener;
        this.profiles = profiles;
    }

    @Override
    public int getItemViewType(int position) {
        return groupedItems.get(position).getType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == GroupedItem.TYPE_HEADER) {
            View view = inflater.inflate(R.layout.item_grouped_header, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_transaction, parent, false);
            return new TransactionViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        GroupedItem item = groupedItems.get(position);
        String profileName = "";

        if (item.getType() == GroupedItem.TYPE_HEADER) {
            HeaderViewHolder headerHolder = (HeaderViewHolder) holder;
            headerHolder.tvHeader.setText(item.getHeader());

        } else {
            TransactionViewHolder transactionHolder = (TransactionViewHolder) holder;
            DataItem dataItem = item.getDataItem();

            if (dataItem.getItemType() == 0) {
                Transaction transaction = ((TransactionItem) dataItem).getTransaction();
                String formattedDate = formatDate(transaction.getCreatedAt());
                transactionHolder.tvCreatedAt.setText(formattedDate);

                transactionHolder.tvCreatedAt.setText(transaction.getCreatedAt());
                if (transaction.getIdRecipient().equals(new SessionManager(context).getUserId())) {
                    for (ProfileResponse profile: profiles){
                        if (profile.getId().equals(transaction.getIdSender())){
                            transactionHolder.tvRecipient.setText(
                                    context.getString(R.string.transaction_from) +
                                            profile.getFull_name());
                            profileName = context.getString(R.string.transaction_from) +
                                    profile.getFull_name();
                        }
                    }
                    transactionHolder.tvMoney.setTextColor(0xFF72FF30);
                    transactionHolder.tvMoney.setText("+N" +
                            NumberFormatter.formatBalanceCustom(transaction.getMoney()));
                }else{
                    for (ProfileResponse profile: profiles){
                        if (profile.getId().equals(transaction.getIdSender())){
                            transactionHolder.tvRecipient.setText(
                                    context.getString(R.string.transaction_to) +
                                            profile.getFull_name());
                            profileName = context.getString(R.string.transaction_to) +
                                    profile.getFull_name();
                        }
                    }
                    transactionHolder.tvMoney.setTextColor(0xFF09042F);
                    transactionHolder.tvMoney.setText("-N" +
                            NumberFormatter.formatBalanceCustom(transaction.getMoney()));
                }
            } else {
                BalanceHistory balanceHistory = ((BalanceHistoryItem) dataItem).getBalanceHistory();
                transactionHolder.tvRecipient.setText(context.getString(R.string.top_up_balance));
                transactionHolder.tvMoney.setTextColor(0xFF1B7A00);
                transactionHolder.tvMoney.setText("+N" +
                        NumberFormatter.formatBalanceCustom(balanceHistory.getValue()));
            }

            String createdAt = "";
            if (dataItem.getItemType() == 0) {
                createdAt = ((TransactionItem) dataItem).getTransaction().getCreatedAt();
            } else {
                createdAt = ((BalanceHistoryItem) dataItem).getBalanceHistory().getCreatedAt();
            }

            String formattedDate = formatDate(createdAt);
            transactionHolder.tvCreatedAt.setText(formattedDate);

            String finalProfileName = profileName;
            transactionHolder.itemView.setOnClickListener(v -> {
                if (onItemClickListener != null) {
                    onItemClickListener.OnItemClick(dataItem, finalProfileName);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return groupedItems.size();
    }


    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView tvHeader;
        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHeader = itemView.findViewById(R.id.header_text);
        }
    }

    static class TransactionViewHolder extends RecyclerView.ViewHolder {
        TextView tvCreatedAt;
        TextView tvRecipient;
        TextView tvMoney;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCreatedAt = itemView.findViewById(R.id.time_place);
            tvRecipient = itemView.findViewById(R.id.name_place);
            tvMoney = itemView.findViewById(R.id.money_place);
        }
    }

    private String formatDate(String isoDate) {
        try {
            DateTimeFormatter inputFormatter = DateTimeFormatter.ISO_INSTANT;
            Instant instant = Instant.from(inputFormatter.parse(isoDate));
            LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());

            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy, HH:mm");
            return outputFormatter.format(localDateTime);
        } catch (Exception e) {
            e.printStackTrace();
            return isoDate;
        }
    }

    public void applyFilter(Date selectedStartDate, Date selectedEndDate, float minAmount, float maxAmount) {
        List<GroupedItem> filteredList = new ArrayList<>();

        for (GroupedItem item : originalItems) {
            if (item.getType() == GroupedItem.TYPE_HEADER) {
                filteredList.add(item);
            } else if (item.getType() == GroupedItem.TYPE_ITEM) {
                DataItem dataItem = item.getDataItem();
                Date itemDate;
                float itemAmount;

                if (dataItem.getItemType() == 0) {
                    Transaction transaction = ((TransactionItem) dataItem).getTransaction();
                    itemDate = parseIsoDate(transaction.getCreatedAt());
                    itemAmount = transaction.getMoney();
                } else {
                    BalanceHistory balanceHistory = ((BalanceHistoryItem) dataItem).getBalanceHistory();
                    itemDate = parseIsoDate(balanceHistory.getCreatedAt());
                    itemAmount = balanceHistory.getValue();
                }

                boolean isDateInRange = !itemDate.before(selectedStartDate) && !itemDate.after(selectedEndDate);
                boolean isAmountInRange = itemAmount >= minAmount && itemAmount <= maxAmount;

                if (isDateInRange && isAmountInRange) {
                    filteredList.add(item);
                }
            }
        }

        this.filteredByDateItems = filteredList;
        filterByQuery(currentQuery);
    }

    public void filterByQuery(String query) {
        currentQuery = query.toLowerCase(Locale.getDefault()).trim();

        List<GroupedItem> result = new ArrayList<>();

        for (GroupedItem item : filteredByDateItems) { // <-- теперь работает с filteredByDateItems
            if (item.getType() == GroupedItem.TYPE_HEADER) {
                result.add(item);
            } else if (item.getType() == GroupedItem.TYPE_ITEM) {
                DataItem dataItem = item.getDataItem();
                String textToSearch = "";

                if (dataItem.getItemType() == 0) {
                    Transaction transaction = ((TransactionItem) dataItem).getTransaction();
                    if (transaction.getIdRecipient().equals(new SessionManager(context).getUserId())) {
                        for (ProfileResponse profile : profiles) {
                            if (profile.getId().equals(transaction.getIdSender())) {
                                textToSearch = context.getString(R.string.transaction_from) + profile.getFull_name();
                                break;
                            }
                        }
                    } else {
                        for (ProfileResponse profile : profiles) {
                            if (profile.getId().equals(transaction.getIdRecipient())) {
                                textToSearch = context.getString(R.string.transaction_to) + profile.getFull_name();
                                break;
                            }
                        }
                    }
                } else {
                    textToSearch = context.getString(R.string.top_up_balance);
                }

                if (textToSearch.toLowerCase(Locale.getDefault()).contains(currentQuery)) {
                    result.add(item);
                }
            }
        }

        this.groupedItems = result;
        notifyDataSetChanged();
    }

    private Date parseIsoDate(String isoDateString) {
        try {
            String cleanDate = isoDateString.replaceAll("Z$", "+00:00");
            return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault()).parse(cleanDate);
        } catch (Exception e) {
            e.printStackTrace();
            return new Date(0);
        }
    }

    public void resetFilters() {
        this.currentQuery = "";
        this.filteredByDateItems.clear();
        this.filteredByDateItems.addAll(originalItems);
        this.groupedItems.clear();
        this.groupedItems.addAll(originalItems);
        notifyDataSetChanged();
    }
}

