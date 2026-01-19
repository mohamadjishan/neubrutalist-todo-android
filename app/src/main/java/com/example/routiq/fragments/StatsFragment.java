package com.example.routiq.fragments;

import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.example.routiq.AppDatabase;
import com.example.routiq.R;
import com.example.routiq.Task;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stats, container, false);
        
        AppDatabase db = AppDatabase.getInstance(getContext());
        List<Task> tasks = db.taskDao().getAllTasks();
        
        updateSummary(view, tasks);
        updateCategoryBreakdown(view, tasks);
        updatePriorityAnalysis(view, tasks);

        return view;
    }

    private void updateSummary(View view, List<Task> tasks) {
        int total = tasks.size();
        int completed = 0;
        for (Task t : tasks) {
            if (t.isCompleted) completed++;
        }

        ((TextView) view.findViewById(R.id.tvTotalTasks)).setText(String.valueOf(total));
        ((TextView) view.findViewById(R.id.tvCompletedTasks)).setText(String.valueOf(completed));
        
        int streak = calculateStreak(tasks);
        ((TextView) view.findViewById(R.id.tvStreak)).setText(String.valueOf(streak));
    }

    private int calculateStreak(List<Task> tasks) {
        int streak = 0;
        Calendar cal = Calendar.getInstance();
        for (int i = 0; i < 30; i++) {
            boolean foundCompleted = false;
            long start = getStartOfDay(cal);
            long end = getEndOfDay(cal);
            
            for (Task t : tasks) {
                if (t.isCompleted && t.timestamp >= start && t.timestamp <= end) {
                    foundCompleted = true;
                    break;
                }
            }
            if (foundCompleted) {
                streak++;
                cal.add(Calendar.DATE, -1);
            } else {
                break;
            }
        }
        return streak;
    }

    private void updateCategoryBreakdown(View view, List<Task> tasks) {
        LinearLayout container = view.findViewById(R.id.categoryContainer);
        container.removeAllViews();
        
        Map<String, Integer> categoryMap = new HashMap<>();
        for (Task t : tasks) {
            String cat = t.category != null ? t.category : "General";
            categoryMap.put(cat, categoryMap.getOrDefault(cat, 0) + 1);
        }

        for (Map.Entry<String, Integer> entry : categoryMap.entrySet()) {
            View itemView = getLayoutInflater().inflate(R.layout.item_stat, container, false);
            TextView text1 = itemView.findViewById(R.id.statName);
            TextView text2 = itemView.findViewById(R.id.statValue);
            
            text1.setText(entry.getKey());
            text2.setText(entry.getValue() + " Tasks");
            
            container.addView(itemView);
        }
    }

    private void updatePriorityAnalysis(View view, List<Task> tasks) {
        LinearLayout container = view.findViewById(R.id.priorityContainer);
        container.removeAllViews();
        
        int[] priorities = new int[3]; 
        for (Task t : tasks) {
            if (t.priority >= 0 && t.priority <= 2) {
                priorities[t.priority]++;
            }
        }

        String[] labels = {"Low Priority", "Medium Priority", "High Priority"};
        int[] colors = {R.color.accent_blue, R.color.accent_yellow, R.color.accent_pink};

        for (int i = 2; i >= 0; i--) {
            View itemView = getLayoutInflater().inflate(R.layout.item_stat, container, false);
            
            // USE .mutate() to prevent color leaking to other screens!
            LayerDrawable layerDrawable = (LayerDrawable) itemView.getBackground().mutate();
            GradientDrawable backgroundShape = (GradientDrawable) layerDrawable.findDrawableByLayerId(R.id.card_content);
            if (backgroundShape != null) {
                backgroundShape.setColor(ContextCompat.getColor(getContext(), colors[i]));
            }
            itemView.setBackground(layerDrawable);
            
            TextView text1 = itemView.findViewById(R.id.statName);
            TextView text2 = itemView.findViewById(R.id.statValue);
            
            text1.setText(labels[i]);
            text2.setText(String.valueOf(priorities[i]));
            
            container.addView(itemView);
        }
    }

    private long getStartOfDay(Calendar cal) {
        Calendar c = (Calendar) cal.clone();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTimeInMillis();
    }

    private long getEndOfDay(Calendar cal) {
        Calendar c = (Calendar) cal.clone();
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        c.set(Calendar.MILLISECOND, 999);
        return c.getTimeInMillis();
    }
}