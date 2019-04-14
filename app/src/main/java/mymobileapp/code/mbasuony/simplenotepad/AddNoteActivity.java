package mymobileapp.code.mbasuony.simplenotepad;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.lang.ref.WeakReference;

import mymobileapp.code.mbasuony.simplenotepad.notedb.NoteDatabase;
import mymobileapp.code.mbasuony.simplenotepad.notedb.model.Note;
import mymobileapp.code.mbasuony.simplenotepad.util.Constants;

public class AddNoteActivity extends AppCompatActivity
{

    private TextInputEditText et_title,et_content;
    private NoteDatabase noteDatabase;
    private Note note;
    private boolean update=false;
    private   Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        //---------Init---------------//
        et_title = findViewById(R.id.et_title);
        et_content = findViewById(R.id.et_content);
        Button button = findViewById(R.id.but_save);
         toolbar = (Toolbar) findViewById(R.id.toolbar_addnoe);
          setSupportActionBar(toolbar);
          getSupportActionBar().setTitle("New Note");

        noteDatabase = NoteDatabase.getInstance(AddNoteActivity.this);



        if ( (note = (Note) getIntent().getSerializableExtra("note"))!=null )
        {
            getSupportActionBar().setTitle("Update Note");
            update = true;
            button.setText("Update");
            et_title.setText(note.getTitle());
            et_content.setText(note.getContent());
        }

        //Button Save
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (update)
                {
                    // Storing Data in Table
                    note.setContent(et_content.getText().toString());
                    note.setTitle(et_title.getText().toString());

                    noteDatabase.getNoteDao().updateNote(note); // Update Data Base
                    setResult(note,Constants.UPDATE_ITEM); //Return Data to MainActivity
                }else
                    {
                    note = new Note(et_content.getText().toString(), et_title.getText().toString());
                    new InsertTask(AddNoteActivity.this,note).execute();
                }
            }
        });
    }



    private void setResult(Note note, int flag)
    {
        Intent intent=new Intent();
               intent.putExtra("note",note);
               setResult(flag,intent);

        finish();
    }

    private static class InsertTask extends AsyncTask<Void,Void,Boolean>
    {

        private WeakReference<AddNoteActivity> activityReference;
        private Note note;

        // only retain a weak reference to the activity
        InsertTask(AddNoteActivity context, Note note)
        {
            activityReference = new WeakReference<>(context);
            this.note = note;
        }

        // doInBackground methods runs on a worker thread
        @Override
        protected Boolean doInBackground(Void... objs)
        {
            // retrieve auto incremented note id
            long j = activityReference.get().noteDatabase.getNoteDao().insertNote(note);
            note.setNote_id(j);
            Log.e("ID ", "doInBackground: "+j );
            return true;
        }

        // onPostExecute runs on main thread
        @Override
        protected void onPostExecute(Boolean bool)
        {
            if (bool) //is Successful Insert into Database
            {
                activityReference.get().setResult(note, Constants.ADD_ITEM); // Return Data to MainActivity
                activityReference.get().finish();
            }
        }
    }
}
