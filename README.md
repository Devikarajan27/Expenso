# 💰 Expenso - Finance Management App

A modern, professional finance management application that helps you track daily expenses, visualize spending patterns, and manage your budget effectively.

## ✨ Features

### 📊 Expense Tracking
- Add expenses with description, amount, category, and date
- View all expenses in a clean, organized list
- Filter expenses by category
- Delete individual expenses or clear all at once

### 📈 Data Visualization
- **Category Chart**: Doughnut chart showing spending by category
- **Trend Chart**: Line chart displaying daily expenses over the last 7 days
- **Category Breakdown**: Detailed breakdown with percentages

### 💵 Budget Management
- Set monthly budget limits
- Real-time budget tracking with progress bar
- Visual indicators (green/yellow/red) based on spending

### 🔔 Smart Budget Alerts
- **75% Warning**: Get notified when you've used 75% of your budget
- **90% Critical Alert**: Urgent notification at 90% usage
- **Budget Exceeded**: Alert when you've exceeded your budget

### 💾 Data Persistence
- All data stored locally in your browser
- Automatic saving on every action
- Data persists across sessions

### 🎨 Modern UI/UX
- Clean, minimal dark theme design
- Professional gradient effects
- Smooth animations and transitions
- Fully responsive for mobile, tablet, and desktop
- Intuitive user interface

## 🚀 Getting Started

1. **Open the App**
   - Simply open `index.html` in any modern web browser
   - No installation or setup required!

2. **Set Your Budget**
   - Scroll to the "Budget Settings" section
   - Enter your monthly budget amount
   - Click "Set Budget"

3. **Add Expenses**
   - Fill in the expense form:
     - Description (e.g., "Groceries")
     - Amount
     - Category
     - Date
   - Click "Add Expense"

4. **Track Your Spending**
   - View your expenses in the list
   - Check the charts for visual insights
   - Monitor your budget progress bar
   - Get alerts when approaching budget limits

## 📱 Categories

The app includes 8 pre-defined categories:
- 🍔 Food
- 🚗 Transport
- 🛍️ Shopping
- 🎮 Entertainment
- 📄 Bills
- 🏥 Healthcare
- 📚 Education
- 📦 Other

## 🎯 Key Benefits

1. **Simple & Intuitive**: No learning curve - start tracking immediately
2. **Visual Insights**: Understand spending patterns at a glance
3. **Budget Control**: Stay on top of your finances with smart alerts
4. **Privacy First**: All data stored locally in your browser
5. **No Sign-Up**: No accounts, no cloud storage, complete privacy
6. **Offline Ready**: Works completely offline after first load

## 🔧 Technical Details

### Technologies Used
- **HTML5**: Semantic structure
- **CSS3**: Modern styling with CSS variables and animations
- **JavaScript (ES6+)**: Object-oriented programming with classes
- **Chart.js**: Beautiful, responsive charts
- **LocalStorage API**: Client-side data persistence

### Browser Support
- Chrome (recommended)
- Firefox
- Safari
- Edge
- Any modern browser with ES6 support

## 📊 Data Management

### Storage
All data is stored in your browser's localStorage:
- `expenses`: Array of all expense records
- `budget`: Your monthly budget value

### Data Structure
```javascript
{
  id: timestamp,
  name: "Expense description",
  amount: 25.50,
  category: "Food",
  date: "2025-10-02",
  timestamp: "ISO timestamp"
}
```

### Clearing Data
- Click "Clear All" to remove all expenses
- Browser's localStorage can be cleared manually via browser settings

## 🎨 Customization

The app uses CSS variables for easy customization. Edit `style.css`:

```css
:root {
    --primary: #6366f1;        /* Main color */
    --secondary: #8b5cf6;      /* Secondary color */
    --success: #10b981;        /* Success/positive */
    --danger: #ef4444;         /* Danger/negative */
    --warning: #f59e0b;        /* Warning */
    /* ... more variables */
}
```

## 🌟 Tips for Best Use

1. **Daily Entry**: Add expenses daily for accurate tracking
2. **Set Realistic Budgets**: Start with your actual spending, then optimize
3. **Use Categories**: Properly categorize expenses for better insights
4. **Check Trends**: Review the trend chart weekly to spot patterns
5. **Budget Alerts**: Pay attention to alerts to stay within budget

## 📝 Future Enhancements

Potential features for future versions:
- Multiple budget periods (weekly, yearly)
- Income tracking
- Recurring expenses
- Export data (CSV, PDF)
- Multiple currency support
- Expense notes/attachments
- Advanced filtering and search
- Custom categories

## 🤝 Contributing

This is an open project. Feel free to:
- Report bugs
- Suggest features
- Improve the code
- Enhance the design

## 📄 License

Free to use for personal and commercial purposes.

## 💡 Support

For issues or questions:
- Check the code comments for implementation details
- Review the browser console for any errors
- Ensure JavaScript is enabled in your browser

---

**Made with ❤️ for better financial management**

*Track smart. Spend wisely. Live better.*
