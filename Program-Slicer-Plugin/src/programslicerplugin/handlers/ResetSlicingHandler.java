package programslicerplugin.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Display;

public class ResetSlicingHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		
		IWorkbenchPage iPage = window.getActivePage();
		IEditorPart iPart = iPage.getActiveEditor();
		IEditorInput input = iPart.getEditorInput();
		ITextEditor iTextEditor = (ITextEditor) iPage.getActiveEditor();
		IDocumentProvider iDocumentProvider = iTextEditor.getDocumentProvider();
		IDocument document = iDocumentProvider.getDocument(input);
		
		MessageDialog.openInformation(window.getShell(), "Program-Slicer-Plugin",
				"Hello! This plugin is for program slicing.");
		
		ITextViewer viewer = (ITextViewer) iPart.getAdapter(ITextOperationTarget.class);
		
		StyledText styledText = viewer.getTextWidget();
		
		Display display = Display.getDefault();
		StyleRange style = new StyleRange();
//		style.background = display.getSystemColor(SWT.COLOR_DARK_GRAY);
//		style.foreground = display.getSystemColor(SWT.COLOR_GREEN);
//		style.fontStyle = SWT.BOLD;
//		style.borderColor = display.getSystemColor(SWT.COLOR_WHITE);
//		style.borderStyle = SWT.BORDER_SOLID;
		StyleRange[] styles = {style};
		
		
		
		styledText.setStyleRanges(0, 0, new int[] {0,document.getLength()}, styles);

		
		return null;
	}
}