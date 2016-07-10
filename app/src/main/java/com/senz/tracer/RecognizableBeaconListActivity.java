package com.senz.tracer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.alibaba.fastjson.serializer.ObjectArraySerializer;
import com.avos.avoscloud.AVCloud;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.FunctionCallback;
import com.senz.tracer.Tracer.BeaconTracer;
import com.senz.tracer.Tracer.LocationTracer;
import com.senz.tracer.Tracer.TracerCallback;
import com.senz.tracer.dummy.DummyContent;
import com.senz.tracer.ticket.ChecklistActivity;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An activity representing a list of RecognizableBeacons. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link RecognizableBeaconDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class RecognizableBeaconListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    private String roleName = "waiter";


    private Date lastBeaconCheckInAt = new Date();
    private HashMap<String, Object> lastBeaconCheck = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recognizablebeacon_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        AVOSCloud.initialize(this, "p6974wyAF311qTN0tvxrw8oT-gzGzoHsz", "VEi5qSwbn4Hh4Q8pauISVFyD");
        AVOSCloud.setDebugLogEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ("waiter".equals(roleName)) {
                    roleName = "school";
                } else {
                    roleName = "waiter";
                }
                Snackbar.make(view, "Change role to " + roleName, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
//                Log.i("Tracer", "" + adapter.getItemCount());

//                Intent intent = new Intent(RecognizableBeaconListActivity.this, ChecklistActivity.class);
//                startActivity(intent);
            }
        });

        View recyclerView = findViewById(R.id.recognizablebeacon_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

        if (findViewById(R.id.recognizablebeacon_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        DummyContent.ITEMS.clear();
        if (true) {
            beaconTracer = new BeaconTracer(this);
            beaconTracer.startTracing(new TracerCallback() {
                @Override
                public void onUpdate(List<Recognizable> recognizables) {
                    Date now = new Date();
                    if ((now.getTime() - lastBeaconCheckInAt.getTime()) > 3000) {
                        Log.i("Tracer", "beacon list:" + recognizables.size());

                        HashMap<String, Object> params = new HashMap<String, Object>();
                        params.put("installationId", AVInstallation.getCurrentInstallation().getInstallationId());
                        params.put("type", "ibeacon");
                        HashMap<String, Object> content = new HashMap<String, Object>();
                        params.put("content", content);
                        content.put("timestamp", new Date().getTime());
                        HashMap<String, Object> beacons = new HashMap<String, Object>();
                        content.put("beacons", beacons);
                        adapter.notifyDataSetChanged();
                        for (int i = 0; i < recognizables.size(); i++) {
                            HashMap<String, Object> beaconAttr = new HashMap<String, Object>();
                            Recognizable recognizable = recognizables.get(i);
                            beaconAttr.put("vendor", "estimote");
                            beaconAttr.put("distance", recognizable.attributes.get("distance"));
                            beacons.put((String) recognizable.attributes.get("id"), beaconAttr);
                        }

                        AVCloud.callFunctionInBackground("checkin", params, new FunctionCallback<Object>() {
                            @Override
                            public void done(Object o, AVException e) {

                            }
                        });
                        lastBeaconCheckInAt = now;
                    }


                    adapter.notifyDataSetChanged();
                }
            });
        }

        if (false) {
            locationTracer = new LocationTracer(this);
            locationTracer.startTracing(new TracerCallback() {
                @Override
                public void onUpdate(List<Recognizable> recognizable) {
                    Log.i("Tracer", "location:" + recognizable.get(0));
                    HashMap<String, Object> params = new HashMap<String, Object>();
                    params.put("installationId", AVInstallation.getCurrentInstallation().getInstallationId());
                    params.put("type", "location");
                    HashMap<String, Object> content = new HashMap<String, Object>();
                    params.put("content", content);
                    content.put("timestamp", new Date().getTime());
                    content.put("lat", recognizable.get(0).attributes.get("lat"));
                    content.put("lng", recognizable.get(0).attributes.get("long"));
                    content.put("source", "amap");
                    content.put("accuracy", -1);
                    AVCloud.callFunctionInBackground("checkin", params, new FunctionCallback<Object>() {
                        @Override
                        public void done(Object o, AVException e) {

                        }
                    });

                }
            });
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
//        beaconTracer.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconTracer.destroyTracing();
    }

    BeaconTracer beaconTracer;
    LocationTracer locationTracer;

    SimpleItemRecyclerViewAdapter adapter;

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        Log.i("Tracer", "setupRecyclerView");
        if (recyclerView.getAdapter() == null) {
            adapter = new SimpleItemRecyclerViewAdapter(DummyContent.ITEMS);
            recyclerView.setAdapter(adapter);
        }
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<DummyContent.DummyItem> mValues;

        public SimpleItemRecyclerViewAdapter(List<DummyContent.DummyItem> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recognizablebeacon_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            holder.mIdView.setText(mValues.get(position).id);
            holder.mContentView.setText(mValues.get(position).content);

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putString(RecognizableBeaconDetailFragment.ARG_ITEM_ID, holder.mItem.id);
                        RecognizableBeaconDetailFragment fragment = new RecognizableBeaconDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.recognizablebeacon_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, RecognizableBeaconDetailActivity.class);
                        intent.putExtra(RecognizableBeaconDetailFragment.ARG_ITEM_ID, holder.mItem.id);

                        context.startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mIdView;
            public final TextView mContentView;
            public DummyContent.DummyItem mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mIdView = (TextView) view.findViewById(R.id.id);
                mContentView = (TextView) view.findViewById(R.id.content);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }
    }
}
