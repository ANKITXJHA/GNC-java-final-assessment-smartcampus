// Name: Ankit Kumar Jha
// Section: C | Branch: CSE AIML
// College: GNC College | BTech 2nd Year
// Assessment: Final Java Assessment - SmartCampus System

import java.util.*;
import java.io.*;

// ==================== CUSTOM EXCEPTION ====================
class InvalidFeeException extends Exception {
    public InvalidFeeException(String msg) {
        super(msg);
    }
}

class StudentNotFoundException extends Exception {
    public StudentNotFoundException(String msg) {
        super(msg);
    }
}

// ==================== INTERFACE ====================
interface Billable {
    double calculateFee();
}

// ==================== STUDENT CLASS ====================
class Student {
    private int studentId;
    private String studentName;
    private String studentEmail;

    public Student(int studentId, String studentName, String studentEmail) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.studentEmail = studentEmail;
    }

    public int getStudentId()     { return studentId; }
    public String getStudentName()  { return studentName; }
    public String getStudentEmail() { return studentEmail; }

    @Override
    public String toString() {
        return "[ID: " + studentId + "] " + studentName + " | " + studentEmail;
    }
}

// ==================== COURSE CLASS ====================
class Course implements Billable {
    private int courseId;
    private String courseTitle;
    private double courseFee;

    public Course(int courseId, String courseTitle, double courseFee) throws InvalidFeeException {
        if (courseFee < 0) {
            throw new InvalidFeeException("Fee cannot be negative! Entered: " + courseFee);
        }
        this.courseId = courseId;
        this.courseTitle = courseTitle;
        this.courseFee = courseFee;
    }

    public int getCourseId()       { return courseId; }
    public String getCourseTitle() { return courseTitle; }

    @Override
    public double calculateFee() { return courseFee; }

    @Override
    public String toString() {
        return "[ID: " + courseId + "] " + courseTitle + " | Fee: Rs." + courseFee;
    }
}

// ==================== ENROLLMENT PROCESSOR (THREAD) ====================
class EnrollmentProcessor implements Runnable {
    private String studentName;
    private String courseName;

    public EnrollmentProcessor(String studentName, String courseName) {
        this.studentName = studentName;
        this.courseName = courseName;
    }

    @Override
    public void run() {
        try {
            System.out.println("\n[Thread] Processing enrollment for " + studentName + " -> " + courseName + "...");
            Thread.sleep(1500); // Simulate processing delay
            System.out.println("[Thread] Enrollment CONFIRMED: " + studentName + " enrolled in " + courseName);
        } catch (InterruptedException e) {
            System.out.println("[Thread] Enrollment processing interrupted!");
        }
    }
}

// ==================== MAIN SYSTEM CLASS ====================
public class AN_SmartCampus {

    // Collections
    static HashMap<Integer, Student> studentMap = new HashMap<>();
    static HashMap<Integer, Course> courseMap = new HashMap<>();
    static HashMap<Student, ArrayList<Course>> enrollmentMap = new HashMap<>();

    static Scanner sc = new Scanner(System.in);

    // ---- Add Student ----
    static void addStudent() {
        try {
            System.out.print("Enter Student ID   : ");
            int sid = Integer.parseInt(sc.nextLine().trim());
            if (studentMap.containsKey(sid)) {
                System.out.println("Student ID already exists!");
                return;
            }
            System.out.print("Enter Student Name : ");
            String sname = sc.nextLine().trim();
            System.out.print("Enter Student Email: ");
            String semail = sc.nextLine().trim();

            Student s = new Student(sid, sname, semail);
            studentMap.put(sid, s);
            enrollmentMap.put(s, new ArrayList<>());
            System.out.println("Student added: " + s);
        } catch (NumberFormatException e) {
            System.out.println("Invalid input! Student ID must be a number.");
        }
    }

