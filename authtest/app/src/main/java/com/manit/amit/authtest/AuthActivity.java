package com.manit.amit.authtest;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class AuthActivity extends AppCompatActivity {

    private ImageView mIco;

    private EditText mPhoneText;
    private EditText mCodeText;

    private TextView mErrorText;

    private ProgressBar mPhoneBar;
    private ProgressBar mCodeBar;

    private Button mSendBtn;

    private int btnType=0;

    private FirebaseAuth mAuth;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        mPhoneText = (EditText) findViewById(R.id.phoneEditText);
        mCodeText = (EditText) findViewById(R.id.codeEditText);

        mPhoneBar = (ProgressBar) findViewById(R.id.phoneProgress);
        mCodeBar = (ProgressBar) findViewById(R.id.CodeProgress);

        mErrorText = (TextView) findViewById(R.id.errorText);

        mSendBtn = (Button) findViewById(R.id.sendBtn);

        mIco = (ImageView) findViewById(R.id.lockIco);

        mAuth = FirebaseAuth.getInstance();

        mSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(btnType == 0) {
                    mPhoneBar.setVisibility(View.VISIBLE);
                    mPhoneText.setEnabled(false);
                    mSendBtn.setEnabled(false);

                    String phoneNumber = mPhoneText.getText().toString();

                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            phoneNumber,
                            60,
                            TimeUnit.SECONDS,
                            AuthActivity.this,
                            mCallbacks
                    );
                }
                else{

                    mSendBtn.setEnabled(true);
                    mCodeBar.setVisibility(View.VISIBLE);

                    String verificationCode = mCodeText.getText().toString();

                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verificationCode);
                    signInWithPhoneAuthCredential(credential);

                }

            }
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                mErrorText.setText("There was some error in verification!!!");
                mErrorText.setVisibility(View.VISIBLE);

            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {


                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;

                btnType=1;

                mPhoneBar.setVisibility(View.INVISIBLE);
                mCodeBar.setVisibility(View.VISIBLE);
                mCodeText.setVisibility(View.VISIBLE);
                mIco.setVisibility(View.VISIBLE);

                mSendBtn.setText("verify Code");
                mSendBtn.setEnabled(true);


                // ...
            }

        };
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            FirebaseUser user = task.getResult().getUser();
                            Intent mainIntent = new Intent(AuthActivity.this, MainActivity.class);
                            startActivity(mainIntent);
                            finish();

                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI

                            mErrorText.setVisibility(View.VISIBLE);

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }

}
