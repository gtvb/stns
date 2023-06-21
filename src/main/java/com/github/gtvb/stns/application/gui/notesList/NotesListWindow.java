package com.github.gtvb.stns.application.gui.notesList;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

import com.github.gtvb.stns.domain.model.Note;
import com.github.gtvb.stns.domain.model.NoteAndTag;
import com.github.gtvb.stns.domain.model.Tag;
import com.github.gtvb.stns.domain.model.User;
import com.github.gtvb.stns.infra.repository.NoteHasTagRepository;
import com.github.gtvb.stns.infra.repository.NoteRepository;
import com.github.gtvb.stns.infra.repository.TagRepository;
import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.Direction;
import com.googlecode.lanterna.gui2.GridLayout;
import com.googlecode.lanterna.gui2.Label;
import com.googlecode.lanterna.gui2.LinearLayout;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.TextBox;
import com.googlecode.lanterna.gui2.Window;
import com.googlecode.lanterna.gui2.LinearLayout.Alignment;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;

public class NotesListWindow extends BasicWindow {
    private ArrayList<NoteAndTag> notesWithTags = new ArrayList<>();
    private Panel notesPanel;

    private NoteRepository noteRepository; 
    private TagRepository tagRepository; 
    private NoteHasTagRepository noteHasTagRepository; 

    public NotesListWindow(User user, NoteRepository noteRepository, TagRepository tagRepository, NoteHasTagRepository noteHasTagRepository) {
        this.noteRepository = noteRepository;
        this.tagRepository = tagRepository;
        this.noteHasTagRepository = noteHasTagRepository;

        ArrayList<Note> userNotes = noteRepository.getNotesByUserId(user.getUuid());
        for(Note n : userNotes) {
            ArrayList<Tag> noteTags = tagRepository.getTagsInsideNote(n.getUuid());
            this.notesWithTags.add(new NoteAndTag(n, noteTags));
        }

        Panel mainPanel = new Panel();
        mainPanel.setLayoutManager(new LinearLayout(Direction.VERTICAL));

        notesPanel = new Panel();

        if(notesWithTags.size() > 0) {
            notesPanel.setLayoutManager(new GridLayout(2).setHorizontalSpacing(5));
            refreshNotesPanel();
        } else {
            notesPanel.setLayoutManager(new LinearLayout(Direction.VERTICAL));
            notesPanel.addComponent(new Label("There are no notes for this user"), null);
            notesPanel.addComponent(new Button("Close", new CloseButtonAction(this)), LinearLayout.createLayoutData(Alignment.Center));
        }

        mainPanel.addComponent(notesPanel);
        setComponent(mainPanel);
    }

    private void refreshNotesPanel() {
        notesPanel.removeAllComponents(); // Clear the panel

        for (NoteAndTag note : notesWithTags) {
            Panel notePanel = createNotePanel(note);
            notesPanel.addComponent(notePanel);
        }

        invalidate();
    }

    private Panel createNotePanel(NoteAndTag noteWithTag) {
        Note note = noteWithTag.getNote();

        Panel panel = new Panel();
        panel.setLayoutManager(new LinearLayout(Direction.VERTICAL));

        panel.addComponent(new Label(note.getCreatedAt()));
        panel.addComponent(new Label(note.getTitle()));

        TextBox contentsTextBox = new TextBox(note.getContents())
                .setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Fill));
        panel.addComponent(contentsTextBox);

        Button removeButton = new Button("Remove", new RemoveButtonAction(noteWithTag, this.noteRepository));
        panel.addComponent(removeButton);

        Button saveButton = new Button("Save", new SaveButtonAction(noteWithTag, contentsTextBox, this.noteRepository));
        panel.addComponent(saveButton);

        return panel;
    }

    private class RemoveButtonAction implements Runnable {
        private NoteAndTag note;
        private NoteRepository noteRepository;

        public RemoveButtonAction(NoteAndTag note, NoteRepository noteRepository) {
            this.note = note;
            this.noteRepository = noteRepository;
        }

        @Override
        public void run() {
            CompletableFuture.runAsync(new Runnable() {
                @Override
                public void run() {
                    noteRepository.deleteNoteById(note.getNote().getUuid()); 
                    notesWithTags.remove(note);
                    refreshNotesPanel();
                }
            });
        }
    }

    private class SaveButtonAction implements Runnable {
        private NoteAndTag note;
        private TextBox contentsTextBox;
        private NoteRepository noteRepository;

        public SaveButtonAction(NoteAndTag note, TextBox contentsTextBox, NoteRepository noteRepository) {
            this.note = note;
            this.contentsTextBox = contentsTextBox;
            this.noteRepository = noteRepository;
        }

        @Override
        public void run() {
            final String newContents = contentsTextBox.getText();
            note.getNote().setContents(newContents);

            CompletableFuture.runAsync(new Runnable() {
                @Override
                public void run() {
                    noteRepository.editNoteContents(note.getNote().getUuid(), newContents);
                    MessageDialog.showMessageDialog(getTextGUI(), "Note Saved", "Contents saved successfully!");
                    refreshNotesPanel();
                }
            });
        }
    }

    private class CloseButtonAction implements Runnable {
        private Window window;
        
        public CloseButtonAction(Window window) {
            this.window = window;
        }

        @Override
        public void run() {
            window.close();
        }
    }
}
