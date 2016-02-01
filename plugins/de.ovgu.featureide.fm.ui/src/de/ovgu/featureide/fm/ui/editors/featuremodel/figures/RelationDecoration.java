/* FeatureIDE - A Framework for Feature-Oriented Software Development
 * Copyright (C) 2005-2015  FeatureIDE team, University of Magdeburg, Germany
 *
 * This file is part of FeatureIDE.
 * 
 * FeatureIDE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * FeatureIDE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with FeatureIDE.  If not, see <http://www.gnu.org/licenses/>.
 *
 * See http://featureide.cs.ovgu.de/ for further information.
 */
package de.ovgu.featureide.fm.ui.editors.featuremodel.figures;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.RotatableDecoration;
import org.eclipse.draw2d.Shape;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;

import de.ovgu.featureide.fm.core.base.util.tree.Tree;
import de.ovgu.featureide.fm.ui.editors.FeatureUIHelper;
import de.ovgu.featureide.fm.ui.editors.IGraphicalFeature;
import de.ovgu.featureide.fm.ui.editors.IGraphicalFeatureModel;
import de.ovgu.featureide.fm.ui.editors.featuremodel.GUIDefaults;
import de.ovgu.featureide.fm.ui.properties.FMPropertyManager;

/**
 * A decoration for a feature connection that indicates its group type.
 * 
 * @author Thomas Thuem
 */
public class RelationDecoration extends Shape implements RotatableDecoration, GUIDefaults {

	private final boolean fill;

	private Point referencePoint;

	private IGraphicalFeature lastChild;
	private Tree<IGraphicalFeature> children;

	private IGraphicalFeatureModel featureModel;

	public RelationDecoration(final boolean fill, final IGraphicalFeature lastChild) {
		super();
		
		this.fill = fill;
		this.lastChild = lastChild;
		if (lastChild == null) {
			children = null;
		} else {
			children = (Tree<IGraphicalFeature>) lastChild.getTree().getParent();
		}
		final Color decoratorForgroundColor = FMPropertyManager.getDecoratorForgroundColor();
		setForegroundColor(decoratorForgroundColor);
		setBackgroundColor(decoratorForgroundColor);
		setSize(TARGET_ANCHOR_DIAMETER, TARGET_ANCHOR_DIAMETER);
		if (lastChild != null) {
			featureModel = lastChild.getGraphicalModel();
		}
	}

	@Override
	public void setLocation(final Point p) {
		if (this instanceof LegendRelationDecoration) {
			super.setLocation(p.translate((-getBounds().width >> 1) + 1, -getBounds().height >> 1));
		}else {
			setSize(TARGET_ANCHOR_DIAMETER, TARGET_ANCHOR_DIAMETER);
			super.setLocation(p.translate((-getBounds().width >> 1), (-getBounds().height >> 1)));
		}
	}

	@Override
	public void setReferencePoint(final Point p) {
		referencePoint = p;
	}

	@Override
	protected void fillShape(final Graphics graphics) {	}

	@Override
	protected void outlineShape(final Graphics graphics) {
		drawShape(graphics);
	}

	private void drawShape(final Graphics graphics) {
		double minAngle = Double.MAX_VALUE;
		double maxAngle = Double.MIN_VALUE;

		final Rectangle r = new Rectangle(getBounds()).shrink(1,  1);

		final Point center = getBounds().getCenter();
		if (this instanceof LegendRelationDecoration) {
			maxAngle = calculateAngle(center, getFeatureLocation());
			minAngle = calculateAngle(center, referencePoint);
		} else {
			if (children != null && children.getNumberOfChildren() > 1) {
				for (final IGraphicalFeature curChild : children.getChildrenObjects()) {
					lastChild = curChild;
					if (!(lastChild.getObject().getStructure().isHidden() && !FeatureUIHelper.showHiddenFeatures(featureModel))) {
						final Point featureLocation = FeatureUIHelper.getSourceLocation(curChild);
						final double currentAngle = calculateAngle(center, featureLocation);
						if (currentAngle < minAngle) {
							minAngle = currentAngle;
						}
						if (currentAngle > maxAngle) {
							maxAngle = currentAngle;
						}
					}
				}
			} else {
				return;
			}
		}
		if (fill) {
			Draw2dHelper.fillArc(graphics, r, (int) minAngle, (int)(maxAngle - minAngle));
		} else {
			graphics.drawArc(r, (int) minAngle, (int) (maxAngle - minAngle));
		}
	}

	protected Point getFeatureLocation() {
		return FeatureUIHelper.getSourceLocation(lastChild);
	}

	protected int getTargetAnchorDiameter() {
		return TARGET_ANCHOR_DIAMETER;
	}


	private double calculateAngle(final Point point, final Point referencePoint) {
		int dx = referencePoint.x - point.x;
		int dy = referencePoint.y - point.y;
		return 360 - Math.round(Math.atan2(dy, dx) / Math.PI * 180);
	}

}
