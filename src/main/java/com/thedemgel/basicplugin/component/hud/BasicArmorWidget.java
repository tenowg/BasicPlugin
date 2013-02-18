package com.thedemgel.basicplugin.component.hud;

import com.thedemgel.basicplugin.data.BasicRenderMaterials;
import java.awt.Color;
import org.spout.api.gui.Widget;
import org.spout.api.gui.component.RenderPartsHolderComponent;
import org.spout.api.gui.render.RenderPart;
import org.spout.api.gui.render.RenderPartPack;
import org.spout.api.math.Rectangle;
import org.spout.vanilla.component.player.HUDComponent;
import org.spout.vanilla.component.player.hud.ArmorWidget;

public class BasicArmorWidget extends ArmorWidget {
	private RenderPartPack expPack = new RenderPartPack(BasicRenderMaterials.ARMOR_MATERIAL);

	@Override
	public void init(Widget armor, HUDComponent hud) {
		super.init(armor, hud);
		final RenderPartsHolderComponent expRect = widget.add(RenderPartsHolderComponent.class);
		expRect.add(expPack);
		
		final RenderPart expBgRect = new RenderPart();
		expBgRect.setColor(Color.WHITE);
		expBgRect.setSprite(new Rectangle(START_X, 0.0f, 0.25f * SCALE, .25f));
		expBgRect.setSource(new Rectangle(0, 600f / 1024f, 300f / 1024f, 300f / 1024));
		expPack.add(expBgRect);

		final RenderPart expBarRect = new RenderPart();
		expBarRect.setColor(Color.WHITE);
		expPack.add(expBarRect);

		final RenderPart rect = expPack.get(1);
		rect.setSprite(new Rectangle(START_X, 0.0f, 0.25f * SCALE, .25f * 0f));
		rect.setSource(new Rectangle(300f / 1024f, 0f, 300f / 1024f, 0f));

		attach();
	}

	@Override
	public void update() {
		float percent = .8f;
		final RenderPart rect = expPack.get(1);
		rect.setColor(Color.WHITE);
		rect.setSprite(new Rectangle(START_X, 0f, 0.25f * SCALE, .25f * percent));
		rect.setSource(new Rectangle(300f / 1024f, (300f / 1024f * (1f - percent)) + 0f / 1024f, 300f / 1024f, 300f / 1024f * percent));
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
