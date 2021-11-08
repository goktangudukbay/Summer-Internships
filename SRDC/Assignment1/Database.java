
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Database{

    //properties
    private String url;
    private String user;
    private String password;
    private Connection conn;
    private Statement stmt;

    //constructor
    public Database(String url, String user, String password){
        this.url = url;
        this.user = user;
        this.password = password;
        conn = null;
        stmt = null;
    }

    //methods
    //connection method
    public void connection() throws ClassNotFoundException{
        try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to the PostgreSQL server successfully.");
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    //query methods
    //user information when login is succesful
    public User queryAllUserInformation(String username) throws SQLException {
         String query;
         query = "select * from users where username = \'" + username + "\'";
         User user;
         user = new User();
         try {
                stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query);
				if(rs.next())
					user = new User( rs.getString("firstname"), rs.getString("lastname"),  rs.getDate("birthdate").toString(),
                        rs.getString("gender").charAt(0), rs.getString("email"), rs.getString("username"),
                        rs.getString("passcode"), rs.getBoolean("isAdmin"));
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                if (stmt != null) {
                    stmt.close();
                }
            }

         return user;
    }


    public boolean loginSuccesfulCheck(String username, String password) throws SQLException{
        String query;
        boolean success = false;
        try {
            stmt = conn.createStatement();
            query = "select passcode from users where username = \'" + username + "\'";
            ResultSet rs = stmt.executeQuery(query);
            if (rs.next()){
                System.out.println(rs.getString("passcode"));
                if (rs.getString("passcode").equals(password))
                    success = true;
            }
        }catch (SQLException e) {
                e.printStackTrace();
        } finally {
            if (stmt != null) {
                stmt.close();
            }
        }
        return success;
    }

    public ArrayList<Message> getInbox(String username) throws SQLException{
        String query;
        ArrayList<Message> inbox = new ArrayList<Message>();
        try{
            stmt = conn.createStatement();
            query = "select * from messagetable where receiver = '" + username + "'";
            ResultSet rs = stmt.executeQuery(query);
            Message temp;
            while(rs.next()){
                temp = new Message( rs.getString("Sender"), rs.getString("Receiver"), rs.getString("messageTime"),
                        rs.getString("title"), rs.getString("messageContent"));
                inbox.add(temp);
				System.out.println("2eqwasw");
            }
        }catch(SQLException e){
            e.printStackTrace();
        }finally{
            if(stmt !=  null){
                stmt.close();
            }
        }
        return inbox;
    }

    public ArrayList<Message> getOutbox(String username) throws SQLException{
        String query;
        ArrayList<Message> outbox = new ArrayList<Message>();
        try{
            stmt = conn.createStatement();
            query = "select * from messagetable where sender = '" + username + "'";
            ResultSet rs = stmt.executeQuery(query);
            Message temp;
            while(rs.next()){
                temp = new Message( rs.getString("Sender"), rs.getString("Receiver"), rs.getString("messageTime"),
                        rs.getString("title"), rs.getString("messageContent"));
                outbox.add(temp);
            }
        }catch(SQLException e){
            e.printStackTrace();
        }finally{
            if(stmt !=  null){
                stmt.close();
            }
        }
        return outbox;
    }



    public boolean sendMessage(Message m) throws SQLException{
        String query;
        boolean success = false;

        try {
            stmt = conn.createStatement();
            query = "Insert into messagetable Values('" + m.getSender() + "', '" + m.getReceiver() + "', '" + m.getTitle()
                    + "', '" + m.getTime() + "', '" + m.getContent() + "')";

            if (stmt.executeUpdate(query) > 0)
                success = true;

        }catch(SQLException e){
            e.printStackTrace();
        }finally{
            if(stmt !=  null){
                stmt.close();
            }
        }
        return success;
    }



    //add user method input is in the format |ADDUSER|firstname|surname|birthdate|gender|email|username|passwordcolumn|isAdmin|inbox|outbox|
    public boolean addUser(User user) throws SQLException, ParseException{
        String query;
        boolean success = false;
        try{
            stmt = conn.createStatement();
            query = "insert into users Values('" + user.getName() + "', '" + user.getLastName() + "', TO_DATE('" + user.getBirthDate() + "','yyyy/mm/dd'), '" +
                    user.getGender() + "', '" + user.getEmail() + "', '" + user.getUsername() + "', '" + user.getPassword() + "', " + user.isAdmin() + ")";

			System.out.println(query);
            System.out.println(query);
            if(stmt.executeUpdate(query)  > 0)
                success = true;

        }catch(SQLException e){
            success = false;
            e.printStackTrace();
        }finally{
            if(stmt !=  null){
                stmt.close();
            }
        }
        return success;
    }

    //update method in the format Username|DataToChange|NewData|
    public boolean updateUser(String username, String dataToChange, String newData) throws SQLException, ParseException{
        String query;
        boolean success = false;

        try {
            stmt = conn.createStatement();
            if(dataToChange.equals("birthdate"))
                query = "update users set " + dataToChange + " = TO_DATE('" + newData + "', 'yyyy/mm/dd') where username = '" + username + "'";
            else if(dataToChange.equals("admin")) {
                boolean isadm = false;
                if(newData.charAt(0) == 'T' || newData.charAt(0) == 't')
                    isadm = true;
                query = "update users set " + dataToChange + " = " + isadm + " where username = '" + username + "'";
            }
            else
                query = "update users set " + dataToChange + " = \'" + newData + "\' where username = \'" + username + "\'";
            System.out.println(query);
            if(stmt.executeUpdate(query) > 0)
                success = true;
        }catch (SQLException e){
            e.printStackTrace();
        }
        finally{
            if(stmt !=  null){
                stmt.close();
            }
        }
        return success;
    }


    //remove user method input in the form "|REMOVE|username|"
    public boolean removeUser(String username) throws SQLException{
        String query;
        boolean success = false;
        try{
            stmt = conn.createStatement();
            query = "delete from users where username = \'" + username + "\'";
            if(stmt.executeUpdate(query) > 0)
                success = true;
        }
        catch(SQLException e) {
            e.printStackTrace();
        }
        finally {
            if(stmt != null){
                stmt.close();
            }
        }
        return success;
    }

    //listusers method
    public ArrayList<User> listUsers() throws SQLException{
        String query;
        ArrayList<User> list= new ArrayList<User>();
        User temp;
        try{
            stmt = conn.createStatement();
            query = "SELECT * FROM users" + " ORDER BY username ASC";
            ResultSet rs = stmt.executeQuery(query);

            while(rs.next()){
                temp = new User(rs.getString("firstname"), rs.getString("lastname"), rs.getString("birthdate"),
                        rs.getString("gender").charAt(0), rs.getString("email"),rs.getString("username"),
                        rs.getString("passcode"), rs.getBoolean("isAdmin"));
                list.add(temp);
            }
        }catch(SQLException e){
            e.printStackTrace();
        }finally{
            if(stmt != null)
                stmt.close();
        }
        return list;
    }

    public void closeConnection() throws SQLException{
        try {
            conn.close();
        } catch(SQLException e){
            e.printStackTrace();
        }
    }
}
