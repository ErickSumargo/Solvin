package com.example.ericksumargo.solvinap.server.data;

import static android.R.attr.id;

/**
 * Created by Erick Sumargo on 3/15/2017.
 */

public class DataNotification {
    private int id;
    private int auth_id;
    private int sender_id;
    private int subject_id;
    private int status;
    private String photo;
    private String type;
    private String auth_type;
    private String sender_type;
    private String subject_type;
    private String content;
    private String created_at;
    private String updated_at;
    private String sender_name;

    public String getSender_name() {
        return sender_name;
    }

    public void setSender_name(String sender_name) {
        this.sender_name = sender_name;
    }

    public int getAuth_id() {
        return auth_id;
    }

    public void setAuth_id(int auth_id) {
        this.auth_id = auth_id;
    }

    public int getSender_id() {
        return sender_id;
    }

    public void setSender_id(int sender_id) {
        this.sender_id = sender_id;
    }

    public int getSubject_id() {
        return subject_id;
    }

    public void setSubject_id(int subject_id) {
        this.subject_id = subject_id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAuth_type() {
        return auth_type;
    }

    public void setAuth_type(String auth_type) {
        this.auth_type = auth_type;
    }

    public String getSender_type() {
        return sender_type;
    }

    public void setSender_type(String sender_type) {
        this.sender_type = sender_type;
    }

    public String getSubject_type() {
        return subject_type;
    }

    public void setSubject_type(String subject_type) {
        this.subject_type = subject_type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public DataNotification(int id, int auth_id, int sender_id, int subject_id, int status, String type, String auth_type, String sender_type, String subject_type, String content, String created_at, String updated_at) {
        this.id = id;
        this.auth_id = auth_id;
        this.sender_id = sender_id;
        this.subject_id = subject_id;
        this.status = status;
        this.type = type;
        this.auth_type = auth_type;
        this.sender_type = sender_type;
        this.subject_type = subject_type;
        this.content = content;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "id=" + id +
                ", auth_id=" + auth_id +
                ", sender_id=" + sender_id +
                ", subject_id=" + subject_id +
                ", status=" + status +
                ", type='" + type + '\'' +
                ", auth_type='" + auth_type + '\'' +
                ", sender_type='" + sender_type + '\'' +
                ", subject_type='" + subject_type + '\'' +
                ", content='" + content + '\'' +
                ", created_at='" + created_at + '\'' +
                ", updated_at='" + updated_at + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
