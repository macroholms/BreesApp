package com.example.breesapp.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.breesapp.R;
import com.example.breesapp.adapters.CombinedMoneyAdapter;
import com.example.breesapp.adapters.GroupedTransactionAdapter;
import com.example.breesapp.classes.BalanceHistoryItem;
import com.example.breesapp.classes.FilterBottomSheet;
import com.example.breesapp.classes.GroupingHelper;
import com.example.breesapp.classes.NumberFormatter;
import com.example.breesapp.classes.SupabaseClient;
import com.example.breesapp.classes.TransactionItem;
import com.example.breesapp.interfaces.DataItem;
import com.example.breesapp.models.BalanceHistory;
import com.example.breesapp.models.GroupedItem;
import com.example.breesapp.models.ProfileResponse;
import com.example.breesapp.models.Transaction;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;

public class TransactionsHistoryActivity extends AppCompatActivity {

    SupabaseClient supabaseClient;
    GroupedTransactionAdapter adapter;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions_history);
        supabaseClient = new SupabaseClient();

        recyclerView = findViewById(R.id.transactions_rv);

        SearchView sv = findViewById(R.id.transactions_sv);
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.filterByQuery(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    adapter.resetFilters();
                } else {
                    adapter.filterByQuery(newText);
                }
                return false;
            }
        });

        ImageButton filters = findViewById(R.id.filters_btn);
        filters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FilterBottomSheet.show(getSupportFragmentManager(), adapter, (dateStart, dateEnd, minAmount, maxAmount) -> {
                    adapter.applyFilter(dateStart, dateEnd, minAmount, maxAmount);
                });
            }
        });

        ImageButton back_btn = findViewById(R.id.back_btn);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        new FetchDataAsync().execute();
    }

    private class FetchDataAsync extends AsyncTask<Void, Void, List<DataItem>> {
        @Override
        protected List<DataItem> doInBackground(Void... voids) {
            try {
                return supabaseClient.fetchTransactions(getApplicationContext());
            } catch (IOException e) {
                Log.e("FetchDataAsync", "Ошибка при получении данных", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<DataItem> dataItems) {
            if (dataItems != null) {
                List<GroupedItem> groupedItems = groupDataByDay(dataItems);

                SupabaseClient supabaseClient = new SupabaseClient();
                supabaseClient.fetchProfiles(getApplicationContext(), new SupabaseClient.SBC_Callback() {
                    @Override
                    public void onFailure(IOException e) {
                    }

                    @Override
                    public void onResponse(String responseBody) {
                        new Handler(Looper.getMainLooper()).post(() -> {
                            try {
                                Gson gson = new Gson();
                                ProfileResponse[] profiles = gson.fromJson(responseBody, ProfileResponse[].class);
                                adapter = new GroupedTransactionAdapter(groupedItems,
                                        getApplicationContext(),profiles ,new GroupedTransactionAdapter.onTransacClickListener() {
                                    @Override
                                    public void OnItemClick(DataItem item, String profileName) {
                                        Intent i = new Intent(getApplicationContext(), TransacDetailsActivity.class);
                                        if (item.getItemType() == 0) {
                                            Transaction transaction = ((TransactionItem) item).getTransaction();
                                            i.putExtra("status", "transac");
                                            i.putExtra("name", profileName);
                                            i.putExtra("date", transaction.getCreatedAt());
                                            i.putExtra("amount",
                                                    NumberFormatter.formatBalanceCustom(transaction.getMoney()));
                                        }else{
                                            BalanceHistory balanceHistory = ((BalanceHistoryItem) item).getBalanceHistory();
                                            i.putExtra("status", "topup");
                                            i.putExtra("date", balanceHistory.getCreatedAt());
                                            i.putExtra("amount",
                                                    NumberFormatter.formatBalanceCustom(balanceHistory.getValue()));
                                        }
                                        startActivity(i);
                                    }
                                });
                                recyclerView.setAdapter(adapter);
                            } catch (Exception e) {
                            }
                        });
                    }
                });

            } else {
            }
        }
    }

    private List<GroupedItem> groupDataByDay(List<DataItem> items) {
        return new GroupingHelper().groupByDay(items, getApplicationContext());
    }
}