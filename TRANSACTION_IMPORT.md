# 📥 Automatic Transaction Import - Expenso

Never manually type your expenses again! Import transactions automatically from bank statements and emails.

## 🎯 Overview

Expenso now features **powerful automatic import** capabilities:
1. **📄 Bank Statement Import** - Upload CSV files from your bank
2. **📧 Email Transaction Parser** - Paste transaction emails and extract data
3. **🤖 Smart Categorization** - AI-powered automatic expense categorization
4. **👁️ Preview & Review** - Check before importing

## ✨ Features

### **1. Bank Statement Parser**
- ✅ Upload CSV/TXT files from any bank
- ✅ Auto-detect column formats
- ✅ Supports multiple date formats
- ✅ Handles Indian currency (₹)
- ✅ Detects UPI, ATM, Card transactions
- ✅ Smart transaction type detection

**Supported Banks:**
- State Bank of India (SBI)
- HDFC Bank
- ICICI Bank
- Axis Bank
- Kotak Mahindra Bank
- Punjab National Bank (PNB)
- And many more!

### **2. Email Transaction Parser**
- ✅ Parse transaction notification emails
- ✅ Extract amount, date, description automatically
- ✅ Detects UPI apps (Google Pay, PhonePe, Paytm)
- ✅ Works with all major bank emails
- ✅ Supports multiple email formats

**Supported Sources:**
- Google Pay (GPay)
- PhonePe
- Paytm
- Amazon Pay
- All major bank transaction emails

### **3. Smart Auto-Categorization**
Automatically categorizes transactions based on keywords:

**Food** 🍔
- Swiggy, Zomato, Restaurant, Cafe, Hotel, Dominos, McDonald's

**Transport** 🚗
- Uber, Ola, Rapido, Fuel, Petrol, Metro, Bus, Taxi

**Shopping** 🛍️
- Amazon, Flipkart, Myntra, Shop, Mall, Store

**Entertainment** 🎮
- Netflix, Prime Video, Hotstar, Movie, Cinema, Spotify

**Bills** 📄
- Electricity, Water, Gas, Internet, Mobile, Recharge

**Healthcare** 🏥
- Hospital, Doctor, Medical, Pharmacy, Medicine

**Education** 📚
- School, College, Course, Books, Udemy, Coursera

**Other** 📦
- Everything else

## 🚀 How to Use

### **Option 1: Import Bank Statement (CSV)**

#### **Step 1: Download Your Bank Statement**
1. Log in to your net banking
2. Go to "Account Statement" or "Download Statement"
3. Select date range (e.g., last 30 days)
4. Download as **CSV** or **Excel** format
5. If Excel, save/export as CSV

#### **Step 2: Import to Expenso**
1. Click **"📥 Import"** button in the app header
2. Go to **"📄 Bank Statement"** tab
3. Click **"📁 Select Bank Statement (CSV)"**
4. Choose your downloaded CSV file
5. Wait for parsing (instant!)

#### **Step 3: Review & Import**
1. Go to **"👁️ Preview"** tab (opens automatically)
2. Review all detected transactions
3. Check categories are correct
4. Uncheck any transactions you don't want
5. Click **"✅ Import Selected"**
6. Done! All expenses added automatically

---

### **Option 2: Import from Email**

#### **Step 1: Find Transaction Email**
1. Open Gmail/Outlook/any email app
2. Search for: `"debited" OR "credited" OR "UPI"`
3. Find bank/UPI transaction notification
4. Example: "INR 500 debited from your account"

#### **Step 2: Copy Email Content**
1. Select and copy the entire email (Ctrl+A, Ctrl+C)
2. Include both subject and body
3. Example:
   ```
   Account Debited: INR 500.00
   Your account has been debited for Rs. 500.00
   via UPI to Zomato on 02/10/2025
   UPI Ref No: 123456789
   ```

#### **Step 3: Parse in Expenso**
1. Click **"📥 Import"** button
2. Go to **"📧 Email Import"** tab
3. Paste email content in text area
4. Click **"🔍 Parse Email Transaction"**

#### **Step 4: Review & Import**
1. Transaction extracted automatically!
2. Review in **"👁️ Preview"** tab
3. Category assigned automatically
4. Click **"✅ Import Selected"**

---

## 📋 Bank Statement Format Requirements

Your CSV should have these columns (in any order):
- **Date** - Transaction date
- **Description** / **Narration** / **Particulars** - Transaction details
- **Debit** / **Withdrawal** - Money out
- **Credit** / **Deposit** - Money in
- **Balance** (optional) - Closing balance

### **Example CSV Format:**
```csv
Date,Description,Debit,Credit,Balance
02/10/2025,UPI-ZOMATO-123456,500.00,,45000.00
01/10/2025,ATM WITHDRAWAL,2000.00,,45500.00
30/09/2025,SALARY CREDIT,,50000.00,47500.00
```

### **Supported Date Formats:**
- `dd/MM/yyyy` (02/10/2025)
- `dd-MM-yyyy` (02-10-2025)
- `yyyy-MM-dd` (2025-10-02)
- `dd MMM yyyy` (02 Oct 2025)
- `dd-MMM-yyyy` (02-Oct-2025)

---

## 📧 Email Format Examples

### **Example 1: UPI Transaction Email**
```
Subject: Payment sent via UPI

Dear Customer,
INR 250.00 has been debited from your account
via UPI to Uber India on 02/10/2025
UPI Ref No: 987654321
```
**Detected:** ₹250.00, UPI Payment, Transport category

### **Example 2: Bank Debit Email**
```
Subject: Account Debited

Your account XX1234 has been debited for Rs 1,500.00
at Amazon India on 01/10/2025
Transaction Ref: TXN123456
```
**Detected:** ₹1,500.00, Card Payment, Shopping category

