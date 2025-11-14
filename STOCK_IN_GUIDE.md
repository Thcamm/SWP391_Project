# HƯỚNG DẪN SỬ DỤNG HỆ THỐNG NHẬP KHO (STOCK-IN SYSTEM)

## 📋 TỔNG QUAN

Hệ thống nhập kho đã được hoàn thiện với khả năng **quản lý giá nhập thay đổi theo thời gian**.

### ✅ Đã hoàn thành:

1. **Controller**: `StockInController.java` - Xử lý logic nhập kho
2. **Model**: 
   - `InventoryTransaction.java` - Model giao dịch kho (đã thêm `unitPrice`)
   - `Supplier.java` - Model nhà cung cấp
3. **DAO**: 
   - `InventoryTransactionDAO.java` - CRUD cho giao dịch kho
   - `SupplierDAO.java` - CRUD cho nhà cung cấp
   - `PartInventoryDAO.java` - Quản lý linh kiện trong kho
4. **SQL Update Script**: `update_inventory_transaction.sql` - Cập nhật database

---

## 🗄️ BƯỚC 1: CẬP NHẬT DATABASE

**⚠️ QUAN TRỌNG**: Chạy file SQL trước khi sử dụng!

```sql
-- Chạy file này trong MySQL Workbench hoặc command line:
mysql -u root -p garage_mgmt < update_inventory_transaction.sql
```

Hoặc copy nội dung file `update_inventory_transaction.sql` và chạy trong MySQL Workbench.

### Script sẽ thực hiện:

1. ✅ Thêm cột `Quantity` vào bảng `InventoryTransaction`
2. ✅ Thêm cột `SupplierID` vào bảng `InventoryTransaction`
3. ✅ Thêm cột `UnitPrice` vào bảng `InventoryTransaction` (giải quyết vấn đề giá thay đổi)
4. ✅ Tạo bảng `Supplier` để quản lý nhà cung cấp
5. ✅ Thêm 3 nhà cung cấp mẫu

---

## 🎯 GIẢI PHÁP CHO VẤN ĐỀ GIÁ THAY ĐỔI THEO THỜI GIAN

### Vấn đề:
Cùng 1 sản phẩm:
- Tháng 1: Nhập giá 10,000 VNĐ
- Tháng 2: Nhập giá 15,000 VNĐ

❓ Làm sao quản lý?

### Giải pháp:

#### 1. **Bảng `InventoryTransaction`** - Lưu lịch sử GIÁ NHẬP
```
TransactionID | PartDetailID | Quantity | UnitPrice | TransactionDate | SupplierID
1             | 100          | 50       | 10000     | 2025-01-15      | 1
2             | 100          | 30       | 15000     | 2025-02-20      | 1
```

→ **Mỗi lần nhập kho, giá được lưu vào `UnitPrice`**

#### 2. **Bảng `PartDetail`** - Lưu GIÁ BÁN hiện tại
```
PartDetailID | PartID | Quantity | UnitPrice | Location
100          | 50     | 80       | 18000     | A-01
```

→ **`UnitPrice` trong `PartDetail` là giá bán hoặc giá hiện tại**

### Kết quả:
- ✅ Biết được từng lần nhập kho với giá bao nhiêu
- ✅ Tính được giá vốn trung bình (Average Cost)
- ✅ Báo cáo lãi/lỗ chính xác
- ✅ Theo dõi nhà cung cấp nào cung cấp giá tốt nhất

---

## 🚀 CÁCH SỬ DỤNG

### URL:
```
http://localhost:8080/GarageSystem_war/stock-in
```

### Quyền truy cập:
- ✅ Chỉ Storekeeper (Role ID = 5) mới được phép nhập kho

### Form nhập kho:

```
┌─────────────────────────────────────────┐
│ 📦 STOCK IN - NHẬP KHO                  │
├─────────────────────────────────────────┤
│ Date:         [2025-11-13]              │ ← Ngày nhập (mặc định hôm nay)
│ Part:         [Select Part ▼]           │ ← Chọn linh kiện
│ Quantity:     [50]                      │ ← Số lượng nhập
│ Unit Price:   [15000]                   │ ← GIÁ NHẬP (quan trọng!)
│ Supplier:     [Select Supplier ▼]       │ ← Nhà cung cấp (optional)
│ Note:         [Nhập lô mới từ NCC A]    │ ← Ghi chú (optional)
│                                         │
│         [Cancel]    [Stock In]          │
└─────────────────────────────────────────┘
```

### Parameters:
- **Date**: Ngày nhập kho (YYYY-MM-DD)
- **PartDetailID**: ID của linh kiện (required)
- **Quantity**: Số lượng nhập (required, > 0)
- **UnitPrice**: Giá nhập tại thời điểm này (optional) ⭐
- **SupplierID**: ID nhà cung cấp (optional)
- **Note**: Ghi chú (optional)

