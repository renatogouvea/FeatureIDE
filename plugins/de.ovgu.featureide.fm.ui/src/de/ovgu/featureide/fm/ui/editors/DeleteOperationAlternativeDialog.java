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
package de.ovgu.featureide.fm.ui.editors;

import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

import de.ovgu.featureide.fm.core.Feature;
import de.ovgu.featureide.fm.core.FeatureModel;
import de.ovgu.featureide.fm.ui.FMUIPlugin;
import de.ovgu.featureide.fm.ui.editors.featuremodel.GUIDefaults;
import de.ovgu.featureide.fm.ui.editors.featuremodel.operations.FeatureDeleteOperation;

/**
 * TODO description
 * 
 * @author Florian Proksch
 */
public class DeleteOperationAlternativeDialog implements GUIDefaults 
{
	private Text errorMessage;
	
	private List<Feature> features;
	
	private Shell shell;
	private Feature feature;
	
	private FeatureModel featureModel;
	private Button okButton;
	private Combo featureCombo;
	private String titleText;
	
	
	public DeleteOperationAlternativeDialog(FeatureModel featureModel, Feature feature, List<Feature> foundFeatures)
	{
		features = foundFeatures;
		this.feature = feature; 
		this.featureModel = featureModel;
		
		initShell();
		initText();
		initCombo();
		initButtons();
		shell.open();	
	}

	private void initCombo()
	{		
		featureCombo = new Combo(shell, SWT.READ_ONLY);
	    featureCombo.setBounds(120, 20, 350, 65);
	    
	    for (Feature f : features)
	    {
	    	featureCombo.add(f.getName());
	    }	
	    featureCombo.setText(featureCombo.getItem(0));
	}
	
	private void initShell() {
		shell = new Shell(Display.getCurrent());
		titleText = "Replace \"" +  feature.getName() + "\" in constraints";
		shell.setText(titleText);
		shell.setImage(FEATURE_SYMBOL);
		shell.setSize(400, 250);
		GridLayout shellLayout = new GridLayout();
		shellLayout.marginWidth = 0;
		shellLayout.marginHeight = 0;
		shell.setLayout(shellLayout);

		
		
		Monitor primary = shell.getDisplay().getPrimaryMonitor();
		Rectangle bounds = primary.getBounds();
		Rectangle rect = shell.getBounds();
		int x = bounds.x + (bounds.width - rect.width) / 2;
		int y = bounds.y + (bounds.height - rect.height) / 2;
		shell.setLocation(x, y);
		shell.addListener(SWT.Traverse, new Listener() {
			public void handleEvent(Event event) {
				if (event.detail == SWT.TRAVERSE_ESCAPE) {
					shell.close();
				}
			}
		});
	}
	
	public void initText()
	{
		errorMessage = new Text(shell, SWT.MULTI);
		errorMessage.setEditable(false);
		errorMessage.setBackground(shell.getDisplay().getSystemColor(
				SWT.COLOR_WHITE));
		errorMessage.setBounds(20, 20, 380, 65);
		errorMessage.setText("Caution!\nThe feature you are about to delete is contained in several constraints.\nPlease select one of the following features in order to replace it.");
		
	}
		
	
	
	public void initButtons()
	{
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);

		Composite lastComposite = new Composite(shell, SWT.NONE);
		lastComposite.setLayoutData(gridData);

		FormLayout lastCompositeLayout = new FormLayout();
		lastCompositeLayout.marginHeight = 5;
		lastCompositeLayout.marginTop = 85;
		lastCompositeLayout.marginWidth = 5;
		lastComposite.setLayout(lastCompositeLayout);
		
		Button cancelButton = new Button(lastComposite, SWT.NONE);
		cancelButton.setText("Cancel");
		FormData formDataCancel = new FormData();
		formDataCancel.width = 70;
		formDataCancel.right = new FormAttachment(100, -5);
		formDataCancel.bottom = new FormAttachment(100, -5);
		cancelButton.setLayoutData(formDataCancel);
		
		
		okButton = new Button(lastComposite, SWT.NONE);
		okButton.setText("OK");
		FormData formDataOk = new FormData();
		formDataOk.width = 70;
		formDataOk.right = new FormAttachment(cancelButton, -5);
		formDataOk.bottom = new FormAttachment(100, -5);
		okButton.setLayoutData(formDataOk);
		okButton.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {

				closeShell();

			}

		});
		cancelButton.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(
					org.eclipse.swt.events.SelectionEvent e) {
				shell.dispose();
			}
		});		
	}

	/**
	 * 
	 */
	private void closeShell() 
	{
		AbstractOperation op = null;
		for (Feature f : features)
		{
			if (f.getName().equals(featureCombo.getText()))				
			{
				op = new FeatureDeleteOperation(featureModel, feature, true, f);
				break;
			}
		}
		if (op != null)
		{
			op.addContext((IUndoContext) featureModel.getUndoContext());
			try {
				PlatformUI.getWorkbench().getOperationSupport().getOperationHistory().execute(op, null, null);
			} catch (ExecutionException e) {
				FMUIPlugin.getDefault().logError(e);
			}
		}
		shell.dispose();
	
	}
	
	
}
