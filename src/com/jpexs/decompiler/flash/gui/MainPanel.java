/*
 *  Copyright (C) 2010-2022 JPEXS
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.jpexs.decompiler.flash.gui;

import com.jpexs.decompiler.flash.AbortRetryIgnoreHandler;
import com.jpexs.decompiler.flash.ApplicationInfo;
import com.jpexs.decompiler.flash.DecompilerPool;
import com.jpexs.decompiler.flash.EventListener;
import com.jpexs.decompiler.flash.ReadOnlyTagList;
import com.jpexs.decompiler.flash.SWF;
import com.jpexs.decompiler.flash.SWFBundle;
import com.jpexs.decompiler.flash.SWFSourceInfo;
import com.jpexs.decompiler.flash.abc.ABC;
import com.jpexs.decompiler.flash.abc.RenameType;
import com.jpexs.decompiler.flash.abc.ScriptPack;
import com.jpexs.decompiler.flash.abc.avm2.AVM2ConstantPool;
import com.jpexs.decompiler.flash.abc.avm2.deobfuscation.AbcMultiNameCollisionFixer;
import com.jpexs.decompiler.flash.abc.avm2.deobfuscation.DeobfuscationLevel;
import com.jpexs.decompiler.flash.abc.types.traits.Trait;
import com.jpexs.decompiler.flash.configuration.Configuration;
import com.jpexs.decompiler.flash.configuration.ConfigurationItem;
import com.jpexs.decompiler.flash.configuration.SwfSpecificConfiguration;
import com.jpexs.decompiler.flash.configuration.SwfSpecificCustomConfiguration;
import com.jpexs.decompiler.flash.dumpview.DumpInfo;
import com.jpexs.decompiler.flash.dumpview.DumpInfoSwfNode;
import com.jpexs.decompiler.flash.exporters.BinaryDataExporter;
import com.jpexs.decompiler.flash.exporters.FontExporter;
import com.jpexs.decompiler.flash.exporters.FrameExporter;
import com.jpexs.decompiler.flash.exporters.ImageExporter;
import com.jpexs.decompiler.flash.exporters.MorphShapeExporter;
import com.jpexs.decompiler.flash.exporters.MovieExporter;
import com.jpexs.decompiler.flash.exporters.PreviewExporter;
import com.jpexs.decompiler.flash.exporters.ShapeExporter;
import com.jpexs.decompiler.flash.exporters.SoundExporter;
import com.jpexs.decompiler.flash.exporters.SymbolClassExporter;
import com.jpexs.decompiler.flash.exporters.TextExporter;
import com.jpexs.decompiler.flash.exporters.commonshape.Matrix;
import com.jpexs.decompiler.flash.exporters.modes.BinaryDataExportMode;
import com.jpexs.decompiler.flash.exporters.modes.ButtonExportMode;
import com.jpexs.decompiler.flash.exporters.modes.FontExportMode;
import com.jpexs.decompiler.flash.exporters.modes.FrameExportMode;
import com.jpexs.decompiler.flash.exporters.modes.ImageExportMode;
import com.jpexs.decompiler.flash.exporters.modes.MorphShapeExportMode;
import com.jpexs.decompiler.flash.exporters.modes.MovieExportMode;
import com.jpexs.decompiler.flash.exporters.modes.ScriptExportMode;
import com.jpexs.decompiler.flash.exporters.modes.ShapeExportMode;
import com.jpexs.decompiler.flash.exporters.modes.SoundExportMode;
import com.jpexs.decompiler.flash.exporters.modes.SpriteExportMode;
import com.jpexs.decompiler.flash.exporters.modes.SymbolClassExportMode;
import com.jpexs.decompiler.flash.exporters.modes.TextExportMode;
import com.jpexs.decompiler.flash.exporters.script.AS2ScriptExporter;
import com.jpexs.decompiler.flash.exporters.script.AS3ScriptExporter;
import com.jpexs.decompiler.flash.exporters.settings.BinaryDataExportSettings;
import com.jpexs.decompiler.flash.exporters.settings.ButtonExportSettings;
import com.jpexs.decompiler.flash.exporters.settings.FontExportSettings;
import com.jpexs.decompiler.flash.exporters.settings.FrameExportSettings;
import com.jpexs.decompiler.flash.exporters.settings.ImageExportSettings;
import com.jpexs.decompiler.flash.exporters.settings.MorphShapeExportSettings;
import com.jpexs.decompiler.flash.exporters.settings.MovieExportSettings;
import com.jpexs.decompiler.flash.exporters.settings.ScriptExportSettings;
import com.jpexs.decompiler.flash.exporters.settings.ShapeExportSettings;
import com.jpexs.decompiler.flash.exporters.settings.SoundExportSettings;
import com.jpexs.decompiler.flash.exporters.settings.SpriteExportSettings;
import com.jpexs.decompiler.flash.exporters.settings.SymbolClassExportSettings;
import com.jpexs.decompiler.flash.exporters.settings.TextExportSettings;
import com.jpexs.decompiler.flash.exporters.swf.SwfJavaExporter;
import com.jpexs.decompiler.flash.exporters.swf.SwfXmlExporter;
import com.jpexs.decompiler.flash.flexsdk.MxmlcAs3ScriptReplacer;
import com.jpexs.decompiler.flash.gui.abc.ABCPanel;
import com.jpexs.decompiler.flash.gui.abc.ClassesListTreeModel;
import com.jpexs.decompiler.flash.gui.abc.DecompiledEditorPane;
import com.jpexs.decompiler.flash.gui.abc.DeobfuscationDialog;
import com.jpexs.decompiler.flash.gui.action.ActionPanel;
import com.jpexs.decompiler.flash.gui.controls.JPersistentSplitPane;
import com.jpexs.decompiler.flash.gui.dumpview.DumpTree;
import com.jpexs.decompiler.flash.gui.dumpview.DumpTreeModel;
import com.jpexs.decompiler.flash.gui.dumpview.DumpViewPanel;
import com.jpexs.decompiler.flash.gui.editor.LineMarkedEditorPane;
import com.jpexs.decompiler.flash.gui.helpers.ObservableList;
import com.jpexs.decompiler.flash.gui.player.FlashPlayerPanel;
import com.jpexs.decompiler.flash.gui.taglistview.TagListTree;
import com.jpexs.decompiler.flash.gui.taglistview.TagListTreeModel;
import com.jpexs.decompiler.flash.gui.tagtree.AbstractTagTree;
import com.jpexs.decompiler.flash.gui.tagtree.AbstractTagTreeModel;
import com.jpexs.decompiler.flash.gui.tagtree.TagTree;
import com.jpexs.decompiler.flash.gui.tagtree.TagTreeContextMenu;
import com.jpexs.decompiler.flash.gui.tagtree.TagTreeModel;
import com.jpexs.decompiler.flash.gui.timeline.TimelineViewPanel;
import com.jpexs.decompiler.flash.helpers.FileTextWriter;
import com.jpexs.decompiler.flash.helpers.Freed;
import com.jpexs.decompiler.flash.importers.AS2ScriptImporter;
import com.jpexs.decompiler.flash.importers.AS3ScriptImporter;
import com.jpexs.decompiler.flash.importers.As3ScriptReplacerFactory;
import com.jpexs.decompiler.flash.importers.As3ScriptReplacerInterface;
import com.jpexs.decompiler.flash.importers.BinaryDataImporter;
import com.jpexs.decompiler.flash.importers.FFDecAs3ScriptReplacer;
import com.jpexs.decompiler.flash.importers.ImageImporter;
import com.jpexs.decompiler.flash.importers.ScriptImporterProgressListener;
import com.jpexs.decompiler.flash.importers.ShapeImporter;
import com.jpexs.decompiler.flash.importers.SwfXmlImporter;
import com.jpexs.decompiler.flash.importers.SymbolClassImporter;
import com.jpexs.decompiler.flash.importers.TextImporter;
import com.jpexs.decompiler.flash.importers.svg.SvgImporter;
import com.jpexs.decompiler.flash.search.ABCSearchResult;
import com.jpexs.decompiler.flash.search.ActionSearchResult;
import com.jpexs.decompiler.flash.search.ScriptSearchResult;
import com.jpexs.decompiler.flash.tags.ABCContainerTag;
import com.jpexs.decompiler.flash.tags.DefineBinaryDataTag;
import com.jpexs.decompiler.flash.tags.DefineBitsJPEG3Tag;
import com.jpexs.decompiler.flash.tags.DefineBitsJPEG4Tag;
import com.jpexs.decompiler.flash.tags.DefineBitsTag;
import com.jpexs.decompiler.flash.tags.DefineShape2Tag;
import com.jpexs.decompiler.flash.tags.DefineSoundTag;
import com.jpexs.decompiler.flash.tags.DefineSpriteTag;
import com.jpexs.decompiler.flash.tags.DoActionTag;
import com.jpexs.decompiler.flash.tags.DoInitActionTag;
import com.jpexs.decompiler.flash.tags.EndTag;
import com.jpexs.decompiler.flash.tags.FileAttributesTag;
import com.jpexs.decompiler.flash.tags.JPEGTablesTag;
import com.jpexs.decompiler.flash.tags.MetadataTag;
import com.jpexs.decompiler.flash.tags.PlaceObjectTag;
import com.jpexs.decompiler.flash.tags.ShowFrameTag;
import com.jpexs.decompiler.flash.tags.Tag;
import com.jpexs.decompiler.flash.tags.TagInfo;
import com.jpexs.decompiler.flash.tags.UnknownTag;
import com.jpexs.decompiler.flash.tags.base.ASMSource;
import com.jpexs.decompiler.flash.tags.base.BoundedTag;
import com.jpexs.decompiler.flash.tags.base.ButtonTag;
import com.jpexs.decompiler.flash.tags.base.CharacterTag;
import com.jpexs.decompiler.flash.tags.base.DrawableTag;
import com.jpexs.decompiler.flash.tags.base.FontTag;
import com.jpexs.decompiler.flash.tags.base.ImageTag;
import com.jpexs.decompiler.flash.tags.base.MissingCharacterHandler;
import com.jpexs.decompiler.flash.tags.base.MorphShapeTag;
import com.jpexs.decompiler.flash.tags.base.PlaceObjectTypeTag;
import com.jpexs.decompiler.flash.tags.base.ShapeTag;
import com.jpexs.decompiler.flash.tags.base.SoundStreamHeadTypeTag;
import com.jpexs.decompiler.flash.tags.base.SoundTag;
import com.jpexs.decompiler.flash.tags.base.SymbolClassTypeTag;
import com.jpexs.decompiler.flash.tags.base.TextImportErrorHandler;
import com.jpexs.decompiler.flash.tags.base.TextTag;
import com.jpexs.decompiler.flash.tags.text.TextParseException;
import com.jpexs.decompiler.flash.timeline.DepthState;
import com.jpexs.decompiler.flash.timeline.Frame;
import com.jpexs.decompiler.flash.timeline.TagScript;
import com.jpexs.decompiler.flash.timeline.Timeline;
import com.jpexs.decompiler.flash.timeline.Timelined;
import com.jpexs.decompiler.flash.treeitems.FolderItem;
import com.jpexs.decompiler.flash.treeitems.HeaderItem;
import com.jpexs.decompiler.flash.treeitems.SWFList;
import com.jpexs.decompiler.flash.treeitems.TreeItem;
import com.jpexs.decompiler.flash.types.FILLSTYLE;
import static com.jpexs.decompiler.flash.types.FILLSTYLE.CLIPPED_BITMAP;
import com.jpexs.decompiler.flash.types.FILLSTYLEARRAY;
import com.jpexs.decompiler.flash.types.LINESTYLEARRAY;
import com.jpexs.decompiler.flash.types.MATRIX;
import com.jpexs.decompiler.flash.types.RECT;
import com.jpexs.decompiler.flash.types.SHAPEWITHSTYLE;
import com.jpexs.decompiler.flash.types.shaperecords.EndShapeRecord;
import com.jpexs.decompiler.flash.types.shaperecords.SHAPERECORD;
import com.jpexs.decompiler.flash.types.shaperecords.StraightEdgeRecord;
import com.jpexs.decompiler.flash.types.shaperecords.StyleChangeRecord;
import com.jpexs.decompiler.flash.types.sound.SoundFormat;
import com.jpexs.decompiler.flash.xfl.FLAVersion;
import com.jpexs.helpers.ByteArrayRange;
import com.jpexs.helpers.CancellableWorker;
import com.jpexs.helpers.Helper;
import com.jpexs.helpers.Path;
import com.jpexs.helpers.ProgressListener;
import com.jpexs.helpers.Reference;
import com.jpexs.helpers.SerializableImage;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import jsyntaxpane.DefaultSyntaxKit;

/**
 *
 * @author JPEXS
 */
public final class MainPanel extends JPanel implements TreeSelectionListener, SearchListener<TextTag>, Freed {

    private final MainFrame mainFrame;

    private final ObservableList<SWFList> swfs;

    private final JPanel welcomePanel;

    private final TimelineViewPanel timelineViewPanel;

    private final MainFrameStatusPanel statusPanel;

    private Thread taskThread;

    private final MainFrameMenu mainMenu;

    private final JProgressBar progressBar = new JProgressBar(0, 100);

    public TagTree tagTree;

    public DumpTree dumpTree;

    public TagListTree tagListTree;

    private final FlashPlayerPanel flashPanel;

    private final FlashPlayerPanel flashPanel2;

    private final JPanel contentPanel;

    private final JPanel displayPanel;

    public FolderPreviewPanel folderPreviewPanel;

    private boolean isWelcomeScreen = true;

    private static final String CARDPREVIEWPANEL = "Preview card";

    private static final String CARDFOLDERPREVIEWPANEL = "Folder preview card";

    private static final String CARDEMPTYPANEL = "Empty card";

    private static final String CARDDUMPVIEW = "Dump view";

    private static final String CARDACTIONSCRIPTPANEL = "ActionScript card";

    private static final String CARDACTIONSCRIPT3PANEL = "ActionScript3 card";

    private static final String CARDHEADER = "Header card";

    private static final String DETAILCARDAS3NAVIGATOR = "Traits list";

    private static final String DETAILCARDTAGINFO = "Tag information";

    private static final String DETAILCARDEMPTYPANEL = "Empty card";

    private static final String SPLIT_PANE1 = "SPLITPANE1";

    private static final String WELCOME_PANEL = "WELCOMEPANEL";

    private static final String TIMELINE_PANEL = "TIMELINEPANEL";

    private static final String RESOURCES_VIEW = "RESOURCES";

    private static final String DUMP_VIEW = "DUMP";

    private static final String TAGLIST_VIEW = "TAGLIST";

    private static final String TIMELINE_VIEW = "TIMELINE";

    private final JPersistentSplitPane splitPane1;

    private final JPersistentSplitPane splitPane2;

    private JPanel detailPanel;

    private JTextField filterField = new MyTextField("");

    private JPanel searchPanel;

    private ABCPanel abcPanel;

    private ActionPanel actionPanel;

    private final PreviewPanel previewPanel;

    private final HeaderInfoPanel headerPanel;

    private DumpViewPanel dumpViewPanel;

    private final JPanel treePanel;

    private final PreviewPanel dumpPreviewPanel;

    private final TagInfoPanel tagInfoPanel;

    private TreePanelMode treePanelMode;

    public TreeItem oldItem;

    private int currentView = VIEW_RESOURCES;

    public List<SearchResultsDialog> searchResultsDialogs = new ArrayList<>();

    private TagTreeContextMenu contextPopupMenu;

    private static final Logger logger = Logger.getLogger(MainPanel.class.getName());

    private Map<TreeItem, Set<Integer>> missingNeededCharacters = new WeakHashMap<>();

    private Thread calculateMissingNeededThread;

    private List<WeakReference<TreeItem>> orderedClipboard = new ArrayList<>();
    private Map<TreeItem, Boolean> clipboard = new WeakHashMap<>();

    private boolean clipboardCut = false;

    public void gcClipboard() {
        for (int i = orderedClipboard.size() - 1; i >= 0; i--) {
            WeakReference<TreeItem> ref = orderedClipboard.get(i);
            TreeItem item = ref.get();
            if (item != null) {
                if (item.getSwf() == null) {
                    orderedClipboard.remove(i);
                    clipboard.remove(item);
                }
            }
        }
    }

    public void emptyClipboard() {
        copyToClipboard(new ArrayList<>());
    }

    public void copyToClipboard(Collection<TreeItem> items) {
        orderedClipboard.clear();
        clipboard.clear();
        for (TreeItem item : items) {
            orderedClipboard.add(new WeakReference<>(item));
            clipboard.put(item, true);
        }
        clipboardCut = false;
    }

    public void cutToClipboard(Collection<TreeItem> items) {
        copyToClipboard(items);
        clipboardCut = true;
    }

    public boolean clipboardContains(TreeItem item) {
        return clipboard.containsKey(item);
    }

    public boolean clipboardEmpty() {
        return clipboard.isEmpty();
    }

    public Set<TreeItem> getClipboardContents() {
        Set<TreeItem> ret = new LinkedHashSet<>();
        for (WeakReference<TreeItem> ref : orderedClipboard) {
            TreeItem item = ref.get();
            if (item != null) {
                ret.add(item);
            }
        }
        return ret;
    }

    public boolean isClipboardCut() {
        return clipboardCut;
    }

    private class MyTreeSelectionModel extends DefaultTreeSelectionModel {

        private boolean isModified() {
            if (abcPanel != null && abcPanel.isEditing()) {
                abcPanel.tryAutoSave();
            }

            if (actionPanel != null && actionPanel.isEditing()) {
                actionPanel.tryAutoSave();
            }

            if (previewPanel.isEditing()) {
                previewPanel.tryAutoSave();
            }

            if (headerPanel.isEditing()) {
                headerPanel.tryAutoSave();
            }

            return (abcPanel != null && abcPanel.isEditing())
                    || (actionPanel != null && actionPanel.isEditing())
                    || previewPanel.isEditing() || headerPanel.isEditing();
        }

        @Override
        public void addSelectionPath(TreePath path) {
            if (isModified()) {
                return;
            }

            super.addSelectionPath(path);
        }

        @Override
        public void addSelectionPaths(TreePath[] paths) {
            if (isModified()) {
                return;
            }

            super.addSelectionPaths(paths);
        }

        @Override
        public void setSelectionPath(TreePath path) {
            if (isModified()) {
                return;
            }

            super.setSelectionPath(path);
        }

        @Override
        public void setSelectionPaths(TreePath[] pPaths) {
            if (isModified()) {
                return;
            }

            super.setSelectionPaths(pPaths);
        }

        @Override
        public void clearSelection() {
            if (isModified()) {
                return;
            }

            super.clearSelection();
        }

        public void setSelection(TreePath[] selection) {
            if (isModified()) {
                return;
            }

            this.selection = selection;
        }

        @Override
        public void removeSelectionPath(TreePath path) {
            if (isModified()) {
                return;
            }

            super.removeSelectionPath(path);
        }

        @Override
        public void removeSelectionPaths(TreePath[] paths) {
            if (isModified()) {
                return;
            }

            super.removeSelectionPaths(paths);
        }
    }

    public TagTreeContextMenu getContextPopupMenu() {
        return contextPopupMenu;
    }

    public void setPercent(int percent) {
        View.checkAccess();

        progressBar.setValue(percent);
        progressBar.setVisible(true);
    }

    public void hidePercent() {
        View.checkAccess();

        if (progressBar.isVisible()) {
            progressBar.setVisible(false);
        }
    }

    public MainFrame getMainFrame() {
        return mainFrame;
    }

