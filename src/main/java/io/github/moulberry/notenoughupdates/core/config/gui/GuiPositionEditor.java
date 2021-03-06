package io.github.moulberry.notenoughupdates.core.config.gui;

import io.github.moulberry.notenoughupdates.core.config.Position;
import io.github.moulberry.notenoughupdates.core.util.render.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;

import java.io.IOException;

public class GuiPositionEditor extends GuiScreen {

    private Position position;
    private Position originalPosition;
    private int elementWidth;
    private int elementHeight;
    private Runnable renderCallback;
    private Runnable positionChangedCallback;
    private Runnable closedCallback;
    private boolean clicked = false;
    private int grabbedX = 0;
    private int grabbedY = 0;

    private int oldMouseX = 0;
    private int oldMouseY = 0;

    public GuiPositionEditor(Position position, int elementWidth, int elementHeight,
                                    Runnable renderCallback,
                                    Runnable positionChangedCallback,
                                    Runnable closedCallback) {
        this.position = position;
        this.originalPosition = position.clone();
        this.elementWidth = elementWidth;
        this.elementHeight = elementHeight;
        this.renderCallback = renderCallback;
        this.positionChangedCallback = positionChangedCallback;
        this.closedCallback = closedCallback;
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        closedCallback.run();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());

        this.width = scaledResolution.getScaledWidth();
        this.height = scaledResolution.getScaledHeight();

        drawDefaultBackground();

        if(clicked) {
            grabbedX += position.moveX(mouseX - grabbedX, elementWidth, scaledResolution);
            grabbedY += position.moveY(mouseY - grabbedY, elementHeight, scaledResolution);
        }

        renderCallback.run();

        int x = position.getAbsX(scaledResolution);
        int y = position.getAbsY(scaledResolution);

        Gui.drawRect(x, y, x+elementWidth, y+elementHeight, 0x80404040);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        if(mouseButton == 0) {
            ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());

            int x = position.getAbsX(scaledResolution);
            int y = position.getAbsY(scaledResolution);

            if(mouseX >= x && mouseY >= y &&
                    mouseX <= x+elementWidth && mouseY <= y+elementHeight) {
                clicked = true;
                grabbedX = mouseX;
                grabbedY = mouseY;
            }
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        clicked = false;
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);

        ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
        if(clicked) {
            oldMouseX = mouseX;
            oldMouseY = mouseY;

            grabbedX += position.moveX(mouseX - grabbedX, elementWidth, scaledResolution);
            grabbedY += position.moveY(mouseY - grabbedY, elementHeight, scaledResolution);
            positionChangedCallback.run();
        }
    }
}
