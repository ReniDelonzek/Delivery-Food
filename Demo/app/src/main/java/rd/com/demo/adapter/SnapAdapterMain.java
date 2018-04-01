package rd.com.demo.adapter;


import android.os.Handler;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.rubensousa.gravitysnaphelper.GravitySnapHelper;
import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;

import java.util.ArrayList;

import rd.com.demo.R;
import rd.com.demo.item.Snap_Item;


public class SnapAdapterMain extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements
        GravitySnapHelper.SnapListener {

    private ArrayList<Snap_Item> mSnaps;
    private int POS = 0, TOTAL = 0;
    // Disable touch detection for parent recyclerView if we use vertical nested recyclerViews
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            v.getParent().requestDisallowInterceptTouchEvent(true);
            return false;
        }
    };

    public SnapAdapterMain() {
        mSnaps = new ArrayList<>();
    }

    public void addSnap(Snap_Item snap) {
        mSnaps.add(snap);
    }

    @Override
    public int getItemViewType(int position) {
        Snap_Item snap = mSnaps.get(position);
       return snap.getType();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())//falso
                .inflate(R.layout.container_snap, parent, false);;
        if (viewType == 0) {
            view = LayoutInflater.from(parent.getContext())//falso
                    .inflate(R.layout.conteiner_top, parent, false);
            view.findViewById(R.id.recyclerView).setOnTouchListener(mTouchListener);
        } else if (viewType == 1 || viewType == 3 || viewType == 4){
            view = LayoutInflater.from(parent.getContext())//falso
                    .inflate(R.layout.container_snap, parent, false);
        } else if (viewType == 2 ){
            view = LayoutInflater.from(parent.getContext())//falso
                    .inflate(R.layout.conteiner_top, parent, false);
        }
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        Snap_Item snap = mSnaps.get(position);
        ViewHolder holder = (ViewHolder) viewHolder;
        if (snap.title && snap.getType() != 0) {
            holder.snapTextView.setText(snap.getText());
        }
        if (snap.getType() == 0){
            TOTAL++;
            LinearLayoutManager layoutManager = new LinearLayoutManager(holder.recyclerView.getContext(),
                    LinearLayoutManager.HORIZONTAL, false);
            holder.recyclerView.setLayoutManager(layoutManager);
            holder.progressBar.setVisibility(View.GONE);
            tempo(3000, layoutManager);

        } else if (snap.getType() == 1) {
            holder.progressBar.setVisibility(View.GONE);
            FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(holder.recyclerView.getContext());
            layoutManager.setFlexWrap(FlexWrap.WRAP);
            layoutManager.setFlexDirection(FlexDirection.ROW);
            layoutManager.setAlignItems(AlignItems.FLEX_START);
            layoutManager.setJustifyContent(JustifyContent.SPACE_AROUND);
            holder.recyclerView.setLayoutManager(layoutManager);
        } else if (snap.getType() == 2 || snap.getType() == 3) {
            GridLayoutManager layoutManager = new GridLayoutManager(holder.recyclerView.getContext(), 2);
            holder.recyclerView.setLayoutManager(layoutManager);
        } else if (snap.getType() == 4){
            holder.progressBar.setVisibility(View.GONE);
            LinearLayoutManager layoutManager = new LinearLayoutManager(holder.recyclerView.getContext(),
                    LinearLayoutManager.HORIZONTAL, false);
            holder.recyclerView.setLayoutManager(layoutManager);
        }

        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(holder.recyclerView);
        holder.recyclerView.setAdapter(new AdapterMain(snap.getApps(), snap.getType(), holder.progressBar, holder.relativeLayout));
            if (snap.title && snap.getType() != 0) {
//                holder.snapTextView.setText(snap.getText());
            }
    }
    private void tempo(final int time, final LinearLayoutManager layoutManager){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                 if (POS < TOTAL){
                    layoutManager.scrollToPosition(++POS);
                    tempo(3000, layoutManager);
                } else if (POS == TOTAL && TOTAL > 1){
                     layoutManager.scrollToPosition(0);
                     tempo(3000, layoutManager);
                }
                //
            }
        }, time);
    }
    @Override
    public int getItemCount() {
        return mSnaps.size();
    }
    @Override
    public void onSnap(int position) {
        Log.d("Snapped: ", position + "");
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView snapTextView, carregando;
        RecyclerView recyclerView;
        ProgressBar progressBar;
        RelativeLayout relativeLayout;


        ViewHolder(View itemView) {
            super(itemView);
            relativeLayout = itemView.findViewById(R.id.relativeLayout);
            progressBar = itemView.findViewById(R.id.progressBar);
            snapTextView = itemView.findViewById(R.id.snapTextView);
            recyclerView = (RecyclerView) itemView.findViewById(R.id.recyclerView);
            carregando = itemView.findViewById(R.id.carregando);
            carregando.setVisibility(View.GONE);
        }
    }

}

