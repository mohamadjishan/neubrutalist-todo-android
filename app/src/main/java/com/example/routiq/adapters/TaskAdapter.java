package com.example.routiq.adapters;

import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.routiq.R;
import com.example.routiq.Task;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<Task> tasks;
    private OnTaskStatusChangedListener statusListener;
    private OnTaskLongClickListener longClickListener;

    public interface OnTaskStatusChangedListener {
        void onStatusChanged(Task task);
    }

    public interface OnTaskLongClickListener {
        void onLongClick(Task task);
    }

    public TaskAdapter(List<Task> tasks, OnTaskStatusChangedListener statusListener, OnTaskLongClickListener longClickListener) {
        this.tasks = tasks;
        this.statusListener = statusListener;
        this.longClickListener = longClickListener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = tasks.get(position);
        holder.title.setText(task.title);
        holder.checkBox.setChecked(task.isCompleted);
        holder.category.setText(task.category.toUpperCase());

        if (task.priority == 2) {
            holder.priority.setText("HIGH");
            holder.priority.setBackgroundColor(Color.parseColor("#FF90E8"));
        } else if (task.priority == 1) {
            holder.priority.setText("MED");
            holder.priority.setBackgroundColor(Color.parseColor("#FFD600"));
        } else {
            holder.priority.setText("LOW");
            holder.priority.setBackgroundColor(Color.parseColor("#33B5FF"));
        }

        if (task.isCompleted) {
            holder.title.setPaintFlags(holder.title.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.title.setPaintFlags(holder.title.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }

        if (task.reminderTime > 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault());
            String timeText = sdf.format(new Date(task.reminderTime));
            if (task.isRecurring) timeText += " (Daily)";
            holder.time.setText(timeText);
            holder.time.setVisibility(View.VISIBLE);
        } else {
            holder.time.setVisibility(View.GONE);
        }

        renderSubtasks(holder.llSubtasksDisplay, task);

        holder.checkBox.setOnClickListener(v -> {
            task.isCompleted = holder.checkBox.isChecked();
            statusListener.onStatusChanged(task);
        });

        holder.itemView.setOnLongClickListener(v -> {
            longClickListener.onLongClick(task);
            return true;
        });
    }

    private void renderSubtasks(LinearLayout container, Task task) {
        container.removeAllViews();
        try {
            JSONArray array = new JSONArray(task.subtasksJson);
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                String subTitle = obj.getString("title");
                boolean subDone = obj.getBoolean("completed");

                View view = LayoutInflater.from(container.getContext()).inflate(R.layout.item_subtask_display, container, false);
                CheckBox cb = view.findViewById(R.id.cbSubtask);
                TextView tv = view.findViewById(R.id.tvSubtaskName);

                tv.setText(subTitle);
                cb.setChecked(subDone);
                if (subDone) tv.setPaintFlags(tv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                int index = i;
                cb.setOnClickListener(v -> {
                    try {
                        obj.put("completed", cb.isChecked());
                        array.put(index, obj);
                        task.subtasksJson = array.toString();
                        statusListener.onStatusChanged(task);
                    } catch (JSONException e) {}
                });

                container.addView(view);
            }
        } catch (JSONException e) {}
    }

    public Task getTaskAt(int position) {
        return tasks.get(position);
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        TextView title, time, priority, category;
        LinearLayout llSubtasksDisplay;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkBoxTask);
            title = itemView.findViewById(R.id.textViewTaskTitle);
            time = itemView.findViewById(R.id.textViewTaskTime);
            priority = itemView.findViewById(R.id.tvPriority);
            category = itemView.findViewById(R.id.tvCategory);
            llSubtasksDisplay = itemView.findViewById(R.id.llSubtasksDisplay);
        }
    }
}