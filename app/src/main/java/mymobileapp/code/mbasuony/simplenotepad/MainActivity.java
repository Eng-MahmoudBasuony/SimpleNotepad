package mymobileapp.code.mbasuony.simplenotepad;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import mymobileapp.code.mbasuony.simplenotepad.adapter.NotesAdapter;
import mymobileapp.code.mbasuony.simplenotepad.notedb.NoteDatabase;
import mymobileapp.code.mbasuony.simplenotepad.notedb.model.Note;
import mymobileapp.code.mbasuony.simplenotepad.util.Constants;

public class MainActivity extends AppCompatActivity implements NotesAdapter.OnNoteItemClick
{

    private ImageView imageViewMsg;
    private RecyclerView recyclerView;
    private NoteDatabase noteDatabase;
    private List<Note> noteList;
    private NotesAdapter notesAdapter;
    private int pos;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeVies();
        displayList();
    }

    private void initializeVies(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        imageViewMsg =findViewById(R.id.imageViewMsg);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(listener);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        noteList = new ArrayList<>(); // List

        notesAdapter = new NotesAdapter(noteList,MainActivity.this); //object From Adapter
        recyclerView.setAdapter(notesAdapter);
    }


    private void displayList()
    {
        noteDatabase = NoteDatabase.getInstance(MainActivity.this);
        new RetrieveTask(this).execute(); //Run AsyncTask
    }

    //--AsyncTask
    private static class RetrieveTask extends AsyncTask<Void,Void,List<Note>> {

        private WeakReference<MainActivity> activityReference;

        // only retain a weak reference to the activity
        RetrieveTask(MainActivity context)
        {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected List<Note> doInBackground(Void... voids)
        {
            if (activityReference.get()!=null)
                return activityReference.get().noteDatabase.getNoteDao().getNotes(); //Return All Rows Notes
            else
                return null;
        }

        @Override
        protected void onPostExecute(List<Note> notes)
        {

            if (notes!=null && notes.size()>0 )
            {
                activityReference.get().noteList.clear(); // Clear List
                activityReference.get().noteList.addAll(notes); // Add Notes into List

                activityReference.get().imageViewMsg.setVisibility(View.GONE);// hides empty Image view
                activityReference.get().notesAdapter.notifyDataSetChanged();   // Adapter
            }
        }
    }



    //--Listener for FAB
    private View.OnClickListener listener = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            Intent intent=new Intent(MainActivity.this,AddNoteActivity.class);
                   startActivityForResult(intent,Constants.REQUEST_QUDE_ACTIVITYFORRESULT);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == Constants.REQUEST_QUDE_ACTIVITYFORRESULT && resultCode > 0 )
        {
            if( resultCode == Constants.ADD_ITEM) // Add Row "Note"
            {
                noteList.add((Note) data.getSerializableExtra("note"));

            }else if( resultCode == Constants.UPDATE_ITEM) // update item "Note"
            {
                noteList.set(pos,(Note) data.getSerializableExtra("note")); // update item in RecyclerView
            }
            listVisibility();
        }
    }


    // follow Context Menu for Item
    @Override
    public void onNoteClick(final int pos)
    {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Select Options")
                .setItems(new String[]{"Delete", "Update"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        // noteList.get(pos)  return instance from Note ==> Note{note_id=13,content='typing content', title='typing title'}
                        switch (i)
                        {
                            case 0: //Delete

                                noteDatabase.getNoteDao().deleteNote(noteList.get(pos));
                                noteList.remove(pos);
                                listVisibility();
                                break;

                            case 1:  //update
                                MainActivity.this.pos = pos;

                                Intent intent=new Intent(MainActivity.this,AddNoteActivity.class);
                                        intent.putExtra("note", noteList.get(pos));
                                       startActivityForResult(intent,100);
                                break;
                        }
                    }
                }).show();

    }

    private void listVisibility()
    {
        int emptyMsgVisibility = View.GONE;
        if (noteList.size() == 0)
        { // no item to display
            if (imageViewMsg.getVisibility() == View.GONE)
                emptyMsgVisibility = View.VISIBLE;
        }
        imageViewMsg.setVisibility(emptyMsgVisibility);
        notesAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        noteDatabase.cleanUp();
        super.onDestroy();
    }
}
