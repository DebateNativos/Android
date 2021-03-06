package com.nativos.forumriu;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.nativos.forumriu.models.DebateModel;
import com.nativos.forumriu.models.UserModel;


import static com.nativos.forumriu.R.drawable.user;
import static java.security.AccessController.getContext;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        ProfileFragment.OnFragmentInteractionListener,
        AboutUsFragment.OnFragmentInteractionListener,
        InsertCodeFragment.OnFragmentInteractionListener,
        RulesFragment.OnFragmentInteractionListener{


    FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fragmentContainer, new HomeFragment());
        fragmentTransaction.commit();
        getSupportActionBar().setTitle("Página Principal");

        setUserDataHeader();


    }

    public void setUserDataHeader() {

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View hView = navigationView.getHeaderView(0);
        UserModel userModel = getIntent().getParcelableExtra("userModel");
        String getEmail = userModel.getEmail();
        String getFullName = userModel.getName() + " " + userModel.getLastname();

        final TextView finalTextEmail = (TextView) hView.findViewById(R.id.textViewEmailHeader);
        finalTextEmail.setText(getEmail);

        final TextView finalTextFullName = (TextView) hView.findViewById(R.id.textViewFullnameHeader);
        finalTextFullName.setText(getFullName);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        if (drawer.isDrawerOpen(GravityCompat.START)) {
//            drawer.closeDrawer(GravityCompat.START);
//        } else {
//            super.onBackPressed();
//        }
       drawer.openDrawer(GravityCompat.START);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        // if (id == R.id.action_settings) {
        //     return true;
        //    }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        FragmentManager manager = getSupportFragmentManager();

        switch (item.getItemId()) {
            case R.id.nav_home:
                HomeFragment homeFragment = new HomeFragment();
                manager.beginTransaction().replace(R.id.fragmentContainer, homeFragment, homeFragment.getTag()).commit();
                getSupportActionBar().setTitle("Página Principal");
                break;
            case R.id.nav_insertCode:

                InsertCodeFragment insertCodeFragment = InsertCodeFragment.newInstance("1", "2");
                manager.beginTransaction().replace(R.id.fragmentContainer, insertCodeFragment, insertCodeFragment.getTag()).commit();
                getSupportActionBar().setTitle("Ingresar Código");
                break;
            case R.id.nav_profile:
                ProfileFragment profileFragment = ProfileFragment.newInstance("as", "sd");
                manager.beginTransaction().replace(R.id.fragmentContainer, profileFragment, profileFragment.getTag()).commit();
                getSupportActionBar().setTitle("Editar Perfil");
                break;
            case R.id.nav_signOut:
                Intent intent = new Intent(getBaseContext(), SignIn.class);
                startActivity(intent);
                break;
            case R.id.nav_rules:
                RulesFragment rulesFragment = RulesFragment.newInstance("as", "sd");
                manager.beginTransaction().replace(R.id.fragmentContainer, rulesFragment, rulesFragment.getTag()).commit();
                getSupportActionBar().setTitle("Reglamento");
                break;
            case R.id.nav_aboutUs:
                AboutUsFragment aboutUsFragment = AboutUsFragment.newInstance("1", "2");
                manager.beginTransaction().replace(R.id.fragmentContainer, aboutUsFragment, aboutUsFragment.getTag()).commit();
                getSupportActionBar().setTitle("Acerca de Nosotros");

                break;
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        //Recibir dato
    }


}
