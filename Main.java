import java.util.Scanner;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final BookInventory inventory = new BookInventory();

    public static void main(String[] args) {

        // ---------- เริ่มต้น ----------
        System.out.println("=== ระบบจัดการคลังหนังสือ (Book Inventory Management System) ===");

        // ข้อมูลตัวอย่างเริ่มต้น เพื่อให้ทดสอบเมนู "ลบ" และ "ค้นหา" ได้ทันที
        seedSampleData();

        boolean continueProgram = true;

        // ลูปหลักของโปรแกรม จะวนซ้ำจนกว่าผู้ใช้เลือก "ไม่" ที่ขั้นตอน "ต้องการทำต่อหรือไม่"
        while (continueProgram) {

            // ---------- เมนูหลัก ----------
            int choice = showMainMenu();

            switch (choice) {
                case 1 -> handleDeleteFlow();   // ลบ
                case 2 -> handleAddFlow();      // เพิ่ม
                case 3 -> handleSearchFlow();   // ค้นหา
                default -> System.out.println("กรุณาเลือกเมนูให้ถูกต้อง (1-3)");
            }

            // ---------- ต้องการทำต่อหรือไม่ ----------
            if (choice >= 1 && choice <= 3) {
                continueProgram = askContinue();
            }
        }

        // ---------- สิ้นสุด ----------
        System.out.println("\n=== สิ้นสุดการทำงานของโปรแกรม ===");
        scanner.close();
    }

    /**
     * แสดง "เมนูหลัก" และรับตัวเลือกจากผู้ใช้
     */
    private static int showMainMenu() {
        System.out.println("\n----- เมนูหลัก -----");
        System.out.println("1. ลบ");
        System.out.println("2. เพิ่ม");
        System.out.println("3. ค้นหา");
        System.out.print("เลือกเมนู: ");

        int choice;
        try {
            choice = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            choice = -1; // ค่าที่ไม่ตรงกับเมนูใด ๆ
        }
        return choice;
    }

    // =========================================================
    // Flow: ลบ (Delete)
    // =========================================================
    private static void handleDeleteFlow() {
        System.out.print("ชื่อหนังสือที่ต้องการลบ: ");
        String title = scanner.nextLine().trim();

        BookInventory.OperationResult result = inventory.deleteBook(title);

        // แสดงผลตามสถานะที่ได้ (ไม่พบหนังสือ / มีการยืมหนังสืออยู่ / ลบหนังสือเรียบร้อย)
        System.out.println(result.message);
    }

    // =========================================================
    // Flow: เพิ่ม (Add)
    // =========================================================
    private static void handleAddFlow() {
        System.out.print("ชื่อหนังสือที่ต้องการเพิ่ม: ");
        String title = scanner.nextLine().trim();

        int quantity = readPositiveInt("จำนวนที่ต้องการเพิ่ม: ");

        System.out.print("ที่อยู่ของหนังสือ (ใช้กรณีเพิ่มหนังสือใหม่): ");
        String location = scanner.nextLine().trim();

        BookInventory.OperationResult result = inventory.addBook(title, quantity, location);
        System.out.println(result.message);
    }

    // =========================================================
    // Flow: ค้นหา (Search)
    // =========================================================
    private static void handleSearchFlow() {
        System.out.print("ชื่อหนังสือที่ต้องการค้นหา: ");
        String title = scanner.nextLine().trim();

        BookInventory.OperationResult result = inventory.searchBook(title);
        System.out.println(result.message);
    }

    /**
     * ถามผู้ใช้ว่า "ต้องการทำต่อหรือไม่"
     * คืนค่า true ถ้าตอบ "ใช่" (วนกลับไปที่เมนูหลัก)
     * คืนค่า false ถ้าตอบ "ไม่" (ไปที่ สิ้นสุด)
     */
    private static boolean askContinue() {
        while (true) {
            System.out.print("\nต้องการทำต่อหรือไม่ (y = ใช่ / n = ไม่): ");
            String input = scanner.nextLine().trim().toLowerCase();

            if (input.equals("y") || input.equals("ใช่")) {
                return true;
            } else if (input.equals("n") || input.equals("ไม่")) {
                return false;
            } else {
                System.out.println("กรุณากรอก y หรือ n เท่านั้น");
            }
        }
    }

    /**
     * รับค่าจำนวนเต็มบวกจากผู้ใช้ พร้อมตรวจสอบความถูกต้องของ input
     */
    private static int readPositiveInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                int value = Integer.parseInt(scanner.nextLine().trim());
                if (value > 0) {
                    return value;
                }
                System.out.println("กรุณากรอกจำนวนที่มากกว่า 0");
            } catch (NumberFormatException e) {
                System.out.println("กรุณากรอกตัวเลขเท่านั้น");
            }
        }
    }

    /**
     * เพิ่มข้อมูลตัวอย่าง เพื่อให้สามารถทดสอบเมนู "ลบ" และ "ค้นหา" ได้ทันทีโดยไม่ต้องเพิ่มก่อน
     */
    private static void seedSampleData() {
        inventory.addBook("Clean Code", 5, "ชั้น A1");
        inventory.addBook("Effective Java", 3, "ชั้น A2");

        // จำลองว่า "Effective Java" กำลังถูกยืมอยู่ 1 เล่ม
        // เพื่อสาธิตเงื่อนไข "หนังสือมีการยืมอยู่หรือไม่" ตอนลบ
        inventory.borrowBook("Effective Java");
    }
}
