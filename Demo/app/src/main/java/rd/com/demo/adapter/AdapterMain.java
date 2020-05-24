package rd.com.demo.adapter;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;

import rd.com.demo.R;
import rd.com.demo.activity.DetalhesAmostra;
import rd.com.demo.activity.DetalhesProduto;
import rd.com.demo.activity.ListaEstabelecimentos;
import rd.com.demo.activity.ListaProdutosCategorias;
import rd.com.demo.activity.MainActivity;
import rd.com.demo.auxiliares.Constants;
import rd.com.demo.banco.sugarOs.AmostrasFavoritasDB;
import rd.com.demo.banco.sugarOs.Carinho_itemDB;
import rd.com.demo.item.firebase.Amostras;
import rd.com.demo.item.firebase.Estabelecimento;
import rd.com.demo.item.firebase.Item_Imagem;
import rd.com.demo.item.firebase.Lista_Opcoes;
import rd.com.demo.item.firebase.Produto;


public class AdapterMain extends RecyclerView.Adapter {
    private int type;
    private ProgressBar progressBar;
    private RelativeLayout relativeLayout;

    private List<Object> list;
    AdapterMain(List<Object> apps, int type, ProgressBar progressBar,
                RelativeLayout holder) {
        list = apps;
        this.type = type;
        this.progressBar = progressBar;
        this.relativeLayout = holder;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        if (type == 0) {
            viewHolder = new Holder_Header(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.adapter_item_propag, parent, false));
        } else if (type == 1){
            viewHolder = new Holder_principal(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item, parent, false));
        } else if (type == 2){
            viewHolder = new Holder_Amostras(
                    LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_amostras, parent, false));
        } else if (type == 3){
            viewHolder = new HolderEstabelecimentos(
                    LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.item_estabelecimentos, parent, false));
        } else if (type == 4){
            viewHolder = new ViewHolderSearch(
                    LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.item_pesquisa, parent, false));
        } else if (type == 5){
            viewHolder = new Holder_Amostras(
                    LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.item_amostras, parent, false));
        }
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof Holder_principal) {//adaptador para as categorias

            final Lista_Opcoes servicos = (Lista_Opcoes) list.get(position);
            final Holder_principal holder = (Holder_principal) viewHolder;
            holder.imageView.setImageResource(servicos.getDrawable());
            holder.nameTextView.setText(servicos.getName());
            /*
            holder.linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {//trata do clique em um item das categorias
                    Intent intent = new Intent(holder.linearLayout.getContext(), ListaEstabelecimentos.class);
                    intent.putExtra(Constants.cidadecode, MainActivity.cidadecode);
                    intent.putExtra("tipo", servicos.getCod());
                    intent.putExtra("nome", servicos.getName());
                    holder.linearLayout.getContext().startActivity(intent);
                }
            });
*/
        } else if (viewHolder instanceof Holder_Header){//adaptador para as promoções do topo
            Item_Imagem header = (Item_Imagem) list.get(position);
            final Holder_Header holderHeader = (Holder_Header) viewHolder;
            Picasso.with(holderHeader.imageView.getContext()).
                    load(header.getUrl()).into(holderHeader.imageView);
            holderHeader.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Snackbar.make(holderHeader.cardView, "Aqui abre a tela do item em promoção", Snackbar.LENGTH_SHORT).show();
                }
            });

        } else if (viewHolder instanceof Holder_Amostras) {//adaptador para os amostras
            /*
            progressBar.setVisibility(View.GONE);
            final Amostras produto = (Amostras) list.get(position);
            final Holder_Amostras holder = (Holder_Amostras) viewHolder;
            holder.titulo.setText(produto.getTitulo());
            String string;
            if (produto.getQuantidade() == 1){
                string = produto.getQuantidade() + " opção";
            } else {
                string = produto.getQuantidade() + " opções";
            }
            holder.descricao.setText(string);
            //Picasso.with(holder.imageView.getContext()).
              //      load(produto.getUrl()).into(holder.imageView);
            holder.imageView.setImageResource(R.drawable.carne_vermelha);
            holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), DetalhesAmostra.class);
                    intent.putExtra("amostra", produto);
                    intent.putExtra("id", produto.getId_estabelecimento());
                    intent.putExtra("nome", produto.getNome_estabelecimento());

                    v.getContext().startActivity(intent);
                }
            });
//            if (MainActivity.amostrasFavoritasDB.contains(produto.getCaminho())){
  //              holder.favoritar.setImageResource(R.drawable.ic_favorite_white_36dp);
   ///         }
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
            */
            progressBar.setVisibility(View.GONE);
            final Produto produto = (Produto) list.get(position);
            final Holder_Amostras holder = (Holder_Amostras) viewHolder;
            holder.imageView.setImageResource(produto.getCaminho());
            holder.titulo.setText(produto.getNome());
            holder.preco.setText(String.format(Locale.getDefault(), "R$%.2f", produto.getPreco()));

            holder.favoritar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adc_carinho(produto, v);
                }
            });
            holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), DetalhesProduto.class);
                    intent.putExtra("produto", (Serializable) list.get(holder.getAdapterPosition()));
                    v.getContext().startActivity(intent);

                }
            });

        } else  if (viewHolder instanceof HolderEstabelecimentos) {//adaptador para os estabelecimentos
            relativeLayout.setVisibility(View.VISIBLE);
            final Estabelecimento estabelecimento = (Estabelecimento) list.get(position);
            final HolderEstabelecimentos holder = (HolderEstabelecimentos) viewHolder;
            progressBar.setVisibility(View.GONE);
            holder.nameTextView.setText(estabelecimento.getNome());
            Picasso.with(holder.imageView.getContext()).
                    load(estabelecimento.getUrl()).into(holder.imageView);

            holder.linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {//trata do clique em um dos estabelecimentos
                    /*
                    Intent intent = new Intent(v.getContext(), ListaProdutosCategorias.class);
                    intent.setAction("");
                    intent.putExtra("tipo", ListaEstabelecimentos.tipo);
                    intent.putExtra("id", estabelecimento.getId());
                    intent.putExtra("nome", estabelecimento.getNome());
                    */
                   // v.getContext().startActivity(intent);
                }
            });

        } else if (viewHolder instanceof ViewHolderSearch){

        }
    }
    private void adc_carinho(Produto produto, View v) {
        List<Carinho_itemDB> itensCarinho = Carinho_itemDB.find(Carinho_itemDB.class, "codigo = ? " +
                        "and estabelecimentoid = ?",
                produto.getCodigo(), produto.getEstabelecimento_id());//le todos os itens ja presentes no carinho

        Long tsLong = System.currentTimeMillis()/1000;
        String ts = tsLong.toString();
        if (itensCarinho.isEmpty()){//nao existe nenhum pedido assim no carinho, adiciona um novo
            Carinho_itemDB pedido = new Carinho_itemDB(produto.getNome(), produto.getDescricao(), produto.getTipo_estabelecimento(), produto.getEstabelecimento(),
                    produto.getEstabelecimento_id(), produto.getCodigo(), produto.getUrl(), "1", String.valueOf(produto.getPreco()), ts, true, true, produto.getCidadecode(),
                    produto.getCidade(), produto.getNomeamostra());
            pedido.save();
            Snackbar.make(v, "Produto adicionado ao Carrinho :)", Snackbar.LENGTH_SHORT).show();
        } else if (itensCarinho.size() == 1){//o produto ja consta no carrinho, apenas adiciona mais um item
            Carinho_itemDB carinhoitem1 = itensCarinho.get(0);
            carinhoitem1.setQuantidade(String.valueOf(Integer.parseInt(carinhoitem1.getQuantidade()) + 1));
            carinhoitem1.save();
            Snackbar.make(v, String.valueOf(carinhoitem1.getQuantidade()) +
                    " itens no carinho", Snackbar.LENGTH_SHORT).show();
        }

    }
    @Override
    public int getItemCount() {
        return list.size();
    }

    private class Holder_principal extends RecyclerView.ViewHolder {//para as categorias

        private final LinearLayout linearLayout;
        private final ImageView imageView;
        private final TextView nameTextView;

        Holder_principal(View itemView) {
            super(itemView);
            linearLayout =  itemView.findViewById(R.id.linearLayout);
            imageView = itemView.findViewById(R.id.imageView);
            nameTextView = itemView.findViewById(R.id.titulo);
        }
    }
    private class Holder_Header extends RecyclerView.ViewHolder implements View.OnClickListener {//holder anuncios topo
        private final ImageView imageView;
        private final CardView cardView;
        Holder_Header(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            cardView = itemView.findViewById(R.id.cardview);
            imageView = itemView.findViewById(R.id.imageView);
        }

        @Override
        public void onClick(View v) {

        }
    }
    private class Holder_Amostras extends RecyclerView.ViewHolder implements View.OnClickListener {//holder anuncios topo
        private final ImageView imageView;
        private final TextView titulo, descricao, preco;
        private final RelativeLayout relativeLayout;
        private final FloatingActionButton favoritar;

        Holder_Amostras(View itemView) {
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
    private class HolderEstabelecimentos extends RecyclerView.ViewHolder {//para as categorias

        private final LinearLayout linearLayout;
        private final ImageView imageView;
        private final TextView nameTextView;

        HolderEstabelecimentos(View itemView) {
            super(itemView);
            linearLayout =  itemView.findViewById(R.id.linearLayout);
            imageView = itemView.findViewById(R.id.imageView);
            nameTextView = itemView.findViewById(R.id.titulo);
        }
    }
    private  class ViewHolderSearch extends RecyclerView.ViewHolder {
        SearchView searchView;

        ViewHolderSearch(View itemView) {
            super(itemView);
            searchView = itemView.findViewById(R.id.search);
        }

    }
}

