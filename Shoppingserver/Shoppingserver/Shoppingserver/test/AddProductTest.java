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

public class AddProductTest {
    
    private AdminService adminService;
    private TestDB testDB;
    
    private static class TestDB extends DB {
        private List<Product> insertedProducts = new ArrayList<Product>();
        private boolean insertCalled = false;
        
        @Override
        public void insertProduct(Product product) {
            insertCalled = true;
            insertedProducts.add(product);
        }
        
        public boolean wasInsertCalled() {
            return insertCalled;
        }
        
        public List<Product> getInsertedProducts() {
            return insertedProducts;
        }
        
        public int getInsertCount() {
            return insertedProducts.size();
        }
        
        public Product getLastInsertedProduct() {
            if (insertedProducts.isEmpty()) {
                return null;
            }
            return insertedProducts.get(insertedProducts.size() - 1);
        }
        
        public void reset() {
            insertedProducts.clear();
            insertCalled = false;
        }
    }
    
    @BeforeClass
    public static void setUpClass() {
        System.out.println("Starting AddProductTest suite");
    }
    
    @AfterClass
    public static void tearDownClass() {
        System.out.println("Finished AddProductTest suite");
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
    public void testAddProduct_ValidProduct_Success() throws RemoteException {
        Product product = new Product("P001", "Laptop", 15000.0, "Gaming Laptop", 10);
        adminService.Add_Product(product);
        assertTrue(testDB.wasInsertCalled());
        assertEquals(1, testDB.getInsertCount());
        Product inserted = testDB.getLastInsertedProduct();
        assertNotNull(inserted);
        assertEquals("P001", inserted.getProductId());
        assertEquals("Laptop", inserted.getName());
        assertEquals(15000.0, inserted.getPrice(), 0.001);
    }
    
    @Test
    public void testAddProduct_MultipleProducts_Success() throws RemoteException {
        Product product1 = new Product("P001", "Laptop", 15000.0, "Gaming Laptop", 10);
        Product product2 = new Product("P002", "Mouse", 250.0, "Wireless Mouse", 50);
        Product product3 = new Product("P003", "Keyboard", 800.0, "Mechanical Keyboard", 30);
        adminService.Add_Product(product1);
        adminService.Add_Product(product2);
        adminService.Add_Product(product3);
        assertEquals(3, testDB.getInsertCount());
        List<Product> inserted = testDB.getInsertedProducts();
        assertEquals("P001", inserted.get(0).getProductId());
        assertEquals("P002", inserted.get(1).getProductId());
        assertEquals("P003", inserted.get(2).getProductId());
    }
    
    @Test
    public void testAddProduct_MinimumData_Success() throws RemoteException {
        Product product = new Product("P999", "Test Product", 0.0, "", 0);
        adminService.Add_Product(product);
        assertTrue(testDB.wasInsertCalled());
        assertEquals(1, testDB.getInsertCount());
    }
    
    @Test
    public void testAddProduct_NullProductId_CallsInsert() throws RemoteException {
        Product product = new Product(null, "Product", 100.0, "Description", 5);
        adminService.Add_Product(product);
        assertTrue(testDB.wasInsertCalled());
        assertNull(testDB.getLastInsertedProduct().getProductId());
    }
    
    @Test
    public void testAddProduct_HighPrice_Success() throws RemoteException {
        Product product = new Product("P004", "Expensive Item", 999999.99, "Luxury Product", 1);
        adminService.Add_Product(product);
        assertTrue(testDB.wasInsertCalled());
        assertEquals(999999.99, testDB.getLastInsertedProduct().getPrice(), 0.001);
    }
    
    @Test
    public void testAddProduct_ZeroPrice_Success() throws RemoteException {
        Product product = new Product("P005", "Free Item", 0.0, "Free product", 100);
        adminService.Add_Product(product);
        assertEquals(0.0, testDB.getLastInsertedProduct().getPrice(), 0.001);
    }
    
    @Test
    public void testAddProduct_LargeQuantity_Success() throws RemoteException {
        Product product = new Product("P006", "Bulk Item", 10.0, "Wholesale", 10000);
        adminService.Add_Product(product);
        assertEquals(10000, testDB.getLastInsertedProduct().getQuantity());
    }
    
    @Test
    public void testAddProduct_SpecialCharactersInName_Success() throws RemoteException {
        Product product = new Product("P007", "Product @#$%", 50.0, "Special chars!", 5);
        adminService.Add_Product(product);
        assertEquals("Product @#$%", testDB.getLastInsertedProduct().getName());
    }
    
    @Test
    public void testAddProduct_LongDescription_Success() throws RemoteException {
        StringBuilder longDesc = new StringBuilder();
        for (int i = 0; i < 50; i++) {
            longDesc.append("This is a very long description. ");
        }
        Product product = new Product("P008", "Product", 100.0, longDesc.toString(), 10);
        adminService.Add_Product(product);
        assertTrue(testDB.getLastInsertedProduct().getDescription().length() > 100);
    }
    
    @Test
    public void testAddProduct_DBCalledOnce_Success() throws RemoteException {
        Product product = new Product("P009", "Single Call Test", 100.0, "Test", 5);
        adminService.Add_Product(product);
        assertEquals(1, testDB.getInsertCount());
    }
    
    @Test
    public void testAddProduct_DuplicateProduct_BothCallsMade() throws RemoteException {
        Product product1 = new Product("P010", "Duplicate", 100.0, "First", 5);
        Product product2 = new Product("P010", "Duplicate", 150.0, "Second", 10);
        adminService.Add_Product(product1);
        adminService.Add_Product(product2);
        assertEquals(2, testDB.getInsertCount());
    }
    
    @Test
    public void testAddProduct_VerifyProductData_Correct() throws RemoteException {
        String expectedId = "P011";
        String expectedName = "Test Product";
        String expectedDesc = "Test Description";
        double expectedPrice = 299.99;
        int expectedQty = 15;
        Product product = new Product(expectedId, expectedName, expectedPrice, expectedDesc, expectedQty);
        adminService.Add_Product(product);
        Product inserted = testDB.getLastInsertedProduct();
        assertEquals(expectedId, inserted.getProductId());
        assertEquals(expectedName, inserted.getName());
        assertEquals(expectedDesc, inserted.getDescription());
        assertEquals(expectedPrice, inserted.getPrice(), 0.001);
        assertEquals(expectedQty, inserted.getQuantity());
    }
    
    @Test
    public void testAddProduct_NoRemoteException_Success() {
        Product product = new Product("P012", "Test", 100.0, "Test", 10);
        adminService.Add_Product(product);
        assertTrue(true);
    }
    
    @Test
    public void testAddProduct_NegativePrice_CallsInsert() throws RemoteException {
        Product product = new Product("P013", "Negative Price", -100.0, "Test", 10);
        adminService.Add_Product(product);
        assertTrue(testDB.wasInsertCalled());
        assertEquals(-100.0, testDB.getLastInsertedProduct().getPrice(), 0.001);
    }
    
    @Test
    public void testAddProduct_NegativeQuantity_CallsInsert() throws RemoteException {
        Product product = new Product("P014", "Negative Qty", 100.0, "Test", -5);
        adminService.Add_Product(product);
        assertTrue(testDB.wasInsertCalled());
        assertEquals(-5, testDB.getLastInsertedProduct().getQuantity());
    }
}