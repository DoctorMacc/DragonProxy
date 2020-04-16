package org.dragonet.proxy.network.translator.misc.inventory;

import com.github.steveice10.mc.protocol.data.game.entity.metadata.ItemStack;
import com.nukkitx.nbt.CompoundTagBuilder;
import com.nukkitx.protocol.bedrock.data.ItemData;
import com.nukkitx.protocol.bedrock.packet.InventoryContentPacket;
import com.nukkitx.protocol.bedrock.packet.InventorySlotPacket;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.dragonet.proxy.data.window.BedrockWindowType;
import org.dragonet.proxy.network.session.ProxySession;
import org.dragonet.proxy.network.session.cache.object.CachedWindow;
import org.dragonet.proxy.network.translator.misc.ItemTranslator;

import java.util.Arrays;

@Log4j2
public class SingleChestInventoryTranslator extends IInventoryTranslator {
    private static ItemData UNUSABLE_INVENTORY_SPACE_BLOCK;
    private final int rows;

    static {
        CompoundTagBuilder root = CompoundTagBuilder.builder();
        CompoundTagBuilder display = CompoundTagBuilder.builder();

        display.stringTag("Name", "Unusuable inventory space");
        root.tag(display.build("display"));

        // Update block
        UNUSABLE_INVENTORY_SPACE_BLOCK = ItemData.of(248, (short) 0, 1, root.buildRootTag());
    }

    public SingleChestInventoryTranslator(int size, int rows) {
        super(BedrockWindowType.CHEST, size);
        this.rows = rows;
    }

    @Override
    public void updateInventory(ProxySession session, CachedWindow window) {
        ItemData[] bedrockItems = new ItemData[size];
        int length = (9 * rows);

        for (int i = 0; i < bedrockItems.length; i++) {
            if(i < length) {
                bedrockItems[i] = window.getItem(i);
            } else {
                bedrockItems[i] = UNUSABLE_INVENTORY_SPACE_BLOCK;
            }
        }

        InventoryContentPacket contentPacket = new InventoryContentPacket();
        contentPacket.setContainerId(window.getWindowId());
        contentPacket.setContents(bedrockItems);
        session.sendPacket(contentPacket);

//        log.warn("update inventory");
    }

    @Override
    public void updateSlot(ProxySession session, CachedWindow window, int slot) {
        InventorySlotPacket inventorySlotPacket = new InventorySlotPacket();
        inventorySlotPacket.setContainerId(window.getWindowId());
        inventorySlotPacket.setItem(ItemTranslator.translateSlotToBedrock(window.getItems()[slot]));
        inventorySlotPacket.setSlot(slot);
        session.sendPacket(inventorySlotPacket);

//        log.warn("update slot");
    }

    @Override
    public boolean isSlotValid(int slot) {
        return slot < (9 * rows);
    }
}
