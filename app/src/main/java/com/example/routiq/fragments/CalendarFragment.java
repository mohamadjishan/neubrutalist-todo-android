package com.example.routiq.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.routiq.AppDatabase;
import com.example.routiq.R;
import com.example.routiq.Task;
import com.example.routiq.adapters.TaskAdapter;
import java.util.Calendar;
import java.util.List;

public class CalendarFragment extends Fragment {

    private RecyclerView recyclerView;
    private TaskAdapter adapter;
    private AppDatabase db;
    private CalendarView calendarView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);
        db = AppDatabase.getInstance(getContext());
        recyclerView = view.findViewById(R.id.recyclerViewCalendarTasks);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        calendarView = view.findViewById(R.id.calendarView);

        calendarView.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
            loadTasksForDate(year, month, dayOfMonth);
        });

        Calendar c = Calendar.getInstance();
        loadTasksForDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));

        return view;
    }

    private void loadTasksForDate(int year, int month, int dayOfMonth) {
        Calendar start = Calendar.getInstance();
        start.set(year, month, dayOfMonth, 0, 0, 0);
        long startMillis = start.getTimeInMillis();

        Calendar end = Calendar.getInstance();
        end.set(year, month, dayOfMonth, 23, 59, 59);
        long endMillis = end.getTimeInMillis();

        List<Task> tasks = db.taskDao().getTasksForDay(startMillis, endMillis);
        adapter = new TaskAdapter(tasks, task -> db.taskDao().update(task), task -> {
            db.taskDao().delete(task);
            loadTasksForDate(year, month, dayOfMonth);
        });
        recyclerView.setAdapter(adapter);
    }
}