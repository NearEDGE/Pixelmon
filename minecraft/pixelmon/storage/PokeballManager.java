package pixelmon.storage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import pixelmon.DownloadHelper;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.world.WorldEvent;
import cpw.mods.fml.common.network.Player;

public class PokeballManager {
	private File workingDir;

	private ArrayList<PlayerStorage> playerPokemonList = new ArrayList<PlayerStorage>();

	public enum PokeballManagerMode {
		Player, Trainer
	}

	public PokeballManager() {
	}

	public PlayerStorage getPlayerStorage(EntityPlayerMP owner) throws PlayerNotLoadedException {
		for (PlayerStorage p : playerPokemonList) {
			if (p.player != null && owner != null && p.player.username.equals(owner.username))
				return p;
		}
		return loadPlayer(owner);
	}

	public EntityPlayerMP getPlayerFromName(String name) {
		for (PlayerStorage p : playerPokemonList)
			if (p.player.username.equals(name))
				return p.player;
		return null;
	}

	@SuppressWarnings("unchecked")
	public PlayerStorage loadPlayer(EntityPlayerMP player) throws PlayerNotLoadedException {
		if (player == null)
			throw new PlayerNotLoadedException();
		File saveDirPath = new File(getSaveFolder(player));
		if (!saveDirPath.exists())
			saveDirPath.mkdirs();
		File playerFile = new File(getSaveFolder(player) + player.username + ".pk");
		PlayerStorage p;
		if (playerFile.exists()) {
			p = new PlayerStorage(player);
			try {
				p.readFromNBT(CompressedStreamTools.read(new DataInputStream(new FileInputStream(playerFile))));
			} catch (FileNotFoundException e) {
				System.out.println("Couldn't read player data file for " + player.username);
				throw new PlayerNotLoadedException();
			} catch (IOException e) {
				System.out.println("Couldn't read player data file for " + player.username);
				throw new PlayerNotLoadedException();
			}
			playerPokemonList.add(p);

		} else {
			p = new PlayerStorage(player);
			playerPokemonList.add(p);
		}
		return p;
	}

	public void saveAll() {
		for (int i = 0; i < playerPokemonList.size(); i++) {
			savePlayer(playerPokemonList.get(i));
		}
	}

	public void savePlayer(PlayerStorage p) {
		try {
			for (int i = 0; i < playerPokemonList.size(); i++) {
				String userName = playerPokemonList.get(i).userName;
				File playerSaveFile = new File(playerPokemonList.get(i).saveFile);
				FileOutputStream f = new FileOutputStream(playerSaveFile);
				DataOutputStream s = new DataOutputStream(f);
				NBTTagCompound nbt = new NBTTagCompound();
				playerPokemonList.get(i).writeToNBT(nbt);
				CompressedStreamTools.write(nbt, s);
				s.close();
				f.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String getSaveFolder(EntityPlayer player) {
		return DownloadHelper.getDir() + "/saves/" + player.worldObj.getSaveHandler().getWorldDirectoryName() + "/pokemon/";
	}

	@ForgeSubscribe
	public void onWorldLoad(WorldEvent.Load event) {
		ArrayList<EntityPlayerMP> playerList = new ArrayList<EntityPlayerMP>();
		for (int i = 0; i < playerPokemonList.size(); i++) {
			playerList.add(playerPokemonList.get(i).player);
		}
		playerPokemonList.clear();
		for (EntityPlayerMP player : playerList) {
			try {
				loadPlayer(player);
			} catch (Exception e) {
				System.out.println("Failed to load player " + player.username);
			}
		}
	}

	@ForgeSubscribe
	public void onWorldSave(WorldEvent.Save event) {
		saveAll();
	}

	public boolean hasPlayerFile(Player player) {
		File playerSaveFile = new File(getSaveFolder((EntityPlayerMP) player) + ((EntityPlayerMP) player).username + ".pk");
		return playerSaveFile.exists();
	}

	public void onPlayerDC(EntityPlayer player) {
		if (player == null)
			return;
		for (int i = 0; i < playerPokemonList.size(); i++) {
			if (playerPokemonList.get(i).userName.equals(player.username)) {
				savePlayer(playerPokemonList.get(i));
				playerPokemonList.remove(i);
				break;
			}
		}
	}
}
