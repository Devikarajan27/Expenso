// Expense Manager Application
class ExpenseManager {
    constructor() {
        this.expenses = this.loadFromStorage('expenses') || [];
        this.budget = this.loadFromStorage('budget') || 0;
        this.categoryChart = null;
        this.trendChart = null;
        this.init();
    }

    init() {
        this.setupEventListeners();
        this.setDefaultDate();
        this.updateUI();
        this.initCharts();
    }

    setupEventListeners() {
        // Form submission
        document.getElementById('expenseForm').addEventListener('submit', (e) => {
            e.preventDefault();
            this.addExpense();
        });

        // Budget setting
        document.getElementById('setBudgetBtn').addEventListener('click', () => {
            this.setBudget();
        });

        // Filter category
        document.getElementById('filterCategory').addEventListener('change', (e) => {
            this.filterExpenses(e.target.value);
        });

        // Clear all expenses
        document.getElementById('clearAllBtn').addEventListener('click', () => {
            if (confirm('Are you sure you want to clear all expenses?')) {
                this.clearAllExpenses();
            }
        });

        // Chart tabs
        document.querySelectorAll('.chart-tab').forEach(tab => {
            tab.addEventListener('click', (e) => {
                this.switchChart(e.target.dataset.chart);
            });
        });
    }

    setDefaultDate() {
        const today = new Date().toISOString().split('T')[0];
        document.getElementById('expenseDate').value = today;
    }

    addExpense() {
        const name = document.getElementById('expenseName').value.trim();
        const amount = parseFloat(document.getElementById('expenseAmount').value);
        const category = document.getElementById('expenseCategory').value;
        const date = document.getElementById('expenseDate').value;

        if (!name || !amount || !category || !date) {
            this.showAlert('Please fill in all fields', 'danger');
            return;
        }

        const expense = {
            id: Date.now(),
            name,
            amount,
            category,
            date,
            timestamp: new Date().toISOString()
        };

        this.expenses.unshift(expense);
        this.saveToStorage('expenses', this.expenses);
        this.updateUI();
        this.checkBudgetAlerts();
        
        // Reset form
        document.getElementById('expenseForm').reset();
        this.setDefaultDate();
        
        this.showAlert('Expense added successfully!', 'success');
    }

    deleteExpense(id) {
        this.expenses = this.expenses.filter(exp => exp.id !== id);
        this.saveToStorage('expenses', this.expenses);
        this.updateUI();
        this.checkBudgetAlerts();
    }

    filterExpenses(category) {
        const expensesList = document.getElementById('expensesList');
        const filteredExpenses = category === 'all' 
            ? this.expenses 
            : this.expenses.filter(exp => exp.category === category);

        if (filteredExpenses.length === 0) {
            expensesList.innerHTML = '<div class="empty-state"><p>No expenses found in this category.</p></div>';
            return;
        }

        this.renderExpenses(filteredExpenses);
    }

    clearAllExpenses() {
        this.expenses = [];
        this.saveToStorage('expenses', this.expenses);
        this.updateUI();
        document.getElementById('alertContainer').innerHTML = '';
    }

    setBudget() {
        const budgetInput = document.getElementById('monthlyBudget');
        const budget = parseFloat(budgetInput.value);

        if (!budget || budget <= 0) {
            this.showAlert('Please enter a valid budget amount', 'danger');
            return;
        }

        this.budget = budget;
        this.saveToStorage('budget', this.budget);
        this.updateBudgetInfo();
        this.checkBudgetAlerts();
        this.showAlert('Budget set successfully!', 'success');
    }

    updateUI() {
        this.updateStats();
        this.renderExpenses(this.expenses);
        this.updateBudgetInfo();
        this.updateCharts();
        this.updateCategoryBreakdown();
    }

