package service.vehicle;

import dao.vehicle.VehicleDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import service.vehicle.VehicleService;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VehicleServiceTest {

    @Mock
    private VehicleDAO mockVehicleDAO;

    private VehicleService vehicleService;

    @BeforeEach
    void setUp() {
        vehicleService = new VehicleService(mockVehicleDAO);

        // Precondition setup for isLicensePlateTaken (UID10-14)
        try {
            lenient().when(mockVehicleDAO.getAllLicensePlates())
                    .thenReturn(List.of("30A-111", "29B-222"));

            lenient().when(mockVehicleDAO.getVehicleIdByLicensePlate("30A-111"))
                    .thenReturn(1);

            lenient().when(mockVehicleDAO.getVehicleIdByLicensePlate("29B-222"))
                    .thenReturn(2);

        } catch (SQLException e) {
            fail("Failed to setup mocks: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("UID1 (N): Normalize '30A-123.45'")
    void testUID01() {
        assertEquals("30A12345", vehicleService.normalizeLicensePlate("30A-123.45"));
    }

    @Test
    @DisplayName("UID2 (N): Normalize '29b 55566'")
    void testUID02() {
        assertEquals("29B55566", vehicleService.normalizeLicensePlate("29b 55566"));
    }

    @Test
    @DisplayName("UID3 (B): Normalize null")
    void testUID03() {
        assertNull(vehicleService.normalizeLicensePlate(null));
    }

    @Test
    @DisplayName("UID4 (N): Validate '30A-12345'")
    void testUID04() {
        assertTrue(vehicleService.validateLicensePlateFormat("30A-12345"));
    }

    @Test
    @DisplayName("UID5 (A): Validate '123456")
    void testUID05() {
        assertFalse(vehicleService.validateLicensePlateFormat("123456"));
    }

    @Test
    @DisplayName("UID6 (A): Validate 'ABCDEFG'")
    void testUID06() {
        assertFalse(vehicleService.validateLicensePlateFormat("ABCDEFG"));
    }

    @Test
    @DisplayName("UID7 (B): Validate '30A'")
    void testUID07() {
        assertFalse(vehicleService.validateLicensePlateFormat("30A"));
    }

    @Test
    @DisplayName("UID8 (B): Validate '30A1234567890'")
    void testUID08() {
        assertFalse(vehicleService.validateLicensePlateFormat("30A1234567890"));
    }

    @Test
    @DisplayName("UID9 (B): Validate null")
    void testUID09() {
        assertFalse(vehicleService.validateLicensePlateFormat(null));
    }


    @Test
    @DisplayName("UID10 (N): Add new - '99C-333'")
    void testUID10() throws Exception {
        assertFalse(vehicleService.isLicensePlateTaken("99C-333", -1));
    }

    @Test
    @DisplayName("UID11 (A): Add new - '30A.111'")
    void testUID11() throws Exception {
        assertTrue(vehicleService.isLicensePlateTaken("30A.111", -1));
    }

    @Test
    @DisplayName("UID12 (A): Update ID 1 - Check '29B 222'")
    void testUID12() throws Exception {
        assertTrue(vehicleService.isLicensePlateTaken("29B 222", 1));
    }

    @Test
    @DisplayName("UID13 (N): Update ID 1 - Check '30A-111'")
    void testUID13() throws Exception {
        assertFalse(vehicleService.isLicensePlateTaken("30A-111", 1));
    }

    @Test
    @DisplayName("UID14 (B): BUG - Input 'null'")
    void testUID14() {
        assertThrows(NullPointerException.class, () -> {
            vehicleService.isLicensePlateTaken(null, 1);
        });
    }
}