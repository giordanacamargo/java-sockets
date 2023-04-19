import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import javax.swing.*;
import javax.swing.text.*;

public class ChatClient extends javax.swing.JFrame{

    String serverAddress;
    Scanner in;
    PrintWriter out;
    JFrame frame = new JFrame("Chatter");
    JTextField textField = new JTextField(50);
    JTextPane messageArea = new JTextPane();


    public ChatClient() {
        this.serverAddress = "localhost";
        centerFrame();
        messageArea.setPreferredSize(new Dimension(700, 700) );
        textField.setEditable(false);
        messageArea.setEditable(false);
        frame.getContentPane().add(textField, BorderLayout.SOUTH);
        frame.setSize(700,700);
        frame.getContentPane().add(new JScrollPane(messageArea), BorderLayout.CENTER);
        frame.pack();

        // Send on enter then clear to prepare for next message
        textField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                out.println(textField.getText());
                textField.setText("");
            }
        });
    }

    private String getUserName() {
        return JOptionPane.showInputDialog(frame, "Escolha um nome de usuário:", "Nome de Usuário",
                JOptionPane.PLAIN_MESSAGE);
    }

    private String getColor() {
        return String.valueOf(JColorChooser.showDialog(frame, "Escolha uma cor", Color.red).getRGB());
    }

    private void run() throws IOException {
        try {
            Socket socket = new Socket(serverAddress, 59898);
            in = new Scanner(socket.getInputStream());
            out = new PrintWriter(socket.getOutputStream(), true);

            while (in.hasNextLine()) {
                String line = in.nextLine();
                if (line.startsWith("SUBMITNAME")) {
                    out.println(getUserName());
                } else if(line.startsWith("USERCOLOR")){
                    out.println(getColor());
                } else if (line.startsWith("NAMEACCEPTED")) {
                    this.frame.setTitle("Chatter - " + line.substring(13));
                    textField.setEditable(true);
                } else if (line.startsWith("MESSAGE")) {
                    Color c = new Color(Integer.valueOf(getServerMessageUserColor(line)));
                    appendToPane(getServerMessageUserName(line), getServerUserMessage(line), c);
                }
            }
        } catch (BadLocationException e) {
            throw new RuntimeException(e);
        } finally {
            frame.setVisible(false);
            frame.dispose();
        }
    }

    public static void main(String[] args) throws Exception {
        ChatClient client = new ChatClient();
        client.frame.setSize(700, 700);
        client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        client.frame.setVisible(true);
        client.run();
    }

    private String getServerUserMessage(String line) {
        // Mensagem do Servidor - [0]
        // Cor do usuário - [1]
        // Nome - [2]
        // Mensagem - [3]
        String msg[] = line.split(";");
        return msg[3];
    }

    private String getServerMessageUserColor (String line){
        // Mensagem do Servidor - [0]
        // Cor do usuário - [1]
        // Nome - [2]
        // Mensagem - [3]
        String msg[] = line.split(";");
        return msg[1];
    }

    private String getServerMessageUserName (String line){
        // Mensagem do Servidor - [0]
        // Cor do usuário - [1]
        // Nome - [2]
        // Mensagem - [3]
        String msg[] = line.split(";");
        return msg[2];
    }

    public void appendToPane(String name, String msg, Color c) throws BadLocationException {
        StyleContext sc = StyleContext.getDefaultStyleContext();
        AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c); //cor do nome
        aset = sc.addAttribute( aset, StyleConstants.FontFamily, "Lucida Console" );
        aset = sc.addAttribute( aset, StyleConstants.Bold, true);
        messageArea.getStyledDocument().insertString(messageArea.getDocument().getLength(), name, aset);

        StyleContext sc2 = StyleContext.getDefaultStyleContext();
        AttributeSet aset2 = sc2.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, Color.BLACK);
        aset2 = sc2.addAttribute( aset2, StyleConstants.FontFamily, "Lucida Console" );
        aset2 = sc2.addAttribute( aset2, StyleConstants.Italic, true);
        messageArea.getStyledDocument().insertString(messageArea.getDocument().getLength(), msg + "\n", aset2);
    }

    public void centerFrame() {
        Dimension windowSize = getSize();
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Point centerPoint = ge.getCenterPoint();

        int dx = (centerPoint.x - (windowSize.width / 2))-350;
        int dy = (centerPoint.y - (windowSize.height / 2))-350;
        frame.setLocation(dx, dy);
    }
}