package com.example.routiq.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.routiq.AddTaskActivity;
import com.example.routiq.AppDatabase;
import com.example.routiq.R;
import com.example.routiq.Task;
import com.example.routiq.adapters.TaskAdapter;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class TasksFragment extends Fragment {

    private RecyclerView recyclerView;
    private TaskAdapter adapter;
    private AppDatabase db;
    private List<Task> allTasks = new ArrayList<>();
    private int currentFilter = R.id.chipAll;
    private String searchQuery = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tasks, container, false);

        db = AppDatabase.getInstance(getContext());
        recyclerView = view.findViewById(R.id.recyclerViewTasks);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        EditText etSearch = view.findViewById(R.id.etSearch);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchQuery = s.toString().toLowerCase();
                filterTasks();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        ChipGroup chipGroup = view.findViewById(R.id.chipGroupFilter);
        chipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (!checkedIds.isEmpty()) {
                currentFilter = checkedIds.get(0);
                filterTasks();
            }
        });

        setupSwipeToDelete();

        FloatingActionButton fab = view.findViewById(R.id.fabAddTask);
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AddTaskActivity.class);
            startActivity(intent);
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadTasks();
    }

    private void loadTasks() {
        allTasks = db.taskDao().getAllTasks();
        filterTasks();
    }

    private void filterTasks() {
        List<Task> filteredList = new ArrayList<>();
        for (Task task : allTasks) {
            boolean matchesFilter = false;
            if (currentFilter == R.id.chipAll) matchesFilter = true;
            else if (currentFilter == R.id.chipPending && !task.isCompleted) matchesFilter = true;
            else if (currentFilter == R.id.chipCompleted && task.isCompleted) matchesFilter = true;

            boolean matchesSearch = task.title.toLowerCase().contains(searchQuery);

            if (matchesFilter && matchesSearch) {
                filteredList.add(task);
            }
        }

        adapter = new TaskAdapter(filteredList, task -> {
            db.taskDao().update(task);
            loadTasks();
        }, task -> {
            db.taskDao().delete(task);
            loadTasks();
        });
        recyclerView.setAdapter(adapter);
    }

    private void setupSwipeToDelete() {
        ItemTouchHelper.SimpleCallback swipeHandler = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Task taskToDelete = adapter.getTaskAt(position);
                db.taskDao().delete(taskToDelete);
                Toast.makeText(getContext(), "Task deleted", Toast.LENGTH_SHORT).show();
                loadTasks();
            }
        };
        new ItemTouchHelper(swipeHandler).attachToRecyclerView(recyclerView);
    }
}