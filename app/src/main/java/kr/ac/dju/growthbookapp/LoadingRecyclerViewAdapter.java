package kr.ac.dju.growthbookapp;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * Created by geonyounglim on 2017. 6. 2..
 */
public class LoadingRecyclerViewAdapter extends RecyclerView.Adapter<LoadingRecyclerViewAdapter.ViewHolder>  {

    public LoadingRecyclerViewAdapter() {

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.loading_item,parent,false);

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
