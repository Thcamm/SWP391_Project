package util;

import common.constant.IConstant;

import java.util.regex.Pattern;
import java.util.Arrays;
import java.util.List;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Utility class that contains static validation methods.
 * This class implements IConstant to allow direct use of constant values.
 * Regular expression string constants are compiled into Pattern instances
 * to improve performance.
 */
public final class Validate implements IConstant {
    private static final Pattern EMAIL_REGEX = Pattern.compile(EMAIL_PATTERN);
    private static final Pattern PHONE_REGEX = Pattern.compile(PHONE_PATTERN);
    private static final Pattern USERNAME_REGEX = Pattern.compile(USERNAME_PATTERN);
    private static final Pattern EMPLOYEE_CODE_REGEX = Pattern.compile(EMPLOYEE_CODE_PATTERN);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT);
    private static final List<String> VALID_GENDERS = Arrays.asList("Male", "Female", "Other");

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private Validate() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }


    /**
     * Check whether a string is null or empty (only whitespace).
     * (This helper was missing and caused errors in your code previously)
     */
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * Validate maximum length of a string.
     */
    public static boolean isLengthValid(String str, int maxLength) {
        if (isNullOrEmpty(str)) {
            return true; // An empty string is considered valid in terms of length
        }
        return str.length() <= maxLength;
    }

    /**
     * Safely parse a string to Integer.
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
     * Safely parse a string to Double.
     * Handles both ',' (Vietnamese style) and '.' (US style) decimal separators.
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
     * Check whether an Integer is positive (> 0).
     */
    public static boolean isPositive(Integer number) {
        return number != null && number > 0;
    }

    /**
     * Check whether a Double is non-negative (>= 0).
     */
    public static boolean isNonNegative(Double number) {
        return number != null && number >= 0;
    }

    /**
     * Validate email format (required).
     * @return false if empty or not matching the pattern.
     */
    public static boolean isValidEmail(String email) {
        if (isNullOrEmpty(email)) {
            return false; // Email is required
        }
        return EMAIL_REGEX.matcher(email).matches();
    }

    /**
     * Validate username format (required).
     * @return false if empty or does not match the regex.
     */
    public static boolean isValidUsername(String username) {
        if (isNullOrEmpty(username)) {
            return false; // Username is required
        }
        return USERNAME_REGEX.matcher(username).matches();
    }

    /**
     * Validate phone number (optional).
     * @return true if empty OR matches the phone pattern.
     */
    public static boolean isValidPhoneNumber(String phoneNumber) {
        if (isNullOrEmpty(phoneNumber)) {
            return true; // Phone number is optional
        }
        return PHONE_REGEX.matcher(phoneNumber).matches();
    }

    /**
     * Validate employee code (optional).
     * @return true if empty OR matches the employee code pattern.
     */
    public static boolean isValidEmployeeCode(String code) {
        if (isNullOrEmpty(code)) {
            return true; // Employee code is optional
        }
        return EMPLOYEE_CODE_REGEX.matcher(code).matches();
    }

    /**
     * Validate gender value (allows empty/optional).
     * @return true if empty OR contained in VALID_GENDERS list.
     */
    public static boolean isValidGender(String gender) {
        if (isNullOrEmpty(gender)) {
            return true; // Gender is optional
        }
        return VALID_GENDERS.contains(gender);
    }

    /**
     * Validate date of birth (DOB).
     * If empty -> considered valid (optional). Otherwise the date must parse
     * according to the configured DATE_FORMAT and must not be in the future.
     *
     * @param dateStr Date string (e.g. "1990-10-30").
     * @return true if empty OR a valid date not in the future; false if parsing fails or the date is in the future.
     */
    public static boolean isValidDateOfBirth(String dateStr) {
        // Treat DOB as optional. If empty -> valid.
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