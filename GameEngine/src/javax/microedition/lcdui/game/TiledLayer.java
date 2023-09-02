/**
 * <p>Title: TiledLayer.java</p>
 *
 * <p>Description: TiledLayer MIDP2.0</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * @author fengsheng.yang
 *
 * @version 1.0
 *
 * @Date 2009-5-14
 */
package javax.microedition.lcdui.game;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

public class TiledLayer extends Layer {
	/**
	 * the overall height of the TiledLayer grid
	 */
	private int cellHeight; // = 0;
	/**
	 * the overall cell width of the TiledLayer grid
	 */
	private int cellWidth; // = 0;

	/**
	 * The num of rows of the TiledLayer grid.
	 */
	private int rows; // = 0;

	/**
	 * the num of columns in the TiledLayer grid
	 */
	private int columns; // = 0;

	/**
	 * int array for storing row and column of cell
	 * 
	 * it contains the tile Index for both static and animated tiles
	 */
	private int[][] cellMatrix; // = null;

	/**
	 * Source image for tiles
	 */
	// package access as it is used by Pixel level Collision
	// detection with a Sprite
	Bitmap sourceImage; // = null;

	/**
	 * no. of tiles
	 */
	private int numberOfTiles; // = 0;

	/**
	 * X co-ordinate definitions for individual frames into the source image
	 */
	// package access as it is used by Pixel level Collision
	// detection with a Sprite
	int[] tileSetX;

	/**
	 * Y co-ordinate definitions for individual frames into the source image
	 */
	// package access as it is used by Pixel level Collision
	// detection with a Sprite
	int[] tileSetY;

	/**
	 * Table to map from animated Index to static Index 0th location is unused.
	 * anim --> static Index -1 --> 21 -2 --> 34 -3 --> 45 for now keep 0 the
	 * location of the table empty instead of computing -index make index +ve
	 * and access this Table.
	 * 
	 */
	private int[] anim_to_static; // = null;

	/**
	 * total number of animated tiles. This variable is also used as index in
	 * the above table to add new entries to the anim_to_static table.
	 * initialized to 1 when table is created.
	 */
	private int numOfAnimTiles; // = 0

	/**
	 * Creates a new TiledLayer.
	 * <p>
	 * 
	 * The TiledLayer's grid will be <code>rows</code> cells high and
	 * <code>columns</code> cells wide. All cells in the grid are initially
	 * empty (i.e. they contain tile index 0). The contents of the grid may be
	 * modified through the use of {@link #setCell} and {@link #fillCells}.
	 * <P>
	 * The static tile set for the TiledLayer is created from the specified
	 * Image with each tile having the dimensions of tileWidth x tileHeight. The
	 * width of the source image must be an integer multiple of the tile width,
	 * and the height of the source image must be an integer multiple of the
	 * tile height; otherwise, an IllegalArgumentException is thrown;
	 * <p>
	 * 
	 * The entire static tile set can be changed using
	 * {@link #setStaticTileSet(Image, int, int)}. These methods should be used
	 * sparingly since they are both memory and time consuming. Where possible,
	 * animated tiles should be used instead to animate tile appearance.
	 * <p>
	 * 
	 * @param columns
	 *            the width of the <code>TiledLayer</code>, expressed as a
	 *            number of cells
	 * @param rows
	 *            the height of the <code>TiledLayer</code>, expressed as a
	 *            number of cells
	 * @param image
	 *            the <code>Image</code> to use for creating the static tile set
	 * @param tileWidth
	 *            the width in pixels of a single tile
	 * @param tileHeight
	 *            the height in pixels of a single tile
	 * @throws NullPointerException
	 *             if <code>image</code> is <code>null</code>
	 * @throws IllegalArgumentException
	 *             if the number of <code>rows</code> or <code>columns</code> is
	 *             less than <code>1</code>
	 * @throws IllegalArgumentException
	 *             if <code>tileHeight</code> or <code>tileWidth</code> is less
	 *             than <code>1</code>
	 * @throws IllegalArgumentException
	 *             if the <code>image</code> width is not an integer multiple of
	 *             the <code>tileWidth</code>
	 * @throws IllegalArgumentException
	 *             if the <code>image</code> height is not an integer multiple
	 *             of the <code>tileHeight</code>
	 */
	public TiledLayer(int columns, int rows, Bitmap image, int tileWidth,
			int tileHeight) {
		// IllegalArgumentException will be thrown
		// in the Layer super-class constructor
		super(columns < 1 || tileWidth < 1 ? -1 : columns * tileWidth, rows < 1
				|| tileHeight < 1 ? -1 : rows * tileHeight);

		// if img is null img.getWidth() will throw NullPointerException
		if (((image.getWidth() % tileWidth) != 0)
				|| ((image.getHeight() % tileHeight) != 0)) {
			throw new IllegalArgumentException();
		}
		this.columns = columns;
		this.rows = rows;

		cellMatrix = new int[rows][columns];

		int noOfFrames = (image.getWidth() / tileWidth)
				* (image.getHeight() / tileHeight);
		// the zero th index is left empty for transparent tile
		// so it is passed in createStaticSet as noOfFrames + 1
		// Also maintain static indices is true
		// all elements of cellMatrix[][]
		// are set to zero by new, so maintainIndices = true
		createStaticSet(image, noOfFrames + 1, tileWidth, tileHeight, true);
	}

