package rd.com.demofuncionario.adapter;

/**
 * Created by Reni on 09/02/2018.
 */
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import rd.com.demofuncionario.R;
import rd.com.demofuncionario.activity.Detalhes_pedido;
import rd.com.demofuncionario.item.firebase.Pedidos;


public class Pedidos_adapter extends RecyclerView.Adapter {
    private int type;
    private List<Pedidos> list;

    public Pedidos_adapter(List<Pedidos> objects) {
        list = objects;
        this.type = 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        viewHolder = new Holder_Pedidos(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_detalhes_pedido, parent, false));
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {
        if (viewHolder instanceof Holder_Pedidos){//adaptador para aos detalhes pedido
            final Holder_Pedidos holder_pedidos = (Holder_Pedidos) viewHolder;
            final Pedidos pedidos = list.get(position);

            holder_pedidos.titulo.setText(pedidos.getTitulo());
            holder_pedidos.detalhes.setText(pedidos.getDescricao());
            holder_pedidos.quantidade.setText(String.format("%sx - ", pedidos.getQuantidade()));
            holder_pedidos.relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private class Holder_Pedidos extends RecyclerView.ViewHolder {//holder anuncios topo
        private final TextView titulo, detalhes, quantidade;
        private final RelativeLayout relativeLayout;


        Holder_Pedidos(View itemView) {
            super(itemView);
            relativeLayout = itemView.findViewById(R.id.relativeLayout);
            quantidade = itemView.findViewById(R.id.quantidade);
            titulo = itemView.findViewById(R.id.titulo);
            detalhes = itemView.findViewById(R.id.detalhes);


        }
    }
}

