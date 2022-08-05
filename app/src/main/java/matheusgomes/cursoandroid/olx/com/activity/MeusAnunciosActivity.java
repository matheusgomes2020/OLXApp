package matheusgomes.cursoandroid.olx.com.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.AdapterView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dmax.dialog.SpotsDialog;
import matheusgomes.cursoandroid.olx.com.R;
import matheusgomes.cursoandroid.olx.com.adapter.AdapterAnuncios;
import matheusgomes.cursoandroid.olx.com.databinding.ActivityMeusAnunciosBinding;
import matheusgomes.cursoandroid.olx.com.helper.ConfiguracaoFirebase;
import matheusgomes.cursoandroid.olx.com.helper.RecyclerItemClickListener;
import matheusgomes.cursoandroid.olx.com.model.Anuncio;

public class MeusAnunciosActivity extends AppCompatActivity {

    private ActivityMeusAnunciosBinding binding;

    private List<Anuncio> anuncios = new ArrayList<>();

    private AdapterAnuncios adapterAnuncios;

    private DatabaseReference anuncioUsuarioRef;

    private AlertDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMeusAnunciosBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        anuncioUsuarioRef = ConfiguracaoFirebase.getFirebase()
                .child( "meus_anuncios" )
                        .child( ConfiguracaoFirebase.getidUsuario() );

        //Configurar RecyclerView
        binding.i.recyclerAnuncios.setLayoutManager( new LinearLayoutManager( this ));
        binding.i.recyclerAnuncios.setHasFixedSize( true );
        adapterAnuncios = new AdapterAnuncios( anuncios, this );
        binding.i.recyclerAnuncios.setAdapter( adapterAnuncios );


        //Recupera anuncios para o susaurio
        recuperarAnuncios();

        //Adiciona evento de clique no recyclerView
        binding.i.recyclerAnuncios.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        this,
                        binding.i.recyclerAnuncios,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {

                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                                Anuncio anuncioSelecionado =  anuncios.get( position );
                                anuncioSelecionado.remover();

                                adapterAnuncios.notifyDataSetChanged();

                            }

                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                            }
                        }
                )
        );

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity( new Intent( getApplicationContext(), CadastrarAnuncioActivity.class ));

            }
        });
    }

    private void recuperarAnuncios() {

        dialog = new SpotsDialog.Builder()
                .setContext( this )
                .setMessage( "Recuperando anúncios" )
                .setCancelable( false )
                .build();

        dialog.show();

        anuncioUsuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                anuncios.clear();

                for ( DataSnapshot ds : snapshot.getChildren(  ) ){

                    anuncios.add( ds.getValue( Anuncio.class ) );

                }

                Collections.reverse( anuncios );
                adapterAnuncios.notifyDataSetChanged();

                dialog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


}