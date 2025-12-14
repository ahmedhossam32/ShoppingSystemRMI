import Database.DB;
import Mainclasses.Product;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.junit.BeforeClass;
import org.junit.AfterClass;
import org.junit.Ignore;
import java.rmi.RemoteException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import rmi_implementations.AdminService;

public class UpdateProductTest {
    
    private AdminService adminService;
    private TestDB testDB;
    
    private static class TestDB extends DB {
        private List<Product> updatedProducts = new ArrayList<Product>();
        private boolean updateCalled = false;
        
        @Override
        public void updateProduct(Product product) {
            updateCalled = true;
            updatedProducts.add(product);
        }
        
        public boolean wasUpdateCalled() {
            return updateCalled;
        }
        
        public List<Product> getUpdatedProducts() {
            return updatedProducts;
        }
        
        public int getUpdateCount() {
            return updatedProducts.size();
        }
        
        public Product getLastUpdatedProduct() {
            if (updatedProducts.isEmpty()) {
                return null;
            }
            return updatedProducts.get(updatedProducts.size() - 1);
        }
        
        public void reset() {
            updatedProducts.clear();
            updateCalled = false;
        }
    }
    
    @BeforeClass
    public static void setUpClass() {
        System.out.println("Starting UpdateProductTest suite");
    }
    
    @AfterClass
    public static void tearDownClass() {
        System.out.println("Finished UpdateProductTest suite");
    }
    
    @Before
    public void setUp() throws Exception {
        adminService = new AdminService();
        testDB = new TestDB();
        Field dbField = AdminService.class.getDeclaredField("db");
        dbField.setAccessible(true);
        dbField.set(adminService, testDB);
    }
    
    @After
    public void tearDown() {
        adminService = null;
        testDB = null;
    }
    
    @Test
    public void testUpdateProduct_ValidProduct_Success() throws RemoteException {
        Product product = new Product("P001", "Updated Laptop", 18000.0, "New Gaming Laptop", 15);
        adminService.Update_Product(product);
        assertTrue(testDB.wasUpdateCalled());
        assertEquals(1, testDB.getUpdateCount());
        Product updated = testDB.getLastUpdatedProduct();
        assertNotNull(updated);
        assertEquals("P001", updated.getProductId());
        assertEquals("Updated Laptop", updated.getName());
        assertEquals(18000.0, updated.getPrice(), 0.001);
    }
    
    @Test
    public void testUpdateProduct_MultipleProducts_Success() throws RemoteException {
        Product product1 = new Product("P001", "Laptop V2", 20000.0, "Updated Gaming Laptop", 8);
        Product product2 = new Product("P002", "Mouse V2", 300.0, "New Wireless Mouse", 60);
        Product product3 = new Product("P003", "Keyboard V2", 1200.0, "RGB Keyboard", 25);
        adminService.Update_Product(product1);
        adminService.Update_Product(product2);
        adminService.Update_Product(product3);
        assertEquals(3, testDB.getUpdateCount());
        List<Product> updated = testDB.getUpdatedProducts();
        assertEquals("P001", updated.get(0).getProductId());
        assertEquals("P002", updated.get(1).getProductId());
        assertEquals("P003", updated.get(2).getProductId());
    }
    
    @Test
    public void testUpdateProduct_OnlyName_Success() throws RemoteException {
        Product product = new Product("P001", "New Product Name", 1000.0, "Old Description", 10);
        adminService.Update_Product(product);
        assertEquals("New Product Name", testDB.getLastUpdatedProduct().getName());
    }
    
    @Test
    public void testUpdateProduct_OnlyPrice_Success() throws RemoteException {
        Product product = new Product("P001", "Laptop", 25000.0, "Gaming Laptop", 10);
        adminService.Update_Product(product);
        assertEquals(25000.0, testDB.getLastUpdatedProduct().getPrice(), 0.001);
    }
    
    @Test
    public void testUpdateProduct_OnlyQuantity_Success() throws RemoteException {
        Product product = new Product("P001", "Laptop", 15000.0, "Gaming Laptop", 50);
        adminService.Update_Product(product);
        assertEquals(50, testDB.getLastUpdatedProduct().getQuantity());
    }
    
    @Test
    public void testUpdateProduct_OnlyDescription_Success() throws RemoteException {
        Product product = new Product(
            "P001",
            "Laptop",
            15000.0,
            "Completely new description with more details",
            10
        );
        adminService.Update_Product(product);
        assertEquals(
            "Completely new description with more details",
            testDB.getLastUpdatedProduct().getDescription()
        );
    }
    
    @Test
    public void testUpdateProduct_AllFields_Success() throws RemoteException {
        Product product = new Product(
            "P001",
            "Completely New Name",
            99999.99,
            "Completely New Description",
            999
        );
        adminService.Update_Product(product);
        Product updated = testDB.getLastUpdatedProduct();
        assertEquals("P001", updated.getProductId());
        assertEquals("Completely New Name", updated.getName());
        assertEquals("Completely New Description", updated.getDescription());
        assertEquals(99999.99, updated.getPrice(), 0.001);
        assertEquals(999, updated.getQuantity());
    }
    
    @Test
    public void testUpdateProduct_PriceToZero_Success() throws RemoteException {
        Product product = new Product("P001", "Free Product", 0.0, "Now Free!", 10);
        adminService.Update_Product(product);
        assertEquals(0.0, testDB.getLastUpdatedProduct().getPrice(), 0.001);
    }
    
    @Test
    public void testUpdateProduct_QuantityToZero_Success() throws RemoteException {
        Product product = new Product("P001", "Out of Stock", 1000.0, "No longer available", 0);
        adminService.Update_Product(product);
        assertEquals(0, testDB.getLastUpdatedProduct().getQuantity());
    }
    
