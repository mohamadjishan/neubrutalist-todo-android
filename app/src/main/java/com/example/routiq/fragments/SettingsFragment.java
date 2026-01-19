package com.example.routiq.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import com.example.routiq.AppDatabase;
import com.example.routiq.R;
import com.example.routiq.Task;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

public class SettingsFragment extends Fragment {

    private SharedPreferences prefs;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        prefs = getContext().getSharedPreferences("RoutiqPrefs", Context.MODE_PRIVATE);

        setupDarkMode(view);
        setupNagInterval(view);
        setupAutoDelete(view);
        
        view.findViewById(R.id.btnExportData).setOnClickListener(v -> exportData());

        return view;
    }

    private void setupDarkMode(View view) {
        SwitchCompat switchDarkMode = view.findViewById(R.id.switchDarkMode);
        boolean isDark = prefs.getBoolean("darkMode", false);
        switchDarkMode.setChecked(isDark);
        
        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("darkMode", isChecked).apply();
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });
    }

    private void setupNagInterval(View view) {
        Spinner spinner = view.findViewById(R.id.spinnerNagInterval);
        String[] intervals = {"5 Minutes", "10 Minutes", "15 Minutes", "30 Minutes"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, intervals);
        spinner.setAdapter(adapter);

        int savedPos = prefs.getInt("nagIntervalPos", 0);
        spinner.setSelection(savedPos);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int minutes = Integer.parseInt(intervals[position].split(" ")[0]);
                prefs.edit().putInt("nagInterval", minutes).putInt("nagIntervalPos", position).apply();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupAutoDelete(View view) {
        SwitchCompat switchAuto = view.findViewById(R.id.switchAutoDelete);
        switchAuto.setChecked(prefs.getBoolean("autoDelete", false));
        switchAuto.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("autoDelete", isChecked).apply();
        });
    }

    private void exportData() {
        AppDatabase db = AppDatabase.getInstance(getContext());
        List<Task> tasks = db.taskDao().getAllTasks();
        StringBuilder csvData = new StringBuilder();
        csvData.append("ID,Title,Category,Priority,Completed\n");
        for (Task task : tasks) {
            csvData.append(task.id).append(",")
                    .append(task.title).append(",")
                    .append(task.category).append(",")
                    .append(task.priority).append(",")
                    .append(task.isCompleted).append("\n");
        }

        try {
            File file = new File(getContext().getFilesDir(), "tasks_export.csv");
            FileOutputStream out = new FileOutputStream(file);
            out.write(csvData.toString().getBytes());
            out.close();

            Uri path = FileProvider.getUriForFile(getContext(), "com.example.routiq.fileprovider", file);
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/csv");
            intent.putExtra(Intent.EXTRA_STREAM, path);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(intent, "Export Data"));
        } catch (Exception e) {
            Toast.makeText(getContext(), "Export failed", Toast.LENGTH_SHORT).show();
        }
    }
}