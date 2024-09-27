import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Calculator extends JFrame implements ActionListener {
    private JTextField textField;
    private JPanel panel;

    Calculator() {
        setTitle("Calculator Panel");
        setSize(350, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        textField = new JTextField();
        textField.setFont(new Font("Arial", Font.PLAIN, 50));
        textField.setEditable(false);
        //textField.setBackground(Color.WHITE);

        panel = new JPanel();
        panel.setLayout(new GridLayout(5, 4, 10, 10));
        //panel.setBackground(Color.black);

        String[] buttonLabels = {"C", "(", ")", "*", "7", "8", "9", "/", "4", "5", "6", "-", "1", "2", "3", "+", "Del", "0", ".", "="};

        for (String label : buttonLabels) {
            JButton button = new JButton(label);
            button.addActionListener(this);
            button.setFont(new Font("Arial", Font.PLAIN, 18));
            button.setFocusable(false);
            if (label.equals("C") || label.equals("(") || label.equals(")") || label.equals("*")) {
                button.setBackground(new Color(255, 165, 0));
            } else if (label.equals("/") || label.equals("-") || label.equals("+") || label.equals("=")) {
                button.setBackground(new Color(255, 165, 0));
            } else
                button.setBackground(new Color(204, 204, 204));
                
            panel.add(button);
        }

        add(textField, BorderLayout.NORTH);
        add(panel);

        setVisible(true);
    }

    public static void main(String[] args) {
        new Calculator();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        switch (command) {
            case "=":
                calculate();
                break;
            case "C":
                textField.setText("");
                break;
            case "Del":
                String text = textField.getText();
                if (!text.isEmpty())
                    textField.setText(text.substring(0, text.length() - 1));

                break;
            default:
                textField.setText(textField.getText() + command);
                break;
        }
    }

    private void calculate() {
        String expression = textField.getText();
        try {
            double result = evaluateExpression(expression);
            textField.setText(String.valueOf(result));
        } catch (Exception e) {
            textField.setText("Error");
        }
    }

    private double evaluateExpression(String expression) {
        return new Object() {
            int pos = -1, ch = 0;

            void nextChar() {
                if((++pos < expression.length())){
                    ch = expression.charAt(pos);
                } else
                    ch = -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') {
                    nextChar();
                }
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < expression.length())
                    throw new RuntimeException("Unexpected: " + (char)ch);
                return x;
            }

            double parseExpression() {
                double x = parseTerm();
                for (;;) {
                    if (eat('+')) {
                        x += parseTerm();
                    } else if (eat('-')) {
                        x -= parseTerm();
                    } else
                        return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (;;) {
                    if (eat('*')){
                        x *= parseFactor();
                    }else if (eat('/')){
                         x /= parseFactor();
                    }else
                        return x;
                }
            }

            double parseFactor() {
                if (eat('+'))
                    return parseFactor();
                if (eat('-'))
                    return -parseFactor();

                double x;
                int startPos = this.pos;

                if (eat('(')) {
                    x = parseExpression();
                    eat(')');
                } else if ((ch >= '0' && ch <= '9') || ch == '.') {
                    while ((ch >= '0' && ch <= '9') || ch == '.') { 
                        nextChar();
                    }
                    x = Double.parseDouble(expression.substring(startPos, this.pos));
                } else {
                    throw new RuntimeException("Unexpected: " + (char)ch);
                }

                if (eat('^'))
                    x = Math.pow(x, parseFactor());

                return x;
            }
        }.parse();
    }
}