package org.terasology.rendering.gui.widgets;

import org.lwjgl.opengl.GL11;
import org.terasology.asset.Assets;
import org.terasology.entitySystem.EntityRef;
import org.terasology.logic.inventory.ItemComponent;
import org.terasology.model.inventory.Icon;
import org.terasology.rendering.assets.Texture;
import org.terasology.rendering.gui.framework.UIDisplayContainer;
import org.terasology.world.block.Block;
import org.terasology.world.block.entity.BlockItemComponent;
import org.terasology.world.block.family.BlockFamily;

import javax.vecmath.Vector2f;

/**
 * Displays an item as an icon with an optional stack count.
 */
public class UIItemIcon extends UIDisplayContainer {
    private static final Vector2f ITEM_COUNT_POSITION = new Vector2f(26f, 5f);

    private EntityRef item = EntityRef.NULL;

    private boolean displayingItemCount = true;

    //sub elements
    private final UILabel itemCount;

    //rendering
    private Texture terrainTex;


    public UIItemIcon() {
        terrainTex = Assets.getTexture("engine:terrain");

        itemCount = new UILabel();
        itemCount.setVisible(false);
        itemCount.setPosition(ITEM_COUNT_POSITION);

        addDisplayElement(itemCount);
        setVisible(false);
    }

    public void setItem(EntityRef item) {
        this.item = item;

        ItemComponent itemComponent = item.getComponent(ItemComponent.class);
        setVisible(itemComponent != null);
        updateCountLabel(itemComponent);
    }


    public boolean isDisplayingItemCount() {
        return displayingItemCount;
    }

    public void setDisplayingItemCount(boolean enable) {
        displayingItemCount = enable;
        updateCountLabel(item.getComponent(ItemComponent.class));

    }

    private void updateCountLabel(ItemComponent itemComponent) {
        if (itemComponent != null) {
            if (itemComponent.stackCount > 1 && displayingItemCount) {
                itemCount.setVisible(true);
                itemCount.setText(Integer.toString(itemComponent.stackCount));
            } else {
                itemCount.setVisible(false);
            }
        }
    }

    public EntityRef getItem() {
        return item;
    }

    @Override
    public void layout() {

    }

    @Override
    public void update() {
        setVisible(item.exists());
    }

    @Override
    public void render() {

        ItemComponent itemComponent = item.getComponent(ItemComponent.class);
        if (itemComponent == null) {
            return;
        }

        //render icon
        if (itemComponent.icon.isEmpty()) {
            BlockItemComponent blockItem = item.getComponent(BlockItemComponent.class);
            if (blockItem != null) {
                renderBlockIcon(blockItem.blockFamily);
            }
        } else {
            Icon icon = Icon.get(itemComponent.icon);
            if (icon != null) {
                renderIcon(icon);
            }
        }

        super.render();
    }

    private void renderIcon(Icon icon) {
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glPushMatrix();
        GL11.glTranslatef(20f, 20f, 0f);
        icon.render();
        GL11.glPopMatrix();
        GL11.glDisable(GL11.GL_DEPTH_TEST);
    }

    private void renderBlockIcon(BlockFamily blockFamily) {
        if (blockFamily == null) {
            renderIcon(Icon.get("questionmark"));
            return;
        }

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glPushMatrix();

        GL11.glTranslatef(20f, 20f, 0f);

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glPushMatrix();
        GL11.glTranslatef(4f, 0f, 0f);
        GL11.glScalef(20f, 20f, 20f);
        GL11.glRotatef(170f, 1f, 0f, 0f);
        GL11.glRotatef(-16f, 0f, 1f, 0f);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, terrainTex.getId());

        Block block = blockFamily.getArchetypeBlock();
        block.renderWithLightValue(1.0f);

        GL11.glPopMatrix();

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glPopMatrix();
        GL11.glDisable(GL11.GL_DEPTH_TEST);
    }

}