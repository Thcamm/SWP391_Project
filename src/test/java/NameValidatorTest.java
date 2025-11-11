

import common.utils.NameValidator;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

class NameValidatorTest {


    private static String repeat(char c, int n) {
        return String.valueOf(c).repeat(n);
    }

    // ------------ normalizeDisplayName ------------
    @Test
    void normalizeDisplayName_null_or_len_le_min_returns_null() {

        assertNull(NameValidator.normalizeDisplayName(null));
        assertNull(NameValidator.normalizeDisplayName(""));
        assertNull(NameValidator.normalizeDisplayName("a"));
        assertNull(NameValidator.normalizeDisplayName("ab"));
    }

    @Test
    void normalizeDisplayName_collapse_spaces_and_trim() {
        String in  = "   John    Doe   ";
        String out = NameValidator.normalizeDisplayName(in);
        assertEquals("John Doe", out);
    }

    // ------------ validateDisplayName ------------
    @Test
    void validateDisplayName_null_or_blank_fails() {
        NameValidator.ValidationResult r1 =
                NameValidator.validateDisplayName(null, nm -> false, null);
        assertFalse(r1.valid);
        assertTrue(r1.errors.contains("Name cannot be null or blank"));

        NameValidator.ValidationResult r2 =
                NameValidator.validateDisplayName("   ", nm -> false, null);
        assertFalse(r2.valid);
        assertTrue(r2.errors.contains("Name cannot be null or blank"));
    }

    @Test
    void validateDisplayName_length_out_of_range_fails() {
        String tooLong = repeat('A', 51);
        NameValidator.ValidationResult r =
                NameValidator.validateDisplayName(tooLong, nm -> false, null);
        assertFalse(r.valid);
        assertTrue(r.errors.stream().anyMatch(s -> s.contains("between")));
    }

    @Test
    void validateDisplayName_invalid_chars_fails() {
        NameValidator.ValidationResult r =
                NameValidator.validateDisplayName("A$B", nm -> false, null);
        assertFalse(r.valid);
        assertTrue(r.errors.contains("Name contains invalid characters"));
    }

    @Test
    void validateDisplayName_duplicate_fails_when_existsIgnoreCase_true() {
        Function<String, Boolean> exists = nm -> true;
        NameValidator.ValidationResult r =
                NameValidator.validateDisplayName("Alice", exists, null);
        assertFalse(r.valid);
        assertTrue(r.errors.contains("Name already exists (case-insensitive)"));
    }

    @Test
    void validateDisplayName_valid_passes_and_normalizes_spaces() {

        NameValidator.ValidationResult r =
                NameValidator.validateDisplayName("  Alice   Bob  ", nm -> false, null);
        assertTrue(r.valid);
        assertEquals("Alice Bob", r.normalizedValue);
        assertEquals(List.of(), r.errors);
    }

    // ------------ normalizePermCode ------------
    @Test
    void normalizePermCode_null_or_len_le_min_returns_null() {

        assertNull(NameValidator.normalizePermCode(null));
        assertNull(NameValidator.normalizePermCode(""));     // 0
        assertNull(NameValidator.normalizePermCode("a"));    // 1
        assertNull(NameValidator.normalizePermCode("ab"));   // 2
        assertNull(NameValidator.normalizePermCode("abc"));  // 3 (biên) -> vẫn null theo code hiện tại
    }

    @Test
    void normalizePermCode_trims_and_lowercases() {
        String out = NameValidator.normalizePermCode("  AbC_X  ");
        assertEquals("abc_x", out);
    }

    // ------------ validatePermCode ------------
    @Test
    void validatePermCode_null_or_blank_fails() {
        NameValidator.ValidationResult r1 =
                NameValidator.validatePermCode(null, nm -> false);
        assertFalse(r1.valid);
        assertTrue(r1.errors.contains("Code cannot be null or blank"));

        NameValidator.ValidationResult r2 =
                NameValidator.validatePermCode("   ", nm -> false);
        assertFalse(r2.valid);
        assertTrue(r2.errors.contains("Code cannot be null or blank"));
    }

    @Test
    void validatePermCode_length_out_of_range_or_pattern_fails() {

        NameValidator.ValidationResult rShort =
                NameValidator.validatePermCode("ab", nm -> false);
        assertFalse(rShort.valid);
        assertTrue(rShort.errors.contains("Code cannot be null or blank"));


        NameValidator.ValidationResult rBadStartDigit =
                NameValidator.validatePermCode("1abc", nm -> false);
        assertFalse(rBadStartDigit.valid);
        assertTrue(rBadStartDigit.errors.contains("Code contains invalid characters"));

        // sai pattern: có dấu '-'
        NameValidator.ValidationResult rBadDash =
                NameValidator.validatePermCode("a-bc", nm -> false);
        assertFalse(rBadDash.valid);
        assertTrue(rBadDash.errors.contains("Code contains invalid characters"));
    }

    @Test
    void validatePermCode_duplicate_fails_when_exists_true() {
        NameValidator.ValidationResult r =
                NameValidator.validatePermCode("abc_xyz", nm -> true);
        assertFalse(r.valid);
        assertTrue(r.errors.contains("Code already exists"));
    }

    @Test
    void validatePermCode_valid_passes_and_normalizes() {
        NameValidator.ValidationResult r =
                NameValidator.validatePermCode("  Abc_xyz  ", nm -> false);
        assertTrue(r.valid);
        assertEquals("abc_xyz", r.normalizedValue); // đã toLowerCase + trim
        assertTrue(r.errors.isEmpty());
    }
}
