package com.example.jetpackdemo.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.jetpackdemo.R;
import com.example.libnavannotation.FragmentDestination;


@FragmentDestination(pageUrl = "main/tabs/find", asStarter = false)
public class FindFragment extends Fragment {

    private FindViewModel mFindViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mFindViewModel = new ViewModelProvider(this).get(FindViewModel.class);
        View root = inflater.inflate(R.layout.fragment_find, container, false);
        final TextView textView = root.findViewById(R.id.text_dashboard);
        mFindViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}