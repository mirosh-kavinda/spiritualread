package me.mirosh.spiritualread.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import me.mirosh.spiritualread.adapters.AdapterPdfUser;
import me.mirosh.spiritualread.databinding.FragmentBooksGuestBinding;
import me.mirosh.spiritualread.model.ModelPdf;

public class BooksGuestFragment extends Fragment {
    private static final String TAG = "BOOKS_USER_TAG";
    private String categoryId,category,uid;
    private ArrayList<ModelPdf>pdfArrayList;
    private AdapterPdfUser adapterPdfUser;
    private FragmentBooksGuestBinding binding;

    public BooksGuestFragment() {
        // Required empty public constructor
    }

    public static BooksGuestFragment newInstance(String id, String category, String uid) {
        BooksGuestFragment fragment = new BooksGuestFragment();
        Bundle args = new Bundle();
        args.putString("categoryId", id);
        args.putString("category", category);
        args.putString("uid", uid);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            categoryId = getArguments().getString("categoryId");
            category = getArguments().getString("category");
            uid = getArguments().getString("uid");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment /and binding
        binding=FragmentBooksGuestBinding.inflate(LayoutInflater.from(getContext()),container,false);

        Log.d(TAG, "onCreateView: Category :"+category);

        if(category.equals("Most_Viewed")){
            //most view ones load
            LoadMostViewedDownloadedBooks("viewsCount");
        }else if(category.equals("Most_Downloaded")){
            //most Downloaded ones load
            LoadMostViewedDownloadedBooks("downloadsCount");
        }
        //search
        binding.searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    adapterPdfUser.getFilter().filter(s);
                }catch (Exception e){
                    Log.d("Search_View", "onTextChanged: "+e.getMessage());
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        return binding.getRoot();
    }

    private void LoadMostViewedDownloadedBooks(String orderBy) {
        //init list
        pdfArrayList =new ArrayList<>();

        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Books");
        ref.orderByChild(orderBy).limitToLast(10)//load most viwee or donalod books

                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //clear list before starting adding data into it
                        pdfArrayList.clear();

                        for(DataSnapshot ds:snapshot.getChildren()){
                            //get data
                    ModelPdf model=ds.getValue(ModelPdf.class);
//                            add to list
                    pdfArrayList.add(model);
                        }
                        ///setup adapter
                        adapterPdfUser=new AdapterPdfUser(getContext(),pdfArrayList);
                        //set adapter to recycler view
                        binding.booksRv.setAdapter(adapterPdfUser);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }



}