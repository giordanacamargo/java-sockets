//// Java program to implement JColorChooser
//// class using ActionListener
//import java.awt.event.*;
//import java.awt.*;
//import javax.swing.*;
//
//public class ColorChooser extends JFrame implements ActionListener {
//
//    // create a button
//    JButton b = new JButton("color");
//
//    Container c = getContentPane();
//
//    // Constructor
//    ColorChooser() {
//        c.setLayout(new FlowLayout());
//        b.addActionListener(this);
//        c.add(b);
//    }
//
//    public void actionPerformed(ActionEvent e) {
//
//        Color initialcolor = Color.RED;
//        Color color = JColorChooser.showDialog(this,
//                "Select a color", initialcolor);
//        c.setBackground(color);
//    }
//
//    // Main Method
//    public static void main(String[] args)
//    {
//        ColorChooser ch = new ColorChooser();
//        ch.setSize(400, 400);
//        ch.setVisible(true);
//        ch.setDefaultCloseOperation(EXIT_ON_CLOSE);
//    }
//}