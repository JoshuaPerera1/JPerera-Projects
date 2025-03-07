package nz.ac.wgtn.swen225.lc.persistency;

import java.util.List;
import nz.ac.wgtn.swen225.lc.domain.Chap;
import nz.ac.wgtn.swen225.lc.domain.EnemyActor;
import nz.ac.wgtn.swen225.lc.domain.TileType;

/**
 * Record containing a Snapshot the State of Domain.
 *
 * @param levelNumber   the current level being played.
 * @param mazeTiles     state of maze tile grid.
 * @param chap          the player chap object.
 * @param enemies       list of the enemy actors present.
 * @param chapInventory keys present in Chap's inventory.
 * @param infoText      text shown by the level info tile.
 */
public record GameState(
    int levelNumber,
    TileType[][] mazeTiles,
    Chap chap,
    List<EnemyActor> enemies,
    List<TileType> chapInventory,
    String infoText
) {
}
