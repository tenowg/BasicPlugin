
package com.thedemgel.basicplugin.data;

import org.spout.api.Spout;
import org.spout.api.render.RenderMaterial;
import org.spout.api.resource.FileSystem;


public final class BasicRenderMaterials {
	private static final FileSystem fileSystem = Spout.getFileSystem();
	public static final RenderMaterial ARMOR_MATERIAL;
	
	static {
		ARMOR_MATERIAL = (RenderMaterial) fileSystem.getResource("material://BasicPlugin/gui/smt/ArmorGUIMaterial.smt");
	}
	
	private BasicRenderMaterials() {
	}
}
