package com.cc17.zenith;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class DocumentAdapter extends RecyclerView.Adapter<DocumentAdapter.DocumentViewHolder> {

    private List<Document> documents;
    private List<Document> documentsFiltered;
    private OnDocumentClickListener onDocumentClickListener;

    public DocumentAdapter(List<Document> documents, OnDocumentClickListener onDocumentClickListener) {
        this.documents = documents;
        this.documentsFiltered = new ArrayList<>(documents);
        this.onDocumentClickListener = onDocumentClickListener;
    }

    @NonNull
    @Override
    public DocumentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.document_item, parent, false);
        return new DocumentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DocumentViewHolder holder, int position) {
        Document document = documentsFiltered.get(position);
        holder.thumbnail.setImageResource(document.getThumbnail());
        holder.title.setText(document.getTitle());
        holder.date.setText(document.getDate());
        holder.documentItemLayout.setOnClickListener(v -> onDocumentClickListener.onDocumentClick(document));
    }

    @Override
    public int getItemCount() {
        return documentsFiltered.size();
    }

    public void filter(String query) {
        documentsFiltered.clear();
        if (query.isEmpty()) {
            documentsFiltered.addAll(documents);
        } else {
            for (Document document : documents) {
                if (document.getTitle().toLowerCase().contains(query.toLowerCase())) {
                    documentsFiltered.add(document);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void sort(java.util.Comparator<Document> comparator) {
        documentsFiltered.sort(comparator);
        notifyDataSetChanged();
    }

    static class DocumentViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnail;
        TextView title;
        TextView date;
        View documentItemLayout;

        public DocumentViewHolder(@NonNull View itemView) {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.document_thumbnail);
            title = itemView.findViewById(R.id.document_title);
            date = itemView.findViewById(R.id.document_date);
            documentItemLayout = itemView.findViewById(R.id.document_item_layout);
        }
    }

    public interface OnDocumentClickListener {
        void onDocumentClick(Document document);
    }
}
