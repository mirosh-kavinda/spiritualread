package me.mirosh.spiritualread.Dashboards;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import me.mirosh.spiritualread.BooksGuestFragment;
import me.mirosh.spiritualread.databinding.ActivityGuestBinding;
import me.mirosh.spiritualread.model.ModelCategory;

public class Guest extends AppCompatActivity {

    private ActivityGuestBinding binding;

    public ArrayList<ModelCategory> categoryArrayList;
    public ViewPagerAdapter viewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding= ActivityGuestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        setupViewPagerAdapter(binding.viewPager);
        binding.tabLayout.setupWithViewPager(binding.viewPager);

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
          onBackPressed();
            }
        });


    }

    private void setupViewPagerAdapter(ViewPager viewPager){
        viewPagerAdapter= new Guest.ViewPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT,this);

        categoryArrayList=new ArrayList<>();

        //load categories
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Categories");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //clear before adding to list
                categoryArrayList.clear();

                //load categories static
                //add data to models

                ModelCategory modelMostViewed=new ModelCategory("01","Most_Viewed","",1);
                ModelCategory modelMostDownloaded=new ModelCategory("02","Most_Downloaded","",1);


                //add model to list

                categoryArrayList.add(modelMostViewed);
                categoryArrayList.add(modelMostDownloaded);

                viewPagerAdapter.addFragment(BooksGuestFragment.newInstance(
                        ""+modelMostDownloaded.getId(),
                        ""+modelMostDownloaded.getCategory(),
                        ""+modelMostDownloaded.getUid()
                ), modelMostDownloaded.getCategory());
                viewPagerAdapter.addFragment(BooksGuestFragment.newInstance(
                        ""+modelMostViewed.getId(),
                        ""+modelMostViewed.getCategory(),
                        ""+modelMostViewed.getUid()
                ), modelMostViewed.getCategory());

                //REFRESH THE LIST
                viewPagerAdapter.notifyDataSetChanged();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //set adapter to pager
        viewPager.setAdapter(viewPagerAdapter);

    }

public class ViewPagerAdapter extends FragmentPagerAdapter{

    private ArrayList<BooksGuestFragment> fragmentList= new ArrayList<>();
    private ArrayList<String> fragmentTitleList=new ArrayList<>();
    private Context context;


    public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior, Context context) {
        super(fm, behavior);
        this.context=context;

    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    private void addFragment(BooksGuestFragment fragment,String title){
        //add fragment passed as parameter infragment list
        fragmentList.add(fragment);
        //add title passed as parameter in fragmentTitle list
        fragmentTitleList.add(title);
    }


    @Override
    public CharSequence getPageTitle(int position) {
        return fragmentTitleList.get(position);
    }
}


}
