/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gibstick.bukkit.discosheep;

import java.util.List;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerShearEntityEvent;

/**
 *
 * @author Mauve
 */
public class SheepDeshearer implements Listener {

	List<Sheep> sheep;

	public SheepDeshearer(List sheep) {
		this.sheep = sheep;
	}

	@EventHandler
	public void onPlayerShear(PlayerShearEntityEvent e) {
		if (e.getEntity() instanceof Sheep && sheep.contains((Sheep) e.getEntity())) {
			e.setCancelled(true);
		}
	}
}
