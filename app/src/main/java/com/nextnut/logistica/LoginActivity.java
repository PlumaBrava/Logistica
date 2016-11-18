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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nextnut.logistica.modelos.Empresa;
import com.nextnut.logistica.modelos.Perfil;
import com.nextnut.logistica.modelos.Usuario;
import com.rey.material.widget.ProgressView;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static android.content.Intent.EXTRA_USER;
import static com.nextnut.logistica.util.Constantes.ESQUEMA_USERS;
import static com.nextnut.logistica.util.Constantes.ESQUEMA_USER_EMPRESA;
import static com.nextnut.logistica.util.Constantes.ESQUEMA_USER_PERFIL;
import static com.nextnut.logistica.util.Constantes.EXTRA_EMPRESA;
import static com.nextnut.logistica.util.Constantes.EXTRA_EMPRESA_KEY;
import static com.nextnut.logistica.util.Constantes.EXTRA_FIREBASE_URL;
import static com.nextnut.logistica.util.Constantes.EXTRA_PERFIL;
import static com.nextnut.logistica.util.Constantes.EXTRA_USER_KEY;
import static com.nextnut.logistica.util.UtilFirebase.getDatabase;

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

    private String mEmpresaKey;
    private Empresa mEmpresa;

    private String mUserKey;
    private Usuario mUsuario;

    private Perfil mPerfil;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabase = getDatabase().getReference();
        Log.d(TAG, "mDatabase:" + mDatabase.toString());

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // Check auth on Activity start
                    if (mAuth.getCurrentUser() != null) {
                        onAuthSuccess(mAuth.getCurrentUser().getUid());
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


                        if (task.isSuccessful()) {
                            onAuthSuccess(task.getResult().getUser().getUid());
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
                            ;

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
                            // No es necesario llamar a esta funcion esta el listener escuchando y la llama directamente
//                            onAuthSuccess(task.getResult().getUser().getUid());
                        } else {
                            Log.d(TAG, "signInFormEmail:" + task.getException().getMessage().toString());
                            Toast.makeText(LoginActivity.this, task.getException().getMessage().toString(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void onAuthSuccess(final String userKey) {


        /*  Desde este metodo se disparna:
        *       buscarUsuario(userKey)
        *       buscarPerfil(userKey)
        *       buscarEmpresa(userJey)
        *
        *   Se dejan 3 nodos por separado para modificar el perfil de usuario y los datos de la empresa sin tocar a Usario
        *   son mas llamados a la base pero se hacen en una única vez.
        *
        *   Luego estos datos se pasan de una actividad a la otra evitando los llamados a la base.
        *
         */

        buscarUsuario(userKey);

    }

    private void buscarUsuario(final String userKey) {
        /*
        *    Busca los datos de usuario, y se llena la variable global que luego se enviará al resto de las actividades.
        */

        Log.d(TAG, "buscarUsuario: Uid:" + userKey);
        Query userPerfil = mDatabase.child(ESQUEMA_USERS).child(userKey);
        userPerfil.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "buscarUsuario:onDataChange getChildrenCount: " + dataSnapshot.getChildrenCount());

                if (dataSnapshot.getChildrenCount() <= 0) {
                    // Este caso seria un error no deberiamos tener usuarios sin su Key en este nodo
                    // Nos quedamos en esta pantalla y comunicamos el error
                    Toast.makeText(LoginActivity.this, getString(R.string.error_usuario), Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "buscarUsuario:getChildrenCount: )<=0 - " + dataSnapshot.getChildrenCount() + "- ERROR ");

                } else {
                    // Existe el usuario
                    // llenamos la variable global y pasamos a buscar el perfil

                    Log.d(TAG, "buscarUsuario:getChildrenCount: - Existe Usuario ");
                    mUserKey = userKey;
                    mUsuario = dataSnapshot.getValue(Usuario.class);
                    buscarPerfil(userKey);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "buscarUsuario: cancelado " + databaseError.toString());

            }
        });
    }

    private void buscarPerfil(final String userKey) {
        Log.d(TAG, "buscarPerfil: Uid:" + userKey);
        Query userPerfil = mDatabase.child(ESQUEMA_USER_PERFIL).child(userKey);
        userPerfil.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "buscarPerfil:onDataChange getChildrenCount: " + dataSnapshot.getChildrenCount());

                if (dataSnapshot.getChildrenCount() <= 0) {
                    // puede ser que el usuario nunca asigno una empresa, por lo tanto no tiene un perfil
                    // En tal caso asignamos un perfil administrado y seguimos a buscar la empresa.

                    mPerfil = new Perfil();
                    mPerfil.setPerfilAdministrador();
                    buscarEmpresa(mUserKey);
                    Log.d(TAG, "buscarPerfils:getChildrenCount: )<=0 - " + dataSnapshot.getChildrenCount() + "- ERROR ");

                } else if (dataSnapshot.getChildrenCount() == Perfil.NUMERO_DE_VARIABLES) {// 10 por el Numero de variables del modelo Perfil
                    // Existe el perfil, lo seleccionamos y buscamos la empresa
                    // Tiene que existir un solo perfil por usuario.

                    Log.d(TAG, "buscarPerfil:getChildrenCount()==1 ");

                    mPerfil = dataSnapshot.getValue(Perfil.class);

                    buscarEmpresa(userKey);


                } else {
                    Log.d(TAG, "buscarPerfil:getChildrenCount()>NUMERO_DE_VARIABLES ");

                    // es un error tiene que existir un solo perfil asignado al usuario
                    Toast.makeText(LoginActivity.this, getString(R.string.error_perfil), Toast.LENGTH_SHORT).show();
                    FirebaseAuth.getInstance().signOut();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "buscarPerfil: cancelado " + databaseError.toString());

            }
        });
    }

    private void buscarEmpresa(final String userKey) {
        // Verifica que el usuario ha elegido una empresa para trabajar
        // si no lo ha hecho lo envia a Elegir una empresa o Crear una (Empesas List Activity)
        // Para verificar si el usuario ha elegido una empresa nos fijamos en USUARIO_EMPRESA
        //   USUARIO_EMPRESA: Tiene la empresa seleccionda. Siempre tiene que se una solamente
        //                   Modelo: Empresa
        //


        Log.d(TAG, "buscarEmpres: Uid:" + userKey);
        Query userEmpresa = mDatabase.child(ESQUEMA_USER_EMPRESA).child(userKey);
        userEmpresa.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "buscarEmpres:getChildrenCount: " + dataSnapshot.getChildrenCount());
                Log.d(TAG, "buscarEmpres:getKey: " + dataSnapshot.getKey());

                if (dataSnapshot.getChildrenCount() <= 0) {
                    // Seleccionar una empresa o crear una
                    Intent intent = new Intent(getApplication(), EmpresasListActivity.class);
                    intent.putExtra(EXTRA_FIREBASE_URL, mDatabase.getRef().toString());
                    intent.putExtra(EXTRA_USER_KEY, mUserKey);
                    Log.d(TAG, "buscarEmpres:Usuario: " + mUsuario.getUsername());
                    Log.d(TAG, "buscarEmpres:Email: " + mUsuario.getEmail());
                    Log.d(TAG, "buscarEmpres:Status: " + mUsuario.getStatus());
                    Log.d(TAG, "buscarEmpres:Activo: " + mUsuario.getActivo());
                    intent.putExtra(EXTRA_USER, mUsuario);
                    intent.putExtra(EXTRA_EMPRESA_KEY, mEmpresaKey);
                    intent.putExtra(EXTRA_EMPRESA, mEmpresa);
                    intent.putExtra(EXTRA_PERFIL, mPerfil);
//                            intent.putExtra(UsuarioDetailFragment.EXTRA_EMPRESA_KEY, mEmpresaKey);
//                            Log.d(TAG, "onAuthSuccess:getChildrenCount()<=0- Crear Empresa " );
//                            intent.putExtra(UsuarioDetailFragment.EXTRA_EMPRESA,mEmpresa);
                    startActivity(intent);
                    // Finalizar esta actividad
                    finish();

                } else {
                    if (dataSnapshot.getChildrenCount() == 1) {

                        Log.d(TAG, "buscarEmpres:getChildrenCount()==1  ");
                        for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                            Log.d(TAG, "buscarEmpres-mEmpresaKey" + messageSnapshot.getKey());

                            mEmpresaKey = messageSnapshot.getKey();
                            mEmpresa = (Empresa) messageSnapshot.getValue(Empresa.class);
                            Log.d(TAG, "buscarEmpres-empresaNombre" + mEmpresa.getNombre());


                        }
                        // ya tenemos todos los datos y llamamos a Main activity
                        Intent intent = new Intent(getApplication(), MainActivity.class);
                        intent.putExtra(EXTRA_FIREBASE_URL, mDatabase.getRef().toString());
                        intent.putExtra(EXTRA_USER_KEY, mUserKey);
                        intent.putExtra(EXTRA_USER, mUsuario);
                        intent.putExtra(EXTRA_EMPRESA_KEY, mEmpresaKey);
                        intent.putExtra(EXTRA_EMPRESA, mEmpresa);
                        intent.putExtra(EXTRA_PERFIL, mPerfil);
                        startActivity(intent);

                        // Finalizar esta actividad
                        finish();


                    } else {
                        // es un error tiene que existir solo una empresa asignada al usuario.
                        Toast.makeText(LoginActivity.this, getString(R.string.error_empresa), Toast.LENGTH_SHORT).show();
                        FirebaseAuth.getInstance().signOut();
                        Log.d(TAG, "buscarEmpres:getChildrenCount()>1  ");


                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onAuthSuccess " + databaseError.toString());

            }
        });


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
        showProgress(true);
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
                            showProgress(false);
                            onAuthSuccess(task.getResult().getUser().getUid());
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

//            Bundle arguments = new Bundle();
//            arguments.putString(MainActivity.USER_DISPLAY_NAME, result.getSignInAccount().getDisplayName());
//            arguments.putString(MainActivity.USER_ID, result.getSignInAccount().getId());
            firebaseAuthWithGoogle(account);

//            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//            intent.putExtras(arguments);
//            startActivity(intent);
//            finish();


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
                            onAuthSuccess(task.getResult().getUser().getUid());
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
        return password.length() > 6;
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

