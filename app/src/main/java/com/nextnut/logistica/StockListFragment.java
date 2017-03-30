package com.nextnut.logistica;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.nextnut.logistica.modelos.Almacen;
import com.nextnut.logistica.modelos.Stock;
import com.nextnut.logistica.swipe_helper.SimpleItemTouchHelperCallback;
import com.nextnut.logistica.ui.FirebaseRecyclerAdapter;
import com.nextnut.logistica.viewholder.AlmacenesViewHolder;
import com.nextnut.logistica.viewholder.StockViewHolder;

import static com.nextnut.logistica.util.Constantes.ADAPTER_CABECERA_DELIVEY;
import static com.nextnut.logistica.util.Constantes.ADAPTER_CABECERA_ORDEN_EN_DELIVEY;

public class StockListFragment extends FragmentBasic
{




    View emptyViewProductosEnStock;
//    View emptyViewPagos;
    private FirebaseRecyclerAdapter<Almacen, AlmacenesViewHolder> mAlmacenesAdapter;
    private FirebaseRecyclerAdapter<Stock, StockViewHolder> mStockProductosAdapter;
//    private FirebaseRecyclerAdapter<Pago, PagosViewHolder> mPagosCursorAdapter;

    private RecyclerView recyclerViewAlmacenes;
    private RecyclerView recyclerViewStockProductos;
//    private RecyclerView recyclerViewPagos;

    private CardView mStockTile;
    private TextView mAlmacenStockTitle;


    private LinearLayout mLinearProductosEnStock;



    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    private static final String LOG_TAG = StockListFragment.class.getSimpleName();


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);


    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser) {
        }
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public void savePhoto(Bitmap bitmap) {

    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.stock_list_fragment, container, false);

        Log.i(LOG_TAG, "  monCreateView" );
        mStockTile = (CardView) rootView.findViewById(R.id.stockTitleID);
        mStockTile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mStockTile.setVisibility(View.GONE);
                recyclerViewStockProductos.setVisibility(View.GONE);
                recyclerViewAlmacenes.setVisibility(View.VISIBLE);
