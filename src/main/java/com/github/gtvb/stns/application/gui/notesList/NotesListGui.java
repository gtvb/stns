package com.github.gtvb.stns.application.gui.notesList;

import java.io.IOException;
import java.util.ArrayList;

import com.github.gtvb.stns.domain.model.Note;
import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

public class NotesListGui {
    private Screen screen;
    private WindowBasedTextGUI gui;
    private NotesListWindow window;

    public NotesListGui(ArrayList<Note> notes) {
        Terminal term;
        try {
            term = new DefaultTerminalFactory().createTerminal();
            this.screen = new TerminalScreen(term);
            this.gui = new MultiWindowTextGUI(screen);
            this.window = new NotesListWindow(notes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void init() {
        try {
            this.screen.startScreen();
            this.gui.addWindow(this.window);
            this.window.waitUntilClosed();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void kill() {
        try {
            this.screen.stopScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
