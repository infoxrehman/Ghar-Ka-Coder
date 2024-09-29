package com.lixtanetwork.gharkacoder.explore.hackathon.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.lixtanetwork.gharkacoder.R;
import com.lixtanetwork.gharkacoder.databinding.ActivityHostHackathonBinding;

import java.util.Calendar;

public class HostHackathonActivity extends AppCompatActivity {

    private ActivityHostHackathonBinding binding;
    private FirebaseFirestore firebaseFirestore;
    private String dateTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHostHackathonBinding.inflate(getLayoutInflater());
        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.background));
        setContentView(binding.getRoot());

        firebaseFirestore = FirebaseFirestore.getInstance();

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        binding.forLayout.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.forStudentOption) {
                    binding.forStudentEntry.setVisibility(View.VISIBLE);
                    binding.forCollegeUniversityEntry.setVisibility(View.GONE);
                    forStudent();
                } else if (checkedId == R.id.forCommuntityOrCollegeOption) {
                    binding.forStudentEntry.setVisibility(View.GONE);
                    binding.forCollegeUniversityEntry.setVisibility(View.VISIBLE);
                }
            }
        });

        binding.forStudentOption.setChecked(true);

    }

    private void forStudent() {

        binding.studentHackathonStartDateEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateTimePickerDialog(binding.studentHackathonStartDateEt);
            }
        });

        binding.studentHackathonEndDateEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateTimePickerDialog(binding.studentHackathonEndDateEt);
            }
        });
    }

    private void showDateTimePickerDialog(EditText editText) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, selectedYear, selectedMonth, selectedDay) -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(this, (timeView, selectedHour, selectedMinute) -> {
                dateTime = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear + " " +
                        String.format("%02d:%02d", selectedHour, selectedMinute);
                editText.setText(dateTime);
            }, hour, minute, true);
            timePickerDialog.show();
        }, year, month, day);
        datePickerDialog.show();
    }

}