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

import java.util.ArrayList;

/**
 * This class implements an array-like collection on top of a Firebase location.
 */
class FirebaseArrayDistinct implements ChildEventListener {
    public interface OnChangedListener {
        enum EventType {ADDED, CHANGED, REMOVED, MOVED}
        void onChanged(EventType type, int index, int oldIndex);
        void onCancelled(DatabaseError databaseError);
    }

    private Query mQuery;
    private OnChangedListener mListener;
    private ArrayList<DataSnapshot> mSnapshots;
    private ArrayList<String> keys;// keys que no quiero que se muestren porque ya se utilizaron

    public FirebaseArrayDistinct(Query ref,final ArrayList<String> keys) {
        mQuery = ref;
        mSnapshots = new ArrayList<DataSnapshot>();
        mQuery.addChildEventListener(this);
        this.keys=keys;
        Log.i("ArrayDistinct", "constructor");

    }

    public void cleanup() {
        Log.i("ArrayDistinct", "cleanup");

        mQuery.removeEventListener(this);
    }

    public int getCount() {
        Log.i("ArrayDistinct", "ngetCount()");
        return mSnapshots.size();

    }
    public DataSnapshot getItem(int index) {

        Log.i("ArrayDistinct", "getItem()");
        return mSnapshots.get(index);
    }

    private int getIndexForKey(String key) {
        Log.i("ArrayDistinct", "get IndexForkey "+key);

        int index = 0;
        if(keys!=null) {
            for (String keyloop : keys) {
                if (key.equals(keyloop)) {
                    Log.i("ArrayDistinct", "mSnapshots.size() - 1" + (mSnapshots.size() - 1));
                    return mSnapshots.size() - 1;
                }
            }
        }
        for (DataSnapshot snapshot : mSnapshots) {
            if (snapshot.getKey().equals(key)) {
                return index;
            } else {
                index++;
            }
        }
        throw new IllegalArgumentException("Key not found");
    }

    // Start of ChildEventListener methods
    public void onChildAdded(DataSnapshot snapshot, String previousChildKey) {
        Log.i("ArrayDistinct", "onChildAdded-previousChildKey "+previousChildKey);

        int index = 0;
        if(keys!=null) {
            for (String key : keys) {
                if (snapshot.getKey().equals(key)) {
                    Log.i("ArrayDistinct", "not added" + key);
                    return;
                }
            }
        }
        if (previousChildKey != null) {
            index = getIndexForKey(previousChildKey) + 1;
        }

        mSnapshots.add(index, snapshot);
        notifyChangedListeners(OnChangedListener.EventType.ADDED, index);
    }

    public void onChildChanged(DataSnapshot snapshot, String previousChildKey) {
        Log.i("ArrayDistinct", "onChildChanged - previousChildKey "+previousChildKey);

        int index = getIndexForKey(snapshot.getKey());
        mSnapshots.set(index, snapshot);
        notifyChangedListeners(OnChangedListener.EventType.CHANGED, index);
    }

    public void onChildRemoved(DataSnapshot snapshot) {
        Log.i("ArrayDistinct", "onChildRemoved ");

        int index = getIndexForKey(snapshot.getKey());
        mSnapshots.remove(index);
        notifyChangedListeners(OnChangedListener.EventType.REMOVED, index);
    }

    public void onChildMoved(DataSnapshot snapshot, String previousChildKey) {
        Log.i("ArrayDistinct", "onChildMoved - previousChildKey "+previousChildKey);

        int oldIndex = getIndexForKey(snapshot.getKey());
        mSnapshots.remove(oldIndex);
        int newIndex = previousChildKey == null ? 0 : (getIndexForKey(previousChildKey) + 1);
        mSnapshots.add(newIndex, snapshot);
        notifyChangedListeners(OnChangedListener.EventType.MOVED, newIndex, oldIndex);
    }

    public void onCancelled(DatabaseError databaseError) {
        Log.i("ArrayDistinct", " - onCancelled");

        notifyCancelledListeners(databaseError);
    }
    // End of ChildEventListener methods

    public void setOnChangedListener(OnChangedListener listener) {
        Log.i("ArrayDistinct", "setOnChangedListener");

        mListener = listener;
    }
    
    protected void notifyChangedListeners(OnChangedListener.EventType type, int index) {
        Log.i("ArrayDistinct", "otifyChangedListeners"+ type+"-"+index);

        notifyChangedListeners(type, index, -1);
    }
    
    protected void notifyChangedListeners(OnChangedListener.EventType type, int index, int oldIndex) {
        Log.i("ArrayDistinct", "otifyChangedListeners"+ type+"-"+index+oldIndex);

        if (mListener != null) {
            mListener.onChanged(type, index, oldIndex);
        }
    }
    
    protected void notifyCancelledListeners(DatabaseError databaseError) {
        Log.i("ArrayDistinct", "notifyCancelledListeners"+ databaseError);

        if (mListener != null) {
            mListener.onCancelled(databaseError);
        }
    }
}
