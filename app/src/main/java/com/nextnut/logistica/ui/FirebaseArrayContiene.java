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

package com.nextnut.logistica.ui;

import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.nextnut.logistica.modelos.Cliente;

import java.util.ArrayList;

/**
 * This class implements an array-like collection on top of a Firebase location.
 */
class FirebaseArrayContiene implements ChildEventListener {
    public interface OnChangedListener {
        enum EventType {ADDED, CHANGED, REMOVED, MOVED, FILTRED}

        void onChanged(EventType type, int index, int oldIndex);

        void onCancelled(DatabaseError databaseError);
    }

    private Query mQuery;
    private OnChangedListener mListener;
    private ArrayList<DataSnapshot> mSnapshots;
    private ArrayList<DataSnapshot> mSnapshotsComplto;
    private String mFilterKey;// mFilterKey que no quiero que se muestren porque ya se utilizaron

    public FirebaseArrayContiene(Query ref, final String filterKey) {
        mQuery = ref;
        mSnapshots = new ArrayList<DataSnapshot>();
        mSnapshotsComplto = new ArrayList<DataSnapshot>();
        mQuery.addChildEventListener(this);
        this.mFilterKey = filterKey;
        Log.i("ArrayContiene", "constructor");
        Log.i("ArrayContiene", "constructor filterKey " + filterKey);


    }

    public void cleanup() {
        Log.i("ArrayContiene", "cleanup");

        mQuery.removeEventListener(this);
    }

    public int getCount() {
        Log.i("ArrayContiene", "ngetCount() " + mSnapshots.size());
        return mSnapshots.size();

    }

    public DataSnapshot getItem(int index) {

        Log.i("ArrayContiene", "getItem()" + index);
        return mSnapshots.size()==0? null: mSnapshots.get(index);
    }

    private int getIndexForKey(String key) {
        Log.i("ArrayContiene", "get IndexForkey " + key);

        int index = 0;
//        if (mFilterKey != null) {
//            for (DataSnapshot snapshot : mSnapshots) {
//                Cliente cliente = snapshot.getValue(Cliente.class);
//                if (!cliente.getIndiceNombreApellido().contains(mFilterKey)) {
//                    Log.i("ArrayContiene", "IndexForKey-se filtra" + cliente.getNombre());
//
//                    Log.i("ArrayContiene", "IndexForKey-mSnapshots.size() - 1" + (mSnapshots.size() - 1));
//                    return mSnapshots.size() - 1;
//
//                } else {
//                    Log.i("ArrayContiene", "sIndexForKey-se agrega Cliente" + cliente.getNombre());
//
//                }
//            }
//        }
        for (DataSnapshot snapshot : mSnapshots) {
            if (snapshot.getKey().equals(key)) {
                return index;
            } else {
                index++;
            }
        }
//        return mSnapshots.size()-1;
        throw new IllegalArgumentException("Key not found");
    }

    // Start of ChildEventListener methods


    public void onFilter(String filtro) {
//        mSnapshots.clear();
        if (mFilterKey != null) {
            Log.i("ArrayContiene", "onFilter mFilterKey " + filtro);
            int index_nuevo=0;
            for (DataSnapshot snapshot : mSnapshotsComplto) {

                Cliente cliente = snapshot.getValue(Cliente.class);
                if (cliente != null) {
                    if (cliente.getIndiceNombreApellido().contains(filtro)) {
                        Log.i("ArrayContiene", "onFilter Agergo " + cliente.getIndiceNombreApellido());
                        mSnapshots.remove(snapshot);
                        mSnapshots.add(index_nuevo,snapshot);
                        index_nuevo=index_nuevo+1;

                    } else {
                        Log.i("ArrayContiene", "onFilter saco " + cliente.getIndiceNombreApellido());
                        mSnapshots.remove(snapshot);
                        continue;
                    }
                }

            }
            notifyChangedListeners(OnChangedListener.EventType.FILTRED, 0);
        }
    }

    public void onChildAdded(DataSnapshot snapshot, String previousChildKey) {
        Log.i("ArrayContiene", "onChildAdded-previousChildKey " + previousChildKey);

//        int index = 0;
//
//        if (previousChildKey != null) {
//            index = getIndexForKey(previousChildKey) + 1;
//        }

        mSnapshots.add(snapshot);
        mSnapshotsComplto.add(snapshot);
        notifyChangedListeners(OnChangedListener.EventType.ADDED, mSnapshots.size() - 1);
    }

    public void onChildChanged(DataSnapshot snapshot, String previousChildKey) {
        Log.i("ArrayContiene", "onChildChanged - previousChildKey " + previousChildKey);

        int index = getIndexForKey(snapshot.getKey());
        mSnapshots.set(index, snapshot);
        notifyChangedListeners(OnChangedListener.EventType.CHANGED, index);
    }

    public void onChildRemoved(DataSnapshot snapshot) {
        Log.i("ArrayContiene", "onChildRemoved ");

        int index = getIndexForKey(snapshot.getKey());
        mSnapshots.remove(index);
        notifyChangedListeners(OnChangedListener.EventType.REMOVED, index);
    }

    public void onChildMoved(DataSnapshot snapshot, String previousChildKey) {
        Log.i("ArrayContiene", "onChildMoved - previousChildKey " + previousChildKey);

        int oldIndex = getIndexForKey(snapshot.getKey());
        mSnapshots.remove(oldIndex);
        int newIndex = previousChildKey == null ? 0 : (getIndexForKey(previousChildKey) + 1);
        mSnapshots.add(newIndex, snapshot);
        notifyChangedListeners(OnChangedListener.EventType.MOVED, newIndex, oldIndex);
    }

    public void onCancelled(DatabaseError databaseError) {
        Log.i("ArrayContiene", " - onCancelled");

        notifyCancelledListeners(databaseError);
    }
    // End of ChildEventListener methods

    public void setOnChangedListener(OnChangedListener listener) {
        Log.i("ArrayContiene", "setOnChangedListener");

        mListener = listener;
    }

    protected void notifyChangedListeners(OnChangedListener.EventType type, int index) {
        Log.i("ArrayContiene", "otifyChangedListeners" + type + "-" + index);

        notifyChangedListeners(type, index, -1);
    }

    protected void notifyChangedListeners(OnChangedListener.EventType type, int index, int oldIndex) {
        Log.i("ArrayContiene", "otifyChangedListeners" + type + "-" + index + oldIndex);

        if (mListener != null) {
            mListener.onChanged(type, index, oldIndex);
        }
    }

    protected void notifyCancelledListeners(DatabaseError databaseError) {
        Log.i("ArrayContiene", "notifyCancelledListeners" + databaseError);

        if (mListener != null) {
            mListener.onCancelled(databaseError);
        }
    }
}
