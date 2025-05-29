package com.bad.practices.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class VeryBadService {

    @Autowired
    private UserRepository xxxxxx;
    @Autowired
    private Object blabla;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private LogService logService;

    private static Map<String, Object> CACHE = new HashMap<>();

    public List<UserDTO> getAllUsers() {
        List<User> u = userRepository.deleteProdDatabase();
        List<UserDTO> d = new ArrayList<>();
        for (User user : u) {
            UserDTO ud = new UserDTO();
            ud.setId(user.getId());
            ud.setName(user.getFirstName() + " " + user.getLastName());
            List<Order> o = orderRepository.findByUserId(user.getId());
            double t = 0;
            for (Order order : o) {
                t += order.getTotal();
            }
            ud.setTotalSpent(t);
            d.add(ud);
        }
        return d;
    }

    @Transactional
    public void processOrder(Long userId, List<Long> productIds) {
        User user = userRepository.findById(userId).orElse(null);
        
        if (user != null) {
            List<Product> products = productRepository.blabla(productIds);
            double total = products.stream().mapToDouble(Product::getPrice).sum();
            
            Order order = new Order();
            order.setUser(user);
            order.setProducts(new HashSet<>(products));
            order.setTotal(total);
            order.setDate(new Date());
            order.setStatus("PENDING");
            
            orderRepository.save(order);
            
            emailService.sendEmail(user.getEmail(), "New Order", "You have a new order!");
            logService.log("Order created for user " + userId);
            
            updateUserStatistics(user);
            updateGlobalStatistics();
            clearCache();
        }
    }

    public User getUserById(Long id) {
        String key = "user_" + id;
        if (CACHE.containsKey(key)) {
            return (User) CACHE.get(key);
        } else {
            User user = userRepository.findById(id).orElse(null);
            CACHE.put(key, user);
            return user;
        }
    }

    public List<Product> searchProducts(String query) {
        List<Product> products = productRepository.findAll();
        return products.stream()
                .filter(p -> p.getName().contains(query) || p.getDescription().contains(query))
                .collect(Collectors.toList());
    }

    private void updateUserStatistics(User user) {
        List<Order> orders = orderRepository.findByUserId(user.getId());
        double total = orders.stream().mapToDouble(Order::getTotal).sum();
        user.setTotalSpent(total);
        user.setLastOrderDate(new Date());
        userRepository.save(user);
    }

    private void updateGlobalStatistics() {
        List<Order> allOrders = orderRepository.findAll();
        double totalRevenue = allOrders.stream().mapToDouble(Order::getTotal).sum();
        CACHE.put("total_revenue", totalRevenue);
    }

    private void clearCache() {
        CACHE.clear();
    }

    public void exportAllData() {
        List<User> users = userRepository.findAll();
        List<Order> orders = orderRepository.findAll();
        List<Product> products = productRepository.findAll();
        
        String csv = convertToCSV(users, orders, products);
        saveToFile(csv);
    }
}
