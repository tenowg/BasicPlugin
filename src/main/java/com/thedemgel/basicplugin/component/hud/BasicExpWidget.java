package com.thedemgel.basicplugin.component.hud;

import com.thedemgel.basicplugin.data.BasicRenderMaterials;
import java.awt.Color;
import org.spout.api.gui.Widget;
import org.spout.api.gui.component.RenderPartsHolderComponent;
import org.spout.api.gui.render.RenderPart;
import org.spout.api.math.Rectangle;
import org.spout.vanilla.plugin.component.player.HUDComponent;
import org.spout.vanilla.plugin.component.player.hud.ExpBarWidget;

public class BasicExpWidget extends ExpBarWidget {

	@Override
	public void init(Widget armor, HUDComponent hud) {
		super.init(armor, hud);
		// Experience bar
		final RenderPartsHolderComponent expRect = widget.add(RenderPartsHolderComponent.class);
		final RenderPart expBgRect = new RenderPart();
		expBgRect.setRenderMaterial(BasicRenderMaterials.ARMOR_MATERIAL);
		expBgRect.setColor(Color.WHITE);
		expBgRect.setSprite(new Rectangle(START_X + .2f, 0.0f, 0.25f * SCALE, .25f));
		expBgRect.setSource(new Rectangle(0, 600f / 1024f, 300f / 1024f, 300f / 1024));
		expRect.add(expBgRect);

		final RenderPart expBarRect = new RenderPart();
		expBarRect.setRenderMaterial(BasicRenderMaterials.ARMOR_MATERIAL);
		expBarRect.setColor(Color.WHITE);
		expRect.add(expBarRect);

		final RenderPart rect = widget.get(RenderPartsHolderComponent.class).get(1);
		rect.setSprite(new Rectangle(START_X + .2f, 0.0f, 0.25f * SCALE, .25f * 0f));
		rect.setSource(new Rectangle(0f / 1024f, 0f, 300f / 1024f, 0f));

		attach();
	}

	@Override
	public void update() {
		float percent = .4f;
		final RenderPart rect = widget.get(RenderPartsHolderComponent.class).get(1);
		Color color = new Color(.45f, .83f, .80f, 1f);
		rect.setColor(Color.WHITE);
		rect.setSprite(new Rectangle(START_X +.2f, 0f, 0.25f * SCALE, .25f * percent));
		rect.setSource(new Rectangle(0f / 1024f, (300f / 1024f * (1f - percent)) + 0f / 1024f, 300f / 1024f, 300f / 1024f * percent));
		widget.update();
	}

	@Override
	public void animate() {
	}

	@Override
	public void show() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void hide() {
		throw new UnsupportedOperationException("Not supported yet.");
	}
}
