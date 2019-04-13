package mymobileapp.code.mbasuony.simplenotepad.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import mymobileapp.code.mbasuony.simplenotepad.R;
import mymobileapp.code.mbasuony.simplenotepad.notedb.model.Note;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteHolder>
{

    private List<Note> list;
    private Context context;
    private LayoutInflater layoutInflater;
    private OnNoteItemClick onNoteItemClick;

    public NotesAdapter(List<Note> list,Context context)
    {
        layoutInflater = LayoutInflater.from(context);
        this.list = list;
        this.context = context;
        this.onNoteItemClick = (OnNoteItemClick) context;
    }

    @NonNull
    @Override
    public NoteHolder onCreateViewHolder(@NonNull ViewGroup parent, int i)
    {
        View view = layoutInflater.inflate(R.layout.note_list_item,parent,false);
        return new NoteHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteHolder holder, int position)
    {
        Log.e("bind", "onBindViewHolder: "+ list.get(position));
        holder.textViewTitle.setText(list.get(position).getTitle());
        holder.textViewContent.setText(list.get(position).getContent());
    }

    @Override
    public int getItemCount()
    {
        return list.size();
    }


    public class NoteHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView textViewContent;
        TextView textViewTitle;
        public NoteHolder(View itemView)
        {
            super(itemView);
            itemView.setOnClickListener(this);
            textViewContent = itemView.findViewById(R.id.item_text);
            textViewTitle = itemView.findViewById(R.id.tv_title);
        }

        @Override
        public void onClick(View view)
        {
            onNoteItemClick.onNoteClick(getAdapterPosition());
        }
    }


    public interface OnNoteItemClick
    {
        void onNoteClick(int pos);
    }
}
