/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.webdriver.app;

import com.android.webdriver.app.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.widget.EditText;
import android.widget.FrameLayout;

/**
 * This class supplies static methods to display simple alert and input
 * boxes and to return results to the calling method.
 * <p>
 * These convenience methods wrap standard Android alert dialogs  
 */
public class Alerts {
	/**
	 * Displays a dialog box with a single EditText widget.
	 * Returns text that user enters.
	 * 
	 * @param context Context
     * @param handler Handler that receives result message
	 */
	public static void inputBox(Context context, Handler handler) {
		inputBox(context, handler, "Please enter:", "");
	}
	
	/**
	 * Displays a dialog box with a single EditText widget.
	 * Returns text that user enters.
	 * 
	 * @param context Context
     * @param handler Handler that receives result message
	 * @param prompt Prompt text displayed in header
	 * @param presetText Default value for text edit
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
	 * @param handler Handler that receives result message
	 * @param prompt Text to display in the dialog body
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
	
	/**
	 * Displays a dialog box with text message.
	 * 
	 * @param context Android context
	 * @param message Text to display in the dialog body
	 */
	public static void Message(Context context, String message) {
      new AlertDialog.Builder(context)
      .setIcon(R.drawable.alert_dialog_icon)
      .setMessage(message)
      .create().show();
	}
}