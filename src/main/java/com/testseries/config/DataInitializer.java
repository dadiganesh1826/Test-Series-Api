package com.testseries.config;

import com.testseries.model.Question;
import com.testseries.model.TestSeries;
import com.testseries.model.User;
import com.testseries.repository.QuestionRepository;
import com.testseries.repository.TestSeriesRepository;
import com.testseries.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);
    private final UserRepository userRepository;
    private final TestSeriesRepository testSeriesRepository;
    private final QuestionRepository questionRepository;

    @Override
    public void run(String... args) {
        // Check if data already exists to prevent duplicate initialization
        if (userRepository.count() > 0) {
            log.info("Data already initialized, skipping...");
            return;
        }

        log.info("Starting data initialization...");
        // Create sample users
        User user1 = new User();
        user1.setName("John Doe");
        user1.setEmail("john@example.com");
        user1.setPassword("password123");
        userRepository.save(user1);

        User user2 = new User();
        user2.setName("Jane Smith");
        user2.setEmail("jane@example.com");
        user2.setPassword("password123");
        userRepository.save(user2);

        // Create sample test series
        TestSeries javaTest = new TestSeries();
        javaTest.setTitle("Java Fundamentals Test");
        javaTest.setDescription("Test your knowledge of Java programming basics");
        javaTest.setDurationMinutes(30);
        javaTest.setTotalMarks(50);
        javaTest.setPassingMarks(25);
        javaTest.setIsActive(true);
        javaTest = testSeriesRepository.save(javaTest);

        // Add questions to Java test
        Question q1 = new Question();
        q1.setTestSeries(javaTest);
        q1.setQuestionText("What is the size of int in Java?");
        q1.setOptionA("8 bits");
        q1.setOptionB("16 bits");
        q1.setOptionC("32 bits");
        q1.setOptionD("64 bits");
        q1.setCorrectAnswer("C");
        q1.setMarks(5);
        q1.setExplanation("In Java, int is a 32-bit signed integer.");
        questionRepository.save(q1);

        Question q2 = new Question();
        q2.setTestSeries(javaTest);
        q2.setQuestionText("Which keyword is used to inherit a class in Java?");
        q2.setOptionA("implements");
        q2.setOptionB("extends");
        q2.setOptionC("inherits");
        q2.setOptionD("super");
        q2.setCorrectAnswer("B");
        q2.setMarks(5);
        q2.setExplanation("The 'extends' keyword is used for class inheritance in Java.");
        questionRepository.save(q2);

        Question q3 = new Question();
        q3.setTestSeries(javaTest);
        q3.setQuestionText("What is the default value of a boolean variable in Java?");
        q3.setOptionA("true");
        q3.setOptionB("false");
        q3.setOptionC("0");
        q3.setOptionD("null");
        q3.setCorrectAnswer("B");
        q3.setMarks(5);
        q3.setExplanation("The default value of a boolean variable is false.");
        questionRepository.save(q3);

        Question q4 = new Question();
        q4.setTestSeries(javaTest);
        q4.setQuestionText("Which of these is not a Java feature?");
        q4.setOptionA("Object-oriented");
        q4.setOptionB("Platform-independent");
        q4.setOptionC("Use of pointers");
        q4.setOptionD("Multithreaded");
        q4.setCorrectAnswer("C");
        q4.setMarks(5);
        q4.setExplanation("Java does not support pointers to ensure security and simplicity.");
        questionRepository.save(q4);

        Question q5 = new Question();
        q5.setTestSeries(javaTest);
        q5.setQuestionText("What is the parent class of all classes in Java?");
        q5.setOptionA("System");
        q5.setOptionB("Object");
        q5.setOptionC("Class");
        q5.setOptionD("Main");
        q5.setCorrectAnswer("B");
        q5.setMarks(5);
        q5.setExplanation("The Object class is the root of the class hierarchy in Java.");
        questionRepository.save(q5);

        Question q6 = new Question();
        q6.setTestSeries(javaTest);
        q6.setQuestionText("Which method is the entry point of a Java application?");
        q6.setOptionA("start()");
        q6.setOptionB("run()");
        q6.setOptionC("main()");
        q6.setOptionD("init()");
        q6.setCorrectAnswer("C");
        q6.setMarks(5);
        q6.setExplanation("The main() method is the entry point for any Java application.");
        questionRepository.save(q6);

        Question q7 = new Question();
        q7.setTestSeries(javaTest);
        q7.setQuestionText("What is encapsulation in Java?");
        q7.setOptionA("Hiding implementation details");
        q7.setOptionB("Inheriting properties");
        q7.setOptionC("Creating multiple methods");
        q7.setOptionD("Using interfaces");
        q7.setCorrectAnswer("A");
        q7.setMarks(5);
        q7.setExplanation("Encapsulation is the mechanism of hiding implementation details from users.");
        questionRepository.save(q7);

        Question q8 = new Question();
        q8.setTestSeries(javaTest);
        q8.setQuestionText("Which package contains the String class?");
        q8.setOptionA("java.util");
        q8.setOptionB("java.io");
        q8.setOptionC("java.lang");
        q8.setOptionD("java.net");
        q8.setCorrectAnswer("C");
        q8.setMarks(5);
        q8.setExplanation("The String class is in the java.lang package, which is imported by default.");
        questionRepository.save(q8);

        Question q9 = new Question();
        q9.setTestSeries(javaTest);
        q9.setQuestionText("What is the output of 10 / 3 in Java?");
        q9.setOptionA("3.33");
        q9.setOptionB("3");
        q9.setOptionC("3.0");
        q9.setOptionD("Error");
        q9.setCorrectAnswer("B");
        q9.setMarks(5);
        q9.setExplanation("Integer division in Java returns an integer result, truncating the decimal part.");
        questionRepository.save(q9);

        Question q10 = new Question();
        q10.setTestSeries(javaTest);
        q10.setQuestionText("Which keyword is used to prevent method overriding?");
        q10.setOptionA("static");
        q10.setOptionB("final");
        q10.setOptionC("abstract");
        q10.setOptionD("private");
        q10.setCorrectAnswer("B");
        q10.setMarks(5);
        q10.setExplanation("The final keyword prevents a method from being overridden in subclasses.");
        questionRepository.save(q10);

        // Create another test series
        TestSeries webTest = new TestSeries();
        webTest.setTitle("Web Development Basics");
        webTest.setDescription("Test your knowledge of HTML, CSS, and JavaScript");
        webTest.setDurationMinutes(25);
        webTest.setTotalMarks(40);
        webTest.setPassingMarks(20);
        webTest.setIsActive(true);
        webTest = testSeriesRepository.save(webTest);

        // Add questions to Web test
        Question wq1 = new Question();
        wq1.setTestSeries(webTest);
        wq1.setQuestionText("What does HTML stand for?");
        wq1.setOptionA("Hyper Text Markup Language");
        wq1.setOptionB("High Tech Modern Language");
        wq1.setOptionC("Home Tool Markup Language");
        wq1.setOptionD("Hyperlinks and Text Markup Language");
        wq1.setCorrectAnswer("A");
        wq1.setMarks(5);
        wq1.setExplanation("HTML stands for Hyper Text Markup Language.");
        questionRepository.save(wq1);

        Question wq2 = new Question();
        wq2.setTestSeries(webTest);
        wq2.setQuestionText("Which CSS property is used to change text color?");
        wq2.setOptionA("font-color");
        wq2.setOptionB("text-color");
        wq2.setOptionC("color");
        wq2.setOptionD("text-style");
        wq2.setCorrectAnswer("C");
        wq2.setMarks(5);
        wq2.setExplanation("The 'color' property is used to set the text color in CSS.");
        questionRepository.save(wq2);

        Question wq3 = new Question();
        wq3.setTestSeries(webTest);
        wq3.setQuestionText("What is the correct syntax for a JavaScript function?");
        wq3.setOptionA("function myFunction()");
        wq3.setOptionB("def myFunction()");
        wq3.setOptionC("func myFunction()");
        wq3.setOptionD("function:myFunction()");
        wq3.setCorrectAnswer("A");
        wq3.setMarks(5);
        wq3.setExplanation("JavaScript functions are declared using the 'function' keyword.");
        questionRepository.save(wq3);

        Question wq4 = new Question();
        wq4.setTestSeries(webTest);
        wq4.setQuestionText("Which HTML tag is used for creating a hyperlink?");
        wq4.setOptionA("<link>");
        wq4.setOptionB("<a>");
        wq4.setOptionC("<href>");
        wq4.setOptionD("<url>");
        wq4.setCorrectAnswer("B");
        wq4.setMarks(5);
        wq4.setExplanation("The <a> tag is used to create hyperlinks in HTML.");
        questionRepository.save(wq4);

        Question wq5 = new Question();
        wq5.setTestSeries(webTest);
        wq5.setQuestionText("What does CSS stand for?");
        wq5.setOptionA("Cascading Style Sheets");
        wq5.setOptionB("Computer Style Sheets");
        wq5.setOptionC("Creative Style Sheets");
        wq5.setOptionD("Colorful Style Sheets");
        wq5.setCorrectAnswer("A");
        wq5.setMarks(5);
        wq5.setExplanation("CSS stands for Cascading Style Sheets.");
        questionRepository.save(wq5);

        Question wq6 = new Question();
        wq6.setTestSeries(webTest);
        wq6.setQuestionText("Which JavaScript method is used to write into the HTML output?");
        wq6.setOptionA("document.write()");
        wq6.setOptionB("console.log()");
        wq6.setOptionC("window.alert()");
        wq6.setOptionD("print()");
        wq6.setCorrectAnswer("A");
        wq6.setMarks(5);
        wq6.setExplanation("document.write() is used to write directly to the HTML document.");
        questionRepository.save(wq6);

        Question wq7 = new Question();
        wq7.setTestSeries(webTest);
        wq7.setQuestionText("Which HTML attribute specifies an alternate text for an image?");
        wq7.setOptionA("title");
        wq7.setOptionB("alt");
        wq7.setOptionC("src");
        wq7.setOptionD("text");
        wq7.setCorrectAnswer("B");
        wq7.setMarks(5);
        wq7.setExplanation("The 'alt' attribute provides alternative text for images.");
        questionRepository.save(wq7);

        Question wq8 = new Question();
        wq8.setTestSeries(webTest);
        wq8.setQuestionText("How do you select an element with id 'demo' in CSS?");
        wq8.setOptionA(".demo");
        wq8.setOptionB("#demo");
        wq8.setOptionC("*demo");
        wq8.setOptionD("demo");
        wq8.setCorrectAnswer("B");
        wq8.setMarks(5);
        wq8.setExplanation("The # symbol is used to select elements by ID in CSS.");
        questionRepository.save(wq8);

        log.info("Sample data initialized successfully!");
        log.info("Created 2 users, 2 test series, and 18 questions");
    }
}
