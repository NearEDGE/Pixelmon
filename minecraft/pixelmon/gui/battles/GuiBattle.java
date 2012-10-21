package pixelmon.gui.battles;

import java.util.ArrayList;
import java.util.prefs.BackingStoreException;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import cpw.mods.fml.common.network.PacketDispatcher;

import pixelmon.ServerStorageDisplay;
import pixelmon.comm.EnumPackets;
import pixelmon.comm.PacketCreator;
import pixelmon.comm.PixelmonDataPacket;
import pixelmon.comm.PixelmonMovesetDataPacket;
import pixelmon.config.PixelmonItems;
import pixelmon.config.PixelmonItemsPokeballs;
import pixelmon.entities.EntityCamera;
import pixelmon.entities.pixelmon.EntityPixelmon;
import pixelmon.enums.BagSection;
import pixelmon.gui.ContainerEmpty;
import pixelmon.gui.GuiPixelmonOverlay;
import pixelmon.items.ItemData;
import pixelmon.items.ItemEther;
import pixelmon.items.ItemPokeBall;
import pixelmon.items.ItemPotion;
import pixelmon.items.heldItems.ItemBerryLeppa;
import net.minecraft.client.Minecraft;
import net.minecraft.src.Container;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.GuiContainer;
import net.minecraft.src.InventoryPlayer;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.RenderHelper;
import net.minecraft.src.RenderManager;
import net.minecraft.src.Slot;
import net.minecraft.src.Tessellator;

public class GuiBattle extends GuiContainer {

	public enum BattleMode {
		Waiting, MainMenu, ChoosePokemon, ChooseBag, UseBag, ChooseAttack;
	}

	private int battleControllerIndex;
	public static BattleMode mode;
	public static BagSection bagSection;
	private int guiWidth = 300;
	private int guiHeight = 60;

	boolean cameraEnabled = false;

	public GuiBattle(int battleControllerIndex) {
		super(new ContainerEmpty());
		this.battleControllerIndex = battleControllerIndex;
		mode = BattleMode.Waiting;
		GuiPixelmonOverlay.isVisible = false;
		ClientBattleManager.clearMessages();
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float mfloat, int mouseX, int mouseY) {
		if (cameraEnabled && ClientBattleManager.camera != null)
			if (Minecraft.getMinecraft().renderViewEntity != ClientBattleManager.camera)
				Minecraft.getMinecraft().renderViewEntity = ClientBattleManager.camera;

		int left = (width - xSize) / 2;
		int top = (height - ySize) / 2;
		RenderHelper.disableStandardItemLighting();
		if (ClientBattleManager.hasMoreMessages() || mode == BattleMode.Waiting)
			drawMessageScreen();
		else if (mode == BattleMode.MainMenu)
			drawMainMenu(mouseX, mouseY);
		else if (mode == BattleMode.ChooseAttack)
			drawChooseAttack(mouseX, mouseY);
		else if (mode == BattleMode.ChoosePokemon)
			drawChoosePokemon(mouseX, mouseY);
		else if (mode == BattleMode.ChooseBag)
			drawChooseBag(mouseX, mouseY);
		else if (mode == BattleMode.UseBag)
			drawUseBag(mouseX, mouseY);

	}

	private int startIndex = 0;

