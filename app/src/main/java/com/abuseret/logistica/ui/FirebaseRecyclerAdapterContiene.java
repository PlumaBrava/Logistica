/*
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.abuseret.logistica.ui;

import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * This class is a generic way of backing an RecyclerView with a Firebase location.
 * It handles all of the child events at the given Firebase location. It marshals received data into the given
 * class type.
 * <p>
 * To use this class in your app, subclass it passing in all required parameters and implement the
 * populateViewHolder method.
 * <p>
 * <blockquote><pre>
 * {@code
 *     private static class ChatMessageViewHolder extends RecyclerView.ViewHolder {
 *         TextView messageText;
 *         TextView nameText;
 * <p>
 *         public ChatMessageViewHolder(View itemView) {
 *             super(itemView);
 *             nameText = (TextView)itemView.findViewById(android.R.id.text1);
 *             messageText = (TextView) itemView.findViewById(android.R.id.text2);
 *         }
 *     }
 * <p>
 *     FirebaseRecyclerAdapter<ChatMessage, ChatMessageViewHolder> adapter;
 *     DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
 * <p>
 *     RecyclerView recycler = (RecyclerView) findViewById(R.id.messages_recycler);
 *     recycler.setHasFixedSize(true);
 *     recycler.setLayoutManager(new LinearLayoutManager(this));
 * <p>
 *     adapter = new FirebaseRecyclerAdapter<ChatMessage, ChatMessageViewHolder>(ChatMessage.class, android.R.layout.two_line_list_item, ChatMessageViewHolder.class, ref) {
 *         public void populateViewHolder(ChatMessageViewHolder chatMessageViewHolder, ChatMessage chatMessage, int position) {
 *             chatMessageViewHolder.nameText.setText(chatMessage.getName());
 *             chatMessageViewHolder.messageText.setText(chatMessage.getMessage());
 *         }
 *     };
 *     recycler.setAdapter(mAdapter);
 * }
 * </pre></blockquote>
 *
 * @param <T>  The Java class that maps to the type of objects stored in the Firebase location.
 * @param <VH> The ViewHolder class that contains the Views in the layout that is shown for each object.
 */
