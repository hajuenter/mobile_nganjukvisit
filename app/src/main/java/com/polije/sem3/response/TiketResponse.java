package com.polije.sem3.response;

import com.polije.sem3.model.TiketModel;

import java.util.List;

public class TiketResponse {
    private boolean success;
    private String message;
    private List<TiketModel> data;

    // Getter and Setter methods
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<TiketModel> getData() {
        return data;
    }

    public void setData(List<TiketModel> data) {
        this.data = data;
    }
}
