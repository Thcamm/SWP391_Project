package common.constant;

import common.message.SystemMessage;

public class MessageConstants {

    public static final SystemMessage MSG001 = new SystemMessage(
            "MSG001",
            MessageType.INLINE,
            "There is not any search result",
            "No search results."
    );

    public static final SystemMessage MSG002 = new SystemMessage(
            "MSG002",
            MessageType.VALIDATION,
            "Input-required fields are empty",
            "The * field is required."
    );

    public static final SystemMessage MSG003 = new SystemMessage(
            "MSG003",
            MessageType.SUCCESS,
            "Updating asset(s) information successfully",
            "Update asset(s) successfully."
    );

    public static final SystemMessage MSG004 = new SystemMessage(
            "MSG004",
            MessageType.SUCCESS,
            "Adding new asset successfully",
            "Add asset successfully."
    );

    public static final SystemMessage MSG005 = new SystemMessage(
            "MSG005",
            MessageType.SUCCESS,
            "Confirming email of asset hand-over is sent successfully",
            "A confirmation email has been sent to {email_address}."
    );

    public static final SystemMessage MSG006 = new SystemMessage(
            "MSG006",
            MessageType.SUCCESS,
            "Resetting asset information successfully",
            "Return asset(s) successfully."
    );

    public static final SystemMessage MSG007 = new SystemMessage(
            "MSG007",
            MessageType.SUCCESS,
            "Deleting asset information successfully",
            "Delete asset(s) successfully."
    );

    public static final SystemMessage MSG008 = new SystemMessage(
            "MSG008",
            MessageType.VALIDATION,
            "Input value length > max length",
            "Exceed max length of {max_length}."
    );

    public static final SystemMessage MSG009 = new SystemMessage(
            "MSG009",
            MessageType.INLINE,
            "Username or password is not correct when clicking sign-in",
            "Incorrect user name or password. Please check again."
    );

    public static final SystemMessage AUTH001 = new SystemMessage(
            "AUTH001",
            MessageType.ERROR,
            "User is not authenticated",
            "Please login to continue."
    );

    public static final SystemMessage AUTH002 = new SystemMessage(
            "AUTH002",
            MessageType.ERROR,
            "User does not have permission",
            "You don't have permission to access this resource."
    );

    public static final SystemMessage AUTH003 = new SystemMessage(
            "AUTH003",
            MessageType.SUCCESS,
            "Login successful",
            "Welcome back!"
    );

    public static final SystemMessage AUTH004 = new SystemMessage(
            "AUTH004",
            MessageType.SUCCESS,
            "Logout successful",
            "You have been logged out successfully."
    );

    public static final SystemMessage TASK001 = new SystemMessage(
            "TASK001",
            MessageType.SUCCESS,
            "Task accepted successfully",
            "Task accepted successfully!"
    );

    public static final SystemMessage TASK002 = new SystemMessage(
            "TASK002",
            MessageType.SUCCESS,
            "Task rejected successfully",
            "Task rejected successfully."
    );

    public static final SystemMessage TASK003 = new SystemMessage(
            "TASK003",
            MessageType.SUCCESS,
            "Task progress updated successfully",
            "Progress updated successfully!"
    );

    public static final SystemMessage TASK004 = new SystemMessage(
            "TASK004",
            MessageType.SUCCESS,
            "Task completed successfully",
            "Task completed successfully!"
    );

    public static final SystemMessage TASK005 = new SystemMessage(
            "TASK005",
            MessageType.ERROR,
            "Failed to update task",
            "Failed to update task. Please try again."
    );

    public static final SystemMessage TASK006 = new SystemMessage(
            "TASK006",
            MessageType.INLINE,
            "No tasks assigned",
            "No tasks assigned to you at the moment."
    );

    public static final SystemMessage TASK007 = new SystemMessage(
            "TASK007",
            MessageType.VALIDATION,
            "Invalid progress percentage",
            "Progress must be between 0 and 100."
    );

    public static final SystemMessage TASK008 = new SystemMessage(
            "TASK008",
            MessageType.VALIDATION,
            "Task notes required",
            "Please provide notes for this action."
    );

