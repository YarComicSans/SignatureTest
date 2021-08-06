/**
 * Приложение для создания простой электронной подписи Word или PDF документов с возможностью проверки подлинности данной подписи с помощью публичного ключа
 * @author Даниил Алексеев
 * @version 1.0
 */

package view.components;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.nio.file.Paths;

import services.SimpleSign;

public class App extends JFrame {
    /** Поля компонентов интерфейса */
    /** Кнопка выбора документа для подписи */
    private JButton btnChooseDocToSign = null;
    /** Кнопка выбора файла публичного ключа */
    private JButton btnChoosePublicKeyFile = null;
    /** Кнопка выбора файла подписи */
    private JButton btnChooseSignFile = null;
    /** Кнопка для подписи документа */
    private JButton btnSignDoc = null;
    /** Кнопка для проверки подписи */
    private JButton btnCheckDocSign = null;
    /** Пометка для отслеживания текущего выбранного документа для подписи */
    private JLabel lbCurrentDocToSign = null;
    /** Пометка для отслеживания текущего выбранного документа публичного ключа */
    private JLabel lbCurrentPublicKeyFile = null;
    /** Пометка для отслеживания текущего выбранного документа подписи */
    private JLabel lbCurrentSignFile = null;
    /** Поле для логирования совершенных действий и ошибок */
    private JTextArea log = null;
    /** Компонент для выбора файлов на диске */
    private JFileChooser fileChooser = null;

    /** Внутреннее состояние приложения */
    /** Поле выбранного документа для подписи */
    private File chosenFile = null;
    /** Поле выбранного документа публичного ключа */
    private File chosenSignatureFile = null;
    /** Поле выбранного документа подписи */
    private File chosenPublicKeyFile = null;

    /**
     * Конструктор - создание нового объекта приложения
     */
    public App() {
        super("Электронная подпись документа");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        initContents();
        addContents();

        setSize(900, 360);
        setVisible(true);
    }

    /**
     * Метод инициализации компонентов интерфейса приложения
     */
    private void initContents() {
        btnChooseDocToSign = new JButton("Выбрать документ");
        btnSignDoc = new JButton("Подписать документ");
        btnCheckDocSign = new JButton("Проверить подпись");
        btnChoosePublicKeyFile = new JButton("Выбрать публичный ключ");
        btnChooseSignFile = new JButton("Выбрать файл подписи");
        addListeners();

        lbCurrentDocToSign = new JLabel();
        lbCurrentPublicKeyFile = new JLabel();
        lbCurrentSignFile = new JLabel();

        log = new JTextArea(5,60);
        log.setMargin(new Insets(5,5,5,5));
        log.setEditable(false);


        var pathToFiles = Paths.get(System.getProperty("user.dir"), "files").toFile();
        fileChooser = new JFileChooser(pathToFiles);
    }

    /**
     * Метод добавления компонентов интерфейса на главную панель приложения
     */
    private void addContents() {
        var contents = new JPanel();
        var buttonsBox = Box.createHorizontalBox();
        buttonsBox.add(btnChooseDocToSign);
        buttonsBox.add(btnChoosePublicKeyFile);
        buttonsBox.add(btnChooseSignFile);
        buttonsBox.add(btnSignDoc);
        buttonsBox.add(btnCheckDocSign);

        var labelsBox = Box.createVerticalBox();
        labelsBox.add(lbCurrentDocToSign);
        labelsBox.add(lbCurrentPublicKeyFile);
        labelsBox.add(lbCurrentSignFile);

        var logScrollPane = new JScrollPane(log);

        contents.add(buttonsBox);
        contents.add(labelsBox);
        contents.add(logScrollPane, BorderLayout.CENTER);
        setContentPane(contents);
    }

