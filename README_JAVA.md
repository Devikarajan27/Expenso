# 💰 Expenso - Java Swing Finance Management Application

A professional desktop finance management application built with Java Swing that helps you track daily expenses, visualize spending patterns, and manage your budget effectively.

## ✨ Features

### 📊 Expense Tracking
- Add expenses with description, amount, category, and date
- View all expenses in a sortable table
- Filter expenses by category
- Delete individual expenses or clear all at once
- Real-time updates

### 📈 Data Visualization
- **Pie Chart**: Visual spending breakdown by category
- **Bar Chart**: 7-day expense trend analysis
- **Category Breakdown**: Detailed statistics with percentages
- Toggle between different chart views

### 💵 Budget Management
- Set monthly budget limits
- Real-time budget tracking with progress bar
- Visual indicators (green/yellow/red) based on spending level
- Current month expense tracking

### 🔔 Smart Budget Alerts
- **75% Warning**: Early warning when approaching budget limit
- **90% Critical Alert**: Urgent notification at high usage
- **Budget Exceeded**: Alert when budget is exceeded
- Dynamic alert display at top of application

### 💾 Data Persistence
- Automatic data saving to local files
- Data stored in user home directory (`.expenso` folder)
- Expenses and budget saved separately
- Data persists across application restarts

### 🎨 Modern UI/UX
- Professional dark theme design
- Clean, minimal interface
- Smooth interactions
- Custom-styled components
- Gradient button effects
- Color-coded categories

## 🚀 Getting Started

### Prerequisites
- **Java JDK 8 or higher** installed
- Java `bin` directory added to your system PATH

### Installation & Running

#### Windows

1. **Compile the Application**
   ```cmd
   compile.bat
   ```
   This will compile all Java source files to the `bin` directory.

2. **Run the Application**
   ```cmd
   run.bat
   ```
   This will launch the Expenso Finance Manager.

#### Manual Compilation (All Platforms)

1. **Compile**
   ```bash
   javac -d bin -sourcepath src src/com/expenso/*.java src/com/expenso/model/*.java src/com/expenso/data/*.java src/com/expenso/ui/*.java
   ```

2. **Run**
   ```bash
   java -cp bin com.expenso.Main
   ```

## 📂 Project Structure

```
Expenso/
├── src/
│   └── com/
│       └── expenso/
│           ├── Main.java                 # Application entry point
│           ├── model/
│           │   └── Expense.java          # Expense data model
│           ├── data/
│           │   └── DataManager.java      # Data persistence layer
│           └── ui/
│               ├── ExpensoApp.java       # Main application window
│               └── ChartPanel.java       # Chart visualization panel
├── bin/                                  # Compiled classes (generated)
├── compile.bat                           # Windows compilation script
├── run.bat                               # Windows run script
└── README_JAVA.md                        # This file
```

## 📱 Categories

The app includes 8 pre-defined expense categories:
- 🍔 Food
- 🚗 Transport
- 🛍️ Shopping
- 🎮 Entertainment
- 📄 Bills
- 🏥 Healthcare
- 📚 Education
- 📦 Other

## 💡 How to Use

### 1. Set Your Budget
- Enter your monthly budget in the "Budget Settings" section
- Click "SET BUDGET"
- View your budget progress bar

### 2. Add Expenses
- Fill in the expense details:
  - **Description**: e.g., "Groceries"
  - **Amount**: Enter the cost
  - **Category**: Select from dropdown
  - **Date**: Use format yyyy-MM-dd
- Click "ADD EXPENSE"

### 3. View and Analyze
- Check the expense table for recent transactions
- Use the filter dropdown to view specific categories
- Switch between chart views (By Category / Trend)
- Review category breakdown statistics

### 4. Manage Expenses
- Click "Delete" button in table rows to remove expenses
- Click "CLEAR ALL" to remove all expenses (with confirmation)
- Budget alerts appear automatically when thresholds are reached

## 📊 Data Storage

All data is stored locally in:
```
Windows: C:\Users\[YourUsername]\.expenso\
Mac/Linux: /home/[username]/.expenso/
```

Files:
- `expenses.dat` - Serialized expense data
- `budget.dat` - Budget information

## 🎨 Color Scheme

- **Primary**: Indigo (#6366f1)
- **Secondary**: Purple (#8b5cf6)
- **Success**: Green (#10b981)
- **Danger**: Red (#ef4444)
- **Warning**: Orange (#f59e0b)
- **Background**: Dark theme with navy shades

## 🔧 Technical Details

### Technologies
- **Language**: Java 8+
- **GUI Framework**: Swing
- **Layouts**: BorderLayout, BoxLayout, GridLayout
- **Data Storage**: Java Serialization
- **Graphics**: Java 2D Graphics API

### Key Components

#### Expense Model (`Expense.java`)
- Represents individual expense entries
- Category enum with emoji icons
- Formatted date and amount display

#### Data Manager (`DataManager.java`)
- Handles data persistence
- File I/O operations
- Expense calculations and filtering

#### Main Application (`ExpensoApp.java`)
- Main JFrame window
- UI component management
- Event handling
- Budget alerts

#### Chart Panel (`ChartPanel.java`)
- Custom pie chart implementation
- Custom bar chart implementation
- Category statistics display

## 🌟 Tips for Best Use

1. **Daily Tracking**: Add expenses as they occur for accurate records
2. **Realistic Budgets**: Start with your actual spending, then optimize
3. **Category Discipline**: Consistently use the same categories
4. **Regular Review**: Check the trend chart weekly to spot patterns
5. **Heed Alerts**: Pay attention to budget warnings to stay on track

## ⚙️ Customization

### Modifying Colors
Edit the static Color fields in `ExpensoApp.java` and `ChartPanel.java`:
```java
private static final Color PRIMARY = new Color(99, 102, 241);
private static final Color SUCCESS = new Color(16, 185, 129);
// ... etc
```

### Adding Categories
Modify the `Category` enum in `Expense.java`:
```java
public enum Category {
    FOOD("🍔 Food"),
    // Add new categories here
    CUSTOM("🎯 Custom");
}
```

## 🐛 Troubleshooting

### Application won't compile
- Ensure Java JDK is installed: `java -version` and `javac -version`
- Check that JAVA_HOME is set correctly
- Verify all source files are present

### Data not saving
- Check write permissions in your home directory
- Ensure `.expenso` folder can be created
- Check console for error messages

### Charts not displaying
- Ensure expenses have been added
- Check date ranges for trend chart (last 7 days)
- Try resizing the window to trigger repaint

## 📝 Future Enhancements

Potential features for future versions:
- Export data to CSV/PDF
- Multiple budget periods (weekly, yearly)
- Recurring expense templates
- Income tracking
- Advanced filtering and search
- Expense categories customization
- Data backup/restore
- Multiple user profiles

## 🤝 Contributing

Feel free to:
- Report bugs or issues
- Suggest new features
- Improve the code
- Enhance the UI/UX

## 📄 License

Free to use for personal and commercial purposes.

## 💡 Support

For issues:
- Check this README for solutions
- Review console output for error messages
- Ensure Java version compatibility
- Verify file permissions for data storage

---

**Built with Java Swing for reliable desktop performance**

*Track smart. Spend wisely. Live better.* 💰✨
