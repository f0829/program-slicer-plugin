package programslicerplugin.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import programslicerplugin.configs.DefaultStyle;

import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.swt.custom.StyledText;

public class ResetSlicingHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		
		IWorkbenchPage iPage = window.getActivePage();
		IEditorPart iPart = iPage.getActiveEditor();
		
		ITextViewer viewer = (ITextViewer) iPart.getAdapter(ITextOperationTarget.class);
		
		StyledText styledText = viewer.getTextWidget();
		styledText.setStyleRanges(DefaultStyle.DEFAULTSTYLEDTEXTRANGES);
		
		return null;
	}
}
