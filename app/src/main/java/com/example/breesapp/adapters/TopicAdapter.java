package com.example.breesapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.breesapp.R;
import com.example.breesapp.activities.FAQ_Element_Activity;
import com.example.breesapp.models.Topic;

import java.util.ArrayList;
import java.util.List;

public class TopicAdapter extends RecyclerView.Adapter<TopicAdapter.ViewHolder> {

    public interface OnTopicClickListener{
        void onTopicClick(Topic state, int position);
    }

    public final OnTopicClickListener onClickListener;

    private final LayoutInflater inflater;
    private final List<Topic> topics;
    private final List<Topic> topicsFull;
    private Context context;

    public TopicAdapter(List<Topic> states, Context context, OnTopicClickListener onClickListener) {
        this.onClickListener = onClickListener;
        this.topics = new ArrayList<>(states);
        this.topicsFull = new ArrayList<>(states);
        this.inflater = LayoutInflater.from(context);
        this.context = context;
    }

    @NonNull
    @Override
    public TopicAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.topic_element, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TopicAdapter.ViewHolder holder, int position) {
        Topic topic = topics.get(position);
        holder.titleView.setText(topic.getTittle());
        holder.contentView.setText(topic.getContent());
        holder.btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.onTopicClick(topic, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return topics.size();
    }

    public void filter(String text) {
        topics.clear();

        if (text == null || text.trim().isEmpty()) {
            topics.addAll(topicsFull);
        } else {
            String lowerText = text.toLowerCase();
            for (Topic topic : topicsFull) {
                if (topic.getTittle().toLowerCase().contains(lowerText) ||
                        topic.getContent().toLowerCase().contains(lowerText)) {
                    topics.add(topic);
                }
            }
        }

        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        final TextView titleView, contentView;
        Button btn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleView = itemView.findViewById(R.id.title_topic);
            contentView = itemView.findViewById(R.id.content_topic);
            btn = itemView.findViewById(R.id.view_topic_btn);
        }
    }
}
