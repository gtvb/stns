package com.github.gtvb.stns.application.gui.notesList;

import java.util.ArrayList;
import java.util.List;

import com.github.gtvb.stns.domain.model.Note;
import com.github.gtvb.stns.domain.model.NoteAndTag;
import com.github.gtvb.stns.domain.model.Tag;
import com.github.gtvb.stns.domain.model.User;
import com.github.gtvb.stns.infra.repository.NoteRepository;
import com.github.gtvb.stns.infra.repository.TagRepository;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.Borders;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.Direction;
import com.googlecode.lanterna.gui2.GridLayout;
import com.googlecode.lanterna.gui2.Label;
import com.googlecode.lanterna.gui2.LinearLayout;
import com.googlecode.lanterna.gui2.TextBox.Style;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.TextBox;
import com.googlecode.lanterna.gui2.Window;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;

public class NotesListWindow extends BasicWindow {
    private List<NoteAndTag> notesWithTags = new ArrayList<>();
    private static final int maxItemsPerWindow = 4;
    private int currentInitialIndex = 0;
    private Panel notesPanel;

    private NoteRepository noteRepository; 

    public NotesListWindow(User user, NoteRepository noteRepository, TagRepository tagRepository) {
        this.noteRepository = noteRepository;

        ArrayList<Note> userNotes = noteRepository.getNotesByUserId(user.getUuid());
        for(Note n : userNotes) {
            ArrayList<Tag> noteTags = tagRepository.getTagsInsideNote(n.getUuid());
            this.notesWithTags.add(new NoteAndTag(n, noteTags));
        }

        Panel mainPanel = new Panel();
        mainPanel.setLayoutManager(new LinearLayout(Direction.VERTICAL));

        notesPanel = new Panel();
        int notesQuantity = notesWithTags.size();
        if(notesQuantity > 0) {
            if(notesQuantity > 4) {
                mainPanel.addComponent(new Button("Next Page", new NextPageButtonAction()));
                mainPanel.addComponent(new Button("Previous Page", new PrevPageButtonAction()));
            }
            notesPanel.setLayoutManager(new GridLayout(2).setHorizontalSpacing(5));
            notesPanel.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Fill));
            
            refreshNotesPanel();
        } else {
            notesPanel.setLayoutManager(new LinearLayout(Direction.VERTICAL));
            notesPanel.addComponent(new Label("There are no notes for this user"), null);
        }

        mainPanel.addComponent(notesPanel);
        mainPanel.addComponent(new Button("Close", new CloseButtonAction(this)));
        setComponent(mainPanel);
    }

    private void refreshNotesPanel() {
        try {
        notesPanel.removeAllComponents(); 

        List<NoteAndTag> notesSublist;
        if(currentInitialIndex + maxItemsPerWindow >= notesWithTags.size()) {
            notesSublist = notesWithTags.subList(currentInitialIndex, notesWithTags.size());
        } else {
            notesSublist = notesWithTags.subList(currentInitialIndex, currentInitialIndex + maxItemsPerWindow);
        }

        for (NoteAndTag note : notesSublist) {
            Panel notePanel = createNotePanel(note);
            notesPanel.addComponent(notePanel);
        }

        invalidate();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private Panel createNotePanel(NoteAndTag noteWithTag) {
        Note note = noteWithTag.getNote();

        Panel panel = new Panel();
        panel.setLayoutManager(new LinearLayout(Direction.VERTICAL));

        panel.addComponent(new Label(note.getTitle()));
        panel.addComponent(new Label(note.getCreatedAt()));

        Panel tagsPanel = new Panel();
        tagsPanel.withBorder(Borders.singleLine("Tags"));
        tagsPanel.setLayoutManager(new GridLayout(4));

        for(Tag t : noteWithTag.getTags()) {
            Label tagLabel = new Label("# " + t.getName());
            tagsPanel.addComponent(tagLabel);
        }

        panel.addComponent(tagsPanel);

        TextBox contentsTextBox = new TextBox(note.getContents(), Style.MULTI_LINE)
                .setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Fill));
            contentsTextBox.setPreferredSize(new TerminalSize(50, 8));
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
            noteRepository.deleteNoteById(note.getNote().getUuid()); 
            notesWithTags.remove(note);
            refreshNotesPanel();
        }
    }

    private class NextPageButtonAction implements Runnable {
        @Override
        public void run() {
            if(currentInitialIndex + maxItemsPerWindow < notesWithTags.size()) {
                currentInitialIndex += maxItemsPerWindow;
                refreshNotesPanel();
            }
        }
    }

    private class PrevPageButtonAction implements Runnable {
        @Override
        public void run() {
            if(currentInitialIndex - maxItemsPerWindow >= 0) {
                currentInitialIndex -= maxItemsPerWindow;
                refreshNotesPanel();
            }
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

            noteRepository.editNoteContents(note.getNote().getUuid(), newContents);
            MessageDialog.showMessageDialog(getTextGUI(), "Note Saved", "Contents saved successfully!");

            refreshNotesPanel();
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
