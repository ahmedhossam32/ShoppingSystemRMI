package Database;

import Mainclasses.Admin;
import Mainclasses.DeliveryStaff;
import Mainclasses.Order;
import Mainclasses.Product;
import Mainclasses.User;
import Mainclasses.CartItem;
import Mainclasses.Customer;
import Mainclasses.Payment;
import com.google.gson.Gson;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

import java.util.List;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bson.Document;

public class DB {

    private MongoClient client;
    private MongoDatabase database;

    private MongoCollection<Document> adminCollection;
    private MongoCollection<Document> productCollection;
    private MongoCollection<Document> orderCollection;
    private MongoCollection<Document> userCollection;
    private MongoCollection<Document> paymentCollection;

    private Gson gson = new Gson();


    public DB() {

        Logger mongoLogger = Logger.getLogger("org.mongodb.driver");
        mongoLogger.setLevel(Level.SEVERE);

        client = new MongoClient("localhost", 27017);
        database = client.getDatabase("ShopSystem");

        adminCollection   = database.getCollection("Admin");
        productCollection = database.getCollection("Product");
        orderCollection   = database.getCollection("Order");
        userCollection    = database.getCollection("User");   // NEW!
        paymentCollection = database.getCollection("Payment");
    }

public void insertAdmin(Admin admin) {
    Document doc = Document.parse(gson.toJson(admin));
    userCollection.insertOne(doc);
    System.out.println("Admin inserted into users collection.");
}

public void insertProduct(Product product) {
        Document existing = productCollection.find(Filters.eq("productId", product.getProductId())).first();

        if (existing == null) {
            Document doc = Document.parse(gson.toJson(product));
            productCollection.insertOne(doc);
            System.out.println("Product added successfully.");
        } else {
            System.out.println("Product already exists.");
        }
    }

    public void deleteProduct(String productId) {
        productCollection.deleteOne(Filters.eq("productId", productId));
        System.out.println("Product deleted: " + productId);
    }

    public void updateProduct(Product product) {
        Document doc = Document.parse(gson.toJson(product));
        productCollection.replaceOne(Filters.eq("productId", product.getProductId()), doc);
        System.out.println("Product updated: " + product.getProductId());
    }
   

    public Product getProduct(String productId) {
        Document doc = productCollection.find(Filters.eq("productId", productId)).first();
        return (doc != null) ? gson.fromJson(doc.toJson(), Product.class) : null;
    }

    public ArrayList<Product> getAllProducts() {
        ArrayList<Product> list = new ArrayList<>();
        for (Document doc : productCollection.find()) {
            list.add(gson.fromJson(doc.toJson(), Product.class));
        }
        return list;
    }


    public String getName(String productId) {
        Product p = getProduct(productId);
        return (p != null) ? p.getName() : null;
    }

    public Double getPrice(String productId) {
        Product p = getProduct(productId);
        return (p != null) ? p.getPrice() : null;
    }

    public String getId(String productId) {
        Product p = getProduct(productId);
        return (p != null) ? p.getProductId() : null;
    }

   
    public void insertUser(User user) {
        Document doc = Document.parse(gson.toJson(user));
        userCollection.insertOne(doc);
        System.out.println("User inserted: " + user.getId());
    }
public User findUserByEmail(String email) {
    Document doc = userCollection.find(Filters.eq("email", email)).first();
    if (doc == null) return null;

    String lower = email.toLowerCase();
    if (lower.contains("delivery")) {
        return gson.fromJson(doc.toJson(), DeliveryStaff.class);
    } else if (lower.contains("admin")) {
        return gson.fromJson(doc.toJson(), Admin.class);
    } else {
        return gson.fromJson(doc.toJson(), Customer.class);
    }
}

public User findUserById(String userId) {
    Document doc = userCollection.find(Filters.eq("id", userId)).first();
    if (doc == null) return null;

    String email = doc.getString("email");
    String lower = (email == null) ? "" : email.toLowerCase();

    if (lower.contains("delivery")) {
        return gson.fromJson(doc.toJson(), DeliveryStaff.class);
    } else if (lower.contains("admin")) {
        return gson.fromJson(doc.toJson(), Admin.class);
    } else {
        return gson.fromJson(doc.toJson(), Customer.class);
    }
}



    public void updateUser(User user) {
        Document doc = Document.parse(gson.toJson(user));
        userCollection.replaceOne(Filters.eq("id", user.getId()), doc);
        System.out.println("User updated: " + user.getId());
    }

