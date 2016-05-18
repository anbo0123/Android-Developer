package com.example.smallwave.calculator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // 声明控件
    Button btn_0;
    Button btn_1;
    Button btn_2;
    Button btn_3;
    Button btn_4;
    Button btn_5;
    Button btn_6;
    Button btn_7;
    Button btn_8;
    Button btn_9;

    Button btn_point;
    Button btn_clear;
    Button btn_del;
    Button btn_plus;
    Button btn_minus;
    Button btn_multiply;
    Button btn_divide;
    Button btn_equle;

    TextView et_input; // 显示屏
    boolean clear_flag; // 清空标识

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 实例化控件
        btn_0 = (Button) findViewById(R.id.btn_0);
        btn_1 = (Button) findViewById(R.id.btn_1);
        btn_2 = (Button) findViewById(R.id.btn_2);
        btn_3 = (Button) findViewById(R.id.btn_3);
        btn_4 = (Button) findViewById(R.id.btn_4);
        btn_5 = (Button) findViewById(R.id.btn_5);
        btn_6 = (Button) findViewById(R.id.btn_6);
        btn_7 = (Button) findViewById(R.id.btn_7);
        btn_8 = (Button) findViewById(R.id.btn_8);
        btn_9 = (Button) findViewById(R.id.btn_9);

        btn_point = (Button) findViewById(R.id.btn_point);
        btn_clear = (Button) findViewById(R.id.btn_clear);
        btn_del = (Button) findViewById(R.id.btn_del);
        btn_plus = (Button) findViewById(R.id.btn_plus);
        btn_minus = (Button) findViewById(R.id.btn_minus);
        btn_multiply = (Button) findViewById(R.id.btn_multiply);
        btn_divide = (Button) findViewById(R.id.btn_divide);
        btn_equle = (Button) findViewById(R.id.btn_equal);

        et_input = (TextView) findViewById(R.id.et_input);

        // 设置按钮的点击事件
        btn_0.setOnClickListener(this);
        btn_1.setOnClickListener(this);
        btn_2.setOnClickListener(this);
        btn_3.setOnClickListener(this);
        btn_4.setOnClickListener(this);
        btn_5.setOnClickListener(this);
        btn_6.setOnClickListener(this);
        btn_7.setOnClickListener(this);
        btn_8.setOnClickListener(this);
        btn_9.setOnClickListener(this);

        btn_point.setOnClickListener(this);
        btn_clear.setOnClickListener(this);
        btn_del.setOnClickListener(this);
        btn_plus.setOnClickListener(this);
        btn_minus.setOnClickListener(this);
        btn_multiply.setOnClickListener(this);
        btn_divide.setOnClickListener(this);
        btn_equle.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {

        // 取出显示屏的数据
        String str = et_input.getText().toString();
        switch (view.getId()){
            // 数字按钮
            case R.id.btn_0:
            case R.id.btn_1:
            case R.id.btn_2:
            case R.id.btn_3:
            case R.id.btn_4:
            case R.id.btn_5:
            case R.id.btn_6:
            case R.id.btn_7:
            case R.id.btn_8:
            case R.id.btn_9:
            case R.id.btn_point:
                if (clear_flag){
                    clear_flag = false;
                    str = "";
                    et_input.setText("");
                }
//                Log.d("Tag","getText:" + ((Button)view).getText()); // 输出值
                String inputValue = str + ((Button)view).getText().toString();
                et_input.setText(inputValue);
//                et_input.setText(str + ((Button)view).getText()); // 将点击的数字按钮数据累加到显示屏
                break;
            // 运算符按钮
            case R.id.btn_plus:
            case R.id.btn_minus:
            case R.id.btn_multiply:
            case R.id.btn_divide:
                if (clear_flag){
                    clear_flag = false;
                    str = "";
                    et_input.setText("");
                }else if (str.equals("")){
                    et_input.setText(str);
                }else {
                    et_input.setText(str + " " + ((Button)view).getText() + " ");
                }
                break;
            // 特殊按钮
            case R.id.btn_clear:
                clear_flag = false;
                str = "";
                et_input.setText("");
                break;
            case R.id.btn_del:
                if (clear_flag){
                    clear_flag = false;
                    str = "";
                    et_input.setText("");
                }else if (str != null && !str.equals("")){
                    et_input.setText(str.substring(0,str.length() - 1));
                }
                break;
            // 等号按钮
            case R.id.btn_equal:
                getResult();
                break;
        }

    }

    /*
     *  运算结果
     */
    private void getResult(){

        // 取出显示屏上的内容
        String exp = et_input.getText().toString();
        // 判断显示屏上的内容是否为空
        if (exp == null || exp.equals("")){
            return;
        }
        if (!exp.contains(" ")){
            return;
        }else {
            if (clear_flag){
                clear_flag = false;
                return;
            }
            clear_flag = true;
            double result = 0;
            // 运算符前面的字符串
            String s1 = exp.substring(0,exp.indexOf(" "));
            // 截取到的运算符
            String op = exp.substring(exp.indexOf(" ") + 1, exp.indexOf(" ") + 2);
            // 运算符后面的字符串
            String s2 = exp.substring(exp.indexOf(" ") + 3);
            if (!s1.equals("") && !s2.equals("")){
                double d1 = Double.parseDouble(s1);
                double d2 = Double.parseDouble(s2);
                if (op.equals("+")){
                    result = d1 + d2;
                }else if (op.equals("-")){
                    result = d1 - d2;
                }else if (op.equals("×")){
                    result = d1 * d2;
                }else if (op.equals("÷")){
                    if (d2 == 0){
                        result = 0;
                    }else {
                        result = d1 / d2;
                    }
                }
                // s1 s2 都没有小数点，是int型数据
                if (!s1.contains(".") && !s2.contains(".") && !op.equals("÷")){
                    int r = (int) result;
                    et_input.setText(r + "");
                }else {
                    et_input.setText(result + "");
                }
            }else if (!s1.equals("") && s2.equals("")){
                String expStr = exp.substring(0,exp.indexOf(" "));
                et_input.setText(expStr);
            }else if (s1.equals("") && !s2.equals("")){
                double d2 = Double.parseDouble(s2);
                if (op.equals("+")){
                    result = 0 + d2;
                }else if (op.equals("-")){
                    result = 0 - d2;
                }else if (op.equals("×")){
                    result = 0;
                }else if (op.equals("÷")){
                    result = 0;
                }
                // s1 s2 都没有小数点，是int型数据
                if (!s1.contains(".") && !s2.contains(".")){
                    int r = (int) result;
                    et_input.setText(r + "");
                }else {
                    et_input.setText(result + "");
                }
            }else {
                et_input.setText("");
            }
        }

    }
}