### **Example 3: Google Pay Email**
```
Subject: You sent ₹500 to Friend Name

₹500.00 was sent from Google Pay
to Friend Name
on October 2, 2025
```
**Detected:** ₹500.00, UPI Sent, Other category

---

## 🎨 Smart Features

### **Automatic Transaction Type Detection**
- **UPI** - Contains "UPI", "GooglePay", "PhonePe", "Paytm"
- **ATM Withdrawal** - Contains "ATM", "Cash Withdrawal"
- **Card Payment** - Contains "Card", "POS", "Swipe"
- **Bank Transfer** - Contains "NEFT", "RTGS", "IMPS"

### **Intelligent Amount Parsing**
- Handles: `Rs.`, `INR`, `₹`, commas
- Examples: `₹1,000.00`, `Rs 500`, `INR 2500.00`

### **Flexible Date Recognition**
- Multiple format support
- Auto-detection of day/month/year

### **Description Extraction**
- Finds merchant names
- Extracts recipient info
- Preserves reference numbers

---

## 🔧 Advanced Usage

### **Bulk Import Multiple Months**
1. Download statements for multiple months
2. Merge CSV files (keep header from first file only)
3. Import the merged file
4. All transactions imported at once!

### **Filter Before Import**
1. After parsing, use Preview tab
2. Uncheck transactions you don't want
3. Only selected ones will be imported

### **Re-categorize After Import**
- Imported transactions can be edited manually
- Change category from expense table
- Smart categorization is a suggestion!

### **Combine Multiple Sources**
1. Import bank statement first
2. Then parse emails for missing transactions
3. Preview shows all transactions
4. Import together!

---

## 💡 Tips & Tricks

### **For Best Results:**

1. **Download Clean CSVs**
   - Avoid PDF conversions
   - Use direct CSV export from bank
   - Remove summary rows if present

2. **Email Import Tips**
   - Copy complete email text
   - Include subject line
   - Paste one email at a time

3. **Check Categories**
   - Review auto-assigned categories
   - They're based on keywords
   - Edit if needed after import

4. **Regular Imports**
   - Import weekly or monthly
   - Keeps data fresh
   - Less manual work!

5. **Backup First**
   - Data is saved automatically
   - But good to export occasionally
   - Just in case!

---

## 🐛 Troubleshooting

### **Problem: No transactions detected in CSV**
**Solutions:**
- Check CSV has proper headers
- Ensure date column is recognized
- Try different date formats
- Remove extra header/footer rows

### **Problem: Dates are wrong**
**Solutions:**
- Check date format in CSV
- Use dd/MM/yyyy format if possible
- Update dates manually after import

### **Problem: Amounts not detected**
**Solutions:**
- Ensure amounts are in Debit/Credit columns
- Remove currency symbols from numbers
- Use decimal point, not comma (500.00 not 500,00)

### **Problem: Email not parsing**
**Solutions:**
- Make sure it's a transaction email
- Copy complete email with subject
- Try subject line only if full email fails
- Check email contains amount (Rs/₹/INR)

### **Problem: Wrong categories**
**Solutions:**
- Auto-categorization is AI-suggested
- Review and change after import
- Categories improve with more data

---

## 📊 Data Privacy & Security

### **Your Data is Safe:**
- ✅ **No Cloud Upload** - Everything stays on your computer
- ✅ **No Internet Required** - Parsing happens locally
- ✅ **No APIs** - Direct file parsing only
- ✅ **No Logging** - Your transactions aren't tracked
- ✅ **Local Storage** - Saved in your user directory only

### **Files are Not Stored:**
- CSV files are read and discarded
- Only transaction data is saved
- Original files remain yours
- Delete anytime from Downloads

---

## 🎯 Use Cases

### **1. Monthly Budget Tracking**
- Download last month's statement
- Import to Expenso
- Instant spending analysis
- Compare against budget

### **2. Tax Preparation**
- Import full year's transactions
- Auto-categorized by type
- Easy to find deductibles
- Export filtered data

### **3. Expense Reports**
- Import business transactions
- Filter by category
- Generate reports
- Submit for reimbursement

### **4. Financial Planning**
- Import 3-6 months data
- Analyze spending patterns
- Identify savings opportunities
- Set realistic budgets

---

## 🔮 Future Enhancements

Coming soon:
- 📱 **PDF Statement Support** - Parse PDF statements
- 🔄 **Auto-sync** - Connect bank APIs (with permission)
- 📊 **Excel Support** - Direct .xlsx import
- 🌐 **Gmail Integration** - Auto-fetch transaction emails
- 🤖 **Better AI** - Improved categorization
- 📝 **Custom Rules** - Define your own category rules
- 🔔 **Duplicate Detection** - Prevent double imports
- 📈 **Import History** - Track what you've imported

---

## 📞 Support

### **Need Help?**
1. Check examples above
2. Try with a small CSV first
3. Review error messages
4. Ensure file format is correct

### **Feature Requests?**
- More bank formats?
- Different currencies?
- Additional categories?
- Let us know!

---

## 📝 Quick Reference

### **Keyboard Shortcuts** (In Import Dialog)
- `Tab` - Switch between tabs
- `Space` - Select/Deselect transaction
- `Ctrl+A` - Select all (in text area)
- `Ctrl+V` - Paste email content
- `Enter` - Confirm actions

### **Buttons**
- 📥 **Import** - Open import dialog
- 📁 **Select File** - Choose CSV
- 🔍 **Parse Email** - Extract transaction
- 👁️ **Preview** - Review before import
- ✅ **Import Selected** - Add to expenses

---

**Made with ❤️ for effortless expense tracking**

*Import once. Track forever. Never type again.* 📥✨
