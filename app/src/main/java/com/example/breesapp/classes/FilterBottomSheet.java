package com.example.breesapp.classes;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.fragment.app.FragmentManager;

import com.example.breesapp.R;
import com.example.breesapp.adapters.GroupedTransactionAdapter;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.slider.RangeSlider;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class FilterBottomSheet extends BottomSheetDialogFragment {

    private static final Date DEFAULT_START_DATE = getDefaultStartDate();
    private static final Date DEFAULT_END_DATE = new Date();

    private static Date getDefaultStartDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2025, Calendar.JANUARY, 1, 0, 0, 0);// 01.01.2025
        return calendar.getTime();
    }

    public interface OnApplyFilterListener {
        void onApply(Date dateStart, Date dateEnd, float minAmount, float maxAmount);
    }

    private OnApplyFilterListener listener;
    private TextView dateRangeText;
    private RangeSlider rangeSlider;
    private Date selectedStartDate = null;
    private Date selectedEndDate = null;

    private GroupedTransactionAdapter adapter;


    public static void show(FragmentManager fragmentManager, GroupedTransactionAdapter adapter, OnApplyFilterListener listener) {
        FilterBottomSheet bottomSheet = new FilterBottomSheet();
        bottomSheet.adapter = adapter;
        bottomSheet.listener = listener;
        bottomSheet.show(fragmentManager, "FilterBottomSheet");
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext(), R.style.AppBottomSheetDialogTheme);

        View view = LayoutInflater.from(getContext()).inflate(R.layout.bottom_sheet_filter, null);

        dateRangeText = view.findViewById(R.id.date_range_text);
        ImageButton btnPickDate = view.findViewById(R.id.btn_pick_date);
        rangeSlider = view.findViewById(R.id.range_slider_money);
        Button btnReset = view.findViewById(R.id.btn_reset);
        Button btnApply = view.findViewById(R.id.btn_apply);

        selectedStartDate = DEFAULT_START_DATE;
        selectedEndDate = DEFAULT_END_DATE;

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        String formattedDateRange = sdf.format(selectedStartDate) + " - " + sdf.format(selectedEndDate);
        dateRangeText.setText(formattedDateRange);

        btnPickDate.setOnClickListener(v -> {
            MaterialDatePicker<Pair<Long, Long>> datePicker = MaterialDatePicker.Builder.dateRangePicker().build();
            datePicker.addOnPositiveButtonClickListener(selection -> {
                if (selection != null) {
                    Date startDate = new Date(selection.first);

                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(new Date(selection.second));
                    calendar.set(Calendar.HOUR_OF_DAY, 23);
                    calendar.set(Calendar.MINUTE, 59);
                    calendar.set(Calendar.SECOND, 59);
                    calendar.set(Calendar.MILLISECOND, 999);
                    Date endDate = calendar.getTime();

                    selectedStartDate = startDate;
                    selectedEndDate = endDate;
                    String formatted = sdf.format(selectedStartDate) + " - " + sdf.format(selectedEndDate);
                    dateRangeText.setText(formatted);
                }
            });
            datePicker.show(getParentFragmentManager(), "DATE_PICKER");
        });

        btnReset.setOnClickListener(v -> {
            rangeSlider.setValues(Arrays.asList(0f, 10000000f));
            selectedStartDate = DEFAULT_START_DATE;
            selectedEndDate = DEFAULT_END_DATE;

            String formatted = sdf.format(selectedStartDate) + " - " + sdf.format(selectedEndDate);
            dateRangeText.setText(formatted);

            if (listener != null) {
                float min = rangeSlider.getValues().get(0);
                float max = rangeSlider.getValues().get(1);
                listener.onApply(selectedStartDate, selectedEndDate, min, max);
            }

            new Handler(Looper.getMainLooper()).post(() -> {
                if (adapter != null) {
                    adapter.resetFilters();
                }
            });
        });

        btnApply.setOnClickListener(v -> {
            if (listener != null) {
                float min = rangeSlider.getValues().get(0);
                float max = rangeSlider.getValues().get(1);
                if (selectedStartDate == null || selectedEndDate == null) {
                    Toast.makeText(getContext(), "Выберите дату", Toast.LENGTH_SHORT).show();
                    return;
                }

                listener.onApply(selectedStartDate, selectedEndDate, min, max);
            }
            dismiss();
        });

        dialog.setContentView(view);
        return dialog;
    }

}