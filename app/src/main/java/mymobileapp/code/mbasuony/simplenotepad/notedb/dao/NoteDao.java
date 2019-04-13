package mymobileapp.code.mbasuony.simplenotepad.notedb.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import mymobileapp.code.mbasuony.simplenotepad.notedb.model.Note;
import mymobileapp.code.mbasuony.simplenotepad.util.Constants;

@Dao
public interface NoteDao
{
  @Query("SELECT * FROM "+ Constants.TABLE_NAME_NOTES)
  List<Note>getNotes();

  @Insert
  long insertNote(Note note);

  @Update
  void updateNote(Note repos);

  @Delete
  void deleteNote(Note note);


    @Delete
    void deleteNotes(Note... note); // Note... is varargs, here note is an array

}
