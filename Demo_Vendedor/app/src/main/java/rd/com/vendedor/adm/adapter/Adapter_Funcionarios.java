package rd.com.vendedor.adm.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import rd.com.vendedor.R;
import rd.com.vendedor.adm.item.Funcionario;
import rd.com.vendedor.adm.item.Pedidos;

/**
 * Created by Reni on 15/02/2018.
 */

public class Adapter_Funcionarios extends RecyclerView.Adapter {
    private List<Funcionario> list;

    public Adapter_Funcionarios(List<Funcionario> objects) {
        list = objects;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return  new Holder_Pedidos(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_funcionario, parent, false));
    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {
        if (viewHolder instanceof Holder_Pedidos){//adaptador para as promoções do topo
            final Holder_Pedidos holder_funcionarios = (Holder_Pedidos) viewHolder;
            final Funcionario funcionario = list.get(position);
            holder_funcionarios.email.setText(funcionario.getEmail());
            holder_funcionarios.nome.setText(funcionario.getNome());
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private class Holder_Pedidos extends RecyclerView.ViewHolder {//holder anuncios topo
        private final TextView nome, email;


        Holder_Pedidos(View itemView) {
            super(itemView);
            nome = itemView.findViewById(R.id.nome);
            email = itemView.findViewById(R.id.email);



        }
    }
}
