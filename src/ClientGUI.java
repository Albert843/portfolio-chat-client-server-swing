import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Класс окна клиента
 */
public class ClientGUI extends JFrame {

    //region Поля

    private static final int WINDOW_WIDTH = 400;
    private static final int WINDOW_HEIGHT = 300;

    private ServerWindow serverWindow;
    private boolean connected;
    private String name;

    JTextArea log;
    JTextField tfIPAddress, tfPort, tfLogin, tfMessage;
    JPasswordField tfPassword;
    JButton btnLogin, btnSend;
    JPanel headerPanel;
    JPanel footerPanel;

    //endregion


    //region Конструктор

    /** Конструктор
     *
     * @param serverWindow окно сервера
     */
    public ClientGUI(ServerWindow serverWindow) {
        this.serverWindow = serverWindow;

        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setResizable(false);
        setLocation(serverWindow.getX() + 405, serverWindow.getY());
        setTitle("Chat client");
        createPanel();
        setVisible(true);
    }

    //endregion


    //region Методы

    public void answer(String text) {
        appendLog(text + "");
    }

    /**
     * Подключение пользователя к серверу чата
     */
    private void connectToServer() {
        if (serverWindow.connectUser(this)) {
            appendLog("Вы успешно подключились!\n");
            headerPanel.setVisible(false);
            connected = true;
            name = tfLogin.getText();
            String log = serverWindow.getLog();
            if (log != null) {
                appendLog(log);
            }
        } else {
            appendLog("Подключение не удалось");
        }
    }

    /**
     * Отключение пользователя от серверу чата
     */
    public void disconnectFromServer() {
        if (connected) {
            headerPanel.setVisible(true);
            connected = false;
            serverWindow.disconnectUser(this);
            appendLog("Вы были отключены от сервера!");
        }
    }

    /**
     * Отправка сообщения
     */
    public void sendMessage() {
        if (connected) {
            String text = tfMessage.getText();
            if (!text.equals("")) {
                serverWindow.message(name + ": " + text);
                tfMessage.setText("");
            }
        } else {
            appendLog("Нет подключения к серверу");
        }
    }

    /**
     * Добавление сообщения
     * @param text
     */
    private void appendLog(String text) {
        log.append(text + "\n");
    }

    /**
     * Создание панели
     */
    private void createPanel() {
        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createLog());
        add(createFooter(), BorderLayout.SOUTH);
    }

    /**
     * Заголовок шапка панели
     * @return
     */
    private Component createHeaderPanel() {
        headerPanel = new JPanel(new GridLayout(2, 3));
        tfIPAddress = new JTextField("127.0.0.1");
        tfPort = new JTextField("8189");
        tfLogin = new JTextField("Ivan Ivanovich");
        tfPassword = new JPasswordField("12345");
        btnLogin = new JButton("Login");
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                connectToServer();
            }
        });
        headerPanel.add(tfIPAddress);
        headerPanel.add(tfPort);
        headerPanel.add(new JPanel());
        headerPanel.add(tfLogin);
        headerPanel.add(tfPassword);
        headerPanel.add(btnLogin);

        return headerPanel;
    }

    /**
     * Лог в панели + скроллинг
     * @return
     */
    private Component createLog() {
        log = new JTextArea();
        log.setEditable(false);
        return new JScrollPane(log);
    }

    /**
     * Футер низ панели
     * @return
     */
    private Component createFooter() {
        footerPanel = new JPanel(new BorderLayout());
        tfMessage = new JTextField();
        tfMessage.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() == '\n') {
                    sendMessage();
                }
            }
        });
        btnSend = new JButton("send");
        btnSend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });
        footerPanel.add(tfMessage);
        footerPanel.add(btnSend, BorderLayout.EAST);
        return footerPanel;
    }

    /**
     * Отключение пользователя при закрытии окна
     * @param e  the window event событие в окне
     */
    @Override
    protected void processWindowEvent(WindowEvent e) {
        super.processWindowEvent(e);
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            disconnectFromServer();
        }
    }

    //endregion
}
