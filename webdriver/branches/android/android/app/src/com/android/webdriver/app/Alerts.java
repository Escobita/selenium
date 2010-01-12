
package com.android.webdriver.app;

import com.android.webdriver.app.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.widget.EditText;
import android.widget.FrameLayout;


public class Alerts {
	/**
	 * Interface definition for a callback to be invoked when any property of
	 * the session is changed.
	 */
	public static interface ButtonListener {
	    /**
	     * Called after any session was updated.
	     *
	     * @param changedSession Session object that was changed.
	     */
	    public abstract void onButtonClicked(int which);
	}
	
	
	/**
	 * Displays a dialog box with a single EditText widget.
	 * Returns text that user enters.
	 * 
	 * @param context Context
	 * @return User-inputed text
	 */
	public static void inputBox(Context context, Handler handler) {
		inputBox(context, handler, "Please enter:", "");
	}
	
	/**
	 * Displays a dialog box with a single EditText widget.
	 * Returns text that user enters.
	 * 
	 * @param context Context
	 * @param prompt Prompt text displayed in header
	 * @param presetText Default value for text edit
	 * @return User-inputed text
	 */
	public static void inputBox(Context context, Handler handler,
			String prompt, String presetText) {
		final FrameLayout fl = new FrameLayout(context); 
		final EditText input = new EditText(context); 
	
		fl.addView(input, new FrameLayout.LayoutParams
				(FrameLayout.LayoutParams.FILL_PARENT,
				 FrameLayout.LayoutParams.WRAP_CONTENT)); 

	    // "Answer" callback.
	    final Message msg = Message.obtain();
	    msg.setTarget(handler);
	    
		input.setText(presetText);
		new AlertDialog.Builder(context)
		     .setView(fl)
		     .setTitle(prompt)
		     .setPositiveButton("OK", new DialogInterface.OnClickListener(){ 
		          public void onClick(DialogInterface d, int which) { 
		               d.dismiss();
		               msg.obj = input.getText().toString();
		               msg.sendToTarget();
		          }
		     })
		     .setNegativeButton("Cancel", new DialogInterface.OnClickListener(){ 
		          public void onClick(DialogInterface d, int which) { 
		               d.dismiss();
		          } 
		     }).create().show();
	}

	/**
	 * Displays a dialog box with text message and Yes/No buttons.
	 * Returns true if 'Yes' was pressed.
	 * 
	 * @param context Context
	 * @param prompt Text to display in the dialog body
	 * @return
	 */
	public static void Confirm(Context context, Handler handler,
			String prompt) {

		// Positive answer callback.
	    final Message msg = Message.obtain();
	    msg.setTarget(handler);
		
		new AlertDialog.Builder(context)
        .setIcon(R.drawable.alert_dialog_icon)
        .setTitle("Confirm")
        .setMessage(prompt)
        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface d, int which) {
            	d.dismiss();
            	msg.sendToTarget();
            }
        })
        .setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface d, int which) {
            	d.dismiss();
            }
        })
        .create().show();
	}
	
	public static void Message(Context context, String message) {
      new AlertDialog.Builder(context)
      .setIcon(R.drawable.alert_dialog_icon)
      .setMessage(message)
      .create().show();
	}
}