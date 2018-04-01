
package rd.com.demo.adapter;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import rd.com.demo.R;
import rd.com.demo.activity.ListaEstabelecimentos;
import rd.com.demo.activity.DetalhesAmostra;
import rd.com.demo.activity.ListaProdutosCategorias;
import rd.com.demo.activity.MainActivity;
import rd.com.demo.auxiliares.Constants;
import rd.com.demo.banco.sugarOs.AmostrasFavoritasDB;
import rd.com.demo.item.firebase.Amostras;
import rd.com.demo.item.firebase.Estabelecimento;


public class AdapterListaEstabelecimentos extends RecyclerView.Adapter {
    private int type;
    private ProgressBar progressBar;
    private List<Object> list;
    TextView textView;
    AdapterListaEstabelecimentos(List<Object> apps, int type, ProgressBar progressBar, TextView carregando) {
        list = apps;
        this.type = type;
        this.progressBar = progressBar;
        this.textView = carregando;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        if (type == 0){
            viewHolder = new HolderEstabelecimentos(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_estabelecimentos, parent, false));
        } else if (type == 1){
            viewHolder = new HolderAmostras(
                    LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_amostras, parent, false));
        }
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof HolderEstabelecimentos) {//adaptador para os estabelecimentos
            final Estabelecimento estabelecimento = (Estabelecimento) list.get(position);
            final HolderEstabelecimentos holder = (HolderEstabelecimentos) viewHolder;
            progressBar.setVisibility(View.GONE);
            textView.setVisibility(View.GONE);
            holder.nameTextView.setText(estabelecimento.getNome());
            Picasso.with(holder.imageView.getContext()).
                    load(estabelecimento.getUrl()).into(holder.imageView);

            holder.linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {//trata do clique em um dos estabelecimentos
                    Intent intent = new Intent(v.getContext(), ListaProdutosCategorias.class);
                    intent.setAction("");
                    intent.putExtra("tipo", ListaEstabelecimentos.tipo);
                    intent.putExtra("id", estabelecimento.getId());
                    intent.putExtra("nome", estabelecimento.getNome());
                    intent.putExtra(Constants.cidadecode, estabelecimento.getCidadecode());
                    v.getContext().startActivity(intent);
                }
            });

        } else if (viewHolder instanceof HolderAmostras) {//adaptador para as amostra
            progressBar.setVisibility(View.GONE);
            textView.setVisibility(View.GONE);
            final Amostras amostra = (Amostras) list.get(position);
            final HolderAmostras holder = (HolderAmostras) viewHolder;
            holder.titulo.setText(amostra.getTitulo());
            String string;
            if (amostra.getQuantidade() == 1){
                string = amostra.getQuantidade() + " opção";
            } else {
                string = amostra.getQuantidade() + " opções";
            }
            holder.descricao.setText(string);
            Picasso.with(holder.imageView.getContext()).
                    load(amostra.getUrl()).into(holder.imageView);
            holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), DetalhesAmostra.class);
                    intent.putExtra("amostra", amostra);
                    intent.putExtra(Constants.cidadecode, amostra.getCidadecode());
                    v.getContext().startActivity(intent);
                }
            });
            holder.preco.setVisibility(View.GONE);
            if (MainActivity.amostrasFavoritasDB.contains(amostra.getCaminho())){
                holder.favoritar.setImageResource(R.drawable.ic_favorite_white_36dp);
            }
            holder.preco.setVisibility(View.GONE);
            holder.favoritar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (MainActivity.amostrasFavoritasDB.contains(amostra.getCaminho())){
                        int pos = MainActivity.amostrasFavoritasDB.indexOf(amostra.getCaminho());
                        AmostrasFavoritasDB.deleteAll(AmostrasFavoritasDB.class, "caminho = ?",
                                amostra.getCaminho());
                        holder.favoritar.setImageResource(R.drawable.ic_favorite_border_white_36dp);
                        Snackbar.make(v, "Amostra removida das favoritas", Snackbar.LENGTH_SHORT).show();
                        MainActivity.amostrasFavoritasDB.remove(pos);
                    } else {
                        AmostrasFavoritasDB favoritasDB = new AmostrasFavoritasDB(amostra.getTitulo(), amostra.getUrl(),
                                amostra.getCaminho(), amostra.getCategoria(), amostra.getNome_estabelecimento(), amostra.getId_estabelecimento(),
                                amostra.getQuantidade());
                        favoritasDB.save();
                        MainActivity.amostrasFavoritasDB.add(amostra.getCaminho());
                        Snackbar.make(holder.imageView, "Amostra adiciona aos favoritos", Snackbar.LENGTH_SHORT).show();
                        holder.favoritar.setImageResource(R.drawable.ic_favorite_white_36dp);
                    }
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private class HolderEstabelecimentos extends RecyclerView.ViewHolder {//para as categorias

        private final LinearLayout linearLayout;
        private final ImageView imageView;
        private final TextView nameTextView;

        HolderEstabelecimentos(View itemView) {
            super(itemView);
            linearLayout =  itemView.findViewById(R.id.linearLayout);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            nameTextView = (TextView) itemView.findViewById(R.id.titulo);
        }
    }
    private class HolderAmostras extends RecyclerView.ViewHolder implements View.OnClickListener {//holder produtos
        private final ImageView imageView;
        private final TextView titulo, descricao, preco;
        private final RelativeLayout relativeLayout;
        private final FloatingActionButton favoritar;

        HolderAmostras(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            favoritar = itemView.findViewById(R.id.edit);
            imageView = itemView.findViewById(R.id.imageView);
            titulo = itemView.findViewById(R.id.titulo);
            descricao = itemView.findViewById(R.id.text_quantidade);
            preco = itemView.findViewById(R.id.preco);
            relativeLayout = itemView.findViewById(R.id.linearLayout);

        }

        @Override
        public void onClick(View v) {

        }
    }
}

