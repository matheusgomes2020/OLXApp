package matheusgomes.cursoandroid.olx.com.activity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import matheusgomes.cursoandroid.olx.com.R;
import matheusgomes.cursoandroid.olx.com.adapter.AdapterAnuncios;
import matheusgomes.cursoandroid.olx.com.databinding.ActivityMeusAnunciosBinding;
import matheusgomes.cursoandroid.olx.com.model.Anuncio;

public class MeusAnunciosActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMeusAnunciosBinding binding;

    private List<Anuncio> anuncios = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMeusAnunciosBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);


        //AdapterAnuncios adapterAnuncios = new AdapterAnuncios( anuncios, this );


        //Configurar RecyclerView
        binding.i.recyclerAnuncios.setLayoutManager( new LinearLayoutManager( this ));
        binding.i.recyclerAnuncios.setHasFixedSize( true );

        //binding.i.recyclerAnuncios.setAdapter( adapterAnuncios );





        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity( new Intent( getApplicationContext(), CadastrarAnuncioActivity.class ));

            }
        });
    }


}