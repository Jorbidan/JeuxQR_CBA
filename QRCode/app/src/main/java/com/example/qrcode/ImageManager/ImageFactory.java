package com.example.qrcode.ImageManager;


public class ImageFactory {
    public static ImageService getInstance() { return new FireStoreImageService();}
}
