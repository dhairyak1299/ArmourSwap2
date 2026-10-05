package com.dwm.autoarmor;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

public class AutoArmorHandler {
    private final Minecraft mc = Minecraft.getMinecraft();
    private final KeyBinding toggleKey = new KeyBinding("AutoArmor Toggle", Keyboard.KEY_G, "DWM AutoArmor");
    private final KeyBinding guiKey = new KeyBinding("AutoArmor Settings", Keyboard.KEY_H, "DWM AutoArmor");

    private boolean enabled = false;
    private boolean inventoryOpen = false;
    private long nextAction = 0L;

    // Settings exposed through the GUI.
    public boolean autoOpen = true;
    public boolean closeWhenDone = true;
    public boolean pauseWhileMoving = false;
    public boolean pauseWhileUsing = true;
    public boolean considerEnchants = true;
    public int minDurability = 8;
    public int minScoreGain = 1;
    public int delayMs = 120;

    public AutoArmorHandler() {
        ClientRegistry.registerKeyBinding(toggleKey);
        ClientRegistry.registerKeyBinding(guiKey);
    }

    @SubscribeEvent
    public void onKey(InputEvent.KeyInputEvent event) {
        while (toggleKey.isPressed()) {
            enabled = !enabled;
            if (!enabled && inventoryOpen && closeWhenDone) closeInventory();
        }
        while (guiKey.isPressed()) {
            if (mc.thePlayer != null) mc.displayGuiScreen(new AutoArmorGui(this));
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !enabled || mc.thePlayer == null || mc.theWorld == null) return;
        if (mc.currentScreen != null && !(mc.currentScreen instanceof net.minecraft.client.gui.inventory.GuiInventory)) return;
        if (System.currentTimeMillis() < nextAction) return;
        if (pauseWhileMoving && (mc.thePlayer.motionX * mc.thePlayer.motionX + mc.thePlayer.motionZ * mc.thePlayer.motionZ) > 0.0025) return;
        if (pauseWhileUsing && mc.thePlayer.isUsingItem()) return;

        if (!inventoryOpen) {
            if (!autoOpen) return;
            mc.displayGuiScreen(new net.minecraft.client.gui.inventory.GuiInventory(mc.thePlayer));
            inventoryOpen = true;
            nextAction = System.currentTimeMillis() + 250L;
            return;
        }

        if (equipBestArmor()) {
            nextAction = System.currentTimeMillis() + delayMs;
        } else if (closeWhenDone) {
            closeInventory();
        }
    }

    private boolean equipBestArmor() {
        EntityPlayer p = mc.thePlayer;
        int bestInvSlot = -1;
        int bestArmorType = -1;
        double bestGain = minScoreGain;

        for (int inv = 9; inv < 45; inv++) {
            ItemStack stack = getStack(inv);
            if (stack == null || !(stack.getItem() instanceof ItemArmor)) continue;
            ItemArmor armor = (ItemArmor) stack.getItem();
            int type = armor.armorType;
            if (type < 0 || type > 3) continue;
            if (durabilityPercent(stack) < minDurability) continue;

            double candidate = score(stack);
            ItemStack equipped = p.inventory.armorInventory[type];
            double current = equipped == null ? 0.0 : score(equipped);
            double gain = candidate - current;
            if (gain > bestGain) {
                bestGain = gain;
                bestInvSlot = inv;
                bestArmorType = type;
            }
        }

        if (bestInvSlot == -1) return false;
        swapArmor(bestInvSlot, bestArmorType);
        return true;
    }

    private void swapArmor(int inventorySlot, int armorType) {
        // Container slot IDs for the player's inventory armor slots:
        // helmet=5, chest=6, legs=7, boots=8.
        int armorContainerSlot = 5 + (3 - armorType);
        Container c = mc.thePlayer.inventoryContainer;
        int window = c.windowId;
        mc.playerController.windowClick(window, inventorySlot, 0, 0, mc.thePlayer);
        mc.playerController.windowClick(window, armorContainerSlot, 0, 0, mc.thePlayer);
        mc.playerController.windowClick(window, inventorySlot, 0, 0, mc.thePlayer);
    }

    private ItemStack getStack(int slot) {
        if (slot >= 36 && slot <= 44) return mc.thePlayer.inventory.getStackInSlot(slot - 36);
        if (slot >= 9 && slot <= 35) return mc.thePlayer.inventory.getStackInSlot(slot);
        return null;
    }

    private double score(ItemStack stack) {
        if (stack == null || !(stack.getItem() instanceof ItemArmor)) return 0;
        ItemArmor a = (ItemArmor) stack.getItem();
        double value = a.damageReduceAmount * 10.0;
        if (considerEnchants) {
            value += EnchantmentHelperCompat.level(stack, Enchantment.protection.effectId) * 3.0;
            value += EnchantmentHelperCompat.level(stack, Enchantment.unbreaking.effectId) * 0.75;
        }
        value += durabilityPercent(stack) * 0.05;
        return value;
    }

    private int durabilityPercent(ItemStack stack) {
        if (stack == null || !stack.isItemDamaged()) return 100;
        int max = stack.getMaxDamage();
        if (max <= 0) return 100;
        return Math.max(0, (int) (((double) (max - stack.getItemDamage()) / max) * 100.0));
    }

    private void closeInventory() {
        inventoryOpen = false;
        if (mc.currentScreen != null) mc.displayGuiScreen(null);
    }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean value) { enabled = value; }

    private static final class EnchantmentHelperCompat {
        static int level(ItemStack stack, int id) {
            if (stack == null) return 0;
            return net.minecraft.enchantment.EnchantmentHelper.getEnchantmentLevel(id, stack);
        }
    }
}
