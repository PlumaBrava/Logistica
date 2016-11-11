package com.nextnut.logistica;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nextnut.logistica.modelos.EmpresaPerfil;
import com.nextnut.logistica.modelos.Usuario;
import com.rey.material.widget.ProgressView;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static com.nextnut.logistica.util.Constantes.ESQUEMA_USER_PROPUETO_EMPRESA;
import static com.nextnut.logistica.util.KeyMailConverter.getKeyFromEmail;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity

        extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private static final String TAG = "LoginActivity";
    private static final String USER_LIST = "UserList";
    private static final int RC_SIGN_IN = 9001;
    private static final int RC_SIGN_IN_FACEBOOK = 64206;
    private static final int REQUEST_READ_CONTACTS = 0;
//    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private Button mSignInButton;
    private Button mSignUpButton;
    private ProgressView mProgressView;
    private View mLoginFormView;

    private SignInButton signInButtonGoogle;
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private CallbackManager mCallbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // Check auth on Activity start
                    if (mAuth.getCurrentUser() != null) {
                        onAuthSuccess(mAuth.getCurrentUser());
                    }

                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);
        setupActionBar();

        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
//        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
//                if (id == R.id.login || id == EditorInfo.IME_NULL) {
//                    attemptLogin();
//                    return true;
//                }
//                return false;
//            }
//        });

        // Add code to print out the key hash
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.nextnut.logistica",
                    PackageManager.GET_SIGNATURES);
            Log.d("KeyHash:", "ingreso");
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.d("KeyHash:", "NameNotFoundException " + e.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.d("KeyHash:", "NoSuchAlgorithmException " + e.toString());

        }


//        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
//        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                attemptLogin();
//            }
//        });

        mSignInButton = (Button) findViewById(R.id.button_sign_in);
        mSignUpButton = (Button) findViewById(R.id.button_sign_up);

        // Click listeners
        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInFormEmail();
            }
        });
        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUp();
            }
        });


        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = (ProgressView) findViewById(R.id.login_progress);
        mProgressView.setVisibility(View.GONE);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(getString(R.string.web_client_id))
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */,
                        this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        signInButtonGoogle = (SignInButton) findViewById(R.id.sign_in_button);
        signInButtonGoogle.setSize(SignInButton.SIZE_STANDARD);
        signInButtonGoogle.setScopes(gso.getScopeArray());
        signInButtonGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.sign_in_button:
                        showProgressDialog();
                        showProgress(true);
                        signIn();
                        break;
                    // ...
                }
            }
        });

        // Initialize Facebook Login button
        mCallbackManager = CallbackManager.Factory.create();

        LoginButton loginButton = (LoginButton) findViewById(R.id.button_facebook_login);
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProgressDialog();
                showProgress(true);
            }
        });

        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "signInWithCredentialfacebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "signInWithCredentialfacebook:onCancel");
                // ...
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "signInWithCredentialfacebook:onError", error);
                // ...
            }
        });

        TextView olvidoContrasena = (TextView) findViewById(R.id.texto_olvidaste_contrasena);
        olvidoContrasena.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isEmailValid(mEmailView.getText().toString())) {
                    showProgress(true);
                    mAuth.sendPasswordResetEmail(mEmailView.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    showProgress(false);

                                    if (task.isSuccessful()) {
                                        Toast.makeText(LoginActivity.this, "Se ha enviado un mail para cabiar la clave.",
                                                Toast.LENGTH_SHORT).show();
                                        Log.d(TAG, "Email sent.");
                                    }
                                    Toast.makeText(LoginActivity.this, "No se puedo envial un mail.",
                                            Toast.LENGTH_SHORT).show();
                                    Log.d(TAG, "Email sent error.");
                                }
                            });
                } else {
                    Toast.makeText(LoginActivity.this, "EMail incorrecto.",
                            Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "EMail incorrecto.");
                }
            }
        });


    }


    @Override
    public void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);

////         Check auth on Activity start
//        if (mAuth.getCurrentUser() != null) {
//            onAuthSuccess(mAuth.getCurrentUser());
//        }
    }

    @Override
    public void onStop() {
        super.onStop();

        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult:" + requestCode);

        if (requestCode == RC_SIGN_IN_FACEBOOK) { //Facebook result
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                // ...
            }
        }