public abstract class FirebaseRecyclerAdapterContiene<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    Class<T> mModelClass;
    protected int mModelLayout;
    Class<VH> mViewHolderClass;
    FirebaseArrayContiene mSnapshots;
    TextView mEmptyView;
    String mFilterKey;

    /**
     * @param modelClass      Firebase will marshall the data at a location into an instance of a class that you provide
     * @param modelLayout     This is the layout used to represent a single item in the list. You will be responsible for populating an
     *                        instance of the corresponding view with the data from an instance of modelClass.
     * @param viewHolderClass The class that hold references to all sub-views in an instance modelLayout.
     * @param ref             The Firebase location to watch for data changes. Can also be a slice of a location, using some
     *                        combination of <code>limit()</code>, <code>startAt()</code>, and <code>endAt()</code>
     */
    public FirebaseRecyclerAdapterContiene(Class<T> modelClass, int modelLayout, Class<VH> viewHolderClass, final Query ref, EditText filterkeyView, TextView empty) {
        mModelClass = modelClass;
        mModelLayout = modelLayout;
        mViewHolderClass = viewHolderClass;
        mFilterKey = filterkeyView.getText().toString().toLowerCase();
        mEmptyView = empty;
        mSnapshots = new FirebaseArrayContiene(ref, mFilterKey);
        Log.i("AdapterContiene", "FirebaseRecyclerAdapter-constructor-Query ref");
        mSnapshots.setOnChangedListener(new FirebaseArrayContiene.OnChangedListener() {
            @Override
            public void onChanged(EventType type, int index, int oldIndex) {
                Log.i("AdapterContiene", "FirebaseRecyclerAdapter-onChanged-type" + type.toString() + "-index: " + index + "- oldindex: " + oldIndex);
//                Log.i("AdapterContiene", "getKey()"+mSnapshots.getItem(index).getKey());

                switch (type) {
                    case ADDED:
                        notifyItemInserted(index);
                        break;
                    case CHANGED:
                        notifyItemChanged(index);
                        break;
                    case REMOVED:
                        notifyItemRemoved(index);
                        break;
                    case MOVED:
                        notifyItemMoved(oldIndex, index);
                        break;
                    case FILTRED:
                        notifyDataSetChanged();
                        break;

                    default:
                        throw new IllegalStateException("Incomplete case statement");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i("AdapterContiene", "FirebaseRecyclerAdapter-onCancelled" + databaseError);

                FirebaseRecyclerAdapterContiene.this.onCancelled(databaseError);
            }
        });
        filterkeyView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                Log.i("AdapterContiene", "filterkeyView editable " + editable.toString());
                Log.i("AdapterContiene", "mFilterKey " + mFilterKey);
                mSnapshots.onFilter(editable.toString().toLowerCase());
//                mSnapshots = new FirebaseArrayContiene(ref,editable.toString().toLowerCase());
            }
        });
    }

    /**
     * @param modelClass      Firebase will marshall the data at a location into an instance of a class that you provide
     * @param modelLayout     This is the layout used to represent a single item in the list. You will be responsible for populating an
     *                        instance of the corresponding view with the data from an instance of modelClass.
     * @param viewHolderClass The class that hold references to all sub-views in an instance modelLayout.
     * @param ref             The Firebase location to watch for data changes. Can also be a slice of a location, using some
     *                        combination of <code>limit()</code>, <code>startAt()</code>, and <code>endAt()</code>
     */
    public FirebaseRecyclerAdapterContiene(Class<T> modelClass, int modelLayout, Class<VH> viewHolderClass, DatabaseReference ref, EditText filterKeyView, TextView emptyview) {

        this(modelClass, modelLayout, viewHolderClass, (Query) ref, (EditText) filterKeyView, (TextView) emptyview);
        Log.i("AdapterContiene", "FirebaseRecyclerAdapter- solo");
    }

    public void cleanup() {
        Log.i("AdapterContiene", "cleanup");
        mSnapshots.cleanup();
    }

    @Override
    public int getItemCount() {
        Log.i("AdapterContiene", "mSnapshots.getCount(): " + mSnapshots.getCount());
        if (mSnapshots.getCount() == 0) {
            mEmptyView.setVisibility(View.VISIBLE);
        }else{
            mEmptyView.setVisibility(View.GONE);
        }

        return mSnapshots.getCount();
    }

    public T getItem(int position) {
        Log.i("AdapterContiene", "position: " + position + " getItem: " + mSnapshots.getItem(position));
        return parseSnapshot(mSnapshots.getItem(position));
    }

    /**
     * This method parses the DataSnapshot into the requested type. You can override it in subclasses
     * to do custom parsing.
     *
     * @param snapshot the DataSnapshot to extract the model from
     * @return the model extracted from the DataSnapshot
     */
    protected T parseSnapshot(DataSnapshot snapshot) {
        return snapshot.getValue(mModelClass);
    }

    public DatabaseReference getRef(int position) {
        Log.i("AdapterContiene", "position: " + position + " mSnapshots.getItem(position).getRef(): " + mSnapshots.getItem(position).getRef());
        return mSnapshots.getItem(position).getRef();
    }

    @Override
    public long getItemId(int position) {
        // http://stackoverflow.com/questions/5100071/whats-the-purpose-of-item-ids-in-android-listview-adapter
        return mSnapshots.getItem(position).getKey().hashCode();
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.i("AdapterContiene", "onCreateViewHolder  xx: " + parent.toString());
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        try {
            Constructor<VH> constructor = mViewHolderClass.getConstructor(View.class);
            return constructor.newInstance(view);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onBindViewHolder(VH viewHolder, int position) {
        Log.i("AdapterContiene", "onBindViewHolder");
        T model = getItem(position);
        populateViewHolder(viewHolder, model, position);
    }

    @Override
    public int getItemViewType(int position) {
        return mModelLayout;
    }

    /**
     * This method will be triggered in the event that this listener either failed at the server,
     * or is removed as a result of the security and Firebase Database rules.
     *
     * @param databaseError A description of the error that occurred
     */
    protected void onCancelled(DatabaseError databaseError) {
        Log.w("AdapterContiene", databaseError.toException());
    }

    /**
     * Each time the data at the given Firebase location changes, this method will be called for each item that needs
     * to be displayed. The first two arguments correspond to the mLayout and mModelClass given to the constructor of
     * this class. The third argument is the item's position in the list.
     * <p>
     * Your implementation should populate the view using the data contained in the model.
     *
     * @param viewHolder The view to populate
     * @param model      The object containing the data used to populate the view
     * @param position   The position in the list of the view being populated
     */
    abstract protected void populateViewHolder(VH viewHolder, T model, int position);
}
