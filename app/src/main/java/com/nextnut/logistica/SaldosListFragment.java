package com.nextnut.logistica;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.nextnut.logistica.modelos.CabeceraOrden;
import com.nextnut.logistica.swipe_helper.SimpleItemTouchHelperCallback;
import com.nextnut.logistica.ui.FirebaseRecyclerAdapter;
import com.nextnut.logistica.viewholder.CabeceraViewHolder;
import com.nextnut.logistica.viewholder.SaldosViewHolder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.nextnut.logistica.util.Constantes.ADAPTER_CABECERA_DELIVEY;
import static com.nextnut.logistica.util.Constantes.ADAPTER_CABECERA_ORDEN_EN_DELIVEY;
import static com.nextnut.logistica.util.Constantes.EXTRA_CABECERA_ORDEN;
import static com.nextnut.logistica.util.Constantes.EXTRA_NRO_PICKIG;
import static com.nextnut.logistica.util.Constantes.ORDER_STATUS_DELIVERED_PARA_COMPENSAR;

public class SaldosListFragment extends FragmentBasic
{


    private long mCustomOrderIdSelected;

    private long mIDPickingOrderSelected;
    View emptyViewTotalProducts;
    private Query totalProductos;

    View emptyViewCustomOrder;
    private FirebaseRecyclerAdapter<CabeceraOrden, SaldosViewHolder> mSaldosAdapter;
    private FirebaseRecyclerAdapter<CabeceraOrden, CabeceraViewHolder> mCustomsOrdersCursorAdapter;

    private RecyclerView recyclerViewSaldos;
    private RecyclerView recyclerViewCustomOrderEnSaldos;

    private CardView mSaldosTile;
    private TextView mTilePickingOrderNumber;
    private EditText mTilePickingComent;
    private TextView mCreationDate;

    private   ArrayList<Task> taskList = new ArrayList<Task>();
    private   Task<Void> allTask;

    private DataSnapshot mProductosEnOrdenDatos;
    private DataSnapshot mOrdenesEnPickingDatos;

//private CabeceraPicking datosCabeceraPickingSeleccionada;

    private FloatingActionButton fab_new;
    private FloatingActionButton fab_delete;



    private LinearLayout mLinearOrders;


    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    private static final String LOG_TAG = SaldosListFragment.class.getSimpleName();


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
        View rootView = inflater.inflate(R.layout.saldos_list_fragment, container, false);

        Log.i(LOG_TAG, "  monCreateView" );
        mSaldosTile = (CardView) rootView.findViewById(R.id.saldosTitleID);
        mSaldosTile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mIDPickingOrderSelected = 0;
                mSaldosTile.setVisibility(View.GONE);
                recyclerViewCustomOrderEnSaldos.setVisibility(View.GONE);
                recyclerViewSaldos.setVisibility(View.VISIBLE);
            }
        });
        mSaldosTile.setVisibility(View.GONE);
        mTilePickingOrderNumber = (TextView) mSaldosTile.findViewById(R.id.titlepickingNumberOrderCard);
        mTilePickingComent = (EditText) mSaldosTile.findViewById(R.id.TitlepickingOrderComents);
        mCreationDate = (TextView) mSaldosTile.findViewById(R.id.titlePicckinOder_creationdate);


        mLinearOrders =(LinearLayout)rootView.findViewById(R.id.linearOrders);



        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        // Lista de saldos
        recyclerViewSaldos = (RecyclerView) rootView.findViewById(R.id.saldos_listRV );
        recyclerViewSaldos.setLayoutManager(layoutManager);

        final View saldosEmpty = rootView.findViewById(R.id.recyclerview_saldos_empty);
        Query saldosQuery = refSaldoTotalClientes_10List();