	/**
	 * Creates a new animated tile and returns the index that refers to the new
	 * animated tile. It is initially associated with the specified tile index
	 * (either a static tile or 0).
	 * <P>
	 * The indices for animated tiles are always negative. The first animated
	 * tile shall have the index -1, the second, -2, etc.
	 * 
	 * @param staticTileIndex
	 *            the index of the associated tile (must be <code>0</code> or a
	 *            valid static tile index)
	 * @return the index of newly created animated tile
	 * @throws IndexOutOfBoundsException
	 *             if the <code>staticTileIndex</code> is invalid
	 */
	public int createAnimatedTile(int staticTileIndex) {
		// checks static tile
		if (staticTileIndex < 0 || staticTileIndex >= numberOfTiles) {
			throw new IndexOutOfBoundsException();
		}

		if (anim_to_static == null) {
			anim_to_static = new int[4];
			numOfAnimTiles = 1;
		} else if (numOfAnimTiles == anim_to_static.length) {
			// grow anim_to_static table if needed
			int new_anim_tbl[] = new int[anim_to_static.length * 2];
			System.arraycopy(anim_to_static, 0, new_anim_tbl, 0,
					anim_to_static.length);
			anim_to_static = new_anim_tbl;
		}
		anim_to_static[numOfAnimTiles] = staticTileIndex;
		numOfAnimTiles++;
		return (-(numOfAnimTiles - 1));
	}

	/**
	 * Associates an animated tile with the specified static tile.
	 * <p>
	 * 
	 * @param animatedTileIndex
	 *            the index of the animated tile
	 * @param staticTileIndex
	 *            the index of the associated tile (must be <code>0</code> or a
	 *            valid static tile index)
	 * @throws IndexOutOfBoundsException
	 *             if the <code>staticTileIndex</code> is invalid
	 * @throws IndexOutOfBoundsException
	 *             if the animated tile index is invalid
	 * @see #getAnimatedTile
	 * 
	 */
	public void setAnimatedTile(int animatedTileIndex, int staticTileIndex) {
		// checks static tile
		if (staticTileIndex < 0 || staticTileIndex >= numberOfTiles) {
			throw new IndexOutOfBoundsException();
		}
		// do animated tile index check
		animatedTileIndex = -animatedTileIndex;
		if (anim_to_static == null || animatedTileIndex <= 0
				|| animatedTileIndex >= numOfAnimTiles) {
			throw new IndexOutOfBoundsException();
		}

		anim_to_static[animatedTileIndex] = staticTileIndex;

	}

