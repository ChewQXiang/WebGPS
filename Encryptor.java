package com.gps; // 定義此類別所屬的套件為 com.gps

public class Encryptor {

    // 加密方法：將明文每個字元向後偏移 3 個 Unicode 編碼
    public static String encrypt(String plainText) {
        StringBuilder encrypted = new StringBuilder(); // 用來儲存加密後的字串

        // 逐字處理明文中的每個字元
        for(char c: plainText.toCharArray()) {
            c += 3; // 字元向後偏移 3 位（簡單 Caesar 密碼）
            encrypted.append(c); // 加入加密結果
        }

        return encrypted.toString(); // 回傳整段加密後的字串
    }

    // 解密方法：將密文每個字元向前偏移 3 個 Unicode 編碼
    public static String decrypt(String cipherText) {
        StringBuilder decrypted = new StringBuilder(); // 用來儲存解密後的結果

        // 逐字處理密文中的每個字元
        for(char c : cipherText.toCharArray()) {
            c -= 3; // 字元向前偏移 3 位
            decrypted.append(c); // 加入解密結果
        }

        return decrypted.toString(); // 回傳整段解密後的明文字串
    }
}