    updateStats() {
        const totalExpenses = this.expenses.reduce((sum, exp) => sum + exp.amount, 0);
        const balance = this.budget - totalExpenses;

        document.getElementById('totalExpenses').textContent = `₹${totalExpenses.toFixed(2)}`;
        document.getElementById('totalBalance').textContent = `₹${balance.toFixed(2)}`;
        
        // Update balance color
        const balanceEl = document.getElementById('totalBalance');
        if (balance < 0) {
            balanceEl.style.color = 'var(--danger)';
        } else {
            balanceEl.style.color = 'var(--success)';
        }
    }

    renderExpenses(expenses) {
        const expensesList = document.getElementById('expensesList');

        if (expenses.length === 0) {
            expensesList.innerHTML = '<div class="empty-state"><p>No expenses yet. Start tracking your spending!</p></div>';
            return;
        }

        expensesList.innerHTML = expenses.map(expense => `
            <div class="expense-item">
                <div class="expense-info">
                    <div class="expense-name">${expense.name}</div>
                    <div class="expense-meta">
                        <span class="expense-category">${this.getCategoryEmoji(expense.category)} ${expense.category}</span>
                        <span class="expense-date">${this.formatDate(expense.date)}</span>
                    </div>
                </div>
                <div class="expense-actions">
                    <span class="expense-amount">₹${expense.amount.toFixed(2)}</span>
                    <button class="delete-btn" onclick="expenseManager.deleteExpense(${expense.id})" title="Delete">
                        ✕
                    </button>
                </div>
            </div>
        `).join('');
    }

    updateBudgetInfo() {
        const budgetInfo = document.getElementById('budgetInfo');
        const budgetInput = document.getElementById('monthlyBudget');

        if (this.budget > 0) {
            budgetInput.value = this.budget;
            const currentMonthExpenses = this.getCurrentMonthExpenses();
            const percentage = (currentMonthExpenses / this.budget) * 100;
            const remaining = this.budget - currentMonthExpenses;

            let progressClass = '';
            if (percentage >= 100) {
                progressClass = 'danger';
            } else if (percentage >= 80) {
                progressClass = 'warning';
            }

            budgetInfo.innerHTML = `
                <div class="budget-progress">
                    <div style="display: flex; justify-content: space-between; margin-bottom: 5px;">
                        <span>Spent: ₹${currentMonthExpenses.toFixed(2)}</span>
                        <span>Budget: ₹${this.budget.toFixed(2)}</span>
                    </div>
                    <div class="progress-bar">
                        <div class="progress-fill ${progressClass}" style="width: ${Math.min(percentage, 100)}%"></div>
                    </div>
                    <div style="display: flex; justify-content: space-between; margin-top: 5px;">
                        <span style="color: var(--text-muted);">Remaining: ₹${remaining.toFixed(2)}</span>
                        <span style="color: var(--text-muted);">${percentage.toFixed(1)}%</span>
                    </div>
                </div>
            `;
        } else {
            budgetInfo.innerHTML = '<p style="color: var(--text-muted); font-size: 12px;">Set your monthly budget to track spending</p>';
        }
    }

    checkBudgetAlerts() {
        const alertContainer = document.getElementById('alertContainer');
        
        if (this.budget <= 0) {
            alertContainer.innerHTML = '';
            return;
        }

        const currentMonthExpenses = this.getCurrentMonthExpenses();
        const percentage = (currentMonthExpenses / this.budget) * 100;

        let alerts = [];

        if (percentage >= 100) {
            alerts.push({
                type: 'danger',
                title: '🚨 Budget Exceeded!',
                message: `You've exceeded your monthly budget by ₹${(currentMonthExpenses - this.budget).toFixed(2)}. Consider reducing spending.`
            });
        } else if (percentage >= 90) {
            alerts.push({
                type: 'danger',
                title: '⚠️ Critical Budget Alert!',
                message: `You've used ${percentage.toFixed(1)}% of your monthly budget. Only ₹${(this.budget - currentMonthExpenses).toFixed(2)} remaining.`
            });
        } else if (percentage >= 75) {
            alerts.push({
                type: 'warning',
                title: '⚡ Budget Warning',
                message: `You've used ${percentage.toFixed(1)}% of your monthly budget. ₹${(this.budget - currentMonthExpenses).toFixed(2)} remaining.`
            });
        }

        alertContainer.innerHTML = alerts.map((alert, index) => `
            <div class="alert ${alert.type}" id="alert-${index}">
                <div class="alert-content">
                    <div class="alert-title">${alert.title}</div>
                    <div class="alert-message">${alert.message}</div>
                </div>
                <button class="alert-close" onclick="document.getElementById('alert-${index}').remove()">✕</button>
            </div>
        `).join('');
    }

