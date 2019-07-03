package com.stpl.pg4me.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.stpl.pg4me.Class.BoardedTenantClass;
import com.stpl.pg4me.R;
import com.stpl.pg4me.TenantDetailsActivity;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyHolder> {
    List<BoardedTenantClass> tenants;
    Context context;
    public RecyclerAdapter(List<BoardedTenantClass> routines, Context context)
    {
        this.tenants=routines;
        this.context=context;
    }

    @NonNull
    @Override
    public RecyclerAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_view, viewGroup,false);
        MyHolder myHolder=new MyHolder(view);
        return myHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapter.MyHolder myHolder, final int i) {

        BoardedTenantClass tenant= tenants.get(i);

        myHolder.name.setText(tenant.getTenantname());

        myHolder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(context, TenantDetailsActivity.class);
                intent.putExtra("Name", tenants.get(i).getTenantname());
                intent.putExtra("Phone", tenants.get(i).getTenantphone());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        int arr=0;
        try {
            if(tenants.size()==0)
            {
                arr=0;
            }else
            {
                arr=tenants.size();
            }
        }catch (Exception e){

        }
        return arr;
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        TextView name;
        CircleImageView dp;
        public MyHolder(View view) {
            super(view);
            name =view.findViewById(R.id.namerecycler);
            dp= view.findViewById(R.id.dprecycler);
        }
    }
}
