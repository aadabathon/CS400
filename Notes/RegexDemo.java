import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexDemo {

    public static void main(String[] args) {

        // testing a string
        String text1 = "firstname.lastname@cs.wisc.edu";
        Pattern emailPattern = Pattern.compile("[\\w\\.]+@[\\w\\.]+\\.\\w\\w\\w?");
        Matcher emailMatcher = emailPattern.matcher(text1);
        System.out.println("Is valid email: " + emailMatcher.matches());
        
        // shorthand for testing a string
        System.out.println("Is valid email short: " + Pattern.matches("[\\w\\.]@[\\w\\.]+\\.\\w\\w\\w?", text1));

        // extracting multiple patterns from a long string
        String text2 = "This is a line of text\n" +
                        "And here is an email address alskdjf@wisc.edu\n" +
                        "Here is an email address sls345@wisc.edu\n" +
                        "Another line of text\n" +
                        "some more emails: zlxcvn3@wisc.edu zlkxcjh45@wisc.edu alsdkjfds@wisc.edu\n" +
                        "and another line";
        
        Matcher text2Matcher = emailPattern.matcher(text2);
        while (text2Matcher.find()) {
            String email = text2Matcher.group();
            int start = text2Matcher.start();
            int end = text2Matcher.end();
            System.out.println(email + " at position " + start + " to " + end);
        }
    }

}
