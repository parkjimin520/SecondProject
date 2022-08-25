package com.example.secondproject;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SlidingAdapter extends RecyclerView.Adapter<SlidingAdapter.CustonViewHolder> {

    Context context;

    public interface OnItemClickListener{
        void onItemClick(int pos);
    }
    private OnItemClickListener mListener = null;
    public void setOnItemClickListener(OnItemClickListener listener){
        this.mListener = listener;
    }

    //리스트의 아이템 담을 배열
    private ArrayList<SlidingList> arrayList = null;
    public SlidingAdapter(ArrayList<SlidingList> list) {
        arrayList = list;
    }

    //아이템 클릭 상태를 저장할 배열
    private SparseBooleanArray selectedItems = new SparseBooleanArray();
    private int prePosition = -1;

    @NonNull
    @Override
    public SlidingAdapter.CustonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.slidinglist,parent,false);
        CustonViewHolder holder = new CustonViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull SlidingAdapter.CustonViewHolder holder, int position) {
        holder.onBind(arrayList.get(position),position);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }


    public class CustonViewHolder extends RecyclerView.ViewHolder{
        public ImageView imageView;
        private int position;

        public CustonViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION){
                        if(mListener != null){
                            mListener.onItemClick(pos);
                        }
                    }
                }
            });

            imageView = (ImageView) itemView.findViewById(R.id.imageView);

        }

        void onBind(SlidingList item, int position){
            this.position = position;

            imageView.setImageResource(item.getImg());
        }
    }
}
