package com.PointToPoint;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import java.util.regex.*;

public class PTPLinkMarginCalc extends Activity {
	
	private EditText TXPower;
	private EditText TXGain;
	private EditText TXCBL;
	private EditText RXGain;
	private EditText RXRSL;
	private TextView Link_Margin_Val;
	private Button LM_BackBtn;
	private Button LM_FresnelBtn;
	private Button LM_RangeBtn;
	
	private float[ ] LMVars;
	private float LMVal = 0;
	private String[ ] LMLabels;
	private String[ ] LMStr;
	
	public void onCreate(Bundle savedInstanceState) {
		
	    super.onCreate(savedInstanceState);    
	    setContentView(R.layout.link_budget); 
	}
	
	public void CalcLMClickHandler(View target) {
		   if (!this.validate()) {
			   return;
		   }else {
			   	LMVal = ( LMVars[0]+LMVars[1] - LMVars[2] + LMVars[3] - LMVars[4]);
			   	Log.i("INFO", " Link Margin is:"+LMVal);
			   	Link_Margin_Val = (TextView) findViewById(R.id.LMCalcVal);
			   	Link_Margin_Val.setText((Float.toString(LMVal)));
		   }
	}
	
	private boolean validate() {
    boolean valid = false;
    int i = 0;
    
    Log.i("INFO", " In LinkMarginCalc validate");   
    
    
        this.TXPower = (EditText) findViewById(R.id.TXPwrVal);
        this.TXGain = (EditText) findViewById(R.id.TXGainVal);
        this.TXCBL = (EditText) findViewById(R.id.CblLossVal1);
        this.RXGain = (EditText) findViewById(R.id.RXGainVal);
        this.RXRSL = (EditText) findViewById(R.id.RSLVal);
        
        Log.i("INFO", "LinkMarginCalc validate after EditText findViewById");
        
        LMVars = new float[5];
        LMLabels = new String[5];
        LMStr = new String[5];
        
        LMLabels[0] = "Tx Power";
        LMLabels[1] = "Tx Gain";
        LMLabels[2] = "Tx Cable Loss";
        LMLabels[3] = "Rx Gain";
        LMLabels[4] = "Rx RSL";
        
        Log.i("INFO", "LinkMarginCalc validate after LMVars init");

        //        LMVars[0] = Integer.parseInt(TXPower.getText().toString());

        
        LMStr[0] = TXPower.getText().toString();
        LMStr[1] = TXGain.getText().toString();
        LMStr[2] = TXCBL.getText().toString();
        LMStr[3] = RXGain.getText().toString();
        LMStr[4] = RXRSL.getText().toString();
        
        Log.i("INFO", "LinkMarginCalc validate after LMVars IntegerparseInt");

        for (i=0;i <=4;i++) {
        			if (!this.isNumeric(LMStr[i])) {
        			    valid = false;
        			    new AlertDialog.Builder(this)
        			      .setMessage(LMLabels[i]+" Must be a numeric value")
        			      .setPositiveButton("Oops",null)
        			      .show();
        			} else {
        					valid = true;
        					LMVars[i] = Float.valueOf(LMStr[i]).floatValue();
        			}
        }

        return valid;
    }
public boolean isNumeric(String numstr) {  
		 return java.util.regex.Pattern.matches("-?\\d+(\\.\\d)?", numstr);  
} 
	
	
}
