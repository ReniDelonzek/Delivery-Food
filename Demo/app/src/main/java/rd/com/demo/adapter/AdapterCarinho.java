package rd.com.demo.adapter;


import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import rd.com.demo.R;
import rd.com.demo.banco.sugarOs.Carinho_itemDB;


public class AdapterCarinho extends RecyclerView.Adapter {
    private List<Carinho_itemDB> list;
    public AdapterCarinho(List<Carinho_itemDB> apps) {
        list = apps;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_carinho, parent, false));
    }
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {
        final Carinho_itemDB carinhoitem = list.get(position);
        final Holder holder = (Holder) viewHolder;
        if (list.get(position).isCheck2()){
            holder.checkBox2.setChecked(true);
        }
        if (list.get(position).isCheck1()){
            holder.checkBox1.setChecked(true);
        }
        if (holder.getAdapterPosition() > 0){
            if (list.get(holder.getAdapterPosition() - 1).getid()//checa se o ultimo pedido listado e do mesmo estabelecimento que este
                    .equals(list.get(position).getid())){
                holder.checkBox1.setVisibility(View.GONE);//caso o ultimo pedido seja do mesmo, n exibe o nome do estabelecimento
                holder.cardView1.setVisibility(View.GONE);

            } else {//o ultimo produto e de estabelecimento diferente
                holder.checkBox1.setText(carinhoitem.getEstabelecimento());
            }
        }
        holder.checkBox1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                carinhoitem.setCheck1(isChecked);
                carinhoitem.setCheck2(isChecked);
                list.set(holder.getAdapterPosition(), carinhoitem);
                holder.checkBox2.setChecked(isChecked);

                if (holder.getAdapterPosition() < list.size()){//checa se este nao e o ultimo item da lista
                    int o = list.size() - holder.getAdapterPosition();
                    for(int i = 1; i < o; i++){//percore todos os itens da lista a partir do atual
                        // pra marcar todos os produtos do estabelecimento
                        if (list.get(holder.getAdapterPosition() + i).getid()
                                .equals(list.get(holder.getAdapterPosition()).getid())){
                            Carinho_itemDB p = list.get(holder.getAdapterPosition() + i);
                            p.setCheck2(isChecked);
                            p.setCheck1(isChecked);
                            list.set(holder.getAdapterPosition() + i, p);

                            notifyItemChanged(holder.getAdapterPosition() + i);
                        }
                    }
                }
            }
        });
        holder.checkBox2.setText(carinhoitem.getTitulo());
        holder.checkBox2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                carinhoitem.setCheck2(isChecked);
                list.set(holder.getAdapterPosition(), carinhoitem);
            }
        });
        holder.descricao.setText(carinhoitem.getDescricao());
        holder.quantidade.setText(String.valueOf(carinhoitem.getQuantidade()));
        holder.btAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quant = Integer.parseInt(carinhoitem.getQuantidade());
                    ++quant;
                    carinhoitem.setQuantidade(String.valueOf(quant));
                    list.set(holder.getAdapterPosition(), carinhoitem);
                holder.quantidade.setText(String.valueOf(carinhoitem.getQuantidade()));
            }
        });
        holder.btRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quant = Integer.parseInt(carinhoitem.getQuantidade());
                if (quant > 0){
                 --quant;
                 carinhoitem.setQuantidade(String.valueOf(quant));
                 list.set(holder.getAdapterPosition(), carinhoitem);
                }
                holder.quantidade.setText(String.valueOf(carinhoitem.getQuantidade()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private class Holder extends RecyclerView.ViewHolder {//
        private final TextView descricao, preco, quantidade;
        private final ImageButton btAdd, btRemove;
        private final CheckBox checkBox1, checkBox2;
        private final CardView cardView1, cardView2;

        Holder(View itemView) {
            super(itemView);

            cardView1 = itemView.findViewById(R.id.cardview1);
            cardView2 = itemView.findViewById(R.id.cardview);
            quantidade = itemView.findViewById(R.id.quantidade);
            descricao = itemView.findViewById(R.id.descricao);
            preco = itemView.findViewById(R.id.preco);
            btAdd = itemView.findViewById(R.id.bt_add);
            btRemove = itemView.findViewById(R.id.bt_remove);
            checkBox1 = itemView.findViewById(R.id.checkBox);
            checkBox2 = itemView.findViewById(R.id.checkBox2);



        }
    }

}