//
//        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
//        if (requestCode == RC_SIGN_IN) {
//            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
//            handleSignInResult(result);
//        }
    }


    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "signInWithCredentialfacebook:token" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredentialfacebook:onComplete:" + task.isSuccessful());
                        hideProgressDialog();
                        showProgress(false);

                        hideProgressDialog();

                        if (task.isSuccessful()) {
                            onAuthSuccess(task.getResult().getUser());
                            Log.d(TAG, "signInWithCredentialfacebook:email:" + task.getResult().getUser().getEmail().toString());
                            Log.d(TAG, "signInWithCredentialfacebook:Display Name:" + task.getResult().getUser().getDisplayName());
                            Log.d(TAG, "signInWithCredentialfacebook:PhotoUrl:" + task.getResult().getUser().getPhotoUrl());
                            Log.d(TAG, "signInWithCredentialfacebook:ProviderData:" + task.getResult().getUser().getProviderData().toString());
                            Log.d(TAG, "signInWithCredentialfacebook:ProviderID:" + task.getResult().getUser().getProviderId().toString());

                            String userId = task.getResult().getUser().getUid();
                            String username = usernameFromEmail(task.getResult().getUser().getEmail());
                            String email = task.getResult().getUser().getEmail();
                            String photoURL = task.getResult().getUser().getPhotoUrl().toString();
                            String status = "inicial";
                            Boolean activo = true;
//                            Perfil perfil = new Perfil();
//                            perfil.setPerfilAdministrador();

                            // Write new user
                            writeNewUser(userId, username, email, photoURL, status, activo);


                        } else {

                            Log.d(TAG, "signInWithCredentialfacebook:" + task.getException().getMessage().toString());
                            Toast.makeText(LoginActivity.this, task.getException().getMessage().toString(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    private void signInFormEmail() {
        Log.d(TAG, "signInFormEmail");
        if (!validateForm()) {
            return;
        }
        showProgress(true);
        showProgressDialog();
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInFormEmail:onComplete:" + task.isSuccessful());
                        hideProgressDialog();
                        showProgress(false);
                        if (task.isSuccessful()) {
                            onAuthSuccess(task.getResult().getUser());
                        } else {
                            Log.d(TAG, "signInFormEmail:" + task.getException().getMessage().toString());
                            Toast.makeText(LoginActivity.this, task.getException().getMessage().toString(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void onAuthSuccess(FirebaseUser user) {

        // Verifica que si existe ese usuario en NewUser
        Log.d(TAG, "onAuthSuccess: Uid:" + user.getUid());
        Query userEmpresa= mDatabase.child(ESQUEMA_USER_PROPUETO_EMPRESA).child(getKeyFromEmail(user.getEmail() ));
        userEmpresa.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                       Log.d(TAG, "onAuthSuccess:getChildrenCount: " + dataSnapshot.getChildrenCount());

                        if(dataSnapshot.getChildrenCount()<=0){
                // Crear una empresa
                            Log.d(TAG, "onAuthSuccess:getChildrenCount()<=0- Crear Empresa " );

                        }

                        else {
                            Log.d(TAG, "onAuthSuccess:getChildrenCount: - Elegir  o Crear Empresa ");
                            for (DataSnapshot messageSnapshot: dataSnapshot.getChildren()) {
//                                String category = (String) messageSnapshot.child("category").getValue();
                                Log.d(TAG, "onAuthSuccess-KEY"+ messageSnapshot.getKey());
                                EmpresaPerfil u = messageSnapshot.getValue(EmpresaPerfil.class);
                                Log.d(TAG, "onAuthSuccess-EMPRESA NOMBRE:"+ u.getEmpresa().getNombre()+" - "+u.getEmpresa().getCiudad());
                                Log.d(TAG, "onAuthSuccess-Perfil:"+ u.getPerfil().getClientes()+" - "+u.getPerfil().getUsuarios());
                            }
                        }
                        }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d(TAG, "onAuthSuccess "+ databaseError.toString());

                    }
                })



        ;



        Log.d(TAG, "onAuthSuccess:user " + user.toString());

        // Go to MainActivity
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }

    private String usernameFromEmail(String email) {
        if (email.contains("@")) {
            return email.split("@")[0];
        } else {
            return email;
        }
    }

    private boolean validateForm() {
        boolean result = true;
        if (TextUtils.isEmpty(mEmailView.getText().toString())) {
            mEmailView.setError(getResources().getString(R.string.Required));
            result = false;
        } else {
            mEmailView.setError(null);
        }

        if (TextUtils.isEmpty(mPasswordView.getText().toString())) {
            mPasswordView.setError(getResources().getString(R.string.Required));
            result = false;
        } else {
            mPasswordView.setError(null);
        }

        return result;
    }

    // [START basic_write]
    private void writeNewUser(String userId, String username, String email, String photoURL, String status, Boolean activo) {
        Usuario user = new Usuario(username, email, photoURL, status, activo);
        mDatabase.child("users").child(userId).setValue(user);
    }
    // [END basic_write]


    private void signUp() {
        Log.d(TAG, "signUp");
        if (!validateForm()) {
            return;
        }

        showProgressDialog();
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "EmailAndPassword-createUser:onComplete:" + task.isSuccessful());

                        hideProgressDialog();

                        if (task.isSuccessful()) {
                            onAuthSuccess(task.getResult().getUser());
                            Log.d(TAG, "EmailAndPassword:email:" + task.getResult().getUser().getEmail().toString());
                            Log.d(TAG, "EmailAndPassword:Display Name:" + task.getResult().getUser().getDisplayName());
                            Log.d(TAG, "EmailAndPasswordk:PhotoUrl:" + task.getResult().getUser().getPhotoUrl());
                            Log.d(TAG, "EmailAndPassword:ProviderData:" + task.getResult().getUser().getProviderData().toString());
                            Log.d(TAG, "EmailAndPassword:ProviderID:" + task.getResult().getUser().getProviderId().toString());

                            String userId = task.getResult().getUser().getUid();
                            String username = usernameFromEmail(task.getResult().getUser().getEmail());
                            String email = task.getResult().getUser().getEmail();
                            String photoURL = null;// no existe la foto al momento de crear el usuario
                            String status = "inicial";
                            Boolean activo = true;
//                            Perfil perfil = new Perfil();
//                            perfil.setPerfilAdministrador();

                            // Write new user
                            writeNewUser(userId, username, email, photoURL, status, activo);


                        } else {

                           Log.d(TAG, "EmailAndPassword:Error:" + task.getException().getMessage().toString());
                           Toast.makeText(LoginActivity.this, task.getException().getMessage().toString(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }


    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount account = result.getSignInAccount();

            Bundle arguments = new Bundle();
            arguments.putString(MainActivity.USER_DISPLAY_NAME, result.getSignInAccount().getDisplayName());
            arguments.putString(MainActivity.USER_ID, result.getSignInAccount().getId());
            firebaseAuthWithGoogle(account);

            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtras(arguments);
            startActivity(intent);
            finish();


        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                        hideProgressDialog();
                        showProgress(false);
                        hideProgressDialog();

                        if (task.isSuccessful()) {
                            onAuthSuccess(task.getResult().getUser());
                            Log.d(TAG, "AuthWithGoogle:email:" + task.getResult().getUser().getEmail().toString());
                            Log.d(TAG, "AuthWithGoogle:Display Name:" + task.getResult().getUser().getDisplayName());
                            Log.d(TAG, "AuthWithGoogle:PhotoUrl:" + task.getResult().getUser().getPhotoUrl());
                            Log.d(TAG, "AuthWithGoogle:ProviderData:" + task.getResult().getUser().getProviderData().toString());
                            Log.d(TAG, "AuthWithGoogle:ProviderID:" + task.getResult().getUser().getProviderId().toString());

                            String userId = task.getResult().getUser().getUid();
                            String username = usernameFromEmail(task.getResult().getUser().getEmail());
                            String email = task.getResult().getUser().getEmail();
                            String photoURL = task.getResult().getUser().getPhotoUrl().toString();
                            String status = "inicial";
                            Boolean activo = true;
//                            Perfil perfil = new Perfil();
//                            perfil.setPerfilAdministrador();

                            // Write new user
                            writeNewUser(userId, username, email, photoURL, status, activo);


                        } else {

                            Log.d(TAG, "AuthWithGoogle:Error:" + task.getException().getMessage().toString());
                            Toast.makeText(LoginActivity.this, task.getException().getMessage().toString(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }


    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // Show the Up button in the action bar.
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
//        if (mAuthTask != null) {
//            return;
//        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
//            mAuthTask = new UserLoginTask(email, password);
//            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        Log.w(TAG, "showProgress-show" + show);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
            Log.w(TAG, "showProgress-Build.VERSION.SDK_INT a" + Build.VERSION.SDK_INT);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            Log.w(TAG, "showProgress-Build.VERSION.SDK_INT b" + Build.VERSION.SDK_INT);

            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
//    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
//
//        private final String mEmail;
//        private final String mPassword;
//
//        UserLoginTask(String email, String password) {
//            mEmail = email;
//            mPassword = password;
//        }
//
//        @Override
//        protected Boolean doInBackground(Void... params) {
//
//
//            SharedPreferences sharedPreferences =
//                    PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
//            String user = sharedPreferences.getString(USER_LIST, "");
//            String[] userlists = user.split(",");
//
//            for (String credential : userlists) {
//                String[] pieces = credential.split(":");
//                if (pieces[0].equals(mEmail)) {
//                    // Account exists, return true if the password matches.
//                    return pieces[1].equals(mPassword);
//                }
//            }
//
//            StringBuilder sb = new StringBuilder();
//            for (int i = 0; i < userlists.length; i++) {
//                sb.append(userlists[i]).append(",");
//            }
//            sb.append(user + mEmail + ":" + mPassword).append(",");
//
//            sharedPreferences.edit().putString(USER_LIST, sb.toString()).apply();
//
//            return true;
//        }
//
//        @Override
//        protected void onPostExecute(final Boolean success) {
////            mAuthTask = null;
//            showProgress(false);
//
//            if (success) {
//                Bundle arguments = new Bundle();
//                arguments.putString(MainActivity.USER_DISPLAY_NAME, mEmail);
//                arguments.putString(MainActivity.USER_ID, null);
//
//                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                intent.putExtras(arguments);
//                startActivity(intent);
//
//                finish();
//            } else {
//                mPasswordView.setError(getString(R.string.error_incorrect_password));
//                mPasswordView.requestFocus();
//            }
//        }
//
//        @Override
//        protected void onCancelled() {
////            mAuthTask = null;
//            showProgress(false);
//        }
//    }

    private ProgressDialog mProgressDialog;

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage("Loading...");
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }


}