    /**
     * Метод добавления слушателей событий кнопок
     * @see SimpleSign#verifySignature(File, File, File)
     * @see SimpleSign#signDocument(File)
     */
    private void addListeners() {
        /**
         * Слушатель события нажатия для кнопки выбора документа для подписи
         */
        btnChooseDocToSign.addActionListener(event -> {
                fileChooser.setDialogTitle("Выберите документ");

                var filter = new FileNameExtensionFilter("Word & PDF", "docx", "pdf");
                fileChooser.setFileFilter(filter);

                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                var openResult = fileChooser.showOpenDialog(App.this);

                if(openResult == JFileChooser.APPROVE_OPTION) {
                    chosenFile = fileChooser.getSelectedFile();

                    lbCurrentDocToSign.setText("Выбранный документ: " + chosenFile.getName());
                    var message = "Выбран документ: " + chosenFile + "\n";
                    displayInformation(message, "Информация", JOptionPane.INFORMATION_MESSAGE);
                }
        });
        /**
         * Слушатель события нажатия для кнопки выбора документа подписи
         */
        btnChooseSignFile.addActionListener(event -> {
                fileChooser.setDialogTitle("Выберите документ");

                var filter = new FileNameExtensionFilter("Файлы подписи", "sig");
                fileChooser.setFileFilter(filter);

                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                var openResult = fileChooser.showOpenDialog(App.this);

                if(openResult == JFileChooser.APPROVE_OPTION) {
                    chosenSignatureFile = fileChooser.getSelectedFile();

                    lbCurrentSignFile.setText("Выбранный файл подписи: " + chosenSignatureFile.getName());
                    var message = "Выбран файл подписи: " + chosenSignatureFile + "\n";
                    displayInformation(message, "Информация", JOptionPane.INFORMATION_MESSAGE);
                }
        });
        /**
         * Слушатель события нажатия для кнопки выбора документа публичного ключа
         */
        btnChoosePublicKeyFile.addActionListener(event -> {
                fileChooser.setDialogTitle("Выберите документ");

                var filter = new FileNameExtensionFilter("Файлы публичных ключей", "pk");
                fileChooser.setFileFilter(filter);

                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                var openResult = fileChooser.showOpenDialog(App.this);

                if(openResult == JFileChooser.APPROVE_OPTION) {
                    chosenPublicKeyFile = fileChooser.getSelectedFile();

                    lbCurrentPublicKeyFile.setText("Выбранный файл публичного ключа: " + chosenPublicKeyFile.getName());
                    var message = "Выбран публичный ключ: " + chosenPublicKeyFile + "\n";
                    displayInformation(message, "Информация", JOptionPane.INFORMATION_MESSAGE);
                }
        });
        /**
         * Слушатель события нажатия для кнопки подписи документа
         */
        btnSignDoc.addActionListener(event -> {
            if(chosenFile == null) {
                var message = "Документ для подписи не выбран!\n";
                displayInformation(message, "Ошибка!", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                SimpleSign.signDocument(chosenFile);

                displayInformation("Документ подписан!\n", "Информация", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {}
        });
        /**
         * Слушатель события нажатия для кнопки проверки подписи
         */
        btnCheckDocSign.addActionListener(event -> {
            if (!checkIfFilesSupplied()) return;

            try {
                var isVerified = SimpleSign.verifySignature(chosenSignatureFile, chosenPublicKeyFile, chosenFile);
                if (isVerified) {
                    displayInformation("Подпись подтверждена!\n", "Информация", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    displayInformation("Подпись не подтверждена!\n", "Ошибка!", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {}
        });
    }

    /**
     * Метод для отображения информации пользователю
     * @param message - сообщение для отображения
     * @param title - заголовок для окна оповещения
     * @param typeOfMessage - тип сообщения для окна оповещения
     */
    private void displayInformation(String message, String title, int typeOfMessage) {
        JOptionPane.showMessageDialog(App.this, message, title, typeOfMessage);
        log.append(message);
        log.setCaretPosition(log.getDocument().getLength());
    }

    /**
     * Метод проверки наличия всех файлов перед проверкой подписи
     * @return Возвращает ложь, если хотя бы один из файлов не был предоставлен. Иначе возвращает истину
     */
    private boolean checkIfFilesSupplied() {
        if(chosenFile == null) {
            displayInformation("Документ для подписи не выбран!\n", "Ошибка!", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if(chosenPublicKeyFile == null) {
            displayInformation("Публичный ключ не выбран!\n", "Ошибка!", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if(chosenSignatureFile == null) {
            displayInformation("Файл подписи не выбран!\n", "Ошибка!", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    /**
     * Метод локализации компонента выбора файлов
     */
    private static void localizeFileChooser() {
        UIManager.put("FileChooser.openButtonText", "Открыть");
        UIManager.put("FileChooser.cancelButtonText", "Отмена");
        UIManager.put("FileChooser.fileNameLabelText", "Наименование файла");
        UIManager.put("FileChooser.filesOfTypeLabelText", "Типы файлов");
        UIManager.put("FileChooser.lookInLabelText", "Директория");
    }

    /**
     * Метод, запускающий приложение
     * @param args - аргументы для запуска
     */
    public static void main(String[] args) {
        localizeFileChooser();
        new App();
    }
}
