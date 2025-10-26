import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

public class ModernCalculator extends JFrame implements ActionListener, KeyListener {
    private JTextField currentField;
    private JTextField historyField;
    private String operator = "";
    private double result = 0;
    private boolean startNewNumber = true;

    public ModernCalculator() {
        setTitle("Modern Calculator");
        setSize(400, 550);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(30, 30, 30));

        // History field
        historyField = new JTextField();
        historyField.setEditable(false);
        historyField.setHorizontalAlignment(JTextField.RIGHT);
        historyField.setFont(new Font("Arial", Font.PLAIN, 18));
        historyField.setBackground(new Color(30, 30, 30));
        historyField.setForeground(Color.LIGHT_GRAY);
        historyField.setBorder(BorderFactory.createEmptyBorder(5,10,5,10));
        add(historyField, BorderLayout.NORTH);

        // Current number field
        currentField = new JTextField("0");
        currentField.setEditable(false);
        currentField.setHorizontalAlignment(JTextField.RIGHT);
        currentField.setFont(new Font("Arial", Font.BOLD, 40));
        currentField.setBackground(Color.BLACK);
        currentField.setForeground(Color.WHITE);
        currentField.setBorder(BorderFactory.createEmptyBorder(5,10,5,10));
        currentField.addKeyListener(this);
        add(currentField, BorderLayout.CENTER);

        // Buttons panel
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5, 4, 12, 12));
        panel.setBackground(new Color(30, 30, 30));

        String[] buttons = {
            "C","←","%","/",
            "7","8","9","*",
            "4","5","6","-",
            "1","2","3","+",
            "±","0",".","="
        };

        for (String text : buttons) {
            JButton button = createButton(text);
            panel.add(button);
        }

        add(panel, BorderLayout.SOUTH);
        setVisible(true);
        currentField.requestFocusInWindow();
    }

    // Create button with modern look and hover effect
    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 24));
        button.setFocusable(false);
        button.setBorder(new RoundedBorder(15));
        
        // Set colors
        switch (text) {
            case "C":
            case "←":
            case "±":
                button.setBackground(new Color(220, 20, 60)); // Red
                button.setForeground(Color.WHITE);
                break;
            case "/":
            case "*":
            case "-":
            case "+":
            case "=":
            case "%":
                button.setBackground(new Color(255, 165, 0)); // Orange
                button.setForeground(Color.BLACK);
                break;
            default:
                button.setBackground(new Color(50, 50, 50)); // Dark gray
                button.setForeground(Color.WHITE);
                break;
        }

        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(button.getBackground().brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                // Reset color
                switch (text) {
                    case "C":
                    case "←":
                    case "±": button.setBackground(new Color(220, 20, 60)); break;
                    case "/":
                    case "*":
                    case "-":
                    case "+":
                    case "=":
                    case "%": button.setBackground(new Color(255, 165, 0)); break;
                    default: button.setBackground(new Color(50, 50, 50)); break;
                }
            }
        });

        button.addActionListener(this);
        return button;
    }

    // Rounded border class
    private static class RoundedBorder implements Border {
        private int radius;
        public RoundedBorder(int radius) { this.radius = radius; }
        public Insets getBorderInsets(Component c) { return new Insets(radius+1, radius+1, radius+1, radius+1); }
        public boolean isBorderOpaque() { return false; }
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            g.setColor(c.getBackground().darker());
            g.drawRoundRect(x, y, width-1, height-1, radius, radius);
        }
    }

    // Handle button presses
    public void actionPerformed(ActionEvent e) {
        handleInput(e.getActionCommand());
    }

    private void handleInput(String command) {
        if ("0123456789.".contains(command)) {
            if (startNewNumber) {
                currentField.setText(command.equals(".") ? "0." : command);
                startNewNumber = false;
            } else {
                if (!(command.equals(".") && currentField.getText().contains("."))) {
                    currentField.setText(currentField.getText() + command);
                }
            }
        } else if ("/*-+%".contains(command)) {
            calculatePendingOperation();
            operator = command;
            historyField.setText(result + " " + operator);
            startNewNumber = true;
        } else if ("=".equals(command)) {
            calculatePendingOperation();
            historyField.setText("");
            operator = "";
            startNewNumber = true;
        } else if ("C".equals(command)) {
            clearAll();
        } else if ("←".equals(command)) {
            backspace();
        } else if ("±".equals(command)) {
            toggleSign();
        }
    }

    private void calculatePendingOperation() {
        double currentNumber = Double.parseDouble(currentField.getText());
        switch (operator) {
            case "+": result += currentNumber; break;
            case "-": result -= currentNumber; break;
            case "*": result *= currentNumber; break;
            case "/": 
                if (currentNumber != 0) result /= currentNumber;
                else { JOptionPane.showMessageDialog(this, "Cannot divide by zero"); result = 0; }
                break;
            case "%": result %= currentNumber; break;
            case "": result = currentNumber; break;
        }
        currentField.setText(formatResult(result));
    }

    private String formatResult(double value) {
        if (value == (long)value) return String.valueOf((long)value);
        else return String.format("%.8g", value);
    }

    private void clearAll() {
        currentField.setText("0");
        historyField.setText("");
        operator = "";
        result = 0;
        startNewNumber = true;
    }

    private void backspace() {
        if (!startNewNumber) {
            String text = currentField.getText();
            if (text.length() > 1) currentField.setText(text.substring(0, text.length() - 1));
            else currentField.setText("0");
        }
    }

    private void toggleSign() {
        String text = currentField.getText();
        if (!text.equals("0")) {
            if (text.startsWith("-")) currentField.setText(text.substring(1));
            else currentField.setText("-" + text);
        }
    }

    // Keyboard support
    public void keyTyped(KeyEvent e) {
        char c = e.getKeyChar();
        if (Character.isDigit(c) || c == '.') handleInput(String.valueOf(c));
        else if ("+-*/%".indexOf(c) != -1) handleInput(String.valueOf(c));
        else if (c == '\n' || c == '=') handleInput("=");
        else if (c == '\b') handleInput("←");
        else if (c == 27) handleInput("C"); // ESC clears
    }
    public void keyPressed(KeyEvent e) {}
    public void keyReleased(KeyEvent e) {}

    public static void main(String[] args) {
        new ModernCalculator();
    }
}