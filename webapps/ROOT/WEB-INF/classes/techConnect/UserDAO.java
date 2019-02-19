package techConnect;


import java.sql.*;

public class UserDAO
{
    static Connection currentCon = null;
    static ResultSet rs = null;



    public static UserBean login(UserBean bean)  {

        //preparing some objects for connection
        Statement stmt = null;

        String username = bean.getUsername();
        String password = bean.getPassword();

        String searchQuery =
                "select * from users where username='"
                        + username
                        + "' AND password='"
                        + password
                        + "'";

        //Prints out username/password if found, null if not
        System.out.println("Your user name is " + username);
        System.out.println("Your password is " + password);
        System.out.println("Query: "+searchQuery);

        try
        {
            //connect to DB
            currentCon = techConnect.ConnectionManager.getConnection();
            stmt=currentCon.createStatement();
            rs = stmt.executeQuery(searchQuery);
            boolean more = rs.next();

            // if user does not exist set the isValid variable to false
            if (!more)
            {
                System.out.println("Sorry, you are not a registered user! Please sign up first");
                bean.setValid(false);
            }

            //if user exists set the isValid variable to true
            else if (more)
            {
                String firstName = rs.getString("FirstName");
                String lastName = rs.getString("LastName");

                System.out.println("Welcome " + firstName);
                bean.setFirstName(firstName);
                bean.setLastName(lastName);
                bean.setValid(true);
            }
        }

        catch (Exception ex)
        {
            System.out.println("Log In failed: An Exception has occurred! " + ex);
        }

        //some exception handling
        finally
        {
            if (rs != null)	{
                try {
                    rs.close();
                } catch (Exception e) {}
                rs = null;
            }

            if (stmt != null) {
                try {
                    stmt.close();
                } catch (Exception e) {}
                stmt = null;
            }

            if (currentCon != null) {
                try {
                    currentCon.close();
                } catch (Exception e) {
                }

                currentCon = null;
            }
        }

        return bean;

    }

    public static void registration(UserBean bean) {
        String insertionQuery = "INSERT INTO users (user_name, pass, email) VALUES (?, ?, ?)";
        try {
            currentCon = ConnectionManager.getConnection();
            PreparedStatement insertStmt = currentCon.prepareStatement(insertionQuery);
            insertStmt.setString(1, bean.getUsername());
            insertStmt.setString(2, bean.getPassword());
            insertStmt.setString(3, bean.getEmail());
            insertStmt.executeUpdate();
        }
        catch (SQLException e) {
            System.out.println("failed to register" + e.getMessage());
        }
    }

    //Does this username already exist?
    public static boolean getUsername(String username) {
        String searchQuery = "SELECT userID FROM users WHERE username=?";
        ResultSet results;
        try {
            currentCon = ConnectionManager.getConnection();
            PreparedStatement getStmt = currentCon.prepareStatement(searchQuery);
            getStmt.setString(1, username);
            results = getStmt.executeQuery();
            return results == null;
        }
        catch (SQLException e) {
            return false;
        }
    }
}
