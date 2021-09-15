package banking;

import org.sqlite.SQLiteDataSource;

import java.sql.*;

public class Database {
    private final String url;
    private final SQLiteDataSource dataSource = new SQLiteDataSource();
    private Connection connection;
    private boolean isValid;
    private Statement statement;
    private final String TABLE = "card";


    public Database(String url) throws SQLException {
        this.url = url;
        dataSource.setUrl(url);
        this.connection = dataSource.getConnection();
        this.isValid = true;
        init();

    }

    public boolean isValid() {
        return isValid;
    }

    public int getMaxId() {
        String query = "SELECT * FROM card WHERE id = (SELECT MAX(id) FROM card)";
        try (ResultSet rs = this.query(query)) {
            while (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private void init() {
        try {
            this.statement = this.connection.createStatement();
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS card(" +
                    "id INTEGER," +
                    "number TEXT," +
                    "pin TEXT," +
                    "balance INTEGER DEFAULT 0)");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ResultSet query(String sql) {
        ResultSet result = null;

        try {
            statement = connection.createStatement();
            result = statement.executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        clearStatement();
        return result;

    }

    public int insert(Integer id, String number, String pin) {
        int i = 0;
        String s = "INSERT INTO " + TABLE + " (id,number,pin) VALUES(?,?,?)";
        try (PreparedStatement insert = this.connection.prepareStatement(s)) {
            insert.setInt(1, id);
            insert.setString(2, number);
            insert.setString(3, pin);
            insert.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return i;
    }

    public boolean delete(int id) {

        String sql = "DELETE FROM " + TABLE + " WHERE id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setInt(1,id);
            preparedStatement.executeUpdate();
            return true;
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateBalance(int income, int id) {

        ResultSet rs = query("SELECT * FROM " + TABLE + " WHERE id = " + id);

        try {

            int oldBalance = -1;

            while (rs.next()) {
                oldBalance = rs.getInt("balance");
            }

            String sql = "UPDATE " + TABLE + " SET balance = ? WHERE id=?";

            try (PreparedStatement preparedStatement = this.connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, oldBalance + income);
                preparedStatement.setInt(2, id);
                preparedStatement.executeUpdate();
                preparedStatement.close();
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }


        return false;
    }

    public boolean transfer(int id, int amount, int ownID) {

        boolean amountReduced = this.updateBalance(amount * -1, ownID);

        if (amountReduced) {
            if (this.updateBalance(amount, id)) {
                System.out.println("true");
                return true;
            }
        } else {

            return false;
        }


        return false;
    }

    private void clearStatement() {
        this.statement = null;
    }

}

