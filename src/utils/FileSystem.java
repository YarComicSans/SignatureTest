package utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Утилитарный класс для работы с файловой системой
 */
public class FileSystem {
    /**
     * Метод для записи байтов в файле
     * @param bytes - байты для записи
     * @param path - путь, по которому требуется создать файл
     * @throws IOException
     */
    public static void saveBytesInFile(byte[] bytes, String path) throws IOException {
        FileOutputStream fos = null;
        fos = new FileOutputStream(path);
        fos.write(bytes);
        fos.close();

    }

    /**
     * Метод чтения байтов из файла
     * @param filename - полное имя файла для чтения байтов
     * @return Возвращает прочитанные байты
     * @throws IOException
     */
    public static byte[] readBytesFromFile(String filename) throws IOException {
        var fis = new FileInputStream(filename);
        var fileBytes = new byte[fis.available()];
        fis.read(fileBytes);
        fis.close();
        return fileBytes;
    }
}