    // ---- Add Course ----
    static void addCourse() {
        try {
            System.out.print("Enter Course ID  : ");
            int cid = Integer.parseInt(sc.nextLine().trim());
            if (courseMap.containsKey(cid)) {
                System.out.println("Course ID already exists!");
                return;
            }
            System.out.print("Enter Course Name: ");
            String cname = sc.nextLine().trim();
            System.out.print("Enter Course Fee : ");
            double fee = Double.parseDouble(sc.nextLine().trim());

            Course c = new Course(cid, cname, fee);
            courseMap.put(cid, c);
            System.out.println("Course added: " + c);
        } catch (NumberFormatException e) {
            System.out.println("Invalid input! IDs and fees must be numbers.");
        } catch (InvalidFeeException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // ---- Enroll Student ----
    static void enrollStudent() {
        try {
            System.out.print("Enter Student ID: ");
            int sid = Integer.parseInt(sc.nextLine().trim());
            System.out.print("Enter Course ID : ");
            int cid = Integer.parseInt(sc.nextLine().trim());

            Student student = studentMap.get(sid);
            Course course   = courseMap.get(cid);

            if (student == null) throw new StudentNotFoundException("No student with ID: " + sid);
            if (course == null)  throw new StudentNotFoundException("No course with ID: " + cid);

            ArrayList<Course> courses = enrollmentMap.get(student);
            for (Course c : courses) {
                if (c.getCourseId() == cid) {
                    System.out.println("Student already enrolled in this course!");
                    return;
                }
            }
            courses.add(course);
            System.out.println("Enrolled " + student.getStudentName() + " in " + course.getCourseTitle());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input! IDs must be numbers.");
        } catch (StudentNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // ---- View Students ----
    static void viewStudents() {
        if (studentMap.isEmpty()) {
            System.out.println("No students found.");
            return;
        }
        System.out.println("\n--- Student List ---");
        for (Student s : studentMap.values()) {
            System.out.println(s);
        }
    }

    // ---- View Enrollments ----
    static void viewEnrollments() {
        if (enrollmentMap.isEmpty()) {
            System.out.println("No enrollments found.");
            return;
        }
        System.out.println("\n--- Enrollment Details ---");
        for (Map.Entry<Student, ArrayList<Course>> entry : enrollmentMap.entrySet()) {
            Student s = entry.getKey();
            ArrayList<Course> courses = entry.getValue();
            System.out.println(s.getStudentName() + " -> " + (courses.isEmpty() ? "No courses enrolled" : ""));
            for (Course c : courses) {
                System.out.println("    " + c);
            }
        }
    }

    // ---- Process Enrollment (Thread) ----
    static void processEnrollment() {
        try {
            System.out.print("Enter Student ID to process: ");
            int sid = Integer.parseInt(sc.nextLine().trim());
            System.out.print("Enter Course ID  to process: ");
            int cid = Integer.parseInt(sc.nextLine().trim());

            Student s = studentMap.get(sid);
            Course  c = courseMap.get(cid);

            if (s == null || c == null) {
                System.out.println("Invalid student or course ID.");
                return;
            }

            Thread t = new Thread(new EnrollmentProcessor(s.getStudentName(), c.getCourseTitle()));
            t.start();
        } catch (NumberFormatException e) {
            System.out.println("Invalid input! IDs must be numbers.");
        }
    }

    // ---- BONUS: Save Data to File ----
    static void saveToFile() {
        try (PrintWriter pw = new PrintWriter(new FileWriter("campus_data.txt"))) {
            pw.println("=== SmartCampus Data ===");
            pw.println("-- Students --");
            for (Student s : studentMap.values()) pw.println(s);
            pw.println("-- Courses --");
            for (Course c : courseMap.values()) pw.println(c);
            pw.println("-- Enrollments --");
            for (Map.Entry<Student, ArrayList<Course>> e : enrollmentMap.entrySet()) {
                for (Course c : e.getValue()) {
                    pw.println(e.getKey().getStudentName() + " -> " + c.getCourseTitle());
                }
            }
            System.out.println("Data saved to campus_data.txt");
        } catch (IOException e) {
            System.out.println("File error: " + e.getMessage());
        }
    }

    // ==================== MAIN MENU ====================
    public static void main(String[] args) {
        System.out.println("============================================");
        System.out.println("   SmartCampus Management System");
        System.out.println("   By: Ankit Kumar Jha | Section C | AIML");
        System.out.println("============================================");

        while (true) {
            System.out.println("\n--- MENU ---");
            System.out.println("1. Add Student");
            System.out.println("2. Add Course");
            System.out.println("3. Enroll Student");
            System.out.println("4. View Students");
            System.out.println("5. View Enrollments");
            System.out.println("6. Process Enrollment (Thread)");
            System.out.println("7. Save Data to File (Bonus)");
            System.out.println("8. Exit");
            System.out.print("Choose option: ");

            try {
                int choice = Integer.parseInt(sc.nextLine().trim());
                switch (choice) {
                    case 1: addStudent();       break;
                    case 2: addCourse();        break;
                    case 3: enrollStudent();    break;
                    case 4: viewStudents();     break;
                    case 5: viewEnrollments();  break;
                    case 6: processEnrollment(); break;
                    case 7: saveToFile();       break;
                    case 8:
                        System.out.println("Exiting SmartCampus. Goodbye, Ankit!");
                        sc.close();
                        System.exit(0);
                    default:
                        System.out.println("Invalid option! Choose between 1-8.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid menu number.");
            }
        }
    }
}
