package kr.ac.dju.growthbookapp;

import android.content.res.TypedArray;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by geonyounglim on 2017. 6. 2..
 */
public class EmptyRecyclerViewAdapter extends RecyclerView.Adapter<EmptyRecyclerViewAdapter.ViewHolder>  {

    public EmptyRecyclerViewAdapter() {

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.empty_item,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

    }
    @Override
    public int getItemCount() {
        return 1;
    }



    public class ViewHolder extends  RecyclerView.ViewHolder {



        public ViewHolder(View itemView){
            super(itemView);


        }

    }


}
