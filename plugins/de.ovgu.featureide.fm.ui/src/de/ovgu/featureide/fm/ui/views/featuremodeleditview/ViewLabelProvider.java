/* FeatureIDE - An IDE to support feature-oriented software development
 * Copyright (C) 2005-2011  FeatureIDE Team, University of Magdeburg
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see http://www.gnu.org/licenses/.
 *
 * See http://www.fosd.de/featureide/ for further information.
 */
package de.ovgu.featureide.fm.ui.views.featuremodeleditview;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import de.ovgu.featureide.fm.core.configuration.SelectableFeature;
import de.ovgu.featureide.fm.core.configuration.Selection;
import de.ovgu.featureide.fm.ui.FMUIPlugin;


/**
 * Returns a label and an image for a given tree element.
 * 
 * @author Thomas Thuem
 */
public class ViewLabelProvider extends LabelProvider {

	public static final Image IMAGE_ASELECTED = FMUIPlugin.getImage("aselected.ico");
	public static final Image IMAGE_ADESELECTED = FMUIPlugin.getImage("adeselected.ico");
	
	public ViewLabelProvider() {
	}
	
	public String getText(Object o) {
		return o.toString();
	}

	public Image getImage(Object o) {
		if (o instanceof TreeObject)
			return ((TreeObject) o).getImage();
		if (o instanceof SelectableFeature) {
			SelectableFeature feature = (SelectableFeature) o;
			return feature.getManual() == Selection.SELECTED ? IMAGE_ASELECTED
					: IMAGE_ADESELECTED;
		}
		return null;
	}
	
	@Override
	public void dispose() {
		IMAGE_ASELECTED.dispose();
		IMAGE_ADESELECTED.dispose();
		super.dispose();
	}

}
