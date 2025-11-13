package model.customer;

import model.inventory.DiagnosticPart;
import model.vehicle.VehicleDiagnostic;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class CustomerDiagnosticsView {


    public int customerId;

    public int serviceRequestId;

    public int workOrderId;

    public RequestInfo request;

    public VehicleInfo vehicle;


    public final List<ServiceTypeInfo> requestedServices = new ArrayList<>();


    public final List<ServiceBlock> services = new ArrayList<>();

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getServiceRequestId() {
        return serviceRequestId;
    }

    public void setServiceRequestId(int serviceRequestId) {
        this.serviceRequestId = serviceRequestId;
    }

    public int getWorkOrderId() {
        return workOrderId;
    }

    public void setWorkOrderId(int workOrderId) {
        this.workOrderId = workOrderId;
    }

    public RequestInfo getRequest() {
        return request;
    }

    public void setRequest(RequestInfo request) {
        this.request = request;
    }

    public VehicleInfo getVehicle() {
        return vehicle;
    }

    public void setVehicle(VehicleInfo vehicle) {
        this.vehicle = vehicle;
    }

    public List<ServiceTypeInfo> getRequestedServices() {
        return requestedServices;
    }

    public List<ServiceBlock> getServices() {
        return services;
    }

    public static class RequestInfo {
        public int requestId;
        public LocalDateTime requestDate;
        public String status;     // PENDING / APPROVE / REJECTED
        public String note;

        public int getRequestId() {
            return requestId;
        }

        public void setRequestId(int requestId) {
            this.requestId = requestId;
        }

        public LocalDateTime getRequestDate() {
            return requestDate;
        }

        public void setRequestDate(LocalDateTime requestDate) {
            this.requestDate = requestDate;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getNote() {
            return note;
        }

        public void setNote(String note) {
            this.note = note;
        }
        // ServiceRequest.Note
    }


    public static class VehicleInfo {
        public int vehicleId;
        public String licensePlate;
        public String brand;
        public String model;
        public Integer yearManufacture;


        public int getVehicleId() {
            return vehicleId;
        }

        public void setVehicleId(int vehicleId) {
            this.vehicleId = vehicleId;
        }

        public String getLicensePlate() {
            return licensePlate;
        }

        public void setLicensePlate(String licensePlate) {
            this.licensePlate = licensePlate;
        }

        public String getBrand() {
            return brand;
        }

        public void setBrand(String brand) {
            this.brand = brand;
        }

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }

        public Integer getYearManufacture() {
            return yearManufacture;
        }

        public void setYearManufacture(Integer yearManufacture) {
            this.yearManufacture = yearManufacture;
        }
    }


    public static class ServiceTypeInfo {
        public int serviceId;
        public String serviceName;  // Service_Type.ServiceName
        public String category;
        public double unitPrice;

        public int getServiceId() {
            return serviceId;
        }

        public void setServiceId(int serviceId) {
            this.serviceId = serviceId;
        }

        public String getServiceName() {
            return serviceName;
        }

        public void setServiceName(String serviceName) {
            this.serviceName = serviceName;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public double getUnitPrice() {
            return unitPrice;
        }

        public void setUnitPrice(double unitPrice) {
            this.unitPrice = unitPrice;
        }
    }

    public static class ServiceBlock {


        public int detailId;


        public String serviceLabel;

        public final List<DiagnosticRow> diagnostics = new ArrayList<>();

        public int getDetailId() {
            return detailId;
        }

        public void setDetailId(int detailId) {
            this.detailId = detailId;
        }

        public String getServiceLabel() {
            return serviceLabel;
        }

        public void setServiceLabel(String serviceLabel) {
            this.serviceLabel = serviceLabel;
        }

        public List<DiagnosticRow> getDiagnostics() {
            return diagnostics;
        }
    }


    public static class DiagnosticRow {


        public VehicleDiagnostic diagnostic;
        
        public List<DiagnosticPart> parts = new ArrayList<>();

        public VehicleDiagnostic getDiagnostic() {
            return diagnostic;
        }

        public void setDiagnostic(VehicleDiagnostic diagnostic) {
            this.diagnostic = diagnostic;
        }

        public List<DiagnosticPart> getParts() {
            return parts;
        }

        public void setParts(List<DiagnosticPart> parts) {
            this.parts = parts;
        }
    }
}
