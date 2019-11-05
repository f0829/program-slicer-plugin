package programslicerplugin.handlers;

import java.io.File;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;
import understand.AnalyzeUnderstand;
import understand.CFG;
import understand.CFGNode;
import understand.MySlicing;
import understand.Slicing;
import understand.UnderstandJavaSourceFiles;
import understand.VariableUsage;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Display;

public class BackwardSliceHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);

		IWorkbenchPage iPage = window.getActivePage();
		IEditorPart iPart = iPage.getActiveEditor();
		IEditorInput input = iPart.getEditorInput();

		FileStoreEditorInput fStoreEditorInput = input.getAdapter(FileStoreEditorInput.class);
		String pathname = fStoreEditorInput.getURI().getPath();

		InputDialog inputDialog = new InputDialog(window.getShell(), "Program Slicing", "Enter slicing line number",
				null, null);
		ITextEditor iTextEditor = (ITextEditor) iPage.getActiveEditor();
		IDocumentProvider iDocumentProvider = iTextEditor.getDocumentProvider();
		IDocument document = iDocumentProvider.getDocument(input);
		int totalLine = document.getNumberOfLines();

		if (inputDialog.open() == Window.OK) {
			Integer line = Integer.parseInt(inputDialog.getValue());
			if (line.intValue() > totalLine) {
				MessageDialog.openError(window.getShell(), "Error", "Invalid line number");
				return null;
			}

			File sourceFile = new File(pathname);
			String sourceDir = sourceFile.getParent();

			System.out.println("The following file is selected:");
			System.out.println(sourceFile);
			String timestamp = String.valueOf(new Date().getTime());

			String udbFile = sourceDir + File.separator + sourceFile.getName() + timestamp + ".udb";
			String useFile = sourceDir + File.separator + sourceFile.getName() + timestamp + ".use";
			String cfgFile = sourceDir + File.separator + sourceFile.getName() + timestamp + ".cfg";

			File indexFile = new File(udbFile);
			
			
			if (!indexFile.exists()) {
				try {
					UnderstandJavaSourceFiles.createAnalysisDB(sourceFile, udbFile);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				System.out.println("Filename already exists. Exiting...");
			}
			indexFile = new File(cfgFile);
			try {
				AnalyzeUnderstand.extractCFG(udbFile, cfgFile);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			indexFile = new File(useFile);
			try {
				AnalyzeUnderstand.extractVariableUse(udbFile, useFile);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			CFG cfg = AnalyzeUnderstand.reloadCFG(cfgFile);
			Collection<VariableUsage> variableUsages = AnalyzeUnderstand.reloadVariableUsage(useFile);
			Slicing backwardSlicing = new MySlicing(cfg, variableUsages);

			HashSet<CFGNode> cfgnodes = backwardSlicing.getSlicedNode(line.intValue());
			
			ITextViewer viewer = (ITextViewer) iPart.getAdapter(ITextOperationTarget.class);
			StyledText styledText = viewer.getTextWidget();
			
			Display display = Display.getDefault();
			StyleRange style = new StyleRange();
			style.background = display.getSystemColor(SWT.COLOR_DARK_GRAY);
			style.foreground = display.getSystemColor(SWT.COLOR_GREEN);
			style.fontStyle = SWT.BOLD;
			style.borderColor = display.getSystemColor(SWT.COLOR_WHITE);
			style.borderStyle = SWT.BORDER_SOLID;
			StyleRange[] styles = {style};
					
			for (Iterator<CFGNode> iterator2 = cfgnodes.iterator(); iterator2.hasNext();) {
				CFGNode cfgNode = (CFGNode) iterator2.next();

				for (int i = cfgNode.getLineStart(); i <= cfgNode.getLineEnd(); i++) {
					
					
					try {
						IRegion lineInfo = document.getLineInformation(i-1);
						styledText.setStyleRanges(0, 0, new int[] {lineInfo.getOffset(),lineInfo.getLength()}, styles);
					} catch (BadLocationException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
					
					
				}

			}

			MessageDialog.openInformation(window.getShell(), "Program Slicing", "Slicing Complete");


		}
		return null;
	}
}
