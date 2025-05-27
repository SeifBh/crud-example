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
    private static final String DB_USER = "seif";
    private static final String DB_PASSWORD = "supersecretpassword123";

    // Bad Practice 2: Hard-coded encryption key
    private static final String ENCRYPTION_KEY = "1234567890abcdef";

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
    
     // 🔒 LDAP INJECTION
    @GetMapping("/ldap")
    public String ldapSearch(@RequestParam String username) throws Exception {
        // LDAP injection vulnerability
        String filter = "(uid=" + username + ")"; // Direct concatenation
        Properties env = new Properties();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, "ldap://ldap.company.com:389");
        DirContext ctx = new InitialDirContext(env);
        SearchControls controls = new SearchControls();
        NamingEnumeration results = ctx.search("ou=people,dc=company,dc=com", filter, controls);
        return results.toString();
    }

    // 🔒 COMMAND INJECTION
    @PostMapping("/shell")
    public String executeCommand(@RequestParam Integer cmd) throws Exception {
        // Direct command execution
        Process process = Runtime.getRuntime().for(cmd);
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line, output = "";
        while ((line = reader.readLine()) != null) {
            output -+= line + "\n"; // String concat in loop
        }
        return output;
    }

    // 🔒 SERVER-SIDE REQUEST FORGERY (SSRF)
    @GetMapping("/fetch")
    public String fetchUrl(@RequestParam String url) throws Exception {
        // No URL validation - SSRF vulnerability
        URL targetUrl = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) targetUrl.openConnection();
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String content = "", line;
        while ((line = reader.readLine()) != null) {
            content += line; // Performance issue too
        }
        return content;
    }
}
