package es.ernesto.minicars.extensions;

import org.andengine.opengl.font.Font;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.BaseGameActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.text.InputFilter;
import android.view.Gravity;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
 
public class InputText {
 
        private final String    mTitle;
        private final String    mMessage;
        private String          mValue;
        private BaseGameActivity mContext;
        public boolean exit = false;

        public InputText(final String title, final String message, Font font, 
        				 int textOffsetX, int textOffsetY, VertexBufferObjectManager vbo, BaseGameActivity context) {
                this.mMessage = message;
                this.mTitle = title;
                this.mContext = context;
        }
 
        public String getText() {
                return this.mValue;
        }
 
        public void setText(String text) {
                this.mValue = text;
        }
 
        public void showTextInput() {
                mContext.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                                final AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
                                alert.setTitle(InputText.this.mTitle);
                                alert.setMessage(InputText.this.mMessage);
 
                                final EditText editText = new EditText(mContext);
                                editText.setTextSize(20f);
                                editText.setText(InputText.this.mValue);
                                editText.setGravity(Gravity.CENTER_HORIZONTAL);
                                int maxLength = 10;
                                InputFilter[] FilterArray = new InputFilter[1];
                                FilterArray[0] = new InputFilter.LengthFilter(maxLength);
                                editText.setFilters(FilterArray);
                                alert.setView(editText);
 
                                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                        	if (editText.getText().length() > 0) {
                                        		setText(editText.getText().toString());
                                        	} else {
                                        		setText("Player");
                                        	}
                                        	exit = true;
                                        }
                                });
 
                                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                        	exit = true;
                                        }
                                });
 
                                final AlertDialog dialog = alert.create();
                                dialog.setOnShowListener(new OnShowListener() {
                                        @Override
                                        public void onShow(DialogInterface dialog) {
                                                editText.requestFocus();
                                                final InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                                                imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
                                        }
                                });
                                dialog.show();
                                if (exit) {
                                	dialog.dismiss();               
                                }
                        }
                });
        }
 
}