/*
 * Copyright (C) 2022 JPEXS
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.jpexs.decompiler.flash.gui;

import com.jpexs.decompiler.flash.SWFCompression;
import com.jpexs.decompiler.flash.gui.helpers.TableLayoutHelper;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Window;
import java.awt.event.ActionEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.event.ChangeEvent;
import layout.TableLayout;

/**
 *
 * @author JPEXS
 */
public class NewFileDialog extends AppDialog {        

    private final JPanel compressionEditorPanel = new JPanel();

    private final JComboBox<ComboBoxItem<SWFCompression>> compressionComboBox = new JComboBox<>();

    private final JPanel versionEditorPanel = new JPanel();

    private final JSpinner versionEditor = new JSpinner();

    private final JCheckBox gfxCheckBox = new JCheckBox();

    private final JPanel frameRateEditorPanel = new JPanel();

    private final JSpinner frameRateEditor = new JSpinner();

    private final JPanel propertiesPanel = new JPanel();

    private final JPanel buttonsPanel = new JPanel();
   
    private final JButton okButton = new JButton(AppStrings.translate("button.ok"));

    private final JButton cancelButton = new JButton(AppStrings.translate("button.cancel"));

    private final JPanel displayRectEditorPanel = new JPanel();

    private final JSpinner xMinEditor = new JSpinner();

    private final JSpinner xMaxEditor = new JSpinner();

    private final JSpinner yMinEditor = new JSpinner();

    private final JSpinner yMaxEditor = new JSpinner();

    private final JPanel warningPanel = new JPanel();

    private final JLabel warningLabel = new JLabel();
    
    private JButton backgroundColorButton;

    private int result = ERROR_OPTION;
    
    public NewFileDialog(Window owner) {
        super(owner);
        
        setTitle(translate("dialog.title"));
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        Container cnt = getContentPane();
        cnt.setLayout(new BorderLayout());
        
        cnt.setLayout(new BorderLayout());

        TableLayout tl;
        propertiesPanel.setLayout(tl = new TableLayout(new double[][]{
            {TableLayout.PREFERRED, TableLayout.FILL},
            {TableLayout.PREFERRED, TableLayout.PREFERRED, TableLayout.PREFERRED,
                TableLayout.PREFERRED, TableLayout.PREFERRED, TableLayout.PREFERRED,
                TableLayout.PREFERRED, TableLayout.PREFERRED, TableLayout.PREFERRED,
                TableLayout.PREFERRED}
        }));

        FlowLayout layout = new FlowLayout(SwingConstants.WEST);
        layout.setHgap(0);
        layout.setVgap(0);

        compressionEditorPanel.setLayout(layout);
        compressionComboBox.addItem(new ComboBoxItem<>(AppStrings.translate("header.uncompressed"), SWFCompression.NONE));
        compressionComboBox.addItem(new ComboBoxItem<>("Zlib", SWFCompression.ZLIB));
        compressionComboBox.addItem(new ComboBoxItem<>("LZMA", SWFCompression.LZMA));
        compressionComboBox.addActionListener((ActionEvent e) -> {
            validateHeader();
        });
        compressionEditorPanel.add(compressionComboBox);

        versionEditorPanel.setLayout(layout);
        versionEditor.setPreferredSize(new Dimension(80, versionEditor.getPreferredSize().height));
        versionEditor.addChangeListener((ChangeEvent e) -> {
            validateHeader();
        });
        versionEditorPanel.add(versionEditor);

        gfxCheckBox.addActionListener((ActionEvent e) -> {
            validateHeader();
        });

        frameRateEditorPanel.setLayout(layout);
        frameRateEditor.setPreferredSize(new Dimension(80, frameRateEditor.getPreferredSize().height));
        frameRateEditorPanel.add(frameRateEditor);

        displayRectEditorPanel.setLayout(layout);
        displayRectEditorPanel.setMinimumSize(new Dimension(10, displayRectEditorPanel.getMinimumSize().height));
        xMinEditor.setPreferredSize(new Dimension(80, xMinEditor.getPreferredSize().height));
        xMaxEditor.setPreferredSize(new Dimension(80, xMaxEditor.getPreferredSize().height));
        yMinEditor.setPreferredSize(new Dimension(80, yMinEditor.getPreferredSize().height));
        yMaxEditor.setPreferredSize(new Dimension(80, yMaxEditor.getPreferredSize().height));
        displayRectEditorPanel.add(xMinEditor);
        displayRectEditorPanel.add(new JLabel(","));
        displayRectEditorPanel.add(yMinEditor);
        displayRectEditorPanel.add(new JLabel(" => "));
        displayRectEditorPanel.add(xMaxEditor);
        displayRectEditorPanel.add(new JLabel(","));
        displayRectEditorPanel.add(yMaxEditor);
        displayRectEditorPanel.add(new JLabel(" twips"));

        warningLabel.setIcon(View.getIcon("warning16"));
        warningPanel.setLayout(layout);
        warningPanel.setBackground(new Color(255, 213, 29));
        warningPanel.add(warningLabel);
        warningPanel.setVisible(false);
        
        backgroundColorButton = new JButton("") {

            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(getBackground());
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintBorder(g);
            }

        };
        backgroundColorButton.setToolTipText(AppStrings.translate("button.selectcolor.hint"));
        backgroundColorButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backgroundColorButton.addActionListener(this::selectBackgroundColorActionPerformed);

