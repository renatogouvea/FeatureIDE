/* FeatureIDE - An IDE to support feature-oriented software development
 * Copyright (C) 2005-2012  FeatureIDE team, University of Magdeburg
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
package de.ovgu.featureide.fm.core.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

import de.ovgu.featureide.fm.core.FMCorePlugin;
import de.ovgu.featureide.fm.core.FeatureModel;


/**
 * Default writer to be extended for each feature model format.
 * 
 * If IFile support is needed, the {@link FeatureModelWriterIFileWrapper} has to be used.
 * 
 * @author Thomas Thuem
 */
public abstract class AbstractFeatureModelWriter implements IFeatureModelWriter {

	/**
	 * the feature model to write out
	 */
	protected FeatureModel featureModel;
	
	public void setFeatureModel(FeatureModel featureModel) {
		this.featureModel = featureModel;
	}
	
	public FeatureModel getFeatureModel() {
		return featureModel;
	}
	
	public void writeToFile(File file) {
		FileOutputStream output = null;
		try {
			if (!file.exists()) file.createNewFile();
			output = new FileOutputStream(file);
			output.write(writeToString().getBytes(Charset.availableCharsets().get("UTF-8")));
			output.flush();
		} catch (IOException e) {
			FMCorePlugin.getDefault().logError(e);
		} finally {
			try {
				if (output != null) { 
					output.close();
				}
			} catch (IOException e) {
				FMCorePlugin.getDefault().logError(e);
			}
		}
	}
	
}
