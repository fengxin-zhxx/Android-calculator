package com.example.calculation;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

public class MainActivity extends AppCompatActivity {

    Button[] nums = new Button[10];
    // 0-9

    Button[] ops = new Button[8];
    static String[] opText = {"+", "-", "*", "/", "."};


    TextView text;
    TextView res;

    Cauculate cauculate = new Cauculate();

    boolean flag = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nums[0] = findViewById(R.id.btn0);
        nums[1] = findViewById(R.id.btn1);
        nums[2] = findViewById(R.id.btn2);
        nums[3] = findViewById(R.id.btn3);
        nums[4] = findViewById(R.id.btn4);
        nums[5] = findViewById(R.id.btn5);
        nums[6] = findViewById(R.id.btn6);
        nums[7] = findViewById(R.id.btn7);
        nums[8] = findViewById(R.id.btn8);
        nums[9] = findViewById(R.id.btn9);

        ops[0] = findViewById(R.id.btn_pls);
        ops[1] = findViewById(R.id.btn_mns);
        ops[2] = findViewById(R.id.btn_mul);
        ops[3] = findViewById(R.id.btn_div);
        ops[4] = findViewById(R.id.btn_pnt);

        ops[5] = findViewById(R.id.btn_del);
        ops[6] = findViewById(R.id.btn_clr);
        ops[7] = findViewById(R.id.btn_equ);

        text = findViewById(R.id.text);
        res = findViewById(R.id.result);


        for (int i = 0; i < 10; i++) {
            int finalI = i;
            nums[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String preText = (String) text.getText();
                    if (flag) preText = "";
                    flag = false;
                    text.setText(preText + finalI);
                    res.setText(cauculate.calc((String) text.getText()));
                }
            });
        }
        for (int i = 0; i < 5; i++) {
            int finalI = i;
            ops[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String preText = (String) text.getText();
                    if("Error!".equals(preText) || "请输入表达式:".equals(preText))preText="";
                    flag = false;
                    int len = preText.length();
                    if (len != 0 && Arrays.asList(opText).contains(preText.substring(len - 1, len))) {
                        text.setText(preText.substring(0, len - 1) + opText[finalI]);
                    } else text.setText(preText + opText[finalI]);

                }
            });
        }

        // del
        ops[5].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newText = (String) text.getText();
                if (flag) {
                    text.setText("");
                    return;
                }
                if (newText.length() != 0) newText = newText.substring(0, newText.length() - 1);
                text.setText(newText);
                if (newText.length() != 0) {
                    String resStr = cauculate.calc((String) text.getText());
                    if (!"Error!".equals(resStr)) res.setText(resStr);
                }else{
                    res.setText("");
                }
            }
        });

        //clr
        ops[6].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = false;
                text.setText("");
                res.setText("");
            }
        });

        //equ
        ops[7].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text.setText(cauculate.calc((String) text.getText()));
                res.setText("");
                flag = true;
            }
        });
    }

    class Cauculate {
        private boolean isNum(char ch) {
            return (int) ch >= 48 && (int) ch <= 58;
        }

        private boolean isOperator(char ch) {
            for (String op : opText) {
                if (op.charAt(0) == ch) return true;
            }
            return false;
        }

        private boolean isDouble(String s) {
            try {
                Double.valueOf(s);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }

        private boolean heightOperator(String o1, String o2) {
            if ((o1.equals("+") || o1.equals("-"))
                    && (o2.equals("*") || o2.equals("/"))) {
                return false;
            } else{
                return true;
            }
        }

        private Double getCountResult(String oper, Double num1, Double num2) {
            if ("+".equals(oper))
                return num1 + num2;
            else if ("-".equals(oper))
                return num1 - num2;
            else if ("*".equals(oper))
                return num1 * num2;
            else if ("/".equals(oper))
                return num1 / num2;
            else return 0.0;
        }


        private boolean isExpression(String str) {
            int flag = 0;
            if (!isNum(str.charAt((0))) || !isNum(str.charAt((str.length() - 1))))
                return false;

            for (int i = 0; i < str.length() - 1; i++) {
                char ch = str.charAt(i);
                char chb = str.charAt(i + 1);
                if ((ch == '.' && !isNum(chb)) || (!isNum(ch) && chb == '.')) {
                    return false;
                }
                if (isOperator(ch) && isOperator(chb)) {
                    return false;
                }
            }
            return true;
        }

        private List<String> resolveString(String str) {
            List<String> list = new ArrayList<String>();
            String temp = "";
            for (int i = 0; i < str.length(); i++) {
                final char ch = str.charAt(i);
                if (isNum(ch) || ch == '.') {
                    char c = str.charAt(i);
                    temp += c;
                } else if (isOperator(ch)) {
                    if (!temp.equals("")) {
                        list.add(temp);
                    }
                    list.add("" + ch);
                    temp = "";
                }
                if (i == str.length() - 1) {
                    list.add(temp);
                }
            }
            return list;
        }


        //  5 - 4 - 3 * 2 + 1
        //

        private List<String> nifix_to_post(List<String> list) {
            Stack<String> stack = new Stack<String>();
            List<String> plist = new ArrayList<String>();
            for (String str : list) {
                if (isDouble(str)) {
                    plist.add(str);
                } else {
                    while (!stack.isEmpty() && heightOperator(stack.lastElement(), str)) {
                        plist.add(stack.pop());
                    }
                    stack.push(str);
                }
            }
            while (!stack.isEmpty()) {
                plist.add(stack.pop());
            }
            return plist;
        }

        private Double get_postfis_result(List<String> list) {
            Stack<String> stack = new Stack<>();
            for (String str : list) {
                if (isDouble(str)) {
                    stack.push(str);
                } else if (isOperator(str.charAt((0)))) {
                    Double n2 = Double.parseDouble(stack.pop());
                    Double n1 = Double.parseDouble(stack.pop());
                    stack.push("" + getCountResult(str, n1, n2));
                }
                System.out.println(stack.toString());
            }
            return Double.valueOf(stack.pop());
        }

        String calc(String str) {
            if (!isExpression(str)) {
                return "Error!";
            }
            List<String> list = resolveString(str);
            list = nifix_to_post(list);
//            return list.toString();
            return (String.format("%.6f",get_postfis_result(list)));
        }
    }
}
