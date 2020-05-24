package rd.com.demo.adapter;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import rd.com.demo.R;
import rd.com.demo.activity.DetalhesAmostra;
import rd.com.demo.activity.MainActivity;
import rd.com.demo.auxiliares.Constants;
import rd.com.demo.banco.sugarOs.AmostrasFavoritasDB;
import rd.com.demo.item.firebase.Amostras;

/**
 * Created by Reni on 14/02/2018.
 */

public class AdapterAmostras extends RecyclerView.Adapter {
    List<Amostras> list;

    public AdapterAmostras(List<Amostras> amostrasList){
        list = amostrasList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_amostras, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final Amostras produto = list.get(position);
        final Holder holder = (Holder) viewHolder;
        holder.titulo.setText(produto.getTitulo());
        String string;
        if (produto.getQuantidade() == 1){
            string = produto.getQuantidade() + " opção";
        } else {
            string = produto.getQuantidade() + " opções";
        }
        holder.descricao.setText(string);
        Picasso.with(holder.imageView.getContext()).
                load(produto.getUrl()).into(holder.imageView);
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), DetalhesAmostra.class);
                intent.putExtra("amostra", produto);
                intent.putExtra("nome", produto.getNome_estabelecimento());
                intent.putExtra("id", produto.getId_estabelecimento());
                intent.putExtra(Constants.cidadecode, produto.getCidadecode());
                v.getContext().startActivity(intent);
            }
        });
        if (MainActivity.amostrasFavoritasDB.contains(produto.getCaminho())){
            holder.favoritar.setImageResource(R.drawable.ic_favorite_white_36dp);
        }
        holder.preco.setVisibility(View.GONE);
        holder.favoritar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MainActivity.amostrasFavoritasDB.contains(produto.getCaminho())){
                    int pos = MainActivity.amostrasFavoritasDB.indexOf(produto.getCaminho());
                    AmostrasFavoritasDB.deleteAll(AmostrasFavoritasDB.class, "caminho = ?",
                            produto.getCaminho());
                    holder.favoritar.setImageResource(R.drawable.ic_favorite_border_white_36dp);
                    Snackbar.make(v, "Amostra removida das favoritas", Snackbar.LENGTH_SHORT).show();
                    MainActivity.amostrasFavoritasDB.remove(pos);
                } else {
                    AmostrasFavoritasDB favoritasDB = new AmostrasFavoritasDB(produto.getTitulo(), produto.getUrl(),
                            produto.getCaminho(), produto.getCategoria(), produto.getNome_estabelecimento(), produto.getId_estabelecimento(),
                            produto.getQuantidade());
                    favoritasDB.save();
                    MainActivity.amostrasFavoritasDB.add(produto.getCaminho());
                    Snackbar.make(holder.imageView, "Amostra adiciona aos favoritos", Snackbar.LENGTH_SHORT).show();
                    holder.favoritar.setImageResource(R.drawable.ic_favorite_white_36dp);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    private class Holder extends RecyclerView.ViewHolder {
        private final ImageView imageView;
        final TextView titulo;
        private final TextView descricao;
        private final TextView preco;
        private final RelativeLayout relativeLayout;
        private final FloatingActionButton favoritar;

        Holder(View itemView) {
            super(itemView);
            favoritar = itemView.findViewById(R.id.edit);
            imageView = itemView.findViewById(R.id.imageView);
            titulo = itemView.findViewById(R.id.titulo);
            descricao = itemView.findViewById(R.id.text_quantidade);
            preco = itemView.findViewById(R.id.preco);
            relativeLayout = itemView.findViewById(R.id.linearLayout);

        }

    }
}

