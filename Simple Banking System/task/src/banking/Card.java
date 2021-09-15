package banking;

import java.util.Random;

public class Card {
//    private static int incrementID = 1;
    private final int id;
    private final String bin;
    private final String accountNumber;
    private final int checkDigit;
    private final String pin;


    public Card(int lastID) {
        this.bin = "400000";

        this.accountNumber = createAccountNumber();
        this.checkDigit = createCheckDigit();
        this.pin = createPin();
        this.id = lastID + 1;
    }

    public int getId() {
        return id;
    }

    public String getCardNumber() {
        StringBuilder sb = new StringBuilder();
        sb.append(bin);
        sb.append(accountNumber);
        sb.append(checkDigit);
        return sb.toString();
    }

    public String getPin() {
        return pin;
    }

    private String createAccountNumber() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        while (sb.length() < 9 ) {
            sb.append(random.nextInt(10));
        }

        return sb.toString();
    }

    private int createCheckDigit() {

        String fullNumber = bin + accountNumber;
        int sum = 0;


        for (int i = 1; i <= fullNumber.length(); i++) {
            int number = Character.getNumericValue(fullNumber.charAt(i - 1));
            if (i % 2 == 1) {
                number *= 2;
            }

            if (number > 9) {
                number -= 9;
            }


            sum += number;
        }

        return sum % 10 == 0 ? 0 : 10 - (sum % 10) ;

    }

    private String createPin() {
        Random random = new Random();
        String pinString = "";
        while (pinString.length() < 4) {
            pinString += Integer.toString(random.nextInt(10));
        }

        return pinString;
    }


}
