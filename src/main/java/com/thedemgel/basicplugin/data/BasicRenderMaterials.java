
package com.thedemgel.basicplugin.data;

import org.spout.api.FileSystem;
import org.spout.api.Spout;
import org.spout.api.render.RenderMaterial;


public final class BasicRenderMaterials {
	private static final FileSystem fileSystem = Spout.getFilesystem();
	public static final RenderMaterial ARMOR_MATERIAL;
	
	static {
		ARMOR_MATERIAL = (RenderMaterial) fileSystem.getResource("material://BasicPlugin/gui/smt/ArmorGUIMaterial.smt");
	}
	
	private BasicRenderMaterials() {
	}
}
