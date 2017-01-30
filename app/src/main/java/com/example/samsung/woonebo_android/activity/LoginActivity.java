package com.example.samsung.woonebo_android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.samsung.woonebo_android.R;

import com.example.samsung.woonebo_android.service.AuthService;
import com.example.samsung.woonebo_android.service.ServiceGenerator;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginActivity extends AppCompatActivity {
    private EditText inputEmail;
    private EditText inputPassword;
    private ProgressBar progressBar;
    private Button btnSignup;
    private Button btnLogin;
    private Button btnReset;
    private LoginButton btnFacebookLogin;
    private CallbackManager callbackManager;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    private String idToken = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());

        //페이스북 로그인 응답을 처리할 콜백 관리자
        callbackManager = CallbackManager.Factory.create();

        //set the view now
        setContentView(R.layout.activity_login);

        btnFacebookLogin = (LoginButton)findViewById(R.id.login_button);
        btnFacebookLogin.setReadPermissions("email");
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btnSignup = (Button) findViewById(R.id.btn_signup);
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnReset = (Button) findViewById(R.id.btn_reset_password);


        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        //로그인 상태 확인 여부
        isLoginEnabled();

        //페이스북 로그인 버튼에 콜백 등록
        btnFacebookLogin.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) { signInWithFacebook(loginResult.getAccessToken()); }

            @Override
            public void onCancel() {
                Log.i("onCancel:", "Facebook Login attempt canceled");
            }

            @Override
            public void onError(FacebookException error) {
                Log.i("onError:", "Error");
            }
        });

        //회원이 아니라면, 계정등록 페이지로 이동.
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
            }
        });

        //비밀번호를 모른다면,
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
            }
        });

        //로그인 버튼
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInWithEmailPassword();
            }
        });
    }// onCreate() END..

    private void isLoginEnabled() {
        //로그인이 되어 있다면
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            Log.d("alreadyLogin", auth.getCurrentUser().getUid());

            signInWithToken(); //사용자 토큰 가져오기
        }
    }

    //페이스북을 통한 로그인
    private void signInWithFacebook(AccessToken token) {
        Log.i("onSuccess : ", token.getUserId() + "," + token.getToken());

        progressBar.setVisibility(View.VISIBLE);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.i("onComplte", "signInWithCredential:onComplete:" + task.isSuccessful());

                        //로그인 실패
                        if (!task.isSuccessful()) {
                            Log.w("onFailure", "signInWithCredential", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Log.i("LogInSuccess", "success");

                            signInWithToken();
                        }
                    }
                });
    }

    //이메일 패스워드를 통한 로그인
    private void signInWithEmailPassword() {
        final String email = inputEmail.getText().toString();
        final String password = inputPassword.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);

        //authenticate user
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        progressBar.setVisibility(View.GONE);

                        if (!task.isSuccessful()) {
                            // there was an error
                            if (password.length() < 3) {
                                inputPassword.setError(getString(R.string.minimum_password));
                            } else {
                                Toast.makeText(LoginActivity.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Log.i("LogInSuccess", "success");

                            signInWithToken();
                        }
                    }
                });
    }

    //사용자 토큰 가져오기
    private void signInWithToken() {
        FirebaseAuth.getInstance().getCurrentUser().getToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
            @Override
            public void onComplete(@NonNull Task<GetTokenResult> task) {
                if (task.isSuccessful()) {
                    idToken = task.getResult().getToken();
                    Log.i("idToken3", idToken);

                    AuthService authService = ServiceGenerator.createService(AuthService.class);
                    authService.getAuth(idToken).enqueue(new Callback<JsonObject>() {
                        @Override
                        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                            Log.i("code", response.code()+"");
                            Intent intent = new Intent(LoginActivity.this, KioskViewActivity.class);
                            intent.putExtra("idToken", idToken);
                            startActivity(intent);
                            finish();
                        }

                        @Override
                        public void onFailure(Call<JsonObject> call, Throwable t) {
                            t.printStackTrace();
                            Log.i("tokenfail", "fail");
                        }
                    });
                } else {
                    Log.i("idToken3", "faile");
                }
            }
        });
    }

    //페이스북
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);

    }
}