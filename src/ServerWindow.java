import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс окна сервера
 */
public class ServerWindow extends JFrame {

    //region Поля

    private static final int WINDOW_WIDTH = 400;
    private static final int WINDOW_HEIGHT = 300;
    private static final String LOG_PATH = "src/log.txt";

    private List<ClientGUI> clientGUIList;

    private JButton btnStart, btnStop;
    private JTextArea log;
    private boolean isServerWorking;

    //endregion


    //region Конструктор
    /**
     * Конструктор
     */
    public ServerWindow() {
        clientGUIList = new ArrayList<>();

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setLocationRelativeTo(null);
        setTitle("Chat server");
        setResizable(false);
        createPanel();
        setVisible(true);
    }

    //endregion


    //region Методы

    /**
     * Подключение пользователя к серверу чата
     * @param clientGUI список пользователей чата
     * @return
     */
    public boolean connectUser(ClientGUI clientGUI) {
        if (!isServerWorking) {
            return false;
        }
        clientGUIList.add(clientGUI);
        return true;
    }

    /**
     * Отключение пользователя от сервера чата
     * @param clientGUI список пользователей чата
     */
    public void disconnectUser(ClientGUI clientGUI) {
        clientGUIList.remove(clientGUI);
        if (clientGUI != null) {
            clientGUI.disconnectFromServer();
        }
    }

    /**
     * Получение лог истории
     * @return
     */
    public String getLog() {
        return readLog();
    }

    /**
     * Добавление сообщения в лог, отправление этого сообщения всем пользователям,
     * добавление сообщения в файл
     * @param text
     */
    public void message(String text) {
        if (!isServerWorking) {
            return;
        }
        appendLog(text);
        answerAll(text);
        saveInLog(text);
    }

    /**
     * Отправление этого сообщения всем пользователям
     * @param text
     */
    private void answerAll(String text) {
        for (ClientGUI clientGUI:clientGUIList) {
            clientGUI.answer(text);
        }
    }

    /**
     * Добавление сообщения в файл
     * @param text
     */
    private void saveInLog(String text) {
        try(FileWriter writer = new FileWriter(LOG_PATH, true)) {
            writer.write(text);
            writer.write("\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Чтение из файла
     * @return
     */
    private String readLog() {
        StringBuilder stringBuilder = new StringBuilder();
        try(FileReader fileReader = new FileReader(LOG_PATH)) {
            int c;
            while ((c = fileReader.read()) != -1) {
                stringBuilder.append((char) c);
            }
            stringBuilder.delete(stringBuilder.length()-1, stringBuilder.length());
            return stringBuilder.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Переход на следующую строку после добавления сообщения
     * @param text
     */
    private void appendLog(String text) {
        log.append((text + "\n"));
    }

    /**
     * Создание панели
     */
    private void createPanel() {
        log = new JTextArea();
        add(log);
        add(createButtons(), BorderLayout.SOUTH);
    }

    /**
     * Создание кнопок и их слушателей
     * @return
     */
    private Component createButtons() {
        JPanel panel = new JPanel(new GridLayout(1, 2));
        btnStart = new JButton("Start");
        btnStop = new JButton("Stop");

        btnStart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isServerWorking) {
                    appendLog("Сервер уже запущен");
                } else {
                    if (isServerWorking = true);
                    appendLog("Сервер запущен");
                }
            }
        });

        btnStop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isServerWorking) {
                    appendLog("Сервер уже был остановлен");
                } else {
                    isServerWorking = false;
                    while (!clientGUIList.isEmpty()) {
                        disconnectUser(clientGUIList.get(clientGUIList.size() - 1));
                    }
                    appendLog("Сервер остановлен");
                }
            }
        });
        panel.add(btnStart);
        panel.add(btnStop);
        return panel;
    }

    //endregion
}