	private void drawUseBag(int mouseX, int mouseY) {
		int guiIndex = -1;
		guiIndex = mc.renderEngine.getTexture("/pixelmon/gui/itemGui2.png");

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		drawImageQuad(guiIndex, width / 2 - 256 / 2, height / 2 - 102, 256, 205, 0, 0, 1, 205f / 256f);
		if (mouseX > width / 2 + 63 && mouseX < width / 2 + 111 && mouseY > height / 2 - 91 && mouseY < height / 2 - 74)
			drawImageQuad(guiIndex, width / 2 + 63, height / 2 - 91, 48, 17, 198f / 256f, 234 / 256f, 246f / 256f, 251f / 256f);
		drawString(fontRenderer, "Back", width / 2 + 76, height / 2 - 85, 0xFFFFFF);

		drawCenteredString(fontRenderer, bagSection.displayString, width / 2 - 40, height / 2 - 80, 0xFFFFFF);

		for (int i = startIndex; i < 6 + startIndex; i++) {
			if (i < ClientBattleManager.bagStore.size()) {
				if (mouseX > width / 2 - 98 && mouseX < width / 2 - 98 + 187 && mouseY > height / 2 - 44 + i * 21 && mouseY < height / 2 - 24 + i * 21)
					drawImageQuad(guiIndex, width / 2 - 98, height / 2 - 44 + i * 21, 187, 20, 3f / 256f, 206 / 256f, 194f / 256f, 225f / 256f);
				else
					drawImageQuad(guiIndex, width / 2 - 98, height / 2 - 44 + i * 21, 187, 20, 3f / 256f, 227 / 256f, 194f / 256f, 246f / 256f);
				Item item = PixelmonItems.getItem(ClientBattleManager.bagStore.get(i).id);
				if (item == null)
					item = PixelmonItemsPokeballs.getItemFromID(ClientBattleManager.bagStore.get(i).id);
				drawString(fontRenderer, item.getItemDisplayName(null), width / 2 - 55, height / 2 - 38 + i * 21, 0xFFFFFF);
				drawString(fontRenderer, "x" + ClientBattleManager.bagStore.get(i).count, width / 2 + 55, height / 2 - 38 + i * 21, 0xFFFFFF);
			}
		}

		if (mouseX > width / 2 - 11 && mouseX < width / 2 + 6 && mouseY > height / 2 - 55 && mouseY < height / 2 - 45)
			drawImageQuad(guiIndex, width / 2 - 11, height / 2 - 55, 17, 10, 211f / 256f, 220 / 256f, 228f / 256f, 230f / 256f);
		if (mouseX > width / 2 - 11 && mouseX < width / 2 + 6 && mouseY > height / 2 + 82 && mouseY < height / 2 + 92)
			drawImageQuad(guiIndex, width / 2 - 11, height / 2 + 82, 17, 10, 236f / 256f, 220 / 256f, 253f / 256f, 230f / 256f);

		for (int i = startIndex; i < 6 + startIndex; i++) {
			if (i < ClientBattleManager.bagStore.size()) {
				Item item = PixelmonItems.getItem(ClientBattleManager.bagStore.get(i).id);
				if (item == null)
					item = PixelmonItemsPokeballs.getItemFromID(ClientBattleManager.bagStore.get(i).id);
				itemRenderer.renderItemIntoGUI(this.fontRenderer, this.mc.renderEngine, new ItemStack(item), width / 2 - 85, height / 2 - 42 + i * 21);
			}
		}

	}

	private void drawChooseBag(int mouseX, int mouseY) {
		int guiIndex = -1;
		guiIndex = mc.renderEngine.getTexture("/pixelmon/gui/itemGui1_Test.png");

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		drawImageQuad(guiIndex, width / 2 - 256 / 2, height / 2 - 76, 256, 153, 0, 0, 1, 153f / 256f);
		drawCenteredString(fontRenderer, "Poke Balls", width / 2 + 53, height / 2 - 36, 0xFFFFFF);
		drawCenteredString(fontRenderer, "HP/PP Restore", width / 2 + 53, height / 2 + 31, 0xFFFFFF);
		drawCenteredString(fontRenderer, "Status Restore", width / 2 - 53, height / 2 - 36, 0xFFFFFF);
		drawCenteredString(fontRenderer, "Battle Items", width / 2 - 53, height / 2 + 31, 0xFFFFFF);
		int x1, x2, y1, y2;
		x1 = width / 2 - 103;
		x2 = width / 2 + 3;
		y1 = height / 2 - 63;
		y2 = height / 2 + 4;
		int buttonWidth = 100, buttonHeight = 62;
		if (mouseX > x1 && mouseX < x1 + buttonWidth && mouseY > y1 && mouseY < y1 + buttonHeight)
			drawImageQuad(guiIndex, x1, y1, buttonWidth, buttonHeight, 28f / 256f, 176f / 256f, 128f / 256f, 238f / 256f);
		if (mouseX > x1 && mouseX < x1 + buttonWidth && mouseY > y2 && mouseY < y2 + buttonHeight)
			drawImageQuad(guiIndex, x1, y2, buttonWidth, buttonHeight, 28f / 256f, 176f / 256f, 128f / 256f, 238f / 256f);
		if (mouseX > x2 && mouseX < x2 + buttonWidth && mouseY > y1 && mouseY < y1 + buttonHeight)
			drawImageQuad(guiIndex, x2, y1, buttonWidth, buttonHeight, 28f / 256f, 176f / 256f, 128f / 256f, 238f / 256f);
		if (mouseX > x2 && mouseX < x2 + buttonWidth && mouseY > y2 && mouseY < y2 + buttonHeight)
			drawImageQuad(guiIndex, x2, y2, buttonWidth, buttonHeight, 28f / 256f, 176f / 256f, 128f / 256f, 238f / 256f);

		if (mouseX > width / 2 + 106 && mouseX < width / 2 + 126 && mouseY > height / 2 + 55 && mouseY < height / 2 + 77)
			drawImageQuad(guiIndex, width / 2 + 106, height / 2 + 55, 20, 22, 234f / 256f, 154f / 256f, 254f / 256f, 176f / 256f);
	}

