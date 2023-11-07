package com.example.urbanin.account;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.urbanin.R;

import java.util.List;

public class AccountOptionsAdapter extends RecyclerView.Adapter<AccountOptionsAdapter.ViewHolder> {

    private List<AccountOption> options;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(AccountOption option);
    }

    public AccountOptionsAdapter(List<AccountOption> options, OnItemClickListener listener) {
        this.options = options;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_account_option, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AccountOption option = options.get(position);
        holder.bind(option, listener);
    }

    @Override
    public int getItemCount() {
        return options.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView iconView;
        private TextView titleView;

        public ViewHolder(View itemView) {
            super(itemView);
            iconView = itemView.findViewById(R.id.ivIcon);
            titleView = itemView.findViewById(R.id.tvOption);
        }

        public void bind(final AccountOption option, final OnItemClickListener listener) {
            iconView.setImageResource(option.getIcon());
            titleView.setText(option.getTitle());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(option);
                }
            });
        }
    }
}
