<%@ page import="model.user.User" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Reschedule Appointment</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/customer/reschedule-appointment.css">
</head>
<body>
<jsp:include page="/common/header.jsp" />
<jsp:include page="/view/customerservice/result.jsp"/>
<main class="appointment-section">
    <div class="container">
        <div class="row justify-content-center">
            <div class="col-lg-8">
                <div class="appointment-form">
                    <h2 class="mb-4" style="font-size: 32px; font-weight: 700; color: #000;">
                        RESCHEDULE APPOINTMENT
                    </h2>

                    <form action="${pageContext.request.contextPath}/customer/reschedule-appointment" method="post">
                        <!-- Hidden: appointmentID -->
                        <input type="hidden" name="appointmentID" value="${appointment.appointmentID}"/>

                        <!-- Customer info (readonly) -->
                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <label class="form-label">Full Name</label>
                                <input type="text" class="form-control" value="${customer.fullName}" readonly>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <label class="form-label">Email</label>
                                <input type="text" class="form-control" value="${customer.email}" readonly>
                            </div>
                            <div class="col-md-6 mb-3">
                                <label class="form-label">Phone Number</label>
                                <input type="text" class="form-control" value="${customer.phoneNumber}" readonly>
                            </div>
                        </div>

                        <!-- Current appointment -->
                        <div class="mb-3">
                            <label class="form-label">Current Appointment Date</label>
                            <input type="text" class="form-control" value="${appointment.appointmentDate}" readonly>
                        </div>

                        <!-- New appointment -->
                        <div class="mb-3">
                            <label for="newAppointmentDate" class="form-label">
                                New Appointment Date <span class="required">*</span>
                            </label>
                            <input type="text" class="form-control" id="newAppointmentDate"
                                   name="newAppointmentDate" placeholder="Select date and time" required>
                        </div>

                        <!-- Description -->
                        <div class="mb-4">
                            <label for="description" class="form-label">Description</label>
                            <textarea class="form-control" id="description" name="description"
                                      rows="4" maxlength="300"
                                      placeholder="Enter details about your vehicle issue (if any)">${appointment.description}</textarea>
                        </div>

                        <!-- Submit -->
                        <div class="text-center">
                            <button type="submit" class="btn btn-submit">
                                <i class="fas fa-paper-plane"></i> Reschedule
                            </button>
                        </div>
                    </form>

                </div>
            </div>
        </div>
    </div>
</main>

<jsp:include page="/common/footer.jsp" />

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/flatpickr/dist/flatpickr.min.css">
<script src="https://cdn.jsdelivr.net/npm/flatpickr"></script>
<script>
    // Existing appointments (from server)
    const existingAppointments = [
        <c:forEach var="apm" items="${appointments}">
        "${apm.appointmentDate}",
        </c:forEach>
    ];

    // Flatpickr setup
    flatpickr("#newAppointmentDate", {
        enableTime: true,
        dateFormat: "Y-m-d H:i",
        minDate: "today", // no past dates
        time_24hr: true,
        minuteIncrement: 30,
        onChange: function(selectedDates, dateStr, instance) {
            if (selectedDates.length > 0) {
                const selected = selectedDates[0];
                const hour = selected.getHours();

                // Only allow 8:00 - 17:00
                if (hour < 8 || hour > 17) {
                    alert("Appointments are only available between 08:00 and 17:00.");
                    instance.clear();
                    return;
                }

                // Prevent duplicate appointments
                const isoStr = selected.toISOString().slice(0,16);
                if (existingAppointments.some(apm => apm.startsWith(isoStr))) {
                    alert("This time slot is already booked. Please choose another.");
                    instance.clear();
                }
            }
        }
    });

    // Description validation
    const desc = document.getElementById("description");
    desc.addEventListener("input", function() {
        if (this.value.length > 300) {
            alert("Description cannot exceed 300 characters.");
            this.value = this.value.substring(0, 300);
        }
    });
</script>

</body>
</html>
