package com.github.gtvb.stns.application.gui.notesList;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

import com.github.gtvb.stns.domain.model.Note;
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
    private ArrayList<Note> notes;
    private Panel notesPanel;

    public NotesListWindow(ArrayList<Note> notes) {
        this.notes = notes;

        Panel mainPanel = new Panel();
        mainPanel.setLayoutManager(new LinearLayout(Direction.VERTICAL));

        notesPanel = new Panel();

        if(notes.size() > 0) {
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

        for (Note note : notes) {
            Panel notePanel = createNotePanel(note);
            notesPanel.addComponent(notePanel);
        }

        invalidate();
    }

    private Panel createNotePanel(Note note) {
        Panel panel = new Panel();
        panel.setLayoutManager(new LinearLayout(Direction.VERTICAL));

        panel.addComponent(new Label(note.getCreatedAt()));
        panel.addComponent(new Label(note.getTitle()));

        TextBox contentsTextBox = new TextBox(note.getContents())
                .setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Fill));
        panel.addComponent(contentsTextBox);

        Button removeButton = new Button("Remove", new RemoveButtonAction(note));
        panel.addComponent(removeButton);

        Button saveButton = new Button("Save", new SaveButtonAction(note, contentsTextBox));
        panel.addComponent(saveButton);

        return panel;
    }

    private class RemoveButtonAction implements Runnable {
        private Note note;

        public RemoveButtonAction(Note note) {
            this.note = note;
        }

        @Override
        public void run() {
            CompletableFuture.runAsync(new Runnable() {
                @Override
                public void run() {
                    notes.remove(note);
                    refreshNotesPanel();
                }
            });
        }
    }

    private class SaveButtonAction implements Runnable {
        private Note note;
        private TextBox contentsTextBox;

        public SaveButtonAction(Note note, TextBox contentsTextBox) {
            this.note = note;
            this.contentsTextBox = contentsTextBox;
        }

        @Override
        public void run() {
            String newContents = contentsTextBox.getText();
            note.setContents(newContents);

            CompletableFuture.runAsync(new Runnable() {
                @Override
                public void run() {
                    // Perform any additional saving operations here
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
