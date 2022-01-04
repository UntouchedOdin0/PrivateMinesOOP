package me.untouchedodin0.privatemines.we_6.worldedit;

import com.cryptomorin.xseries.XMaterial;
import com.sk89q.worldedit.blocks.BlockType;
import org.bukkit.Material;

public class Utils {

    public BlockType bukkitToBlockType(Material material) {
        return BlockType.fromID(XMaterial.matchXMaterial(material).getId());
    }
}
