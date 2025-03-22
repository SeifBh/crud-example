package com.javatechie.crud.example.service;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.logging.Logger;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class BadPracticesExample {

    // Bad Practice 1: Hard-coded credentials
    private static final String DB_URL = "jdbc:mysql://production-server:3306/userdb";
    private static final String DB_USER = "admin";
    private static final String DB_PASSWORD = "supersecretpassword123";

    // Bad Practice 2: Hard-coded encryption key
    private static final String ENCRYPTION_KEY = "1234567890abcdef";

    // Bad Practice 3: Public non-final static field
    public static String globalState = "INITIAL";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Bad Practice 4: Raw use of parameterized class
    private List userList = new ArrayList();

    private Logger logger = Logger.getLogger("BadPracticeService");

    // Bad Practice 5: Vulnerable to SQL injection
    public User findUserByUsername(String username) {
        try {
            // Bad Practice 6: Direct string concatenation in SQL
            String sql = "SELECT * FROM users WHERE username = '" + username + "'";

            // Bad Practice 7: Creating redundant connections instead of using jdbcTemplate
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            Statement stmt = conn.createStatement();
            var resultSet = stmt.executeQuery(sql);

            if (resultSet.next()) {
                User user = new User();
                user.setId(resultSet.getLong("id"));
                user.setUsername(resultSet.getString("username"));
                user.setPassword(resultSet.getString("password"));
                return user;
            }

            // Bad Practice 8: Resource leak - not closing connections
            return null;
        } catch (Exception e) {
            // Bad Practice 9: Catching generic Exception
            // Bad Practice 10: Printing stack trace instead of proper logging
            e.printStackTrace();
            return null;
        }
    }

    // Bad Practice 11: Insecure method to save passwords
    public void saveUserPassword(User user) {
        // Bad Practice 12: Storing password in plain text
        jdbcTemplate.update("UPDATE users SET password = ? WHERE id = ?",
                user.getPassword(), user.getId());

        // Bad Practice 13: Not handling exceptions
    }

    // Bad Practice 14: Vulnerable to path traversal
    public void saveUserFile(String fileName, byte[] content) {
        try {
            // Bad Practice 15: No validation on file name - path traversal vulnerability
            File file = new File("/var/data/user_files/" + fileName);
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(content);
            fos.close();
        } catch (Exception e) {
            // Bad Practice 16: Swallowing exception
        }
    }

    // Bad Practice 17: Insecure encryption implementation
    public String encryptSensitiveData(String data) {
        try {
            // Bad Practice 18: Using weak encryption algorithm
            Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            SecretKeySpec secretKey = new SecretKeySpec(ENCRYPTION_KEY.getBytes(), "DES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            return Base64.getEncoder().encodeToString(cipher.doFinal(data.getBytes()));
        } catch (Exception e) {
            logger.warning("Encryption failed: " + e.getMessage());
            // Bad Practice 19: Returning original data on failure
            return data;
        }
    }

    // Bad Practice 20: Insecure external API call
    public String fetchExternalData(String endpoint) {
        // Bad Practice 21: No validation on endpoint - potential SSRF vulnerability
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(endpoint, String.class);
    }

    // Bad Practice 22: Thread-unsafe implementation
    public void incrementCounter() {
        // Bad Practice 23: Race condition vulnerability
        int count = getCount();
        setCount(count + 1);
    }

    private int count = 0;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    // Bad Practice 24: Inefficient algorithm - O(n²) complexity
    public List<Integer> findDuplicates(List<Integer> numbers) {
        List<Integer> duplicates = new ArrayList<>();

        for (int i = 0; i < numbers.size(); i++) {
            for (int j = i + 1; j < numbers.size(); j++) {
                if (numbers.get(i).equals(numbers.get(j)) && !duplicates.contains(numbers.get(i))) {
                    duplicates.add(numbers.get(i));
                }
            }
        }

        return duplicates;
    }

    // Bad Practice 25: Memory leak potential
    public void processLargeData(List<String> data) {
        // Bad Practice 26: Creating a large object without proper cleanup
        StringBuilder sb = new StringBuilder();
        for (String item : data) {
            sb.append(item).append(",");
            // Bad Practice 27: No size limit check
        }

        // Bad Practice 28: Storing in static variable can lead to memory leak
        globalState = sb.toString();
    }
}

class User {
    private long id;
    private String username;
    private String password;

    // Bad Practice 29: Exposing setters for sensitive fields
    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
