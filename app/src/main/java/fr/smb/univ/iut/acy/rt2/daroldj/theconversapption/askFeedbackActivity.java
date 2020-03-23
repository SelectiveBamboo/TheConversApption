package fr.smb.univ.iut.acy.rt2.daroldj.theconversapption;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class askFeedbackActivity extends AppCompatActivity {

    private final static String TAG = askFeedbackActivity.class.getName();

    private SharedPreferences sharedPreferences;

    private EditText feedback;
    private Button sendFeedback;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_feedback);

        this.feedback = super.findViewById(R.id.editFeedback);

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

        this.sendFeedback = super.findViewById(R.id.sendFeedback);
        this.sendFeedback.setEnabled(false);
    }

    public void sendFeedback(View view)
    {
        Log.d(TAG, "sendFeedack");

        if (this.feedback.getText().length() > 20)
        {
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
        new Sender(this, "10.102.251.6", feedbackToSend);
    }
}
