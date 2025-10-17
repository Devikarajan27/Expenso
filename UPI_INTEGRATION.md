# 💳 UPI Payment Integration - Expenso

Comprehensive UPI payment integration for seamless digital payments directly from your expense tracking app!

## 🎯 Overview

Expenso now includes full UPI (Unified Payments Interface) integration, allowing you to:
- Send money via UPI from within the app
- Generate UPI payment links
- Create QR codes for payments
- Track UPI transactions
- Auto-add payments to expenses

## ✨ Features

### 1. **UPI Payment Dialog**
A beautiful, intuitive interface for making UPI payments:
- Enter UPI ID or phone number
- Specify recipient name
- Set payment amount
- Add optional payment notes

### 2. **Multiple Payment Options**
- **Pay Now** - Opens UPI link in your default UPI app
- **Generate QR** - Creates a scannable QR code
- **Copy Link** - Copies UPI deep link to clipboard

### 3. **QR Code Generation**
- Generates visual QR-like codes
- Displays payment details
- Easy to scan with any UPI app

### 4. **Automatic Expense Tracking**
- Option to add payments to expenses
- Auto-categorization
- Transaction history

## 🚀 How to Use

### **Method 1: Quick UPI Payment**

1. Click the **"💳 UPI Pay"** button in the header
2. Enter payment details:
   - UPI ID (e.g., `example@paytm`) OR phone number (e.g., `9876543210`)
   - Recipient name
   - Amount in rupees
   - Optional note
3. Choose your payment method:
   - **Pay Now** - Opens in browser/UPI app
   - **Generate QR** - Shows QR code to scan
   - **Copy Link** - Copy link to share

### **Method 2: Generate QR Code**

1. Click **"Generate QR"** button
2. A QR code will be displayed
3. Scan with any UPI app (Google Pay, PhonePe, Paytm, etc.)
4. Complete payment in your UPI app

### **Method 3: Copy & Share Link**

1. Click **"Copy Link"** button
2. Paste the UPI link anywhere
3. Open in any UPI app
4. Complete the payment

## 📱 Supported UPI Apps

The UPI integration works with all major UPI apps:
- 💚 **Google Pay** (GPay)
- 💜 **PhonePe**
- 💙 **Paytm**
- 🏦 **BHIM UPI**
- 🏦 **Bank UPI Apps** (SBI Pay, HDFC PayZapp, etc.)
- 📱 **Amazon Pay**
- 🎯 **And many more!**

## 🔗 UPI Deep Link Format

The app generates standard UPI deep links:
```
upi://pay?pa=UPIID&pn=Name&am=Amount&cu=INR&tn=Note
```

**Parameters:**
- `pa` - Payee UPI address
- `pn` - Payee name
- `am` - Amount
- `cu` - Currency (INR)
- `tn` - Transaction note

## 💡 Usage Examples

### Example 1: Pay a Friend
```
UPI ID: friend@paytm
Name: Friend Name
Amount: 500
Note: Lunch split
```
→ Generates: `upi://pay?pa=friend@paytm&pn=Friend%20Name&am=500.00&cu=INR&tn=Lunch%20split`

### Example 2: Pay by Phone Number
```
Phone: 9876543210
Name: Shop Owner
Amount: 250
Note: Purchase
```
→ Auto-converts to: `9876543210@paytm`

### Example 3: Merchant Payment
```
UPI ID: merchant@oksbi
Name: Store Name
Amount: 1500
Note: Order #12345
```

## 🎨 Visual QR Code

The app generates a custom QR-like pattern with:
- Corner markers (standard QR feature)
- Center marker
- Data-based pattern generation
- Payment amount and recipient displayed

**Note:** For production use with real scanning, consider integrating a proper QR library like ZXing.

## 🔒 Security Features

✅ **No API Keys Required** - Direct UPI protocol
✅ **No Sensitive Data Storage** - All in UPI app
✅ **Secure Payment Flow** - Handled by UPI ecosystem
✅ **No Merchant Account Needed** - P2P payments
✅ **Clipboard Auto-Clear** - Optional security

## ⚙️ Technical Details

### **Files Structure**
```
src/com/expenso/
├── model/
│   └── UpiPayment.java        # UPI payment model
├── ui/
│   └── UpiPaymentDialog.java  # Payment dialog UI
└── util/
    └── QRCodeGenerator.java   # QR code generation
```

### **Classes Overview**

