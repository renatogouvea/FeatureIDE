/* FeatureIDE - An IDE to support feature-oriented software development
 * Copyright (C) 2005-2010  FeatureIDE Team, University of Magdeburg
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
package de.ovgu.featureide.fm.ui.actions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.internal.util.BundleUtility;
import org.osgi.framework.Bundle;

import de.ovgu.featureide.fm.core.FMCorePlugin;
import de.ovgu.featureide.fm.core.FeatureModel;
import de.ovgu.featureide.fm.core.io.guidsl.FeatureModelReader;
import de.ovgu.featureide.fm.core.io.guidsl.FeatureModelWriter;
import de.ovgu.featureide.fm.core.io.xml.XmlFeatureModelReader;
import de.ovgu.featureide.fm.core.io.xml.XmlFeatureModelWriter;
import de.ovgu.featureide.fm.ui.FMUIPlugin;


/**
 * Opens the currently selected feature model with GUIDSL.
 * 
 * @author Thomas Thuem
 */
@SuppressWarnings("restriction")
public class OpenWithGuidslAction implements IObjectActionDelegate {

	private ISelection selection;

	public OpenWithGuidslAction() {
	}

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
	}

	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;
	}

	public void run(IAction action) {
		if (selection instanceof IStructuredSelection) {
			for (Iterator<?> it = ((IStructuredSelection) selection).iterator(); it.hasNext();) {
				Object element = it.next();
				IFile file = null;
				if (element instanceof IFile) {
					file = (IFile) element;
				} else if (element instanceof IAdaptable) {
					file = (IFile) ((IAdaptable) element).getAdapter(IFile.class);
				}
				if (file != null) {
					openWithGuidsl(file);
				}
			}
		}
	}
	
	public static String getFileFromPlugin(String pluginId, String localPath) throws IOException {
        if (pluginId == null || localPath == null) {
            throw new IllegalArgumentException();
        }

        // if the bundle is not ready then there is no file
        Bundle bundle = Platform.getBundle(pluginId);
        if (!BundleUtility.isReady(bundle)) {
			return null;
		}

        // look for the file
        URL url = BundleUtility.find(bundle, localPath);
        url = FileLocator.toFileURL(url);
		return new Path(url.getPath()).toOSString();
    }

    private void openWithGuidsl(IFile modelfile) {
		try {
			String jakarta = getFileFromPlugin(FMCorePlugin.PLUGIN_ID, "lib/jakarta.jar");
			String guidsl = getFileFromPlugin(FMCorePlugin.PLUGIN_ID, "lib/guidsl.jar");
			String command = "java -cp \"" + jakarta + "\"";
			command += " -jar \"" + guidsl + "\"";
				
			
			
				FeatureModel fm = new FeatureModel();
				XmlFeatureModelReader fmReader = new XmlFeatureModelReader(fm);
				fmReader.readFromFile(modelfile);
				FeatureModelWriter fmWriter = new FeatureModelWriter(fm);
//				
//				if (fmWriter.hasConcreteCompounds()){
//					boolean proceed = MessageDialog
//					.openQuestion(new Shell(), "Warning!",
//							"The current feature model cannot be transformed due to concrete compounds! Proceed? (all compound features will be set as abstract)");
//					if (!proceed) {
//						return;
//					}
//				}

				//FileDialog fileDialog = new FileDialog(new Shell(),
						//SWT.SAVE);
				//fileDialog.setFileName("model.m");
				//fileDialog.setOverwrite(true);
				//String filepath = fileDialog.open();
				//if (filepath == null) return;
				
		
				//IFile outputFile = modelfile.getFullPath();
								
				//fmWriter.writeToFile(outputFile);
				
				//System.out.println("getLocation().toOsString [-3]: " +);

				//IWorkspace workspace= ResourcesPlugin.getWorkspace();
				//IPath location= Path.fromOSString(file.getAbsolutePath());
				
				//IFile file= modelfile.getParent().toString(); //getFilesForLocation(modelfile.getLocation().toOSString().substring(0, modelfile.getLocation().toOSString().length()-4));
				
				//modelfile.getLocation().toOSString().substring(0, modelfile.getLocation().toOSString().length()-4);


				
				
				//modelfile.getProject()
				
				
				
				// Parse XML to GUIDSL and save file as model.m
				String loc = modelfile.getLocation().toOSString();
				String dirpath = loc.substring(0, loc.length()-10); 
				String filepath = loc.substring(0,loc.length()-4) + ".m";
				File outputfile = new File(filepath);
				fmWriter.writeToFile(outputfile);
				
				command += " \"" + filepath + "\"";
				
				System.out.println(command);
				System.out.println(dirpath);
				
				execProcess(command, new File(dirpath));
				
				//modelfile.getFullPath()
				//IFile file = modelfile.getFullPath().removeLastSegments(1).append("model.m");
				//IFile file = modelfile.getParent().getFile(Path.fromOSString(filepath));
//				
//				
//				IFile file = model
//				IFile file= workspace.getRoot().getFilesForLocation(location);
//				
//				System.out.print(test );
//			

			
			// TODO: parse to guidsl
			//command += " \"" + modelfile.getLocation().toOSString() + "\"";
			//execProcess(command, modelfile.getParent().getLocation().toFile());
//			System.out.println(file.getFullPath());
//			command += " \"" + file.getFullPath() + "\"";
//			System.out.print(command);
//			//execProcess(command, file.getParent().getLocation().toFile());

		} catch (Exception e) {
			FMUIPlugin.getDefault().logError("Unable to start GUIDSL", e);
		}
//		try {
//			File pathfile = FMCorePlugin.getDefault().getStateLocation().append("guidslpath.txt").toFile();
//			String guidslJar = "";
//			boolean newLocation = false;
//			if (pathfile.exists()) {
//		        BufferedReader reader = new BufferedReader(new FileReader(pathfile));
//		        guidslJar = reader.readLine();
//		        reader.close();
//			}
//			while (guidslJar != null && !new File(guidslJar).exists()) {
//				newLocation = true;
//				FileDialog fd = new FileDialog(Display.getDefault().getActiveShell(), SWT.OPEN);
//		        fd.setText("Locate the guidsl.jar file");
//		        fd.setFilterExtensions(new String[] { "*.jar" });
//		        fd.setFileName(guidslJar);
//		        guidslJar = fd.open();
//			}
//			if (guidslJar != null && new File(guidslJar).exists()) { //guidslJar != null -> fixed ticket #56
//				if (newLocation) {
//					BufferedWriter writer = new BufferedWriter(new FileWriter(pathfile));
//					writer.write(guidslJar + "\r\n");
//					writer.close();
//				}
//				execProcess("java -jar \"" + guidslJar + "\" \"" + modelfile.getLocation().toOSString() + "\"", modelfile.getParent().getLocation().toFile());
//			}
//		} catch (IOException e) {
//			FMUIPlugin.getDefault().logError(e);
//		}
	}
	
	private final boolean showOutputs = true;

	private void execProcess(String command, File dir) throws IOException {
        System.out.println("Starting process: " + command);
        Process process = Runtime.getRuntime().exec(command, null, dir);
        String sys = System.getProperty("os.name");
        // #58 ,OS dependent Code for excuting commands,  Linux does not execute without a shell
        if (sys.equals("Linux")){
        	String[] cmd = new String[3];
        	 cmd[0] = "/bin/bash";
             cmd[1] = "-c";
             cmd[2] = command;
             process = Runtime.getRuntime().exec(cmd,null,dir);
        }
       
        BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
        BufferedReader error = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        
        long start = System.currentTimeMillis();
        int diff = 250;
        while (true) {
            try {
                String line;
                while ((line = input.readLine()) != null) {
                    if (showOutputs) {
                        System.out.println(">>>" + line);
                    }
                }
                while ((line = error.readLine()) != null) {
                    System.err.println(">>>" + line);
                }
                if (System.currentTimeMillis() - start > diff) {
                    start += diff;
                    System.out.print(".");
                }
                int exitValue = process.exitValue();
                System.out.println("...finished (exit=" + exitValue + ")!");
                if (exitValue != 0) {
                    throw new IOException(
                            "The process doesn't finish normally (exit=" + exitValue + ")!");
                }
                return;
            }
            catch (IllegalThreadStateException e) {
            }
        }
    }

}
