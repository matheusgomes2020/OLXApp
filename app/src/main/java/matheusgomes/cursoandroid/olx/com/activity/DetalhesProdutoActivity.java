package matheusgomes.cursoandroid.olx.com.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import matheusgomes.cursoandroid.olx.com.R;
import matheusgomes.cursoandroid.olx.com.databinding.ActivityDetalhesProdutoBinding;

public class DetalhesProdutoActivity extends AppCompatActivity {

    private ActivityDetalhesProdutoBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityDetalhesProdutoBinding.inflate( getLayoutInflater() );
        setContentView( binding.getRoot() );
    }
}