	-- =========================================================
	-- Garage Management - Full Schema (MySQL 8+, InnoDB)
	-- =========================================================
	SET NAMES utf8mb4;
	SET sql_mode = 'STRICT_ALL_TABLES,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';
	SET time_zone = '+00:00';

	-- Tạo database (đổi tên nếu cần)
	CREATE DATABASE IF NOT EXISTS garage_mgmt CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
	USE garage_mgmt;

	-- =========================================================
	-- 0) LOOKUP / USER MANAGEMENT
	-- =========================================================

	CREATE TABLE RoleInfo (
	  RoleID      INT          NOT NULL AUTO_INCREMENT,
	  RoleName    VARCHAR(50)  NOT NULL,
	  PRIMARY KEY (RoleID),
	  UNIQUE KEY ux_role_name (RoleName)
	) ENGINE=InnoDB;

	ALTER TABLE RoleInfo
	  ADD COLUMN Description VARCHAR(255) NULL
	  AFTER RoleName;

	CREATE TABLE User (
	  UserID        INT           NOT NULL AUTO_INCREMENT,
	  RoleID        INT           NOT NULL,
	  FullName      VARCHAR(100)  NOT NULL,
	  UserName      VARCHAR(50)   NOT NULL,
	  Email         VARCHAR(100)  NOT NULL,
	  PhoneNumber   VARCHAR(20),
	  Gender        ENUM('Male', 'Female', 'Other') NULL,
	  Birthdate     DATE NULL,
	  Address       VARCHAR(255) NULL,
	  PasswordHash  VARCHAR(255),
	  ActiveStatus  TINYINT(1)    NOT NULL DEFAULT 1,
	  CreatedAt     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
	  UpdatedAt     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	  PRIMARY KEY (UserID),
	  UNIQUE KEY ux_user_username (UserName),
	  UNIQUE KEY ux_user_email (Email),
	  KEY ix_user_role (RoleID),
	  CONSTRAINT fk_user_role
		FOREIGN KEY (RoleID) REFERENCES RoleInfo(RoleID)
		ON UPDATE RESTRICT ON DELETE RESTRICT
	) ENGINE=InnoDB; 

	CREATE TABLE Customer (
	  CustomerID    INT           NOT NULL AUTO_INCREMENT,
	  UserID        INT           NOT NULL,
	  PointLoyalty  INT           NOT NULL DEFAULT 0,
	  CreatedAt     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
	  PRIMARY KEY (CustomerID),
	  UNIQUE KEY ux_customer_user (UserID),
	  CONSTRAINT fk_customer_user
		FOREIGN KEY (UserID) REFERENCES `User`(UserID)
		ON UPDATE CASCADE ON DELETE CASCADE
	) ENGINE=InnoDB;

	-- *** ĐÃ SỬA LỖI TYPO TÊN BẢNG ***
	CREATE TABLE Employee (
	  EmployeeID    INT          NOT NULL AUTO_INCREMENT,
	  UserID        INT          NOT NULL,
	  EmployeeCode  VARCHAR(20)  UNIQUE,
	  Salary        DECIMAL(10,2),
	  ManagedBy     INT          NULL,   -- line manager (Tech Manager, v.v.)
	  CreatedBy     INT          NULL,
	  CreatedAt     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
	  PRIMARY KEY (EmployeeID),
	  UNIQUE KEY ux_emp_user (UserID),
	  KEY ix_emp_managedby (ManagedBy),
	  KEY ix_emp_createdby (CreatedBy),
	  CONSTRAINT fk_emp_user
		FOREIGN KEY (UserID) REFERENCES `User`(UserID)
		ON UPDATE RESTRICT ON DELETE RESTRICT,
	  CONSTRAINT fk_emp_managedby
		FOREIGN KEY (ManagedBy) REFERENCES Employee(EmployeeID)
		ON UPDATE RESTRICT ON DELETE SET NULL,
	  CONSTRAINT fk_emp_createdby
		FOREIGN KEY (CreatedBy) REFERENCES Employee(EmployeeID)
		ON UPDATE RESTRICT ON DELETE SET NULL
	) ENGINE=InnoDB;


	-- =========================================================
	-- 1) CATALOG / PARTS
	-- =========================================================
	CREATE TABLE Unit (
	  unit_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	  code    VARCHAR(10) UNIQUE,     -- CAI, BO, CHAI, L, KG...
	  name    VARCHAR(50) NOT NULL
	) ENGINE=InnoDB;

	CREATE TABLE Part (
	  PartID       INT           NOT NULL AUTO_INCREMENT,
	  PartCode     VARCHAR(30)   UNIQUE,
	  PartName     VARCHAR(100)  NOT NULL,
	  Category     VARCHAR(50),
	  Description  TEXT,
	  base_unit_id INT           NOT NULL,
	  PRIMARY KEY (PartID),
	  CONSTRAINT fk_part_unit
		FOREIGN KEY (base_unit_id) REFERENCES Unit(unit_id)
		ON UPDATE RESTRICT ON DELETE RESTRICT
	) ENGINE=InnoDB;

	CREATE TABLE CharacteristicType (
	  TypeID   INT          NOT NULL AUTO_INCREMENT,
	  Name     VARCHAR(50)  NOT NULL,
	  PRIMARY KEY (TypeID),
	  UNIQUE KEY ux_ctype_name (Name)
	) ENGINE=InnoDB;

	CREATE TABLE CharacteristicValue (
	  ValueID    INT           NOT NULL AUTO_INCREMENT,
	  ValueName  VARCHAR(100)  NOT NULL,
	  TypeID     INT           NOT NULL,
	  PRIMARY KEY (ValueID),
	  KEY ix_cval_type (TypeID),
	  CONSTRAINT fk_cval_type
		FOREIGN KEY (TypeID) REFERENCES CharacteristicType(TypeID)
		ON UPDATE RESTRICT ON DELETE RESTRICT
	) ENGINE=InnoDB;

	CREATE TABLE PartDetail (
	  PartDetailID  INT           NOT NULL AUTO_INCREMENT,
	  PartID        INT           NOT NULL,
	  TypeID        INT           NULL,           -- tham chiếu loại/thuộc tính (nếu dùng)
	  Quantity      INT           NOT NULL,
	  UnitPrice     DECIMAL(10,2) NOT NULL,
	  PRIMARY KEY (PartDetailID),
	  KEY ix_pdetail_part (PartID),
	  KEY ix_pdetail_type (TypeID),
	  CONSTRAINT fk_pdetail_part
		FOREIGN KEY (PartID) REFERENCES Part(PartID)
		ON UPDATE RESTRICT ON DELETE RESTRICT,
	  CONSTRAINT fk_pdetail_type
		FOREIGN KEY (TypeID) REFERENCES CharacteristicType(TypeID)
		ON UPDATE RESTRICT ON DELETE SET NULL
	) ENGINE=InnoDB;

	-- =========================================================
	-- 2) SERVICE FLOW (REQUEST → APPOINTMENT → WORK ORDER)
	-- =========================================================

	CREATE TABLE Service_Type (
	  ServiceID     INT           NOT NULL AUTO_INCREMENT,
	  ServiceName   VARCHAR(100)  NOT NULL,
	  Category      VARCHAR(50),
	  UnitPrice     DECIMAL(10,2) NOT NULL,
	  PRIMARY KEY (ServiceID),
	  UNIQUE KEY ux_service_name (ServiceName)
	) ENGINE=InnoDB;

	CREATE TABLE Vehicle (
	  VehicleID        INT           NOT NULL AUTO_INCREMENT,
	  CustomerID       INT           NOT NULL,
	  LicensePlate     VARCHAR(15)   UNIQUE,
	  Brand            VARCHAR(50),
	  Model            VARCHAR(50),
	  YearManufacture  YEAR,
	  PRIMARY KEY (VehicleID),
	  KEY ix_vehicle_customer (CustomerID),
	  CONSTRAINT fk_vehicle_customer
		FOREIGN KEY (CustomerID) REFERENCES Customer(CustomerID)
		ON UPDATE RESTRICT ON DELETE RESTRICT
	) ENGINE=InnoDB;

	CREATE TABLE Appointment (
	  AppointmentID  INT        NOT NULL AUTO_INCREMENT,
	  CustomerID     INT        NOT NULL,
	  VehicleID      INT        NOT NULL,
	  Date           DATETIME   NOT NULL,
	  Status         ENUM('CANCELLED','CONFIRM','COMPLETE') NOT NULL,
	  PRIMARY KEY (AppointmentID),
	  KEY ix_appt_customer (CustomerID),
	  KEY ix_appt_vehicle (VehicleID),
	  CONSTRAINT fk_appt_customer
		FOREIGN KEY (CustomerID) REFERENCES Customer(CustomerID)
		ON UPDATE RESTRICT ON DELETE RESTRICT,
	  CONSTRAINT fk_appt_vehicle
		FOREIGN KEY (VehicleID) REFERENCES Vehicle(VehicleID)
		ON UPDATE RESTRICT ON DELETE RESTRICT
	) ENGINE=InnoDB;

	alter table appointment
	add column Description Text;

	CREATE TABLE ServiceRequest (
	  RequestID       INT         NOT NULL AUTO_INCREMENT,
	  CustomerID      INT         NOT NULL,
	  VehicleID       INT         NOT NULL,
	  ServiceID       INT         NOT NULL, -- loại dịch vụ
	  AppointmentID   INT         NULL,     -- nếu đến từ đặt lịch
	  RequestDate     DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
	  Status          ENUM('PENDING','APPROVE','REJECTED') NOT NULL DEFAULT 'PENDING',
	  PRIMARY KEY (RequestID),
	  KEY ix_sr_customer (CustomerID),
	  KEY ix_sr_vehicle (VehicleID),
	  KEY ix_sr_service (ServiceID),
	  KEY ix_sr_appointment (AppointmentID),
	  CONSTRAINT fk_sr_customer
		FOREIGN KEY (CustomerID) REFERENCES Customer(CustomerID)
		ON UPDATE RESTRICT ON DELETE RESTRICT,
	  CONSTRAINT fk_sr_vehicle
		FOREIGN KEY (VehicleID) REFERENCES Vehicle(VehicleID)
		ON UPDATE RESTRICT ON DELETE RESTRICT,
	  CONSTRAINT fk_sr_service
		FOREIGN KEY (ServiceID) REFERENCES Service_Type(ServiceID)
		ON UPDATE RESTRICT ON DELETE RESTRICT,
	  CONSTRAINT fk_sr_appointment
		FOREIGN KEY (AppointmentID) REFERENCES Appointment(AppointmentID)
		ON UPDATE RESTRICT ON DELETE SET NULL
	) ENGINE=InnoDB;

	CREATE TABLE WorkOrder (
	  WorkOrderID    INT        NOT NULL AUTO_INCREMENT,
	  TechManagerID  INT        NOT NULL, -- người duyệt/phân công
	  RequestID      INT        NOT NULL, -- tạo khi request APPROVE
	  EstimateAmount DECIMAL(10,2),
	  Status         ENUM('PENDING','IN_PROCESS','COMPLETE') NOT NULL DEFAULT 'PENDING',
	  CreatedAt      DATETIME   NOT NULL DEFAULT CURRENT_TIMESTAMP,
	  PRIMARY KEY (WorkOrderID),
	  UNIQUE KEY ux_wo_request (RequestID),
	  KEY ix_wo_manager (TechManagerID),
	  KEY ix_wod_wo_status (Status),
	  CONSTRAINT fk_wo_request
		FOREIGN KEY (RequestID) REFERENCES ServiceRequest(RequestID)
		ON UPDATE RESTRICT ON DELETE RESTRICT,
	  CONSTRAINT fk_wo_manager
		FOREIGN KEY (TechManagerID) REFERENCES Employee(EmployeeID)
		ON UPDATE RESTRICT ON DELETE RESTRICT
	) ENGINE=InnoDB;

	-- =========================================================
	-- 3) DIAGNOSTICS (1:N với Assignment) + N:N technician tham gia
	-- =========================================================

	CREATE TABLE VehicleDiagnostic (
	  VehicleDiagnosticID  INT           NOT NULL AUTO_INCREMENT,
	  AssignmentID         INT           NOT NULL,
	  IssueFound           TEXT,
	  EstimateCost         DECIMAL(10,2),
	  Status               TINYINT(1)    NOT NULL DEFAULT 1,
	  CreatedAt            DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
	  PRIMARY KEY (VehicleDiagnosticID),
	  KEY ix_vd_assignment (AssignmentID)
	) ENGINE=InnoDB;

	CREATE TABLE WorkOrderDetail (
	  DetailID              INT           NOT NULL AUTO_INCREMENT,
	  WorkOrderID           INT           NOT NULL,
	  source                ENUM('REQUEST','DIAGNOSTIC') NOT NULL DEFAULT 'REQUEST',
	  diagnostic_id         INT           NULL,
	  approval_status       ENUM('PENDING','APPROVED','DECLINED') NULL DEFAULT NULL,
	  approved_by_user_id   INT           NULL,
	  approved_at           DATETIME      NULL,
	  TaskDescription       TEXT,
	  EstimateHours         DECIMAL(5,2),
	  EstimateAmount        DECIMAL(10,2) NULL,
	  ActualHours           DECIMAL(5,2),
	  PRIMARY KEY (DetailID),
	  KEY ix_wod_wo (WorkOrderID),
	  KEY ix_wod_diagnostic (diagnostic_id),
	  KEY ix_wod_source (source),
	  KEY ix_wod_approval (approval_status),
	  CONSTRAINT fk_wod_wo
		FOREIGN KEY (WorkOrderID) REFERENCES WorkOrder(WorkOrderID)
		ON UPDATE RESTRICT ON DELETE CASCADE,
	  CONSTRAINT fk_wod_diagnostic
		FOREIGN KEY (diagnostic_id) REFERENCES VehicleDiagnostic(VehicleDiagnosticID)
		ON UPDATE RESTRICT ON DELETE SET NULL
	) ENGINE=InnoDB;

	CREATE TABLE TaskAssignment (
	  AssignmentID    INT        NOT NULL AUTO_INCREMENT,
	  DetailID        INT        NOT NULL,
	  AssignToTechID  INT        NOT NULL,
	  AssignedDate    DATETIME,
	  StartAt         DATETIME,
	  CompleteAt      DATETIME,
	  TaskDescription TEXT,
	  task_type       ENUM('DIAGNOSIS','REPAIR','OTHER') NOT NULL DEFAULT 'REPAIR',
	  Status          ENUM('ASSIGNED','IN_PROGRESS','COMPLETE') NOT NULL DEFAULT 'ASSIGNED',
	  PRIMARY KEY (AssignmentID),
	  KEY ix_ta_detail (DetailID),
	  KEY ix_ta_tech (AssignToTechID),
	  KEY ix_ta_tasktype (task_type),
	  KEY ix_ta_status (Status),
	  CONSTRAINT fk_ta_detail
		FOREIGN KEY (DetailID) REFERENCES WorkOrderDetail(DetailID)
		ON UPDATE RESTRICT ON DELETE CASCADE,
	  CONSTRAINT fk_ta_tech
		FOREIGN KEY (AssignToTechID) REFERENCES Employee(EmployeeID)
		ON UPDATE RESTRICT ON DELETE RESTRICT
	) ENGINE=InnoDB;

	-- Thêm foreign key cho VehicleDiagnostic sau khi TaskAssignment đã được tạo
	ALTER TABLE VehicleDiagnostic
	  ADD CONSTRAINT fk_vd_assignment
		FOREIGN KEY (AssignmentID) REFERENCES TaskAssignment(AssignmentID)
		ON UPDATE RESTRICT ON DELETE CASCADE;

	-- Nếu một diagnostic có nhiều technician cùng làm:
	CREATE TABLE VehicleDiagnosticTechnician (
	  VehicleDiagnosticID INT NOT NULL,
	  TechnicianID        INT NOT NULL,
	  IsLead              TINYINT(1) DEFAULT 0,
	  HoursSpent          DECIMAL(6,2),
	  AddedAt             DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
	  PRIMARY KEY (VehicleDiagnosticID, TechnicianID),
	  KEY ix_vdt_tech (TechnicianID),
	  CONSTRAINT fk_vdt_vd
		FOREIGN KEY (VehicleDiagnosticID) REFERENCES VehicleDiagnostic(VehicleDiagnosticID)
		ON UPDATE RESTRICT ON DELETE CASCADE,
	  CONSTRAINT fk_vdt_emp
		FOREIGN KEY (TechnicianID) REFERENCES Employee(EmployeeID)
		ON UPDATE RESTRICT ON DELETE RESTRICT
	) ENGINE=InnoDB;

	-- =========================================================
	-- 4) WORK ORDER PARTS & INVENTORY
	-- =========================================================

	CREATE TABLE WorkOrderPart (
	  WorkOrderPartID  INT           NOT NULL AUTO_INCREMENT,
	  DetailID         INT           NOT NULL,
	  PartDetailID     INT           NOT NULL,
	  RequestedByID    INT           NOT NULL, -- người yêu cầu (tech/manager)
	  QuantityUsed     INT           NOT NULL,
	  UnitPrice        DECIMAL(10,2) NOT NULL,
	  PRIMARY KEY (WorkOrderPartID),
	  KEY ix_wop_detail (DetailID),
	  KEY ix_wop_partdetail (PartDetailID),
	  KEY ix_wop_requestedby (RequestedByID),
	  CONSTRAINT fk_wop_detail
		FOREIGN KEY (DetailID) REFERENCES WorkOrderDetail(DetailID)
		ON UPDATE RESTRICT ON DELETE CASCADE,
	  CONSTRAINT fk_wop_partdetail
		FOREIGN KEY (PartDetailID) REFERENCES PartDetail(PartDetailID)
		ON UPDATE RESTRICT ON DELETE RESTRICT,
	  CONSTRAINT fk_wop_requestedby
		FOREIGN KEY (RequestedByID) REFERENCES Employee(EmployeeID)
		ON UPDATE RESTRICT ON DELETE RESTRICT
	) ENGINE=InnoDB;

	CREATE TABLE InventoryTransaction (
	  TransactionID    INT           NOT NULL AUTO_INCREMENT,
	  PartID           INT           NOT NULL,
	  PartDetailID     INT           NULL,
	  TransactionType  ENUM('IN','OUT','JUSTIFY') NOT NULL,
	  TransactionDate  DATETIME      NOT NULL,
	  StoreKeeperID    INT           NOT NULL,
	  WorkOrderPartID  INT           NULL,   -- nếu OUT cho WO
	  Note             TEXT,
	  PRIMARY KEY (TransactionID),
	  KEY ix_it_part (PartID),
	  KEY ix_it_partdetail (PartDetailID),
	  KEY ix_it_storekeeper (StoreKeeperID),
	  KEY ix_it_wop (WorkOrderPartID),
	  KEY ix_it_type_date (TransactionType, TransactionDate),
	  CONSTRAINT fk_it_part
		FOREIGN KEY (PartID) REFERENCES Part(PartID)
		ON UPDATE RESTRICT ON DELETE RESTRICT,
	  CONSTRAINT fk_it_partdetail
		FOREIGN KEY (PartDetailID) REFERENCES PartDetail(PartDetailID)
		ON UPDATE RESTRICT ON DELETE SET NULL,
	  CONSTRAINT fk_it_storekeeper
		FOREIGN KEY (StoreKeeperID) REFERENCES Employee(EmployeeID)
		ON UPDATE RESTRICT ON DELETE RESTRICT,
	  CONSTRAINT fk_it_wop
		FOREIGN KEY (WorkOrderPartID) REFERENCES WorkOrderPart(WorkOrderPartID)
		ON UPDATE RESTRICT ON DELETE SET NULL
	) ENGINE=InnoDB;

	-- =========================================================
	-- 5) INVOICE / PAYMENT / FEEDBACK / NOTIFICATION
	-- =========================================================

	CREATE TABLE Invoice (
	  InvoiceID      INT           NOT NULL AUTO_INCREMENT,
	  WorkOrderID    INT           NOT NULL,
	  InvoiceNumber  VARCHAR(20)   NOT NULL,
	  InvoiceDate    DATE          NOT NULL,
	  DueDate        DATE          NULL,
	  Subtotal       DECIMAL(12,2) NOT NULL DEFAULT 0,
	  TaxAmount      DECIMAL(12,2) NOT NULL DEFAULT 0,
	  TotalAmount    DECIMAL(12,2) GENERATED ALWAYS AS (Subtotal + TaxAmount) STORED,
	  PaidAmount     DECIMAL(12,2) NOT NULL DEFAULT 0,
	  BalanceAmount  DECIMAL(12,2) GENERATED ALWAYS AS (TotalAmount - PaidAmount) STORED,
	  PaymentStatus  ENUM('UNPAID','PARTIALLY_PAID','PAID','VOID') NOT NULL DEFAULT 'UNPAID',
	  Notes          TEXT,
	  CreatedAt      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
	  UpdatedAt      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	  PRIMARY KEY (InvoiceID),
	  UNIQUE KEY ux_invoice_number (InvoiceNumber),
	  UNIQUE KEY ux_invoice_workorder (WorkOrderID),
	  KEY ix_invoice_wo (WorkOrderID),
	  KEY ix_invoice_status_date (PaymentStatus, InvoiceDate),
	  CONSTRAINT fk_invoice_wo
		FOREIGN KEY (WorkOrderID) REFERENCES WorkOrder(WorkOrderID)
		ON UPDATE RESTRICT ON DELETE RESTRICT
	) ENGINE=InnoDB;

	CREATE TABLE Payment (
	  PaymentID     INT           NOT NULL AUTO_INCREMENT,
	  InvoiceID     INT           NOT NULL,
	  WorkOrderID   INT           NOT NULL,            -- giúp truy vấn nhanh theo WO
	  PaymentDate   DATETIME      NOT NULL,
	  Amount        DECIMAL(12,2) NOT NULL,
	  Method        ENUM('ONLINE','OFFLINE') NOT NULL,
	  ReferenceNo   VARCHAR(50),
	  AccountantID  INT           NOT NULL,
	  Note          TEXT,
	  PRIMARY KEY (PaymentID),
	  KEY ix_pay_invoice (InvoiceID),
	  KEY ix_pay_workorder (WorkOrderID),
	  KEY ix_pay_accountant (AccountantID),
	  KEY ix_pay_date (PaymentDate),
	  CONSTRAINT ck_pay_amount CHECK (Amount > 0),
	  CONSTRAINT fk_pay_invoice
		FOREIGN KEY (InvoiceID) REFERENCES Invoice(InvoiceID)
		ON UPDATE RESTRICT ON DELETE RESTRICT,
	  CONSTRAINT fk_pay_workorder
		FOREIGN KEY (WorkOrderID) REFERENCES WorkOrder(WorkOrderID)
		ON UPDATE RESTRICT ON DELETE RESTRICT,
	  CONSTRAINT fk_pay_accountant
		FOREIGN KEY (AccountantID) REFERENCES Employee(EmployeeID)
		ON UPDATE RESTRICT ON DELETE RESTRICT
	) ENGINE=InnoDB;

	CREATE TABLE Feedback (
	  FeedbackID    INT         NOT NULL AUTO_INCREMENT,
	  CustomerID    INT         NOT NULL,
	  WorkOrderID   INT         NOT NULL,
	  Rating        TINYINT     NOT NULL,
	  FeedbackText  TEXT,
	  FeedbackDate  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
	  PRIMARY KEY (FeedbackID),
	  KEY ix_fb_customer (CustomerID),
	  KEY ix_fb_workorder (WorkOrderID),
	  CONSTRAINT ck_fb_rating CHECK (Rating BETWEEN 1 AND 5),
	  CONSTRAINT fk_fb_customer
		FOREIGN KEY (CustomerID) REFERENCES Customer(CustomerID)
		ON UPDATE RESTRICT ON DELETE RESTRICT,
	  CONSTRAINT fk_fb_workorder
		FOREIGN KEY (WorkOrderID) REFERENCES WorkOrder(WorkOrderID)
		ON UPDATE RESTRICT ON DELETE RESTRICT
	) ENGINE=InnoDB;

	CREATE TABLE Notification (
	  NotificationID  INT        NOT NULL AUTO_INCREMENT,
	  UserID          INT        NOT NULL,
	  Title           VARCHAR(200) NOT NULL,
	  Body            TEXT,
	  CreatedAt       DATETIME   NOT NULL DEFAULT CURRENT_TIMESTAMP,
	  IsRead          TINYINT(1) NOT NULL DEFAULT 0,
	  EntityType      ENUM('APPOINTMENT','SERVICE_REQUEST','WORK_ORDER','INVOICE','PAYMENT') NOT NULL,
	  EntityID        INT        NOT NULL,
	  AppointmentID   INT        NULL, -- tuỳ chọn, nếu cần ràng buộc cứng
	  PRIMARY KEY (NotificationID),
	  KEY ix_ntf_user_created (UserID, CreatedAt),
	  KEY ix_ntf_entity (EntityType, EntityID),
	  KEY ix_ntf_appt (AppointmentID),
	  CONSTRAINT fk_ntf_user
		FOREIGN KEY (UserID) REFERENCES `User`(UserID)
		ON UPDATE RESTRICT ON DELETE CASCADE,
	  CONSTRAINT fk_ntf_appt
		FOREIGN KEY (AppointmentID) REFERENCES Appointment(AppointmentID)
		ON UPDATE RESTRICT ON DELETE SET NULL
	) ENGINE=InnoDB;

	-- =========================================================
	-- 6) PERMISSION & ROLE MANAGEMENT
	-- =========================================================

	CREATE TABLE Permission (
	  PermID      INT NOT NULL AUTO_INCREMENT,
	  Code        VARCHAR(64) NOT NULL,
	  Name        VARCHAR(100) NOT NULL,
	  Category    VARCHAR(50)  NULL,
	  Description VARCHAR(255) NULL,
	  Active      TINYINT(1)   NOT NULL DEFAULT 1,
	  PRIMARY KEY (PermID),
	  UNIQUE KEY ux_perm_code (Code),
	  KEY ix_perm_category (Category)
	) ENGINE=InnoDB;

	CREATE TABLE RolePermission (
	  RoleID INT NOT NULL,
	  PermID INT NOT NULL,
	  AssignedByUserID INT NULL COMMENT 'ID of the user who last assigned/modified this permission for the role',
	  AssignedAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
	  PRIMARY KEY (RoleID, PermID),
	  KEY ix_rp_perm (PermID),
	  CONSTRAINT fk_rp_role FOREIGN KEY (RoleID) REFERENCES RoleInfo(RoleID)
		ON UPDATE RESTRICT ON DELETE CASCADE,
	  CONSTRAINT fk_rp_perm FOREIGN KEY (PermID) REFERENCES Permission(PermID)
		ON UPDATE RESTRICT ON DELETE CASCADE,
	  CONSTRAINT fk_rp_assigned_by
		FOREIGN KEY (AssignedByUserID)
		REFERENCES `User`(UserID)
		ON UPDATE RESTRICT ON DELETE SET NULL
	) ENGINE=InnoDB;

	CREATE TABLE MenuItem (
	  MenuID   INT NOT NULL AUTO_INCREMENT,
	  Label    VARCHAR(100) NOT NULL,
	  Path     VARCHAR(200) NOT NULL,
	  Icon     VARCHAR(50)  NULL,
	  OrderNo  INT NOT NULL DEFAULT 0,
	  ParentID INT NULL,
	  PermID   INT NULL,
	  PRIMARY KEY (MenuID),
	  KEY ix_menu_parent (ParentID),
	  KEY ix_menu_perm (PermID),
	  CONSTRAINT fk_menu_parent FOREIGN KEY (ParentID) REFERENCES MenuItem(MenuID)
		ON UPDATE RESTRICT ON DELETE CASCADE,
	  CONSTRAINT fk_menu_perm FOREIGN KEY (PermID) REFERENCES Permission(PermID)
		ON UPDATE RESTRICT ON DELETE SET NULL
	) ENGINE=InnoDB;

	ALTER TABLE RoleInfo
	  ADD COLUMN RoleCode VARCHAR(40)
		AS (UPPER(REPLACE(TRIM(RoleName),' ','_'))) STORED;

	CREATE UNIQUE INDEX ux_role_rolecode ON  RoleInfo(RoleCode);

	DELIMITER $$

	-- *** ĐÃ SỬA TRIGGER ĐỂ TRÁNH LỖI DUPLICATE ENTRY ***
	-- Trigger này giờ CHỈ tự động tạo Customer. 
	-- Việc tạo Employee sẽ do code Java (AdminDAO) xử lý.
	CREATE TRIGGER trg_after_user_insert_create_profile
	AFTER INSERT ON User
	FOR EACH ROW
	BEGIN
		DECLARE v_role_name VARCHAR(50);

		SELECT RoleName INTO v_role_name 
		FROM RoleInfo 
		WHERE RoleID = NEW.RoleID;

		IF v_role_name = 'Customer' THEN
			INSERT INTO Customer (UserID) VALUES (NEW.UserID);
		END IF;
	END$$

	-- Trigger cho UPDATE (để xử lý đổi vai trò, giữ nguyên)
	CREATE TRIGGER trg_after_user_update_manage_profiles
	AFTER UPDATE ON User
	FOR EACH ROW
	BEGIN
		DECLARE v_old_role_name VARCHAR(50);
		DECLARE v_new_role_name VARCHAR(50);
		DECLARE v_employee_exists INT DEFAULT 0;
		DECLARE v_customer_exists INT DEFAULT 0;
		
		IF OLD.RoleID <> NEW.RoleID THEN
		
			SELECT RoleName INTO v_old_role_name FROM RoleInfo WHERE RoleID = OLD.RoleID;
			SELECT RoleName INTO v_new_role_name FROM RoleInfo WHERE RoleID = NEW.RoleID;

			-- Tăng cấp: Customer -> Employee
			IF v_old_role_name = 'Customer' AND v_new_role_name <> 'Customer' THEN
				SELECT COUNT(*) INTO v_employee_exists FROM Employee WHERE UserID = NEW.UserID;
				IF v_employee_exists = 0 THEN
					INSERT INTO Employee (UserID, EmployeeCode, Salary, CreatedAt)
					VALUES (NEW.UserID, NULL, NULL, NOW());
				END IF;
			
			-- Giáng cấp: Employee -> Customer
			ELSEIF v_old_role_name <> 'Customer' AND v_new_role_name = 'Customer' THEN
				SELECT COUNT(*) INTO v_customer_exists FROM Customer WHERE UserID = NEW.UserID;
				IF v_customer_exists = 0 THEN
					INSERT INTO Customer (UserID) VALUES (NEW.UserID);
				END IF;
			END IF;
		END IF;
	END$$

	DELIMITER ;

	CREATE TABLE CarBrands (
		BrandID INT NOT NULL AUTO_INCREMENT,
		BrandName VARCHAR(50) NOT NULL,
		PRIMARY KEY (BrandID),
		UNIQUE KEY ux_brand_name (BrandName)
	);

	CREATE TABLE CarModels (
		ModelID INT NOT NULL AUTO_INCREMENT,
		ModelName VARCHAR(50) NOT NULL,
		BrandID INT NOT NULL,
		PRIMARY KEY (ModelID),
		CONSTRAINT fk_model_brand
			FOREIGN KEY (BrandID) REFERENCES CarBrands(BrandID)
			ON DELETE CASCADE
	);
	ALTER TABLE Appointment
	MODIFY COLUMN Status ENUM(
		'PENDING',
		'ACCEPTED',
		'REJECTED',
		'CANCELLED'
	) DEFAULT 'PENDING';

	CREATE TABLE SupportCategory (
	  CategoryID INT AUTO_INCREMENT PRIMARY KEY,
	  CategoryName VARCHAR(100) NOT NULL,
	  Description VARCHAR(255)
	);

	CREATE TABLE SupportFAQ (
	  FAQID INT AUTO_INCREMENT PRIMARY KEY,
	  Question VARCHAR(255) NOT NULL,
	  Answer TEXT NOT NULL,
	  IsActive BOOLEAN DEFAULT TRUE
	);

	CREATE TABLE SupportChannel (
	  ChannelID INT AUTO_INCREMENT PRIMARY KEY,
	  ChannelName VARCHAR(100) NOT NULL,
	  IconPath VARCHAR(255),
	  ContactLink VARCHAR(255),
	  Description VARCHAR(255),
	  IsActive BOOLEAN DEFAULT TRUE
	);

	CREATE TABLE SupportRequest (
	  RequestID INT AUTO_INCREMENT PRIMARY KEY,
	  CustomerID INT NOT NULL,
	  WorkOrderID INT NULL,
	  AppointmentID INT NULL,
	  Email VARCHAR(255) NOT NULL,
	  Phone VARCHAR(50),
	  CategoryID INT,
	  Description TEXT,
	  AttachmentPath VARCHAR(255),
	  Status ENUM('Pending', 'InProgress', 'Resolved', 'Closed') DEFAULT 'Pending',
	  CreatedAt DATETIME DEFAULT CURRENT_TIMESTAMP,
	  UpdatedAt DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	  FOREIGN KEY (CustomerID) REFERENCES Customer(CustomerID) ON DELETE cascade,
	  FOREIGN KEY (WorkOrderID) REFERENCES WorkOrder(WorkOrderID) ON DELETE SET NULL,
	  FOREIGN KEY (AppointmentID) REFERENCES Appointment(AppointmentID) ON DELETE SET NULL,
	  FOREIGN KEY (CategoryID) REFERENCES SupportCategory(CategoryID) ON DELETE SET NULL
	);

	CREATE TABLE SupportRequest (
	  RequestID INT AUTO_INCREMENT PRIMARY KEY,
	  CustomerID INT NOT NULL,
	  WorkOrderID INT NULL,
	  AppointmentID INT NULL,
	  Email VARCHAR(255) NOT NULL,
	  Phone VARCHAR(50),
	  CategoryID INT,
	  Description TEXT,
	  AttachmentPath VARCHAR(255),
	  Status ENUM('Pending', 'InProgress', 'Resolved', 'Closed') DEFAULT 'Pending',
	  CreatedAt DATETIME DEFAULT CURRENT_TIMESTAMP,
	  UpdatedAt DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	  FOREIGN KEY (CustomerID) REFERENCES Customer(CustomerID) ON DELETE cascade,
	  FOREIGN KEY (WorkOrderID) REFERENCES WorkOrder(WorkOrderID) ON DELETE SET NULL,
	  FOREIGN KEY (AppointmentID) REFERENCES Appointment(AppointmentID) ON DELETE SET NULL,
	  FOREIGN KEY (CategoryID) REFERENCES SupportCategory(CategoryID) ON DELETE SET NULL
	);


	-- =========================================================
	-- STORED PROCEDURES
	-- =========================================================

	DELIMITER $$

	-- SP đã sửa (INSERT trước, UPDATE sau) để tương thích với Trigger
	CREATE PROCEDURE SP_PromoteCustomerToEmployee(
		IN p_user_id INT,
		IN p_new_role_name VARCHAR(50),
		IN p_employee_code VARCHAR(20),
		IN p_salary DECIMAL(10,2),
		IN p_managed_by_employee_id INT,
		IN p_created_by_employee_id INT
	)
	BEGIN
		DECLARE v_new_role_id INT;
		DECLARE v_current_role_id INT;
		DECLARE v_customer_role_id INT;
		DECLARE v_existing_employee_count INT;
		DECLARE EXIT HANDLER FOR SQLEXCEPTION
		BEGIN
			ROLLBACK;
			RESIGNAL;
		END;

		START TRANSACTION;

		SELECT RoleID INTO v_current_role_id 
		FROM User 
		WHERE UserID = p_user_id;

		SELECT RoleID INTO v_customer_role_id 
		FROM RoleInfo 
		WHERE RoleName = 'Customer' 
		LIMIT 1;

		SELECT RoleID INTO v_new_role_id 
		FROM RoleInfo 
		WHERE RoleName = p_new_role_name 
		LIMIT 1;

		IF v_current_role_id IS NULL THEN
			SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'User not found';
		END IF;
		IF v_new_role_id IS NULL THEN
			SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Target role not found';
		END IF;
		IF v_customer_role_id IS NULL THEN
			SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Customer role not found in system';
		END IF;
		IF v_current_role_id != v_customer_role_id THEN
			SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'User is not a Customer, cannot promote';
		END IF;

		SELECT COUNT(*) INTO v_existing_employee_count 
		FROM Employee 
		WHERE UserID = p_user_id;

		IF v_existing_employee_count > 0 THEN
			SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'User already has Employee record';
		END IF;
		
		-- 1. Create Employee record (CHẠY TRƯỚC)
		INSERT INTO Employee (UserID, EmployeeCode, Salary, ManagedBy, CreatedBy, CreatedAt)
		VALUES (p_user_id, p_employee_code, p_salary, p_managed_by_employee_id, p_created_by_employee_id, NOW());

		-- 2. Update user role (CHẠY SAU)
		UPDATE User 
		SET RoleID = v_new_role_id, UpdatedAt = NOW() 
		WHERE UserID = p_user_id;

		COMMIT;
	END$$

	DELIMITER ;

	-- =========================================================
	-- MIGRATION SCRIPT: Technician Dashboard Enhancement
	-- =========================================================

	-- (Giữ nguyên các lệnh ALTER và CREATE IF NOT EXISTS)

	USE garage_mgmt;

	-- 1. Enhance TaskAssignment Table
	SET @col_exists = 0;
	SELECT COUNT(*) INTO @col_exists 
	FROM INFORMATION_SCHEMA.COLUMNS 
	WHERE TABLE_SCHEMA = 'garage_mgmt' 
	  AND TABLE_NAME = 'TaskAssignment' 
	  AND COLUMN_NAME = 'priority';

	SET @sql = IF(@col_exists = 0,
		'ALTER TABLE TaskAssignment ADD COLUMN priority ENUM(''LOW'', ''MEDIUM'', ''HIGH'', ''URGENT'') DEFAULT ''MEDIUM'' COMMENT ''Task priority level'' AFTER task_type',
		'SELECT ''Column priority already exists'' AS Info');
	PREPARE stmt FROM @sql;
	EXECUTE stmt;
	DEALLOCATE PREPARE stmt;

	SET @col_exists = 0;
	SELECT COUNT(*) INTO @col_exists 
	FROM INFORMATION_SCHEMA.COLUMNS 
	WHERE TABLE_SCHEMA = 'garage_mgmt' 
	  AND TABLE_NAME = 'TaskAssignment' 
	  AND COLUMN_NAME = 'progress_percentage';

	SET @sql = IF(@col_exists = 0,
		'ALTER TABLE TaskAssignment ADD COLUMN progress_percentage TINYINT DEFAULT 0 COMMENT ''Progress from 0-100%'' AFTER Status',
		'SELECT ''Column progress_percentage already exists'' AS Info');
	PREPARE stmt FROM @sql;
	EXECUTE stmt;
	DEALLOCATE PREPARE stmt;

	SET @col_exists = 0;
	SELECT COUNT(*) INTO @col_exists 
	FROM INFORMATION_SCHEMA.COLUMNS 
	WHERE TABLE_SCHEMA = 'garage_mgmt' 
	  AND TABLE_NAME = 'TaskAssignment' 
	  AND COLUMN_NAME = 'notes';

	SET @sql = IF(@col_exists = 0,
		'ALTER TABLE TaskAssignment ADD COLUMN notes TEXT NULL COMMENT ''Technician notes for this task''',
		'SELECT ''Column notes already exists'' AS Info');
	PREPARE stmt FROM @sql;
	EXECUTE stmt;
	DEALLOCATE PREPARE stmt;

	SET @index_exists = 0;
	SELECT COUNT(*) INTO @index_exists
	FROM INFORMATION_SCHEMA.STATISTICS
	WHERE TABLE_SCHEMA = 'garage_mgmt'
	  AND TABLE_NAME = 'TaskAssignment'
	  AND INDEX_NAME = 'ix_ta_priority';

	SET @sql = IF(@index_exists = 0,
		'CREATE INDEX ix_ta_priority ON TaskAssignment(priority)',
		'SELECT ''Index ix_ta_priority already exists'' AS Info');
	PREPARE stmt FROM @sql;
	EXECUTE stmt;
	DEALLOCATE PREPARE stmt;

	SET @index_exists = 0;
	SELECT COUNT(*) INTO @index_exists
	FROM INFORMATION_SCHEMA.STATISTICS
	WHERE TABLE_SCHEMA = 'garage_mgmt'
	  AND TABLE_NAME = 'TaskAssignment'
	  AND INDEX_NAME = 'ix_ta_status_priority';

	SET @sql = IF(@index_exists = 0,
		'CREATE INDEX ix_ta_status_priority ON TaskAssignment(Status, priority)',
		'SELECT ''Index ix_ta_status_priority already exists'' AS Info');
	PREPARE stmt FROM @sql;
	EXECUTE stmt;
	DEALLOCATE PREPARE stmt;

	-- 2. Enhance WorkOrderPart Table
	SET @col_exists = 0;
	SELECT COUNT(*) INTO @col_exists 
	FROM INFORMATION_SCHEMA.COLUMNS 
	WHERE TABLE_SCHEMA = 'garage_mgmt' 
	  AND TABLE_NAME = 'WorkOrderPart' 
	  AND COLUMN_NAME = 'request_status';

	SET @sql = IF(@col_exists = 0,
		'ALTER TABLE WorkOrderPart ADD COLUMN request_status ENUM(''PENDING'', ''AVAILABLE'', ''DELIVERED'') DEFAULT ''PENDING'' COMMENT ''Status of parts request'' AFTER UnitPrice',
		'SELECT ''Column request_status already exists'' AS Info');
	PREPARE stmt FROM @sql;
	EXECUTE stmt;
	DEALLOCATE PREPARE stmt;

	SET @col_exists = 0;
	SELECT COUNT(*) INTO @col_exists 
	FROM INFORMATION_SCHEMA.COLUMNS 
	WHERE TABLE_SCHEMA = 'garage_mgmt' 
	  AND TABLE_NAME = 'WorkOrderPart' 
	  AND COLUMN_NAME = 'requested_at';

	SET @sql = IF(@col_exists = 0,
		'ALTER TABLE WorkOrderPart ADD COLUMN requested_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT ''When parts were requested''',
		'SELECT ''Column requested_at already exists'' AS Info');
	PREPARE stmt FROM @sql;
	EXECUTE stmt;
	DEALLOCATE PREPARE stmt;

	SET @index_exists = 0;
	SELECT COUNT(*) INTO @index_exists
	FROM INFORMATION_SCHEMA.STATISTICS
	WHERE TABLE_SCHEMA = 'garage_mgmt'
	  AND TABLE_NAME = 'WorkOrderPart'
	  AND INDEX_NAME = 'ix_wop_status';

	SET @sql = IF(@index_exists = 0,
		'CREATE INDEX ix_wop_status ON WorkOrderPart(request_status)',
		'SELECT ''Index ix_wop_status already exists'' AS Info');
	PREPARE stmt FROM @sql;
	EXECUTE stmt;
	DEALLOCATE PREPARE stmt;

	SET @index_exists = 0;
	SELECT COUNT(*) INTO @index_exists
	FROM INFORMATION_SCHEMA.STATISTICS
	WHERE TABLE_SCHEMA = 'garage_mgmt'
	  AND TABLE_NAME = 'WorkOrderPart'
	  AND INDEX_NAME = 'ix_wop_requestedby_status';

	SET @sql = IF(@index_exists = 0,
		'CREATE INDEX ix_wop_requestedby_status ON WorkOrderPart(RequestedByID, request_status)',
		'SELECT ''Index ix_wop_requestedby_status already exists'' AS Info');
	PREPARE stmt FROM @sql;
	EXECUTE stmt;
	DEALLOCATE PREPARE stmt;

	-- 3. Create TechnicianActivityLog Table
	CREATE TABLE IF NOT EXISTS TechnicianActivityLog (
		ActivityID INT NOT NULL AUTO_INCREMENT,
		TechnicianID INT NOT NULL,
		ActivityType ENUM(
			'TASK_ACCEPTED', 
			'TASK_REJECTED',
			'TASK_STARTED', 
			'TASK_COMPLETED',
			'PARTS_REQUESTED',
			'DIAGNOSTIC_CREATED',
			'PROGRESS_UPDATED',
			'NOTE_ADDED'
		) NOT NULL,
		TaskAssignmentID INT NULL,
		Description VARCHAR(500),
		ActivityTime DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
		PRIMARY KEY (ActivityID),
		KEY ix_activity_tech_time (TechnicianID, ActivityTime DESC),
		KEY ix_activity_type (ActivityType),
		KEY ix_activity_task (TaskAssignmentID),
		CONSTRAINT fk_activity_tech
			FOREIGN KEY (TechnicianID) REFERENCES Employee(EmployeeID)
			ON UPDATE RESTRICT ON DELETE CASCADE,
		CONSTRAINT fk_activity_task
			FOREIGN KEY (TaskAssignmentID) REFERENCES TaskAssignment(AssignmentID)
			ON UPDATE RESTRICT ON DELETE SET NULL
	) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

	ALTER TABLE ServiceRequest
	ADD COLUMN Note VARCHAR(500) NULL;

	ALTER TABLE ServiceRequest
	DROP FOREIGN KEY fk_sr_service;

	ALTER TABLE ServiceRequest
	DROP COLUMN ServiceID;

	CREATE TABLE ServiceRequestDetail (
	  DetailID       INT         NOT NULL AUTO_INCREMENT,
	  RequestID      INT         NOT NULL,
	  ServiceID      INT         NOT NULL,
	  PRIMARY KEY (DetailID),
	  KEY ix_srd_request (RequestID),
	  CONSTRAINT fk_srd_request
		FOREIGN KEY (RequestID) REFERENCES ServiceRequest(RequestID)
		ON UPDATE CASCADE ON DELETE CASCADE,
	  CONSTRAINT fk_srd_service
		FOREIGN KEY (ServiceID) REFERENCES Service_Type(ServiceID)
		ON UPDATE RESTRICT ON DELETE RESTRICT
	) ENGINE=InnoDB;


	CREATE TABLE DiagnosticPart (
		DiagnosticPartID INT NOT NULL AUTO_INCREMENT,
		VehicleDiagnosticID INT NOT NULL,
		PartDetailID INT NOT NULL,
		QuantityNeeded INT NOT NULL DEFAULT 1,
		UnitPrice DECIMAL(10,2) NOT NULL,
		PartCondition ENUM('REQUIRED', 'RECOMMENDED', 'OPTIONAL') NOT NULL DEFAULT 'REQUIRED',
		ReasonForReplacement VARCHAR(500) NULL COMMENT 'Lý do cần thay (ví dụ: "Brake pad worn 90%")',
		IsApproved TINYINT(1) DEFAULT 0 COMMENT 'Customer/Manager đã duyệt chưa',
		
		PRIMARY KEY (DiagnosticPartID),
		KEY ix_dp_diagnostic (VehicleDiagnosticID),
		KEY ix_dp_partdetail (PartDetailID),
		KEY ix_dp_condition (PartCondition),
		KEY ix_dp_approved (IsApproved),
		
		CONSTRAINT fk_dp_diagnostic
			FOREIGN KEY (VehicleDiagnosticID) 
			REFERENCES VehicleDiagnostic(VehicleDiagnosticID)
			ON UPDATE RESTRICT ON DELETE CASCADE,
			
		CONSTRAINT fk_dp_partdetail
			FOREIGN KEY (PartDetailID) 
			REFERENCES PartDetail(PartDetailID)
			ON UPDATE RESTRICT ON DELETE RESTRICT
	) ENGINE=InnoDB;
    
    ALTER TABLE VehicleDiagnostic
  MODIFY COLUMN Status ENUM('SUBMITTED','APPROVED','REJECTED')
  NOT NULL DEFAULT 'SUBMITTED';