	/**
	 * Gets the tile referenced by an animated tile.
	 * <p>
	 * 
	 * Returns the tile index currently associated with the animated tile.
	 * 
	 * @param animatedTileIndex
	 *            the index of the animated tile
	 * @return the index of the tile reference by the animated tile
	 * @throws IndexOutOfBoundsException
	 *             if the animated tile index is invalid
	 * @see #setAnimatedTile
	 */
	public int getAnimatedTile(int animatedTileIndex) {
		animatedTileIndex = -animatedTileIndex;
		if (anim_to_static == null || animatedTileIndex <= 0
				|| animatedTileIndex >= numOfAnimTiles) {
			throw new IndexOutOfBoundsException();
		}

		return anim_to_static[animatedTileIndex];
	}

	/**
	 * Sets the contents of a cell.
	 * <P>
	 * 
	 * The contents may be set to a static tile index, an animated tile index,
	 * or it may be left empty (index 0)
	 * 
	 * @param col
	 *            the column of cell to set
	 * @param row
	 *            the row of cell to set
	 * @param tileIndex
	 *            the index of tile to place in cell
	 * @throws IndexOutOfBoundsException
	 *             if there is no tile with index <code>tileIndex</code>
	 * @throws IndexOutOfBoundsException
	 *             if <code>row</code> or <code>col</code> is outside the bounds
	 *             of the <code>TiledLayer</code> grid
	 * @see #getCell
	 * @see #fillCells
	 */
	public void setCell(int col, int row, int tileIndex) {

		if (col < 0 || col >= this.columns || row < 0 || row >= this.rows) {
			throw new IndexOutOfBoundsException();
		}

		if (tileIndex > 0) {
			// do checks for static tile
			if (tileIndex >= numberOfTiles) {
				throw new IndexOutOfBoundsException();
			}
		} else if (tileIndex < 0) {
			// do animated tile index check
			if (anim_to_static == null || (-tileIndex) >= numOfAnimTiles) {
				throw new IndexOutOfBoundsException();
			}
		}

		cellMatrix[row][col] = tileIndex;

	}

	/**
	 * Gets the contents of a cell.
	 * <p>
	 * 
	 * Gets the index of the static or animated tile currently displayed in a
	 * cell. The returned index will be 0 if the cell is empty.
	 * 
	 * @param col
	 *            the column of cell to check
	 * @param row
	 *            the row of cell to check
	 * @return the index of tile in cell
	 * @throws IndexOutOfBoundsException
	 *             if <code>row</code> or <code>col</code> is outside the bounds
	 *             of the <code>TiledLayer</code> grid
	 * @see #setCell
	 * @see #fillCells
	 */
	public int getCell(int col, int row) {
		if (col < 0 || col >= this.columns || row < 0 || row >= this.rows) {
			throw new IndexOutOfBoundsException();
		}
		return cellMatrix[row][col];
	}

	/**
	 * Fills a region cells with the specific tile. The cells may be filled with
	 * a static tile index, an animated tile index, or they may be left empty
	 * (index <code>0</code>).
	 * 
	 * @param col
	 *            the column of top-left cell in the region
	 * @param row
	 *            the row of top-left cell in the region
	 * @param numCols
	 *            the number of columns in the region
	 * @param numRows
	 *            the number of rows in the region
	 * @param tileIndex
	 *            the Index of the tile to place in all cells in the specified
	 *            region
	 * @throws IndexOutOfBoundsException
	 *             if the rectangular region defined by the parameters extends
	 *             beyond the bounds of the <code>TiledLayer</code> grid
	 * @throws IllegalArgumentException
	 *             if <code>numCols</code> is less than zero
	 * @throws IllegalArgumentException
	 *             if <code>numRows</code> is less than zero
	 * @throws IndexOutOfBoundsException
	 *             if there is no tile with index <code>tileIndex</code>
	 * @see #setCell
	 * @see #getCell
	 */
	public void fillCells(int col, int row, int numCols, int numRows,
			int tileIndex) {

		if (col < 0 || col >= this.columns || row < 0 || row >= this.rows
				|| numCols < 0 || col + numCols > this.columns || numRows < 0
				|| row + numRows > this.rows) {
			throw new IndexOutOfBoundsException();
		}

		if (tileIndex > 0) {
			// do checks for static tile
			if (tileIndex >= numberOfTiles) {
				throw new IndexOutOfBoundsException();
			}
		} else if (tileIndex < 0) {
			// do animated tile index check
			if (anim_to_static == null || (-tileIndex) >= numOfAnimTiles) {
				throw new IndexOutOfBoundsException();
			}
		}

		for (int rowCount = row; rowCount < row + numRows; rowCount++) {
			for (int columnCount = col; columnCount < col + numCols; columnCount++) {
				cellMatrix[rowCount][columnCount] = tileIndex;
			}
		}
	}

