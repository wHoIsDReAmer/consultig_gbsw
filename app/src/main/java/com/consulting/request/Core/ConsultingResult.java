package com.consulting.request.Core;

public class ConsultingResult {

    public enum Result {
        WAITING("수락 대기 중"),
        SUCCESS("수락됨"),
        DENY("거절됨");

        private final String title;
        Result(String title) {
            this.title = title;
        }

        public String getTitle() {
            return this.title;
        }
    }

    private final Result type;
    private String sid;
    private String name;
    private String date;
    private boolean isParents;
    private String consultingType;

    private String message;

    public ConsultingResult(Result type) {
        this.type = type;
    }

    public ConsultingResult(Result type, String sid, String name, String when, boolean isParents) {
        this.type = type;
        this.sid = sid;
        this.name = name;
        this.date = when;
        this.isParents = isParents;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getSid() {
        return sid;
    }

    public void setParents(boolean flag) {
        this.isParents = flag;
    }

    public boolean isParents() {
        return this.isParents;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDate() {
        return this.date;
    }

    public Result getType() {
        return this.type;
    }

    public void setMessage(String msg) {
        this.message = msg;
    }

    public String getMessage() {
        return this.message;
    }

    public String getConsultingType() {
        return this.consultingType;
    }

    public void setConsultingType(String type) {
        this.consultingType = type;
    }
}
