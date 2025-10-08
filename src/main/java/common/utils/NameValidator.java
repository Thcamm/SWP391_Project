package common.utils;

import dao.rbac.RoleDao;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class NameValidator {
    private NameValidator() {
    }

    private static final String DISPLAY_NAME_REGEX = "^[\\p{L}\\p{N}][\\p{L}\\p{N} _-]*$";
    private static final int DISPLAY_NAME_MIN = 2;
    private static final int DISPLAY_NAME_MAX = 50;

    private static final String PERM_CODE_REGEX = "^[a-z][a-z0-9_]{2,63}$";
    private static final int PERM_CODE_MIN = 3;
    private static final int PERM_CODE_MAX = 64;


    public static String normalizeDisplayName(String input){
        if(input == null || input.length() <= DISPLAY_NAME_MIN){
            return null;
        }

        return input.replaceAll("\\s+", " ").trim();
    }

    public static String normalizePermCode(String input){
        if(input == null || input.length() <= PERM_CODE_MIN){
            return null;
        }

        return input.trim().toLowerCase();
    }

    public static final class ValidationResult{
        public final boolean valid;
        public final String normalizedValue;
        public final List<String> errors;

        public ValidationResult(boolean valid, String normalizedValue, List<String> errors) {
            this.valid = valid;
            this.normalizedValue = normalizedValue;
            this.errors = errors;
        }

        public static ValidationResult valid(String value){
            return new ValidationResult(true, value, List.of());

        }

        public static ValidationResult invalid(String value, List<String> errors){
            return new ValidationResult(false, value, errors);
        }


    }

    public static ValidationResult validateDisplayName(String rawName, Function<String, Boolean> existsIgnoreCase, Integer excludeId){
        String name = normalizeDisplayName(rawName);
        List<String> errors = new ArrayList<>();

        if(name == null || name.isBlank()){
            errors.add("Name cannot be null or blank");
        } else {
            if(name.length() < DISPLAY_NAME_MIN || name.length() > DISPLAY_NAME_MAX){
                errors.add("Name must be between " + DISPLAY_NAME_MIN + " and " + DISPLAY_NAME_MAX + " characters");
            }

            if(!name.matches(DISPLAY_NAME_REGEX)){
                errors.add("Name contains invalid characters");
            }
        }

        if(errors.isEmpty() && existsIgnoreCase != null){
            if(Boolean.TRUE.equals(existsIgnoreCase.apply(rawName))){

                errors.add("Name already exists (case-insensitive)");
            }
        }

        return errors.isEmpty() ? ValidationResult.valid(rawName) : ValidationResult.invalid(rawName, errors);
    }


    public static ValidationResult validatePermCode(
            String rawCode,
            Function<String, Boolean> existsIgnoreCase){
        String code = normalizePermCode(rawCode);
        List<String> errors = new ArrayList<>();

        if(code == null || code.isBlank()){
            errors.add("Code cannot be null or blank");
        } else {
            if(code.length() < PERM_CODE_MIN || code.length() > PERM_CODE_MAX){
                errors.add("Code must be between " + PERM_CODE_MIN + " and " + PERM_CODE_MAX + " characters");
            }

            if(!code.matches(PERM_CODE_REGEX)){
                errors.add("Code contains invalid characters");
            }


        }

        if(errors.isEmpty() && existsIgnoreCase != null){
            if(Boolean.TRUE.equals(existsIgnoreCase.apply(code))){
                errors.add("Code already exists");
            }
        }

        return errors.isEmpty() ? ValidationResult.valid(code) : ValidationResult.invalid(code, errors);
    }

}
