package fr.smb.univ.iut.acy.rt2.daroldj.theconversapption;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

public class askFeedbackActivity extends AppCompatActivity {

    private final static String TAG = askFeedbackActivity.class.getName();

    private SharedPreferences sharedPreferences;

    private EditText feedback;
    private Button sendFeedback;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_feedback);

        this.feedback = super.findViewById(R.id.editFeedback);

        context = this;

        this.feedback.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence text, int start, int count, int after)
            {
                askFeedbackActivity.this.feedback.setError(null);
            }

            @Override
            public void onTextChanged(CharSequence text, int start, int before, int count)
            {
                askFeedbackActivity.this.sendFeedback.setEnabled(true);
            }

            @Override
            public void afterTextChanged(Editable text)
            {
                if (text.length() == 0)
                {
                    askFeedbackActivity.this.sendFeedback.setEnabled(false);
                }
            }
        });

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        this.sendFeedback = super.findViewById(R.id.sendFeedback);
        this.sendFeedback.setEnabled(false);
    }

    public void sendFeedback(View view)
    {
        Log.d(TAG, "sendFeedack");

        if (this.feedback.getText().length() > 1)
        {
            //for whether the user exit the activity, he could come back and have his text again (planned)
            SharedPreferences.Editor editor = this.sharedPreferences.edit();
            editor.putString("feedback", this.feedback.getText().toString());
            editor.commit();

            this.sendFeedbackToServer(this.feedback.getText().toString());
        }
        else
        {
            this.feedback.setError("Message too short");
        }
    }

    private void sendFeedbackToServer(String feedbackToSend)
    {
        new Sender(this, "192.168.1.65", feedbackToSend);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        // Check if the key event was the Back button and if there's history
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
            Intent intent = new Intent(context, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            this.finish();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}
