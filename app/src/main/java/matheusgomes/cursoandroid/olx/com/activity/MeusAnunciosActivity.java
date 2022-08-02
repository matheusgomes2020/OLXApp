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

import matheusgomes.cursoandroid.olx.com.R;
import matheusgomes.cursoandroid.olx.com.databinding.ActivityMeusAnunciosBinding;

public class MeusAnunciosActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMeusAnunciosBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMeusAnunciosBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);




        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity( new Intent( getApplicationContext(), CadastrarAnuncioActivity.class ));

            }
        });
    }


}