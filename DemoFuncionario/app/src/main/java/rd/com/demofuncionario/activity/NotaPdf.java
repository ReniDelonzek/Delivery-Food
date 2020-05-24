package rd.com.demofuncionario.activity;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.PrintManager;
import android.print.pdf.PrintedPdfDocument;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import rd.com.demofuncionario.R;
import rd.com.demofuncionario.item.Sugar.Estabelecimento;
import rd.com.demofuncionario.item.firebase.Pedidos;

public class NotaPdf extends AppCompatActivity {
    List<Estabelecimento>  estabelecimentos;
    Pedidos pedido;
    List<Pedidos> pedidos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nota_pdf);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                printDocument(view);
            }
        });
        pedido = (Pedidos) getIntent().getSerializableExtra("pedido");
        estabelecimentos = new ArrayList<>();
        estabelecimentos = Estabelecimento.listAll(Estabelecimento.class);

        pedidos = new ArrayList<>();

        if (pedido.getCodigo().contains("¨")) {//varios produtos
            String[] codigos = pedido.getCodigo().split("¨");
            String[] titulos = pedido.getTitulo().split("¨");
            String[] descricoes = pedido.getDescricao().split("¨");
            String[] quantidades = pedido.getQuantidade().split("¨");
            //String[] status = pedido.getStatus().split("¨");


            for (int i = 0; i < codigos.length; i++) {
                pedidos.add(new Pedidos(titulos[i], descricoes[i], codigos[i], pedido.getStatus(), pedido.getNomeEstabelecimento(),
                        pedido.getIdEstabelecimento(), quantidades[i], pedido.getUserid(), pedido.getUsername(),
                        pedido.getEndereco(), pedido.getTipoEntrega(),
                        pedido.getData(), pedido.getHora(), pedido.getMensagem(), pedido.getStatus_code(), pedido.getUserToken()));
            }

        } else {//pedido de item unico
            pedidos.add(pedido);
        }


    }
    public void printDocument(View view) {
        PrintManager printManager = (PrintManager) this
                .getSystemService(Context.PRINT_SERVICE);

        String jobName ="Nota fiscal";
        if (printManager != null) {
            printManager.print(jobName, new MyPrintDocumentAdapter(this),
                    null);
        }
    }



    public class MyPrintDocumentAdapter extends PrintDocumentAdapter {
        Context context;
        private int pageHeight;
        private int pageWidth;
        PdfDocument myPdfDocument;
        int totalpages = 1;

        MyPrintDocumentAdapter(Context context) {
            this.context = context;
        }
        @Override
        public void onLayout(PrintAttributes oldAttributes,
                             PrintAttributes newAttributes,
                             CancellationSignal cancellationSignal,
                             LayoutResultCallback callback,
                             Bundle metadata) {

            myPdfDocument = new PrintedPdfDocument(context, newAttributes);
            pageHeight = newAttributes.getMediaSize().getHeightMils()/1000 * 72;
            pageWidth = newAttributes.getMediaSize().getWidthMils()/1000 * 72;

            if (cancellationSignal.isCanceled() ) {
                callback.onLayoutCancelled();
                return;
            }

            if (totalpages > 0) {
                PrintDocumentInfo.Builder builder = new PrintDocumentInfo
                        .Builder("nota" + totalpages +".pdf")
                        .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                        .setPageCount(totalpages);

                PrintDocumentInfo info = builder.build();
                callback.onLayoutFinished(info, true);
            } else {
                callback.onLayoutFailed("Page count is zero.");
            }
        }



        public void onWrite(final PageRange[] pageRanges,
                            final ParcelFileDescriptor destination,
                            final CancellationSignal cancellationSignal,
                            final WriteResultCallback callback) {

            for (int i = 0; i < totalpages; i++) {
                if (pageInRange(pageRanges, i))
                {
                    PdfDocument.PageInfo newPage = new PdfDocument.PageInfo.Builder(pageWidth,
                            pageHeight, i).create();

                    PdfDocument.Page page =
                            myPdfDocument.startPage(newPage);

                    if (cancellationSignal.isCanceled()) {
                        callback.onWriteCancelled();
                        myPdfDocument.close();
                        myPdfDocument = null;
                        return;
                    }

                    int margin = drawProdutos(page,  drawEstabelecimentos(page, 72));
                    drawComprador(page, margin);
                    myPdfDocument.finishPage(page);
                }
            }

            try {
                myPdfDocument.writeTo(new FileOutputStream(
                        destination.getFileDescriptor()));
            } catch (IOException e) {
                callback.onWriteFailed(e.toString());
                return;
            } finally {
                myPdfDocument.close();
                myPdfDocument = null;
            }

            callback.onWriteFinished(pageRanges);
        }

        private boolean pageInRange(PageRange[] pageRanges, int page) {
            for (PageRange pageRange : pageRanges) {
                if ((page >= pageRange.getStart()) &&
                        (page <= pageRange.getEnd()))
                    return true;
            }
            return false;
        }
        private int drawEstabelecimentos(PdfDocument.Page page, int p) {
            Estabelecimento estabelecimento = estabelecimentos.get(0);
            Canvas canvas = page.getCanvas();
            int titleBaseLine = p;
            int leftMargin = 50;

            Paint paint = new Paint();
            paint.setColor(Color.BLACK);
            paint.setTextSize(25);
            drawText(canvas, "Nota não fiscal", leftMargin, titleBaseLine, paint);

            paint.setTextAlign(Paint.Align.LEFT);
            paint.setTextSize(14);//define as informacoes da empresa
            drawText(canvas, estabelecimento.getNome(), 20,
                    titleBaseLine += 50, paint);//margem entre o titulo e as infomacoes do estabelecimento
            drawText(canvas, estabelecimento.getEndereco() + " - " + estabelecimento.getCidade(),
                    20, titleBaseLine += 20, paint);
            drawText(canvas, estabelecimento.getTelefone(), 20, titleBaseLine += 20, paint);
            drawText(canvas, estabelecimento.getEmail(), 20, titleBaseLine +=20, paint);
            //descricao da empresa
            return titleBaseLine;
        }
    }

    private void drawComprador(PdfDocument.Page page, int margin) {

        Canvas canvas = page.getCanvas();
        int titleBaseLine = margin ;
        int leftMargin = 50;

        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(25);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            paint.setElegantTextHeight(true);
        }
        drawText(canvas, "Nome do comprador:", leftMargin, titleBaseLine, paint);
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTextSize(14);//define as informacoes da empresa

        drawText(canvas, pedido.getUsername(), 20, titleBaseLine += 50, paint);
    }

    private void drawText(Canvas canvas, String text,
                          int leftMargin, int marginTop, Paint paint){
        canvas.drawText(text, leftMargin, marginTop
                , paint);
    }

    private int drawProdutos(PdfDocument.Page page, int margin) {
        int titleBaseLine = margin;
        for(int i = 0; i < pedidos.size(); i++){
            Pedidos p = pedidos.get(i);
            Canvas canvas = page.getCanvas();

            int leftMargin = 50;

            Paint paint = new Paint();
            paint.setColor(Color.BLACK);
            paint.setTextSize(25);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                paint.setElegantTextHeight(true);
            }
            drawText(canvas, "Descrição dos produtos:", leftMargin, titleBaseLine, paint);
            paint.setTextAlign(Paint.Align.LEFT);
            paint.setTextSize(14);//define as informacoes da empresa

            drawText(canvas, p.getTitulo(), 20, titleBaseLine += 50, paint);
            //margem entre o titulo e as infomacoes do estabelecimento
            drawText(canvas, p.getDescricao(), 20, titleBaseLine += 20, paint);
            drawText(canvas, p.getQuantidade(), 20, titleBaseLine += 20, paint);
            drawText(canvas, p.getTipoEntrega(), 20, titleBaseLine +=20, paint);
            drawText(canvas, p.getData(), 20, titleBaseLine +=20, paint);
            //drawText(canvas, p.getPreco(), 20, titleBaseLine +=20, paint);
        }
        return titleBaseLine;
    }


}
