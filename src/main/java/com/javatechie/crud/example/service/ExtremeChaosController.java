package com.hell.enterprise;

import lombok.*;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Cipher;
import javax.persistence.*;
import java.io.*;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.sql.*;
import java.util.*;

// 🔥 EXTREME NIGHTMARE - Advanced bad practices
@Data
@Getter
@Setter
@ToString
@EqualsAndHashCode // ALL redundant with @Data
@Component
@Service
@Repository // Multiple conflicting stereotypes
@RestController
@Controller // Conflicting annotations
@Entity
@Table(name = "chaos") // Entity annotation on controller
@RequestMapping("/hell")
public class ExtremeChaosController extends Thread implements Runnable, Serializable, Cloneable {

    // 🔒 EXTREME SECURITY DISASTERS
    public static final String MASTER_PASSWORD = "admin123!@#";
    public static final String JWT_SECRET = "myJWT$ecret2024";
    public static final String API_KEY = "sk-1234567890abcdef";
    public static final String DATABASE_URL = "jdbc:postgresql://prod-db-01.company.com:5432/users";
    private static final String ADMIN_TOKEN = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9";
    public static String ENCRYPTION_KEY = "AES256Key!@#$%^&*()";
    private static int x, y, z, a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u, v, w;
    private static PreparedStatement globalPreparedStatement;
    // 🧹 NAMING DISASTERS + Security Issues
    public String pwd, usr, tkn, sql, cmd, xml, json, api, db, fs, net, sec;
    public volatile boolean GLOBAL_FLAG = true;
    private List raw1, raw2, raw3; // Multiple raw types
    private Map m1, m2, m3; // More raw types
    private Set s1, s2; // Even more raw types
    private HashMap hm; // Raw HashMap
    private ArrayList al; // Raw ArrayList
    // 🏗️ ARCHITECTURAL NIGHTMARES
    private Connection globalConnection; // Shared mutable state
    private FileWriter logWriter;
    private Socket clientSocket;
    private volatile String sharedState = "";

    // 🔒 REFLECTION + SERIALIZATION HELL
    @PostMapping("/reflect")
    public String executeReflection(@RequestParam String className, @RequestParam String methodName) throws Exception {
        // Unrestricted reflection - RCE vulnerability
        Class<?> clazz = Class.forName(className);
        Method method = clazz.getDeclaredMethod(methodName);
        method.setAccessible(true);
        Object result = method.invoke(clazz.newInstance());
        return result.toString();
    }

    @PostMapping("/deserialize")
    public Object deserializeObject(@RequestBody byte[] data) throws Exception {
        // Unsafe deserialization
        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        ObjectInputStream ois = new ObjectInputStream(bis);
        return ois.readObject(); // Massive security hole
    }