	int flashCount = 0;

	private void drawMessageScreen() {
		int guiIndex = -1;
		guiIndex = mc.renderEngine.getTexture("/pixelmon/gui/battleGui3.png");

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		drawImageQuad(guiIndex, width / 2 - guiWidth / 2, height - guiHeight, guiWidth, guiHeight, 0, 0, 1, 146f / 480f);
		if (mode != BattleMode.Waiting || ClientBattleManager.hasMoreMessages()) {
			drawCenteredString(fontRenderer, ClientBattleManager.getNextMessage(), width / 2, height - 35, 0xFFFFFF);
			flashCount++;
			if (flashCount > 30) {
				drawImageQuad(guiIndex, width / 2 + 130, height - 15, 10, 6, 611f / 640f, 149f / 480f, 628f / 640f, 159f / 480f);
				if (flashCount > 60)
					flashCount = 0;
			}
		} else {
			flashCount++;
			if (flashCount >= 160)
				flashCount = 0;
			if (flashCount < 40)
				drawCenteredString(fontRenderer, "Waiting", width / 2, height - 35, 0xFFFFFF);
			else if (flashCount < 80)
				drawCenteredString(fontRenderer, "Waiting.", width / 2, height - 35, 0xFFFFFF);
			else if (flashCount < 120)
				drawCenteredString(fontRenderer, "Waiting..", width / 2, height - 35, 0xFFFFFF);
			else if (flashCount < 160)
				drawCenteredString(fontRenderer, "Waiting...", width / 2, height - 35, 0xFFFFFF);

		}
	}

