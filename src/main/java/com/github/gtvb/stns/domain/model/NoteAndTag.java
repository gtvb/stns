package com.github.gtvb.stns.domain.model;

import java.util.ArrayList;

public class NoteAndTag {
   private Note note;
   private ArrayList<Tag> tags;

   public NoteAndTag(Note note, ArrayList<Tag> tags) {
        this.note = note;
        this.tags = tags;
   }

   public Note getNote() {
       return note;
   }

   public ArrayList<Tag> getTags() {
       return tags;
   }
}
