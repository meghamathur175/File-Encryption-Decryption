import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Key;

public class FileEncryptionDecryption {

    private static final String AES_ALGORITHM = "AES";
    private static final String ENCRYPTED_FILE_EXTENSION = ".enc";

    public static void main(String[] args) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Enter file path: ");
            String filePath = reader.readLine();

            System.out.print("Enter encryption key (16/24/32 characters): ");
            String encryptionKey = reader.readLine();

            System.out.print("Encrypt (E) or Decrypt (D): ");
            String mode = reader.readLine();

            if (mode.equalsIgnoreCase("E")) {
                encryptFile(filePath, encryptionKey);
                System.out.println("File encrypted successfully!");
            } else if (mode.equalsIgnoreCase("D")) {
                decryptFile(filePath, encryptionKey);
                System.out.println("File decrypted successfully!");
            } else {
                System.out.println("Invalid mode selected.");
            }
        } catch (Exception e) {
            System.err.println("An error occurred: " + e.getMessage());
        }
    }

    private static void encryptFile(String filePath, String encryptionKey) throws Exception {
        byte[] fileContent = Files.readAllBytes(Paths.get(filePath));

        Key key = generateKey(encryptionKey);
        Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);

        byte[] encryptedContent = cipher.doFinal(fileContent);

        String encryptedFilePath = filePath + ENCRYPTED_FILE_EXTENSION;
        try (FileOutputStream outputStream = new FileOutputStream(encryptedFilePath)) {
            outputStream.write(encryptedContent);
        }
    }

    private static void decryptFile(String filePath, String encryptionKey) throws Exception {
        byte[] encryptedContent = Files.readAllBytes(Paths.get(filePath));

        Key key = generateKey(encryptionKey);
        Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);

        byte[] decryptedContent = cipher.doFinal(encryptedContent);

        String decryptedFilePath = filePath.endsWith(ENCRYPTED_FILE_EXTENSION)
                ? filePath.substring(0, filePath.length() - ENCRYPTED_FILE_EXTENSION.length())
                : filePath + ".decrypted";
        try (FileOutputStream outputStream = new FileOutputStream(decryptedFilePath)) {
            outputStream.write(decryptedContent);
        }
    }

    private static Key generateKey(String encryptionKey) throws Exception {
        byte[] keyBytes = encryptionKey.getBytes("UTF-8");
        if (keyBytes.length != 16 && keyBytes.length != 24 && keyBytes.length != 32) {
            throw new IllegalArgumentException("Invalid key size. Use a 16, 24, or 32-byte key.");
        }
        return new SecretKeySpec(keyBytes, AES_ALGORITHM);
    }
}
