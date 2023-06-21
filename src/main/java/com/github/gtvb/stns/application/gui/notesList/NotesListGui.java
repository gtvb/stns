package com.github.gtvb.stns.application.gui.notesList;

import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;

import com.github.gtvb.stns.domain.model.User;
import com.github.gtvb.stns.infra.repository.NoteHasTagRepository;
import com.github.gtvb.stns.infra.repository.NoteRepository;
import com.github.gtvb.stns.infra.repository.TagRepository;
import com.github.gtvb.stns.utils.LoginUtils;
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

    public NotesListGui(Connection conn) {
        Terminal term;

        User user = LoginUtils.getLoggedUser();
        NoteRepository nr = new NoteRepository(conn);
        TagRepository tr = new TagRepository(conn);
        NoteHasTagRepository nhtr = new NoteHasTagRepository(conn);

        try {
            term = new DefaultTerminalFactory().createTerminal();
            this.screen = new TerminalScreen(term);
            this.gui = new MultiWindowTextGUI(screen);
            this.window = new NotesListWindow(user, nr, tr, nhtr);
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