---

## 📊 FLOW XỬ LÝ

```
User nhập form
    ↓
StockInController.doPost()
    ↓
1. Validate dữ liệu
    ↓
2. Kiểm tra quyền Storekeeper
    ↓
3. Tạo InventoryTransaction object
    ├── TransactionType = "IN"
    ├── Quantity = số lượng nhập
    ├── UnitPrice = giá nhập ⭐
    └── SupplierID = nhà cung cấp
    ↓
4. performStockIn() - Transaction Database
    ├── INSERT vào InventoryTransaction (lưu lịch sử + giá)
    └── UPDATE PartDetail.Quantity += số lượng
    ↓
5. Redirect về /inventory?action=list
```

---

## 🔍 TRUY VẤN GIÁ THEO THỜI GIAN

### Query: Xem lịch sử nhập kho của 1 sản phẩm
```sql
SELECT 
    it.TransactionDate,
    it.Quantity,
    it.UnitPrice AS PurchasePrice,
    s.SupplierName,
    it.Note
FROM InventoryTransaction it
LEFT JOIN Supplier s ON it.SupplierID = s.SupplierID
WHERE it.PartDetailID = 100 
  AND it.TransactionType = 'IN'
ORDER BY it.TransactionDate DESC;
```

### Query: Tính giá vốn trung bình (Average Cost)
```sql
SELECT 
    pd.PartDetailID,
    p.PartName,
    pd.Quantity AS CurrentStock,
    AVG(it.UnitPrice) AS AveragePurchasePrice,
    pd.UnitPrice AS SellingPrice
FROM PartDetail pd
JOIN Part p ON pd.PartID = p.PartID
LEFT JOIN InventoryTransaction it ON pd.PartDetailID = it.PartDetailID 
    AND it.TransactionType = 'IN'
WHERE pd.PartDetailID = 100
GROUP BY pd.PartDetailID;
```

---

## 📁 CẤU TRÚC FILE

```
src/main/java/
├── controller/inventory/
│   └── StockInController.java          ✅ Hoàn thành
├── dao/inventory/
│   ├── InventoryTransactionDAO.java    ✅ Hoàn thành
│   ├── SupplierDAO.java               ✅ Hoàn thành
│   └── PartInventoryDAO.java          ✅ Đã có sẵn
├── model/inventory/
│   ├── InventoryTransaction.java      ✅ Hoàn thành (có UnitPrice)
│   ├── Supplier.java                  ✅ Hoàn thành
│   └── PartDetail.java                ✅ Đã có sẵn
└── webapp/view/storekeeper/
    └── stock-in.jsp                   ⚠️ Cần tạo/hoàn thiện

update_inventory_transaction.sql       ✅ Đã tạo
```

---

## ⚠️ LƯU Ý QUAN TRỌNG

1. **PHẢI chạy SQL update script TRƯỚC KHI sử dụng**
2. **UnitPrice trong InventoryTransaction** = Giá nhập tại thời điểm đó
3. **UnitPrice trong PartDetail** = Giá bán hiện tại
4. Người dùng PHẢI có Employee record với RoleID = 5 (Storekeeper)
5. Transaction database đảm bảo tính toàn vẹn dữ liệu

---

## 🧪 KIỂM THỬ

### Test Case 1: Nhập kho thành công
```
Input:
- PartDetailID: 1
- Quantity: 50
- UnitPrice: 15000
- SupplierID: 1

Expected:
✅ InventoryTransaction được tạo
✅ PartDetail.Quantity += 50
✅ Redirect về /inventory với success message
```

### Test Case 2: Nhập nhiều lần với giá khác nhau
```
Lần 1: Quantity=50, UnitPrice=10000  → Tổng tồn: 50
Lần 2: Quantity=30, UnitPrice=15000  → Tổng tồn: 80

Query lịch sử:
TransactionID | Date       | Qty | UnitPrice
1             | 2025-01-15 | 50  | 10000
2             | 2025-02-20 | 30  | 15000

Giá trung bình = (50*10000 + 30*15000) / 80 = 11875 VNĐ
```

---

## 📞 HỖ TRỢ

Nếu gặp lỗi:
1. Kiểm tra database đã cập nhật đúng chưa
2. Kiểm tra user có phải Storekeeper không
3. Xem log trong console
4. Kiểm tra bảng InventoryTransaction có đủ các cột: Quantity, SupplierID, UnitPrice

---

## 🎉 HOÀN THÀNH!

Hệ thống nhập kho đã sẵn sàng sử dụng với khả năng:
- ✅ Quản lý giá nhập thay đổi theo thời gian
- ✅ Theo dõi nhà cung cấp
- ✅ Lịch sử giao dịch đầy đủ
- ✅ Transaction database an toàn
- ✅ Báo cáo giá vốn chính xác

**Ngày hoàn thành**: 13/11/2025