	@Override
	public void handleKeyboardInput() {
		return;
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		if (ClientBattleManager.hasMoreMessages()) {
			ClientBattleManager.removeMessage();
			return;
		}
		if (mode == BattleMode.MainMenu) {
			int x1 = width / 2 + 31;
			int y1 = height - guiHeight + 9;
			int x2 = width / 2 + 90;
			int y2 = height - guiHeight + 35;
			int w = 48, h = 16;
			if (mouseX > x1 && mouseX < x1 + w && mouseY > y1 && mouseY < y1 + h)
				mode = BattleMode.ChooseAttack;
			else if (mouseX > x2 && mouseX < x2 + w && mouseY > y1 && mouseY < y1 + h)
				mode = BattleMode.ChoosePokemon;
			else if (mouseX > x1 && mouseX < x1 + w && mouseY > y2 && mouseY < y2 + h) {
				mode = BattleMode.ChooseBag;
			} else if (mouseX > x2 && mouseX < x2 + w && mouseY > y2 && mouseY < y2 + h) {
				PacketDispatcher.sendPacketToServer(PacketCreator.createPacket(EnumPackets.Flee, 0));
				mode = BattleMode.Waiting;
			}
			return;

		} else if (mode == BattleMode.ChooseAttack) {
			if (mouseX > width / 2 + 137 && mouseX < width / 2 + 148 && mouseY > height - 11 && mouseY < height - 1) {
				mode = BattleMode.MainMenu;
				return;
			}
			int x1 = width / 2 - 141;
			int x2 = width / 2 - 50;
			int y1 = height - guiHeight + 9;
			int y2 = height - guiHeight + 33;
			int w = 87, h = 20;
			if (mouseX > x1 && mouseX < x1 + w && mouseY > y1 && mouseY < y1 + h) {
				PacketDispatcher.sendPacketToServer(PacketCreator.createPacket(EnumPackets.ChooseAttack, 0, battleControllerIndex,
						ClientBattleManager.getUserPokemon().pokemonID));
				mode = BattleMode.Waiting;
				return;
			} else if (mouseX > x2 && mouseX < x2 + w && mouseY > y1 && mouseY < y1 + h) {
				PacketDispatcher.sendPacketToServer(PacketCreator.createPacket(EnumPackets.ChooseAttack, 1, battleControllerIndex,
						ClientBattleManager.getUserPokemon().pokemonID));
				mode = BattleMode.Waiting;
				return;
			} else if (mouseX > x1 && mouseX < x1 + w && mouseY > y2 && mouseY < y2 + h) {
				PacketDispatcher.sendPacketToServer(PacketCreator.createPacket(EnumPackets.ChooseAttack, 2, battleControllerIndex,
						ClientBattleManager.getUserPokemon().pokemonID));
				mode = BattleMode.Waiting;
				return;
			} else if (mouseX > x2 && mouseX < x2 + w && mouseY > y2 && mouseY < y2 + h) {
				PacketDispatcher.sendPacketToServer(PacketCreator.createPacket(EnumPackets.ChooseAttack, 3, battleControllerIndex,
						ClientBattleManager.getUserPokemon().pokemonID));
				mode = BattleMode.Waiting;
				return;
			}
		} else if (mode == BattleMode.ChoosePokemon) {
			if (mouseX > width / 2 + 63 && mouseX < width / 2 + 63 + 48 && mouseY > height - 27 && mouseY < height - 27 + 17) {
				mode = BattleMode.MainMenu;
				return;
			}
			int pos = 0;
			for (int i = 0; i < 6; i++) {
				if (i != ClientBattleManager.getUserPokemon().order) {
					PixelmonDataPacket pdata = ServerStorageDisplay.pokemon[i];
					if (pdata != null) {
						int xpos = width / 2 - 30;
						int ypos = height - 195 + pos * 30;
						if (mouseX > xpos && mouseX < xpos + 150 && mouseY > ypos + 1 && mouseY < ypos + 31) {
							PacketDispatcher.sendPacketToServer(PacketCreator.createPacket(EnumPackets.SwitchPokemon, pdata.order, battleControllerIndex, 0));
							mode = BattleMode.Waiting;
							return;
						}
					}
					pos++;
				}
			}
		} else if (mode == BattleMode.ChooseBag) {
			if (mouseX > width / 2 + 106 && mouseX < width / 2 + 126 && mouseY > height / 2 + 55 && mouseY < height / 2 + 77)
				mode = BattleMode.MainMenu;

			int x1, x2, y1, y2;
			x1 = width / 2 - 103;
			x2 = width / 2 + 3;
			y1 = height / 2 - 63;
			y2 = height / 2 + 4;
			int buttonWidth = 100, buttonHeight = 62;
			bagSection = null;
			if (mouseX > x1 && mouseX < x1 + buttonWidth && mouseY > y1 && mouseY < y1 + buttonHeight)
				bagSection = BagSection.StatusRestore;

			else if (mouseX > x1 && mouseX < x1 + buttonWidth && mouseY > y2 && mouseY < y2 + buttonHeight)
				bagSection = BagSection.BattleItems;

			else if (mouseX > x2 && mouseX < x2 + buttonWidth && mouseY > y1 && mouseY < y1 + buttonHeight)
				bagSection = BagSection.Pokeballs;

			else if (mouseX > x2 && mouseX < x2 + buttonWidth && mouseY > y2 && mouseY < y2 + buttonHeight)
				bagSection = BagSection.HP;

			if (bagSection != null) {
				mode = BattleMode.UseBag;
				ClientBattleManager.bagStore.clear();
				getInventory();
				startIndex = 0;
			}
		} else if (mode == BattleMode.UseBag) {
			if (mouseX > width / 2 + 63 && mouseX < width / 2 + 111 && mouseY > height / 2 - 91 && mouseY < height / 2 - 74)
				mode = BattleMode.ChooseBag;
		}
		super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	private void drawMainMenu(int mouseX, int mouseY) {
		int guiIndex = -1;
		guiIndex = mc.renderEngine.getTexture("/pixelmon/gui/battleGui1.png");

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		drawImageQuad(guiIndex, width / 2 - guiWidth / 2, height - guiHeight, guiWidth, guiHeight, 0, 0, 1, 146f / 480f);
		drawButton(width / 2 + 31, height - guiHeight + 9, 48, 16, "FIGHT", mouseX, mouseY, guiIndex, 0);
		drawButton(width / 2 + 31, height - guiHeight + 35, 48, 16, "BAG", mouseX, mouseY, guiIndex, 1);
		drawButton(width / 2 + 90, height - guiHeight + 9, 48, 16, "POKEMON", mouseX, mouseY, guiIndex, 2);
		drawButton(width / 2 + 90, height - guiHeight + 35, 48, 16, "RUN", mouseX, mouseY, guiIndex, 3);
		drawString(fontRenderer, "What will " + ClientBattleManager.getUserPokemon().name + " do?", width / 2 - 130, height - 35, 0xFFFFFF);
	}

	private void drawChoosePokemon(int mouseX, int mouseY) {
		int guiIndex = -1;
		guiIndex = mc.renderEngine.getTexture("/pixelmon/gui/choosePokemon.png");

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		drawImageQuad(guiIndex, width / 2 - 128, height - 203, 256, 203, 0, 0, 1, 203f / 256f);

		drawString(fontRenderer, "Choose a Pokemon", width / 2 - 90, height - 23, 0xFFFFFF);

		if (mouseX > width / 2 + 63 && mouseX < width / 2 + 63 + 48 && mouseY > height - 27 && mouseY < height - 27 + 17) {
			drawImageQuad(guiIndex, width / 2 + 63, height - 27, 48, 17, 198f / 256f, 210f / 256f, 246f / 256, 227f / 256f);
		}
		drawString(fontRenderer, "Back", width / 2 + 75, height - 22, 0xFFFFFF);

		PixelmonDataPacket p = ClientBattleManager.getUserPokemon();
		String numString = "";
		if (p.nationalPokedexNumber < 10)
			numString = "00" + p.nationalPokedexNumber;
		else if (p.nationalPokedexNumber < 100)
			numString = "0" + p.nationalPokedexNumber;
		else
			numString = "" + p.nationalPokedexNumber;
		int var9;
		if (p.isShiny)
			var9 = Minecraft.getMinecraft().renderEngine.getTexture("/pixelmon/shinysprites/" + numString + ".png");
		else
			var9 = Minecraft.getMinecraft().renderEngine.getTexture("/pixelmon/sprites/" + numString + ".png");
		drawImageQuad(var9, width / 2 - 121, height - 176, 24f, 24f, 0f, 0f, 1f, 1f);
		drawHealthBar(width / 2 - 85, height - 135, 9, 56, p);
		drawImageQuad(guiIndex, width / 2 - 95, height - 135, 61, 9, 86f / 256f, 240f / 256f, 147f / 256f, 249f / 256f);
		drawCenteredString(fontRenderer, p.health + "/" + p.hp, width / 2 - 59, height - 123, 0xffffff);
		drawString(fontRenderer, p.nickname.equals("") ? p.name : p.nickname, width / 2 - 90, height - 161, 0xffffff);
		drawString(fontRenderer, "Lv. " + p.lvl, width / 2 - 90, height - 148, 0xffffff);
		if (p.isMale)
			drawImageQuad(guiIndex, width / 2 - 60, height - 149, 6, 9, 32f / 256f, 208f / 256f, 38f / 256f, 217f / 256f);
		else
			drawImageQuad(guiIndex, width / 2 - 60, height - 149, 6, 9, 32f / 256f, 218f / 256f, 38f / 256f, 227f / 256f);

		int pos = -1;
		for (int i = 0; i < 6; i++) {
			if (i != p.order) {
				pos++;
				PixelmonDataPacket pdata = ServerStorageDisplay.pokemon[i];
				if (pdata != null) {

					numString = "";
					if (pdata.nationalPokedexNumber < 10)
						numString = "00" + pdata.nationalPokedexNumber;
					else if (pdata.nationalPokedexNumber < 100)
						numString = "0" + pdata.nationalPokedexNumber;
					else
						numString = "" + pdata.nationalPokedexNumber;
					if (pdata.isShiny)
						var9 = Minecraft.getMinecraft().renderEngine.getTexture("/pixelmon/shinysprites/" + numString + ".png");
					else
						var9 = Minecraft.getMinecraft().renderEngine.getTexture("/pixelmon/sprites/" + numString + ".png");
					drawImageQuad(var9, width / 2 - 23, height - 192 + pos * 30, 24f, 24f, 0f, 0f, 1f, 1f);
					drawHealthBar(width / 2 + 65, height - 192 + pos * 30, 9, 56, pdata);
					drawImageQuad(guiIndex, width / 2 + 55, height - 192 + pos * 30, 61, 9, 86f / 256f, 240f / 256f, 147f / 256f, 249f / 256f);
					drawString(fontRenderer, pdata.health + "/" + pdata.hp, width / 2 + 75, height - 180 + pos * 30, 0xffffff);
					drawString(fontRenderer, pdata.nickname.equals("") ? pdata.name : pdata.nickname, width / 2 + 5, height - 190 + pos * 30, 0xffffff);
					drawString(fontRenderer, "Lv. " + pdata.lvl, width / 2 + 5, height - 176 + pos * 30, 0xffffff);
					if (pdata.isMale)
						drawImageQuad(guiIndex, width / 2 + 40, height - 176 + pos * 30, 6, 9, 32f / 256f, 208f / 256f, 38f / 256f, 217f / 256f);
					else
						drawImageQuad(guiIndex, width / 2 + 40, height - 176 + pos * 30, 6, 9, 32f / 256f, 218f / 256f, 38f / 256f, 227f / 256f);

					int xpos = width / 2 - 30;
					int ypos = height - 195 + pos * 30;
					if (mouseX > xpos && mouseX < xpos + 150 && mouseY > ypos + 1 && mouseY < ypos + 31)
						drawImageQuad(guiIndex, xpos, ypos, 150, 32, 43f / 256f, 205f / 256f, 194f / 256f, 237f / 256f);
				}
			}
		}
	}

	public void drawHealthBar(int x, int y, int height, int width, PixelmonDataPacket p) {
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glEnable(GL11.GL_COLOR_MATERIAL);
		GL11.glPushMatrix();
		Tessellator tessellator = Tessellator.instance;
		GL11.glDisable(3553 /* GL_TEXTURE_2D */);
		tessellator.startDrawingQuads();

		int barWidth = (int) (((float) p.health) / ((float) p.hp) * (((float) width) - 6f));
		tessellator.setColorRGBA_F(1.0f - ((float) p.health / (float) p.hp) * 0.8F, 0.2F + ((float) p.health / (float) p.hp) * 0.8F, 0.2F, 1.0F);
		tessellator.addVertex(x, y, 0.0);
		tessellator.addVertex(x, y + height, 0.0);
		tessellator.addVertex(x + barWidth, y + height, 0.0);
		tessellator.addVertex(x + barWidth, y, 0.0);
		tessellator.draw();
		GL11.glPopMatrix();
		GL11.glEnable(3553);
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		GL11.glDisable(GL11.GL_COLOR_MATERIAL);
	}

	private void drawChooseAttack(int mouseX, int mouseY) {
		int guiIndex = -1;
		guiIndex = mc.renderEngine.getTexture("/pixelmon/gui/battleGui2.png");

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		drawImageQuad(guiIndex, width / 2 - guiWidth / 2, height - guiHeight, guiWidth, guiHeight, 0, 0, 1, 146f / 480f);
		PixelmonMovesetDataPacket[] moveset = ClientBattleManager.getUserPokemon().moveset;
		int numMoves = ClientBattleManager.getUserPokemon().numMoves;
		if (numMoves > 0)
			drawButton(width / 2 - 141, height - guiHeight + 9, 87, 20, moveset[0].attackName, mouseX, mouseY, guiIndex, 0);
		if (numMoves > 1)
			drawButton(width / 2 - 50, height - guiHeight + 9, 87, 20, moveset[1].attackName, mouseX, mouseY, guiIndex, 1);
		if (numMoves > 2)
			drawButton(width / 2 - 141, height - guiHeight + 33, 87, 20, moveset[2].attackName, mouseX, mouseY, guiIndex, 2);
		if (numMoves > 3)
			drawButton(width / 2 - 50, height - guiHeight + 33, 87, 20, moveset[3].attackName, mouseX, mouseY, guiIndex, 3);
		drawCenteredString(fontRenderer, "PP: " + moveset[mouseOverButton].pp + "/" + moveset[mouseOverButton].ppBase, width / 2 + 99, height - guiHeight + 18,
				0xFFFFFF);
		drawString(fontRenderer, "Type: ", width / 2 + 99 - fontRenderer.getStringWidth("Type: " + moveset[mouseOverButton].type.toString()) / 2, height
				- guiHeight + 33, 0xFFFFFF);
		drawString(fontRenderer, moveset[mouseOverButton].type.toString(),
				width / 2 + 99 - fontRenderer.getStringWidth("Type: " + moveset[mouseOverButton].type.toString()) / 2 + fontRenderer.getStringWidth("Type: "),
				height - guiHeight + 33, moveset[mouseOverButton].type.getColor());

		if (mouseX > width / 2 + 137 && mouseX < width / 2 + 148 && mouseY > height - 11 && mouseY < height - 1)
			drawImageQuad(guiIndex, width / 2 + 137, height - 11, 11, 10, 613f / 640f, 151f / 480f, 635f / 640f, 171f / 480f);

	}

	private int mouseOverButton = 0;

	private void drawButton(int x, int y, int buttonWidth, int buttonHeight, String string, int mouseX, int mouseY, int guiIndex, int ind) {
		if (mode == BattleMode.MainMenu) {
			if (mouseX > x && mouseX < x + buttonWidth && mouseY > y && mouseY < y + buttonHeight) {
				drawImageQuad(guiIndex, x, y, buttonWidth, buttonHeight, 387f / 640f, 158f / 480f, 489f / 640f, 196f / 480f);
			}
			drawCenteredString(fontRenderer, string, x + buttonWidth / 2, y + buttonHeight / 2 - 3, 0xFFFFFF);
		} else if (mode == BattleMode.ChooseAttack) {
			drawImageQuad(guiIndex, x, y, buttonWidth, buttonHeight, 206f / 640f, 152f / 480f, 393f / 640f, 202f / 480f);
			if (mouseX > x && mouseX < x + buttonWidth && mouseY > y && mouseY < y + buttonHeight) {
				drawImageQuad(guiIndex, x + 2, y + 2, buttonWidth - 5, buttonHeight - 4, 23f / 640f, 155f / 480f, 200f / 640f, 195f / 480f);
				mouseOverButton = ind;
			}
			drawCenteredString(fontRenderer, string, x + buttonWidth / 2, y + buttonHeight / 2 - 3, 0xFFFFFF);
		}
	}

	@Override
	public void drawBackground(int par1) {
	}

	@Override
	public void drawDefaultBackground() {
	}

	private void drawImageQuad(int textureHandle, int x, int y, float w, float h, float us, float vs, float ue, float ve) {
		// activate the specified texture
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureHandle);

		float var7 = 0.00390625F;
		float var8 = 0.00390625F;
		Tessellator var9 = Tessellator.instance;
		var9.startDrawingQuads();
		var9.addVertexWithUV((double) (x + 0), (double) (y + h), (double) this.zLevel, (double) ((float) us), (double) ((float) ve));
		var9.addVertexWithUV((double) (x + w), (double) (y + h), (double) this.zLevel, (double) ((float) ue), (double) ((float) ve));
		var9.addVertexWithUV((double) (x + w), (double) (y + 0), (double) this.zLevel, (double) ((float) ue), (double) ((float) vs));
		var9.addVertexWithUV((double) (x + 0), (double) (y + 0), (double) this.zLevel, (double) ((float) us), (double) ((float) vs));
		var9.draw();
	}

