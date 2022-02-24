package org.osmdroid.tileprovider.tilesource;

/**
 * Tile sources that have a settable "style" attibute can implement this. After setting this on a
 * tile provider, you may need to call clearTileCache() or call setTileSource() again on the tile
 * provider to clear the current tiles on the screen that are still in the old style.
 */
public interface IStyledTileSource<T> {

	void setStyle(T style);

	void setStyle(String style);

	T getStyle();
}