    @Test
    public void testUpdateProduct_VeryHighPrice_Success() throws RemoteException {
        Product product = new Product("P001", "Luxury Item", 999999.99, "Premium product", 1);
        adminService.Update_Product(product);
        assertTrue(testDB.getLastUpdatedProduct().getPrice() > 900000);
    }
    
    @Test
    public void testUpdateProduct_VeryLargeQuantity_Success() throws RemoteException {
        Product product = new Product("P001", "Bulk Item", 10.0, "Wholesale", 100000);
        adminService.Update_Product(product);
        assertEquals(100000, testDB.getLastUpdatedProduct().getQuantity());
    }
    
    @Test
    public void testUpdateProduct_NullValues_CallsUpdate() throws RemoteException {
        Product product = new Product(null, null, 0.0, null, 0);
        adminService.Update_Product(product);
        assertTrue(testDB.wasUpdateCalled());
        assertNull(testDB.getLastUpdatedProduct().getProductId());
    }
    
    @Test
    public void testUpdateProduct_EmptyStrings_Success() throws RemoteException {
        Product product = new Product("", "", 100.0, "", 10);
        adminService.Update_Product(product);
        assertEquals("", testDB.getLastUpdatedProduct().getProductId());
        assertEquals("", testDB.getLastUpdatedProduct().getName());
    }
    
    @Test
    public void testUpdateProduct_SpecialCharacters_Success() throws RemoteException {
        Product product = new Product("P@001", "Product!@#$", 100.0, "Description%^&*()", 10);
        adminService.Update_Product(product);
        assertTrue(testDB.getLastUpdatedProduct().getName().contains("!@#$"));
    }
    
    @Test
    public void testUpdateProduct_LongDescription_Success() throws RemoteException {
        StringBuilder longDesc = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            longDesc.append("This is a very detailed description. ");
        }
        Product product = new Product("P001", "Product", 100.0, longDesc.toString(), 10);
        adminService.Update_Product(product);
        assertTrue(testDB.getLastUpdatedProduct().getDescription().length() > 1000);
    }
    
    @Test
    public void testUpdateProduct_DBCalledOnce_Success() throws RemoteException {
        Product product = new Product("P001", "Test", 100.0, "Test", 10);
        adminService.Update_Product(product);
        assertEquals(1, testDB.getUpdateCount());
    }
    
    @Test
    public void testUpdateProduct_SameProductMultipleTimes_Success() throws RemoteException {
        Product product1 = new Product("P001", "Version 1", 100.0, "First update", 10);
        Product product2 = new Product("P001", "Version 2", 200.0, "Second update", 20);
        Product product3 = new Product("P001", "Version 3", 300.0, "Third update", 30);
        adminService.Update_Product(product1);
        adminService.Update_Product(product2);
        adminService.Update_Product(product3);
        assertEquals(3, testDB.getUpdateCount());
        List<Product> updated = testDB.getUpdatedProducts();
        assertEquals("P001", updated.get(0).getProductId());
        assertEquals("P001", updated.get(1).getProductId());
        assertEquals("P001", updated.get(2).getProductId());
    }
    
    @Test
    public void testUpdateProduct_NoRemoteException_Success() {
        Product product = new Product("P001", "Test", 100.0, "Test", 10);
        try {
            adminService.Update_Product(product);
            assertTrue(true);
        } catch (RemoteException e) {
            fail(e.getMessage());
        }
    }
    
    @Test
    public void testUpdateProduct_NegativePrice_CallsUpdate() throws RemoteException {
        Product product = new Product("P001", "Negative Price", -100.0, "Test", 10);
        adminService.Update_Product(product);
        assertTrue(testDB.wasUpdateCalled());
        assertEquals(-100.0, testDB.getLastUpdatedProduct().getPrice(), 0.001);
    }
    
    @Test
    public void testUpdateProduct_NegativeQuantity_CallsUpdate() throws RemoteException {
        Product product = new Product("P001", "Negative Qty", 100.0, "Test", -10);
        adminService.Update_Product(product);
        assertTrue(testDB.wasUpdateCalled());
        assertEquals(-10, testDB.getLastUpdatedProduct().getQuantity());
    }
    
    @Test
    public void testUpdateProduct_ExactProductPassed_Success() throws RemoteException {
        Product product = new Product("P001", "Exact Product", 555.55, "Exact Description", 55);
        adminService.Update_Product(product);
        Product updated = testDB.getLastUpdatedProduct();
        assertEquals(product.getProductId(), updated.getProductId());
        assertEquals(product.getName(), updated.getName());
    }
    
    @Test
    public void testUpdateProduct_BulkUpdate_Success() throws RemoteException {
        for (int i = 1; i <= 100; i++) {
            Product product = new Product("P" + i, "Product" + i, 100.0 * i, "Updated", i);
            adminService.Update_Product(product);
        }
        assertEquals(100, testDB.getUpdateCount());
    }
    
    @Test
    public void testUpdateProduct_PriceReduction_Success() throws RemoteException {
        Product product = new Product("P001", "Sale Item", 500.0, "On Sale", 10);
        adminService.Update_Product(product);
        assertEquals(500.0, testDB.getLastUpdatedProduct().getPrice(), 0.001);
    }
    
    @Test
    public void testUpdateProduct_QuantityIncrease_Success() throws RemoteException {
        Product product = new Product("P001", "Restocked", 1000.0, "Back in stock", 100);
        adminService.Update_Product(product);
        assertEquals(100, testDB.getLastUpdatedProduct().getQuantity());
    }
}