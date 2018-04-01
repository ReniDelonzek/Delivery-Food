package rd.com.demo.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import rd.com.demo.R;

public class Imagem extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imagem);

        ImageView imageView = findViewById(R.id.imageView);
        String url = getIntent().getStringExtra("url");
        Picasso.with(getApplicationContext()).
                load(url).into(imageView);

    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
