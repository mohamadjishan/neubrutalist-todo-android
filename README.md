# Routiq - Neobrutalist Task Manager

Routiq is a powerful, productivity-focused Android application designed with a **Neobrutalist** aesthetic. It combines bold visuals with robust task management features to help users stay organized without the clutter in a stylish, modern way.

## 📱 About the App

Routiq breaks away from standard Material Design norms by embracing **Neobrutalism**—a design trend characterized by high contrast, bold typography, thick dark borders, and vibrant colors. The app is built natively for Android to ensure smooth performance and deep system integration.

The primary goal of Routiq is to provide a distraction-free environment where users can manage their daily tasks, track productivity, and organize their schedule efficiently.

## ✨ Main Features

*   **Task Management**: effortless ADD, EDIT, and DELETE operations for tasks.
*   **Smart Reminders**: Set precise due dates and times. The app uses `AlarmManager` to send timely notifications so you never miss a deadline.
*   **Neobrutalist Design**: unique UI with defined borders, flat colors, and a distinct lack of gradients or soft shadows.
*   **Real-time Search**: instantly find tasks by searching for keywords in the title.
*   **Task Filtering**: toggle between "All", "Pending", and "Completed" tasks with a single tap.
*   **Swipe Actions**: intuitive swipe-to-delete gestures for quick task management.
*   **Calendar View**: visualize your schedule with a dedicated monthly calendar interface.
*   **Productivity Stats**: track your progress with the built-in statistics dashboard.
*   **Data Persistence**: all data is stored locally using **Room Database**, ensuring your tasks are safe and accessible offline.

## 🎨 Design Style: Neobrutalism

Routiq features:
*   **High Contrast**: Pure black text on vibrant backgrounds.
*   **Thick Borders**: Elements are outlined with distinct strokes.
*   **Geometric Shapes**: Sharp corners and structured layouts.
*   **Bold Typography**: Easy-to-read, impactful fonts.

## 🛠 Tech Stack

*   **Language**: Java
*   **Platform**: Native Android
*   **Database**: Room Database (SQLite abstraction)
*   **Architecture**: MVVM / Fragment-based
*   **UI Components**:
    *   ConstraintLayout
    *   RecyclerView
    *   Material Design Components
    *   Custom Drawable Resources (for Neobrutalist styling)
*   **Core APIs**:
    *   `AlarmManager` & `BroadcastReceiver` (Notifications)
    *   `ViewModel` & `LiveData` (State Management)

## 🚀 Getting Started

### Prerequisites
*   Android Studio Iguana or newer
*   JDK 11 or newer
*   Android SDK Platform 34 (UpsideDownCake)

### Installation
1.  **Clone the repository**:
    ```bash
    git clone https://github.com/yourusername/routiq-android.git
    ```
2.  **Open in Android Studio**:
    *   Launch Android Studio.
    *   Select "Open" and navigate to the cloned project directory.
3.  **Build the Project**:
    *   Let Gradle sync the dependencies.
    *   Click the **Run** button (green arrow) to deploy to an emulator or physical device.

## 📱 How to Use

1.  **Dashboard**: The home screen lists your current tasks.
2.  **Add Task**: Tap the Floating Action Button (+) to create a new task. Enter a title, description, date, and time.
3.  **Search**: Use the search bar at the top to filter tasks by name.
4.  **Filter**: Use the chips (All, Pending, Completed) to filter the list.
5.  **Complete**: Tap on a task item to edit or mark it as done.
6.  **Delete**: Swipe a task to the left or right to remove it permanently.
7.  **Navigation**: Use the Bottom Navigation Bar to switch between Tasks, Calendar, Stats, and Settings.

## 🤝 Contributing

Contributions are welcome! If you have ideas for new features or improvements:
1.  Fork the Project
2.  Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3.  Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4.  Push to the Branch (`git push origin feature/AmazingFeature`)
5.  Open a Pull Request

## 📄 License

Distributed under the MIT License. See `LICENSE` for more information.
