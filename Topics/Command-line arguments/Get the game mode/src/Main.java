class Problem {
    public static void main(String[] args) {
        int found = -1;

        for (int i = 0; i < args.length; i += 2) {
            if (args[i].equals("mode")) {
                found = i + 1;
            }
        }

        System.out.println(found != -1 ? args[found] : "default");


    }
}