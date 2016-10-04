package navigator;

import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.*;
import java.util.List;
import java.util.regex.Pattern;

/**
 *
 */
public class Navigator {
    private static Pattern TEXT_FILE = Pattern.compile(".+\\.txt$", Pattern.CASE_INSENSITIVE);
    private static Pattern VALID_FILE = Pattern.compile("[\\w\\-\\. ]+");
    private static Pattern UNIX_OR_WIN_DIR = Pattern.compile(".*[\\\\/]$");

    private static ResourceBundle resBundle = ResourceBundle.getBundle("Navigator");

    private class FileTreeModel implements TreeModel {

        class FileNode {
            File fNode;
            private final List<FileNode> contentsCache = new ArrayList<>();
            private boolean cacheValid = false;

            private FileNode(File path) {
                fNode = path;
            }

            @Override
            public String toString() {
                return fNode.getName();
            }

            private void reloadContents() {
                cacheValid = false;
            }

            private List<FileNode> getContents() {
                if (!cacheValid) {
                    contentsCache.clear();
                    File[] fList = fNode.listFiles();
                    if (fList != null) {
                        Arrays.sort(fList, ((f1, f2) -> f1.getName().compareToIgnoreCase(f2.getName())));
                        for (File f : fList) if (f.isDirectory()) contentsCache.add(new FileNode(f));
                        for (File f : fList) if (f.isFile()) contentsCache.add(new FileNode(f));
                    }
                    cacheValid = true;
                }
                return Collections.unmodifiableList(contentsCache);
            }

            private boolean isDir() {
                return fNode.isDirectory();
            }
        }


        FileTreeModel(@NotNull File root) {
            this.root = new FileNode(root);
        }

        @Override
        @NotNull
        public Object getRoot() {
            return root;
        }

        private void replaceRoot(File newFRoot) {
            if (root.fNode.equals(newFRoot)) return;
            root.fNode = newFRoot;
            root.reloadContents();
            fireStructureChanged(null);
        }

        @Override
        public Object getChild(@NotNull Object parent, int index) {
            return ((FileNode) parent).getContents().get(index);
        }

        @Override
        public int getChildCount(@NotNull Object parent) {
            return ((FileNode) parent).getContents().size();
        }

        @Override
        public boolean isLeaf(@NotNull Object node) {
            return !((FileNode) node).isDir();
        }

        @Override
        public int getIndexOfChild(@NotNull Object parent, @NotNull Object child) {
            List<FileNode> contents = ((FileNode) parent).getContents();
            FileNode fChild = (FileNode) child;
            for (int i = 0; i < contents.size(); i++) {
                if (contents.get(i).fNode.equals(fChild.fNode)) return i;
            }
            return -1;
        }

        private FileNode root;
        private List<TreeModelListener> listeners = new LinkedList<>();

        private void fireStructureChanged(TreePath path) {
            TreeModelEvent event = new TreeModelEvent(this, path);
            for (TreeModelListener lis : listeners) {
                lis.treeStructureChanged(event);
            }
        }

        @Override
        public void valueForPathChanged(TreePath path, Object newValue) {
        }

        @Override
        public void addTreeModelListener(TreeModelListener l) {
            listeners.add(l);
        }

        @Override
        public void removeTreeModelListener(TreeModelListener l) {
            listeners.remove(l);
        }
    }

    private FileTreeModel myDirectoryStructure;

    private JEditorPane textEditor;
    private JPanel mainPanel;
    private JToolBar drivesBar;
    private JButton editButton;
    private JButton okButton;
    private JTree fileBrowser;
    private JTextField fileNameField;
    private JButton newFileButton;
    private JLabel statusMessage;
    private JLabel fileNameLabel;
    private JButton cancelButton;
    private JButton deleteButton;

    public Navigator() {
        newFileButton.addActionListener(this::fileCreateHandler);
        fileNameField.addActionListener(this::fileNameEditHandler);
        okButton.addActionListener(this::okButtonHandler);
        cancelButton.addActionListener(this::cancelButtonHandler);
        editButton.addActionListener(this::editButtonHandler);
        deleteButton.addActionListener(this::deleteButtonHandler);

        fileBrowser.setSelectionPath(new TreePath(myDirectoryStructure.getRoot()));
        setWorkMode(WorkMode.DIR);
    }

