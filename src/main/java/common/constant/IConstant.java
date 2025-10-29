package common.constant;

public interface IConstant {
    /**
     * Regex cho email.
     * Chấp nhận các ký tự chuẩn, theo sau là @, và một domain hợp lệ.
     */
    public final static String EMAIL_PATTERN = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";

    /**
     * Regex cho số điện thoại Việt Nam (10-11 số, bắt đầu bằng 0).
     */
    public final static String PHONE_PATTERN = "^(0\\d{9,10})$";
    /**
     * (MỚI) Regex cho username: 3-20 ký tự, chỉ chữ, số, và gạch dưới.
     * Không cho phép khoảng trắng.
     */
    public final static String USERNAME_PATTERN = "^[a-zA-Z0-9_]{3,20}$";

    /**
     * (MỚI) Regex cho mã nhân viên (tùy chọn): 2-10 ký tự.
     * Cho phép chữ, số, và dấu gạch nối (ví dụ: NV-001, SALE10).
     */
    public final static String EMPLOYEE_CODE_PATTERN = "^[a-zA-Z0-9-]{2,10}$";

    /**
     * (MỚI) Độ dài tối đa cho Tên đầy đủ.
     */
    int MAX_FULLNAME_LENGTH = 100;

    /**
     * (MỚI) Định dạng ngày tháng chuẩn (ISO 8601).
     */
    String DATE_FORMAT = "yyyy-MM-dd";
}
