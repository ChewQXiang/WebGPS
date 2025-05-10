package com.gps; // 定義此類別所屬的套件為 com.gps

public class Receiver {

    // 處理接收到的加密位置資料
    public void reveiveLocation(String data) {
        // 顯示接收到的（加密後的）GPS 字串資料
        System.out.println("收到的GPS資料: ");
        System.out.println(data);

        // 解密資料：使用 Encryptor 類別進行解密
        String decryptedData = Encryptor.decrypt(data);

        // 顯示解密後的 GPS 資料內容（應為明文位置資訊）
        System.out.println("解密後的GPS資料: ");
        System.out.println(decryptedData);
    }
}