    getCurrentMonthExpenses() {
        const now = new Date();
        const currentMonth = now.getMonth();
        const currentYear = now.getFullYear();

        return this.expenses
            .filter(exp => {
                const expDate = new Date(exp.date);
                return expDate.getMonth() === currentMonth && expDate.getFullYear() === currentYear;
            })
            .reduce((sum, exp) => sum + exp.amount, 0);
    }

    initCharts() {
        const categoryCtx = document.getElementById('categoryChart').getContext('2d');
        const trendCtx = document.getElementById('trendChart').getContext('2d');

        this.categoryChart = new Chart(categoryCtx, {
            type: 'doughnut',
            data: {
                labels: [],
                datasets: [{
                    data: [],
                    backgroundColor: [
                        '#6366f1', '#8b5cf6', '#ec4899', '#f59e0b',
                        '#10b981', '#3b82f6', '#ef4444', '#6366f1'
                    ],
                    borderWidth: 0
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: true,
                plugins: {
                    legend: {
                        position: 'bottom',
                        labels: {
                            color: '#cbd5e1',
                            padding: 15,
                            font: {
                                size: 12
                            }
                        }
                    },
                    tooltip: {
                        callbacks: {
                            label: function(context) {
                                const label = context.label || '';
                                const value = context.parsed || 0;
                                return `${label}: ₹${value.toFixed(2)}`;
                            }
                        }
                    }
                }
            }
        });

        this.trendChart = new Chart(trendCtx, {
            type: 'line',
            data: {
                labels: [],
                datasets: [{
                    label: 'Daily Expenses',
                    data: [],
                    borderColor: '#6366f1',
                    backgroundColor: 'rgba(99, 102, 241, 0.1)',
                    tension: 0.4,
                    fill: true
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: true,
                plugins: {
                    legend: {
                        labels: {
                            color: '#cbd5e1'
                        }
                    }
                },
                scales: {
                    y: {
                        beginAtZero: true,
                        ticks: {
                            color: '#cbd5e1',
                            callback: function(value) {
                                return '₹' + value;
                            }
                        },
                        grid: {
                            color: '#334155'
                        }
                    },
                    x: {
                        ticks: {
                            color: '#cbd5e1'
                        },
                        grid: {
                            color: '#334155'
                        }
                    }
                }
            }
        });
    }

    updateCharts() {
        this.updateCategoryChart();
        this.updateTrendChart();
    }

    updateCategoryChart() {
        const categoryTotals = {};
        
        this.expenses.forEach(exp => {
            categoryTotals[exp.category] = (categoryTotals[exp.category] || 0) + exp.amount;
        });

        const labels = Object.keys(categoryTotals);
        const data = Object.values(categoryTotals);

        this.categoryChart.data.labels = labels.map(cat => `${this.getCategoryEmoji(cat)} ${cat}`);
        this.categoryChart.data.datasets[0].data = data;
        this.categoryChart.update();
    }

    updateTrendChart() {
        const last7Days = this.getLast7Days();
        const dailyTotals = {};

        // Initialize all days with 0
        last7Days.forEach(date => {
            dailyTotals[date] = 0;
        });

        // Sum expenses by date
        this.expenses.forEach(exp => {
            if (last7Days.includes(exp.date)) {
                dailyTotals[exp.date] += exp.amount;
            }
        });

        const labels = last7Days.map(date => this.formatShortDate(date));
        const data = last7Days.map(date => dailyTotals[date]);

        this.trendChart.data.labels = labels;
        this.trendChart.data.datasets[0].data = data;
        this.trendChart.update();
    }

    updateCategoryBreakdown() {
        const categoryBreakdown = document.getElementById('categoryBreakdown');
        const categoryTotals = {};
        const totalExpenses = this.expenses.reduce((sum, exp) => sum + exp.amount, 0);

        this.expenses.forEach(exp => {
            categoryTotals[exp.category] = (categoryTotals[exp.category] || 0) + exp.amount;
        });

        if (Object.keys(categoryTotals).length === 0) {
            categoryBreakdown.innerHTML = '';
            return;
        }

        categoryBreakdown.innerHTML = Object.entries(categoryTotals)
            .sort((a, b) => b[1] - a[1])
            .map(([category, amount]) => {
                const percentage = ((amount / totalExpenses) * 100).toFixed(1);
                return `
                    <div class="category-item">
                        <div class="category-name">${this.getCategoryEmoji(category)} ${category}</div>
                        <div class="category-amount">₹${amount.toFixed(2)}</div>
                        <div class="category-percentage">${percentage}% of total</div>
                    </div>
                `;
            })
            .join('');
    }

    switchChart(chartType) {
        document.querySelectorAll('.chart-tab').forEach(tab => {
            tab.classList.remove('active');
        });
        
        event.target.classList.add('active');

        if (chartType === 'category') {
            document.getElementById('categoryChart').style.display = 'block';
            document.getElementById('trendChart').style.display = 'none';
        } else {
            document.getElementById('categoryChart').style.display = 'none';
            document.getElementById('trendChart').style.display = 'block';
        }
    }

    getLast7Days() {
        const dates = [];
        for (let i = 6; i >= 0; i--) {
            const date = new Date();
            date.setDate(date.getDate() - i);
            dates.push(date.toISOString().split('T')[0]);
        }
        return dates;
    }

    getCategoryEmoji(category) {
        const emojis = {
            'Food': '🍔',
            'Transport': '🚗',
            'Shopping': '🛍️',
            'Entertainment': '🎮',
            'Bills': '📄',
            'Healthcare': '🏥',
            'Education': '📚',
            'Other': '📦'
        };
        return emojis[category] || '📦';
    }

    formatDate(dateString) {
        const date = new Date(dateString);
        const options = { year: 'numeric', month: 'short', day: 'numeric' };
        return date.toLocaleDateString('en-US', options);
    }

    formatShortDate(dateString) {
        const date = new Date(dateString);
        const options = { month: 'short', day: 'numeric' };
        return date.toLocaleDateString('en-US', options);
    }

    showAlert(message, type) {
        const alertContainer = document.getElementById('alertContainer');
        const alertId = `temp-alert-${Date.now()}`;
        
        const alertHTML = `
            <div class="alert ${type}" id="${alertId}">
                <div class="alert-content">
                    <div class="alert-message">${message}</div>
                </div>
                <button class="alert-close" onclick="document.getElementById('${alertId}').remove()">✕</button>
            </div>
        `;
        
        alertContainer.insertAdjacentHTML('afterbegin', alertHTML);
        
        setTimeout(() => {
            const alertEl = document.getElementById(alertId);
            if (alertEl) alertEl.remove();
        }, 5000);
    }

    saveToStorage(key, data) {
        localStorage.setItem(key, JSON.stringify(data));
    }

    loadFromStorage(key) {
        const data = localStorage.getItem(key);
        return data ? JSON.parse(data) : null;
    }
}

// Initialize the app
let expenseManager;
document.addEventListener('DOMContentLoaded', () => {
    expenseManager = new ExpenseManager();
});
