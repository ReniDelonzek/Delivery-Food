package rd.com.demo.adapter;


import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.rubensousa.gravitysnaphelper.GravitySnapHelper;
import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;

import java.util.ArrayList;

import rd.com.demo.R;
import rd.com.demo.activity.ListaEstabelecimentos;
import rd.com.demo.item.Snap_Item;


public class SnapAdapterListaEstabelecimentos extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements
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

    public SnapAdapterListaEstabelecimentos() {
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
        View view; //= LayoutInflater.from(parent.getContext())//falso
               // .inflate(R.layout.adapter_snap, parent, false);;

        if (viewType == 0) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.conteiner_top, parent, false);
            view.findViewById(R.id.recyclerView).setOnTouchListener(mTouchListener);
            return new ViewHolder_Estabelecimentos(view);

        } else if (viewType == 1){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.container_snap, parent, false);
            return new ViewHolder(view);
        }else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.container_snap, parent, false);
            return new ViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        Snap_Item snap = mSnaps.get(position);
        if (viewHolder instanceof ViewHolder) {
            ViewHolder holder = (ViewHolder) viewHolder;
            if (snap.title && snap.getType() != 0) {
                holder.snapTextView.setText(snap.getText());
            }
            GridLayoutManager layoutManager = new GridLayoutManager(holder.recyclerView.getContext(), 2);
            holder.recyclerView.setLayoutManager(layoutManager);
                SnapHelper snapHelper = new LinearSnapHelper();
                snapHelper.attachToRecyclerView(holder.recyclerView);
                holder.recyclerView.setAdapter(new AdapterListaEstabelecimentos(
                        snap.getApps(), snap.getType(), holder.progressBar, holder.carregando));

        } else if (viewHolder instanceof ViewHolder_Estabelecimentos){
            ViewHolder_Estabelecimentos holder = (ViewHolder_Estabelecimentos) viewHolder;
            holder.carregando.setText(String.format("Carregando %s", ListaEstabelecimentos.tipoEstabelecimento));
                FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(holder.recyclerView.getContext());
                layoutManager.setFlexWrap(FlexWrap.WRAP);
                layoutManager.setFlexDirection(FlexDirection.ROW);
                layoutManager.setAlignItems(AlignItems.FLEX_START);
                layoutManager.setJustifyContent(JustifyContent.SPACE_AROUND);
                holder.recyclerView.setLayoutManager(layoutManager);

            SnapHelper snapHelper = new LinearSnapHelper();
            snapHelper.attachToRecyclerView(holder.recyclerView);
            holder.recyclerView.setAdapter(new AdapterListaEstabelecimentos(snap.getApps(),
                    snap.getType(), holder.progressBar, holder.carregando));
        }

    }
    @Override
    public int getItemCount() {
        return mSnaps.size();
    }
    @Override
    public void onSnap(int position) {
        Log.d("Snapped: ", position + "");
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView snapTextView, carregando;
        RecyclerView recyclerView;
        ProgressBar progressBar;

        ViewHolder(View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressBar);
            snapTextView = itemView.findViewById(R.id.snapTextView);
            recyclerView = itemView.findViewById(R.id.recyclerView);
            carregando = itemView.findViewById(R.id.carregando);
        }

    }
    class ViewHolder_Estabelecimentos extends RecyclerView.ViewHolder {
        RecyclerView recyclerView;
        ProgressBar progressBar;
        TextView carregando;

        ViewHolder_Estabelecimentos(View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressBar);
            recyclerView = itemView.findViewById(R.id.recyclerView);
            carregando = itemView.findViewById(R.id.carregando);

        }

    }

}

