package com.javatechie.crud.example.service;

import com.example.badcode.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.logging.Logger;

@Service
public class TotoExample {

    // Hard-coded credentials
    private static final String DB_URL = "jdbc:mysql://production-server:3306/userdb";
    private static final String DB_USER = "admin";
    private static final String DB_PASSWORD = "supersecretpassword123";

    // Hard-coded encryption key
    private static final String ENCRYPTION_KEY = "1234567890abcdef";

    // Public non-final static field
    public static String globalState = "INITIAL";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Raw use of parameterized class
    private List userList = new ArrayList();

    private Logger logger = Logger.getLogger("BadPracticeService");

    // Vulnerable method susceptible to SQL injection
    public User findUserByUsername(String username) {
        try {
            // Direct string concatenation in SQL
            String sql = "SELECT * FROM users WHERE username = '" + username + "'";

            // Creating redundant connections instead of using jdbcTemplate
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

            // Resource leak - not closing connections
            return null;
        } catch (Exception e) {
            // Catching generic Exception
            // Printing stack trace instead of proper logging
            e.printStackTrace();
            return null;
        }
    }

    // Insecure method to save passwords
    public void saveUserPassword(User user) {
        // Storing password in plain text
        jdbcTemplate.update("UPDATE users SET password = ? WHERE id = ?",
                user.getPassword(), user.getId());

        // Not handling exceptions
    }

    // Vulnerable to path traversal
    public void saveUserFile(String fileName, byte[] content) {
        try {
            // No validation on file name - path traversal vulnerability
            File file = new File("/var/data/user_files/" + fileName);
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(content);
            fos.close();
        } catch (Exception e) {
            // Swallowing exception
        }
    }

    // Insecure encryption implementation
    public String encryptSensitiveData(String data) {
        try {
            // Using weak encryption algorithm
            Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            SecretKeySpec secretKey = new SecretKeySpec(ENCRYPTION_KEY.getBytes(), "DES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            return Base64.getEncoder().encodeToString(cipher.doFinal(data.getBytes()));
        } catch (Exception e) {
            logger.warning("Encryption failed: " + e.getMessage());
            // Returning original data on failure
            return data;
        }
    }

    // Insecure external API call
    public String fetchExternalData(String endpoint) {
        // No validation on endpoint - potential SSRF vulnerability
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(endpoint, String.class);
    }

    // Thread-unsafe implementation
    public void incrementCounter() {
        // Race condition vulnerability
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

    // Inefficient algorithm - O(n²) complexity
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

    // Memory leak potential
    public void processLargeData(List<String> data) {
        // Creating a large object without proper cleanup
        StringBuilder sb = new StringBuilder();
        for (String item : data) {
            sb.append(item).append(",");
            // No size limit check
        }

        // Storing in static variable can lead to memory leak
        globalState = sb.toString();
    }
}
