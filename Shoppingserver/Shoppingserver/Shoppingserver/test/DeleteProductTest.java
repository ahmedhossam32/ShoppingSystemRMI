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

public class DeleteProductTest {
    
    private AdminService adminService;
    private TestDB testDB;
    
    private static class TestDB extends DB {
        private List<String> deletedProductIds = new ArrayList<String>();
        private boolean deleteCalled = false;
        
        @Override
        public void deleteProduct(String productId) {
            deleteCalled = true;
            deletedProductIds.add(productId);
        }
        
        public boolean wasDeleteCalled() {
            return deleteCalled;
        }
        
        public List<String> getDeletedProductIds() {
            return deletedProductIds;
        }
        
        public int getDeleteCount() {
            return deletedProductIds.size();
        }
        
        public String getLastDeletedProductId() {
            if (deletedProductIds.isEmpty()) {
                return null;
            }
            return deletedProductIds.get(deletedProductIds.size() - 1);
        }
        
        public void reset() {
            deletedProductIds.clear();
            deleteCalled = false;
        }
    }
    
    @BeforeClass
    public static void setUpClass() {
        System.out.println("Starting DeleteProductTest suite");
    }
    
    @AfterClass
    public static void tearDownClass() {
        System.out.println("Finished DeleteProductTest suite");
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
    public void testDeleteProduct_ValidProduct_Success() throws RemoteException {
        Product product = new Product("P001", "Laptop", 15000.0, "Gaming Laptop", 10);
        adminService.delete_Product(product);
        assertTrue(testDB.wasDeleteCalled());
        assertEquals(1, testDB.getDeleteCount());
        assertEquals("P001", testDB.getLastDeletedProductId());
    }
    
    @Test
    public void testDeleteProduct_MultipleProducts_Success() throws RemoteException {
        Product product1 = new Product("P001", "Laptop", 15000.0, "Gaming Laptop", 10);
        Product product2 = new Product("P002", "Mouse", 250.0, "Wireless Mouse", 50);
        Product product3 = new Product("P003", "Keyboard", 800.0, "Mechanical Keyboard", 30);
        adminService.delete_Product(product1);
        adminService.delete_Product(product2);
        adminService.delete_Product(product3);
        assertEquals(3, testDB.getDeleteCount());
        List<String> deleted = testDB.getDeletedProductIds();
        assertEquals("P001", deleted.get(0));
        assertEquals("P002", deleted.get(1));
        assertEquals("P003", deleted.get(2));
    }
    
    @Test
    public void testDeleteProduct_CorrectProductId_Used() throws RemoteException {
        String expectedId = "P100";
        Product product = new Product(expectedId, "Test Product", 100.0, "Description", 5);
        adminService.delete_Product(product);
        assertEquals(expectedId, testDB.getLastDeletedProductId());
    }
    
    @Test
    public void testDeleteProduct_NullProductId_CallsDelete() throws RemoteException {
        Product product = new Product(null, "Product", 100.0, "Description", 5);
        adminService.delete_Product(product);
        assertTrue(testDB.wasDeleteCalled());
        assertNull(testDB.getLastDeletedProductId());
    }
    
    @Test
    public void testDeleteProduct_EmptyProductId_CallsDelete() throws RemoteException {
        Product product = new Product("", "Product", 100.0, "Description", 5);
        adminService.delete_Product(product);
        assertTrue(testDB.wasDeleteCalled());
        assertEquals("", testDB.getLastDeletedProductId());
    }
    
    @Test
    public void testDeleteProduct_SpecialCharactersInId_Success() throws RemoteException {
        Product product = new Product("P@#$-001", "Product", 100.0, "Description", 5);
        adminService.delete_Product(product);
        assertEquals("P@#$-001", testDB.getLastDeletedProductId());
    }
    
    @Test
    public void testDeleteProduct_LongProductId_Success() throws RemoteException {
        StringBuilder longId = new StringBuilder("P");
        for (int i = 0; i < 100; i++) {
            longId.append("0");
        }
        Product product = new Product(longId.toString(), "Product", 100.0, "Description", 5);
        adminService.delete_Product(product);
        assertEquals(longId.toString(), testDB.getLastDeletedProductId());
    }
    
    @Test
    public void testDeleteProduct_OnlyIdUsed_OtherFieldsIgnored() throws RemoteException {
        Product product1 = new Product("P001", "Name1", 100.0, "Desc1", 10);
        Product product2 = new Product("P001", "DifferentName", 999.0, "DifferentDesc", 999);
        adminService.delete_Product(product1);
        testDB.reset();
        adminService.delete_Product(product2);
        assertEquals("P001", testDB.getLastDeletedProductId());
    }
    
    @Test
    public void testDeleteProduct_SameProductTwice_BothCallsMade() throws RemoteException {
        Product product = new Product("P001", "Product", 100.0, "Description", 5);
        adminService.delete_Product(product);
        adminService.delete_Product(product);
        assertEquals(2, testDB.getDeleteCount());
        assertEquals("P001", testDB.getDeletedProductIds().get(0));
        assertEquals("P001", testDB.getDeletedProductIds().get(1));
    }
    
    @Test
    public void testDeleteProduct_DBCalledOnce_Success() throws RemoteException {
        Product product = new Product("P002", "Single Delete", 100.0, "Test", 5);
        adminService.delete_Product(product);
        assertEquals(1, testDB.getDeleteCount());
    }
    
    @Test
    public void testDeleteProduct_NoRemoteException_Success() {
        Product product = new Product("P004", "Test", 100.0, "Test", 10);
        try {
            adminService.delete_Product(product);
            assertTrue(true);
        } catch (RemoteException e) {
            fail(e.getMessage());
        }
    }
    
    @Test
    public void testDeleteProduct_NumericId_Success() throws RemoteException {
        Product product = new Product("12345", "Product", 100.0, "Description", 5);
        adminService.delete_Product(product);
        assertEquals("12345", testDB.getLastDeletedProductId());
    }
    
    @Test
    public void testDeleteProduct_WhitespaceInId_Success() throws RemoteException {
        Product product = new Product("P 001", "Product", 100.0, "Description", 5);
        adminService.delete_Product(product);
        assertEquals("P 001", testDB.getLastDeletedProductId());
    }
    
    @Test
    public void testDeleteProduct_DifferentIdFormats_Success() throws RemoteException {
        Product product1 = new Product("P001", "Product1", 100.0, "Desc", 5);
        Product product2 = new Product("PROD-002", "Product2", 100.0, "Desc", 5);
        Product product3 = new Product("p_003", "Product3", 100.0, "Desc", 5);
        adminService.delete_Product(product1);
        adminService.delete_Product(product2);
        adminService.delete_Product(product3);
        List<String> deleted = testDB.getDeletedProductIds();
        assertEquals("P001", deleted.get(0));
        assertEquals("PROD-002", deleted.get(1));
        assertEquals("p_003", deleted.get(2));
    }
    
    @Test
    public void testDeleteProduct_ZeroPrice_SuccessfulDelete() throws RemoteException {
        Product product = new Product("P005", "Free Item", 0.0, "Description", 5);
        adminService.delete_Product(product);
        assertEquals("P005", testDB.getLastDeletedProductId());
    }
    
    @Test
    public void testDeleteProduct_ZeroQuantity_SuccessfulDelete() throws RemoteException {
        Product product = new Product("P006", "Out of Stock", 100.0, "Description", 0);
        adminService.delete_Product(product);
        assertEquals("P006", testDB.getLastDeletedProductId());
    }
    
    @Test
    public void testDeleteProduct_BulkDelete_Success() throws RemoteException {
        for (int i = 1; i <= 100; i++) {
            Product product = new Product("P" + i, "Product" + i, 100.0, "Desc", 10);
            adminService.delete_Product(product);
        }
        assertEquals(100, testDB.getDeleteCount());
    }
    
    @Test
    public void testDeleteProduct_OnlyIdPassed_Success() throws RemoteException {
        Product product = new Product("P007", "Product", 100.0, "Description", 5);
        adminService.delete_Product(product);
        assertEquals("P007", testDB.getLastDeletedProductId());
        assertTrue(testDB.wasDeleteCalled());
    }
}