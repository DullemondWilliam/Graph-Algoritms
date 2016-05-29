package comp2402graphEditor;

import java.awt.event.*;

public class MainGraphEditor {

	public static void main(String args[]) {
		GraphEditorGUIView frame = new GraphEditorGUIView("Graph Editor");

		// Add the usual window listener (for closing ability)
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		frame.setVisible(true);
	}
}