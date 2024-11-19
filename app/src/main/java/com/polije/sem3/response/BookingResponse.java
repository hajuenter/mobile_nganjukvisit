package com.polije.sem3.response;

public class BookingResponse {
    private String status;  // Mengganti 'success' dengan 'status' untuk mencocokkan JSON
    private String message;
    private BookingData data;

    // Mengembalikan true jika status adalah "success"
    public boolean isSuccess() {
        return "success".equalsIgnoreCase(status);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public BookingData getData() {
        return data;
    }

    public void setData(BookingData data) {
        this.data = data;
    }

    public static class BookingData {
        private int id_tiket;

        public int getId_tiket() {
            return id_tiket;
        }

        public void setId_tiket(int id_tiket) {
            this.id_tiket = id_tiket;
        }
    }
}