    // 🔒 CRYPTO DISASTERS
    @PostMapping("/encrypt")
    public String encryptData(@RequestParam String data) throws Exception {
        // Weak crypto implementation
        Cipher cipher = Cipher.getInstance("DES"); // Weak algorithm
        SecretKeySpec key = new SecretKeySpec("weakkey1".getBytes(), "DES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encrypted = cipher.doFinal(data.getBytes());
        // Hardcoded IV, no salt
        return Base64.getEncoder().encodeToString(encrypted);
    }

    // 🔒 XML/XXE VULNERABILITIES
    @PostMapping("/xml")
    public String processXML(@RequestBody String xmlData) throws Exception {
        // XXE vulnerability
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        // No XXE protection configured
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new ByteArrayInputStream(xmlData.getBytes()));
        return doc.getTextContent();
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
    public String executeCommand(@RequestParam String cmd) throws Exception {
        // Direct command execution
        Process process = Runtime.getRuntime().exec(cmd);
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line, output = "";
        while ((line = reader.readLine()) != null) {
            output += line + "\n"; // String concat in loop
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

    // 🏗️ EXTREME SRP VIOLATIONS
    @PostMapping("/chaos")
    public synchronized String processEverything(@RequestParam String data) throws Exception {
        // This method does EVERYTHING

        // 1. Input validation with weak regex
        if (!data.matches(".*")) { // Useless regex
            throw new RuntimeException("Invalid input");
        }

        // 2. Database operations with SQL injection
        Connection conn = DriverManager.getConnection(DATABASE_URL, "admin", MASTER_PASSWORD);
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM users WHERE data = '" + data + "'");

        // 3. File operations with path traversal
        FileWriter writer = new FileWriter("/app/logs/" + data + ".txt");
        writer.write("Processing: " + data + " with key: " + ENCRYPTION_KEY);

        // 4. Network operations
        Socket socket = new Socket("external-api.com", 80);
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        out.println("GET /api/process?data=" + data + "&key=" + API_KEY);

        // 5. Reflection madness
        Class<?> clazz = Class.forName("java.lang.Runtime");
        Method method = clazz.getMethod("exec", String.class);
        method.invoke(Runtime.getRuntime(), "echo " + data);

        // 6. Crypto operations with hardcoded keys
        Cipher cipher = Cipher.getInstance("AES");
        SecretKeySpec keySpec = new SecretKeySpec(ENCRYPTION_KEY.getBytes(), "AES");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);

        // 7. Thread manipulation
        Thread thread = new Thread(() -> {
            while (GLOBAL_FLAG) {
                System.out.println("Background process: " + data);
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                }
            }
        });
        thread.start();

        // 8. Memory leaks
        List<String> memoryHog = new ArrayList<>();
        for (int x = 0; x < 10000000; x++) {
            memoryHog.add("Data item " + x + " processed with " + data);
        }

        // 9. Resource leaks (nothing closed properly)
        // conn, writer, socket all leaked

        return "Processed: " + data + " Token: " + ADMIN_TOKEN;
    }

    // 🏗️ OPEN/CLOSED PRINCIPLE DISASTERS
    public void processPaymentMethod(Object payment) {
        if (payment.getClass().getName().equals("CreditCard")) {
            System.out.println("Processing credit card");
        } else if (payment instanceof String && payment.toString().contains("paypal")) {
            System.out.println("Processing PayPal");
        } else if (payment.hashCode() == "bitcoin".hashCode()) {
            System.out.println("Processing Bitcoin");
        } else if (payment.toString().toLowerCase().startsWith("bank")) {
            System.out.println("Processing bank transfer");
        }
        // Adding new payment type = modify this mess
    }

    // 🏗️ DEPENDENCY INVERSION DISASTERS
    @GetMapping("/notify")
    public String sendNotifications() throws Exception {
        // All hardcoded dependencies
        EmailService emailService = new EmailService("smtp.hardcoded.com", 587, "admin", MASTER_PASSWORD);
        SmsService smsService = new SmsService("api.sms.com", API_KEY);
        SlackService slackService = new SlackService("hooks.slack.com/webhook123");
        DatabaseLogger dbLogger = new DatabaseLogger(DATABASE_URL, "root", MASTER_PASSWORD);
        FileLogger fileLogger = new FileLogger("/var/log/app.log");

        // Tight coupling nightmare
        emailService.connect();
        smsService.initialize();
        slackService.authenticate(API_KEY);
        dbLogger.openConnection();
        fileLogger.openFile();

        return "Notifications sent";
    }

    // ⚡ PERFORMANCE DISASTERS
    @GetMapping("/slow")
    public String performanceKiller() {
        String result = "";
        List<Map<String, List<Set<Object>>>> nightmare = new ArrayList<>();

        // Nested loops nightmare
        for (int i = 0; i < 1000; i++) {
            for (int j = 0; j < 1000; j++) {
                for (int k = 0; k < 100; k++) {
                    result += "Item " + i + "-" + j + "-" + k + " "; // O(n³) string concat
                    Map<String, List<Set<Object>>> complexMap = new HashMap<>();
                    List<Set<Object>> complexList = new ArrayList<>();
                    Set<Object> complexSet = new HashSet<>();
                    complexSet.add("Item " + i + j + k);
                    complexList.add(complexSet);
                    complexMap.put("key" + i + j + k, complexList);
                    nightmare.add(complexMap);
                }
            }
        }
        return result;
    }

    // 🧹 EXTREME NAMING VIOLATIONS
    private boolean a() {
        return true;
    }

    private void b(String s) {
        System.out.println(s);
    }

    private int c(int x, int y) {
        return x + y;
    }

    private String d(Object o) {
        return o.toString();
    }

    // Method names that lie about functionality
    private void saveUser() {
        // Actually deletes users
        executeQuery("DELETE FROM users");
    }

    private boolean isValidUser() {
        // Actually creates admin user
        executeQuery("INSERT INTO users VALUES ('admin', 'secret')");
        return false;
    }

    // 🔧 THREADING DISASTERS
    @GetMapping("/race")
    public String raceCondition() {
        // Shared mutable state without synchronization
        List<String> sharedList = new ArrayList<>(); // Not thread-safe

        for (int i = 0; i < 100; i++) {
            new Thread(() -> {
                sharedList.add("Item " + System.currentTimeMillis());
                sharedState += "X"; // Race condition on volatile field
            }).start();
        }

        return "Started " + sharedList.size() + " threads";
    }

    // 🔒 EXCEPTION INFORMATION DISCLOSURE
    @PostMapping("/error")
    public String handleError(@RequestParam String input) {
        try {
            Connection conn = DriverManager.getConnection(DATABASE_URL + "?user=" + input, MASTER_PASSWORD, "");
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM secret_table WHERE key = ?");
            ps.setString(1, ENCRYPTION_KEY);
            ps.executeQuery();
        } catch (Exception e) {
            // Full stack trace with sensitive info exposed
            return "Error: " + e.getMessage() +
                    "\nStack trace: " + Arrays.toString(e.getStackTrace()) +
                    "\nDatabase URL: " + DATABASE_URL +
                    "\nEncryption key: " + ENCRYPTION_KEY;
        }
        return "Success";
    }

    // 🔧 RESOURCE MANAGEMENT DISASTERS
    private void createConnectionsWithoutClosing() {
        for (int i = 0; i < 1000; i++) {
            try {
                Connection conn = DriverManager.getConnection(DATABASE_URL);
                FileInputStream fis = new FileInputStream("/tmp/file" + i);
                Socket socket = new Socket("localhost", 8080);
                // None of these are ever closed - massive resource leak
            } catch (Exception e) {
                // Silent failure
            }
        }
    }

    // Helper method with its own violations
    private void executeQuery(String sql) {
        try {
            if (globalConnection == null) {
                globalConnection = DriverManager.getConnection(DATABASE_URL, "admin", MASTER_PASSWORD);
            }
            Statement stmt = globalConnection.createStatement();
            stmt.execute(sql); // SQL injection ready
        } catch (Exception e) {
            // Log with secrets
            System.out.println("Query failed: " + sql + " with password: " + MASTER_PASSWORD);
        }
    }
}

// 🏗️ BONUS: Supporting disaster classes
class EmailService {
    public String server, username, password;
    public int port;

    public EmailService(String server, int port, String username, String password) {
        this.server = server;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    public void connect() throws Exception {
        // Hardcoded implementation
        Socket socket = new Socket(server, port);
        PrintWriter out = new PrintWriter(socket.getOutputStream());
        out.println("AUTH " + username + " " + password);
    }
}

class SmsService {
    public String apiUrl, apiKey;

    public SmsService(String apiUrl, String apiKey) {
        this.apiUrl = apiUrl;
        this.apiKey = apiKey;
    }

    public void initialize() {
        // More hardcoded nightmare
        System.setProperty("sms.api.key", apiKey);
    }
}

// Classes with multiple violations each...
class SlackService {
    public String webhookUrl;

    public SlackService(String webhookUrl) {
        this.webhookUrl = webhookUrl;
    }

    public void authenticate(String key) { /* hardcoded */ }
}

class DatabaseLogger {
    public String url, user, pass;

    public DatabaseLogger(String url, String user, String pass) {
        this.url = url;
        this.user = user;
        this.pass = pass;
    }

    public void openConnection() { /* resource leak */ }
}

class FileLogger {
    public String path;

    public FileLogger(String path) {
        this.path = path;
    }

    public void openFile() { /* more resource leaks */ }
}