    public ArrayList<User> getAllUsers() {
    ArrayList<User> list = new ArrayList<>();
    for (Document doc : userCollection.find()) {
        String email = doc.getString("email");
        String lower = (email == null) ? "" : email.toLowerCase();

        if (lower.contains("delivery")) {
            list.add(gson.fromJson(doc.toJson(), DeliveryStaff.class));
        } else if (lower.contains("admin")) {
            list.add(gson.fromJson(doc.toJson(), Admin.class));
        } else {
            list.add(gson.fromJson(doc.toJson(), Customer.class));
        }
    }
    return list;
}

   
public void insertOrder(Order order) {

    Document doc = new Document();
    doc.append("id", order.getId());
    doc.append("customerId", order.getCustomerId());
order.recalculateTotal();
doc.append("totalAmount", order.getTotalAmount());
    doc.append("statusName", order.getStatusName());

    // items
    ArrayList<Document> itemDocs = new ArrayList<>();
    if (order.getItems() != null) {
        for (CartItem ci : order.getItems()) {
            Document it = new Document();
            it.append("productId", ci.getProductId());
            it.append("productName", ci.getProductName());
            it.append("productPrice", ci.getProductPrice());
            it.append("quantity", ci.getQuantity());
            itemDocs.add(it);
        }
    }
    doc.append("items", itemDocs);

    orderCollection.insertOne(doc);
    System.out.println("Order inserted: " + order.getId());
}

public Order getOrder(String orderId) {

    Document doc = orderCollection.find(Filters.eq("id", orderId)).first();
    if (doc == null) return null;

    Order o = new Order();
    o.setId(doc.getString("id"));
    o.setCustomerId(doc.getString("customerId"));
    o.setStatusName(doc.getString("statusName"));

    Double total = doc.getDouble("totalAmount");
    o.setItems(new ArrayList<>()); // init first
    if (total != null) {
        // total calculated from items anyway, but keep it if needed
    }

    List<Document> itemDocs = (List<Document>) doc.get("items");
    ArrayList<CartItem> items = new ArrayList<>();
    if (itemDocs != null) {
        for (Document it : itemDocs) {
            CartItem ci = new CartItem(
                    it.getString("productId"),
                    it.getString("productName"),
                    it.getDouble("productPrice"),
                    it.getInteger("quantity")
            );
            items.add(ci);
        }
    }
    o.setItems(items);

    return o;
}

public ArrayList<Order> getAllOrders() {

    ArrayList<Order> list = new ArrayList<>();
    for (Document doc : orderCollection.find()) {
        String id = doc.getString("id");
        Order o = getOrder(id);
        if (o != null) list.add(o);
    }
    return list;
}

public void updateOrder(Order order) {

    Document doc = new Document();
    doc.append("id", order.getId());
    doc.append("customerId", order.getCustomerId());
    doc.append("totalAmount", order.getTotalAmount());
    doc.append("statusName", order.getStatusName());

    ArrayList<Document> itemDocs = new ArrayList<>();
    if (order.getItems() != null) {
        for (CartItem ci : order.getItems()) {
            Document it = new Document();
            it.append("productId", ci.getProductId());
            it.append("productName", ci.getProductName());
            it.append("productPrice", ci.getProductPrice());
            it.append("quantity", ci.getQuantity());
            itemDocs.add(it);
        }
    }
    doc.append("items", itemDocs);

    orderCollection.replaceOne(Filters.eq("id", order.getId()), doc);
    System.out.println("Order updated: " + order.getId());
}


    public void deleteOrder(String orderId) {
        orderCollection.deleteOne(Filters.eq("id", orderId));
        System.out.println("Order deleted: " + orderId);
    }
    
    public void insertDeliveryStaff(DeliveryStaff staff) {
    Document doc = Document.parse(gson.toJson(staff));
    database.getCollection("DeliveryStaff").insertOne(doc);
    System.out.println("Delivery staff inserted: " + staff.getName());
    }
    public DeliveryStaff getDeliveryStaff(String id) {
    Document doc = database.getCollection("DeliveryStaff")
            .find(Filters.eq("id", id)).first();

    return (doc != null) ? gson.fromJson(doc.toJson(), DeliveryStaff.class) : null;
}
    public boolean hasActiveDeliveries(String staffId) {
    Document doc = orderCollection.find(
            Filters.and(
                    Filters.eq("deliveryStaffId", staffId),
                    Filters.ne("status", "Delivered")
            )
    ).first();

    return doc != null;
    }
    public void assignOrderToStaff(String orderId, String staffId) {
    orderCollection.updateOne(
            Filters.eq("id", orderId),
            Updates.set("deliveryStaffId", staffId)
    );
    System.out.println("Order " + orderId + " assigned to staff " + staffId);
}
    public ArrayList<DeliveryStaff> getAllDeliveryStaff() {
    ArrayList<DeliveryStaff> list = new ArrayList<>();
    MongoCollection<Document> deliveryStaffCollection = database.getCollection("DeliveryStaff");

    for (Document doc : deliveryStaffCollection.find()) {
        list.add(gson.fromJson(doc.toJson(), DeliveryStaff.class));
    }

    return list;
}
    
    // ======================================================
    //  PAYMENT METHODS
    // ======================================================

    public void insertPayment(Payment payment) {
        Document doc = Document.parse(gson.toJson(payment));
        paymentCollection.insertOne(doc);
        System.out.println("Payment inserted: " + payment.getId());
    }

    public void updatePayment(Payment payment) {
        Document doc = Document.parse(gson.toJson(payment));
        paymentCollection.replaceOne(Filters.eq("id", payment.getId()), doc);
        System.out.println("Payment updated: " + payment.getId());
    }

    public void deletePayment(String paymentId) {
        paymentCollection.deleteOne(Filters.eq("id", paymentId));
        System.out.println("Payment deleted: " + paymentId);
    }

    public Payment getPayment(String paymentId) {
        Document doc = paymentCollection.find(Filters.eq("id", paymentId)).first();
        return (doc != null) ? gson.fromJson(doc.toJson(), Payment.class) : null;
    }

    public ArrayList<Payment> getAllPayments() {
        ArrayList<Payment> list = new ArrayList<>();
        for (Document doc : paymentCollection.find()) {
            list.add(gson.fromJson(doc.toJson(), Payment.class));
        }
        return list;
    }
    
    
    public void close() {
        client.close();
    }
}