	/**
	 * Gets the width of a single cell, in pixels.
	 * 
	 * @return the width in pixels of a single cell in the
	 *         <code>TiledLayer</code> grid
	 */
	public final int getCellWidth() {
		return cellWidth;
	}

	/**
	 * Gets the height of a single cell, in pixels.
	 * 
	 * @return the height in pixels of a single cell in the
	 *         <code>TiledLayer</code> grid
	 */
	public final int getCellHeight() {
		return cellHeight;
	}

	/**
	 * Gets the number of columns in the TiledLayer grid. The overall width of
	 * the TiledLayer, in pixels, may be obtained by calling {@link #getWidth}.
	 * 
	 * @return the width in columns of the <code>TiledLayer</code> grid
	 */
	public final int getColumns() {
		return columns;
	}

	/**
	 * Gets the number of rows in the TiledLayer grid. The overall height of the
	 * TiledLayer, in pixels, may be obtained by calling {@link #getHeight}.
	 * 
	 * @return the height in rows of the <code>TiledLayer</code> grid
	 */
	public final int getRows() {
		return rows;
	}

	/**
	 * create the Image Array.
	 * 
	 * @param image
	 *            Image to use for creating the static tile set
	 * @param noOfFrames
	 *            total number of frames
	 * @param tileWidth
	 *            The width, in pixels, of a single tile
	 * @param tileHeight
	 *            The height, in pixels, of a single tile
	 * @param maintainIndices
	 */

	private void createStaticSet(Bitmap image, int noOfFrames, int tileWidth,
			int tileHeight, boolean maintainIndices) {

		cellWidth = tileWidth;
		cellHeight = tileHeight;

		int imageW = image.getWidth();
		int imageH = image.getHeight();

		sourceImage = image;

		numberOfTiles = noOfFrames;
		tileSetX = new int[numberOfTiles];
		tileSetY = new int[numberOfTiles];

		if (!maintainIndices) {
			// populate cell matrix, all the indices are 0 to begin with
			for (rows = 0; rows < cellMatrix.length; rows++) {
				int totalCols = cellMatrix[rows].length;
				for (columns = 0; columns < totalCols; columns++) {
					cellMatrix[rows][columns] = 0;
				}
			}
			// delete animated tiles
			anim_to_static = null;
		}

		int currentTile = 1;

		for (int y = 0; y < imageH; y += tileHeight) {
			for (int x = 0; x < imageW; x += tileWidth) {

				tileSetX[currentTile] = x;
				tileSetY[currentTile] = y;

				currentTile++;
			}
		}
	}

