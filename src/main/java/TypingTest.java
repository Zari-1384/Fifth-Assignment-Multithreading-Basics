import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.util.stream.Collectors;
import java.io.IOException;

public class TypingTest {

    private static int AllTime;
    private static int correct_counter;
    private static String lastInput = "";
    private static Scanner scanner = new Scanner(System.in);
    private static boolean stop = false;
    public static class InputRunnable implements Runnable {

        //TODO: Implement a thread to get user input without blocking the main thread
        @Override
        public void run()
        {
            while(!stop)
            {
                lastInput = scanner.nextLine();
            }
        }
    }


    public static void testWord(String wordToTest)
    {
        try {
            int timer = wordToTest.length() * 1000;
            System.out.println(wordToTest);
            lastInput = "";
            long startTime = System.currentTimeMillis();

            while(System.currentTimeMillis() - startTime < timer)
            {
                if(!lastInput.isEmpty())
                {
                    AllTime+= (int) (System.currentTimeMillis() - startTime);
                    // سیستم بالا به این دلیل پیاده‌سازی شد که در نهلیت بتوانیم کل تایم سپری شده را حساب کنیم
                    break;
                }

                AllTime+= (int) (System.currentTimeMillis() - startTime);
                Thread.sleep(50); //برای کاهش مصرف انزی و cpu

            }
            // TODO

            System.out.println();
            System.out.println("You typed: " + lastInput);
            if (lastInput.equals(wordToTest)) {
                System.out.println("Correct\n");
                correct_counter++;
            } else {
                System.out.println("Incorrect\n");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void typingTest(List<String> inputList) throws InterruptedException {

        for (int i = 0; i < inputList.size(); i++) {
            String wordToTest = inputList.get(i);
            testWord(wordToTest);
            Thread.sleep(2000); // Pause briefly before showing the next word
        }

        // TODO: Display a summary of test results
    }

    public static void main(String[] args) throws InterruptedException
    {
        List<String> words = new ArrayList<>();

        try (InputStream inputStream = TypingTest.class.getClassLoader().getResourceAsStream("Words.txt");
                // در خط کد بالا در مسیر src/main/resources دنبال فایل Word.txt میگردد
                // در خط کد پایین داخل پرانتز داده های باینری را به داده های متنی تبدیل میکند
                // در بخش buffer reader یک خواننده خط به خط است
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream)))
        {

            words = reader.lines().collect(Collectors.toList());
            // در بخش reader lines همه خطوط فایل را به صورت یک جریان ازرشته ها میدهد
            //در این بخش collect(Collectors.toList()) این جریان را به یک لیست از رشته ها تبدیل میکند

        } catch (IOException e) {
            System.out.println("error in reading file : " + e.getMessage());
            return; // اجرای برنامه متوقف می‌شود چون فایل مورد نیاز بارگذاری نشده
        } catch (Exception e) {
            System.out.println(" unexpected error : " + e.getMessage());
            return;
        }


        Thread inputThread = new Thread(new InputRunnable());
        inputThread.start();
        typingTest(words);
        stop = true;

        System.out.println("Correct answers : " + correct_counter);
        System.out.println("inCorrect answers : " + (words.size() - correct_counter));
        System.out.println("Average response time : " + AllTime / words.size() + " milliseconds");
        System.out.println("you finished the test in " + AllTime + " milliseconds");


        //TODO: Replace the hardcoded word list with words read from the given file in the resources folder (Words.txt)
    }
}