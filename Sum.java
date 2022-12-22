public class Sum {
    public static void main(String[] args) {
        int sum = 0;
        for (String arg : args) {
            int shift = 0;
            for (int j = 0; j < arg.length(); j++) {
                if (Character.isWhitespace(arg.charAt(j))) {
                    if (!arg.substring(shift, j).equals("")) {
                        sum += Integer.parseInt(arg.substring(shift, j));
                        shift = j + 1;
                    } else if (arg.length() == 1) {
                        sum += Integer.parseInt(String.valueOf(arg.charAt(j)));
                        shift = j + 1;
                    } else {
                        shift = j + 1;
                    }
                }
            }
        }
        System.out.println(sum);
    }
}