	/**
	 * Draws the TiledLayer.
	 * 
	 * The entire TiledLayer is rendered subject to the clip region of the
	 * Graphics object. The TiledLayer's upper left corner is rendered at the
	 * TiledLayer's current position relative to the origin of the Graphics
	 * object. The current position of the TiledLayer's upper-left corner can be
	 * retrieved by calling {@link #getX()} and {@link #getY()}. The appropriate
	 * use of a clip region and/or translation allows an arbitrary region of the
	 * TiledLayer to be rendered.
	 * <p>
	 * If the TiledLayer's Image is mutable, the TiledLayer is rendered using
	 * the current contents of the Image.
	 * 
	 * @param g
	 *            the graphics object to draw the <code>TiledLayer</code>
	 * @throws NullPointerException
	 *             if <code>g</code> is <code>null</code>
	 */
	public final void paint(Canvas canvas) {

		if (canvas == null) {
			throw new NullPointerException();
		}

		if (visible) {
			int tileIndex = 0;

			// y-coordinate
			int ty = this.y;
			for (int row = 0; row < cellMatrix.length; row++, ty += cellHeight) {

				// reset the x-coordinate at the beginning of every row
				// x-coordinate to draw tile into
				int tx = this.x;
				int totalCols = cellMatrix[row].length;

				for (int column = 0; column < totalCols; column++, tx += cellWidth) {

					tileIndex = cellMatrix[row][column];
					// check the indices
					// if animated get the corresponding
					// static index from anim_to_static table
					if (tileIndex == 0) { // transparent tile
						continue;
					} else if (tileIndex < 0) {
						tileIndex = getAnimatedTile(tileIndex);
					}

					drawImage(canvas, tx, ty, sourceImage, tileSetX[tileIndex],
							tileSetY[tileIndex], cellWidth, cellHeight);

				}
			}
		}
	}
	
	private void drawImage(Canvas canvas, int x, int y,
			Bitmap bsrc, int sx, int sy, int w, int h) {
		Rect rect_src = new Rect();
		rect_src.left = sx;
		rect_src.right = sx + w;
		rect_src.top = sy;
		rect_src.bottom = sy + h;

		Rect rect_dst = new Rect();
		rect_dst.left = x;
		rect_dst.right = x + w;
		rect_dst.top = y;
		rect_dst.bottom = y + h;
		canvas.drawBitmap(bsrc, rect_src, rect_dst, null);

		rect_src = null;
		rect_dst = null;
	}

	/**
	 * Change the static tile set.
	 * <p>
	 * 
	 * Replaces the current static tile set with a new static tile set. See the
	 * constructor {@link #TiledLayer(int, int, Image, int, int)} for
	 * information on how the tiles are created from the image.
	 * <p>
	 * 
	 * If the new static tile set has as many or more tiles than the previous
	 * static tile set, the the animated tiles and cell contents will be
	 * preserve. If not, the contents of the grid will be cleared (all cells
	 * will contain index 0) and all animated tiles will be deleted.
	 * <P>
	 * 
	 * @param image
	 *            the <code>Image</code> to use for creating the static tile set
	 * @param tileWidth
	 *            the width in pixels of a single tile
	 * @param tileHeight
	 *            the height in pixels of a single tile
	 * @throws NullPointerException
	 *             if <code>image</code> is <code>null</code>
	 * @throws IllegalArgumentException
	 *             if <code>tileHeight</code> or <code>tileWidth</code> is less
	 *             than <code>1</code>
	 * @throws IllegalArgumentException
	 *             if the <code>image</code> width is not an integer multiple of
	 *             the <code>tileWidth</code>
	 * @throws IllegalArgumentException
	 *             if the <code>image</code> height is not an integer multiple
	 *             of the <code>tileHeight</code>
	 */
	public void setStaticTileSet(Bitmap image, int tileWidth, int tileHeight) {
		// if img is null img.getWidth() will throw NullPointerException
		if (tileWidth < 1 || tileHeight < 1
				|| ((image.getWidth() % tileWidth) != 0)
				|| ((image.getHeight() % tileHeight) != 0)) {
			throw new IllegalArgumentException();
		}
		setWidthImpl(columns * tileWidth);
		setHeightImpl(rows * tileHeight);

		int noOfFrames = (image.getWidth() / tileWidth)
				* (image.getHeight() / tileHeight);

		// the zero th index is left empty for transparent tile
		// so it is passed in createStaticSet as noOfFrames + 1

		if (noOfFrames >= (numberOfTiles - 1)) {
			// maintain static indices
			createStaticSet(image, noOfFrames + 1, tileWidth, tileHeight, true);
		} else {
			createStaticSet(image, noOfFrames + 1, tileWidth, tileHeight, false);
		}
	}

}
