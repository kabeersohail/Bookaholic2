package com.example.sohail.bookaholic;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookButtonBase;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInApi;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.signin.SignIn;
import com.google.android.gms.signin.SignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import br.com.bloder.magic.view.MagicButton;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.firebase.auth.GoogleAuthProvider;

import java.lang.reflect.Array;
import java.util.Arrays;


public class MainActivity extends AppCompatActivity implements View.OnClickListener,GoogleApiClient.OnConnectionFailedListener{
    private static final int RC_SIGN_IN = 1;
    private static final String TAG = ".MainActivity";
    GoogleSignInClient mGoogleSignInClient;
    CallbackManager mCallbackManager;
    private MagicButton Signin,facebook;
    Button JavaSignUp,JavaSignIn;
    private GoogleApiClient apiClient;
    private static final String EMAIL = "email";
    CallbackManager callbackManager;
    private static final int Req_Code = 9001;
    EditText JavaEmail,JavaPassword;
    ProgressDialog progressDialog;
    static FirebaseAuth firebaseAuth;
    static FirebaseUser firebaseUser;
    LoginButton loginButton;
    @Override
    protected void onStart() {
        super.onStart();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if(account!=null){
            updateUIGoogle(account);
        }
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if(currentUser!=null){
            updateUIFirebase(currentUser);
        }
    }

    private void updateUIGoogle(GoogleSignInAccount account) {
        if(account != null){
            startActivity(new Intent(MainActivity.this,Next.class));
        }
        else{
//            Toast.makeText(MainActivity.this,"Logged out",Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUIFirebase(FirebaseUser account) {
        if(account != null){
            progressDialog.dismiss();
            startActivity(new Intent(MainActivity.this,Next.class));
        }
        else{
//            Toast.makeText(MainActivity.this,"Logged out",Toast.LENGTH_SHORT).show();
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        progressDialog.setMessage("Sigining in");
        progressDialog.show();
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this,"Google success",Toast.LENGTH_SHORT).show();
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            updateUIFirebase(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
//                            Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            updateUIFirebase(null);
                        }

                        // ...
                    }
                });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);
        progressDialog.setMessage("Sigining in");
        progressDialog.show();
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            updateUIFirebase(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUIFirebase(null);
                        }

                        // ...
                    }
                });
    }

//    private void signIn() {
//        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
//        startActivityForResult(signInIntent, RC_SIGN_IN);
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        setContentView(R.layout.activity_main);

        Signin = findViewById(R.id.XmlGoogleSignIn1);
        facebook = findViewById(R.id.magic_button_facebook);
        JavaSignIn = findViewById(R.id.XmlSignIn);
        JavaSignUp = findViewById(R.id.XmlSignUp);
        JavaEmail = findViewById(R.id.XmlEmail);
        JavaPassword = findViewById(R.id.XmlPassword);
        loginButton = (LoginButton) findViewById(R.id.login_button);

        JavaSignUp.setOnClickListener(this);
        JavaSignIn.setOnClickListener(this);
        loginButton.setReadPermissions(Arrays.asList(EMAIL));

        callbackManager = CallbackManager.Factory.create();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();



        Signin.setMagicButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                SignIn();
                signIn();
            }
        });
        facebook.setMagicButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logInWithReadPermissions(MainActivity.this, Arrays.asList("email", "public_profile"));
                LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.d(TAG, "facebook:onSuccess:" + loginResult);
                        handleFacebookAccessToken(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        Log.d(TAG, "facebook:onCancel");
                        // ...
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Log.d(TAG, "facebook:onError", error);
                        // ...
                    }
                });
            }
        });

        mCallbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = findViewById(R.id.login_button);
        loginButton.setReadPermissions("email", "public_profile");

//        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
//            @Override
//            public void onSuccess(LoginResult loginResult) {
//                Log.d(TAG, "facebook:onSuccess:" + loginResult);
//                handleFacebookAccessToken(loginResult.getAccessToken());
//            }
//
//            @Override
//            public void onCancel() {
//                Log.d(TAG, "facebook:onCancel");
//                // ...
//            }
//
//            @Override
//            public void onError(FacebookException error) {
//                Log.d(TAG, "facebook:onError", error);
//                // ...
//            }
//        });

        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, options);
        apiClient = new GoogleApiClient.Builder(this).enableAutoManage(this,this).addApi(Auth.GOOGLE_SIGN_IN_API, options).build();
        progressDialog = new ProgressDialog(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.GoogleSignIn : signIn();
                break;
        }

        if(v == JavaSignIn){
            String InputEmail,InputPassword;

            InputEmail = JavaEmail.getText().toString().trim();
            InputPassword = JavaPassword.getText().toString().trim();

            if(TextUtils.isEmpty(InputEmail) && TextUtils.isEmpty(InputPassword)){
                Toast.makeText(this,"Please enter data",Toast.LENGTH_SHORT).show();
                return;
            }

            if(TextUtils.isEmpty(InputEmail)){
                Toast.makeText(this,"Please enter email",Toast.LENGTH_SHORT).show();
                return;
            }

            if(TextUtils.isEmpty(InputPassword)) {
                Toast.makeText(this,"Please enter password",Toast.LENGTH_SHORT).show();
                return;
            }

            progressDialog.setMessage("Sigining in");
            progressDialog.show();

            firebaseAuth.signInWithEmailAndPassword(InputEmail,InputPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        Log.d("TAG","SignInWithEmailAndPassword:Successful");

                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this,"Sign in successful",Toast.LENGTH_SHORT).show();
                        updateUIFirebase(firebaseUser);
//                        startActivity(new Intent(MainActivity.this,Next.class));

                    }
                    else {
                        Log.w("Tag","SignInWithEmailAndPassword:Failure",task.getException());
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this,"Authentication failed",Toast.LENGTH_SHORT).show();
                        updateUIFirebase(null);
                    }
                }
            });

        }

        if(v == JavaSignUp){
            startActivity(new Intent(MainActivity.this,Registration.class));
        }

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

//    private void SignIn(){
//        Intent intent = Auth.GoogleSignInApi.getSignInIntent(apiClient);
//        startActivityForResult(intent,Req_Code);
//    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }



    private void handleResult(GoogleSignInResult result){
        if(result.isSuccess()){
            GoogleSignInAccount account = result.getSignInAccount();
            UpdateUi(true);
        }
        else {
            UpdateUi(false);
        }
    }

    private void UpdateUi(boolean isSignedin) {
        if(isSignedin) {
            finish();
            Toast.makeText(MainActivity.this,"Signed in",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MainActivity.this,Next.class));
        }

        else{
            Signin.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Req_Code){
            GoogleSignInResult googleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleResult(googleSignInResult);
        }

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }

        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }
}

