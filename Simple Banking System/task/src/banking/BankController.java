package banking;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Scanner;

public class BankController {
    private final Scanner scanner;
    private final Database db;


    public BankController(Scanner scanner, String bankName, Database db) throws SQLException {
        this.scanner = scanner;
        this.db = db;
    }

    public void initBankingSystem() {
        boolean flag = true;
        boolean secondFlag = true;

        while (flag) {
            printMenu();
            String selection = scanner.nextLine();
            switch (selection) {
                case "1":
                    int lastID = db.getMaxId() != -1? db.getMaxId():0;
                    Card card = new Card(lastID);
                    System.out.println("\nYour card has been created");
                    System.out.println("Your card number:");
                    System.out.println(card.getCardNumber());
                    System.out.println("Your card PIN:");
                    System.out.println(card.getPin() + "\n");
//                    Add Card
                    createNewCard(card);
                    break;
                case "2":
                    System.out.println("\nEnter your card number:");
                    String cardNumber = scanner.nextLine();
                    System.out.println("Enter your PIN:");
                    String pin = scanner.nextLine();

                    int id = -1;

                    ResultSet rs = db.query("SELECT * FROM card WHERE number=" + cardNumber + " AND pin=" + pin + ";");
                    int affectedRows = 0;
                    try {
                        while (rs.next()) {
                            id = rs.getInt("id");
                            affectedRows++;
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    if (affectedRows == 1) {
                        System.out.println("You have successfully logged in!");

                        while (secondFlag) {

                            printSubMenu();
                            String input = scanner.nextLine();

                            switch (input) {
                                case "1":
                                    System.out.println("\nBalance: " + this.getBalance(id));
                                    break;
                                case "2":
                                    System.out.println("\nEnter income:");
                                    int income = scanner.nextInt();
                                    scanner.nextLine();
                                    if(this.addIncome(income,id)) {
                                        System.out.println("\nIncome was added!");
                                    }
                                    break;
                                case "3":
                                    System.out.println("\nTransfer\n" +
                                            "Enter card number:");
                                    String number = scanner.nextLine();

                                    if(checkLuhn(number)) {
                                        String query = "SELECT * FROM card WHERE number=" + number + ";";
                                        try(ResultSet resultSet = db.query(query)) {
                                            int transID = -1;
                                            int balance = -1;
                                            while (resultSet.next()) {
                                              transID =   resultSet.getInt("id");
                                            }

                                            if(transID != -1 && transID != id) {
                                                System.out.println("Enter how much money you want to transfer:");
                                                int amount =  scanner.nextInt();
                                                scanner.nextLine();
                                                if(getBalance(id) >= amount) {
                                                    if(db.transfer(transID,amount,id)) {
                                                        System.out.println("Success!");
                                                    };
                                                } else {
                                                    System.out.println("Not enough money!");
                                                }
                                            }else if(id == transID) {
                                                System.out.println("You can't transfer money to the same account!");
                                            }else {
                                                System.out.println("Such a card does not exist.");
                                            }


                                        } catch (SQLException e) {
                                            e.printStackTrace();
                                        }
                                    } else {
                                        System.out.println("Probably you made a mistake in the card number. Please try again!");
                                    }
                                    break;
                                case "4":
                                    if(db.delete(id)) {
                                        System.out.println("The account has been closed!");
                                        secondFlag = false;
                                    }
                                    break;
                                case "5":
                                    System.out.println("You have successfully logged out!");
                                    secondFlag = false;
                                case "0":
                                    secondFlag = false;
                                    flag = false;
                            }
                        }
                    } else {
                        System.out.println("Wrong card number or PIN!");
                    }

                    break;


                case "0":
                    flag = false;
                    break;

            }
        }
        System.out.println("\nBye!");
    }

    private void createNewCard(Card card) {
        db.insert(card.getId(),card.getCardNumber(),card.getPin());
    }

    private int getBalance(int id) {
        try (ResultSet rs = db.query("SELECT balance FROM card WHERE id="+id)){
           return rs.getInt("balance");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private boolean addIncome(int income, int id) {
       return db.updateBalance(income,id);
    }

    private boolean checkLuhn(String number) {
        int[] numberArray = new int[number.length()];

        for(int i = 0; i < number.length(); i++) {
            char c = number.charAt(i);
            numberArray[i] = Character.getNumericValue(c);
        }

        for (int i = numberArray.length - 2 ; i>=0;i-=2) {
            int num = numberArray[i] * 2;
            if(num > 9) {
                num = num % 10 + num / 10;
            }
            numberArray[i] = num;
        }
        int sum = Arrays.stream(numberArray).sum();

        return sum % 10 ==0;
    }

    private void printMenu() {
        System.out.println("1. Create an account\n" +
                "2. Log into account\n" +
                "0. Exit");
    }

    private void printSubMenu() {
        System.out.println("\n1. Balance\n" +
                "2. Add income\n" +
                "3. Do transfer\n" +
                "4. Close account\n" +
                "5. Log out\n" +
                "0. Exit");
    }
}
