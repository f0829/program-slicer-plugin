package programslicerplugin.handlers;

import java.util.HashSet;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

import programslicerplugin.configs.DefaultStyle;
import slicer.Criterion;
import slicer.Slicer;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Display;

public class ForwardSliceHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);

		IWorkbenchPage iPage = window.getActivePage();
		IEditorPart iPart = iPage.getActiveEditor();
		IEditorInput input = iPart.getEditorInput();

		String pathname = null;
		
		if(input instanceof FileStoreEditorInput) {
			FileStoreEditorInput fStoreEditorInput = input.getAdapter(FileStoreEditorInput.class);
			pathname = fStoreEditorInput.getURI().getPath();
		} else if (input instanceof FileEditorInput) {
			FileEditorInput fEditorInput = input.getAdapter(FileEditorInput.class);
			pathname = fEditorInput.getURI().getPath();
		}

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

			List<Integer> slicedLines = null;

			try {
				Slicer slicer = new Slicer();
				HashSet<String> variableSet = new HashSet<>();
				variableSet.add("i");
				
				Criterion criterion = new Criterion(line, variableSet);
				slicedLines = slicer.getForwardSlice(pathname, criterion);
			} catch (Exception e) {
				// TODO: handle exception
				MessageDialog.openError(window.getShell(), "Error", "Internal Error Occured");
			}
			
			if (slicedLines == null) {
				MessageDialog.openError(window.getShell(), "Error", "Internal Error Occured");
				return null;
			}

			ITextViewer viewer = (ITextViewer) iPart.getAdapter(ITextOperationTarget.class);
			StyledText styledText = viewer.getTextWidget();
			DefaultStyle.DEFAULTSTYLEDTEXTRANGES = styledText.getStyleRanges();

			Display display = Display.getDefault();
			StyleRange style = new StyleRange();
			style.background = display.getSystemColor(SWT.COLOR_DARK_GRAY);
			style.foreground = display.getSystemColor(SWT.COLOR_YELLOW);
			style.fontStyle = SWT.BOLD;
			style.borderColor = display.getSystemColor(SWT.COLOR_WHITE);
			style.borderStyle = SWT.BORDER_SOLID;
			StyleRange[] styles = { style };

			for (Integer lineNum : slicedLines) {
				try {
					IRegion lineInfo = document.getLineInformation(lineNum-1);
					styledText.setStyleRanges(0, 0, new int[] {lineInfo.getOffset(),lineInfo.getLength()}, styles);
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					MessageDialog.openError(window.getShell(), "Error", "Internal Error Occured");

				}
			}

			MessageDialog.openInformation(window.getShell(), "Program Slicing", "Slicing Complete");

		}
		return null;
	}
}
