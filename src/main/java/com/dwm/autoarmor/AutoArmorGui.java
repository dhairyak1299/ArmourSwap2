package com.dwm.autoarmor;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public class AutoArmorGui extends GuiScreen {
    private final AutoArmorHandler handler;
    private GuiButton enabled, autoOpen, closeDone, pauseMove, pauseUse, enchants;

    public AutoArmorGui(AutoArmorHandler handler) { this.handler = handler; }

    @Override
    public void initGui() {
        int x = width / 2 - 100;
        int y = height / 2 - 90;
        buttonList.clear();
        enabled = add(new GuiButton(1, x, y, 200, 20, "AutoArmor: " + on(handler.isEnabled())));
        autoOpen = add(new GuiButton(2, x, y += 25, 200, 20, "Open inventory: " + on(handler.autoOpen)));
        closeDone = add(new GuiButton(3, x, y += 25, 200, 20, "Close when done: " + on(handler.closeWhenDone)));
        pauseMove = add(new GuiButton(4, x, y += 25, 200, 20, "Pause while moving: " + on(handler.pauseWhileMoving)));
        pauseUse = add(new GuiButton(5, x, y += 25, 200, 20, "Pause while using: " + on(handler.pauseWhileUsing)));
        enchants = add(new GuiButton(6, x, y += 25, 200, 20, "Score enchantments: " + on(handler.considerEnchants)));
        add(new GuiButton(7, x, y += 25, 200, 20, "Done"));
    }

    private GuiButton add(GuiButton b) { buttonList.add(b); return b; }
    private String on(boolean b) { return b ? "ON" : "OFF"; }

    @Override
    protected void actionPerformed(GuiButton button) {
        switch (button.id) {
            case 1: handler.setEnabled(!handler.isEnabled()); break;
            case 2: handler.autoOpen = !handler.autoOpen; break;
            case 3: handler.closeWhenDone = !handler.closeWhenDone; break;
            case 4: handler.pauseWhileMoving = !handler.pauseWhileMoving; break;
            case 5: handler.pauseWhileUsing = !handler.pauseWhileUsing; break;
            case 6: handler.considerEnchants = !handler.considerEnchants; break;
            case 7: mc.displayGuiScreen(null); return;
        }
        initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        drawCenteredString(fontRendererObj, "DWM AutoArmor", width / 2, height / 2 - 120, 0xFFFFFF);
        drawCenteredString(fontRendererObj, "G = toggle  |  H = settings", width / 2, height / 2 + 100, 0xAAAAAA);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
