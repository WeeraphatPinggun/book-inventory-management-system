import java.util.HashMap;
import java.util.Map;

/**
 * คลาส BookInventory
 * เป็นแกนหลัก (Core Logic) ของระบบจัดการคลังหนังสือ
 * รับผิดชอบการ "เพิ่ม", "ลบ", "ค้นหา" หนังสือ ตามลำดับการทำงานใน Flow chart
 *
 * ใช้ HashMap<String, Book> เพื่อให้ค้นหาหนังสือด้วยชื่อได้อย่างรวดเร็ว O(1)
 */
public class BookInventory {

    // เก็บหนังสือทั้งหมดในระบบ โดยใช้ "ชื่อหนังสือ" เป็น key
    private final Map<String, Book> books = new HashMap<>();

    /**
     * ผลลัพธ์ของการดำเนินการแต่ละครั้ง
     * ใช้สื่อสารสถานะกลับไปยังชั้น UI (Main) ตามข้อความใน Flow chart
     */
    public enum ResultStatus {
        SUCCESS,            // ดำเนินการสำเร็จ
        NOT_FOUND,          // ไม่พบหนังสือ / ไม่มีหนังสืออยู่ในระบบ
        ALREADY_BORROWED    // มีการยืมหนังสืออยู่ (ลบไม่ได้)
    }

    /**
     * ผลลัพธ์ที่ห่อ (wrap) สถานะ + ข้อมูลหนังสือ (ถ้ามี) + ข้อความแสดงผล
     */
    public static class OperationResult {
        public final ResultStatus status;
        public final Book book;
        public final String message;

        public OperationResult(ResultStatus status, Book book, String message) {
            this.status = status;
            this.book = book;
            this.message = message;
        }
    }

    // =========================================================
    // เมนู "เพิ่ม" (Add Book)
    // =========================================================
    /**
     * เพิ่มหนังสือเข้าสู่ระบบ
     * ตรรกะตาม Flow chart:
     *  1. รับชื่อหนังสือที่ต้องการเพิ่ม
     *  2. ตรวจสอบว่ามีหนังสือในระบบหรือไม่
     *     - ไม่มี  -> เพิ่มหนังสือเข้าไปในระบบใหม่
     *     - มีแล้ว -> บวกสต็อกเพิ่ม
     *  3. คืนผลลัพธ์ "เพิ่มหนังสือเรียบร้อย"
     */
    public OperationResult addBook(String title, int quantity, String location) {
        if (!books.containsKey(title)) {
            // ไม่มีหนังสือในระบบ -> เพิ่มหนังสือใหม่เข้าไปในระบบ
            Book newBook = new Book(title, quantity, location);
            books.put(title, newBook);
            return new OperationResult(ResultStatus.SUCCESS, newBook, "เพิ่มหนังสือเรียบร้อย (เพิ่มรายการใหม่)");
        } else {
            // มีหนังสืออยู่แล้ว -> บวกสต็อกเพิ่ม
            Book existingBook = books.get(title);
            existingBook.increaseStock(quantity);
            return new OperationResult(ResultStatus.SUCCESS, existingBook, "เพิ่มหนังสือเรียบร้อย (บวกสต็อกเพิ่ม)");
        }
    }

    // =========================================================
    // เมนู "ลบ" (Delete Book)
    // =========================================================
    /**
     * ลบหนังสือออกจากระบบ
     * ตรรกะตาม Flow chart:
     *  1. รับชื่อหนังสือที่ต้องการลบ
     *  2. ตรวจสอบว่ามีหนังสือในระบบหรือไม่
     *     - ไม่มี -> "ไม่พบหนังสือ"
     *     - มี    -> ตรวจสอบว่าหนังสือมีการยืมอยู่หรือไม่
     *         - มีการยืมอยู่    -> "มีการยืมหนังสืออยู่" (ลบไม่ได้)
     *         - ไม่มีการยืมอยู่ -> ลบหนังสือ -> "ลบหนังสือเรียบร้อย"
     */
    public OperationResult deleteBook(String title) {
        Book book = books.get(title);

        if (book == null) {
            // ไม่พบหนังสือในระบบ
            return new OperationResult(ResultStatus.NOT_FOUND, null, "ไม่พบหนังสือ");
        }

        if (book.isBorrowed()) {
            // หนังสือเล่มนี้มีการยืมอยู่ -> ไม่สามารถลบได้
            return new OperationResult(ResultStatus.ALREADY_BORROWED, book, "มีการยืมหนังสืออยู่ ไม่สามารถลบได้");
        }

        // ไม่มีการยืม -> ลบหนังสือออกจากระบบได้เลย
        books.remove(title);
        return new OperationResult(ResultStatus.SUCCESS, book, "ลบหนังสือเรียบร้อย");
    }

    // =========================================================
    // เมนู "ค้นหา" (Search Book)
    // =========================================================
    /**
     * ค้นหาหนังสือในระบบ
     * ตรรกะตาม Flow chart:
     *  1. รับชื่อหนังสือที่ต้องการค้นหา
     *  2. ตรวจสอบว่ามีหนังสือในระบบหรือไม่
     *     - ไม่มี -> "ไม่มีหนังสืออยู่ในระบบ"
     *     - มี    -> แสดง "ที่อยู่ของหนังสือ"
     */
    public OperationResult searchBook(String title) {
        Book book = books.get(title);

        if (book == null) {
            return new OperationResult(ResultStatus.NOT_FOUND, null, "ไม่มีหนังสืออยู่ในระบบ");
        }

        return new OperationResult(ResultStatus.SUCCESS, book,
                "ที่อยู่ของหนังสือ: " + book.getLocation());
    }

    // เมธอดเสริม: ใช้จำลองการยืมหนังสือ เพื่อทดสอบเงื่อนไข "มีการยืมหนังสืออยู่"
    // (ไม่ได้อยู่ใน Flow chart หลัก แต่จำเป็นสำหรับสาธิต/ทดสอบเคส "หนังสือมีการยืมอยู่หรือไม่")
    public boolean borrowBook(String title) {
        Book book = books.get(title);
        if (book == null) {
            return false;
        }
        return book.borrowCopy();
    }

    // เมธอดเสริม: จำลองการคืนหนังสือ
    public boolean returnBook(String title) {
        Book book = books.get(title);
        if (book == null) {
            return false;
        }
        return book.returnCopy();
    }
}