    public static final SystemMessage TASK009 = new SystemMessage(
            "TASK009",
            MessageType.VALIDATION,
            "Task permission required",
            "You do not have permission to modify this task."
    );

    public static final SystemMessage TASK010 = new SystemMessage(
            "TASK010",
            MessageType.VALIDATION,
            "Task update",
            "Can only update tasks that are in progress."
    );

    public static final SystemMessage TASK011 = new SystemMessage(
            "TASK011",
            MessageType.VALIDATION,
            "Task permission required",
            "You dont have permission to create diagnostic for this task. "
    );

    public static final SystemMessage TASK012 = new SystemMessage(
            "TASK012",
            MessageType.VALIDATION,
            "Task status required",
            "Task is waiting for customer response"
    );

    public static final SystemMessage TASK013 = new SystemMessage(
            "TASK013",
            MessageType.VALIDATION,
            "Task status required",
            "You haved task in this time. It will be overlap with another task."
    );

    public static final SystemMessage TASK014 = new SystemMessage(
            "TASK013",
            MessageType.INFO,
            "Data field required",
            "Data field is null. Required"
    );

    public static final SystemMessage TASK015 = new SystemMessage(
            "TASK015",
            MessageType.INFO,
            "Time limit",
            "Over 10 minutes to accept"
    );


    public static final SystemMessage PART001 = new SystemMessage(
            "PART001",
            MessageType.SUCCESS,
            "Parts requested successfully",
            "Parts requested successfully!"
    );

    public static final SystemMessage PART002 = new SystemMessage(
            "PART002",
            MessageType.INFO,
            "Parts are available",
            "Parts are available. You can pick them up."
    );

    public static final SystemMessage PART003 = new SystemMessage(
            "PART003",
            MessageType.WARNING,
            "Parts are pending",
            "Parts request is pending approval."
    );

    public static final SystemMessage DIAG001 = new SystemMessage(
            "DIAG001",
            MessageType.SUCCESS,
            "Diagnostic created successfully",
            "Diagnostic report created successfully!"
    );

    public static final SystemMessage DIAG002 = new SystemMessage(
            "DIAG002",
            MessageType.SUCCESS,
            "Diagnostic updated successfully",
            "Diagnostic report updated successfully!"
    );




    public static final SystemMessage DIAG003 = new SystemMessage(
            "DIAG003",
            MessageType.SUCCESS,
            "Diagnostic task details loaded",
            "Diagnostics page loaded!"
    );

    public static final SystemMessage DIAG004 = new SystemMessage(
            "DIAG004",
            MessageType.ERROR,
            "Diagnostic updated failed",
            "Diagnostic report updated failed!"
    );
    public static final SystemMessage DIAG005 = new SystemMessage(
            "DIAG005",
            MessageType.ERROR,
            "Diagnostic updated failed",
            "Diagnostic is aprroved not updated!"
    );

    public static final SystemMessage ERR001 = new SystemMessage(
            "ERR001",
            MessageType.ERROR,
            "Database connection error",
            "System error. Please try again later."
    );

    public static final SystemMessage ERR002 = new SystemMessage(
            "ERR002",
            MessageType.ERROR,
            "Data not found",
            "The requested data was not found."
    );

    public static final SystemMessage ERR003 = new SystemMessage(
            "ERR003",
            MessageType.ERROR,
            "Invalid input data",
            "Invalid input. Please check your data."
    );

    public static final SystemMessage VAL001 = new SystemMessage(
            "VAL001",
            MessageType.VALIDATION,
            "Field is required",
            "This field is required."
    );

    public static final SystemMessage VAL002 = new SystemMessage(
            "VAL002",
            MessageType.VALIDATION,
            "Invalid email format",
            "Please enter a valid email address."
    );

    public static final SystemMessage VAL003 = new SystemMessage(
            "VAL003",
            MessageType.VALIDATION,
            "Invalid phone number",
            "Please enter a valid phone number."
    );

    public static final SystemMessage VAL004 = new SystemMessage(
            "VAL004",
            MessageType.VALIDATION,
            "Invalid date format",
            "Please enter a valid date."
    );
}
