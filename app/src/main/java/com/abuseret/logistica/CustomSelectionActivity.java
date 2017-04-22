package com.abuseret.logistica;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.abuseret.logistica.modelos.Cliente;
import com.abuseret.logistica.ui.FirebaseRecyclerAdapterContiene;
import com.abuseret.logistica.viewholder.ClienteViewHolder;

import static com.abuseret.logistica.util.Constantes.ESQUEMA_EMPRESA_CLIENTES;
import static com.abuseret.logistica.util.Constantes.EXTRA_CLIENTE;
import static com.abuseret.logistica.util.Constantes.EXTRA_CLIENTE_KEY;

public class CustomSelectionActivity extends ActivityBasic  {
    private FirebaseRecyclerAdapterContiene<Cliente, ClienteViewHolder> mAdapter;
    private EditText mCustomFilter;
    private TextView emptyView;


//    Adapter mAdapter;
    public static String RESULTADO = "resultado";
    private static final String LOG_TAG = CustomSelectionActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_selection);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.content_custom_selection);
        EditText mCustomFilter = (EditText)findViewById(R.id.custom_name_filter);
         emptyView = (TextView) findViewById(R.id.empty_filter);

        emptyView.setVisibility(View.VISIBLE);


        StaggeredGridLayoutManager sglm =
                new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(sglm);


        Query clientesQuery = getQuery(mDatabase);
        mAdapter = new FirebaseRecyclerAdapterContiene<Cliente, ClienteViewHolder>(Cliente.class, R.layout.custom_list_item,
                ClienteViewHolder.class, clientesQuery,mCustomFilter,emptyView) {
            @Override
            protected void populateViewHolder(final ClienteViewHolder viewHolder, final Cliente model, final int position) {
                final DatabaseReference postRef = getRef(position);
//                if(model.equals(null)){ emptyView.setVisibility(View.VISIBLE);}else{ emptyView.setVisibility(View.GONE);};
                Log.i("ClienteViewHolder", "populateViewHolder(postRef)" + postRef.toString());

                // Set click listener for the whole post view
                final String customKey = postRef.getKey();

                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                                           @Override
                                                           public void onClick(View v) {

//                                                               if (mTwoPane) {
//                                                                   Bundle arguments = new Bundle();
//                                                                   // when rotate the screen the selecction of the second Screen is conserved.
////                    arguments.putLong(ProductDetailFragment.ARG_ITEM_ID, id);
////
//                                                                   arguments.putInt(ProductDetailFragment.PRODUCT_ACTION, ProductDetailFragment.PRODUCT_SELECTION);
//
//                                                                   ProductDetailFragment fragment = new ProductDetailFragment();
//                                                                   fragment.setArguments(arguments);
//                                                                  arguments.putString(EXTRA_PRODUCT_KEY, customKey);
//                                                                    getSupportFragmentManager().beginTransaction()
//                                                                           .addToBackStack(null)
//                                                                           .replace(R.id.product_detail_container, fragment)
//                                                                           .commit();
////
////                                                                   fab_new.setVisibility(View.GONE);
////                                                                   fab_save.setVisibility(View.VISIBLE);
//
//                                                               } else {




                                                                   Intent intent = new Intent();
                                                                   intent.putExtra(EXTRA_CLIENTE_KEY, customKey);
                                                                   intent.putExtra(EXTRA_CLIENTE, model);
                                                                   setResult(RESULT_OK, intent);
                                                                   finish();



//
//                                                                   // Launch PostDetailActivity
//                                                                   Intent intent = new Intent(getApplication(), ProductDetailActivity.class);
//                                                                   intent.putExtra(EXTRA_PRODUCT_KEY, productKey);
//                                                                   startActivity(intent);
//
//                                                               }

                                                           }

                                                       }

                );

                viewHolder.bindToPost(model, new View.OnClickListener()

                        {
                            @Override
                            public void onClick(View starView) {

                            }
                        }

                );
            }


        }

        ;

        recyclerView.setAdapter(mAdapter);

    }




    public Query getQuery(DatabaseReference databaseReference) {

        return databaseReference.child(ESQUEMA_EMPRESA_CLIENTES).child(mEmpresaKey).orderByChild("IndiceNombreApellido");
    }

}
