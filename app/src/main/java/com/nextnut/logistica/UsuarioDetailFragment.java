package com.nextnut.logistica;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.rey.material.widget.CheckBox;
import com.rey.material.widget.ProgressView;
import com.rey.material.widget.Switch;

/**
 * A fragment representing a single Usuario detail screen.
 * This fragment is either contained in a {@link UsuarioListActivity}
 * in two-pane mode (on tablets) or a {@link UsuarioDetailActivity}
 * on handsets.
 */
public class UsuarioDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    private EditText mEmail;
    private EditText mEmailConfirmation;
    private Switch mUserSwitch;
    private CheckBox mPerfil_usuarios;
    private CheckBox mPerfil_productos;
    private CheckBox mPerfil_clientes;
    private CheckBox mPerfil_reportes;
    private CheckBox mPerfil_ordenes;
    private CheckBox mPerfil_preparar;
    private CheckBox mPerfil_entregar;
    private CheckBox mPerfil_pagos;
    private CheckBox mPerfil_stock;

    /**
     * The dummy content this fragment is presenting.
     */
    private ProgressView mSpinner;
    private View mFormulario;
    private static final String TAG = "UsuarioDetailFragmet";


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public UsuarioDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
//            mItem = DummyContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(getContext().getResources().getString(R.string.title_usuario_detail));
            }
        }






    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.usuario_detail, container, false);

        // Show the dummy content as text in a TextView.
//        if (mItem != null) {
//            ((TextView) rootView.findViewById(R.id.usuario_detail)).setText("d-"+mItem.details);
//        }

        mSpinner = (ProgressView) rootView.findViewById(R.id.progressUsarios);
        mSpinner.setVisibility(View.GONE);

        mFormulario=(View) rootView.findViewById(R.id.form_usuarioDetail);

        mEmail=(EditText)rootView.findViewById(R.id.email);
        mEmailConfirmation=(EditText)rootView.findViewById(R.id.email_confirmation);
        mUserSwitch=(Switch) rootView.findViewById(R.id.userSwitch);
        mPerfil_usuarios=(CheckBox) rootView.findViewById(R.id.perfil_usuarios);
        mPerfil_productos=(CheckBox) rootView.findViewById(R.id.perfil_productos);
        mPerfil_clientes=(CheckBox) rootView.findViewById(R.id.perfil_clientes);
        mPerfil_reportes=(CheckBox) rootView.findViewById(R.id.perfil_reportes);
        mPerfil_ordenes=(CheckBox) rootView.findViewById(R.id.perfil_ordenes);
        mPerfil_preparar=(CheckBox) rootView.findViewById(R.id.perfil_preparar);
        mPerfil_entregar=(CheckBox) rootView.findViewById(R.id.perfil_entregar);


        return rootView;
    }

    public String getmEmail() {
        return mEmail.getText().toString();
    }
}
