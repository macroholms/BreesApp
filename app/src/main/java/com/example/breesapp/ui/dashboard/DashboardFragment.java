package com.example.breesapp.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.breesapp.classes.SupabaseClient;
import com.example.breesapp.databinding.FragmentDashboardBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;

    private Calendar currentCalendar = Calendar.getInstance();
    private Calendar todayCalendar = Calendar.getInstance();

    TextView spent, recieved, colvo;

    SupabaseClient supabaseClient = new SupabaseClient();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView monthTV = binding.MonthTV;
        final TextView yearTV = binding.YearTV;
        spent = binding.SpentTV;
        recieved = binding.ReceivedTV;
        colvo = binding.ColvoTV;
        final ImageButton plusBtn = binding.plusMonthBtn;
        final ImageButton minusBtn = binding.minusMonthBtn;

        updateDisplay(monthTV, yearTV);

        plusBtn.setOnClickListener(v -> {
            if (!isCurrentMonth(currentCalendar)) {
                currentCalendar.add(Calendar.MONTH, 1);
                updateDisplay(monthTV, yearTV);
            }
        });

        minusBtn.setOnClickListener(v -> {
            currentCalendar.add(Calendar.MONTH, -1);
            updateDisplay(monthTV, yearTV);
        });

        return root;
    }

    private void updateDisplay(TextView monthTV, TextView yearTV) {
        SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM", Locale.getDefault());
        String monthName = monthFormat.format(currentCalendar.getTime());

        monthTV.setText(monthName);
        yearTV.setText(String.valueOf(currentCalendar.get(Calendar.YEAR)));

        supabaseClient.getMonthlyStatistic(getContext(), currentCalendar.get(Calendar.MONTH) + 1,
                currentCalendar.get(Calendar.YEAR), new SupabaseClient.SBC_Callback() {
                    @Override
                    public void onFailure(IOException e) {

                    }

                    @Override
                    public void onResponse(String responseBody) {
                        try {
                            JSONArray array = new JSONArray(responseBody);
                            JSONObject body = array.getJSONObject(0);

                            if (body != null && getActivity() != null){
                                getActivity().runOnUiThread(() -> {
                                    try {
                                        spent.setText("N" + body.getString("spent_money"));
                                        recieved.setText("N" + body.getString("received_money"));
                                        colvo.setText(body.getString("total_operations"));
                                    } catch (JSONException e) {
                                        throw new RuntimeException(e);
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                    }
                });
    }

    private boolean isCurrentMonth(Calendar cal) {
        return cal.get(Calendar.YEAR) == todayCalendar.get(Calendar.YEAR)
                && cal.get(Calendar.MONTH) == todayCalendar.get(Calendar.MONTH);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}