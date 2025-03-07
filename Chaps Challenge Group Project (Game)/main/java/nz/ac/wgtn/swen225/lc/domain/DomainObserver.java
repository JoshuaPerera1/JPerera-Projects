package nz.ac.wgtn.swen225.lc.domain;

/**
 * Domain event Observer. Primarily used by Renderer for sounds.
 *
 * @author Riley West (300608942).
 */
public interface DomainObserver {
  /**
   * The event handling method.
   *
   * @param event    enum type of the event.
   * @param tileType enum associated tiletype.
   */
  void handleEvent(EventType event, TileType tileType);
}

