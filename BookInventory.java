
public class BookInventory {

    // เก็บหนังสือทั้งหมดในระบบด้วย array
    private Book[] books;

    // จำนวนหนังสือที่ถูกใช้งานจริงใน array ณ ขณะนี้ (ไม่ใช่ books.length)
    private int size;

    // ขนาดเริ่มต้นของ array ตอนสร้าง object ครั้งแรก
    private static final int DEFAULT_CAPACITY = 10;

    public BookInventory() {
        books = new Book[DEFAULT_CAPACITY];
        size = 0;
    }

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
    // เมธอดช่วยเหลือ: ค้นหา "ตำแหน่ง index" ของหนังสือใน array ด้วยชื่อ
    // ใช้ Linear Search เพราะ array ไม่มี key แบบ HashMap
    // คืนค่า -1 ถ้าไม่พบ
    // =========================================================
    private int findIndexByTitle(String title) {
        for (int i = 0; i < size; i++) {
            if (books[i].getTitle().equals(title)) {
                return i;
            }
        }
        return -1;
    }

    // =========================================================
    // เมธอดช่วยเหลือ: ขยายขนาด array เมื่อของเต็ม
    // สร้าง array ใหม่ขนาดเป็น 2 เท่า แล้วคัดลอกข้อมูลเดิมทั้งหมดไปใส่
    // =========================================================
    private void resizeIfFull() {
        if (size == books.length) {
            Book[] newBooks = new Book[books.length * 2];
            for (int i = 0; i < books.length; i++) {
                newBooks[i] = books[i];
            }
            books = newBooks;
        }
    }

    // =========================================================
    // เมนู "เพิ่ม" (Add Book)
    // =========================================================
    /**
     * เพิ่มหนังสือเข้าสู่ระบบ
     * ตรรกะตาม Flow chart:
     *  1. รับชื่อหนังสือที่ต้องการเพิ่ม
     *  2. ตรวจสอบว่ามีหนังสือในระบบหรือไม่ (ค้นหาด้วย Linear Search ใน array)
     *     - ไม่มี  -> เพิ่มหนังสือเข้าไปในระบบใหม่ (ต่อท้าย array)
     *     - มีแล้ว -> บวกสต็อกเพิ่ม
     *  3. คืนผลลัพธ์ "เพิ่มหนังสือเรียบร้อย"
     */
    public OperationResult addBook(String title, int quantity, String location) {
        int index = findIndexByTitle(title);

        if (index == -1) {
            // ไม่มีหนังสือในระบบ -> เพิ่มหนังสือใหม่ต่อท้าย array
            resizeIfFull(); // ขยาย array ก่อนถ้าของเต็มแล้ว
            Book newBook = new Book(title, quantity, location);
            books[size] = newBook;
            size++;
            return new OperationResult(ResultStatus.SUCCESS, newBook, "เพิ่มหนังสือเรียบร้อย (เพิ่มรายการใหม่)");
        } else {
            // มีหนังสืออยู่แล้ว -> บวกสต็อกเพิ่ม
            Book existingBook = books[index];
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
     *  2. ตรวจสอบว่ามีหนังสือในระบบหรือไม่ (ค้นหาด้วย Linear Search)
     *     - ไม่มี -> "ไม่พบหนังสือ"
     *     - มี    -> ตรวจสอบว่าหนังสือมีการยืมอยู่หรือไม่
     *         - มีการยืมอยู่    -> "มีการยืมหนังสืออยู่" (ลบไม่ได้)
     *         - ไม่มีการยืมอยู่ -> ลบหนังสือออกจาก array -> "ลบหนังสือเรียบร้อย"
     */
    public OperationResult deleteBook(String title) {
        int index = findIndexByTitle(title);

        if (index == -1) {
            // ไม่พบหนังสือในระบบ
            return new OperationResult(ResultStatus.NOT_FOUND, null, "ไม่พบหนังสือ");
        }

        Book book = books[index];

        if (book.isBorrowed()) {
            // หนังสือเล่มนี้มีการยืมอยู่ -> ไม่สามารถลบได้
            return new OperationResult(ResultStatus.ALREADY_BORROWED, book, "มีการยืมหนังสืออยู่ ไม่สามารถลบได้");
        }

        // ไม่มีการยืม -> ลบหนังสือออกจาก array
        // เนื่องจาก array ไม่มีเมธอด remove() แบบ HashMap จึงต้อง "เลื่อน" สมาชิกที่อยู่หลัง index
        // มาเติมช่องว่างทีละตัว เพื่อไม่ให้เกิดช่องว่าง (null) กลาง array
        for (int i = index; i < size - 1; i++) {
            books[i] = books[i + 1];
        }
        books[size - 1] = null; // เคลียร์ช่องสุดท้ายที่ไม่ใช้แล้ว
        size--;

        return new OperationResult(ResultStatus.SUCCESS, book, "ลบหนังสือเรียบร้อย");
    }

    // =========================================================
    // เมนู "ค้นหา" (Search Book)
    // =========================================================
    /**
     * ค้นหาหนังสือในระบบ
     * ตรรกะตาม Flow chart:
     *  1. รับชื่อหนังสือที่ต้องการค้นหา
     *  2. ตรวจสอบว่ามีหนังสือในระบบหรือไม่ (Linear Search ใน array)
     *     - ไม่มี -> "ไม่มีหนังสืออยู่ในระบบ"
     *     - มี    -> แสดง "ที่อยู่ของหนังสือ"
     */
    public OperationResult searchBook(String title) {
        int index = findIndexByTitle(title);

        if (index == -1) {
            return new OperationResult(ResultStatus.NOT_FOUND, null, "ไม่มีหนังสืออยู่ในระบบ");
        }

        Book book = books[index];
        return new OperationResult(ResultStatus.SUCCESS, book,
                "ที่อยู่ของหนังสือ: " + book.getLocation());
    }

    // เมธอดเสริม: ใช้จำลองการยืมหนังสือ เพื่อทดสอบเงื่อนไข "มีการยืมหนังสืออยู่"
    // (ไม่ได้อยู่ใน Flow chart หลัก แต่จำเป็นสำหรับสาธิต/ทดสอบเคส "หนังสือมีการยืมอยู่หรือไม่")
    public boolean borrowBook(String title) {
        int index = findIndexByTitle(title);
        if (index == -1) {
            return false;
        }
        return books[index].borrowCopy();
    }

    // เมธอดเสริม: จำลองการคืนหนังสือ
    public boolean returnBook(String title) {
        int index = findIndexByTitle(title);
        if (index == -1) {
            return false;
        }
        return books[index].returnCopy();
    }

    /**
     * เมธอดเสริม: คืนจำนวนหนังสือทั้งหมดที่มีอยู่ในระบบขณะนี้
     * มีประโยชน์เวลาต้องการวนลูปแสดงรายการหนังสือทั้งหมด
     */
    public int getSize() {
        return size;
    }

    /**
     * เมธอดเสริม: คืนหนังสือที่ตำแหน่ง index (สำหรับวนลูปแสดงรายการทั้งหมด)
     */
    public Book getBookAt(int index) {
        if (index < 0 || index >= size) {
            return null;
        }
        return books[index];
    }
}
