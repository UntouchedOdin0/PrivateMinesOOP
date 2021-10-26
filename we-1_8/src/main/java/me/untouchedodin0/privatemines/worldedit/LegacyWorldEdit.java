package me.untouchedodin0.privatemines.worldedit;

import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.session.SessionManager;

public class LegacyWorldEdit {

    SessionManager sessionManager = WorldEdit.getInstance().getSessionManager();
    LocalSession localSession;
    Utils utils = new Utils();

}
