
public class Book {

    private String title;          // ชื่อหนังสือ
    private int quantity;          // จำนวนหนังสือทั้งหมดที่มีในระบบ (สต็อก)
    private int borrowedCount;     // จำนวนหนังสือที่ถูกยืมออกไปในขณะนี้
    private String location;       // ที่อยู่/ตำแหน่งจัดเก็บหนังสือ เช่น ชั้นวาง โซน

    /**
     * Constructor สำหรับสร้างหนังสือใหม่เข้าสู่ระบบ
     * ค่าเริ่มต้น: borrowedCount = 0 (ยังไม่มีการยืม)
     */
    public Book(String title, int quantity, String location) {
        this.title = title;
        this.quantity = quantity;
        this.borrowedCount = 0;
        this.location = location;
    }

    // ---------- Getter / Setter ----------

    public String getTitle() {
        return title;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void increaseStock(int amount) {
        // ใช้ตอน "บวกสต็อกเพิ่ม" กรณีหนังสือมีอยู่ในระบบแล้ว
        this.quantity += amount;
    }

    public int getBorrowedCount() {
        return borrowedCount;
    }

    /**
     * ตรวจสอบว่า "หนังสือมีการยืมอยู่หรือไม่"
     * ใช้ในขั้นตอนการลบหนังสือตาม Flow chart
     */
    public boolean isBorrowed() {
        return borrowedCount > 0;
    }

    /**
     * บันทึกการยืมหนังสือ 1 เล่ม (สำหรับจำลอง/ทดสอบเงื่อนไข "มีการยืมอยู่หรือไม่")
     * คืนค่า false หากไม่มีสำเนาว่างให้ยืม
     */
    public boolean borrowCopy() {
        if (borrowedCount >= quantity) {
            return false;
        }
        borrowedCount++;
        return true;
    }

    /**
     * บันทึกการคืนหนังสือ 1 เล่ม
     */
    public boolean returnCopy() {
        if (borrowedCount <= 0) {
            return false;
        }
        borrowedCount--;
        return true;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return String.format(
                "ชื่อหนังสือ: %s | จำนวนคงเหลือ: %d | กำลังถูกยืม: %d | ที่อยู่: %s",
                title, quantity, borrowedCount, location
        );
    }
}