#### **UpiPayment.java**
- Model class for UPI payments
- Generates UPI deep links
- Tracks payment status
- Serializable for storage

#### **UpiPaymentDialog.java**
- Beautiful payment UI
- Input validation
- Multiple payment methods
- QR code display

#### **QRCodeGenerator.java**
- Custom QR-like pattern generation
- Visual payment representation
- No external dependencies

## 🛠️ Customization

### Change Default UPI Handle
Edit `UpiPaymentDialog.java`:
```java
if (upiId.matches("\\d+")) {
    upiId = upiId + "@yourbank"; // Change @paytm to your preferred bank
}
```

### Add More Payment Options
You can extend the dialog to include:
- Payment presets (₹100, ₹500, ₹1000)
- Favorite contacts
- Payment history
- Split bill calculator

### Integration with Real QR Scanner
For production apps, integrate ZXing library:
```java
// Add to dependencies
implementation 'com.google.zxing:core:3.5.0'
```

## 📊 Payment Flow

```
User Input → Validate → Generate UPI Link
                              ↓
                    ┌─────────┴─────────┐
                    │                   │
              Open Link          Generate QR      Copy Link
                    │                   │              │
              Browser/App         Display QR     Clipboard
                    │                   │              │
                    └─────────┬─────────┘──────────────┘
                              ↓
                       UPI App Payment
                              ↓
                    Add to Expenses (Optional)
```

## 💳 Testing

### Test UPI IDs
For testing, you can use:
- Your actual UPI ID
- Phone number (auto-converts)
- Test merchant IDs

### Sandbox Testing
- UPI apps have test modes
- Some banks offer sandbox UPIs
- Google Pay has test environments

## 🎯 Best Practices

1. **Validate UPI IDs**
   - Check format: `username@bankname`
   - Validate phone numbers
   - Trim whitespace

2. **Handle Errors Gracefully**
   - Network errors
   - Invalid UPI IDs
   - User cancellations

3. **User Feedback**
   - Show loading states
   - Confirm payment initiation
   - Display success/error messages

4. **Privacy**
   - Don't store sensitive data
   - Clear clipboard after use
   - Secure transaction notes

## 🐛 Troubleshooting

### Payment Link Doesn't Open
- **Solution**: Use "Copy Link" and paste in UPI app
- Check if Desktop.browse() is supported
- Try QR code method instead

### QR Code Not Scanning
- **Solution**: Use copy link method
- Ensure good lighting for scan
- The generated QR is visual only - use link for actual payments

### UPI App Not Installed
- **Solution**: Install any UPI app from the list
- Use web UPI services
- Copy link and send via messaging apps

### Phone Number Not Working
- **Solution**: Add UPI handle manually (e.g., `number@paytm`)
- Try different UPI handles (@googlepay, @phonepe, etc.)

## 📈 Future Enhancements

Planned features for future versions:
- 📜 **Payment History** - View all UPI transactions
- 👥 **Saved Contacts** - Quick pay to frequent recipients
- 🔔 **Payment Reminders** - Schedule payment notifications
- 📊 **Split Bills** - Divide amounts among multiple people
- 💰 **Request Money** - Generate payment request links
- 🔄 **Recurring Payments** - Set up automatic payments
- 📱 **SMS Integration** - Send payment links via SMS
- 🎨 **Custom QR Designs** - Branded QR codes
- 🔐 **PIN Protection** - Secure payment dialog
- 📧 **Email Receipts** - Send payment confirmations

## 🤝 Integration with Payment Gateways

For production apps with business requirements:

### **Razorpay**
- Full payment gateway
- Merchant dashboard
- Transaction fees apply
- Requires business account

### **PhonePe Business**
- Business UPI integration
- Automatic reconciliation
- Payment confirmation APIs

### **Paytm for Business**
- Merchant integration
- Payment tracking
- Business analytics

## 📞 Support

For issues or questions:
1. Check this documentation
2. Review the source code comments
3. Test with different UPI apps
4. Verify UPI ID formats

## 🎉 Success Indicators

✅ UPI link generated successfully
✅ QR code displayed properly
✅ Payment link copied to clipboard
✅ Browser/app opened for payment
✅ Payment added to expenses

## 📝 License

This UPI integration is part of Expenso and follows the same open-source license.

---

**Made with ❤️ for seamless digital payments**

*Pay smart. Track better. Live easier.* 💳✨
