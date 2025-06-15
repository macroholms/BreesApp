package com.example.breesapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.breesapp.R;
import com.example.breesapp.classes.BalanceHistoryItem;
import com.example.breesapp.classes.NumberFormatter;
import com.example.breesapp.classes.SessionManager;
import com.example.breesapp.classes.SupabaseClient;
import com.example.breesapp.classes.TransactionItem;
import com.example.breesapp.interfaces.DataItem;
import com.example.breesapp.models.BalanceHistory;
import com.example.breesapp.models.ProfileResponse;
import com.example.breesapp.models.Transaction;
import com.google.gson.Gson;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class CombinedMoneyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private List<DataItem> dataList;
    private Context context;
    private ProfileResponse[] profiles;

    public CombinedMoneyAdapter(List<DataItem> dataList, Context context, ProfileResponse[] profiles) {
        this.dataList = dataList;
        this.context = context;
        this.profiles = profiles;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_transaction, parent, false);
        return new TransactionViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        DataItem item = dataList.get(position);
        Activity activity = (Activity) context;
        TransactionViewHolder transactionHolder = (TransactionViewHolder) holder;
        String activityName = activity.getClass().getSimpleName();
        Log.d("CurrentActivity", "Текущая Activity: " + activityName);
        if(activityName.equals("MainActivity")){
            transactionHolder.tvRecipient.setTextColor(0xFFFFFFFF);
            transactionHolder.tvCreatedAt.setTextColor(0xFFFFFFFF);
        }else{
            transactionHolder.tvRecipient.setTextColor(0xFF000000);
            transactionHolder.tvCreatedAt.setTextColor(0xFF000000);
        }
        if (item.getItemType() == 0) {
            TransactionItem transactionItem = (TransactionItem) item;
            Transaction transaction = transactionItem.getTransaction();
            String formattedDate = formatDate(transaction.getCreatedAt());
            transactionHolder.tvCreatedAt.setText(formattedDate);
            if (transaction.getIdRecipient().equals(new SessionManager(context).getUserId())) {
                for (ProfileResponse profile: profiles){
                    if (profile.getId().equals(transaction.getIdSender())){
                        transactionHolder.tvRecipient.setText(
                                context.getString(R.string.transaction_from) +
                                profile.getFull_name());
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
                    }
                }
                transactionHolder.tvMoney.setTextColor(0xFFff0a0a);
                transactionHolder.tvMoney.setText("-N" +
                        NumberFormatter.formatBalanceCustom(transaction.getMoney()));
            }
        }else{
            BalanceHistory balanceHistory = ((BalanceHistoryItem) item).getBalanceHistory();
            String formattedDate = formatDate(balanceHistory.getCreatedAt());
            transactionHolder.tvCreatedAt.setText(formattedDate);
            transactionHolder.tvRecipient.setText(
                    context.getString(R.string.top_up_balance));
            transactionHolder.tvMoney.setTextColor(0xFF72FF30);
            transactionHolder.tvMoney.setText("+N" +
                    NumberFormatter.formatBalanceCustom(balanceHistory.getValue()));
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return dataList.get(position).getItemType();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
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

    class TransactionViewHolder extends RecyclerView.ViewHolder {
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
}
