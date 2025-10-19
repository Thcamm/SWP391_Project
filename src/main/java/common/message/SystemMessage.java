package common.message;

import common.constant.MessageType;

public class SystemMessage {
    private String code;
    private MessageType type;
    private  String context;
    private String content;

    public SystemMessage(String code, MessageType type, String context, String content) {
        this.code = code;
        this.type = type;
        this.context = context;
        this.content = content;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    @Override
    public String toString() {
        return "[" + code + "]  " + content;
    }
}
