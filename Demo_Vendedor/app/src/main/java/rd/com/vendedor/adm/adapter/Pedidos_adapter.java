package rd.com.vendedor.adm.adapter;

/**
 * Created by Reni on 09/02/2018.
 */
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import rd.com.vendedor.R;
import rd.com.vendedor.adm.item.Pedidos;


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
                .inflate(R.layout.item_pedido, parent, false));
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {
        if (viewHolder instanceof Holder_Pedidos){//adaptador para as promoções do topo
            final Holder_Pedidos holder_pedidos = (Holder_Pedidos) viewHolder;
            final Pedidos amostras = list.get(position);
            holder_pedidos.descricao.setText(amostras.getDescricao());
            holder_pedidos.quantidade.setText(String.valueOf(amostras.getQuantidade()) + " Itens");
            holder_pedidos.status.setText(amostras.getStatus());
            holder_pedidos.titulo.setText(amostras.getTitulo());



        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private class Holder_Pedidos extends RecyclerView.ViewHolder {//holder anuncios topo
       private final TextView titulo, descricao, quantidade, status;


        Holder_Pedidos(View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.titulo);
            descricao = itemView.findViewById(R.id.descricao);
            quantidade = itemView.findViewById(R.id.descricao);
            status = itemView.findViewById(R.id.status);


        }
    }
}