    static {
        try {
            File.createTempFile("temp", ".swf").delete(); //First call to this is slow, so make it first
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    public void updateMenu() {
        mainMenu.updateComponents();
    }

    private static void addTab(JTabbedPane tabbedPane, Component tab, String title, Icon icon) {
        tabbedPane.add(tab);

        JLabel lbl = new JLabel(title);
        lbl.setIcon(icon);
        lbl.setIconTextGap(5);
        lbl.setHorizontalTextPosition(SwingConstants.RIGHT);

        tabbedPane.setTabComponentAt(tabbedPane.getTabCount() - 1, lbl);
    }

    public void setStatus(String s) {
        statusPanel.setStatus(s);
    }

    public void setWorkStatus(String s, CancellableWorker worker) {
        statusPanel.setWorkStatus(s, worker);
        mainMenu.updateComponents();
    }

    public CancellableWorker getCurrentWorker() {
        return statusPanel.getCurrentWorker();
    }

    private JPanel createWelcomePanel() {
        JPanel welcomePanel = new JPanel();
        welcomePanel.setLayout(new BoxLayout(welcomePanel, BoxLayout.Y_AXIS));
        JLabel welcomeToLabel = new JLabel(translate("startup.welcometo"));
        welcomeToLabel.setFont(welcomeToLabel.getFont().deriveFont(40));
        welcomeToLabel.setAlignmentX(0.5f);
        JPanel appNamePanel = new JPanel(new FlowLayout());
        JLabel jpLabel = new JLabel("JPEXS ");
        jpLabel.setAlignmentX(0.5f);
        jpLabel.setForeground(new Color(0, 0, 160));
        jpLabel.setFont(new Font("Tahoma", Font.BOLD, 50));
        jpLabel.setHorizontalAlignment(SwingConstants.CENTER);
        appNamePanel.add(jpLabel);

        JLabel ffLabel = new JLabel("Free Flash ");
        ffLabel.setAlignmentX(0.5f);
        ffLabel.setFont(new Font("Tahoma", Font.BOLD, 50));
        ffLabel.setHorizontalAlignment(SwingConstants.CENTER);
        appNamePanel.add(ffLabel);

        JLabel decLabel = new JLabel("Decompiler");
        decLabel.setAlignmentX(0.5f);
        decLabel.setForeground(Color.red);
        decLabel.setFont(new Font("Tahoma", Font.BOLD, 50));
        decLabel.setHorizontalAlignment(SwingConstants.CENTER);
        appNamePanel.add(decLabel);
        appNamePanel.setAlignmentX(0.5f);
        welcomePanel.add(Box.createGlue());
        welcomePanel.add(welcomeToLabel);
        welcomePanel.add(appNamePanel);
        JLabel startLabel = new JLabel(translate("startup.selectopen"));
        startLabel.setAlignmentX(0.5f);
        startLabel.setFont(startLabel.getFont().deriveFont(30));
        welcomePanel.add(startLabel);
        welcomePanel.add(Box.createGlue());
        return welcomePanel;
    }

    private JPanel createFolderPreviewCard() {
        JPanel folderPreviewCard = new JPanel(new BorderLayout());
        folderPreviewPanel = new FolderPreviewPanel(this, new ArrayList<>());
        folderPreviewCard.add(new FasterScrollPane(folderPreviewPanel), BorderLayout.CENTER);

        return folderPreviewCard;
    }

    private JPanel createDumpPreviewCard() {
        JPanel dumpViewCard = new JPanel(new BorderLayout());
        dumpViewPanel = new DumpViewPanel(dumpTree);
        dumpViewCard.add(new FasterScrollPane(dumpViewPanel), BorderLayout.CENTER);

        return dumpViewCard;
    }

    public String translate(String key) {
        return mainFrame.translate(key);
    }

    public MainPanel(MainFrame mainFrame, MainFrameMenu mainMenu, FlashPlayerPanel flashPanel, FlashPlayerPanel previewFlashPanel) {
        super();

        this.mainFrame = mainFrame;
        this.mainMenu = mainMenu;
        this.flashPanel = flashPanel;
        this.flashPanel2 = previewFlashPanel;

        mainFrame.setTitle(ApplicationInfo.applicationVerName);

        setLayout(new BorderLayout());
        swfs = new ObservableList<>();

        detailPanel = new JPanel();
        detailPanel.setLayout(new CardLayout());

        JPanel whitePanel = new JPanel();
        if (View.isOceanic()) {
            whitePanel.setBackground(Color.white);
        }
        detailPanel.add(whitePanel, DETAILCARDEMPTYPANEL);

        tagInfoPanel = new TagInfoPanel(this);
        detailPanel.add(tagInfoPanel, DETAILCARDTAGINFO);

        UIManager.getDefaults().put("TreeUI", BasicTreeUI.class.getName());
        tagTree = new TagTree(null, this);
        tagTree.addTreeSelectionListener(this);
        tagTree.setSelectionModel(new MyTreeSelectionModel());

        tagListTree = new TagListTree(null, this);
        tagListTree.addTreeSelectionListener(this);
        tagListTree.setSelectionModel(new MyTreeSelectionModel());

        DragSource dragSource = DragSource.getDefaultDragSource();
        dragSource.createDefaultDragGestureRecognizer(tagTree, DnDConstants.ACTION_COPY_OR_MOVE, new DragGestureListener() {
            @Override
            public void dragGestureRecognized(DragGestureEvent dge) {
                dge.startDrag(DragSource.DefaultCopyDrop, new Transferable() {
                    @Override
                    public DataFlavor[] getTransferDataFlavors() {
                        return new DataFlavor[]{DataFlavor.javaFileListFlavor};
                    }

                    @Override
                    public boolean isDataFlavorSupported(DataFlavor flavor) {
                        return flavor.equals(DataFlavor.javaFileListFlavor);
                    }

                    @Override
                    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
                        if (flavor.equals(DataFlavor.javaFileListFlavor)) {
                            List<File> files;
                            String tempDir = System.getProperty("java.io.tmpdir");
                            if (!tempDir.endsWith(File.separator)) {
                                tempDir += File.separator;
                            }
                            Random rnd = new Random();
                            tempDir += "ffdec" + File.separator + "export" + File.separator + System.currentTimeMillis() + "_" + rnd.nextInt(1000);
                            File fTempDir = new File(tempDir);
                            Path.createDirectorySafe(fTempDir);

                            File ftemp = new File(tempDir);
                            ExportDialog exd = new ExportDialog(Main.getDefaultDialogsOwner(), null);
                            try {
                                files = exportSelection(new GuiAbortRetryIgnoreHandler(), tempDir, exd);
                            } catch (InterruptedException ex) {
                                logger.log(Level.SEVERE, null, ex);
                                return null;
                            }

                            files.clear();

                            File[] fs = ftemp.listFiles();
                            files.addAll(Arrays.asList(fs));

                            Main.stopWork();

                            for (File f : files) {
                                f.deleteOnExit();
                            }
                            new File(tempDir).deleteOnExit();
                            return files;

                        }
                        return null;
                    }
                }, new DragSourceListener() {
                    @Override
                    public void dragEnter(DragSourceDragEvent dsde) {
                        enableDrop(false);
                    }

                    @Override
                    public void dragOver(DragSourceDragEvent dsde) {
                    }

                    @Override
                    public void dropActionChanged(DragSourceDragEvent dsde) {
                    }

                    @Override
                    public void dragExit(DragSourceEvent dse) {
                    }

                    @Override
                    public void dragDropEnd(DragSourceDropEvent dsde) {
                        enableDrop(true);
                    }
                });
            }
        });

        List<AbstractTagTree> trees = new ArrayList<>();
        trees.add(tagTree);
        trees.add(tagListTree);

        contextPopupMenu = new TagTreeContextMenu(trees, this);

        dumpTree = new DumpTree(null, this);
        dumpTree.addTreeSelectionListener(this);
        dumpTree.createContextMenu();

        currentView = Configuration.lastView.get();

        statusPanel = new MainFrameStatusPanel(this);
        add(statusPanel, BorderLayout.SOUTH);

        displayPanel = new JPanel(new CardLayout());

        DefaultSyntaxKit.initKit();
        previewPanel = new PreviewPanel(this, flashPanel);

        dumpPreviewPanel = new PreviewPanel(this, previewFlashPanel);
        dumpPreviewPanel.setReadOnly(true);

        displayPanel.add(previewPanel, CARDPREVIEWPANEL);
        displayPanel.add(createFolderPreviewCard(), CARDFOLDERPREVIEWPANEL);
        displayPanel.add(createDumpPreviewCard(), CARDDUMPVIEW);

        headerPanel = new HeaderInfoPanel();
        displayPanel.add(headerPanel, CARDHEADER);

        displayPanel.add(new JPanel(), CARDEMPTYPANEL);
        showCard(CARDEMPTYPANEL);

        searchPanel = new JPanel();
        searchPanel.setLayout(new BorderLayout());
        searchPanel.add(filterField, BorderLayout.CENTER);
        searchPanel.add(new JLabel(View.getIcon("search16")), BorderLayout.WEST);
        JLabel closeSearchButton = new JLabel(View.getIcon("cancel16"));
        closeSearchButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                closeTagTreeSearch();
            }
        });
        searchPanel.add(closeSearchButton, BorderLayout.EAST);
        searchPanel.setVisible(false);

        LazyCardLayout treePanelLayout = new LazyCardLayout();
        treePanelLayout.registerLayout(createResourcesViewCard(), RESOURCES_VIEW);
        treePanelLayout.registerLayout(createDumpViewCard(), DUMP_VIEW);
        treePanelLayout.registerLayout(createTagListViewCard(), TAGLIST_VIEW);
        treePanel = new JPanel(treePanelLayout);

        //treePanel.add(searchPanel, BorderLayout.SOUTH);
        //searchPanel.setVisible(false);
        filterField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void changedUpdate(DocumentEvent e) {
                warn();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                warn();
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                warn();
            }

            public void warn() {
                doFilter();
            }
        });

        //displayPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        splitPane2 = new JPersistentSplitPane(JSplitPane.VERTICAL_SPLIT, treePanel, detailPanel, Configuration.guiSplitPane2DividerLocationPercent);
        splitPane1 = new JPersistentSplitPane(JSplitPane.HORIZONTAL_SPLIT, splitPane2, displayPanel, Configuration.guiSplitPane1DividerLocationPercent);

        welcomePanel = createWelcomePanel();
        add(welcomePanel, BorderLayout.CENTER);

        timelineViewPanel = new TimelineViewPanel();

        contentPanel = new JPanel(new CardLayout());
        contentPanel.add(welcomePanel, WELCOME_PANEL);
        contentPanel.add(splitPane1, SPLIT_PANE1);
        contentPanel.add(timelineViewPanel, TIMELINE_PANEL);
        add(contentPanel);
        showContentPanelCard(WELCOME_PANEL);

        tagTree.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if ((e.getKeyCode() == 'F') && (e.isControlDown())) {
                    searchPanel.setVisible(true);
                    filterField.requestFocusInWindow();
                }
                if ((e.getKeyCode() == 'G') && (e.isControlDown())) {
                    SWF swf = getCurrentSwf();
                    if (swf != null) {
                        String val = "";
                        boolean valid;
                        int characterId = -1;
                        do {
                            val = ViewMessages.showInputDialog(MainPanel.this, translate("message.input.gotoCharacter"), translate("message.input.gotoCharacter.title"), val);
                            if (val == null) {
                                break;
                            }
                            try {
                                characterId = Integer.parseInt(val);
                            } catch (NumberFormatException nfe) {
                                characterId = -1;
                            }
                        } while (characterId <= 0);

                        if (characterId > 0) {
                            CharacterTag tag = swf.getCharacter(characterId);
                            if (tag == null) {
                                ViewMessages.showMessageDialog(MainPanel.this, translate("message.character.notfound").replace("%characterid%", "" + characterId), translate("error"), JOptionPane.ERROR_MESSAGE);
                            } else {
                                TreePath path = tagTree.getModel().getTreePath(tag);
                                if (path != null) {
                                    tagTree.setSelectionPath(path);
                                }
                            }
                        }
                    }
                }
            }
        });
        detailPanel.setVisible(false);

        updateUi();

        this.swfs.addCollectionChangedListener((e) -> {
            AbstractTagTreeModel ttm = tagTree.getModel();
            if (ttm != null) {
                if (getCurrentSwf() == null) {
                    tagTree.setSelectionPath(ttm.getTreePath(ttm.getRoot()));
                }
                ttm.updateSwfs(e);
                tagTree.expandRoot();
                tagTree.expandFirstLevelNodes();
            }
            ttm = tagListTree.getModel();
            if (ttm != null) {
                if (getCurrentSwf() == null) {
                    tagListTree.setSelectionPath(ttm.getTreePath(ttm.getRoot()));
                }
                ttm.updateSwfs(e);
                tagListTree.expandRoot();
                tagListTree.expandFirstLevelNodes();
            }

            DumpTreeModel dtm = dumpTree.getModel();
            if (dtm != null) {
                List<List<String>> expandedNodes = View.getExpandedNodes(dumpTree);
                dtm.updateSwfs();
                View.expandTreeNodes(dumpTree, expandedNodes);
                dumpTree.expandRoot();
                dumpTree.expandFirstLevelNodes();
            }

            if (swfs.isEmpty()) {
                tagTree.setUI(new BasicTreeUI() {
                    {
                        setHashColor(Color.gray);
                    }
                });
                dumpTree.setUI(new BasicTreeUI() {
                    {
                        setHashColor(Color.gray);
                    }
                });
                tagListTree.setUI(new BasicTreeUI() {
                    {
                        setHashColor(Color.gray);
                    }
                });
            }
        });

        //Opening files with drag&drop to main window
        enableDrop(true);
        calculateMissingNeededThread = new Thread("calculateMissingNeededThread") {
            @Override
            public void run() {
                while (true) {
                    calculateMissingNeededCharacters();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        return;
                    }
                }
            }
        };
        calculateMissingNeededThread.start();
    }

    public void closeTagTreeSearch() {
        View.checkAccess();

        filterField.setText("");
        doFilter();
        searchPanel.setVisible(false);
    }

    public void loadSwfAtPos(SWFList newSwfs, int index) {
        View.checkAccess();

        SWFList oldSwfList = swfs.get(index);

        List<SWF> allSwfs = new ArrayList<>();
        for (SWF s : oldSwfList.swfs) {
            allSwfs.add(s);
            Main.populateSwfs(s, allSwfs);
        }

        List<List<String>> expandedNodes = View.getExpandedNodes(tagTree);
        previewPanel.clear();
        swfs.set(index, newSwfs);

        for (SWF s : allSwfs) {
            s.clearTagSwfs();
            Main.searchResultsStorage.destroySwf(s);
        }
        SWF swf = newSwfs.size() > 0 ? newSwfs.get(0) : null;
        if (swf != null) {
            updateUi(swf);
        }

        gcClipboard();
        doFilter();
        reload(false);
        View.expandTreeNodes(tagTree, expandedNodes);
    }

    public void load(SWFList newSwfs, boolean first) {
        View.checkAccess();

        List<List<String>> expandedNodes = View.getExpandedNodes(getCurrentTree());
        previewPanel.clear();

        swfs.add(newSwfs);
        SWF swf = newSwfs.size() > 0 ? newSwfs.get(0) : null;
        if (swf != null) {
            updateUi(swf);
        }

        gcClipboard();

        doFilter();
        reload(false);
        View.expandTreeNodes(getCurrentTree(), expandedNodes);
    }

    public ABCPanel getABCPanel() {
        if (abcPanel == null) {
            abcPanel = new ABCPanel(this);
            displayPanel.add(abcPanel, CARDACTIONSCRIPT3PANEL);
            detailPanel.add(abcPanel.tabbedPane, DETAILCARDAS3NAVIGATOR);
        }

        return abcPanel;
    }

    public ActionPanel getActionPanel() {
        if (actionPanel == null) {
            actionPanel = new ActionPanel(MainPanel.this);
            displayPanel.add(actionPanel, CARDACTIONSCRIPTPANEL);
        }

        return actionPanel;
    }

    private void updateUi(final SWF swf) {
        View.checkAccess();

        List<ABCContainerTag> abcList = swf.getAbcList();

        boolean hasAbc = !abcList.isEmpty();

        if (hasAbc) {
            getABCPanel().setAbc(abcList.get(0).getABC());
        }

        if (isWelcomeScreen) {
            showContentPanelCard(SPLIT_PANE1);
            isWelcomeScreen = false;
        }

        mainMenu.updateComponents(swf);

        if (taskThread != null) {
            taskThread.interrupt();
        }

        if (Configuration._debugMode.get()) {
            Thread t = new Thread() {
                @Override
                public void run() {
                    while (!Thread.currentThread().isInterrupted()) {
                        DecompilerPool d = swf.getDecompilerPool();
                        statusPanel.setStatus(swf.getFileTitle() + " " + d.getStat());

                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException ex) {
                            break;
                        }
                    }
                }
            };

            t.start();
            taskThread = t;
        }
    }

    private void updateUi() {
        View.checkAccess();

        if (!isWelcomeScreen && swfs.isEmpty()) {
            showContentPanelCard(WELCOME_PANEL);
            isWelcomeScreen = true;
            closeTagTreeSearch();
        }

        mainFrame.setTitle(ApplicationInfo.applicationVerName);
        mainMenu.updateComponents(null);

        showView(getCurrentView());
    }

    private boolean closeConfirmation(SWFList swfList) {
        View.checkAccess();

        String message = swfList == null
                ? translate("message.confirm.closeAll")
                : translate("message.confirm.close").replace("{swfName}", swfList.toString());

        return ViewMessages.showConfirmDialog(this, message, translate("message.warning"), JOptionPane.OK_CANCEL_OPTION, Configuration.showCloseConfirmation, JOptionPane.OK_OPTION) == JOptionPane.OK_OPTION;
    }

    public boolean isModified() {
        for (SWFList swfList : swfs) {
            for (SWF swf : swfList) {
                if (swf.isModified()) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean closeAll(boolean showCloseConfirmation) {
        View.checkAccess();

        if (showCloseConfirmation && isModified()) {
            boolean closeConfirmResult = closeConfirmation(swfs.size() == 1 ? swfs.get(0) : null);
            if (!closeConfirmResult) {
                return false;
            }
        }

        List<SWFList> swfsLists = new ArrayList<>(swfs);

        for (SearchResultsDialog sr : searchResultsDialogs) {
            sr.setVisible(false);
        }
        searchResultsDialogs.clear();

        swfs.clear();
        oldItem = null;
        clear();
        updateUi();

        List<SWF> swfsToClose = new ArrayList<>();
        for (SWFList swfList : swfsLists) {
            swfsToClose.addAll(swfList);
            for (SWF swf : swfList) {
                Main.populateSwfs(swf, swfsToClose);
            }
        }

        for (SWF swf : swfsToClose) {
            swf.clearTagSwfs();
        }

        refreshTree();

        gcClipboard();
        mainMenu.updateComponents(null);
        previewPanel.clear();

        return true;
    }

    public boolean close(SWFList swfList) {
        View.checkAccess();

        boolean modified = false;
        for (SWF swf : swfList) {
            if (swf.isModified()) {
                modified = true;
            }
        }

        if (modified) {
            boolean closeConfirmResult = closeConfirmation(swfList);
            if (!closeConfirmResult) {
                return false;
            }
        }

        List<SWF> swfsToClose = new ArrayList<>();
        swfsToClose.addAll(swfList);
        for (SWF swf : swfList) {
            Main.populateSwfs(swf, swfsToClose);
        }

        for (int i = 0; i < searchResultsDialogs.size(); i++) {
            SearchResultsDialog sr = searchResultsDialogs.get(i);
            for (SWF swf : swfsToClose) {
                sr.removeSwf(swf);
            }
            if (sr.isEmpty()) {
                sr.setVisible(false);
                searchResultsDialogs.remove(i);
                i--;
            }
        }
        for (SWF swf : swfsToClose) {
            Main.searchResultsStorage.destroySwf(swf);
        }

        swfs.remove(swfList);
        oldItem = null;
        clear();
        updateUi();

        for (SWF swf : swfsToClose) {
            swf.clearTagSwfs();
        }

        refreshTree();

        gcClipboard();

        mainMenu.updateComponents(null);
        previewPanel.clear();
        dumpPreviewPanel.clear();
        return true;
    }

    private void enableDrop(boolean value) {
        if (value) {
            setDropTarget(new DropTarget() {
                @Override
                public synchronized void drop(DropTargetDropEvent dtde) {
                    try {
                        dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
                        @SuppressWarnings("unchecked")
                        List<File> droppedFiles = (List<File>) dtde.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                        if (!droppedFiles.isEmpty()) {
                            SWFSourceInfo[] sourceInfos = new SWFSourceInfo[droppedFiles.size()];
                            for (int i = 0; i < droppedFiles.size(); i++) {
                                sourceInfos[i] = new SWFSourceInfo(null, droppedFiles.get(i).getAbsolutePath(), null);
                            }
                            Main.openFile(sourceInfos, null);
                        }
                    } catch (UnsupportedFlavorException | IOException ex) {
                    }
                }
            });
        } else {
            setDropTarget(null);
        }
    }

    public void updateClassesList() {
        List<TreeItem> nodes = getASTreeNodes(tagTree);
        boolean updateNeeded = false;
        for (TreeItem n : nodes) {
            if (n instanceof ClassesListTreeModel) {
                ((ClassesListTreeModel) n).update();
                updateNeeded = true;
            }
        }

        refreshTree();

        if (updateNeeded) {
            tagTree.updateUI();
        }
    }

    public void doFilter() {
        View.checkAccess();

        List<TreeItem> nodes = getASTreeNodes(tagTree);
        tagTree.clearSelection();
        for (TreeItem n : nodes) {
            if (n instanceof ClassesListTreeModel) {
                String filterText = filterField.getText();
                ((ClassesListTreeModel) n).setFilter(filterText);
                TagTreeModel tm = tagTree.getModel();
                TreePath path = tm.getTreePath(n);
                tm.updateNode(path);
                if (!filterText.isEmpty()) {
                    View.expandTreeNodes(tagTree, path, true);
                }
            }
        }
    }

    public void renameIdentifier(SWF swf, String identifier) throws InterruptedException {
        String oldName = identifier;
        String newName = ViewMessages.showInputDialog(this, translate("rename.enternew"), oldName);
        if (newName != null) {
            if (!oldName.equals(newName)) {
                swf.renameAS2Identifier(oldName, newName);
                ViewMessages.showMessageDialog(this, translate("rename.finished.identifier"));
                updateClassesList();
                reload(true);
            }
        }
    }

    public void renameMultiname(List<ABCContainerTag> abcList, int multiNameIndex) {
        String oldName = "";
        AVM2ConstantPool constants = getABCPanel().abc.constants;
        if (constants.getMultiname(multiNameIndex).name_index > 0) {
            oldName = constants.getString(constants.getMultiname(multiNameIndex).name_index);
        }

        String newName = ViewMessages.showInputDialog(this, translate("rename.enternew"), oldName);
        if (newName != null) {
            if (!oldName.equals(newName)) {
                int mulCount = 0;
                for (ABCContainerTag cnt : abcList) {
                    ABC abc = cnt.getABC();
                    for (int m = 1; m < abc.constants.getMultinameCount(); m++) {
                        int ni = abc.constants.getMultiname(m).name_index;
                        String n = "";
                        if (ni > 0) {
                            n = abc.constants.getString(ni);
                        }
                        if (n.equals(oldName)) {
                            abc.renameMultiname(m, newName);
                            mulCount++;
                        }
                    }
                }

                int fmulCount = mulCount;
                View.execInEventDispatch(() -> {
                    ViewMessages.showMessageDialog(this, translate("rename.finished.multiname").replace("%count%", Integer.toString(fmulCount)));
                    if (abcPanel != null) {
                        abcPanel.reload();
                    }

                    updateClassesList();
                    reload(true);
                    ABCPanel abcPanel = getABCPanel();
                    abcPanel.hilightScript(abcPanel.getSwf(), abcPanel.decompiledTextArea.getScriptLeaf().getClassPath().toRawString());
                });
            }
        }
    }

    public List<TreeItem> getASTreeNodes(TagTree tree) {
        List<TreeItem> result = new ArrayList<>();
        TagTreeModel tm = (TagTreeModel) tree.getModel();
        if (tm == null) {
            return result;
        }
        TreeItem root = tm.getRoot();
        for (int i = 0; i < tm.getChildCount(root); i++) {
            // first level node can be SWF and SWFBundle
            TreeItem node = tm.getChild(root, i);
            if (node instanceof SWFBundle) {
                for (int j = 0; j < tm.getChildCount(node); j++) {
                    // child of SWFBundle should be SWF
                    SWF swfNode = (SWF) tm.getChild(node, j);
                    result.add(tm.getScriptsNode(swfNode));
                }
            } else if (node instanceof SWF) {
                SWF swfNode = (SWF) tm.getChild(root, i);
                result.add(tm.getScriptsNode(swfNode));
            }
        }
        return result;
    }

    public boolean confirmExperimental() {
        View.checkAccess();

        return ViewMessages.showConfirmDialog(this, translate("message.confirm.experimental"), translate("message.warning"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.OK_OPTION;
    }

    private List<TreeItem> getSelection(SWF swf) {
        if (currentView == MainPanel.VIEW_RESOURCES) {
            return tagTree.getSelection(swf);
        } else if (currentView == MainPanel.VIEW_TAGLIST) {
            return tagListTree.getSelection(swf);
        }
        return new ArrayList<>();
    }

    public List<File> exportSelection(AbortRetryIgnoreHandler handler, String selFile, ExportDialog export) throws IOException, InterruptedException {

        List<File> ret = new ArrayList<>();
        List<TreeItem> sel = getSelection(null);

        Set<SWF> usedSwfs = new HashSet<>();
        for (TreeItem d : sel) {
            SWF selectedNodeSwf = d.getSwf();
            if (!usedSwfs.contains(selectedNodeSwf)) {
                usedSwfs.add(selectedNodeSwf);
            }
        }

        Map<String, Integer> usedSwfsIds = new HashMap<>();
        for (SWF swf : usedSwfs) {
            List<ScriptPack> as3scripts = new ArrayList<>();
            List<Tag> images = new ArrayList<>();
            List<Tag> shapes = new ArrayList<>();
            List<Tag> morphshapes = new ArrayList<>();
            List<Tag> sprites = new ArrayList<>();
            List<Tag> buttons = new ArrayList<>();
            List<Tag> movies = new ArrayList<>();
            List<Tag> sounds = new ArrayList<>();
            List<Tag> texts = new ArrayList<>();
            List<TreeItem> as12scripts = new ArrayList<>();
            List<Tag> binaryData = new ArrayList<>();
            Map<Integer, List<Integer>> frames = new HashMap<>();
            List<Tag> fonts = new ArrayList<>();
            List<Tag> symbolNames = new ArrayList<>();

            for (TreeItem d : sel) {
                SWF selectedNodeSwf = d.getSwf();

                if (selectedNodeSwf != swf) {
                    continue;
                }

                if (d instanceof TagScript) {
                    Tag tag = ((TagScript) d).getTag();
                    if (tag instanceof DoActionTag || tag instanceof DoInitActionTag) {
                        as12scripts.add(d);
                    }
                }

                if (d instanceof Tag || d instanceof ASMSource) {
                    TreeNodeType nodeType = TagTree.getTreeNodeType(d);
                    if (nodeType == TreeNodeType.IMAGE) {
                        images.add((Tag) d);
                    }
                    if (nodeType == TreeNodeType.SHAPE) {
                        shapes.add((Tag) d);
                    }
                    if (nodeType == TreeNodeType.BUTTON) {
                        buttons.add((Tag) d);
                    }
                    if (nodeType == TreeNodeType.MORPH_SHAPE) {
                        morphshapes.add((Tag) d);
                    }
                    if (nodeType == TreeNodeType.SPRITE) {
                        sprites.add((Tag) d);
                    }
                    if ((nodeType == TreeNodeType.AS)
                            || (nodeType == TreeNodeType.AS_FRAME)
                            || (nodeType == TreeNodeType.AS_BUTTON)
                            || (nodeType == TreeNodeType.AS_CLIP)
                            || (nodeType == TreeNodeType.AS_INIT)
                            || (nodeType == TreeNodeType.AS_CLASS)) {
                        as12scripts.add(d);
                    }
                    if (nodeType == TreeNodeType.MOVIE) {
                        movies.add((Tag) d);
                    }
                    if (nodeType == TreeNodeType.SOUND) {
                        sounds.add((Tag) d);
                    }
                    if (nodeType == TreeNodeType.BINARY_DATA) {
                        binaryData.add((Tag) d);
                    }
                    if (nodeType == TreeNodeType.TEXT) {
                        texts.add((Tag) d);
                    }
                    if (nodeType == TreeNodeType.FONT) {
                        fonts.add((Tag) d);
                    }
                    if (nodeType == TreeNodeType.OTHER_TAG) {
                        if (d instanceof SymbolClassTypeTag) {
                            symbolNames.add((Tag) d);
                        }
                    }
                }

                if (d instanceof Frame) {
                    Frame fn = (Frame) d;
                    Timelined parent = fn.timeline.timelined;
                    int frame = fn.frame;
                    int parentId = 0;
                    if (parent instanceof CharacterTag) {
                        parentId = ((CharacterTag) parent).getCharacterId();
                    }
                    if (!frames.containsKey(parentId)) {
                        frames.put(parentId, new ArrayList<>());
                    }

                    frames.get(parentId).add(frame);
                }

                if (d instanceof ScriptPack) {
                    as3scripts.add((ScriptPack) d);
                }
            }

            for (Tag sprite : sprites) {
                frames.put(((DefineSpriteTag) sprite).getCharacterId(), null);
            }

            String selFile2;
            if (usedSwfs.size() > 1) {
                selFile2 = selFile + File.separator + Helper.getNextId(swf.getShortFileName(), usedSwfsIds);
            } else {
                selFile2 = selFile;
            }

            EventListener evl = swf.getExportEventListener();

            if (export.isOptionEnabled(ImageExportMode.class)) {
                ret.addAll(new ImageExporter().exportImages(handler, selFile2 + File.separator + ImageExportSettings.EXPORT_FOLDER_NAME, new ReadOnlyTagList(images),
                        new ImageExportSettings(export.getValue(ImageExportMode.class)), evl));
            }

            if (export.isOptionEnabled(ShapeExportMode.class)) {
                ret.addAll(new ShapeExporter().exportShapes(handler, selFile2 + File.separator + ShapeExportSettings.EXPORT_FOLDER_NAME, swf, new ReadOnlyTagList(shapes),
                        new ShapeExportSettings(export.getValue(ShapeExportMode.class), export.getZoom()), evl, export.getZoom()));
            }

            if (export.isOptionEnabled(MorphShapeExportMode.class)) {
                ret.addAll(new MorphShapeExporter().exportMorphShapes(handler, selFile2 + File.separator + MorphShapeExportSettings.EXPORT_FOLDER_NAME, new ReadOnlyTagList(morphshapes),
                        new MorphShapeExportSettings(export.getValue(MorphShapeExportMode.class), export.getZoom()), evl));
            }

            if (export.isOptionEnabled(TextExportMode.class)) {
                ret.addAll(new TextExporter().exportTexts(handler, selFile2 + File.separator + TextExportSettings.EXPORT_FOLDER_NAME, new ReadOnlyTagList(texts),
                        new TextExportSettings(export.getValue(TextExportMode.class), Configuration.textExportSingleFile.get(), export.getZoom()), evl));
            }

            if (export.isOptionEnabled(MovieExportMode.class)) {
                ret.addAll(new MovieExporter().exportMovies(handler, selFile2 + File.separator + MovieExportSettings.EXPORT_FOLDER_NAME, new ReadOnlyTagList(movies),
                        new MovieExportSettings(export.getValue(MovieExportMode.class)), evl));
            }

            if (export.isOptionEnabled(SoundExportMode.class)) {
                ret.addAll(new SoundExporter().exportSounds(handler, selFile2 + File.separator + SoundExportSettings.EXPORT_FOLDER_NAME, new ReadOnlyTagList(sounds),
                        new SoundExportSettings(export.getValue(SoundExportMode.class)), evl));
            }

            if (export.isOptionEnabled(BinaryDataExportMode.class)) {
                ret.addAll(new BinaryDataExporter().exportBinaryData(handler, selFile2 + File.separator + BinaryDataExportSettings.EXPORT_FOLDER_NAME, new ReadOnlyTagList(binaryData),
                        new BinaryDataExportSettings(export.getValue(BinaryDataExportMode.class)), evl));
            }

            if (export.isOptionEnabled(FontExportMode.class)) {
                ret.addAll(new FontExporter().exportFonts(handler, selFile2 + File.separator + FontExportSettings.EXPORT_FOLDER_NAME, new ReadOnlyTagList(fonts),
                        new FontExportSettings(export.getValue(FontExportMode.class)), evl));
            }

            if (export.isOptionEnabled(SymbolClassExportMode.class)) {
                ret.addAll(new SymbolClassExporter().exportNames(handler, selFile2 + File.separator + SymbolClassExportSettings.EXPORT_FOLDER_NAME, new ReadOnlyTagList(symbolNames),
                        new SymbolClassExportSettings(export.getValue(SymbolClassExportMode.class)), evl));
            }

            FrameExporter frameExporter = new FrameExporter();

            if (export.isOptionEnabled(FrameExportMode.class)) {
                FrameExportSettings fes = new FrameExportSettings(export.getValue(FrameExportMode.class), export.getZoom());
                if (frames.containsKey(0)) {
                    String subFolder = FrameExportSettings.EXPORT_FOLDER_NAME;
                    ret.addAll(frameExporter.exportFrames(handler, selFile2 + File.separator + subFolder, swf, 0, frames.get(0), fes, evl));
                }
            }

            if (export.isOptionEnabled(SpriteExportMode.class)) {
                SpriteExportSettings ses = new SpriteExportSettings(export.getValue(SpriteExportMode.class), export.getZoom());
                for (Entry<Integer, List<Integer>> entry : frames.entrySet()) {
                    int containerId = entry.getKey();
                    if (containerId != 0) {
                        String subFolder = SpriteExportSettings.EXPORT_FOLDER_NAME;
                        ret.addAll(frameExporter.exportSpriteFrames(handler, selFile2 + File.separator + subFolder, swf, containerId, entry.getValue(), ses, evl));
                    }
                }
            }

            if (export.isOptionEnabled(ButtonExportMode.class)) {
                ButtonExportSettings bes = new ButtonExportSettings(export.getValue(ButtonExportMode.class), export.getZoom());
                for (Tag tag : buttons) {
                    ButtonTag button = (ButtonTag) tag;
                    String subFolder = ButtonExportSettings.EXPORT_FOLDER_NAME;
                    ret.addAll(frameExporter.exportButtonFrames(handler, selFile2 + File.separator + subFolder, swf, button.getCharacterId(), null, bes, evl));
                }
            }

            if (export.isOptionEnabled(ScriptExportMode.class)) {
                if (as3scripts.size() > 0 || as12scripts.size() > 0) {
                    boolean parallel = Configuration.parallelSpeedUp.get();
                    String scriptsFolder = Path.combine(selFile2, ScriptExportSettings.EXPORT_FOLDER_NAME);
                    Path.createDirectorySafe(new File(scriptsFolder));
                    boolean singleScriptFile = Configuration.scriptExportSingleFile.get();
                    if (parallel && singleScriptFile) {
                        logger.log(Level.WARNING, AppStrings.translate("export.script.singleFilePallelModeWarning"));
                        singleScriptFile = false;
                    }

                    ScriptExportSettings scriptExportSettings = new ScriptExportSettings(export.getValue(ScriptExportMode.class), singleScriptFile, false);
                    String singleFileName = Path.combine(scriptsFolder, swf.getShortFileName() + scriptExportSettings.getFileExtension());
                    try ( FileTextWriter writer = scriptExportSettings.singleFile ? new FileTextWriter(Configuration.getCodeFormatting(), new FileOutputStream(singleFileName)) : null) {
                        scriptExportSettings.singleFileWriter = writer;
                        if (swf.isAS3()) {
                            ret.addAll(new AS3ScriptExporter().exportActionScript3(swf, handler, scriptsFolder, as3scripts, scriptExportSettings, parallel, evl));
                        } else {
                            Map<String, ASMSource> asmsToExport = swf.getASMs(true, as12scripts, false);
                            ret.addAll(new AS2ScriptExporter().exportAS2Scripts(handler, scriptsFolder, asmsToExport, scriptExportSettings, parallel, evl));
                        }
                    }
                }
            }
        }

        return ret;
    }

    public void exportAll(SWF swf, AbortRetryIgnoreHandler handler, String selFile, ExportDialog export) throws IOException, InterruptedException {
        boolean exportAll = false;
        if (exportAll) {
            exportAllDebug(swf, handler, selFile, export);
            return;
        }

        EventListener evl = swf.getExportEventListener();

        if (export.isOptionEnabled(ImageExportMode.class)) {
            new ImageExporter().exportImages(handler, Path.combine(selFile, ImageExportSettings.EXPORT_FOLDER_NAME), swf.getTags(),
                    new ImageExportSettings(export.getValue(ImageExportMode.class)), evl);
        }

        if (export.isOptionEnabled(ShapeExportMode.class)) {
            new ShapeExporter().exportShapes(handler, Path.combine(selFile, ShapeExportSettings.EXPORT_FOLDER_NAME), swf, swf.getTags(),
                    new ShapeExportSettings(export.getValue(ShapeExportMode.class), export.getZoom()), evl, export.getZoom());
        }

        if (export.isOptionEnabled(MorphShapeExportMode.class)) {
            new MorphShapeExporter().exportMorphShapes(handler, Path.combine(selFile, MorphShapeExportSettings.EXPORT_FOLDER_NAME), swf.getTags(),
                    new MorphShapeExportSettings(export.getValue(MorphShapeExportMode.class), export.getZoom()), evl);
        }

        if (export.isOptionEnabled(TextExportMode.class)) {
            new TextExporter().exportTexts(handler, Path.combine(selFile, TextExportSettings.EXPORT_FOLDER_NAME), swf.getTags(),
                    new TextExportSettings(export.getValue(TextExportMode.class), Configuration.textExportSingleFile.get(), export.getZoom()), evl);
        }

        if (export.isOptionEnabled(MovieExportMode.class)) {
            new MovieExporter().exportMovies(handler, Path.combine(selFile, MovieExportSettings.EXPORT_FOLDER_NAME), swf.getTags(),
                    new MovieExportSettings(export.getValue(MovieExportMode.class)), evl);
        }

        if (export.isOptionEnabled(SoundExportMode.class)) {
            new SoundExporter().exportSounds(handler, Path.combine(selFile, SoundExportSettings.EXPORT_FOLDER_NAME), swf.getTags(),
                    new SoundExportSettings(export.getValue(SoundExportMode.class)), evl);
        }

        if (export.isOptionEnabled(BinaryDataExportMode.class)) {
            new BinaryDataExporter().exportBinaryData(handler, Path.combine(selFile, BinaryDataExportSettings.EXPORT_FOLDER_NAME), swf.getTags(),
                    new BinaryDataExportSettings(export.getValue(BinaryDataExportMode.class)), evl);
        }

        if (export.isOptionEnabled(FontExportMode.class)) {
            new FontExporter().exportFonts(handler, Path.combine(selFile, FontExportSettings.EXPORT_FOLDER_NAME), swf.getTags(),
                    new FontExportSettings(export.getValue(FontExportMode.class)), evl);
        }

        if (export.isOptionEnabled(SymbolClassExportMode.class)) {
            new SymbolClassExporter().exportNames(handler, Path.combine(selFile, SymbolClassExportSettings.EXPORT_FOLDER_NAME), swf.getTags(),
                    new SymbolClassExportSettings(export.getValue(SymbolClassExportMode.class)), evl);
        }

        FrameExporter frameExporter = new FrameExporter();

        if (export.isOptionEnabled(FrameExportMode.class)) {
            FrameExportSettings fes = new FrameExportSettings(export.getValue(FrameExportMode.class), export.getZoom());
            frameExporter.exportFrames(handler, Path.combine(selFile, FrameExportSettings.EXPORT_FOLDER_NAME), swf, 0, null, fes, evl);
        }

        if (export.isOptionEnabled(SpriteExportMode.class)) {
            SpriteExportSettings ses = new SpriteExportSettings(export.getValue(SpriteExportMode.class), export.getZoom());
            for (CharacterTag c : swf.getCharacters().values()) {
                if (c instanceof DefineSpriteTag) {
                    frameExporter.exportSpriteFrames(handler, Path.combine(selFile, SpriteExportSettings.EXPORT_FOLDER_NAME), swf, c.getCharacterId(), null, ses, evl);
                }
            }
        }

        if (export.isOptionEnabled(ButtonExportMode.class)) {
            ButtonExportSettings bes = new ButtonExportSettings(export.getValue(ButtonExportMode.class), export.getZoom());
            for (CharacterTag c : swf.getCharacters().values()) {
                if (c instanceof ButtonTag) {
                    frameExporter.exportButtonFrames(handler, Path.combine(selFile, ButtonExportSettings.EXPORT_FOLDER_NAME), swf, c.getCharacterId(), null, bes, evl);
                }
            }
        }

        if (export.isOptionEnabled(ScriptExportMode.class)) {
            boolean parallel = Configuration.parallelSpeedUp.get();
            String scriptsFolder = Path.combine(selFile, ScriptExportSettings.EXPORT_FOLDER_NAME);
            Path.createDirectorySafe(new File(scriptsFolder));
            boolean singleScriptFile = Configuration.scriptExportSingleFile.get();
            if (parallel && singleScriptFile) {
                logger.log(Level.WARNING, AppStrings.translate("export.script.singleFilePallelModeWarning"));
                singleScriptFile = false;
            }

            ScriptExportSettings scriptExportSettings = new ScriptExportSettings(export.getValue(ScriptExportMode.class), singleScriptFile, false);
            String singleFileName = Path.combine(scriptsFolder, swf.getShortFileName() + scriptExportSettings.getFileExtension());
            try ( FileTextWriter writer = scriptExportSettings.singleFile ? new FileTextWriter(Configuration.getCodeFormatting(), new FileOutputStream(singleFileName)) : null) {
                scriptExportSettings.singleFileWriter = writer;
                swf.exportActionScript(handler, scriptsFolder, scriptExportSettings, parallel, evl);
            }
        }
    }

    public void exportAllDebug(SWF swf, AbortRetryIgnoreHandler handler, String selFile, ExportDialog export) throws IOException, InterruptedException {
        EventListener evl = swf.getExportEventListener();

        if (export.isOptionEnabled(ImageExportMode.class)) {
            for (ImageExportMode exportMode : ImageExportMode.values()) {
                new ImageExporter().exportImages(handler, Path.combine(selFile, ImageExportSettings.EXPORT_FOLDER_NAME, exportMode.name()), swf.getTags(),
                        new ImageExportSettings(exportMode), evl);
            }
        }

        if (export.isOptionEnabled(ShapeExportMode.class)) {
            for (ShapeExportMode exportMode : ShapeExportMode.values()) {
                new ShapeExporter().exportShapes(handler, Path.combine(selFile, ShapeExportSettings.EXPORT_FOLDER_NAME, exportMode.name()), swf, swf.getTags(),
                        new ShapeExportSettings(exportMode, export.getZoom()), evl, export.getZoom());
            }
        }

        if (export.isOptionEnabled(MorphShapeExportMode.class)) {
            for (MorphShapeExportMode exportMode : MorphShapeExportMode.values()) {
                new MorphShapeExporter().exportMorphShapes(handler, Path.combine(selFile, MorphShapeExportSettings.EXPORT_FOLDER_NAME, exportMode.name()), swf.getTags(),
                        new MorphShapeExportSettings(exportMode, export.getZoom()), evl);
            }
        }

        if (export.isOptionEnabled(TextExportMode.class)) {
            for (TextExportMode exportMode : TextExportMode.values()) {
                new TextExporter().exportTexts(handler, Path.combine(selFile, TextExportSettings.EXPORT_FOLDER_NAME, exportMode.name()), swf.getTags(),
                        new TextExportSettings(exportMode, Configuration.textExportSingleFile.get(), export.getZoom()), evl);
            }
        }

        if (export.isOptionEnabled(MovieExportMode.class)) {
            for (MovieExportMode exportMode : MovieExportMode.values()) {
                new MovieExporter().exportMovies(handler, Path.combine(selFile, MovieExportSettings.EXPORT_FOLDER_NAME, exportMode.name()), swf.getTags(),
                        new MovieExportSettings(exportMode), evl);
            }
        }

        if (export.isOptionEnabled(SoundExportMode.class)) {
            for (SoundExportMode exportMode : SoundExportMode.values()) {
                new SoundExporter().exportSounds(handler, Path.combine(selFile, SoundExportSettings.EXPORT_FOLDER_NAME, exportMode.name()), swf.getTags(),
                        new SoundExportSettings(exportMode), evl);
            }
        }

        if (export.isOptionEnabled(BinaryDataExportMode.class)) {
            for (BinaryDataExportMode exportMode : BinaryDataExportMode.values()) {
                new BinaryDataExporter().exportBinaryData(handler, Path.combine(selFile, BinaryDataExportSettings.EXPORT_FOLDER_NAME, exportMode.name()), swf.getTags(),
                        new BinaryDataExportSettings(exportMode), evl);
            }
        }

        if (export.isOptionEnabled(FontExportMode.class)) {
            for (FontExportMode exportMode : FontExportMode.values()) {
                new FontExporter().exportFonts(handler, Path.combine(selFile, FontExportSettings.EXPORT_FOLDER_NAME, exportMode.name()), swf.getTags(),
                        new FontExportSettings(exportMode), evl);
            }
        }

        if (export.isOptionEnabled(SymbolClassExportMode.class)) {
            for (SymbolClassExportMode exportMode : SymbolClassExportMode.values()) {
                new SymbolClassExporter().exportNames(handler, Path.combine(selFile, SymbolClassExportSettings.EXPORT_FOLDER_NAME, exportMode.name()), swf.getTags(),
                        new SymbolClassExportSettings(exportMode), evl);
            }
        }

        FrameExporter frameExporter = new FrameExporter();

        if (export.isOptionEnabled(FrameExportMode.class)) {
            for (FrameExportMode exportMode : FrameExportMode.values()) {
                FrameExportSettings fes = new FrameExportSettings(exportMode, export.getZoom());
                frameExporter.exportFrames(handler, Path.combine(selFile, FrameExportSettings.EXPORT_FOLDER_NAME, exportMode.name()), swf, 0, null, fes, evl);
            }
        }

        if (export.isOptionEnabled(SpriteExportMode.class)) {
            for (SpriteExportMode exportMode : SpriteExportMode.values()) {
                SpriteExportSettings ses = new SpriteExportSettings(exportMode, export.getZoom());
                for (CharacterTag c : swf.getCharacters().values()) {
                    if (c instanceof DefineSpriteTag) {
                        frameExporter.exportSpriteFrames(handler, Path.combine(selFile, SpriteExportSettings.EXPORT_FOLDER_NAME, exportMode.name()), swf, c.getCharacterId(), null, ses, evl);
                    }
                }
            }
        }

        if (export.isOptionEnabled(ButtonExportMode.class)) {
            for (ButtonExportMode exportMode : ButtonExportMode.values()) {
                ButtonExportSettings bes = new ButtonExportSettings(exportMode, export.getZoom());
                for (CharacterTag c : swf.getCharacters().values()) {
                    if (c instanceof ButtonTag) {
                        frameExporter.exportButtonFrames(handler, Path.combine(selFile, ButtonExportSettings.EXPORT_FOLDER_NAME, exportMode.name()), swf, c.getCharacterId(), null, bes, evl);
                    }
                }
            }
        }

        if (export.isOptionEnabled(ScriptExportMode.class)) {
            boolean parallel = Configuration.parallelSpeedUp.get();
            for (ScriptExportMode exportMode : ScriptExportMode.values()) {
                String scriptsFolder = Path.combine(selFile, ScriptExportSettings.EXPORT_FOLDER_NAME, exportMode.name());
                Path.createDirectorySafe(new File(scriptsFolder));
                boolean singleScriptFile = Configuration.scriptExportSingleFile.get();
                if (parallel && singleScriptFile) {
                    logger.log(Level.WARNING, AppStrings.translate("export.script.singleFilePallelModeWarning"));
                    singleScriptFile = false;
                }

                ScriptExportSettings scriptExportSettings = new ScriptExportSettings(exportMode, singleScriptFile, false);
                String singleFileName = Path.combine(scriptsFolder, swf.getShortFileName() + scriptExportSettings.getFileExtension());
                try ( FileTextWriter writer = scriptExportSettings.singleFile ? new FileTextWriter(Configuration.getCodeFormatting(), new FileOutputStream(singleFileName)) : null) {
                    scriptExportSettings.singleFileWriter = writer;
                    swf.exportActionScript(handler, scriptsFolder, scriptExportSettings, parallel, evl);
                }
            }
        }
    }

    public List<SWFList> getSwfs() {
        return swfs;
    }

    public SWFList getCurrentSwfList() {
        SWF swf = getCurrentSwf();
        if (swf == null) {
            return null;
        }

        return swf.swfList;
    }

    public SWF getCurrentSwf() {
        if (swfs == null || swfs.isEmpty()) {
            return null;
        }

        if (treePanelMode == TreePanelMode.TAG_TREE) {
            TreeItem treeNode = (TreeItem) tagTree.getLastSelectedPathComponent();
            if (treeNode == null || treeNode instanceof SWFList) {
                return null;
            }

            return treeNode.getSwf();
        } else if (treePanelMode == TreePanelMode.DUMP_TREE) {
            DumpInfo dumpInfo = (DumpInfo) dumpTree.getLastSelectedPathComponent();

            if (dumpInfo == null) {
                return null;
            }

            return DumpInfoSwfNode.getSwfNode(dumpInfo).getSwf();
        } else if (treePanelMode == TreePanelMode.TAGLIST_TREE) {
            TreeItem treeNode = (TreeItem) tagListTree.getLastSelectedPathComponent();
            if (treeNode == null || treeNode instanceof SWFList) {
                return null;
            }

            return treeNode.getSwf();
        }

        return null;
    }

    public AbstractTagTree getCurrentTree() {
        if (currentView == VIEW_RESOURCES) {
            return tagTree;
        }
        if (currentView == VIEW_TAGLIST) {
            return tagListTree;
        }
        return tagTree; //???
    }

    public void gotoFrame(int frame) {
        View.checkAccess();

        TreeItem treeItem = (TreeItem) getCurrentTree().getLastSelectedPathComponent();
        if (treeItem == null) {
            return;
        }
        if (treeItem instanceof Timelined) {
            Timelined t = (Timelined) treeItem;
            Frame f = tagTree.getModel().getFrame(treeItem.getSwf(), t, frame);
            if (f != null) {
                setTagTreeSelectedNode(getCurrentTree(), f);
            }
        }
    }

    public void gotoScriptLine(SWF swf, String scriptName, int line, int classIndex, int traitIndex, int methodIndex) {
        View.checkAccess();

        gotoScriptName(swf, scriptName);
        if (abcPanel != null) {
            if (Main.isDebugPCode()) {
                if (classIndex != -1) {
                    boolean classChanged = false;
                    if (abcPanel.decompiledTextArea.getClassIndex() != classIndex) {
                        abcPanel.decompiledTextArea.setClassIndex(classIndex);
                        classChanged = true;
                    }
                    if (traitIndex != -10 && (classChanged || abcPanel.decompiledTextArea.lastTraitIndex != traitIndex)) {
                        abcPanel.decompiledTextArea.gotoTrait(traitIndex);
                    }
                }
                abcPanel.detailPanel.methodTraitPanel.methodCodePanel.gotoInstrLine(line);
            } else {
                abcPanel.decompiledTextArea.gotoLine(line);
            }
        } else if (actionPanel != null) {
            if (Main.isDebugPCode()) {
                actionPanel.editor.gotoLine(line);
            } else {
                actionPanel.decompiledEditor.gotoLine(line);
            }
        }
        refreshBreakPoints();

    }

    public void refreshBreakPoints() {
        if (abcPanel != null) {
            abcPanel.decompiledTextArea.refreshMarkers();
            abcPanel.detailPanel.methodTraitPanel.methodCodePanel.refreshMarkers();
        }
        if (actionPanel != null) {
            actionPanel.decompiledEditor.refreshMarkers();
            actionPanel.editor.refreshMarkers();
        }
    }

    /*
     public void debuggerBreakAt(SWF swf, String cls, int line) {
     View.execInEventDispatchLater(new Runnable() {

     @Override
     public void run() {
     gotoClassLine(swf, cls, line);
     if (abcPanel != null) {
     abcPanel.decompiledTextArea.addColorMarker(line, DecompiledEditorPane.FG_IP_COLOR, DecompiledEditorPane.BG_IP_COLOR, DecompiledEditorPane.PRIORITY_IP);
     }
     }
     });

     }*/
    public void gotoScriptName(SWF swf, String scriptName) {
        View.checkAccess();

        if (swf == null) {
            return;
        }
        if (swf.isAS3()) {
            String rawScriptName = scriptName;
            if (rawScriptName.startsWith("#PCODE ")) {
                rawScriptName = rawScriptName.substring(rawScriptName.indexOf(';') + 1);
            }

            List<ABCContainerTag> abcList = swf.getAbcList();
            if (!abcList.isEmpty()) {
                ABCPanel abcPanel = getABCPanel();
                abcPanel.setAbc(abcList.get(0).getABC());
                abcPanel.hilightScript(swf, rawScriptName);
            }
        } else {
            String rawScriptName = scriptName;
            if (rawScriptName.startsWith("#PCODE ")) {
                rawScriptName = rawScriptName.substring("#PCODE ".length());
            }
            Map<String, ASMSource> asms = swf.getASMs(true);
            if (actionPanel != null && asms.containsKey(rawScriptName)) {
                actionPanel.setSource(asms.get(rawScriptName), true);
            }
        }
    }

    public void gotoDocumentClass(SWF swf) {
        View.checkAccess();

        if (swf == null) {
            return;
        }

        String documentClass = swf.getDocumentClass();
        if (documentClass != null && currentView != VIEW_DUMP) {
            List<ABCContainerTag> abcList = swf.getAbcList();
            if (!abcList.isEmpty()) {
                ABCPanel abcPanel = getABCPanel();
                for (ABCContainerTag c : abcList) {
                    if (c.getABC().findClassByName(documentClass) > -1) {
                        abcPanel.setAbc(c.getABC());
                        abcPanel.hilightScript(swf, documentClass);
                        break;
                    }
                }
            }
        }
    }

    public void disableDecompilationChanged() {
        View.checkAccess();

        clearAllScriptCache();

        if (abcPanel != null) {
            abcPanel.reload();
        }

        updateClassesList();
    }

    private void clearAllScriptCache() {
        for (SWFList swfList : swfs) {
            for (SWF swf : swfList) {
                swf.clearScriptCache();
            }
        }
    }

    public Set<SWF> getAllSwfs() {
        List<SWF> allSwfs = new ArrayList<>();
        for (SWFList slist : getSwfs()) {
            for (SWF s : slist.swfs) {
                allSwfs.add(s);
                Main.populateSwfs(s, allSwfs);
            }
        }
        return new LinkedHashSet<>(allSwfs);
    }

    private List<TreeItem> getAllSelected() {
        if (currentView == VIEW_RESOURCES) {
            return tagTree.getAllSelected();
        }
        if (currentView == VIEW_TAGLIST) {
            return tagListTree.getAllSelected();
        }
        return new ArrayList<>();
    }

    private List<TreeItem> getSelected() {
        if (currentView == VIEW_RESOURCES) {
            return tagTree.getSelected();
        }
        if (currentView == VIEW_TAGLIST) {
            return tagListTree.getSelected();
        }
        return new ArrayList<>();
    }

    public void searchInActionScriptOrText(Boolean searchInText, SWF swf, boolean useSelection) {
        View.checkAccess();

        Map<SWF, List<ScriptPack>> scopeAs3 = new LinkedHashMap<>();
        Map<SWF, Map<String, ASMSource>> swfToAllASMSourceMap = new HashMap<>();
        Map<SWF, Map<String, ASMSource>> scopeAs12 = new LinkedHashMap<>();

        Set<SWF> swfsUsed = new LinkedHashSet<>();

        List<TreeItem> allItems = getAllSelected();
        for (TreeItem t : allItems) {
            if (t instanceof ScriptPack) {
                ScriptPack sp = (ScriptPack) t;
                SWF s = sp.getSwf();
                if (!scopeAs3.containsKey(s)) {
                    scopeAs3.put(s, new ArrayList<>());
                }
                scopeAs3.get(s).add(sp);
                swfsUsed.add(s);
            }
            ASMSource as = null;
            if (t instanceof ASMSource) {
                as = (ASMSource) t;
            } else if (t instanceof TagScript) {
                TagScript ts = (TagScript) t;
                if (ts.getTag() instanceof ASMSource) {
                    as = (ASMSource) ts.getTag();
                }
            }
            if (as != null) {
                SWF s = as.getSourceTag().getSwf();
                String asId = null;
                Map<String, ASMSource> allSources;
                if (swfToAllASMSourceMap.containsKey(s)) {
                    allSources = swfToAllASMSourceMap.get(s);
                } else {
                    allSources = s.getASMs(false);
                    swfToAllASMSourceMap.put(s, allSources);
                }
                for (String path : allSources.keySet()) {
                    if (allSources.get(path) == as) {
                        asId = path;
                        break;
                    }
                }
                if (!scopeAs12.containsKey(s)) {
                    scopeAs12.put(s, new LinkedHashMap<>());
                }
                scopeAs12.get(s).put(asId, as);
                swfsUsed.add(s);
            }
        }

        List<TreeItem> items = getSelected();
        String selected;

        if (scopeAs12.isEmpty() && scopeAs3.isEmpty()) {
            selected = null;
        } else if (items.size() == 1) {
            selected = items.get(0).toString();
        } else if (items.isEmpty()) {
            selected = null;
        } else {
            selected = AppDialog.translateForDialog("scope.selection.items", SearchDialog.class).replace("%count%", "" + items.size());
        }

        SearchDialog searchDialog = new SearchDialog(getMainFrame().getWindow(), false, selected, useSelection);
        if (searchInText != null) {
            if (searchInText) {
                searchDialog.searchInTextsRadioButton.setSelected(true);
            } else {
                searchDialog.searchInASRadioButton.setSelected(true);
            }
        }

        if (searchDialog.showDialog() == AppDialog.OK_OPTION) {
            final String txt = searchDialog.searchField.getText();
            if (!txt.isEmpty()) {

                if (searchDialog.getCurrentScope() == SearchDialog.SCOPE_CURRENT_FILE) {
                    scopeAs3.clear();
                    scopeAs12.clear();
                    if (swf.isAS3()) {
                        scopeAs3.put(swf, swf.getAS3Packs());
                    } else {
                        scopeAs12.put(swf, swf.getASMs(false));
                    }
                    swfsUsed.clear();
                    swfsUsed.add(swf);
                }
                if (searchDialog.getCurrentScope() == SearchDialog.SCOPE_ALL_FILES) {
                    Set<SWF> allSwfs = getAllSwfs();

                    for (SWF s : allSwfs) {
                        if (s.isAS3()) {
                            scopeAs3.put(s, s.getAS3Packs());
                        } else {
                            scopeAs12.put(s, s.getASMs(false));
                        }
                    }
                    swfsUsed.clear();
                    swfsUsed.addAll(allSwfs);
                }

                if (!scopeAs3.isEmpty()) {
                    getABCPanel();
                }
                if (!scopeAs12.isEmpty()) {
                    getActionPanel();
                }

                boolean ignoreCase = searchDialog.ignoreCaseCheckBox.isSelected();
                boolean regexp = searchDialog.regexpCheckBox.isSelected();

                boolean scriptSearch = searchDialog.searchInASRadioButton.isSelected()
                        || searchDialog.searchInPCodeRadioButton.isSelected();
                if (scriptSearch) {
                    boolean pCodeSearch = searchDialog.searchInPCodeRadioButton.isSelected();
                    new CancellableWorker<Void>() {
                        @Override
                        protected Void doInBackground() throws Exception {

                            List<ScriptSearchResult> fResult = new ArrayList<>();
                            for (SWF s : swfsUsed) {
                                if (scopeAs3.containsKey(s)) {
                                    List<ABCSearchResult> abcResult = getABCPanel().search(s, txt, ignoreCase, regexp, pCodeSearch, this, scopeAs3.get(s));
                                    fResult.addAll(abcResult);
                                    if (!abcResult.isEmpty()) {
                                        Main.searchResultsStorage.addABCResults(s, txt, ignoreCase, regexp, abcResult);
                                    }
                                }
                                if (scopeAs12.containsKey(s)) {
                                    List<ActionSearchResult> actionResult = getActionPanel().search(s, txt, ignoreCase, regexp, pCodeSearch, this, scopeAs12.get(s));
                                    fResult.addAll(actionResult);
                                    if (!actionResult.isEmpty()) {
                                        Main.searchResultsStorage.addActionResults(s, txt, ignoreCase, regexp, actionResult);
                                    }
                                }
                            }
                            Main.searchResultsStorage.finishGroup();

                            View.execInEventDispatch(() -> {
                                boolean found = false;
                                found = true;
                                List<SearchListener<ScriptSearchResult>> listeners = new ArrayList<>();
                                listeners.add(getABCPanel());
                                listeners.add(getActionPanel());
                                SearchResultsDialog<ScriptSearchResult> sr = new SearchResultsDialog<>(getMainFrame().getWindow(), txt, ignoreCase, regexp, listeners);
                                sr.setResults(fResult);
                                sr.setVisible(true);
                                searchResultsDialogs.add(sr);
                                if (!found) {
                                    ViewMessages.showMessageDialog(MainPanel.this, translate("message.search.notfound").replace("%searchtext%", txt), translate("message.search.notfound.title"), JOptionPane.INFORMATION_MESSAGE);
                                }

                                Main.stopWork();
                            });

                            return null;
                        }

                        @Override
                        protected void done() {
                            View.execInEventDispatch(() -> {
                                Main.stopWork();
                            });

                        }

                    }.execute();
                } else if (searchDialog.searchInTextsRadioButton.isSelected()) {
                    new CancellableWorker<Void>() {
                        @Override
                        protected Void doInBackground() throws Exception {
                            List<TextTag> textResult;
                            SearchPanel<TextTag> textSearchPanel = previewPanel.getTextPanel().getSearchPanel();
                            textSearchPanel.setOptions(ignoreCase, regexp);
                            textResult = searchText(txt, ignoreCase, regexp, swf);

                            List<TextTag> fTextResult = textResult;
                            View.execInEventDispatch(() -> {
                                textSearchPanel.setSearchText(txt);
                                boolean found = textSearchPanel.setResults(fTextResult);
                                if (!found) {
                                    ViewMessages.showMessageDialog(MainPanel.this, translate("message.search.notfound").replace("%searchtext%", txt), translate("message.search.notfound.title"), JOptionPane.INFORMATION_MESSAGE);
                                }

                                Main.stopWork();
                            });

                            return null;
                        }

                        @Override
                        protected void done() {
                            View.execInEventDispatch(() -> {
                                Main.stopWork();
                            });

                        }
                    }.execute();
                }
            }
        }
    }

    public void replaceText() {
        SearchDialog replaceDialog = new SearchDialog(getMainFrame().getWindow(), true, null, false);
        if (replaceDialog.showDialog() == AppDialog.OK_OPTION) {
            final String txt = replaceDialog.searchField.getText();
            if (!txt.isEmpty()) {
                final SWF swf = getCurrentSwf();

                new CancellableWorker() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        int findCount = 0;
                        boolean ignoreCase = replaceDialog.ignoreCaseCheckBox.isSelected();
                        boolean regexp = replaceDialog.regexpCheckBox.isSelected();
                        String replacement = replaceDialog.replaceField.getText();
                        if (!regexp) {
                            replacement = Matcher.quoteReplacement(replacement);
                        }
                        Pattern pat;
                        if (regexp) {
                            pat = Pattern.compile(txt, ignoreCase ? (Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE) : 0);
                        } else {
                            pat = Pattern.compile(Pattern.quote(txt), ignoreCase ? (Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE) : 0);
                        }
                        List<TextTag> textTags = new ArrayList<>();
                        for (Tag tag : swf.getTags()) {
                            if (tag instanceof TextTag) {
                                textTags.add((TextTag) tag);
                            }
                        }
                        for (TextTag textTag : textTags) {
                            if (!replaceDialog.replaceInParametersCheckBox.isSelected()) {
                                List<String> texts = textTag.getTexts();
                                boolean found = false;
                                for (int i = 0; i < texts.size(); i++) {
                                    String text = texts.get(i);
                                    if (pat.matcher(text).find()) {
                                        texts.set(i, text.replaceAll(txt, replacement));
                                        found = true;
                                        findCount++;
                                    }
                                }
                                if (found) {
                                    String[] textArray = texts.toArray(new String[texts.size()]);
                                    textTag.setFormattedText(getMissingCharacterHandler(), textTag.getFormattedText(false).text, textArray);
                                }
                            } else {
                                String text = textTag.getFormattedText(false).text;
                                if (pat.matcher(text).find()) {
                                    textTag.setFormattedText(getMissingCharacterHandler(), text.replaceAll(txt, replacement), null);
                                    findCount++;
                                }
                            }
                        }

                        if (findCount > 0) {
                            swf.clearImageCache();
                            repaintTree();
                        }

                        return null;
                    }
                }.execute();
            }
        }
    }

    private List<TextTag> searchText(String txt, boolean ignoreCase, boolean regexp, SWF swf) {
        if (txt != null && !txt.isEmpty()) {
            List<TextTag> found = new ArrayList<>();
            Pattern pat;
            if (regexp) {
                pat = Pattern.compile(txt, ignoreCase ? (Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE) : 0);
            } else {
                pat = Pattern.compile(Pattern.quote(txt), ignoreCase ? (Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE) : 0);
            }
            for (Tag tag : swf.getTags()) {
                if (tag instanceof TextTag) {
                    TextTag textTag = (TextTag) tag;
                    if (pat.matcher(textTag.getFormattedText(false).text).find()) {
                        found.add(textTag);
                    }
                }
            }

            return found;
        }

        return null;
    }

    @Override
    public void updateSearchPos(String searchedText, boolean ignoreCase, boolean regExp, TextTag item) {
        View.checkAccess();

        setTagTreeSelectedNode(getCurrentTree(), item);
        previewPanel.getTextPanel().updateSearchPos();
    }

    private void setDumpTreeSelectedNode(DumpInfo dumpInfo) {
        DumpTreeModel dtm = (DumpTreeModel) dumpTree.getModel();
        TreePath tp = dtm.getDumpInfoPath(dumpInfo);
        if (tp != null) {
            dumpTree.setSelectionPath(tp);
            dumpTree.scrollPathToVisible(tp);
        } else {
            showCard(CARDEMPTYPANEL);
        }
    }

    public void setTagTreeSelectedNode(AbstractTagTree tree, TreeItem treeItem) {
        AbstractTagTreeModel ttm = tree.getModel();
        TreePath tp = ttm.getTreePath(treeItem);
        if (tp != null) {
            tree.setSelectionPath(tp);
            tree.scrollPathToVisible(tp);
        } else {
            showCard(CARDEMPTYPANEL);
        }
    }

    public void autoDeobfuscateChanged() {
        Helper.decompilationErrorAdd = AppStrings.translate(Configuration.autoDeobfuscate.get() ? "deobfuscation.comment.failed" : "deobfuscation.comment.tryenable");
        clearAllScriptCache();
        if (abcPanel != null) {
            abcPanel.reload();
        }
        reload(true);
        updateClassesList();
    }

    public void renameColliding(final SWF swf) {
        View.checkAccess();

        if (swf == null) {
            return;
        }
        if (confirmExperimental()) {
            new CancellableWorker<Integer>() {
                @Override
                protected Integer doInBackground() throws Exception {
                    AbcMultiNameCollisionFixer fixer = new AbcMultiNameCollisionFixer();
                    return fixer.fixCollisions(swf);
                }

                @Override
                protected void onStart() {
                    Main.startWork(translate("work.renaming.identifiers") + "...", this);
                }

                @Override
                protected void done() {
                    View.execInEventDispatch(() -> {
                        try {
                            int cnt = get();
                            Main.stopWork();
                            ViewMessages.showMessageDialog(MainPanel.this, translate("message.rename.renamed").replace("%count%", Integer.toString(cnt)));
                            swf.assignClassesToSymbols();
                            swf.clearScriptCache();
                            if (abcPanel != null) {
                                abcPanel.reload();
                            }
                            updateClassesList();
                            reload(true);
                        } catch (Exception ex) {
                            logger.log(Level.SEVERE, "Error during renaming identifiers", ex);
                            Main.stopWork();
                            ViewMessages.showMessageDialog(MainPanel.this, translate("error.occured").replace("%error%", ex.getClass().getSimpleName()));
                        }
                    });
                }
            }.execute();
        }
    }

    public void renameOneIdentifier(final SWF swf) {
        View.checkAccess();

        if (swf == null) {
            return;
        }

        FileAttributesTag fileAttributes = swf.getFileAttributes();
        if (fileAttributes != null && fileAttributes.actionScript3) {
            final int multiName = getABCPanel().decompiledTextArea.getMultinameUnderCaret(new Reference<ABC>(null));
            final List<ABCContainerTag> abcList = swf.getAbcList();
            if (multiName > 0) {
                new CancellableWorker() {
                    @Override
                    public Void doInBackground() throws Exception {
                        renameMultiname(abcList, multiName);
                        return null;
                    }

                    @Override
                    protected void onStart() {
                        Main.startWork(translate("work.renaming") + "...", this);
                    }

                    @Override
                    protected void done() {
                        Main.stopWork();
                    }
                }.execute();

            } else {
                ViewMessages.showMessageDialog(MainPanel.this, translate("message.rename.notfound.multiname"), translate("message.rename.notfound.title"), JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            final String identifier = getActionPanel().getStringUnderCursor();
            if (identifier != null) {
                new CancellableWorker() {
                    @Override
                    public Void doInBackground() throws Exception {
                        try {
                            renameIdentifier(swf, identifier);
                        } catch (InterruptedException ex) {
                            logger.log(Level.SEVERE, null, ex);
                        }
                        return null;
                    }

                    @Override
                    protected void onStart() {
                        Main.startWork(translate("work.renaming") + "...", this);
                    }

                    @Override
                    protected void done() {
                        Main.stopWork();
                    }
                }.execute();
            } else {
                ViewMessages.showMessageDialog(MainPanel.this, translate("message.rename.notfound.identifier"), translate("message.rename.notfound.title"), JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    public void exportFla(final SWF swf) {
        if (swf == null) {
            return;
        }
        JFileChooser fc = new JFileChooser();
        String selDir = Configuration.lastOpenDir.get();
        fc.setCurrentDirectory(new File(selDir));
        if (!selDir.endsWith(File.separator)) {
            selDir += File.separator;
        }
        String swfShortName = swf.getShortFileName();
        if ("".equals(swfShortName)) {
            swfShortName = "untitled.swf";
        }
        String fileName;
        if (swfShortName.contains(".")) {
            fileName = swfShortName.substring(0, swfShortName.lastIndexOf(".")) + ".fla";
        } else {
            fileName = swfShortName + ".fla";
        }
        final String fSwfShortName = swfShortName;

        fc.setSelectedFile(new File(selDir + fileName));
        List<FileFilter> flaFilters = new ArrayList<>();
        List<FileFilter> xflFilters = new ArrayList<>();
        List<FLAVersion> versions = new ArrayList<>();
        boolean isAS3 = swf.isAS3();
        for (int i = FLAVersion.values().length - 1; i >= 0; i--) {
            final FLAVersion v = FLAVersion.values()[i];
            if (!isAS3 && v.minASVersion() > 2) {
                // This version does not support AS1/2
            } else {
                versions.add(v);
                FileFilter f = new FileFilter() {
                    @Override
                    public boolean accept(File f) {
                        return f.isDirectory() || (f.getName().toLowerCase(Locale.ENGLISH).endsWith(".fla"));
                    }

                    @Override
                    public String getDescription() {
                        return translate("filter.fla").replace("%version%", v.applicationName());
                    }
                };
                if (v == FLAVersion.CS6) {
                    fc.setFileFilter(f);
                } else {
                    fc.addChoosableFileFilter(f);
                }
                flaFilters.add(f);
                f = new FileFilter() {
                    @Override
                    public boolean accept(File f) {
                        return f.isDirectory() || (f.getName().toLowerCase(Locale.ENGLISH).endsWith(".xfl"));
                    }

                    @Override
                    public String getDescription() {
                        return translate("filter.xfl").replace("%version%", v.applicationName());
                    }
                };
                fc.addChoosableFileFilter(f);
                xflFilters.add(f);
            }
        }

        fc.setAcceptAllFileFilterUsed(false);
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            Configuration.lastOpenDir.set(Helper.fixDialogFile(fc.getSelectedFile()).getParentFile().getAbsolutePath());
            File sf = Helper.fixDialogFile(fc.getSelectedFile());

            FileFilter selectedFilter = fc.getFileFilter();
            final boolean compressed = flaFilters.contains(selectedFilter);
            String path = sf.getAbsolutePath();
            if (path.endsWith(".fla") || path.endsWith(".xfl")) {
                path = path.substring(0, path.length() - 4);
            }
            path += compressed ? ".fla" : ".xfl";
            final FLAVersion selectedVersion = versions.get(compressed ? flaFilters.indexOf(selectedFilter) : xflFilters.indexOf(selectedFilter));
            final File selfile = new File(path);
            new CancellableWorker() {
                @Override
                protected Void doInBackground() throws Exception {
                    Helper.freeMem();
                    try {
                        AbortRetryIgnoreHandler errorHandler = new GuiAbortRetryIgnoreHandler();
                        if (compressed) {
                            swf.exportFla(errorHandler, selfile.getAbsolutePath(), fSwfShortName, ApplicationInfo.APPLICATION_NAME, ApplicationInfo.applicationVerName, ApplicationInfo.version, Configuration.parallelSpeedUp.get(), selectedVersion);
                        } else {
                            swf.exportXfl(errorHandler, selfile.getAbsolutePath(), fSwfShortName, ApplicationInfo.APPLICATION_NAME, ApplicationInfo.applicationVerName, ApplicationInfo.version, Configuration.parallelSpeedUp.get(), selectedVersion);
                        }
                    } catch (Exception ex) {
                        logger.log(Level.SEVERE, "FLA export error", ex);
                        ViewMessages.showMessageDialog(MainPanel.this, translate("error.export") + ": " + ex.getClass().getName() + " " + ex.getLocalizedMessage(), translate("error"), JOptionPane.ERROR_MESSAGE);
                    }
                    Helper.freeMem();
                    return null;
                }

                @Override
                protected void onStart() {
                    Main.startWork(translate("work.exporting.fla") + "...", this);
                }

                @Override
                protected void done() {
                    if (Configuration.openFolderAfterFlaExport.get()) {
                        try {
                            Desktop.getDesktop().open(selfile.getAbsoluteFile().getParentFile());
                        } catch (IOException ex) {
                            logger.log(Level.SEVERE, null, ex);
                        }
                    }

                    Main.stopWork();
                }
            }.execute();
        }
    }

    public void importImage(final SWF swf) {
        ViewMessages.showMessageDialog(MainPanel.this, translate("message.info.importImages"), translate("message.info"), JOptionPane.INFORMATION_MESSAGE, Configuration.showImportImageInfo);
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File(Configuration.lastExportDir.get()));
        chooser.setDialogTitle(translate("import.select.directory"));
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            String selFile = Helper.fixDialogFile(chooser.getSelectedFile()).getAbsolutePath();
            File imagesDir = new File(Path.combine(selFile, ImageExportSettings.EXPORT_FOLDER_NAME));
            ImageImporter imageImporter = new ImageImporter();

            final long timeBefore = System.currentTimeMillis();
            new CancellableWorker<Void>() {
                
                private int count = 0;
                
                @Override
                public Void doInBackground() throws Exception {
                    try {
                        Map<Integer, CharacterTag> characters = swf.getCharacters();
                        List<String> extensions = Arrays.asList("png", "jpg", "jpeg", "gif", "bmp");
                        for (int characterId : characters.keySet()) {
                            CharacterTag tag = characters.get(characterId);
                            if (tag instanceof ImageTag) {
                                ImageTag imageTag = (ImageTag) tag;
                                if (!imageTag.importSupported()) {
                                    continue;
                                }
                                List<File> existingFilesForImageTag = new ArrayList<>();
                                for (String ext : extensions) {
                                    File sourceFile = new File(Path.combine(imagesDir.getPath(), "" + characterId + "." + ext));
                                    if (sourceFile.exists()) {
                                        existingFilesForImageTag.add(sourceFile);
                                    }
                                }

                                if (existingFilesForImageTag.isEmpty()) {
                                    continue;
                                }

                                if (existingFilesForImageTag.size() > 1) {
                                    Logger.getLogger(MainPanel.class.getName()).log(Level.WARNING, "Multiple matching files for image tag {0} exists, {1} selected", new Object[]{characterId, existingFilesForImageTag.get(0).getName()});
                                }
                                File sourceFile = existingFilesForImageTag.get(0);
                                try {
                                    imageImporter.importImage(imageTag, Helper.readFile(sourceFile.getPath()));
                                    count++;
                                } catch (IOException ex) {
                                    Logger.getLogger(MainPanel.class.getName()).log(Level.WARNING, "Cannot import image " + characterId + " from file " + sourceFile.getName(), ex);
                                }
                                if (Thread.currentThread().isInterrupted()) {
                                    break;
                                }
                            }
                        }
                        swf.clearImageCache();
                    } catch (Exception ex) {
                        logger.log(Level.SEVERE, "Error during import", ex);
                        ViewMessages.showMessageDialog(null, translate("error.import") + ": " + ex.getClass().getName() + " " + ex.getLocalizedMessage());
                    }
                    return null;
                }

                @Override
                protected void onStart() {
                    Main.startWork(translate("work.importing") + "...", this);
                }

                @Override
                protected void done() {
                    Main.stopWork();
                    long timeAfter = System.currentTimeMillis();
                    final long timeMs = timeAfter - timeBefore;
                    

                    View.execInEventDispatch(() -> {
                        refreshTree(swf);
                        setStatus(translate("import.finishedin").replace("%time%", Helper.formatTimeSec(timeMs)));
                        
                        ViewMessages.showMessageDialog(MainPanel.this, translate("import.image.result").replace("%count%", Integer.toString(count)));
                        if (count != 0) {
                            reload(true);
                        }
                    });
                }
            }.execute();
        }
    }

    public void importText(final SWF swf) {
        ViewMessages.showMessageDialog(MainPanel.this, translate("message.info.importTexts"), translate("message.info"), JOptionPane.INFORMATION_MESSAGE, Configuration.showImportTextInfo);
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File(Configuration.lastExportDir.get()));
        chooser.setDialogTitle(translate("import.select.directory"));
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            String selFile = Helper.fixDialogFile(chooser.getSelectedFile()).getAbsolutePath();
            File textsFile = new File(Path.combine(selFile, TextExportSettings.EXPORT_FOLDER_NAME, TextExporter.TEXT_EXPORT_FILENAME_FORMATTED));
            TextImporter textImporter = new TextImporter(getMissingCharacterHandler(), new TextImportErrorHandler() {
                // "configuration items" for the current replace only
                private final ConfigurationItem<Boolean> showAgainImportError = new ConfigurationItem<>("showAgainImportError", true, true);

                private final ConfigurationItem<Boolean> showAgainInvalidText = new ConfigurationItem<>("showAgainInvalidText", true, true);

                private String getTextTagInfo(TextTag textTag) {
                    StringBuilder ret = new StringBuilder();
                    if (textTag != null) {
                        ret.append(" TextId: ").append(textTag.getCharacterId()).append(" (").append(String.join(", ", textTag.getTexts())).append(")");
                    }

                    return ret.toString();
                }

                @Override
                public boolean handle(TextTag textTag) {
                    String msg = translate("error.text.import");
                    logger.log(Level.SEVERE, "{0}{1}", new Object[]{msg, getTextTagInfo(textTag)});
                    return ViewMessages.showConfirmDialog(MainPanel.this, msg, translate("error"), JOptionPane.OK_CANCEL_OPTION, showAgainImportError, JOptionPane.OK_OPTION) != JOptionPane.OK_OPTION;
                }

                @Override
                public boolean handle(TextTag textTag, String message, long line) {
                    String msg = translate("error.text.invalid.continue").replace("%text%", message).replace("%line%", Long.toString(line));
                    logger.log(Level.SEVERE, "{0}{1}", new Object[]{msg, getTextTagInfo(textTag)});
                    return ViewMessages.showConfirmDialog(MainPanel.this, msg, translate("error"), JOptionPane.OK_CANCEL_OPTION, showAgainInvalidText, JOptionPane.OK_OPTION) != JOptionPane.OK_OPTION;
                }
            });

            // try to import formatted texts
            if (textsFile.exists()) {
                textImporter.importTextsSingleFileFormatted(textsFile, swf);
            } else {
                textsFile = new File(Path.combine(selFile, TextExportSettings.EXPORT_FOLDER_NAME, TextExporter.TEXT_EXPORT_FILENAME_PLAIN));
                // try to import plain texts
                if (textsFile.exists()) {
                    textImporter.importTextsSingleFile(textsFile, swf);
                } else {
                    textImporter.importTextsMultipleFiles(selFile, swf);
                }
            }

            swf.clearImageCache();
            reload(true);
        }
    }

    public As3ScriptReplacerInterface getAs3ScriptReplacer() {
        As3ScriptReplacerInterface r = As3ScriptReplacerFactory.createByConfig();
        if (!r.isAvailable()) {
            if (r instanceof MxmlcAs3ScriptReplacer) {
                if (ViewMessages.showConfirmDialog(this, AppStrings.translate("message.flexpath.notset"), AppStrings.translate("error"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.OK_OPTION) {
                    Main.advancedSettings("paths");
                }
            } else if (r instanceof FFDecAs3ScriptReplacer) {
                if (ViewMessages.showConfirmDialog(this, AppStrings.translate("message.playerpath.lib.notset"), AppStrings.translate("message.action.playerglobal.title"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.OK_OPTION) {
                    Main.advancedSettings("paths");
                }
            } else {
                //Not translated yet - just in case there are more Script replacers in the future. Unused now.
                ViewMessages.showConfirmDialog(this, "Current script replacer is not available", "Script replacer not available", JOptionPane.OK_OPTION, JOptionPane.ERROR_MESSAGE);
            }
            return null;
        }
        return r;
    }

    public void importScript(final SWF swf) {
        As3ScriptReplacerInterface as3ScriptReplacer = getAs3ScriptReplacer();
        if (as3ScriptReplacer == null) {
            return;
        }
        ViewMessages.showMessageDialog(MainPanel.this, translate("message.info.importScripts"), translate("message.info"), JOptionPane.INFORMATION_MESSAGE, Configuration.showImportScriptsInfo);

        String flexLocation = Configuration.flexSdkLocation.get();
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File(Configuration.lastExportDir.get()));
        chooser.setDialogTitle(translate("import.select.directory"));
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            String selFile = Helper.fixDialogFile(chooser.getSelectedFile()).getAbsolutePath();
            String scriptsFolder = Path.combine(selFile, ScriptExportSettings.EXPORT_FOLDER_NAME);
            final long timeBefore = System.currentTimeMillis();
            new CancellableWorker<Void>() {
                private int countAs2 = 0;
                private int countAs3 = 0;

                @Override
                public Void doInBackground() throws Exception {
                    new AS2ScriptImporter().importScripts(scriptsFolder, swf.getASMs(true), new ScriptImporterProgressListener() {
                        @Override
                        public void scriptImported() {
                            countAs2++;
                        }
                    });
                    new AS3ScriptImporter().importScripts(as3ScriptReplacer, scriptsFolder, swf.getAS3Packs(), new ScriptImporterProgressListener() {
                        @Override
                        public void scriptImported() {
                            countAs3++;
                        }
                    }
                    );

                    if (countAs3 > 0) {
                        updateClassesList();
                    }
                    return null;
                }

                @Override
                protected void onStart() {
                    Main.importWorker = this;
                    Main.startWork(translate("work.importing_as") + "...", this);
                }

                @Override
                protected void done() {
                    Main.stopWork();
                    long timeAfter = System.currentTimeMillis();
                    final long timeMs = timeAfter - timeBefore;

                    Main.importWorker = null;
                    View.execInEventDispatch(() -> {
                        setStatus(translate("importing_as.finishedin").replace("%time%", Helper.formatTimeSec(timeMs)));

                        ViewMessages.showMessageDialog(MainPanel.this, translate("import.script.result").replace("%count%", Integer.toString(countAs2 + countAs3)));
                        if (countAs2 != 0 || countAs3 != 0) {
                            reload(true);
                        }
                    });
                }
            }.execute();

        }
    }

    public void importSymbolClass(final SWF swf) {
        ViewMessages.showMessageDialog(MainPanel.this, translate("message.info.importSymbolClass").replace("%file%", SymbolClassExporter.SYMBOL_CLASS_EXPORT_FILENAME), translate("message.info"), JOptionPane.INFORMATION_MESSAGE, Configuration.showImportSymbolClassInfo);

        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File(Configuration.lastExportDir.get()));
        chooser.setDialogTitle(translate("import.select.directory"));
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            String selFile = Helper.fixDialogFile(chooser.getSelectedFile()).getAbsolutePath();
            File importFile = new File(Path.combine(selFile, SymbolClassExporter.SYMBOL_CLASS_EXPORT_FILENAME));
            SymbolClassImporter importer = new SymbolClassImporter();

            if (importFile.exists()) {
                importer.importSymbolClasses(importFile, swf);
            }
        }
    }

    private String selectExportDir() {
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File(Configuration.lastExportDir.get()));
        chooser.setDialogTitle(translate("export.select.directory"));
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            final String selFile = Helper.fixDialogFile(chooser.getSelectedFile()).getAbsolutePath();
            Configuration.lastExportDir.set(Helper.fixDialogFile(chooser.getSelectedFile()).getAbsolutePath());
            return selFile;
        }
        return null;
    }

    public void export(final boolean onlySel) {
        View.checkAccess();

        final SWF swf = getCurrentSwf();
        List<TreeItem> sel = getAllSelected();
        if (!onlySel) {
            sel = null;
        } else if (sel.isEmpty()) {
            return;
        }
        final ExportDialog export = new ExportDialog(Main.getDefaultDialogsOwner(), sel);
        if (export.showExportDialog() == AppDialog.OK_OPTION) {
            final String selFile = selectExportDir();
            if (selFile != null) {
                final long timeBefore = System.currentTimeMillis();

                new CancellableWorker<Void>() {
                    @Override
                    public Void doInBackground() throws Exception {
                        try {
                            AbortRetryIgnoreHandler errorHandler = new GuiAbortRetryIgnoreHandler();
                            if (onlySel) {
                                exportSelection(errorHandler, selFile, export);
                            } else {
                                exportAll(swf, errorHandler, selFile, export);
                            }
                        } catch (Exception ex) {
                            logger.log(Level.SEVERE, "Error during export", ex);
                            ViewMessages.showMessageDialog(null, translate("error.export") + ": " + ex.getClass().getName() + " " + ex.getLocalizedMessage());
                        }
                        return null;
                    }

                    @Override
                    protected void onStart() {
                        Main.startWork(translate("work.exporting") + "...", this);
                    }

                    @Override
                    protected void done() {
                        Main.stopWork();
                        long timeAfter = System.currentTimeMillis();
                        final long timeMs = timeAfter - timeBefore;

                        View.execInEventDispatch(() -> {
                            setStatus(translate("export.finishedin").replace("%time%", Helper.formatTimeSec(timeMs)));
                        });
                    }
                }.execute();

            }
        }
    }

    public void exportJavaSource() {
        List<TreeItem> sel = getSelected();
        Set<SWF> swfs = new LinkedHashSet<>();

        for (TreeItem item : sel) {
            if (item instanceof SWFList) {
                SWFList list = (SWFList) item;
                swfs.addAll(list);
            } else {
                swfs.add(item.getSwf());
            }
        }

        for (SWF item : swfs) {
            SWF swf = (SWF) item;
            final String selFile = selectExportDir();
            if (selFile != null) {
                Main.startWork(translate("work.exporting") + "...", null);

                try {
                    new SwfJavaExporter().exportJavaCode(swf, selFile);
                    Main.stopWork();
                } catch (IOException ex) {
                    logger.log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public void exportSwfXml() {
        View.checkAccess();

        List<TreeItem> sel = getSelected();
        Set<SWF> swfs = new LinkedHashSet<>();

        for (TreeItem item : sel) {
            if (item instanceof SWFList) {
                SWFList list = (SWFList) item;
                swfs.addAll(list);
            } else {
                swfs.add(item.getSwf());
            }
        }

        for (SWF swf : swfs) {
            final String selFile = selectExportDir();
            if (selFile != null) {
                Main.startWork(translate("work.exporting") + "...", null);

                try {
                    File outFile = new File(selFile + File.separator + Helper.makeFileName("swf.xml"));
                    new SwfXmlExporter().exportXml(swf, outFile);
                    Main.stopWork();
                } catch (IOException ex) {
                    logger.log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public void importSwfXml() {
        View.checkAccess();

        ViewMessages.showMessageDialog(MainPanel.this, translate("message.info.importXml"), translate("message.info"), JOptionPane.INFORMATION_MESSAGE, Configuration.showImportXmlInfo);

        List<TreeItem> sel = getSelected();
        Set<SWF> swfs = new LinkedHashSet<>();

        for (TreeItem item : sel) {
            if (item instanceof SWFList) {
                SWFList list = (SWFList) item;
                swfs.addAll(list);
            } else {
                swfs.add(item.getSwf());
            }
        }
        if (swfs.size() > 1) {
            return;
        }

        for (SWF swf : swfs) {
            File selectedFile = showImportFileChooser("filter.xml|*.xml", false);
            if (selectedFile != null) {
                File selfile = Helper.fixDialogFile(selectedFile);
                try {
                    new SwfXmlImporter().importSwf(swf, selfile);
                    swf.clearAllCache();
                    swf.assignExportNamesToSymbols();
                    swf.assignClassesToSymbols();
                    refreshTree(swf);
                } catch (IOException ex) {
                    logger.log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public void renameIdentifiers(final SWF swf) {
        View.checkAccess();

        if (swf == null) {
            return;
        }
        if (confirmExperimental()) {
            RenameDialog renameDialog = new RenameDialog(Main.getDefaultDialogsOwner());
            if (renameDialog.showRenameDialog() == AppDialog.OK_OPTION) {
                final RenameType renameType = renameDialog.getRenameType();
                new CancellableWorker<Integer>() {
                    @Override
                    protected Integer doInBackground() throws Exception {
                        int cnt = swf.deobfuscateIdentifiers(renameType);
                        return cnt;
                    }

                    @Override
                    protected void onStart() {
                        Main.startWork(translate("work.renaming.identifiers") + "...", this);
                    }

                    @Override
                    protected void done() {
                        View.execInEventDispatch(() -> {
                            try {
                                int cnt = get();
                                Main.stopWork();
                                ViewMessages.showMessageDialog(MainPanel.this, translate("message.rename.renamed").replace("%count%", Integer.toString(cnt)));
                                swf.assignClassesToSymbols();
                                swf.clearScriptCache();
                                if (abcPanel != null) {
                                    abcPanel.reload();
                                }
                                updateClassesList();
                                reload(true);
                            } catch (Exception ex) {
                                logger.log(Level.SEVERE, "Error during renaming identifiers", ex);
                                Main.stopWork();
                                ViewMessages.showMessageDialog(MainPanel.this, translate("error.occured").replace("%error%", ex.getClass().getSimpleName()));
                            }
                        });
                    }
                }.execute();
            }
        }
    }

    public void deobfuscate() {
        View.checkAccess();

        DeobfuscationDialog deobfuscationDialog = new DeobfuscationDialog(Main.getDefaultDialogsOwner());
        if (deobfuscationDialog.showDialog() == AppDialog.OK_OPTION) {
            DeobfuscationLevel level = DeobfuscationLevel.getByLevel(deobfuscationDialog.codeProcessingLevel.getValue());
            new CancellableWorker() {
                @Override
                protected Void doInBackground() throws Exception {
                    try {
                        ABCPanel abcPanel = getABCPanel();
                        if (deobfuscationDialog.processAllCheckbox.isSelected()) {
                            SWF swf = abcPanel.getSwf();
                            swf.deobfuscate(level);
                        } else {
                            int bi = abcPanel.detailPanel.methodTraitPanel.methodCodePanel.getBodyIndex();
                            DecompiledEditorPane decompiledTextArea = abcPanel.decompiledTextArea;
                            Trait t = abcPanel.decompiledTextArea.getCurrentTrait();
                            ABC abc = abcPanel.abc;
                            if (bi != -1) {
                                int scriptIndex = decompiledTextArea.getScriptLeaf().scriptIndex;
                                int classIndex = decompiledTextArea.getClassIndex();
                                boolean isStatic = decompiledTextArea.getIsStatic();
                                abc.bodies.get(bi).deobfuscate(level, t, scriptIndex, classIndex, isStatic, ""/*FIXME*/);
                            }
                            abcPanel.detailPanel.methodTraitPanel.methodCodePanel.setBodyIndex(decompiledTextArea.getScriptLeaf().getPathScriptName(), bi, abc, t, abcPanel.detailPanel.methodTraitPanel.methodCodePanel.getScriptIndex());
                        }
                    } catch (Exception ex) {
                        logger.log(Level.SEVERE, "Deobfuscation error", ex);
                    }

                    return null;
                }

                @Override
                protected void onStart() {
                    Main.deobfuscatePCodeWorker = this;
                    Main.startWork(translate("work.deobfuscating") + "...", this);
                }

                @Override
                protected void done() {
                    Main.deobfuscatePCodeWorker = null;
                    View.execInEventDispatch(() -> {
                        Main.stopWork();
                        ViewMessages.showMessageDialog(MainPanel.this, translate("work.deobfuscating.complete"));

                        clearAllScriptCache();
                        getABCPanel().reload();
                        updateClassesList();
                    });
                }
            }.execute();
        }
    }

    public void removeNonScripts(SWF swf) {
        if (swf == null) {
            return;
        }

        List<Tag> tags = swf.getTags().toArrayList();
        List<Tag> toRemove = new ArrayList<>();
        for (Tag tag : tags) {
            System.out.println(tag.getClass());
            if (!(tag instanceof ABCContainerTag || tag instanceof ASMSource)) {
                toRemove.add(tag);
            }
        }

        swf.removeTags(toRemove, true);
        refreshTree(swf);
    }

    public void removeExceptSelected(SWF swf) {
        if (swf == null) {
            return;
        }

        List<TreeItem> sel = getAllSelected();
        Set<Integer> needed = new HashSet<>();
        for (TreeItem item : sel) {
            if (item instanceof CharacterTag) {
                CharacterTag characterTag = (CharacterTag) item;
                characterTag.getNeededCharactersDeep(needed);
                needed.add(characterTag.getCharacterId());
            }
        }

        List<Tag> tagsToRemove = new ArrayList<>();
        for (Tag tag : swf.getTags()) {
            if (tag instanceof CharacterTag) {
                CharacterTag characterTag = (CharacterTag) tag;
                if (!needed.contains(characterTag.getCharacterId())) {
                    tagsToRemove.add(tag);
                }
            }
        }

        swf.removeTags(tagsToRemove, true);
        refreshTree(swf);
    }

    private void clear() {
        dumpViewPanel.clear();
        previewPanel.clear();
        headerPanel.clear();
        folderPreviewPanel.clear();
        if (abcPanel != null) {
            abcPanel.clearSwf();
        }
        if (actionPanel != null) {
            actionPanel.clearSource();
        }
    }

    public void refreshTree() {
        refreshTree(new SWF[0]);
    }

    public void treeOperation(Runnable runnable) {
        TreeItem treeItem = getCurrentTree().getCurrentTreeItem();
        tagTree.clearSelection();
        tagListTree.clearSelection();
        runnable.run();
        clear();
        showCard(CARDEMPTYPANEL);

        tagTree.updateSwfs(new SWF[0]);
        tagListTree.updateSwfs(new SWF[0]);

        if (treeItem != null) {
            SWF swf = treeItem.getSwf();
            if (swf != null) {
                SWF treeItemSwf = swf.getRootSwf();
                if (this.swfs.contains(treeItemSwf.swfList)) {
                    setTagTreeSelectedNode(getCurrentTree(), treeItem);
                }
            }
        }

        reload(true);
    }

    public void refreshTree(SWF swf) {
        refreshTree(new SWF[]{swf});
    }

    public void refreshTree(SWF[] swfs) {
        clear();
        showCard(CARDEMPTYPANEL);
        TreeItem treeItem = null;
        if (currentView == VIEW_RESOURCES || currentView == VIEW_TAGLIST) {
            treeItem = getCurrentTree().getCurrentTreeItem();
        }

        tagTree.updateSwfs(swfs);
        tagListTree.updateSwfs(swfs);

        if (treeItem != null) {
            SWF swf = treeItem.getSwf();
            if (swf != null) {
                SWF treeItemSwf = swf.getRootSwf();
                if (this.swfs.contains(treeItemSwf.swfList)) {
                    setTagTreeSelectedNode(getCurrentTree(), treeItem);
                }
            }
        }

        reload(true);
    }

    public void refreshDecompiled() {
        clearAllScriptCache();
        if (abcPanel != null) {
            abcPanel.reload();
        }

        reload(true);
        updateClassesList();
    }

    private MissingCharacterHandler getMissingCharacterHandler() {
        return new MissingCharacterHandler() {
            // "configuration items" for the current replace only
            private final ConfigurationItem<Boolean> showAgainIgnoreMissingCharacters = new ConfigurationItem<>("showAgainIgnoreMissingCharacters", true, true);

            private boolean ignoreMissingCharacters = false;

            @Override
            public boolean getIgnoreMissingCharacters() {
                return ignoreMissingCharacters;
            }

            @Override
            public boolean handle(TextTag textTag, final FontTag font, final char character) {
                String fontName = font.getSwf().sourceFontNamesMap.get(font.getFontId());
                if (fontName == null) {
                    fontName = font.getFontName();
                }
                final Font f = FontTag.getInstalledFontsByName().get(fontName);
                if (f == null || !f.canDisplay(character)) {
                    String msg = translate("error.font.nocharacter").replace("%char%", "" + character);
                    logger.log(Level.SEVERE, "{0} FontId: {1} TextId: {2}", new Object[]{msg, font.getCharacterId(), textTag.getCharacterId()});
                    ignoreMissingCharacters = ViewMessages.showConfirmDialog(MainPanel.this, msg, translate("error"),
                            JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE,
                            showAgainIgnoreMissingCharacters,
                            ignoreMissingCharacters ? JOptionPane.OK_OPTION : JOptionPane.CANCEL_OPTION) == JOptionPane.OK_OPTION;
                    return false;
                }

                font.addCharacter(character, f);

                return true;
            }
        };
    }

    public boolean saveText(TextTag textTag, String formattedText, String[] texts, LineMarkedEditorPane editor) {
        try {
            if (textTag.setFormattedText(getMissingCharacterHandler(), formattedText, texts)) {
                return true;
            }
        } catch (TextParseException ex) {
            if (editor != null) {
                editor.gotoLine((int) ex.line);
                editor.markError();
            }

            ViewMessages.showMessageDialog(MainPanel.this, translate("error.text.invalid").replace("%text%", ex.text).replace("%line%", Long.toString(ex.line)), translate("error"), JOptionPane.ERROR_MESSAGE);
        }

        return false;
    }

    public boolean previousTag() {
        JTree tree = getCurrentTree();

        if (tree != null) {
            if (tree.getSelectionRows().length > 0) {
                int row = tree.getSelectionRows()[0];
                if (row > 0) {
                    tree.setSelectionRow(row - 1);
                    tree.scrollRowToVisible(row - 1);
                    previewPanel.focusTextPanel();
                }
            }
            return true;
        }

        return false;
    }

    public boolean nextTag() {
        JTree tree = getCurrentTree();

        if (tree != null) {
            if (tree.getSelectionRows().length > 0) {
                int row = tree.getSelectionRows()[0];
                if (row < tree.getRowCount() - 1) {
                    tree.setSelectionRow(row + 1);
                    tree.scrollRowToVisible(row + 1);
                    previewPanel.focusTextPanel();
                }
            }
            return true;
        }
        return false;
    }

    public void selectBkColorButtonActionPerformed(ActionEvent evt) {
        Color newColor = JColorChooser.showDialog(null, AppStrings.translate("dialog.selectbkcolor.title"), View.getSwfBackgroundColor());
        if (newColor != null) {
            View.setSwfBackgroundColor(newColor);
            reload(true);
        }
    }

    public void replaceButtonActionPerformed(ActionEvent evt) {
        List<TreeItem> items = getSelected();
        if (items.size() == 0) {
            return;
        }

        TreeItem ti0 = items.get(0);
        File file = null;
        if (ti0 instanceof DefineSoundTag) {
            file = showImportFileChooser("filter.sounds|*.mp3;*.wav|filter.sounds.mp3|*.mp3|filter.sounds.wav|*.wav", false);
        }
        if (ti0 instanceof ImageTag) {
            file = showImportFileChooser("filter.images|*.jpg;*.jpeg;*.gif;*.png;*.bmp", true);
        }
        if (ti0 instanceof ShapeTag) {
            file = showImportFileChooser("filter.images|*.jpg;*.jpeg;*.gif;*.png;*.bmp;*.svg", true);
        }
        if (ti0 instanceof DefineBinaryDataTag) {
            file = showImportFileChooser("", false);
        }
        if (ti0 instanceof UnknownTag) {
            file = showImportFileChooser("", false);
        }
        for (TreeItem ti : items) {
            doReplaceAction(ti, file);
        }
    }

    private void doReplaceAction(TreeItem item, File selectedFile) {
        if (selectedFile == null) {
            return;
        }
        if (item instanceof DefineSoundTag) {
            File selfile = Helper.fixDialogFile(selectedFile);
            DefineSoundTag ds = (DefineSoundTag) item;
            int soundFormat = SoundFormat.FORMAT_UNCOMPRESSED_LITTLE_ENDIAN;
            if (selfile.getName().toLowerCase(Locale.ENGLISH).endsWith(".mp3")) {
                soundFormat = SoundFormat.FORMAT_MP3;
            }

            boolean ok = false;
            try {
                ok = ds.setSound(new FileInputStream(selfile), soundFormat);
                ds.getSwf().clearSoundCache();
            } catch (IOException ex) {
                //ignore
            }

            if (!ok) {
                ViewMessages.showMessageDialog(this, translate("error.sound.invalid"), translate("error"), JOptionPane.ERROR_MESSAGE);
            } else {
                reload(true);
            }
        }
        if (item instanceof ImageTag) {
            ImageTag it = (ImageTag) item;
            if (it.importSupported()) {
                File selfile = Helper.fixDialogFile(selectedFile);
                byte[] data = Helper.readFile(selfile.getAbsolutePath());
                try {
                    Tag newTag = new ImageImporter().importImage(it, data);
                    SWF swf = it.getSwf();
                    if (newTag != null) {
                        refreshTree(swf);
                        setTagTreeSelectedNode(getCurrentTree(), newTag);
                    }
                    swf.clearImageCache();
                } catch (IOException ex) {
                    logger.log(Level.SEVERE, "Invalid image", ex);
                    ViewMessages.showMessageDialog(this, translate("error.image.invalid"), translate("error"), JOptionPane.ERROR_MESSAGE);
                }

                reload(true);
            }
        }
        if (item instanceof ShapeTag) {
            ShapeTag st = (ShapeTag) item;
            File selfile = Helper.fixDialogFile(selectedFile);
            byte[] data = null;
            String svgText = null;
            if (".svg".equals(Path.getExtension(selfile))) {
                svgText = Helper.readTextFile(selfile.getAbsolutePath());
                showSvgImportWarning();
            } else {
                data = Helper.readFile(selfile.getAbsolutePath());
            }
            try {
                Tag newTag = svgText != null ? new SvgImporter().importSvg(st, svgText) : new ShapeImporter().importImage(st, data);
                SWF swf = st.getSwf();
                if (newTag != null) {
                    refreshTree(swf);
                    setTagTreeSelectedNode(getCurrentTree(), newTag);
                }

                swf.clearImageCache();
            } catch (IOException ex) {
                logger.log(Level.SEVERE, "Invalid image", ex);
                ViewMessages.showMessageDialog(MainPanel.this, translate("error.image.invalid"), translate("error"), JOptionPane.ERROR_MESSAGE);
            }
            reload(true);
        }
        if (item instanceof DefineBinaryDataTag) {
            DefineBinaryDataTag bt = (DefineBinaryDataTag) item;
            File selfile = Helper.fixDialogFile(selectedFile);
            byte[] data = Helper.readFile(selfile.getAbsolutePath());
            new BinaryDataImporter().importData(bt, data);
            refreshTree(bt.getSwf());
            reload(true);
        }

        if (item instanceof UnknownTag) {
            UnknownTag ut = (UnknownTag) item;
            File selfile = Helper.fixDialogFile(selectedFile);
            byte[] data = Helper.readFile(selfile.getAbsolutePath());
            ut.unknownData = new ByteArrayRange(data);
            ut.setModified(true);
            refreshTree(ut.getSwf());
            reload(true);
        }
    }

    public void replaceNoFillButtonActionPerformed(ActionEvent evt) {
        TreeItem item = getCurrentTree().getCurrentTreeItem();
        if (item == null) {
            return;
        }

        if (item instanceof ShapeTag) {
            ShapeTag st = (ShapeTag) item;
            String filter = "filter.images|*.jpg;*.jpeg;*.gif;*.png;*.bmp;*.svg";
            File selectedFile = showImportFileChooser(filter, true);
            if (selectedFile != null) {
                File selfile = Helper.fixDialogFile(selectedFile);
                byte[] data = null;
                String svgText = null;
                if (".svg".equals(Path.getExtension(selfile))) {
                    svgText = Helper.readTextFile(selfile.getAbsolutePath());
                    showSvgImportWarning();
                } else {
                    data = Helper.readFile(selfile.getAbsolutePath());
                }
                try {
                    Tag newTag = svgText != null ? new SvgImporter().importSvg(st, svgText, false) : new ShapeImporter().importImage(st, data, 0, false);
                    SWF swf = st.getSwf();
                    if (newTag != null) {
                        refreshTree(swf);
                        setTagTreeSelectedNode(getCurrentTree(), newTag);
                    }

                    swf.clearImageCache();
                } catch (IOException ex) {
                    logger.log(Level.SEVERE, "Invalid image", ex);
                    ViewMessages.showMessageDialog(this, translate("error.image.invalid"), translate("error"), JOptionPane.ERROR_MESSAGE);
                }
                reload(true);
            }
        }
    }

    private void showSvgImportWarning() {
        ViewMessages.showMessageDialog(this, AppStrings.translate("message.warning.svgImportExperimental"), AppStrings.translate("message.warning"), JOptionPane.WARNING_MESSAGE, Configuration.warningSvgImport);
    }

    public void replaceAlphaButtonActionPerformed(ActionEvent evt) {
        TreeItem item = getCurrentTree().getCurrentTreeItem();
        if (item == null) {
            return;
        }

        if (item instanceof DefineBitsJPEG3Tag || item instanceof DefineBitsJPEG4Tag) {
            ImageTag it = (ImageTag) item;
            if (it.importSupported()) {
                File selectedFile = showImportFileChooser("", false);
                if (selectedFile != null) {
                    File selfile = Helper.fixDialogFile(selectedFile);
                    byte[] data = Helper.readFile(selfile.getAbsolutePath());
                    try {
                        new ImageImporter().importImageAlpha(it, data);
                        SWF swf = it.getSwf();
                        swf.clearImageCache();
                    } catch (IOException ex) {
                        logger.log(Level.SEVERE, "Invalid alpha channel data", ex);
                        ViewMessages.showMessageDialog(this, translate("error.image.alpha.invalid"), translate("error"), JOptionPane.ERROR_MESSAGE);
                    }

                    reload(true);
                }
            }
        }
    }

    public void exportJavaSourceActionPerformed(ActionEvent evt) {
        if (Main.isWorking()) {
            return;
        }

        exportJavaSource();
    }

    public void exportSwfXmlActionPerformed(ActionEvent evt) {
        if (Main.isWorking()) {
            return;
        }

        exportSwfXml();
    }

    public void importSwfXmlActionPerformed(ActionEvent evt) {
        if (Main.isWorking()) {
            return;
        }

        importSwfXml();
    }

    public void exportSelectionActionPerformed(ActionEvent evt) {
        if (Main.isWorking()) {
            return;
        }

        export(true);
    }

    public File showImportFileChooser(String filter, boolean imagePreview) {
        String[] filterArray = filter.length() > 0 ? filter.split("\\|") : new String[0];

        JFileChooser fc = new JFileChooser();
        fc.setCurrentDirectory(new File(Configuration.lastOpenDir.get()));
        if (imagePreview) {
            fc.setAccessory(new FileChooserImagePreview(fc));
            Dimension prefferedSize = new Dimension(fc.getPreferredSize());
            prefferedSize.width += FileChooserImagePreview.PREVIEW_SIZE;
            fc.setPreferredSize(prefferedSize);
        }
        boolean first = true;
        for (int i = 0; i < filterArray.length; i += 2) {
            final String filterName = filterArray[i];
            final String[] extensions = filterArray[i + 1].split(";");
            for (int j = 0; j < extensions.length; j++) {
                if (extensions[j].startsWith("*.")) {
                    extensions[j] = extensions[j].substring(1);
                }
            }
            FileFilter ff = new FileFilter() {
                @Override
                public boolean accept(File f) {
                    if (f.isDirectory()) {
                        return true;
                    }
                    String fileName = f.getName().toLowerCase(Locale.ENGLISH);
                    for (String ext : extensions) {
                        if (fileName.endsWith(ext)) {
                            return true;
                        }
                    }
                    return false;
                }

                @Override
                public String getDescription() {
                    StringBuilder extStr = new StringBuilder();
                    boolean first = true;
                    for (String ext : extensions) {
                        if (first) {
                            first = false;
                        } else {
                            extStr.append(",");
                        }

                        extStr.append("*").append(ext);
                    }

                    return translate(filterName).replace("%extensions%", extStr);
                }
            };
            if (first) {
                fc.setFileFilter(ff);
            } else {
                fc.addChoosableFileFilter(ff);
            }
            first = false;
        }

        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File result = fc.getSelectedFile();
            Configuration.lastOpenDir.set(Helper.fixDialogFile(result).getParentFile().getAbsolutePath());
            return result;
        }

        return null;
    }

    private void showDetail(String card) {
        CardLayout cl = (CardLayout) (detailPanel.getLayout());
        cl.show(detailPanel, card);
        if (card.equals(DETAILCARDEMPTYPANEL)) {
            if (detailPanel.isVisible()) {
                detailPanel.setVisible(false);
            }
        } else if (!detailPanel.isVisible()) {
            detailPanel.setVisible(true);
        }
    }

    private void showCard(String card) {
        CardLayout cl = (CardLayout) (displayPanel.getLayout());
        cl.show(displayPanel, card);
    }

    private void valueChanged(Object source, TreePath selectedPath) {
        TreeItem treeItem = (TreeItem) selectedPath.getLastPathComponent();

        if (treeItem == null) {
            return;
        }

        if (!(treeItem instanceof SWFList)) {
            SWF swf = treeItem.getSwf();
            if (swfs.isEmpty()) {
                // show welcome panel after closing swfs
                updateUi();
            } else {
                if (swf == null && swfs.get(0) != null) {
                    swf = swfs.get(0).get(0);
                }

                if (swf != null) {
                    updateUi(swf);
                }
            }
        } else {
            updateUi();
        }

        reload(false);

        if (source == dumpTree) {
            Tag t = null;
            if (treeItem instanceof DumpInfo) {
                DumpInfo di = (DumpInfo) treeItem;
                t = di.getTag();
            }
            showPreview(t, dumpPreviewPanel, getFrameForTreeItem(t), getTimelinedForTreeItem(t));
        }
    }

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        Object source = e.getSource();
        valueChanged(source, e.getPath());
    }

    private int getFrameForTreeItem(TreeItem treeItem) {
        if (treeItem == null) {
            return -1;
        }
        if (currentView == VIEW_DUMP) {
            if (treeItem instanceof Tag) {
                Tag t = (Tag) treeItem;
                ReadOnlyTagList tags = t.getTimelined().getTags();
                int frame = 0;
                for (int i = 0; i < tags.size(); i++) {
                    if (tags.get(i) == t) {
                        return frame;
                    }
                    if (tags.get(i) instanceof ShowFrameTag) {
                        frame++;
                    }
                }
            }
            return -1;
        }

        if (currentView == VIEW_TIMELINE) {
            return -1;
        }
        TreePath path = getCurrentTree().getModel().getTreePath(treeItem);
        if (path == null) {
            return -1;
        }
        for (int i = path.getPathCount() - 1; i >= 0; i--) {
            if (path.getPathComponent(i) instanceof Frame) {
                Frame frame = (Frame) path.getPathComponent(i);
                return frame.frame;
            }
        }
        return -1;
    }

    private Timelined getTimelinedForTreeItem(TreeItem treeItem) {
        if (treeItem == null) {
            return null;
        }

        if (currentView == VIEW_DUMP) {
            if (treeItem instanceof Tag) {
                Tag t = (Tag) treeItem;
                return t.getTimelined();
            }
            return null;
        }
        if (currentView == VIEW_TIMELINE) {
            return null;
        }

        TreePath path = getCurrentTree().getModel().getTreePath(treeItem);
        if (path == null) {
            return null;
        }
        for (int i = path.getPathCount() - 1; i >= 0; i--) {
            if (path.getPathComponent(i) instanceof Timelined) {
                return (Timelined) path.getPathComponent(i);
            }
        }
        return null;
    }

    public void unloadFlashPlayer() {
        if (flashPanel != null) {
            try {
                flashPanel.close();
            } catch (IOException ex) {
                // ignore
            }
        }
        if (flashPanel2 != null) {
            try {
                flashPanel2.close();
            } catch (IOException ex) {
                // ignore
            }
        }
    }

    public void clearDebuggerColors() {
        if (abcPanel != null) {
            abcPanel.decompiledTextArea.removeColorMarkerOnAllLines(DecompiledEditorPane.IP_MARKER);
            abcPanel.detailPanel.methodTraitPanel.methodCodePanel.clearDebuggerColors();
        }
        if (actionPanel != null) {
            actionPanel.decompiledEditor.removeColorMarkerOnAllLines(DecompiledEditorPane.IP_MARKER);
            actionPanel.editor.removeColorMarkerOnAllLines(DecompiledEditorPane.IP_MARKER);
        }
    }

    private void stopFlashPlayer() {
        if (flashPanel != null) {
            if (!flashPanel.isStopped()) {
                flashPanel.stopSWF();
            }
        }
        if (flashPanel2 != null) {
            if (!flashPanel2.isStopped()) {
                flashPanel2.stopSWF();
            }
        }
    }

    public static boolean isAdobeFlashPlayerEnabled() {
        return Configuration.useAdobeFlashPlayerForPreviews.get();
    }

    public static final int VIEW_RESOURCES = 0;

    public static final int VIEW_DUMP = 1;

    public static final int VIEW_TIMELINE = 2;

    public static final int VIEW_TAGLIST = 3;

    public int getCurrentView() {
        return currentView;
    }

    public void setTreeModel(int view) {
        switch (view) {
            case VIEW_DUMP:
                if (dumpTree.getModel() == null) {
                    DumpTreeModel dtm = new DumpTreeModel(swfs);
                    dumpTree.setModel(dtm);
                    dumpTree.expandFirstLevelNodes();
                }
                break;
            case VIEW_RESOURCES:
            case VIEW_TAGLIST:
                if (tagTree.getModel() == null) {
                    TagTreeModel ttm = new TagTreeModel(swfs, Configuration.tagTreeShowEmptyFolders.get());
                    tagTree.setModel(ttm);
                    tagTree.expandFirstLevelNodes();
                }

                if (tagListTree.getModel() == null) {
                    TagListTreeModel ttm = new TagListTreeModel(swfs);
                    tagListTree.setModel(ttm);
                    tagListTree.expandFirstLevelNodes();
                }
                break;
        }
    }

    private JPanel createDumpViewCard() {
        JPanel r = new JPanel(new BorderLayout());
        r.add(new JPersistentSplitPane(JSplitPane.VERTICAL_SPLIT, new FasterScrollPane(dumpTree), dumpPreviewPanel, Configuration.guiDumpSplitPaneDividerLocationPercent), BorderLayout.CENTER);
        return r;
    }

    private JPanel createTagListViewCard() {
        JPanel r = new JPanel(new BorderLayout());
        r.add(new FasterScrollPane(tagListTree), BorderLayout.CENTER);
        return r;
    }

    private JPanel createResourcesViewCard() {
        JPanel r = new JPanel(new BorderLayout());
        r.add(new FasterScrollPane(tagTree), BorderLayout.CENTER);
        r.add(searchPanel, BorderLayout.SOUTH);
        return r;
    }

    private void showContentPanelCard(String card) {
        CardLayout cl = (CardLayout) (contentPanel.getLayout());
        cl.show(contentPanel, card);
    }

    private void showTreePanelCard(String card) {
        CardLayout cl = (CardLayout) (treePanel.getLayout());
        cl.show(treePanel, card);
    }

    public boolean showView(int view) {
        View.checkAccess();

        setTreeModel(view);
        switch (view) {
            case VIEW_DUMP:
                currentView = view;
                Configuration.lastView.set(currentView);
                if (!isWelcomeScreen) {
                    showContentPanelCard(SPLIT_PANE1);
                }
                showTreePanelCard(DUMP_VIEW);
                treePanelMode = TreePanelMode.DUMP_TREE;
                showDetail(DETAILCARDEMPTYPANEL);
                reload(true);
                return true;
            case VIEW_RESOURCES:
                currentView = view;
                Configuration.lastView.set(currentView);
                if (!isWelcomeScreen) {
                    showContentPanelCard(SPLIT_PANE1);
                }
                showTreePanelCard(RESOURCES_VIEW);

                treePanelMode = TreePanelMode.TAG_TREE;

                treePanel.addComponentListener(new ComponentAdapter() {
                    @Override
                    public void componentResized(ComponentEvent e) {
                        tagTree.scrollPathToVisible(tagTree.getSelectionPath());
                    }
                });

                reload(true);
                return true;
            case VIEW_TIMELINE:
                currentView = view;
                Configuration.lastView.set(currentView);
                final SWF swf = getCurrentSwf();
                if (swf != null) {
                    TreeItem item = tagTree.getCurrentTreeItem();
                    if (item instanceof TagScript) {
                        item = ((TagScript) item).getTag();
                    }
                    if (item instanceof Timelined) {
                        timelineViewPanel.setTimelined((Timelined) item);
                    } else if (item instanceof Frame) {
                        timelineViewPanel.setTimelined(((Frame) item).timeline.timelined);
                    } else {
                        timelineViewPanel.setTimelined(swf);
                    }
                    showContentPanelCard(TIMELINE_PANEL);
                    return true;
                } else {
                    showView(VIEW_RESOURCES);
                }
                return false;
            case VIEW_TAGLIST:
                currentView = view;
                Configuration.lastView.set(currentView);
                if (!isWelcomeScreen) {
                    showContentPanelCard(SPLIT_PANE1);
                }
                showTreePanelCard(TAGLIST_VIEW);
                treePanelMode = TreePanelMode.TAGLIST_TREE;
                reload(true);
                return true;
        }

        return false;
    }

    private void dumpViewReload(boolean forceReload) {
        showDetail(DETAILCARDEMPTYPANEL);

        DumpInfo dumpInfo = (DumpInfo) dumpTree.getLastSelectedPathComponent();
        if (dumpInfo == null) {
            showCard(CARDEMPTYPANEL);
            return;
        }

        dumpViewPanel.revalidate();
        dumpViewPanel.setSelectedNode(dumpInfo);
        showCard(CARDDUMPVIEW);
    }

    public void loadFromBinaryTag(final DefineBinaryDataTag binaryDataTag) {
        loadFromBinaryTag(Arrays.asList(binaryDataTag));
    }

    public void loadFromBinaryTag(final List<DefineBinaryDataTag> binaryDataTags) {

        Main.loadingDialog.setVisible(true);
        new CancellableWorker<Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    for (DefineBinaryDataTag binaryDataTag : binaryDataTags) {
                        try {
                            InputStream is = new ByteArrayInputStream(binaryDataTag.binaryData.getRangeData());
                            SWF bswf = new SWF(is, null, "(SWF Data)", new ProgressListener() {
                                @Override
                                public void progress(int p) {
                                    Main.loadingDialog.setPercent(p);
                                }
                            }, Configuration.parallelSpeedUp.get());
                            binaryDataTag.innerSwf = bswf;
                            bswf.binaryData = binaryDataTag;
                        } catch (IOException ex) {
                            //ignore
                        }
                    }
                } catch (InterruptedException ex) {
                    //ignore
                }

                return null;
            }

            @Override
            protected void onStart() {
                Main.startWork(AppStrings.translate("work.reading.swf") + "...", this);
            }

            @Override
            protected void done() {
                View.execInEventDispatch(() -> {
                    Main.loadingDialog.setVisible(false);
                    refreshTree();
                    Main.stopWork();
                });
            }
        }.execute();
    }

    private void closeTag() {
        View.checkAccess();

        previewPanel.closeTag();
    }

    public static void showPreview(TreeItem treeItem, PreviewPanel previewPanel, int frame, Timelined timelinedContainer) {
        previewPanel.clear();
        if (treeItem == null) {
            previewPanel.showEmpty();
            return;
        }
        boolean internalViewer = !isAdobeFlashPlayerEnabled();
        if (treeItem instanceof SWF) {
            SWF swf = (SWF) treeItem;
            if (internalViewer) {
                previewPanel.showImagePanel(swf, swf, -1, true, Configuration.autoPlaySwfs.get());
            } else {
                previewPanel.setParametersPanelVisible(false);
                //if (flashPanel != null) { //same for flashPanel2
                previewPanel.showFlashViewerPanel();
                previewPanel.showSwf(swf);

                //}                
            }
        } else if ((treeItem instanceof PlaceObjectTypeTag)) {// && (previewPanel != dumpPreviewPanel)) {
            previewPanel.showPlaceTagPanel((PlaceObjectTypeTag) treeItem, frame);
        } else if (treeItem instanceof MetadataTag) {
            MetadataTag metadataTag = (MetadataTag) treeItem;
            previewPanel.showMetaDataPanel(metadataTag);
        } else if (treeItem instanceof DefineBinaryDataTag) {
            DefineBinaryDataTag binaryTag = (DefineBinaryDataTag) treeItem;
            previewPanel.showBinaryPanel(binaryTag);
        } else if (treeItem instanceof UnknownTag) {
            UnknownTag unknownTag = (UnknownTag) treeItem;
            previewPanel.showUnknownPanel(unknownTag);
        } else if (treeItem instanceof ImageTag) {
            ImageTag imageTag = (ImageTag) treeItem;
            previewPanel.setImageReplaceButtonVisible(!((Tag) imageTag).isReadOnly() && imageTag.importSupported(), imageTag instanceof DefineBitsJPEG3Tag || imageTag instanceof DefineBitsJPEG4Tag, false, false);
            SWF imageSWF = makeTimelinedImage(imageTag);
            previewPanel.showImagePanel(imageSWF, imageSWF, 0, false, true);

        } else if ((treeItem instanceof DrawableTag) && (!(treeItem instanceof TextTag)) && (!(treeItem instanceof FontTag)) && internalViewer) {
            final Tag tag = (Tag) treeItem;
            DrawableTag d = (DrawableTag) tag;
            Timelined timelined;
            if (treeItem instanceof Timelined && !(treeItem instanceof ButtonTag)) {
                timelined = (Timelined) tag;
            } else {
                timelined = makeTimelined(tag);
            }

            previewPanel.setParametersPanelVisible(false);
            if (treeItem instanceof ShapeTag) {
                previewPanel.setImageReplaceButtonVisible(false, false, !((Tag) treeItem).isReadOnly(), false);
            }
            previewPanel.showImagePanel(timelined, tag.getSwf(), -1, true, true);
        } else if (treeItem instanceof Frame && internalViewer) {
            Frame fn = (Frame) treeItem;
            SWF swf = fn.getSwf();
            previewPanel.showImagePanel(fn.timeline.timelined, swf, fn.frame, true, true);
        } else if (treeItem instanceof ShowFrameTag) {
            SWF swf;
            if (timelinedContainer instanceof DefineSpriteTag) {
                swf = ((DefineSpriteTag) timelinedContainer).getSwf();
            } else {
                swf = (SWF) timelinedContainer;
            }
            previewPanel.showImagePanel(timelinedContainer, swf, frame, true, true);
        } else if ((treeItem instanceof SoundTag)) { //&& isInternalFlashViewerSelected() && (Arrays.asList("mp3", "wav").contains(((SoundTag) tagObj).getExportFormat())))) {
            previewPanel.showImagePanel(new SerializableImage(View.loadImage("sound32")));
            previewPanel.setImageReplaceButtonVisible(false, false, false, !((Tag) treeItem).isReadOnly() && (treeItem instanceof DefineSoundTag));
            try {
                SoundTagPlayer soundThread = new SoundTagPlayer(null, (SoundTag) treeItem, Configuration.loopMedia.get() ? Integer.MAX_VALUE : 1, true);
                previewPanel.setMedia(soundThread);
            } catch (LineUnavailableException | IOException | UnsupportedAudioFileException ex) {
                logger.log(Level.SEVERE, null, ex);
            }

        } else if ((treeItem instanceof FontTag) && internalViewer) {
            previewPanel.showFontPanel((FontTag) treeItem);
        } else if ((treeItem instanceof TextTag) && internalViewer) {
            previewPanel.showTextPanel((TextTag) treeItem);
        } else if ((treeItem instanceof Frame) || (treeItem instanceof CharacterTag) || (treeItem instanceof FontTag) || (treeItem instanceof SoundStreamHeadTypeTag)) {
            previewPanel.createAndShowTempSwf(treeItem);

            if (treeItem instanceof TextTag) {
                previewPanel.showTextPanel((TextTag) treeItem);
            } else if (treeItem instanceof FontTag) {
                previewPanel.showFontPanel((FontTag) treeItem);
            } else {
                previewPanel.setParametersPanelVisible(false);
            }
        } else {
            previewPanel.showEmpty();
        }
    }

    private void tagListViewReload(boolean forceReload) {
        showDetail(DETAILCARDEMPTYPANEL);
        showCard(CARDEMPTYPANEL);
    }

    public void reload(boolean forceReload) {
        View.checkAccess();

        tagTree.scrollPathToVisible(tagTree.getSelectionPath());
        if (currentView == VIEW_DUMP) {
            dumpViewReload(forceReload);
            return;
        }
        /*else if (currentView == VIEW_TAGLIST) {
            tagListViewReload(forceReload);
            return;
        }*/

        AbstractTagTree tree = getCurrentTree();
        TreeItem treeItem = null;
        TreePath treePath = tree.getSelectionPath();
        if (treePath != null && tree.getModel().treePathExists(treePath)) {
            treeItem = (TreeItem) treePath.getLastPathComponent();
        }

        // save last selected node to config
        if (treeItem != null && !(treeItem instanceof SWFList)) {
            SWF swf = treeItem.getSwf();
            if (swf != null) {
                swf = swf.getRootSwf();
            }

            if (swf != null) {
                SwfSpecificCustomConfiguration swfCustomConf = Configuration.getOrCreateSwfSpecificCustomConfiguration(swf.getShortFileName());
                //swfConf.lastSelectedPath = tagTree.getSelectionPathString();
                swfCustomConf.setCustomData(SwfSpecificCustomConfiguration.KEY_LAST_SELECTED_PATH_RESOURCES, tagTree.getSelectionPathString());
                swfCustomConf.setCustomData(SwfSpecificCustomConfiguration.KEY_LAST_SELECTED_PATH_TAGLIST, tagListTree.getSelectionPathString());

            }
        }

        if (!forceReload && (treeItem == oldItem)) {
            return;
        }

        if (oldItem != treeItem) {
            closeTag();
        }

        oldItem = treeItem;

        // show the preview of the tag when the user clicks to the tagname inside the scripts node, too
        // this is a little bit inconsistent, beacuse the frames (FrameScript) are not shown
        boolean preferScript = false;
        if (treeItem instanceof TagScript) {
            treeItem = ((TagScript) treeItem).getTag();
            preferScript = true;
        }

        folderPreviewPanel.clear();
        previewPanel.clear();
        stopFlashPlayer();

        previewPanel.setImageReplaceButtonVisible(false, false, false, false);

        boolean internalViewer = !isAdobeFlashPlayerEnabled();

        if (treeItem instanceof ScriptPack) {
            final ScriptPack scriptLeaf = (ScriptPack) treeItem;
            if (!Main.isInited() || !Main.isWorking() || Main.isDebugging()) {
                ABCPanel abcPanel = getABCPanel();
                abcPanel.detailPanel.methodTraitPanel.methodCodePanel.clear();
                abcPanel.setAbc(scriptLeaf.abc);
                abcPanel.decompiledTextArea.setScript(scriptLeaf, true);
                abcPanel.decompiledTextArea.setNoTrait();
            }

            if (Configuration.displayAs3TraitsListAndConstantsPanel.get()) {
                showDetail(DETAILCARDAS3NAVIGATOR);
            } else {
                showDetail(DETAILCARDEMPTYPANEL);
            }
            showCard(CARDACTIONSCRIPT3PANEL);
            return;
        }

        if (treeItem instanceof Tag) {
            Tag tag = (Tag) treeItem;
            TagInfo tagInfo = new TagInfo(treeItem.getSwf());
            tag.getTagInfo(tagInfo);
            if (!tagInfo.isEmpty()) {
                tagInfoPanel.setTagInfos(tagInfo);
                showDetail(DETAILCARDTAGINFO);
            } else {
                showDetail(DETAILCARDEMPTYPANEL);
            }
        } else if (treeItem instanceof Frame) {
            Frame frame = (Frame) treeItem;
            Set<Integer> needed = new LinkedHashSet<>();

            frame.getNeededCharacters(needed);

            if (!needed.isEmpty()) {
                TagInfo tagInfo = new TagInfo(treeItem.getSwf());
                tagInfo.addInfo("general", "neededCharacters", Helper.joinStrings(needed, ", "));
                tagInfoPanel.setTagInfos(tagInfo);
                showDetail(DETAILCARDTAGINFO);
            } else {
                showDetail(DETAILCARDEMPTYPANEL);
            }
        } else {
            showDetail(DETAILCARDEMPTYPANEL);
        }

        if (treeItem instanceof HeaderItem) {
            headerPanel.load(((HeaderItem) treeItem).getSwf());
            showCard(CARDHEADER);
        } else if (treeItem instanceof FolderItem) {
            showFolderPreview((FolderItem) treeItem);
        } else if (treeItem instanceof SWF) {
            showPreview(treeItem, previewPanel, -1, null);
            showCard(CARDPREVIEWPANEL);
        } else if (treeItem instanceof MetadataTag) {
            showPreview(treeItem, previewPanel, -1, null);
            showCard(CARDPREVIEWPANEL);
        } else if (treeItem instanceof DefineBinaryDataTag) {
            showPreview(treeItem, previewPanel, -1, null);
            showCard(CARDPREVIEWPANEL);
        } else if (treeItem instanceof UnknownTag) {
            showPreview(treeItem, previewPanel, -1, null);
            showCard(CARDPREVIEWPANEL);
        } else if (treeItem instanceof ASMSource && (!(treeItem instanceof DrawableTag) || preferScript)) {
            getActionPanel().setSource((ASMSource) treeItem, !forceReload);
            showCard(CARDACTIONSCRIPTPANEL);
        } else if (treeItem instanceof ImageTag) {
            showPreview(treeItem, previewPanel, -1, null);
            showCard(CARDPREVIEWPANEL);
        } else if ((treeItem instanceof DrawableTag) && (!(treeItem instanceof TextTag)) && (!(treeItem instanceof FontTag)) && internalViewer) {
            showPreview(treeItem, previewPanel, -1, null);
            showCard(CARDPREVIEWPANEL);
        } else if ((treeItem instanceof FontTag) && internalViewer) {
            showPreview(treeItem, previewPanel, -1, null);
            showCard(CARDPREVIEWPANEL);
        } else if ((treeItem instanceof TextTag) && internalViewer) {
            showPreview(treeItem, previewPanel, -1, null);
            showCard(CARDPREVIEWPANEL);
        } else if (treeItem instanceof Frame && internalViewer) {
            showPreview(treeItem, previewPanel, -1, null);
            showCard(CARDPREVIEWPANEL);
        } else if (treeItem instanceof ShowFrameTag && internalViewer) {
            showPreview(treeItem, previewPanel, getFrameForTreeItem(treeItem), getTimelinedForTreeItem(treeItem));
            showCard(CARDPREVIEWPANEL);
        } else if ((treeItem instanceof SoundTag)) { //&& isInternalFlashViewerSelected() && (Arrays.asList("mp3", "wav").contains(((SoundTag) tagObj).getExportFormat())))) {
            showPreview(treeItem, previewPanel, -1, null);
            showCard(CARDPREVIEWPANEL);
        } else if ((treeItem instanceof Frame) || (treeItem instanceof CharacterTag) || (treeItem instanceof FontTag) || (treeItem instanceof SoundStreamHeadTypeTag)) {
            showPreview(treeItem, previewPanel, -1, null);

            showCard(CARDPREVIEWPANEL);
        } else if (treeItem instanceof PlaceObjectTypeTag) {
            showPreview(treeItem, previewPanel, getFrameForTreeItem(treeItem), null);
            showCard(CARDPREVIEWPANEL);
        } else if (treeItem instanceof Tag) {
            showGenericTag((Tag) treeItem);
        } else {
            showCard(CARDEMPTYPANEL);
        }
    }

    public void repaintTree() {
        tagTree.repaint();
        tagListTree.repaint();
        reload(true);
    }

    public void showGenericTag(Tag tag) {
        previewPanel.showGenericTagPanel(tag);
        showCard(CARDPREVIEWPANEL);
    }

    public void showTextTagWithNewValue(TextTag textTag, TextTag newTextTag) {

        previewPanel.showTextComparePanel(textTag, newTextTag);
    }

    private void addFolderPreviewItems(List<TreeItem> folderPreviewItems, String folderName, Timelined timelined) {
        switch (folderName) {
            case TagTreeModel.FOLDER_SHAPES:
                for (Tag tag : timelined.getTags()) {
                    if (tag instanceof ShapeTag) {
                        folderPreviewItems.add(tag);
                    }
                    if (tag instanceof DefineSpriteTag) {
                        addFolderPreviewItems(folderPreviewItems, folderName, (DefineSpriteTag) tag);
                    }
                }
                break;
            case TagTreeModel.FOLDER_MORPHSHAPES:
                for (Tag tag : timelined.getTags()) {
                    if (tag instanceof MorphShapeTag) {
                        folderPreviewItems.add(tag);
                    }
                    if (tag instanceof DefineSpriteTag) {
                        addFolderPreviewItems(folderPreviewItems, folderName, (DefineSpriteTag) tag);
                    }
                }
                break;
            case TagTreeModel.FOLDER_SPRITES:
                for (Tag tag : timelined.getTags()) {
                    if (tag instanceof DefineSpriteTag) {
                        folderPreviewItems.add(tag);
                        addFolderPreviewItems(folderPreviewItems, folderName, (DefineSpriteTag) tag);
                    }
                }
                break;
            case TagTreeModel.FOLDER_BUTTONS:
                for (Tag tag : timelined.getTags()) {
                    if (tag instanceof ButtonTag) {
                        folderPreviewItems.add(tag);
                    }
                    if (tag instanceof DefineSpriteTag) {
                        addFolderPreviewItems(folderPreviewItems, folderName, (DefineSpriteTag) tag);
                    }
                }
                break;
            case TagTreeModel.FOLDER_FONTS:
                for (Tag tag : timelined.getTags()) {
                    if (tag instanceof FontTag) {
                        folderPreviewItems.add(tag);
                    }
                    if (tag instanceof DefineSpriteTag) {
                        addFolderPreviewItems(folderPreviewItems, folderName, (DefineSpriteTag) tag);
                    }
                }
                break;
            case TagTreeModel.FOLDER_FRAMES:
                for (Frame frame : timelined.getTimeline().getFrames()) {
                    folderPreviewItems.add(frame);
                }
                break;
            case TagTreeModel.FOLDER_IMAGES:
                for (Tag tag : timelined.getTags()) {
                    if (tag instanceof ImageTag) {
                        folderPreviewItems.add(tag);
                    }
                    if (tag instanceof DefineSpriteTag) {
                        addFolderPreviewItems(folderPreviewItems, folderName, (DefineSpriteTag) tag);
                    }
                }
                break;
            case TagTreeModel.FOLDER_TEXTS:
                for (Tag tag : timelined.getTags()) {
                    if (tag instanceof TextTag) {
                        folderPreviewItems.add(tag);
                    }
                    if (tag instanceof DefineSpriteTag) {
                        addFolderPreviewItems(folderPreviewItems, folderName, (DefineSpriteTag) tag);
                    }
                }
                break;
        }
    }

    private void showFolderPreview(FolderItem item) {
        List<TreeItem> folderPreviewItems = new ArrayList<>();
        String folderName = item.getName();
        SWF swf = item.swf;
        addFolderPreviewItems(folderPreviewItems, folderName, swf);

        folderPreviewPanel.setItems(folderPreviewItems);
        showCard(CARDFOLDERPREVIEWPANEL);
    }

    private boolean isFreeing;

    @Override
    public boolean isFreeing() {
        return isFreeing;
    }

    @Override
    public void free() {
        isFreeing = true;
    }

    public void setErrorState(ErrorState errorState) {
        statusPanel.setErrorState(errorState);
    }

    public static Timelined makeTimelined(final Tag tag) {
        return makeTimelined(tag, -1);
    }

    public static SWF makeTimelinedImage(ImageTag imageTag) {
        SWF swf = new SWF();
        swf.gfx = imageTag.getSwf().gfx;
        swf.version = imageTag.getSwf().version;
        int w = (int) (imageTag.getImageDimension().getWidth() * SWF.unitDivisor);
        int h = (int) (imageTag.getImageDimension().getHeight() * SWF.unitDivisor);
        swf.displayRect = new RECT(0, w, 0, h);
        swf.frameCount = 1;
        swf.frameRate = 1;
        swf.setFile(imageTag.getSwf().getFile()); //DefineSubImage calculates relative paths from it
        try {

            JPEGTablesTag jpegTablesTag = null;
            if (imageTag instanceof DefineBitsTag) {
                jpegTablesTag = imageTag.getSwf().getJtt();
            }
            Set<Integer> needed = new LinkedHashSet<>();
            imageTag.getNeededCharacters(needed);

            List<CharacterTag> neededCopies = new ArrayList<>();
            for (int n : needed) {
                CharacterTag ct = (CharacterTag) imageTag.getSwf().getCharacter(n).cloneTag();
                ct.setSwf(swf);
                neededCopies.add(ct);
            }

            ImageTag imageTagCopy = (ImageTag) imageTag.cloneTag();
            imageTagCopy.setSwf(swf);
            int imageCharId = imageTag.getCharacterId();
            DefineShape2Tag shapeTag = new DefineShape2Tag(swf);
            int shapeCharId = imageCharId + 1;
            shapeTag.shapeId = shapeCharId;
            shapeTag.shapeBounds = new RECT(swf.displayRect);

            SHAPEWITHSTYLE shapeData = new SHAPEWITHSTYLE();
            FILLSTYLEARRAY fillStyleArray = new FILLSTYLEARRAY();
            FILLSTYLE fillStyles[] = new FILLSTYLE[1];
            FILLSTYLE fillStyle = new FILLSTYLE();
            fillStyle.bitmapId = imageCharId;
            fillStyle.inShape3 = false;
            fillStyle.fillStyleType = CLIPPED_BITMAP;
            fillStyle.bitmapMatrix = Matrix.getScaleInstance(SWF.unitDivisor).toMATRIX();
            fillStyles[0] = fillStyle;
            fillStyleArray.fillStyles = fillStyles;
            shapeData.fillStyles = fillStyleArray;
            shapeData.lineStyles = new LINESTYLEARRAY();

            List<SHAPERECORD> shapeRecords = new ArrayList<>();

            StyleChangeRecord scr = new StyleChangeRecord();
            scr.stateFillStyle0 = true;
            scr.fillStyle0 = 1;
            shapeRecords.add(scr);

            StyleChangeRecord scr2 = new StyleChangeRecord();
            scr2.stateMoveTo = true;
            scr2.moveDeltaX = 0;
            scr2.moveDeltaY = 0;
            scr2.calculateBits();
            shapeRecords.add(scr2);

            StraightEdgeRecord ser1 = new StraightEdgeRecord();
            ser1.vertLineFlag = true;
            ser1.deltaY = h;
            ser1.calculateBits();
            shapeRecords.add(ser1);

            StraightEdgeRecord ser2 = new StraightEdgeRecord();
            ser2.deltaX = w;
            shapeRecords.add(ser2);

            StraightEdgeRecord ser3 = new StraightEdgeRecord();
            ser3.vertLineFlag = true;
            ser3.deltaY = -h;
            shapeRecords.add(ser3);

            StraightEdgeRecord ser4 = new StraightEdgeRecord();
            ser4.deltaX = -w;
            shapeRecords.add(ser4);

            shapeRecords.add(new EndShapeRecord());

            shapeData.shapeRecords = shapeRecords;

            shapeData.numFillBits = 1;
            shapeData.numLineBits = 0;

            shapeTag.shapes = shapeData;

            PlaceObjectTag placeTag = new PlaceObjectTag(swf, shapeCharId, 1, new Matrix().toMATRIX(), null);

            ShowFrameTag showFrameTag = new ShowFrameTag(swf);

            EndTag endTag = new EndTag(swf);

            if (jpegTablesTag != null) {
                swf.addTag(jpegTablesTag);
            }
            for (CharacterTag neededCopy : neededCopies) {
                swf.addTag(neededCopy);
            }
            swf.addTag(imageTagCopy);
            swf.addTag(shapeTag);
            swf.addTag(placeTag);
            swf.addTag(showFrameTag);
            swf.addTag(endTag);

        } catch (InterruptedException | IOException ex) {
            //ignore
        }
        return swf;
    }

    public static Timelined makeTimelined(final Tag tag, final int fontFrameNum) {

        return new Timelined() {
            private Timeline tim;

            @Override
            public Timeline getTimeline() {
                if (tim == null) {
                    Timeline timeline = new Timeline(tag.getSwf(), this, ((CharacterTag) tag).getCharacterId(), getRect());
                    initTimeline(timeline);
                    tim = timeline;
                }

                return tim;
            }

            @Override
            public void resetTimeline() {
                if (tim != null) {
                    tim.reset(tag.getSwf(), this, ((CharacterTag) tag).getCharacterId(), getRect());
                    initTimeline(tim);
                }
            }

            private void initTimeline(Timeline timeline) {
                if (tag instanceof MorphShapeTag) {
                    timeline.frameRate = PreviewExporter.MORPH_SHAPE_ANIMATION_FRAME_RATE;
                    int framesCnt = (int) (timeline.frameRate * PreviewExporter.MORPH_SHAPE_ANIMATION_LENGTH);
                    for (int i = 0; i < framesCnt; i++) {
                        Frame f = new Frame(timeline, i);
                        DepthState ds = new DepthState(tag.getSwf(), f);
                        ds.characterId = ((CharacterTag) tag).getCharacterId();
                        ds.matrix = new MATRIX();
                        ds.ratio = i * 65535 / framesCnt;
                        f.layers.put(1, ds);
                        f.layersChanged = true;
                        timeline.addFrame(f);
                    }
                } else if (tag instanceof FontTag) {
                    int pageCount = PreviewPanel.getFontPageCount((FontTag) tag);
                    int frame = fontFrameNum;
                    if (frame < 0 || frame >= pageCount) {
                        frame = 0;
                    }

                    Frame f = new Frame(timeline, 0);
                    DepthState ds = new DepthState(tag.getSwf(), f);
                    ds.characterId = ((CharacterTag) tag).getCharacterId();
                    ds.matrix = new MATRIX();
                    f.layers.put(1, ds);
                    f.layersChanged = true;
                    timeline.addFrame(f);
                    timeline.fontFrameNum = frame;
                } else {
                    Frame f = new Frame(timeline, 0);
                    DepthState ds = new DepthState(tag.getSwf(), f);
                    ds.characterId = ((CharacterTag) tag).getCharacterId();
                    ds.matrix = new MATRIX();
                    f.layers.put(1, ds);
                    timeline.addFrame(f);
                }
                timeline.displayRect = getRect();
            }

            @Override
            public RECT getRect() {
                return getRect(new HashSet<>());
            }

            @Override
            public RECT getRect(Set<BoundedTag> added) {
                BoundedTag bt = (BoundedTag) tag;
                if (!added.contains(bt)) {
                    return bt.getRect(added);
                }
                return new RECT(Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE);
            }

            @Override
            public int hashCode() {
                return tag.hashCode();
            }

            @Override
            public void setModified(boolean value) {
            }

            @Override
            public ReadOnlyTagList getTags() {
                return ReadOnlyTagList.EMPTY;
            }

            @Override
            public void removeTag(int index) {
            }

            @Override
            public void removeTag(Tag tag) {
            }

            @Override
            public void addTag(Tag tag) {
            }

            @Override
            public void addTag(int index, Tag tag) {
            }

            @Override
            public void replaceTag(int index, Tag newTag) {
            }

            @Override
            public void replaceTag(Tag oldTag, Tag newTag) {
            }                        

            @Override
            public int indexOfTag(Tag tag) {
                return -1;
            }

            @Override
            public RECT getRectWithStrokes() {
                return getRect();
            }
        };
    }

    private void disposeInner(Container container) {
        for (Component c : container.getComponents()) {
            if (c instanceof Container) {
                Container c2 = (Container) c;
                disposeInner(c2);
            }
        }

        container.removeAll();
        container.setLayout(null);
        if (container instanceof TagEditorPanel) {
            Helper.emptyObject(container);
        }
    }

    public void dispose() {
        if (calculateMissingNeededThread != null) {
            calculateMissingNeededThread.interrupt();
        }
        setDropTarget(null);
        disposeInner(this);
        Helper.emptyObject(this);
    }

    private static void calculateMissingNeededCharacters(Map<TreeItem, Set<Integer>> missingNeededCharacters, Timelined tim) {
        for (Tag t : tim.getTags()) {
            missingNeededCharacters.put(t, t.getMissingNeededCharacters());
            if (t instanceof DefineSpriteTag) {
                calculateMissingNeededCharacters(missingNeededCharacters, (DefineSpriteTag) t);
            }
        }
    }

    public void calculateMissingNeededCharacters() {
        Map<TreeItem, Set<Integer>> missingNeededCharacters = new WeakHashMap<>();
        List<SWFList> swfsLists = new ArrayList<>(swfs);
        for (SWFList swfList : swfsLists) {
            for (SWF swf : swfList) {
                calculateMissingNeededCharacters(missingNeededCharacters, swf);
            }
        }
        this.missingNeededCharacters = missingNeededCharacters;
        tagTree.setMissingNeededCharacters(missingNeededCharacters);
        tagListTree.setMissingNeededCharacters(missingNeededCharacters);
    }       

}
