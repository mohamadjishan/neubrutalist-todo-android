package com.example.routiq;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.chip.ChipGroup;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AddTaskActivity extends AppCompatActivity {

    private EditText etTitle, etSubtaskTitle;
    private TextView tvReminderValue, tvAddTitle;
    private Calendar reminderCalendar;
    private AppDatabase db;
    private Spinner spinnerCategory, spinnerNagInterval;
    private ChipGroup cgPriority;
    private CheckBox cbRecurring, cbNag;
    private LinearLayout llSubtasksContainer, llNagOptions;
    private List<String> subtasks = new ArrayList<>();
    
    private int editTaskId = -1;
    private Task existingTask;

    private final String[] nagOptions = {"5 Minutes", "10 Minutes", "15 Minutes", "30 Minutes", "1 Hour", "2 Hours"};
    private final int[] nagMinutes = {5, 10, 15, 30, 60, 120};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        db = AppDatabase.getInstance(this);
        tvAddTitle = findViewById(R.id.tvAddTitle);
        etTitle = findViewById(R.id.etTaskTitle);
        etSubtaskTitle = findViewById(R.id.etSubtaskTitle);
        tvReminderValue = findViewById(R.id.tvReminderValue);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        spinnerNagInterval = findViewById(R.id.spinnerNagInterval);
        cgPriority = findViewById(R.id.cgPriority);
        cbRecurring = findViewById(R.id.cbRecurring);
        cbNag = findViewById(R.id.cbNag);
        llSubtasksContainer = findViewById(R.id.llSubtasksContainer);
        llNagOptions = findViewById(R.id.llNagOptions);
        
        Button btnAddSubtask = findViewById(R.id.btnAddSubtask);
        Button btnPickDateTime = findViewById(R.id.btnPickDateTime);
        Button btnSave = findViewById(R.id.btnSaveTask);

        // Categories
        String[] categories = {"General", "Work", "Personal", "Study", "Health", "Finance"};
        ArrayAdapter<String> catAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categories);
        spinnerCategory.setAdapter(catAdapter);

        // Nag Intervals
        ArrayAdapter<String> nagAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, nagOptions);
        spinnerNagInterval.setAdapter(nagAdapter);

        cbNag.setOnCheckedChangeListener((buttonView, isChecked) -> {
            llNagOptions.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        });

        btnAddSubtask.setOnClickListener(v -> addSubtask());
        btnPickDateTime.setOnClickListener(v -> showDateTimePicker());
        btnSave.setOnClickListener(v -> saveTask());

        // Check for edit mode
        editTaskId = getIntent().getIntExtra("taskId", -1);
        if (editTaskId != -1) {
            setupEditMode();
        }
    }

    private void setupEditMode() {
        existingTask = db.taskDao().getTaskById(editTaskId);
        if (existingTask != null) {
            tvAddTitle.setText("Edit Task");
            etTitle.setText(existingTask.title);
            
            // Priority
            if (existingTask.priority == 0) cgPriority.check(R.id.chipLow);
            else if (existingTask.priority == 1) cgPriority.check(R.id.chipMedium);
            else if (existingTask.priority == 2) cgPriority.check(R.id.chipHigh);
            
            // Category
            String[] categories = {"General", "Work", "Personal", "Study", "Health", "Finance"};
            for (int i = 0; i < categories.length; i++) {
                if (categories[i].equals(existingTask.category)) {
                    spinnerCategory.setSelection(i);
                    break;
                }
            }
            
            // Reminder
            if (existingTask.reminderTime > 0) {
                reminderCalendar = Calendar.getInstance();
                reminderCalendar.setTimeInMillis(existingTask.reminderTime);
                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault());
                tvReminderValue.setText(sdf.format(reminderCalendar.getTime()));
            }
            
            cbRecurring.setChecked(existingTask.isRecurring);
            cbNag.setChecked(existingTask.nagUntilComplete);
            
            if (existingTask.nagUntilComplete) {
                llNagOptions.setVisibility(View.VISIBLE);
                for (int i = 0; i < nagMinutes.length; i++) {
                    if (nagMinutes[i] == existingTask.nagIntervalMinutes) {
                        spinnerNagInterval.setSelection(i);
                        break;
                    }
                }
            }
            
            // Subtasks
            try {
                JSONArray array = new JSONArray(existingTask.subtasksJson);
                for (int i = 0; i < array.length(); i++) {
                    subtasks.add(array.getJSONObject(i).getString("title"));
                }
                renderSubtasks();
            } catch (JSONException e) {}

            ((Button)findViewById(R.id.btnSaveTask)).setText("Update Task");
        }
    }

    private void addSubtask() {
        String subTitle = etSubtaskTitle.getText().toString().trim();
        if (subTitle.isEmpty()) return;
        subtasks.add(subTitle);
        renderSubtasks();
        etSubtaskTitle.setText("");
    }

    private void renderSubtasks() {
        llSubtasksContainer.removeAllViews();
        for (int i = 0; i < subtasks.size(); i++) {
            String subTitle = subtasks.get(i);
            View view = LayoutInflater.from(this).inflate(R.layout.item_subtask_input, llSubtasksContainer, false);
            TextView tv = view.findViewById(R.id.tvSubtaskName);
            View btnRemove = view.findViewById(R.id.btnRemoveSubtask);
            tv.setText(subTitle);
            int index = i;
            btnRemove.setOnClickListener(v -> {
                subtasks.remove(index);
                renderSubtasks();
            });
            llSubtasksContainer.addView(view);
        }
    }

    private void showDateTimePicker() {
        Calendar current = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            Calendar selected = Calendar.getInstance();
            selected.set(year, month, dayOfMonth);
            new TimePickerDialog(this, (view1, hourOfDay, minute) -> {
                selected.set(Calendar.HOUR_OF_DAY, hourOfDay);
                selected.set(Calendar.MINUTE, minute);
                selected.set(Calendar.SECOND, 0);
                reminderCalendar = selected;
                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault());
                tvReminderValue.setText(sdf.format(selected.getTime()));
            }, current.get(Calendar.HOUR_OF_DAY), current.get(Calendar.MINUTE), false).show();
        }, current.get(Calendar.YEAR), current.get(Calendar.MONTH), current.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void saveTask() {
        String title = etTitle.getText().toString().trim();
        if (title.isEmpty()) {
            Toast.makeText(this, "Please enter a title", Toast.LENGTH_SHORT).show();
            return;
        }

        long remTime = (reminderCalendar != null) ? reminderCalendar.getTimeInMillis() : 0;
        
        Task task;
        if (editTaskId != -1 && existingTask != null) {
            task = existingTask;
            task.title = title;
            task.reminderTime = remTime;
        } else {
            task = new Task(title, "", System.currentTimeMillis(), remTime);
        }
        
        task.category = spinnerCategory.getSelectedItem().toString();
        
        int priority = 1;
        int checkedId = cgPriority.getCheckedChipId();
        if (checkedId == R.id.chipLow) priority = 0;
        else if (checkedId == R.id.chipHigh) priority = 2;
        task.priority = priority;
        
        task.isRecurring = cbRecurring.isChecked();
        task.nagUntilComplete = cbNag.isChecked();
        if (task.nagUntilComplete) {
            task.nagIntervalMinutes = nagMinutes[spinnerNagInterval.getSelectedItemPosition()];
        }

        JSONArray array = new JSONArray();
        for (String s : subtasks) {
            try {
                JSONObject obj = new JSONObject();
                obj.put("title", s);
                obj.put("completed", false);
                array.put(obj);
            } catch (JSONException e) {}
        }
        task.subtasksJson = array.toString();

        if (editTaskId != -1) {
            db.taskDao().update(task);
            cancelExistingAlarm(task.id);
        } else {
            long insertedId = db.taskDao().insert(task);
            task.id = (int) insertedId;
        }

        if (remTime > 0) {
            scheduleNotification(task);
        }

        Toast.makeText(this, editTaskId != -1 ? "Task updated!" : "Task created!", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void cancelExistingAlarm(int taskId) {
        Intent intent = new Intent(this, ReminderReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, taskId, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }

    private void scheduleNotification(Task task) {
        Intent intent = new Intent(this, ReminderReceiver.class);
        intent.putExtra("taskTitle", task.title);
        intent.putExtra("taskId", task.id);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, task.id, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            long triggerAt = task.reminderTime;
            if (task.nagUntilComplete) {
                long intervalMillis = (long) task.nagIntervalMinutes * 60 * 1000;
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, triggerAt, intervalMillis, pendingIntent);
            } else if (task.isRecurring) {
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, triggerAt, AlarmManager.INTERVAL_DAY, pendingIntent);
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent);
                } else {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent);
                }
            }
        }
    }
}