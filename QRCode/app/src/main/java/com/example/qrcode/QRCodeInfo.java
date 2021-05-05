package com.example.qrcode;

public class QRCodeInfo {
    private static String QRCode, imageRef, title, description, question, reponse, indice;

    public static String getQRCode() {
        return QRCode;
    }

    public static void setQRCode(String QRCode) {
        QRCodeInfo.QRCode = QRCode;
    }

    public static String getImageRef() {
        return imageRef;
    }

    public static void setImageRef(String imageRef) {
        QRCodeInfo.imageRef = imageRef;
    }

    public static String getTitle() {
        return title;
    }

    public static void setTitle(String title) {
        QRCodeInfo.title = title;
    }

    public static String getDescription() {
        return description;
    }

    public static void setDescription(String description) {
        QRCodeInfo.description = description;
    }

    public static String getQuestion() {
        return question;
    }

    public static void setQuestion(String question) {
        QRCodeInfo.question = question;
    }

    public static String getReponse() {
        return reponse;
    }

    public static void setReponse(String reponse) {
        QRCodeInfo.reponse = reponse;
    }

    public static String getIndice() {
        return indice;
    }

    public static void setIndice(String indice) {
        QRCodeInfo.indice = indice;
    }
}
