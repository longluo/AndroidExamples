/**
 * <p>Title: Layer.java</p>
 *
 * <p>Description: Layer MIDP2.0</p>
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

import android.graphics.Canvas;

public abstract class Layer {

	/**
	 * position of layer in x offset
	 */
	int x; // = 0;

	/**
	 * position of layer in y offset
	 */
	int y; // = 0;

	/**
	 * width of layer
	 */
	int width; // = 0;

	/**
	 * height of layer
	 */
	int height; // = 0;

	/**
	 * If the Layer is visible it will be drawn when <code>paint</code> is
	 * called.
	 */
	boolean visible = true;

	/**
	 * Creates a new Layer with the specified dimensions.
	 * 
	 * This constructor is declared package scope to prevent developers from
	 * creating Layer subclasses
	 * 
	 * By default, a Layer is visible and its upper-left corner is positioned at
	 * (0,0).
	 * 
	 * @param width
	 *            The width of the layer, in pixels
	 * @param height
	 *            The height of the layer, in pixels
	 * 
	 */
	Layer(int width, int height) {
		setWidthImpl(width);
		setHeightImpl(height);
	}

	/**
	 * Sets this Layer's position such that its upper-left corner is located at
	 * (x,y) in the painter's coordinate system. A Layer is located at (0,0) by
	 * default. <br>
	 * 
	 * @param x
	 *            the horizontal position
	 * @param y
	 *            the vertical position
	 * @see #move
	 * @see #getX
	 * @see #getY
	 * 
	 */
	public void setPosition(int x, int y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Moves this Layer by the specified horizontal and vertical distances. <br>
	 * The Layer's coordinates are subject to wrapping if the passed parameters
	 * will cause them to exceed beyond Integer.MAX_VALUE or Integer.MIN_VALUE.
	 * 
	 * @param dx
	 *            the distance to move along horizontal axis (positive to the
	 *            right, negative to the left)
	 * @param dy
	 *            the distance to move along vertical axis (positive down,
	 *            negative up)
	 * @see #setPosition
	 * @see #getX
	 * @see #getY
	 * 
	 */
	public void move(int dx, int dy) {
		x += dx;
		y += dy;
	}

	/**
	 * Gets the horizontal position of this Layer's upper-left corner in the
	 * painter's coordinate system.
	 * <p>
	 * 
	 * @return the Layer's horizontal position.
	 * @see #getY
	 * @see #setPosition
	 * @see #move
	 * 
	 */
	public final int getX() {
		return x;
	}

	/**
	 * Gets the vertical position of this Layer's upper-left corner in the
	 * painter's coordinate system.
	 * <p>
	 * 
	 * @return the Layer's vertical position.
	 * @see #getX
	 * @see #setPosition
	 * @see #move
	 * 
	 */
	public final int getY() {
		return y;
	}

	/**
	 * Gets the current width of this layer, in pixels.
	 * 
	 * @return the width in pixels
	 * @see #getHeight
	 * 
	 **/
	public final int getWidth() {
		return width;
	}

	/**
	 * Gets the current height of this layer, in pixels.
	 * 
	 * @return the height in pixels
	 * @see #getWidth
	 * 
	 **/
	public final int getHeight() {
		return height;
	}

	/**
	 * Sets the visibility of this Layer. A visible Layer is rendered when its
	 * {@link #paint(Graphics)} method is called; an invisible Layer is not
	 * rendered.
	 * 
	 * @param visible
	 *            <code>true</code> to make the <code>Layer</code> visible,
	 *            <code>false</code> to make it invisible
	 * @see #isVisible
	 * 
	 */
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	/**
	 * Gets the visibility of this Layer.
	 * 
	 * @return <code>true</code> if the <code>Layer</code> is visible,
	 *         <code>false</code> if it is invisible.
	 * @see #setVisible
	 * 
	 */
	public final boolean isVisible() {
		return visible;
	}

	/**
	 * Paints this Layer if it is visible. The upper-left corner of the Layer is
	 * rendered at it's current (x,y) position relative to the origin of the
	 * provided Graphics object. Applications may make use of Graphics clipping
	 * and translation to control where the Layer is rendered and to limit the
	 * region that is rendered.
	 * <P>
	 * Implementations of this method are responsible for checking if this Layer
	 * is visible; this method does nothing if the Layer is not visible.
	 * <p>
	 * The attributes of the Graphics object (clip region, translation, drawing
	 * color, etc.) are not modified as a result of calling this method.
	 * 
	 * @param g
	 *            the graphics object for rendering the <code>Layer</code>
	 * @throws NullPointerException
	 *             if <code>g</code> is <code>null</code>
	 */
	public abstract void paint(Canvas canvas);

	/**
	 * Sets the current width of this layer, in pixels. The Layer's width is
	 * used to determine its bounds for rendering purposes.
	 * 
	 * @param width
	 *            The width in pixels
	 * @throws IllegalArgumentException
	 *             if the specified width is less than 0
	 * @see #setHeightImpl
	 * @see #getHeight
	 * @see #getWidth
	 * 
	 **/
	void setWidthImpl(int width) {
		if (width < 0) {
			throw new IllegalArgumentException();
		}
		this.width = width;
	}

	/**
	 * Sets the current height of this layer, in pixels. The Layer's height is
	 * used to determine its bounds for rendering purposes.
	 * 
	 * @param height
	 *            The height in pixels
	 * @throws IllegalArgumentException
	 *             if the specified height is less than 0
	 * @see #setWidthImpl
	 * @see #getHeight
	 * @see #getWidth
	 * 
	 **/
	void setHeightImpl(int height) {
		if (height < 0) {
			throw new IllegalArgumentException();
		}
		this.height = height;
	}

}
