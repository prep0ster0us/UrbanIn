package com.example.urbanin.account;

// AccountFragment.java
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.urbanin.R;

import java.util.ArrayList;
import java.util.List;

public class AccountFragment extends Fragment {

    private RecyclerView recyclerView;
    private AccountOptionsAdapter adapter;
    private List<AccountOption> options;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        // Initialize the options list
        options = new ArrayList<>();
        // Add options. Replace R.drawable.placeholder with actual drawable resources
        options.add(new AccountOption(R.drawable.ic_profile, "My profile"));
        options.add(new AccountOption(R.drawable.ic_orders, "My orders"));
        // ... add other options

        recyclerView = view.findViewById(R.id.rvAccountOptions);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize the adapter
        adapter = new AccountOptionsAdapter(options, new AccountOptionsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(AccountOption option) {
                // Handle item click events here
            }
        });
        recyclerView.setAdapter(adapter);

        return view;
    }
}