        backgroundColorButton.setBorderPainted(true);
        backgroundColorButton.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        Dimension colorDim = new Dimension(16, 16);
        backgroundColorButton.setSize(colorDim);
        backgroundColorButton.setPreferredSize(colorDim);
        backgroundColorButton.setMaximumSize(colorDim);
        
        JPanel backgroundColorPanel = new JPanel(new BorderLayout());
        backgroundColorPanel.add(backgroundColorButton, BorderLayout.WEST);
                
        propertiesPanel.add(new JLabel(AppStrings.translate("header.compression")), "0,0");
        propertiesPanel.add(compressionEditorPanel, "1,0");
        propertiesPanel.add(new JLabel(AppStrings.translate("header.version")), "0,1");
        propertiesPanel.add(versionEditorPanel, "1,1");
        propertiesPanel.add(new JLabel(AppStrings.translate("header.gfx")), "0,2");
        propertiesPanel.add(gfxCheckBox, "1,2");
        propertiesPanel.add(new JLabel(AppStrings.translate("header.framerate")), "0,3");
        propertiesPanel.add(frameRateEditorPanel, "1,3");
        propertiesPanel.add(new JLabel(AppStrings.translate("header.displayrect")), "0,4");
        propertiesPanel.add(displayRectEditorPanel, "1,4");
        propertiesPanel.add(warningPanel, "0,5,1,5");
        propertiesPanel.add(new JLabel(translate("background.color")), "0,6");
                
        propertiesPanel.add(backgroundColorPanel, "1,6");

        cnt.add(propertiesPanel, BorderLayout.CENTER);

        
        okButton.addActionListener(this::okButtonActionPerformed);

        cancelButton.addActionListener(this::cancelButtonActionPerformed);

        buttonsPanel.setLayout(new FlowLayout());
        //buttonsPanel.setBorder(new BevelBorder(BevelBorder.RAISED));
        buttonsPanel.add(okButton);
        buttonsPanel.add(cancelButton);
        cnt.add(buttonsPanel, BorderLayout.SOUTH);
        
        //TableLayoutHelper.addTableSpaces(tl, 10);   
        pack();
        setResizable(false);
        View.centerScreen(this);
        View.setWindowIcon(this);
        setModal(true);
        
        xMaxEditor.setValue(20 * 550);
        yMaxEditor.setValue(20 * 400);
        frameRateEditor.setValue(24);
        compressionComboBox.setSelectedIndex(1);
        versionEditor.setValue(17);        
        backgroundColorButton.setBackground(Color.white);
    }
    
    private void selectBackgroundColorActionPerformed(ActionEvent evt) {
        Color newColor = JColorChooser.showDialog(null, AppStrings.translate("dialog.selectcolor.title"), backgroundColorButton.getBackground());
        if (newColor != null) {
            backgroundColorButton.setBackground(newColor);
        }
    }
    
    private boolean validateHeader() {
        int version = getVersionNumber();
        boolean gfx = gfxCheckBox.isSelected();
        SWFCompression compression = getCompression();
        String resultStr = "";
        boolean result = true;
        if (gfx && !(compression == SWFCompression.NONE || compression == SWFCompression.ZLIB)) {
            resultStr += AppStrings.translate("header.warning.unsupportedGfxCompression") + " ";
            result = false;
        }

        if (compression == SWFCompression.ZLIB && version < 6) {
            resultStr += AppStrings.translate("header.warning.minimumZlibVersion") + " ";
            result = false;
        }

        if (compression == SWFCompression.LZMA && version < 13) {
            resultStr += AppStrings.translate("header.warning.minimumLzmaVersion") + " ";
            result = false;
        }

        warningPanel.setVisible(!result);
        if (!result) {
            warningLabel.setText(resultStr);
        }

        return result;
    }
    
    public int showDialog() {
        setVisible(true);
        return result;
    }
    
    private void okButtonActionPerformed(ActionEvent evt) {
        result = OK_OPTION;
        setVisible(false);
    }
    
    private void cancelButtonActionPerformed(ActionEvent evt) {
        result = CANCEL_OPTION;
        setVisible(false);
    }
    
    public int getVersionNumber() {
        return (int) versionEditor.getModel().getValue();
    }

    public SWFCompression getCompression() {
        @SuppressWarnings("unchecked")
        ComboBoxItem<SWFCompression> item = (ComboBoxItem<SWFCompression>) compressionComboBox.getSelectedItem();
        return item.getValue();
    }
    
    public boolean isGfx() {
        return gfxCheckBox.isSelected();
    }
    
    public int getFrameRate() {
        return (int) ((Number) (frameRateEditor.getModel().getValue())).floatValue();
    }
    
    public int getXMin() {
        return (int) xMinEditor.getModel().getValue();
    }
    public int getXMax() {
        return (int) xMaxEditor.getModel().getValue();
    }
    public int getYMin() {
        return (int) yMinEditor.getModel().getValue();
    }
    public int getYmax() {
        return (int) yMaxEditor.getModel().getValue();
    }
    
    public Color getBackgroundColor() {
        return backgroundColorButton.getBackground();
    }
}
