package org.eclipse.gymnast.generators.embeddeddsl.ui.actions;

import java.util.HashMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.ui.texteditor.MarkerUtilities;

public class UtilMarkers {

	public static void createProblemMarker(IStatus st, IFile genModelFile, int howManyMarkersSoFar, String parentMsg) {
		if (howManyMarkersSoFar > 100) {
			return;
		}
		if (st.isOK()) {
			return;
		}
		String newParentMsg = st.getMessage();
		if (parentMsg != null && !parentMsg.equals("")) {
			newParentMsg = parentMsg + ". " + newParentMsg;
		}
		if (st.isMultiStatus()) {
			for (IStatus status : st.getChildren()) {
				createProblemMarker(status, genModelFile, howManyMarkersSoFar, newParentMsg);
			}
		} else{
			int stsv = st.getSeverity();
			boolean isWarning = stsv != IStatus.CANCEL && stsv != IStatus.ERROR;
			// FIXME how to determine start and offset 
			int offset = 0;
			int length = 1;
			String msg = newParentMsg;
			createMarker(genModelFile, isWarning, msg, offset, length);
			System.out.println(st.getMessage());
			howManyMarkersSoFar++;
		}
	}

	private static void createMarker(IFile file, boolean isWarning, String message, int offset, int length) {

		HashMap<String, Object> map = new HashMap<String, Object>();

		map.put(IMarker.MESSAGE, message);

		if (isWarning) {
			map.put(IMarker.SEVERITY, new Integer(IMarker.SEVERITY_WARNING));
		} else {
			map.put(IMarker.SEVERITY, new Integer(IMarker.SEVERITY_ERROR));
		}

		map.put(IMarker.CHAR_START, new Integer(offset));
		map.put(IMarker.CHAR_END, new Integer(offset + length));
		map.put(IMarker.TRANSIENT, new Boolean(true));

		try {
			MarkerUtilities.createMarker(file, map, getMarkerType());
		} catch (CoreException ex) {
			ex.printStackTrace();
		}
	}

	public static void clearMarkers(final IFile file) {
		try {
			file.deleteMarkers(getMarkerType(), true, IResource.DEPTH_INFINITE);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static String getMarkerType() {
		return IMarker.PROBLEM;
	}

}
