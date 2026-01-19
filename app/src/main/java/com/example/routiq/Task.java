package com.example.routiq;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "tasks")
public class Task {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String title;
    public String description;
    public long timestamp;
    public boolean isCompleted;
    public long reminderTime;
    
    // New Fields
    public String category; // Work, Personal, etc.
    public int priority; // 0: Low, 1: Medium, 2: High
    public String subtasksJson; // JSON string for checklist
    public boolean isRecurring;
    public boolean nagUntilComplete; // Nag every 5 minutes
    public long lastUpdated;

    public Task(String title, String description, long timestamp, long reminderTime) {
        this.title = title;
        this.description = description;
        this.timestamp = timestamp;
        this.reminderTime = reminderTime;
        this.isCompleted = false;
        this.category = "General";
        this.priority = 1;
        this.subtasksJson = "[]";
        this.isRecurring = false;
        this.nagUntilComplete = false;
        this.lastUpdated = System.currentTimeMillis();
    }
}