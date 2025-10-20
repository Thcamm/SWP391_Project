package common.utils;

import common.message.SystemMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public class MessageHelper {
    private static final String SUCCESS_MESSAGE_KEY = "successMessage";
    private static final String ERROR_MESSAGE_KEY = "errorMessage";
    private static final String WARNING_MESSAGE_KEY = "warningMessage";
    private static final String INFO_MESSAGE_KEY = "infoMessage";

    //Set success message vào session
    public static void setSuccessMessage(HttpSession session, SystemMessage message) {
        session.setAttribute(SUCCESS_MESSAGE_KEY, message);
    }

    public static void setSuccessMessage(HttpSession session, String content) {
        SystemMessage message = new SystemMessage("CUSTOM", common.constant.MessageType.SUCCESS, "", content);
        session.setAttribute(SUCCESS_MESSAGE_KEY, message);
    }

    //Set error message vào session
    public static void setErrorMessage(HttpSession session, SystemMessage message) {
        session.setAttribute(ERROR_MESSAGE_KEY, message);
    }

    public static void setErrorMessage(HttpSession session, String content) {
        SystemMessage message = new SystemMessage("CUSTOM", common.constant.MessageType.ERROR, "", content);
        session.setAttribute(ERROR_MESSAGE_KEY, message);
    }

    //Set info message vào session
    public static void setInfoMessage(HttpSession session, SystemMessage message) {
        session.setAttribute(INFO_MESSAGE_KEY, message);
    }

    public static void setInfoMessage(HttpSession session, String content) {
        SystemMessage message = new SystemMessage("CUSTOM", common.constant.MessageType.INFO, "", content);
        session.setAttribute(INFO_MESSAGE_KEY, message);
    }

    //Get và remove success and error message từ session

    public static SystemMessage getAndRemoveSuccessMessage(HttpSession session) {
        SystemMessage message = (SystemMessage) session.getAttribute(SUCCESS_MESSAGE_KEY);
        if (message != null) {
            session.removeAttribute(SUCCESS_MESSAGE_KEY);
        }
        return message;
    }


    public static SystemMessage getAndRemoveErrorMessage(HttpSession session) {
        SystemMessage message = (SystemMessage) session.getAttribute(ERROR_MESSAGE_KEY);
        if (message != null) {
            session.removeAttribute(ERROR_MESSAGE_KEY);
        }
        return message;
    }

    public static SystemMessage getAndRemoveWarningMessage(HttpSession session) {
        SystemMessage message = (SystemMessage) session.getAttribute(WARNING_MESSAGE_KEY);
        if (message != null) {
            session.removeAttribute(WARNING_MESSAGE_KEY);
        }
        return message;
    }

    public static SystemMessage getAndRemoveInfoMessage(HttpSession session) {
        SystemMessage message = (SystemMessage) session.getAttribute(INFO_MESSAGE_KEY);
        if (message != null) {
            session.removeAttribute(INFO_MESSAGE_KEY);
        }
        return message;
    }

    public static String formatMessage(SystemMessage message, Object... params) {
        String content = message.getContent();

        // Nếu có params, thay thế placeholders
        if (params != null && params.length > 0) {
            for (int i = 0; i < params.length; i += 2) {
                if (i + 1 < params.length) {
                    String placeholder = "{" + params[i] + "}";
                    String value = params[i + 1].toString();
                    content = content.replace(placeholder, value);
                }
            }
        }

        return content;
    }

    public static void setRequestMessage(HttpServletRequest request, String type, SystemMessage message) {
        request.setAttribute(type + "Message", message);
    }

}