//                recyclerViewPagos.setVisibility(View.GONE);
            }
        });
        mStockTile.setVisibility(View.GONE);
        mAlmacenStockTitle = (TextView) mStockTile.findViewById(R.id.almacen_StockTitle);


        mLinearProductosEnStock =(LinearLayout)rootView.findViewById(R.id.linearProductosEnStock);



        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        // Lista de Almacenes
        recyclerViewAlmacenes = (RecyclerView) rootView.findViewById(R.id.almacenes_listRV );
        recyclerViewAlmacenes.setLayoutManager(layoutManager);
        final TextView saldosEmpty = (TextView)rootView.findViewById(R.id.recyclerview_almacenes_empty);



        recyclerViewStockProductos = (RecyclerView) rootView.findViewById(R.id.productoEnStock_list);
        recyclerViewStockProductos.setLayoutManager(new LinearLayoutManager(getContext()));




        // Custom Orders
        emptyViewProductosEnStock = rootView.findViewById(R.id.recyclerview_productosEnStock_empty);



        Query almacenesQuery = refListaAlmacenes();
        mAlmacenesAdapter = new FirebaseRecyclerAdapter<Almacen, AlmacenesViewHolder>(Almacen.class, R.layout.almacenes_list_item,
                AlmacenesViewHolder.class, almacenesQuery) {
            @Override
            protected void populateViewHolder(AlmacenesViewHolder viewHolder, final Almacen model, int position) {
                saldosEmpty.setVisibility(View.GONE);
                viewHolder.bindToPost(model, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        mAlmacenStockTitle.setText(model.getNombre().toString());

                        mClienteKey =model.getAlmacenKey();
                        muestraProductosEnStokDeUnAlmacen();
                        recyclerViewStockProductos.setVisibility(View.VISIBLE);

                        recyclerViewAlmacenes.setVisibility(View.GONE);
                        mLinearProductosEnStock.setVisibility(View.VISIBLE);
                        mStockTile.setVisibility(View.VISIBLE);

                    }

                });
            }

            @Override
            protected void onItemDismissHolder(Almacen model, int position) {

            }

            @Override
            protected void onItemAcceptedHolder(Almacen model, int position) {

            }


        };



        recyclerViewAlmacenes.setAdapter(mAlmacenesAdapter);
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mAlmacenesAdapter, ADAPTER_CABECERA_DELIVEY);
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerViewAlmacenes);


        ImageButton fab_save = (ImageButton) mStockTile.findViewById(R.id.save_picking_Button);
        fab_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                mStockTile.setVisibility(View.GONE);
                mLinearProductosEnStock.setVisibility(View.GONE);
                recyclerViewStockProductos.setVisibility(View.GONE);
                recyclerViewAlmacenes.setVisibility(View.VISIBLE);

            }
        });





        mLinearProductosEnStock.setVisibility(View.GONE);

        //Todo: revisar para el caso de tablets
        if (rootView.findViewById(R.id.customorder_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
        return rootView;
    }



    public void muestraProductosEnStokDeUnAlmacen() {
        Query listadoProductosEnStockDeUnAlmacen =refListaProductosEnStockEnUnAlmacen(mClienteKey);
        Log.i(LOG_TAG, "muestraProductosEnStokDeUnAlmacen:Query: " + listadoProductosEnStockDeUnAlmacen.getRef().toString());

        mStockProductosAdapter = new FirebaseRecyclerAdapter<Stock, StockViewHolder>(Stock.class, R.layout.stock_item,
                StockViewHolder.class, listadoProductosEnStockDeUnAlmacen) {
            @Override
            protected void populateViewHolder(final StockViewHolder viewHolder, final Stock model, final int position) {
                final DatabaseReference ref = getRef(position);
                emptyViewProductosEnStock.setVisibility(View.GONE);
                Log.i(LOG_TAG, "muestraProductosEnStokDeUnAlmacen:ref: " + ref.toString());

                // Set click listener for the whole post view
                final String orderKey = ref.getKey();
                Log.i(LOG_TAG, "muestraProductosEnStokDeUnAlmacen:refKey: " + orderKey);

                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                                           @Override
                                                           public void onClick(View v) {


                                                           }
                                                       }
                );

                viewHolder.bindToPost(model, new View.OnClickListener()

                        {
                            @Override
                            public void onClick(View starView) {
                                Log.d(LOG_TAG, "muestraProductosEnStokDeUnAlmacen:onClick model: " + model.getProducto().getNombreProducto());
                                Log.d(LOG_TAG, "muestraProductosEnStokDeUnAlmacen");
//                                Intent intent = new Intent(getContext(), CustomOrderDetailActivity.class);
//                                mCliente = model.getCliente();
//                                putExtraFirebase_Fragment(intent);
//                                intent.putExtra(EXTRA_CABECERA_ORDEN, model);
////                                Log.d(LOG_TAG, "muestraOrdenesEnPickin CabeceraPicking Nro: "+datosCabeceraPickingSeleccionada.getNumeroDePickingOrden() );
//
//                                //todo: sacaar el 4 y evaluar agrupar las ordenes por cliente.
//                                intent.putExtra(EXTRA_NRO_PICKIG, 4);
//                                intent.putExtra(CustomOrderDetailFragment.CUSTOM_ORDER_ACTION, CustomOrderDetailFragment.CUSTOM_ORDER_NEW);
//                                startActivity(intent);
                            }
                        }

                );
            }

            @Override
            protected void onItemDismissHolder(Stock model, int position) {
                Log.i(LOG_TAG, "muestraOrdenesEnPicking Modelo: Numero de orden- " + model.getProducto().getNombreProducto());

            }

            @Override
            protected void onItemAcceptedHolder(Stock model, int position) {
                Log.i(LOG_TAG, "muestraOrdenesEndelivery Modelo: Numero de orden- " + model.getProducto().getNombreProducto());

            }


        };




        ItemTouchHelper.Callback callback1 = new SimpleItemTouchHelperCallback(mStockProductosAdapter, ADAPTER_CABECERA_ORDEN_EN_DELIVEY);
        ItemTouchHelper mItemTouchHelperCustomOrder = new ItemTouchHelper(callback1);
        mItemTouchHelperCustomOrder.attachToRecyclerView(recyclerViewStockProductos);


        recyclerViewStockProductos.setAdapter(mStockProductosAdapter);
    }








    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {


        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
//            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



}
