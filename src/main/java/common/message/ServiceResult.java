package common.message;

import common.constant.MessageType;

public class ServiceResult {
    private boolean success;
    private SystemMessage message;
    private Object data;

    private ServiceResult(boolean success, SystemMessage message, Object data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public static ServiceResult success(SystemMessage message) {
        return new ServiceResult(true, message, null);
    }

    public static ServiceResult success(SystemMessage message, Object data) {
        return new ServiceResult(true, message, data);
    }

    public static ServiceResult error(SystemMessage message) {
        return new ServiceResult(false, message, null);
    }

    public static ServiceResult error(SystemMessage message, Object data) {
        return new ServiceResult(false, message, data);
    }

    public static ServiceResult success(Object data) {
        return new ServiceResult(true, null, data);
    }

    public static ServiceResult success() {
        return new ServiceResult(true, null, null);
    }

    public static ServiceResult error(String code, String context, String content) {
        return new ServiceResult(false,
                new SystemMessage(code, MessageType.ERROR, context, content),
                null
        );
    }


    public boolean isSuccess() {
        return success;
    }

    public boolean isError() {
        return !success;
    }

    public SystemMessage getMessage() {
        return message;
    }

    public Object getData() {
        return data;
    }

    @SuppressWarnings("unchecked")
    public <T> T getData(Class<T> type) {
        return (T) data;
    }

    @Override
    public String toString() {
        return "ServiceResult{" +
                "success=" + success +
                ", message=" + (message != null ? message.getCode() : "null") +
                ", hasData=" + (data != null) +
                '}';
    }
}