    public static void showAsMain() {
        JFrame frame = new JFrame("Navigator");
        frame.setContentPane(new Navigator().mainPanel);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void createUIComponents() {

        drivesBar = new JToolBar();
        File[] rootPaths = File.listRoots();

        for (File path : rootPaths) {
            String pathString = path.toString();
            JButton drvB = new JButton(pathString);
            drvB.setMinimumSize(new Dimension(50, 20));
            drvB.addActionListener(e -> driveClickedHandler(path));
            drivesBar.add(drvB);
        }

        myDirectoryStructure = new FileTreeModel(rootPaths[0]);
        fileBrowser = new JTree(myDirectoryStructure);
        fileBrowser.addTreeExpansionListener(new TreeExpansionListener() {
            @Override
            public void treeExpanded(TreeExpansionEvent event) {
                TreePath eventPath = event.getPath();
                ((FileTreeModel.FileNode) eventPath.getLastPathComponent()).reloadContents();
                myDirectoryStructure.fireStructureChanged(eventPath);
            }

            @Override
            public void treeCollapsed(TreeExpansionEvent event) {
            }
        });
        fileBrowser.addTreeSelectionListener(this::itemSelectedHandler);
    }

    private File currentTextFile;

    private void itemSelectedHandler(TreeSelectionEvent e) {
        File selectedFile = ((FileTreeModel.FileNode) e.getPath().getLastPathComponent()).fNode;
        if ((TEXT_FILE.matcher(selectedFile.getName()).matches()) && selectedFile.isFile()) {
            textEditor.setText(FileUtils.loadTextFile(selectedFile));
            if (!FileUtils.exceptionMessage.equals("")) {
                setStatusMessage();
                setWorkMode(WorkMode.DIR);
            } else {
                fileNameLabel.setText(selectedFile.getPath());
                currentTextFile = selectedFile;
                statusMessage.setText("");
                setWorkMode(WorkMode.TEXTFILE);
            }
        } else setWorkMode(WorkMode.DIR);
    }

    private void driveClickedHandler(File rootPath) {
        myDirectoryStructure.replaceRoot(rootPath);
        setWorkMode(WorkMode.DIR);
    }

    private void fileCreateHandler(ActionEvent e) {
        fileNameField.setText("");
        setWorkMode(WorkMode.CREATING);
    }

    private void fileNameEditHandler(ActionEvent e) {
        String fn = fileNameField.getText();
        okButton.setEnabled(false);
        if (VALID_FILE.matcher(fn).matches()) {
            fileNameField.setEnabled(false);
            okButton.setEnabled(true);
            String currPath = Optional.ofNullable((FileTreeModel.FileNode) fileBrowser.getLastSelectedPathComponent()).orElse((FileTreeModel.FileNode) myDirectoryStructure.getRoot()).fNode.getPath();
            if (!UNIX_OR_WIN_DIR.matcher(currPath).matches())
                currPath += "/";          // *nix-way, works in Windows too
            if (!fn.contains(".")) fn += ".txt";
            fileNameField.setText(currPath + fn);
        }
    }

    private void okButtonHandler(ActionEvent actionEvent) {
        switch (workMode) {
            case CREATING:
                File f = FileUtils.createTextFile(fileNameField.getText());
                setStatusMessage();
                if (FileUtils.exceptionMessage.equals("")) {
                    (Optional.ofNullable((FileTreeModel.FileNode) fileBrowser.getLastSelectedPathComponent()).orElse((FileTreeModel.FileNode) myDirectoryStructure.getRoot())).reloadContents();
                    myDirectoryStructure.fireStructureChanged(fileBrowser.getSelectionPath());
                }
                if (f != null) {
                    fileNameLabel.setText(f.getPath());
                    currentTextFile = f;
                    textEditor.setText(FileUtils.loadTextFile(f));
                    if (!FileUtils.exceptionMessage.equals(""))
                        setStatusMessage();     // Do not overwrite "File exists" message with empty string
                    else
                        setWorkMode(WorkMode.EDITING);
                }
                break;
            case EDITING:
                FileUtils.saveTextFile(currentTextFile, textEditor.getText());
                setStatusMessage();
                setWorkMode(WorkMode.TEXTFILE);
                break;
        }
    }

    private void cancelButtonHandler(ActionEvent actionEvent) {
        switch (workMode) {
            case CREATING:
                fileNameField.setText("");
                setWorkMode(WorkMode.DIR);
                break;
            case EDITING:
                fileBrowser.setSelectionPath(new TreePath(myDirectoryStructure.getRoot()));
                setWorkMode(WorkMode.DIR);
                break;
        }
        newFileButton.setEnabled(false);
    }

    private void editButtonHandler(ActionEvent actionEvent) {
        textEditor.setText(FileUtils.loadTextFile(currentTextFile));
        if (!FileUtils.exceptionMessage.equals("")) {
            TreePath parentDir = fileBrowser.getSelectionPath().getParentPath();
            if (parentDir != null) {
                ((FileTreeModel.FileNode) parentDir.getLastPathComponent()).reloadContents();
                myDirectoryStructure.fireStructureChanged(parentDir);
                fileBrowser.setSelectionPath(parentDir);
            }
            setStatusMessage();
        } else
            setWorkMode(WorkMode.EDITING);
    }

    private void deleteButtonHandler(ActionEvent actionEvent) {
        FileUtils.deleteFile(currentTextFile);
        if (FileUtils.exceptionMessage.equals("")) {
            TreePath parentDir = fileBrowser.getSelectionPath().getParentPath();
            ((FileTreeModel.FileNode) parentDir.getLastPathComponent()).reloadContents();
            myDirectoryStructure.fireStructureChanged(parentDir);
            fileBrowser.setSelectionPath(parentDir);
            setWorkMode(WorkMode.DIR);
        } else setStatusMessage();
    }

    enum WorkMode {DIR, TEXTFILE, EDITING, CREATING}

    WorkMode workMode;

    private void setStatusMessage() {
        if (FileUtils.exceptionMessage.equals("")) {
            statusMessage.setText("");
            return;
        }
        String s = resBundle.getString(FileUtils.exceptionMessage);
        if (!FileUtils.systemExceptionMessage.equals("")) s += " [" + FileUtils.systemExceptionMessage + "]";
        statusMessage.setText(s);
    }

    private void setWorkMode(WorkMode wm) {
        workMode = wm;
        switch (wm) {
            case DIR:
                fileNameField.setEnabled(false);
                textEditor.setEnabled(false);
                fileBrowser.setEnabled(true);

                editButton.setEnabled(false);
                newFileButton.setEnabled(true);
                okButton.setEnabled(false);
                cancelButton.setEnabled(false);
                deleteButton.setEnabled(false);

                textEditor.setText("");
                fileNameLabel.setText("");
                break;
            case TEXTFILE:
                fileNameField.setEnabled(false);
                textEditor.setEnabled(false);
                fileBrowser.setEnabled(true);

                editButton.setEnabled(true);
                newFileButton.setEnabled(false);
                okButton.setEnabled(false);
                cancelButton.setEnabled(false);
                deleteButton.setEnabled(true);
                break;
            case EDITING:
                fileNameField.setEnabled(false);
                textEditor.setEnabled(true);
                fileBrowser.setEnabled(false);

                editButton.setEnabled(false);
                newFileButton.setEnabled(false);
                okButton.setEnabled(true);
                cancelButton.setEnabled(true);
                textEditor.requestFocusInWindow();
                deleteButton.setEnabled(false);
                break;
            case CREATING:
                fileNameField.setEnabled(true);
                textEditor.setEnabled(true);
                textEditor.setText("");
                fileBrowser.setEnabled(true);

                editButton.setEnabled(false);
                newFileButton.setEnabled(false);
                cancelButton.setEnabled(true);
                deleteButton.setEnabled(false);
                fileNameLabel.setText("");
                fileNameField.requestFocusInWindow();
                break;
        }
    }

    /**
     * Just a runner method
     *
     * @param args Command-line parameters
     */
    public static void main(String[] args) {
        showAsMain();
    }
}
