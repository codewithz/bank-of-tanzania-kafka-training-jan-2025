import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.http.ContentType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TransactionSender {

    public static void main(String[] args) {
        // API Endpoint URL
        String apiUrl = "http://localhost:9098/api/transactions";

        // Generate 500 mock transactions
        List<Transaction> transactions = generateMockTransactions(10);

        // Send transactions
        for (Transaction transaction : transactions) {
            try {
                // Send POST request using Rest-Assured
                Response response = RestAssured.given()
                        .contentType(ContentType.JSON) // Set Content-Type to JSON
                        .body(transaction) // Attach transaction object as JSON
                        .post(apiUrl); // Send POST request

                // Log response
                System.out.println("Transaction sent: " + transaction);
                System.out.println("Response: " + response.getStatusCode() + " - " + response.getBody().asString());
            } catch (Exception e) {
                System.err.println("Error sending transaction: " + e.getMessage());
            }
        }
    }

    // Generate mock transactions with random values
    private static List<Transaction> generateMockTransactions(int count) {
        List<Transaction> transactions = new ArrayList<>();
        Random random = new Random();

        String[] bankNames = {"Bank of Tanzania", "National Bank", "Tanzania Commercial Bank", "Equity Bank", "Access Bank"};
        String[] accountHolders = {"John Doe", "Jane Smith", "Michael Brown", "Emily Davis", "Chris Wilson"};
        String[] states = {"Dar es Salaam", "Dodoma", "Arusha", "Mwanza", "Mbeya"};

        for (int i = 1; i <= count; i++) {
            Transaction transaction = new Transaction();
            transaction.setTxId("TX" + i); // Sequential transaction IDs
            transaction.setBankName(bankNames[random.nextInt(bankNames.length)]); // Random bank name
            transaction.setAccountHolder(accountHolders[random.nextInt(accountHolders.length)]); // Random account holder
            transaction.setAmount(100 + random.nextInt(451)); // Random amount between 50 and 500
            transaction.setBalanceAmount(1000 + random.nextInt(9000)); // Random balance between 1000 and 10000
            transaction.setCountry("Tanzania");
            transaction.setState(states[random.nextInt(states.length)]); // Random state
            transaction.setTimestamp(System.currentTimeMillis());
            transaction.setTxTypeCode(1 + random.nextInt(3)); // Random transaction type code: 1, 2, or 3

            transactions.add(transaction);
        }

        return transactions;
    }

    // Transaction model class
    static class Transaction {
        private String txId;
        private String bankName;
        private String accountHolder;
        private double amount;
        private double balanceAmount;
        private String country;
        private String state;
        private long timestamp;
        private int txTypeCode;

        // Getters and Setters

        public String getTxId() {
            return txId;
        }

        public void setTxId(String txId) {
            this.txId = txId;
        }

        public String getBankName() {
            return bankName;
        }

        public void setBankName(String bankName) {
            this.bankName = bankName;
        }

        public String getAccountHolder() {
            return accountHolder;
        }

        public void setAccountHolder(String accountHolder) {
            this.accountHolder = accountHolder;
        }

        public double getAmount() {
            return amount;
        }

        public void setAmount(double amount) {
            this.amount = amount;
        }

        public double getBalanceAmount() {
            return balanceAmount;
        }

        public void setBalanceAmount(double balanceAmount) {
            this.balanceAmount = balanceAmount;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }

        public int getTxTypeCode() {
            return txTypeCode;
        }

        public void setTxTypeCode(int txTypeCode) {
            this.txTypeCode = txTypeCode;
        }

        @Override
        public String toString() {
            return "Transaction{" +
                    "txId='" + txId + '\'' +
                    ", bankName='" + bankName + '\'' +
                    ", accountHolder='" + accountHolder + '\'' +
                    ", amount=" + amount +
                    ", balanceAmount=" + balanceAmount +
                    ", country='" + country + '\'' +
                    ", state='" + state + '\'' +
                    ", timestamp=" + timestamp +
                    ", txTypeCode=" + txTypeCode +
                    '}';
        }
    }
}
