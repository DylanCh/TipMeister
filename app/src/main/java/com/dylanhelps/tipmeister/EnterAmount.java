package com.dylanhelps.tipmeister;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import java.text.DecimalFormat;

public class EnterAmount extends AppCompatActivity {

    private Button calculateBtn;
    private EditText enterAmountEditText;
    private TextView displayTipTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_amount);

        calculateBtn = (Button) findViewById(R.id.calculateBtn);
        enterAmountEditText = (EditText) findViewById(R.id.enterAmountEditText);
        displayTipTextView = (TextView) findViewById(R.id.displayTipTextView);

        calculateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    double bill = Double.parseDouble(String.valueOf(enterAmountEditText.getText()));
                    DecimalFormat df = new DecimalFormat("####0.00");
                    StringBuilder displayContent = new StringBuilder(
                            "\n15% Tip: $"+df.format(bill*0.15)+" Total: $"+df.format(bill+bill*0.15)+"\n");
                    displayContent.append("20% Tip: $" + df.format(bill*0.2)+ " Total: $"+df.format(bill+bill*0.2)+"\n");
                    displayContent.append("25% Tip: $" + df.format(bill*0.25)+ " Total: $"+df.format(bill+bill*0.25)+"\n");
                    displayTipTextView.setText(displayContent.toString());
                }
                catch (NumberFormatException nfe){
                    AlertDialog alertDialog = new AlertDialog.Builder(EnterAmount.this).create();
                    alertDialog.setTitle("Alert");
                    alertDialog.setMessage("Entry is not in number format");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }// end catch
            }
        });
    }
}
