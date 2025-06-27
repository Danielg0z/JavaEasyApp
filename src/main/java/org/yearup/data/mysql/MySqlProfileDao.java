package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.models.Profile;
import org.yearup.data.ProfileDao;

import javax.sql.DataSource;
import java.sql.*;

@Component
public class MySqlProfileDao extends MySqlDaoBase implements ProfileDao
{
    public MySqlProfileDao(DataSource dataSource)
    {
        super(dataSource);
    }

    @Override
    public Profile create(Profile profile)
    {
        String sql = "INSERT INTO profiles (user_id, first_name, last_name, phone, email, address, city, state, zip) " +
                " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try(Connection connection = getConnection())
        {
            PreparedStatement ps = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setInt(1, profile.getUserId());
            ps.setString(2, profile.getFirstName());
            ps.setString(3, profile.getLastName());
            ps.setString(4, profile.getPhone());
            ps.setString(5, profile.getEmail());
            ps.setString(6, profile.getAddress());
            ps.setString(7, profile.getCity());
            ps.setString(8, profile.getState());
            ps.setString(9, profile.getZip());

            ps.executeUpdate();

            return profile;
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }


    // This method updates the user’s profile in the database.
    // We fill in all the new values from the Profile object,
    // and tell SQL to apply them only to the row where the user_id matches.
    // If it works, we fetch the new version and return it.
    // This is helpful when a user updates their settings or contact info.

    @Override
    public Profile update(int userId, Profile profile) {
        //Changes the user's profile based on their ID
        String sql = "UPDATE profiles SET first_name = ?, last_name = ?, phone = ?, email = ?, address = ?, city = ?, state = ?, zip = ? " +
                "WHERE user_id = ?";

        // Fill in all the (?) placeholders with the profile info
        try(Connection connection = getConnection();
           PreparedStatement preparedStatement = connection.prepareStatement(sql)){

            preparedStatement.setString(1, profile.getFirstName());
            preparedStatement.setString(2, profile.getLastName());
            preparedStatement.setString(3, profile.getPhone());
            preparedStatement.setString(4, profile.getEmail());
            preparedStatement.setString(5, profile.getAddress());
            preparedStatement.setString(6, profile.getCity());
            preparedStatement.setString(7, profile.getState());
            preparedStatement.setString(8, profile.getZip());
            preparedStatement.setInt(9,userId);

            // actually run the update command in the DB
            int rows = preparedStatement.executeUpdate();

            // if the update worked, return the freshly update profile
            if(rows > 0){
                return getById(userId);
            } else{
                return null;
            }

        } catch (SQLException e){
            throw new RuntimeException(e);
        }



    }

    // This method looks in the database for a profile with the matching user ID.
    // If it finds one, it fills out a Profile object and gives it back.
    // If nothing is found, it returns null.
    // This is useful when a user logs in and wants to view their saved profile.

    @Override
    public Profile getById(int userId) {
        //finds a profile where the user_Id matches user Input
        String sql = "SELECT * FROM profiles WHERES user_id = ?";

        try(Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);){

            //Tells SQL what the (?) value is
            preparedStatement.setInt(1,userId);

            //Run the SQL and get the results back(like reading answers from a form)
            try (ResultSet resultSet = preparedStatement.executeQuery()){
                if(resultSet.next()){
                    resultSet.getInt("user_Id");
                    resultSet.getString("first_name");
                    resultSet.getString("last_name");
                    resultSet.getString("phone");
                    resultSet.getString("email");
                    resultSet.getString("address");
                    resultSet.getString("city");
                    resultSet.getString("state");
                    resultSet.getString("zip");

                }
            }

        } catch (SQLException e) {
            // if there's a problem talking to the DB, crash program with explanation
            throw new RuntimeException(e);
        }

        //if no profile was found, return nothing(null)
        return null;
    }

}
