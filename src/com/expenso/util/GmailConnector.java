package com.expenso.util;

import com.expenso.model.Transaction;
import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.search.*;
import java.util.*;
import java.util.Properties;
import java.time.LocalDate;

/**
 * Gmail connector for automatic transaction email fetching
 * Uses JavaMail API with OAuth2 or App Password
 */
public class GmailConnector {
    
    private String email;
    private String appPassword;
    private Session session;
    private Store store;
    
    /**
     * Configure Gmail connection
     * @param email User's Gmail address
     * @param appPassword Gmail App Password (not regular password)
     */
    public GmailConnector(String email, String appPassword) {
        this.email = email;
        this.appPassword = appPassword;
    }
    
    /**
     * Connect to Gmail
     */
    public boolean connect() {
        try {
            Properties props = new Properties();
            props.put("mail.store.protocol", "imaps");
            props.put("mail.imap.host", "imap.gmail.com");
            props.put("mail.imap.port", "993");
            props.put("mail.imap.ssl.enable", "true");
            props.put("mail.imap.ssl.trust", "imap.gmail.com");
            props.put("mail.imap.timeout", "10000");
            props.put("mail.imap.connectiontimeout", "10000");
            
            session = Session.getInstance(props);
            store = session.getStore("imaps");
            store.connect("imap.gmail.com", email, appPassword);
            
            return store.isConnected();
        } catch (Exception e) {
            System.err.println("Gmail connection error: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Fetch transaction emails from last N days
     */
    public List<Transaction> fetchTransactionEmails(int daysBack) {
        List<Transaction> transactions = new ArrayList<>();
        
        try {
            if (store == null || !store.isConnected()) {
                if (!connect()) {
                    return transactions;
                }
            }
            
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);
            
            // Search for transaction emails
            SearchTerm searchTerm = createTransactionSearchTerm(daysBack);
            Message[] messages = inbox.search(searchTerm);
            
            System.out.println("Found " + messages.length + " transaction emails");
            
            for (Message message : messages) {
                try {
                    String subject = message.getSubject();
                    String content = getTextFromMessage(message);
                    
                    if (subject != null && content != null) {
                        List<Transaction> parsed = EmailTransactionParser.parseEmailContent(content, subject);
                        transactions.addAll(parsed);
                    }
                } catch (Exception e) {
                    System.err.println("Error parsing message: " + e.getMessage());
                }
            }
            
            inbox.close(false);
            
        } catch (Exception e) {
            System.err.println("Error fetching emails: " + e.getMessage());
            e.printStackTrace();
        }
        
        return transactions;
    }
    
    /**
     * Create search term for transaction emails
     */
    private SearchTerm createTransactionSearchTerm(int daysBack) throws Exception {
        // Date range
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -daysBack);
        Date fromDate = cal.getTime();
        
        SearchTerm dateSearch = new ReceivedDateTerm(ComparisonTerm.GE, fromDate);
        
        // Common bank/UPI email senders
        String[] senders = {
            "@sbi.co.in", "@hdfcbank.com", "@icicibank.com", "@axisbank.com",
            "@kotak.com", "@pnb.co.in", "@yesbank.in", "@indusind.com",
            "paytm.com", "phonepe.com", "google.com", "amazon.com",
            "alerts", "notification", "noreply"
        };
        
        SearchTerm[] fromSearches = new SearchTerm[senders.length];
        for (int i = 0; i < senders.length; i++) {
            fromSearches[i] = new FromStringTerm(senders[i]);
        }
        SearchTerm fromSearch = new OrTerm(fromSearches);
        
        // Keywords in subject
        String[] keywords = {
            "debited", "credited", "transaction", "payment", "UPI",
            "spent", "received", "withdrawn", "transferred"
        };
        
        SearchTerm[] subjectSearches = new SearchTerm[keywords.length];
        for (int i = 0; i < keywords.length; i++) {
            subjectSearches[i] = new SubjectTerm(keywords[i]);
        }
        SearchTerm keywordSearch = new OrTerm(subjectSearches);
        
        // Combine: (Date AND From) AND Keywords
        SearchTerm combined = new AndTerm(
            new AndTerm(dateSearch, fromSearch),
            keywordSearch
        );
        
        return combined;
    }
    
    /**
     * Extract text content from email message
     */
    private String getTextFromMessage(Message message) throws Exception {
        String result = "";
        
        if (message.isMimeType("text/plain")) {
            result = message.getContent().toString();
        } else if (message.isMimeType("text/html")) {
            result = stripHtml(message.getContent().toString());
        } else if (message.isMimeType("multipart/*")) {
            MimeMultipart multipart = (MimeMultipart) message.getContent();
            result = getTextFromMultipart(multipart);
        }
        
        return result;
    }
    
    /**
     * Extract text from multipart email
     */
    private String getTextFromMultipart(MimeMultipart multipart) throws Exception {
        StringBuilder result = new StringBuilder();
        
        for (int i = 0; i < multipart.getCount(); i++) {
            BodyPart bodyPart = multipart.getBodyPart(i);
            
            if (bodyPart.isMimeType("text/plain")) {
                result.append(bodyPart.getContent().toString());
            } else if (bodyPart.isMimeType("text/html")) {
                result.append(stripHtml(bodyPart.getContent().toString()));
            } else if (bodyPart.getContent() instanceof MimeMultipart) {
                result.append(getTextFromMultipart((MimeMultipart) bodyPart.getContent()));
            }
        }
        
        return result.toString();
    }
    
    /**
     * Strip HTML tags
     */
    private String stripHtml(String html) {
        return html.replaceAll("<[^>]*>", " ")
                   .replaceAll("&nbsp;", " ")
                   .replaceAll("&amp;", "&")
                   .replaceAll("&lt;", "<")
                   .replaceAll("&gt;", ">")
                   .replaceAll("\\s+", " ")
                   .trim();
    }
    
    /**
     * Disconnect from Gmail
     */
    public void disconnect() {
        try {
            if (store != null && store.isConnected()) {
                store.close();
            }
        } catch (Exception e) {
            System.err.println("Error disconnecting: " + e.getMessage());
        }
    }
    
    /**
     * Test connection
     */
    public boolean testConnection() {
        boolean connected = connect();
        if (connected) {
            disconnect();
        }
        return connected;
    }
    
    /**
     * Get folder list (for debugging)
     */
    public List<String> getFolders() {
        List<String> folderNames = new ArrayList<>();
        
        try {
            if (store == null || !store.isConnected()) {
                connect();
            }
            
            Folder[] folders = store.getDefaultFolder().list();
            for (Folder folder : folders) {
                folderNames.add(folder.getName());
            }
        } catch (Exception e) {
            System.err.println("Error getting folders: " + e.getMessage());
        }
        
        return folderNames;
    }
}
