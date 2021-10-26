package me.untouchedodin0.privatemines.worldedit;

import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.session.SessionManager;

//import com.sk89q.worldedit.regions.Region;

public class ModernWorldEdit {

    SessionManager sessionManager = WorldEdit.getInstance().getSessionManager();
    LocalSession localSession;
    Utils utils;
}
