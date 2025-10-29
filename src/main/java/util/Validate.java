package util;

import common.constant.IConstant;

import java.util.regex.Pattern;
import java.util.Arrays;
import java.util.List;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Lớp tiện ích (utility class) chứa các phương thức static để validate.
 * Class này implement IConstant để có thể sử dụng trực tiếp các hằng số.
 * Nó biên dịch các hằng số String Regex thành các đối tượng Pattern
 * để tối ưu hiệu suất.
 */
public final class Validate implements IConstant {
    private static final Pattern EMAIL_REGEX = Pattern.compile(EMAIL_PATTERN);
    private static final Pattern PHONE_REGEX = Pattern.compile(PHONE_PATTERN);
    private static final Pattern USERNAME_REGEX = Pattern.compile(USERNAME_PATTERN);
    private static final Pattern EMPLOYEE_CODE_REGEX = Pattern.compile(EMPLOYEE_CODE_PATTERN);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT);
    private static final List<String> VALID_GENDERS = Arrays.asList("Nam", "Nữ", "Khác");

    /**
     * Private constructor để ngăn chặn việc khởi tạo class này.
     */
    private Validate() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }


    /**
     * Kiểm tra xem một chuỗi có rỗng (null hoặc chỉ chứa khoảng trắng) hay không.
     * (Đây là phương thức bị thiếu gây ra lỗi của bạn)
     */
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * Kiểm tra độ dài tối đa của một chuỗi.
     */
    public static boolean isLengthValid(String str, int maxLength) {
        if (isNullOrEmpty(str)) {
            return true; // Chuỗi rỗng luôn hợp lệ về độ dài
        }
        return str.length() <= maxLength;
    }

    /**
     * Chuyển đổi một chuỗi sang Integer một cách an toàn.
     */
    public static Integer parseInteger(String param) {
        if (isNullOrEmpty(param)) {
            return null;
        }
        try {
            return Integer.parseInt(param.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Chuyển đổi một chuỗi sang Double một cách an toàn.
     * Tự động xử lý cả dấu ',' (kiểu VN) và '.' (kiểu US).
     */
    public static Double parseDouble(String param) {
        if (isNullOrEmpty(param)) {
            return null;
        }
        try {
            String cleanParam = param.trim().replace(",", ".");
            return Double.parseDouble(cleanParam);
        } catch (NumberFormatException e) {
            return null;
        }
    }
    /**
     * Kiểm tra xem một số Integer có là số dương (> 0).
     */
    public static boolean isPositive(Integer number) {
        return number != null && number > 0;
    }

    /**
     * Kiểm tra xem một số Double có là số không âm (>= 0).
     */
    public static boolean isNonNegative(Double number) {
        return number != null && number >= 0;
    }

    /**
     * Kiểm tra định dạng email (bắt buộc).
     * @return false nếu rỗng hoặc sai định dạng.
     */
    public static boolean isValidEmail(String email) {
        if (isNullOrEmpty(email)) {
            return false; // Email là bắt buộc
        }
        return EMAIL_REGEX.matcher(email).matches();
    }

    /**
     * Kiểm tra định dạng username (bắt buộc).
     * @return false nếu rỗng hoặc sai định dạng (không khớp regex).
     */
    public static boolean isValidUsername(String username) {
        if (isNullOrEmpty(username)) {
            return false; // Username là bắt buộc
        }
        return USERNAME_REGEX.matcher(username).matches();
    }

    /**
     * Kiểm tra số điện thoại (tùy chọn).
     * @return true nếu rỗng HOẶC đúng định dạng.
     */
    public static boolean isValidPhoneNumber(String phoneNumber) {
        if (isNullOrEmpty(phoneNumber)) {
            return true; // SĐT là tùy chọn
        }
        return PHONE_REGEX.matcher(phoneNumber).matches();
    }

    /**
     * Kiểm tra mã nhân viên (tùy chọn).
     * @return true nếu rỗng HOẶC đúng định dạng.
     */
    public static boolean isValidEmployeeCode(String code) {
        if (isNullOrEmpty(code)) {
            return true; // Mã NV là tùy chọn
        }
        return EMPLOYEE_CODE_REGEX.matcher(code).matches();
    }

    /**
     * Kiểm tra giá trị gender (cho phép rỗng/tùy chọn).
     * @return true nếu rỗng HOẶC nằm trong danh sách VALID_GENDERS.
     */
    public static boolean isValidGender(String gender) {
        if (isNullOrEmpty(gender)) {
            return true; // Giới tính là tùy chọn
        }
        return VALID_GENDERS.contains(gender);
    }

    /**
     * Kiểm tra Ngày tháng năm sinh (DOB).
     * (Phương thức bạn đã cung cấp)
     * @param dateStr Chuỗi ngày tháng (ví dụ: "1990-10-30").
     * @return true nếu rỗng (coi là tùy chọn) HOẶC là ngày hợp lệ VÀ không ở tương lai.
     * false nếu sai định dạng HOẶC là ngày trong tương lai.
     */
    public static boolean isValidDateOfBirth(String dateStr) {
        // 1. Coi ngày sinh là tùy chọn. Nếu rỗng -> hợp lệ.
        if (isNullOrEmpty(dateStr)) {
            return true;
        }

        try {
            LocalDate dob = LocalDate.parse(dateStr, DATE_FORMATTER);

            LocalDate today = LocalDate.now();
            if (dob.isAfter(today)) {
                return false;
            }
            return true;

        } catch (DateTimeParseException e) {
            return false;
        }
    }
}