package banking;

import java.sql.SQLException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws SQLException {
        String url = "jdbc:sqlite:" + args[1];
        final Database db = new Database(url);
        final Scanner scanner = new Scanner(System.in);
        if(db.isValid()) {
            BankController bk = new BankController(scanner,"DKB",db);
            bk.initBankingSystem();
        }
    }

}