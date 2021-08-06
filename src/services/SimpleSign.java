package services;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import utils.FileSystem;

/**
 *  Класс простой подписи
 */
public class SimpleSign {

    /** Поле подписи */
    private static Signature signature;

    /**
     * Конструктор - инициализация поля подписи
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     */
    public SimpleSign () throws NoSuchAlgorithmException, NoSuchProviderException {
        signature = Signature.getInstance("SHA1withDSA", "SUN");
    }

    /**
     * Метод заполнения подписи данными для подписания
     * @param data - данные для подписания в виде байтов
     * @throws IOException
     * @throws SignatureException
     */
    private static void supplySignatureWithData(byte[] data) throws IOException, SignatureException {
        var bis = new ByteArrayInputStream(data);
        var bufin = new BufferedInputStream(bis);

        var buffer = new byte[1024];
        int len;
        while(bufin.available() != 0) {
            len = bufin.read(buffer);
            signature.update(buffer, 0, len);
        }

        bufin.close();
    }

    /**
     * Метод генерации подписи для содержимого файла
     * @param content - содержимое файла в виде байтов
     * @return Возвращает байты сигнатуры
     * @throws SignatureException
     * @throws IOException
     */
    private static byte[] generateSignature(byte[] content) throws SignatureException, IOException {
        supplySignatureWithData(content);
        var signBytes= signature.sign();

        return signBytes;
    }

    /**
     * Метод генерации пары ключей - приватного и публичного
     * @return Возвращает пару ключей
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     */
    private static KeyPair generateKeys() throws NoSuchAlgorithmException, NoSuchProviderException {
        var keyGen = KeyPairGenerator.getInstance("DSA", "SUN");
        var random = SecureRandom.getInstance("SHA1PRNG", "SUN");
        keyGen.initialize(1024, random);

        var keyPair = keyGen.generateKeyPair();

        return keyPair;
    }

    /**
     * Метод для сохранения подписи в виде файла с разрешением .sig, используя дополнительный класс FileSystem
     * @see FileSystem#saveBytesInFile(byte[], String)
     * @param signature - подпись в виде байтов
     * @param filename - полное имя файла
     * @throws IOException
     * @see FileSystem#saveBytesInFile(byte[], String)
     */
    private static void saveSignature(byte[] signature, String filename) throws IOException {
        FileSystem.saveBytesInFile(signature, filename + ".sig");
    }

    /**
     * Метод для сохранения публичного ключа в виде файла с разрешением .pk, используя дополнительный класс FileSystem
     * @param publicKey - публичный ключ
     * @param filename - полное имя файла
     * @throws IOException
     * @see FileSystem#saveBytesInFile(byte[], String)
     */
    private static void savePublicKey(PublicKey publicKey, String filename) throws IOException {
        var key = publicKey.getEncoded();
        FileSystem.saveBytesInFile(key, filename + ".pk");
    }

    /**
     * Метод генерации публичного ключа из байтов, полученных из файла публичного ключа
     * @param encryptedKeyBytes - байты публичного ключа
     * @return Возвращает публичный ключ
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     * @throws InvalidKeySpecException
     */
    private static PublicKey generatePublicKeyFromBytes(byte[] encryptedKeyBytes) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException {
        var keySpec = new X509EncodedKeySpec(encryptedKeyBytes);
        var keyFactory = KeyFactory.getInstance("DSA", "SUN");
        var key = keyFactory.generatePublic(keySpec);
        return key;
    }

    /**
     * Метод подписи документа
     * @param document - файл, который требуется подписать
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     * @throws InvalidKeyException
     * @throws SignatureException
     * @throws IOException
     */
    public static void signDocument(File document)
            throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, SignatureException, IOException {
        var keys = generateKeys();
        var content = FileSystem.readBytesFromFile(document.getPath());

        signature = Signature.getInstance("SHA1withDSA", "SUN");
        signature.initSign(keys.getPrivate());

        var signBytes = generateSignature(content);

        saveSignature(signBytes, document.getPath());
        savePublicKey(keys.getPublic(), document.getPath());
    }

    /**
     * Метод проверки подписи
     * @param signatureFile - файл подписи
     * @param publicKeyFile - файл публичного ключа
     * @param signedDocument - файл подписанного документа
     * @return Возвращает истину, если подпись из файла соответсвует подписи с публичным ключом, иначе возвращает ложь
     * @throws SignatureException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     * @throws InvalidKeySpecException
     * @throws InvalidKeyException
     * @throws IOException
     */
    public static boolean verifySignature(File signatureFile, File publicKeyFile, File signedDocument)
            throws SignatureException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException, InvalidKeyException, IOException {
        var signatureBytes = FileSystem.readBytesFromFile(signatureFile.getPath());

        var encryptedKeyBytes = FileSystem.readBytesFromFile(publicKeyFile.getPath());
        var publicKey = generatePublicKeyFromBytes(encryptedKeyBytes);

        signature = Signature.getInstance("SHA1withDSA", "SUN");
        signature.initVerify(publicKey);

        var signedDocBytes = FileSystem.readBytesFromFile(signedDocument.getPath());
        supplySignatureWithData(signedDocBytes);

        var isVerified = signature.verify(signatureBytes);
        return isVerified;
    }
}
