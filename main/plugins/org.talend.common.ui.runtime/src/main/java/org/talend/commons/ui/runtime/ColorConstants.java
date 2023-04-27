// ============================================================================
//
// Copyright (C) 2006-2021 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.commons.ui.runtime;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

/**
 * DOC cmeng  class global comment. Detailled comment
 */
public interface ColorConstants {

    static final String BUNDLE_ID_COMMON_UI_RUNTIME = "org.talend.common.ui.runtime";

    static final String KEY_TABLE_BACKGROUND = "table.background";

    static final String KEY_TABLE_FOREGROUND = "table.foreground";

    static final Color WHITE_COLOR = new Color(null, 255, 255, 255);

    static final Color GREY_COLOR = new Color(null, 215, 215, 215);

    static final Color YELLOW_GREEN_COLOR = new Color(null, 88,153,24);// 143, 163, 35

    static final Color YELLOW_COLOR = new Color(null, 255, 173, 37);// 254, 182, 84

    static final Color RED_COLOR = new Color(null, new RGB(204, 87, 89));// 255
    
    static final Color ERROR_FONT_COLOR = new Color(null, new RGB(255, 0, 0));// 255

    static final Color VERTICAL_SEPERATOR_LINE_COLOR = new Color(null, 162, 179, 195);

    static final Color LOCHMARA_COLOR = new Color(null, 6, 117, 193);

    static final Color WATHET_COLOR = new Color(null, 135, 206, 235);

    static final Color INFO_COLOR = new Color(null, 205, 227, 242);
//    static final Color INFO_COLOR = YELLOW_GREEN_COLOR;

    static final Color WARN_COLOR = new Color(null, 252, 230, 217);

    static final Color ERR_COLOR = new Color(null, 255, 235, 235);

    static final Color SUCCEED_COLOR = new Color(null, 221, 242, 217);

    static Color getTableBackgroundColor() {
        return ITalendThemeService.getColor(ColorConstants.BUNDLE_ID_COMMON_UI_RUNTIME, ColorConstants.KEY_TABLE_BACKGROUND)
                .orElse(Display.getDefault().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
    }

    static Color getTableForegroundColor() {
        return ITalendThemeService.getColor(ColorConstants.BUNDLE_ID_COMMON_UI_RUNTIME, ColorConstants.KEY_TABLE_FOREGROUND)
                .orElse(Display.getDefault().getSystemColor(SWT.COLOR_LIST_FOREGROUND));
    }
    
    static Color getTableReadOnlyForegroundColor() {
        return ITalendThemeService.getColor("CONTEXT_TABLE_READONLY_FOREGROUND")
                .orElse(Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_DARK_SHADOW));
    }

}