	private void getInventory() {
		InventoryPlayer inventory = Minecraft.getMinecraft().thePlayer.inventory;
		for (int i = 0; i < inventory.mainInventory.length; i++) {
			if (bagSection == BagSection.Pokeballs) {
				if (inventory.mainInventory[i] != null && inventory.mainInventory[i].getItem() instanceof ItemPokeBall)
					checkExists(inventory.mainInventory[i].itemID, inventory.mainInventory[i].stackSize);
			} else if (bagSection == BagSection.HP) {
				if (inventory.mainInventory[i] != null && inventory.mainInventory[i].getItem() instanceof ItemPotion)
					checkExists(inventory.mainInventory[i].itemID, inventory.mainInventory[i].stackSize);
				else if (inventory.mainInventory[i] != null && inventory.mainInventory[i].getItem() instanceof ItemEther)
					checkExists(inventory.mainInventory[i].itemID, inventory.mainInventory[i].stackSize);
			}
		}
	}

	private void checkExists(int itemID, int count) {
		boolean hasItem = false;
		for (ItemData d : ClientBattleManager.bagStore) {
			if (d.id == itemID) {
				hasItem = true;
				d.count += count;
			}
		}
		if (!hasItem)
			ClientBattleManager.bagStore.add(new ItemData(itemID, count));

	}
}