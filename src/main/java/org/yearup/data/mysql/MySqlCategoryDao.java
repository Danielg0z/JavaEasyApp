package org.yearup.data.mysql;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.yearup.data.CategoryDao;
import org.yearup.models.Category;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class MySqlCategoryDao extends MySqlDaoBase implements CategoryDao
{
    private static final Logger log = LoggerFactory.getLogger(MySqlCategoryDao.class);
    private MySqlDaoBase dataSource;

    public MySqlCategoryDao(DataSource dataSource)
    {
        super(dataSource);
    }

    @Override
    public List<Category> getAllCategories()
    {

        List<Category> categories = new ArrayList<>();

        String sql = "SELECT * FROM categories";

        // This is a "try-with-resources" block.
        // It ensures that the Connection, Statement, and ResultSet are closed automatically after we are done.
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement(); // change ot prepared
             ResultSet resultSet = statement.executeQuery(sql)) {

            // Loop through each row in the ResultSet.
            while (resultSet.next()) {
                Category category = mapRow(resultSet);
                 // Add the Product object to our list.
                categories.add(category);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }

        return categories;
    }

    @Override
    public Category getById(int categoryId)
    {
        Category category = null;

        String sql = "SELECT category_id, name, description FROM categories WHERE category_id = ?";

        // This is a "try-with-resources" block.
        // It ensures that the Connection, Statement, and ResultSet are closed automatically after we are done.
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);) {
            {

                preparedStatement.setInt(1, categoryId);

                try(ResultSet keys = preparedStatement.executeQuery()){
                    if (keys.next()){
                        category = mapRow(keys);

                    }
                }
            }

        } catch (SQLException e) {
            // If something goes wrong (SQL error), print the stack trace to help debug.
            e.printStackTrace();
        }

        return category;
    }


    @Override
    public Category getByName(String name){
        Category category = null;

        String sql = "SELECT category_id, name, description FROM categories WHERE name = ?";

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, name);

            try(ResultSet resultSet = preparedStatement.executeQuery()){
                if(resultSet.next()){
                    category = mapRow(resultSet);
                }
            }

        } catch (SQLException e){
            e.printStackTrace();
        }
        return  category;

        }

    @Override
    public Category create(Category category)
    {
        String sql = "INSERT INTO Categories (name, description) VALUES (?, ?)";

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, category.getName());

            preparedStatement.setString(2, category.getDescription());

            preparedStatement.executeUpdate();

            try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    int newId = resultSet.getInt(1);
                    return getById(newId);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        return category;
    }

    @Override
    public Category update(int categoryId, Category category)
    {
        String sql = "UPDATE categories SET name = ?, description = ? WHERE category_id = ?";

        try(Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setString(1, category.getName());
            preparedStatement.setString(2, category.getDescription());
            preparedStatement.setInt(3, categoryId);

            int rowsAffected = preparedStatement.executeUpdate();
            if(rowsAffected > 0){
                return getById(categoryId); // returns the actual object from the DB
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return category;
    }

    @Override
    public Category delete(int categoryId)
    {
        Category categoryToDelete = getById(categoryId);

        if (categoryToDelete == null) {
            return null;
        }

        String sql = "DELETE FROM categories WHERE category_id = ?";

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

                preparedStatement.setInt(1, categoryId);
                preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        return categoryToDelete;
    }


    private Category mapRow(ResultSet row) throws SQLException
    {
        int categoryId = row.getInt("category_id");
        String name = row.getString("name");
        String description = row.getString("description");

        Category category = new Category()
        {{
            setCategoryId(categoryId);
            setName(name);
            setDescription(description);
        }};

        return category;
    }

}
