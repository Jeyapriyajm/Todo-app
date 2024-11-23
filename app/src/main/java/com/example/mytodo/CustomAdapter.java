package com.example.mytodo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {
    private Context context;
    private Activity activity;
    private ArrayList<String> taskIds, taskNames, taskDescriptions;

    public CustomAdapter(Activity activity, Context context, ArrayList<String> taskIds, ArrayList<String> taskNames, ArrayList<String> taskDescriptions) {
        this.activity = activity;
        this.context = context;
        this.taskIds = taskIds;
        this.taskNames = taskNames;
        this.taskDescriptions = taskDescriptions;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.my_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.taskIdTxt.setText(taskIds.get(position));
        holder.taskNameTxt.setText(taskNames.get(position));
        holder.taskDescTxt.setText(taskDescriptions.get(position));

        Animation animation = AnimationUtils.loadAnimation(context, R.anim.item_animation);
        holder.mainLayout.startAnimation(animation);

        holder.mainLayout.setOnClickListener(v -> {
            Intent intent = new Intent(context, updateActivity.class);
            intent.putExtra("id", taskIds.get(position));
            intent.putExtra("task", taskNames.get(position));
            intent.putExtra("description", taskDescriptions.get(position));
            activity.startActivityForResult(intent, 1);
        });
    }

    @Override
    public int getItemCount() {
        return taskIds.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView taskIdTxt, taskNameTxt, taskDescTxt;
        LinearLayout mainLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            taskIdTxt = itemView.findViewById(R.id.task_id_txt);
            taskNameTxt = itemView.findViewById(R.id.task_txt);
            taskDescTxt = itemView.findViewById(R.id.task_desc_txt);
            mainLayout = itemView.findViewById(R.id.mainLayout);
        }
    }

    public void removeItem(int position) {
        taskIds.remove(position);
        taskNames.remove(position);
        taskDescriptions.remove(position);
        notifyItemRemoved(position);
        updateIds();
    }

    private void updateIds() {
        for (int i = 0; i < taskIds.size(); i++) {
            taskIds.set(i, String.valueOf(i + 1));
        }
        notifyDataSetChanged();
    }

    public void updateData(ArrayList<String> newTaskIds, ArrayList<String> newTaskNames, ArrayList<String> newTaskDescriptions) {
        taskIds.clear();
        taskNames.clear();
        taskDescriptions.clear();

        taskIds.addAll(newTaskIds);
        taskNames.addAll(newTaskNames);
        taskDescriptions.addAll(newTaskDescriptions);

        notifyDataSetChanged();
    }
}
