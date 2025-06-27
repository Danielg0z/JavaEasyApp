// This class handles all shopping cart operations using MySQL
package org.yearup.data.mysql;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yearup.data.ProductDao;
import org.yearup.data.ShoppingCartDao;
import org.yearup.models.Product;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcShoppingCartDAO extends MySqlDaoBase implements ShoppingCartDao {

    // Used to look up full product details when retrieving cart items
    private ProductDao productDao;

    // Constructor that injects the DataSource and ProductDao dependencies
    public JdbcShoppingCartDAO(DataSource dataSource, ProductDao productDao) {
        super(dataSource);
        this.productDao = productDao;
    }

    // Get a user's shopping cart by userId
    @Override
    public ShoppingCart getByUserId(int userId) {
        ShoppingCart shoppingCart = new ShoppingCart();
        String sql = "SELECT product_id, quantity FROM shopping_cart WHERE user_id = ?";

        try(Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();

            // Loop through each row and create ShoppingCartItems
            while (resultSet.next()) {
                int productId = resultSet.getInt("product_id");
                int quantity = resultSet.getInt("quantity");

                Product product = productDao.getById(productId);
                if (product != null) {
                    ShoppingCartItem item = new ShoppingCartItem();
                    item.setProduct(product);
                    shoppingCart.add(item);
                }
            }

        } catch (SQLException e){
            throw new RuntimeException(e);
        }

        return shoppingCart;
    }

    // Adds a product to the user's cart or increases quantity if it already exists
    @Override
    public void addProduct(int userId, int productId) {
        String updateSql = "UPDATE shopping_cart SET quantity = quantity + 1 WHERE user_id = ? AND product_id = ?";
        String insertSql = "INSERT INTO shopping_cart (user_id, product_id, quantity) " +
                "SELECT ?, ?, 1 WHERE NOT EXISTS (SELECT 1 FROM shopping_cart WHERE user_id = ? AND product_id = ?)";

        try (Connection connection = getConnection();
             PreparedStatement updateStmt = connection.prepareStatement(updateSql)) {

            updateStmt.setInt(1, userId);
            updateStmt.setInt(2, productId);
            int rows = updateStmt.executeUpdate();

            // If no update occurred, insert the product as a new item
            if (rows == 0) {
                try (PreparedStatement insertStmt = connection.prepareStatement(insertSql)) {
                    insertStmt.setInt(1, userId);
                    insertStmt.setInt(2, productId);
                    insertStmt.setInt(3, userId);
                    insertStmt.setInt(4, productId);
                    insertStmt.executeUpdate();
                }
            }
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    // Updates the quantity of a specific product in the cart
    @Override
    public void updateItemQuantity(int userId, int productId, int quantity) {
        String sql = "UPDATE shopping_cart SET quantity = ? WHERE user_id = ? AND product_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setInt(1, quantity);
            preparedStatement.setInt(2, userId);
            preparedStatement.setInt(3, productId);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Removes one item (product) from the shopping cart
    @Override
    public void removeItem(int userId, int productId) {
        String sql = "DELETE FROM shopping_cart WHERE user_id = ? AND product_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, productId);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Clears the entire cart for a specific user
    @Override
    public void clearCart(int userId) {
        String sql = "DELETE FROM shopping_cart WHERE user_id = ?";

        try (Connection connection = getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, userId);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Increments the quantity of a product by 1
    @Override
    public void incrementQuantity(int userId, int productId) {
        String sql = "UPDATE shopping_cart SET quantity = quantity + 1 WHERE user_id = ? AND product_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, productId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Decrements quantity or removes item if quantity goes to 0
    @Override
    public void decrementQuantity(int userId, int productId) {
        String selectSql = "SELECT quantity FROM shopping_cart WHERE user_id = ? AND product_id = ?";
        String updateSql = "UPDATE shopping_cart SET quantity = quantity - 1 WHERE user_id = ? AND product_id = ?";
        String deleteSql = "DELETE FROM shopping_cart WHERE user_id = ? AND product_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {

            selectStmt.setInt(1, userId);
            selectStmt.setInt(2, productId);
            ResultSet rs = selectStmt.executeQuery();

            if (rs.next()) {
                int quantity = rs.getInt("quantity");

                if (quantity > 1) {
                    // Reduce quantity by 1
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                        updateStmt.setInt(1, userId);
                        updateStmt.setInt(2, productId);
                        updateStmt.executeUpdate();
                    }
                } else {
                    // If quantity is 1, remove item instead
                    try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                        deleteStmt.setInt(1, userId);
                        deleteStmt.setInt(2, productId);
                        deleteStmt.executeUpdate();
                    }
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Returns all items in the cart for a specific user
    @Override
    public List<ShoppingCartItem> getItems(int userId) {
        List<ShoppingCartItem> items = new ArrayList<>();

        String sql = "SELECT product_id, quantity FROM shopping_cart WHERE user_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int productId = rs.getInt("product_id");
                int quantity = rs.getInt("quantity");

                // Use product DAO to fetch full product info
                Product product = productDao.getById(productId);

                if (product != null) {
                    ShoppingCartItem item = new ShoppingCartItem();
                    item.setProduct(product);
                    item.setQuantity(quantity);

                    items.add(item);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return items;
    }
}