//         = mDatabase.child(ESQUEMA_PICKING).child(mEmpresaKey).child(String.valueOf(PICKING_STATUS_INICIAL));
        mSaldosAdapter = new FirebaseRecyclerAdapter<CabeceraOrden, SaldosViewHolder>(CabeceraOrden.class, R.layout.saldos_list_content,
                SaldosViewHolder.class, saldosQuery) {
            @Override
            protected void populateViewHolder(SaldosViewHolder viewHolder, final CabeceraOrden model, int position) {
                viewHolder.bindToPost(model, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        saldosEmpty.setVisibility(View.GONE);
                        mTilePickingOrderNumber.setText(String.valueOf( model.getNumeroDePickingOrden()));
                        mTilePickingComent.setVisibility(View.VISIBLE);
                        SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

                        mCreationDate.setText(sfd.format(new Date(model.getFechaDeCreacion())));






                        mSaldosAdapter.notifyDataSetChanged();


                        muestraOrdenesSinCompensar();

//                        mCursorAdapterTotalProductos.notifyDataSetChanged();
                        recyclerViewCustomOrderEnSaldos.setVisibility(View.VISIBLE);




                        recyclerViewSaldos.setVisibility(View.GONE);
                        mLinearOrders.setVisibility(View.VISIBLE);
                        mSaldosTile.setVisibility(View.VISIBLE);

                    }

                });
            }

            @Override
            protected void onItemDismissHolder(CabeceraOrden model, int position) {

            }

            @Override
            protected void onItemAcceptedHolder(CabeceraOrden model, int position) {

            }


        };



        recyclerViewSaldos.setAdapter(mSaldosAdapter);
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mSaldosAdapter, ADAPTER_CABECERA_DELIVEY);
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerViewSaldos);


        ImageButton fab_save = (ImageButton) mSaldosTile.findViewById(R.id.save_picking_Button);
        fab_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mIDPickingOrderSelected = 0;
                mSaldosTile.setVisibility(View.GONE);
                mLinearOrders.setVisibility(View.GONE);
                recyclerViewCustomOrderEnSaldos.setVisibility(View.GONE);
                recyclerViewSaldos.setVisibility(View.VISIBLE);

            }
        });



        recyclerViewCustomOrderEnSaldos = (RecyclerView) rootView.findViewById(R.id.customOrderEnSaldos_list);
        recyclerViewCustomOrderEnSaldos.setLayoutManager(new LinearLayoutManager(getContext()));



        // Custom Orders
       emptyViewCustomOrder = rootView.findViewById(R.id.recyclerview_orden_empty);



        mLinearOrders.setVisibility(View.GONE);

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



    public void muestraOrdenesSinCompensar() {
        Query listadoOrdenesEnPickingQuery = refCabeceraOrden_2_List(ORDER_STATUS_DELIVERED_PARA_COMPENSAR, 4);
        Log.i(LOG_TAG, "muestraOrdenesSinCompensar:Query: " + listadoOrdenesEnPickingQuery.getRef().toString());

        mCustomsOrdersCursorAdapter = new FirebaseRecyclerAdapter<CabeceraOrden, CabeceraViewHolder>(CabeceraOrden.class, R.layout.customorder_list_content,
                CabeceraViewHolder.class, listadoOrdenesEnPickingQuery) {
            @Override
            protected void populateViewHolder(final CabeceraViewHolder viewHolder, final CabeceraOrden model, final int position) {
                final DatabaseReference CabeceraRef = getRef(position);
                emptyViewCustomOrder.setVisibility(View.GONE);
                Log.i(LOG_TAG, "muestraOrdenesSinCompensar:CabeceraRef: " + CabeceraRef.toString());

                // Set click listener for the whole post view
                final String orderKey = CabeceraRef.getKey();
                Log.i(LOG_TAG, "muestraOrdenesSinCompensar:orderKey: " + orderKey);

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
                                Log.d(LOG_TAG, "mmuestraOrdenesSinCompensar:onClick model: " + model.getCliente().getNombre());
                                Log.d(LOG_TAG, "muestraOrdenesSinCompensaronClick");
                                Intent intent = new Intent(getContext(), CustomOrderDetailActivity.class);
                                mCliente = model.getCliente();
                                putExtraFirebase_Fragment(intent);
                                intent.putExtra(EXTRA_CABECERA_ORDEN, model);
//                                Log.d(LOG_TAG, "muestraOrdenesEnPickin CabeceraPicking Nro: "+datosCabeceraPickingSeleccionada.getNumeroDePickingOrden() );

                                //todo: sacaar el 4 y evaluar agrupar las ordenes por cliente.
                                intent.putExtra(EXTRA_NRO_PICKIG, 4);
                                intent.putExtra(CustomOrderDetailFragment.CUSTOM_ORDER_ACTION, CustomOrderDetailFragment.CUSTOM_ORDER_NEW);
                                startActivity(intent);
                            }
                        }

                );
            }

            @Override
            protected void onItemDismissHolder(CabeceraOrden model, int position) {
                Log.i(LOG_TAG, "muestraOrdenesEnPicking Modelo: Numero de orden- " + model.getNumeroDeOrden());

            }

            @Override
            protected void onItemAcceptedHolder(CabeceraOrden model, int position) {




                    Log.i(LOG_TAG, "muestraOrdenesEndelivery Modelo: Numero de orden- " + model.getNumeroDeOrden());



                }
        };




        ItemTouchHelper.Callback callback1 = new SimpleItemTouchHelperCallback(mCustomsOrdersCursorAdapter, ADAPTER_CABECERA_ORDEN_EN_DELIVEY);
        ItemTouchHelper mItemTouchHelperCustomOrder = new ItemTouchHelper(callback1);
        mItemTouchHelperCustomOrder.attachToRecyclerView(recyclerViewCustomOrderEnSaldos);


        recyclerViewCustomOrderEnSaldos.setAdapter(mCustomsOrdersCursorAdapter);
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
