package mymobileapp.code.mbasuony.simplenotepad.notedb;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import mymobileapp.code.mbasuony.simplenotepad.notedb.dao.NoteDao;
import mymobileapp.code.mbasuony.simplenotepad.notedb.model.Note;
import mymobileapp.code.mbasuony.simplenotepad.util.Constants;
import mymobileapp.code.mbasuony.simplenotepad.util.DateRoomConverter;

@Database(entities = {Note.class},version = 1)
@TypeConverters({DateRoomConverter.class})
public abstract class NoteDatabase extends RoomDatabase
{

    public abstract NoteDao getNoteDao();
    public static NoteDatabase noteDatabase;

    // synchronized is use to avoid concurrent access in multithred environment
    public static  /*synchronized*/ NoteDatabase getInstance(Context context)
    {
       if (noteDatabase==null)
       {
         noteDatabase=buildDatabaseInstance(context);
       }

       return noteDatabase;
    }

    private static NoteDatabase buildDatabaseInstance(Context context)
    {

        return Room.databaseBuilder(context,NoteDatabase.class, Constants.DB_NAME)
                      .allowMainThreadQueries() // using this is not recommended on real apps. This is just for demonstration instead use AsyncTask (or handler, rxjava).
                      .build();
    }

    public void cleanUp()
    {
      noteDatabase=null;
    }

}