CREATE INDEX ix_vd_status ON VehicleDiagnostic(Status);

-- Gắn WOP về DiagnosticPart để trace
ALTER TABLE WorkOrderPart
  ADD COLUMN DiagnosticPartID INT NULL AFTER PartDetailID,
  ADD CONSTRAINT fk_wop_diagpart
    FOREIGN KEY (DiagnosticPartID) REFERENCES DiagnosticPart(DiagnosticPartID)
    ON UPDATE RESTRICT ON DELETE SET NULL;

-- 1 VehicleDiagnostic chỉ đổ sang đúng 1 WorkOrderDetail (source=DIAGNOSTIC)
CREATE UNIQUE INDEX ux_wod_unique_diag
  ON WorkOrderDetail(diagnostic_id);

-- Không tạo trùng WOP theo cùng Detail & dòng diagnostic
CREATE UNIQUE INDEX ux_wop_detail_diagpart
  ON WorkOrderPart(DetailID, DiagnosticPartID);

-- (Tối ưu cho UI liệt kê các dòng đã duyệt)
CREATE INDEX ix_dp_vd_isapproved
  ON DiagnosticPart(VehicleDiagnosticID, IsApproved);
  
  DELIMITER $$

CREATE PROCEDURE SP_OnVehicleDiagnosticApproved(IN p_vd_id INT)
BEGIN
    DECLARE v_assignment_id INT;
    DECLARE v_detail_id_src INT;
    DECLARE v_work_order_id INT;
    DECLARE v_new_detail_id INT;
    DECLARE v_issue TEXT;
    DECLARE v_estimate DECIMAL(12,2);

    -- Lấy chain: VD -> Assignment -> Detail(REQUEST) -> WorkOrder
    SELECT AssignmentID, IssueFound, EstimateCost
      INTO v_assignment_id, v_issue, v_estimate
    FROM VehicleDiagnostic
    WHERE VehicleDiagnosticID = p_vd_id;

    SELECT DetailID INTO v_detail_id_src
    FROM TaskAssignment
    WHERE AssignmentID = v_assignment_id;

    SELECT WorkOrderID INTO v_work_order_id
    FROM WorkOrderDetail
    WHERE DetailID = v_detail_id_src;

    START TRANSACTION;

      /* 1) Tạo WorkOrderDetail (source=DIAGNOSTIC).
            Nhờ unique index ux_wod_unique_diag, nếu đã tồn tại sẽ bỏ qua. */
      INSERT IGNORE INTO WorkOrderDetail
        (WorkOrderID, source, diagnostic_id, approval_status, TaskDescription, EstimateAmount)
      VALUES
        (v_work_order_id, 'DIAGNOSTIC', p_vd_id, 'APPROVED', v_issue, v_estimate);

      -- Lấy DetailID đã tạo (hoặc có sẵn)
      IF ROW_COUNT() > 0 THEN
        SET v_new_detail_id = LAST_INSERT_ID();
      ELSE
        SELECT DetailID INTO v_new_detail_id
        FROM WorkOrderDetail
        WHERE diagnostic_id = p_vd_id
        LIMIT 1;
      END IF;

      /* 2) Sinh WorkOrderPart cho các dòng KH đã duyệt */
      INSERT IGNORE INTO WorkOrderPart
        (DetailID, PartDetailID, DiagnosticPartID, RequestedByID, QuantityUsed, UnitPrice, request_status, requested_at)
      SELECT
        v_new_detail_id,
        dp.PartDetailID,
        dp.DiagnosticPartID,
        -- ai request: tạm dùng TechManager của WO; nếu muốn khác thì đổi ở đây
        wo.TechManagerID,
        dp.QuantityNeeded,
        dp.UnitPrice,
        'PENDING',
        NOW()
      FROM DiagnosticPart dp
      JOIN WorkOrderDetail wod_src ON wod_src.DetailID = v_detail_id_src
      JOIN WorkOrder wo ON wo.WorkOrderID = wod_src.WorkOrderID
      WHERE dp.VehicleDiagnosticID = p_vd_id
        AND dp.IsApproved = 1;

    COMMIT;
END$$

DELIMITER ;
-- Trigger theo dõi Status
DELIMITER $$

CREATE TRIGGER trg_vd_on_status_approved
AFTER UPDATE ON VehicleDiagnostic
FOR EACH ROW
BEGIN
  -- Chỉ chạy khi chuyển sang APPROVED
  IF (OLD.Status <> 'APPROVED') AND (NEW.Status = 'APPROVED') THEN
      CALL SP_OnVehicleDiagnosticApproved(NEW.VehicleDiagnosticID);
  END IF;
END$$

DELIMITER ;