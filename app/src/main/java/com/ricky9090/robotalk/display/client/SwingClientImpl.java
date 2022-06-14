package com.ricky9090.robotalk.display.client;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ricky9090.robotalk.R;
import com.ricky9090.robotalk.widget.DroidBorderPanel;
import com.ricky9090.robotalk.widget.DroidListAdapter;
import com.ricky9090.robotalk.widget.DroidWindow;
import com.ricky9090.robotalk.widget.EditTextSelected;
import com.ricky9090.smallworld.SmallWorld;
import com.ricky9090.smallworld.display.IScreenClient;
import com.ricky9090.smallworld.obj.SmallObject;
import com.ricky9090.smallworld.view.UIConst;
import com.ricky9090.smallworld.view.advui.STBorderPanel;
import com.ricky9090.smallworld.view.advui.STButton;
import com.ricky9090.smallworld.view.advui.STGridPanel;
import com.ricky9090.smallworld.view.advui.STLabelPanel;
import com.ricky9090.smallworld.view.advui.STListView;
import com.ricky9090.smallworld.view.advui.STMenu;
import com.ricky9090.smallworld.view.advui.STMenuItem;
import com.ricky9090.smallworld.view.advui.STScrollBar;
import com.ricky9090.smallworld.view.advui.STTextArea;
import com.ricky9090.smallworld.view.advui.STTextField;
import com.ricky9090.smallworld.view.advui.STTextView;
import com.ricky9090.smallworld.view.advui.STView;
import com.ricky9090.smallworld.view.advui.STWindow;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SwingClientImpl extends Thread implements IScreenClient {

    public boolean running = true;

    private final Map<String, WindowPair> localWindowMap = new HashMap<>();

    private final List<UIAction> uiActionList = new LinkedList<>();

    private final Context activityContext;
    private final Handler uiHandler;

    public SwingClientImpl(Handler handler, Context context) {
        this.uiHandler = handler;
        this.activityContext = context;
    }

    public void clearWindow() {
        localWindowMap.clear();
    }

    /**
     * Do not need while loop in Android
     */
    @Deprecated
    @Override
    public void run() {
        while (running) {
            try {
                synchronized (SmallWorld.RENDER_LOCK) {
                    while (uiActionList.size() == 0) {
                        SmallWorld.RENDER_LOCK.wait(20);
                    }

                    // dirty check
                    // update
                    //onUpdate();

                    SmallWorld.RENDER_LOCK.notifyAll();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onUpdate(STView target, int action) {
        //UIAction uiAction = uiActionList.remove(0);

        final STView dirtyTarget = target;
        final int dirtyAction = action;
        System.out.println("Swing Client on Update: " + dirtyAction);
        if (dirtyAction == UIConst.PRIM_60_CREATE_WINDOW) {
            STWindow window = (STWindow) dirtyTarget;
            WindowPair pair = new WindowPair();
            pair.stWindow = window;
            String tag = pair.stWindow.getId() + "";

            localWindowMap.put(tag, pair);
        } else if (dirtyAction == UIConst.PRIM_61_CHANGE_WINDOW_VISIBILITY) {
            // build real window only when the whole window has been completely created
            STWindow window = (STWindow) dirtyTarget;
            String windowTag = window.getId() + "";

            DroidWindow _androidWindow = buildAndroidWindow(window);
            //_swingWindow.setVisible(true);

            WindowPair pair = localWindowMap.get(windowTag);
            if (pair != null) {
                pair.androidWindow = _androidWindow;
            }

            Message msg = Message.obtain();
            msg.what = 99;
            msg.obj = _androidWindow;
            uiHandler.sendMessage(msg);
        } else if (dirtyAction == UIConst.PRIM_66_REPAINT_WINDOW) {
            STWindow window = (STWindow) dirtyTarget;
            String windowTag = window.getId() + "";
            WindowPair pair = localWindowMap.get(windowTag);
            if (pair != null && pair.androidWindow != null) {
                pair.androidWindow.invalidate();
            }
        } else if (dirtyAction == UIConst.PRIM_84_SET_LIST_DATA) {
            String targetTag = dirtyTarget.getId() + "";
            WidgetPair targetPair = findWidgetPairInWindowMap(targetTag);

            if (targetPair != null) {
                try {
                    View component = targetPair.androidWidget;
                    if (component == null) {
                        return;
                    }

                    STListView targetListView = (STListView) targetPair.stWidget;

                    if (component instanceof RecyclerView) {
                        DroidListAdapter adapter = (DroidListAdapter) ((RecyclerView) component).getAdapter();
                        adapter.setDataList(targetListView.getDataList());
                        adapter.setListener(new DroidListAdapter.DroidListListener() {
                            @Override
                            public void onItemClick(int index, SmallObject data) {
                                targetListView.setSelectedIndex(index);
                                targetListView.getListener().onListItemClick(index, data);
                            }
                        });
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (dirtyAction == UIConst.PRIM_82_SET_VIEW_TEXT) {
            String targetTag = dirtyTarget.getId() + "";
            WidgetPair targetPair = findWidgetPairInWindowMap(targetTag);

            if (targetPair != null) {
                try {
                    View component = targetPair.androidWidget;
                    if (component == null) {
                        return;
                    }

                    STTextView targetTextView = (STTextView) targetPair.stWidget;

                    if (component instanceof EditTextSelected) {
                        ((EditTextSelected) component).setText(targetTextView.getText());
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (dirtyAction == UIConst.PRIM_89_SET_SELECTED_TEXT) {
            String targetTag = dirtyTarget.getId() + "";
            WidgetPair targetPair = findWidgetPairInWindowMap(targetTag);

            if (targetPair != null) {
                try {
                    View component = targetPair.androidWidget;
                    if (component == null) {
                        return;
                    }

                    STTextView targetTextView = (STTextView) targetPair.stWidget;

                    if (component instanceof EditTextSelected) {
                        ((EditTextSelected) component).setText(targetTextView.getSelectedText());
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private DroidWindow buildAndroidWindow(STWindow window) {
        DroidWindow androidWindow = new DroidWindow(activityContext);

        // size

        // title
        androidWindow.setTitle(window.getTitle());

        // menu
        List<STMenu> menuList = window.getMenuList();
        for (STMenu menu : menuList) {
            View androidMenu = buildMenu(window, menu);
            androidWindow.addMenu(androidMenu);

            String widgetTag = menu.getId() + "";
            String windowTag = window.getId() + "";

            WidgetPair widgetPair = new WidgetPair(androidMenu, menu);
            localWindowMap.get(windowTag).registerWidget(widgetTag, widgetPair);
        }

        // content
        View content = buildContent(window, window.getContent());
        if (content != null) {
            androidWindow.addWindowContent(content);
        }

        androidWindow.setWindowCloseListner(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                window.getListener().onWindowClosing();
                androidWindow.onClose();
            }
        });

        return androidWindow;
    }

    private View buildMenu(STWindow window, STMenu menu) {
        TextView androidMenu = new TextView(activityContext);
        androidMenu.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        ));
        androidMenu.setTextSize(20);
        androidMenu.setText(menu.getText());

        androidMenu.setPadding(20, 20, 20, 20);

        LinearLayout menuView = new LinearLayout(activityContext);
        menuView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        menuView.setOrientation(LinearLayout.VERTICAL);
        menuView.setBackgroundColor(activityContext.getResources().getColor(R.color.teal_700));

        List<STMenuItem> itemList = menu.getItemList();
        for (STMenuItem item : itemList) {
            View androidMenuItem = buildMenuItem(item);
            menuView.addView(androidMenuItem);

            String widgetTag = item.getId() + "";
            String windowTag = window.getId() + "";

            WidgetPair widgetPair = new WidgetPair(androidMenuItem, item);

            localWindowMap.get(windowTag).registerWidget(widgetTag, widgetPair);
        }
        final PopupWindow popupWindow = new PopupWindow(menuView, 1000, 300);
        popupWindow.setOutsideTouchable(true);
        androidMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.showAsDropDown(androidMenu);
            }
        });

        return androidMenu;
    }

    private View buildMenuItem(STMenuItem menuItem) {
        TextView androidMenuItem = new TextView(activityContext);
        androidMenuItem.setText(menuItem.getText());
        androidMenuItem.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        androidMenuItem.setTextSize(18);
        androidMenuItem.setPadding(10, 10, 10, 10);
        androidMenuItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuItem.getListener().onClick();
            }
        });
        return androidMenuItem;
    }

    private View buildContent(STWindow window, STView content) {
        View realWidget = dispatchBuildView(window, content);
        String widgetTag = content.getId() + "";
        String windowTag = window.getId() + "";

        WidgetPair widgetPair = new WidgetPair(realWidget, content);
        localWindowMap.get(windowTag).registerWidget(widgetTag, widgetPair);
        return realWidget;
    }

    private View dispatchBuildView(STWindow window, STView view) {
        if (view instanceof STTextField) {
            return buildTextField((STTextField) view);
        }
        if (view instanceof STTextArea) {
            return buildTextArea((STTextArea) view);
        }
        if (view instanceof STLabelPanel) {
            return buildLabelPanel((STLabelPanel) view);
        }
        if (view instanceof STScrollBar) {
            return buildScrollBar((STScrollBar) view);
        }
        if (view instanceof STButton) {
            return buildButton((STButton) view);
        }
        if (view instanceof STListView) {
            return buildListView((STListView) view);
        }
        if (view instanceof STBorderPanel) {
            return buildBorderPanel(window, (STBorderPanel) view);
        }
        if (view instanceof STGridPanel) {
            return buildGridPanel(window, (STGridPanel) view);
        }
        return null;
    }

    private View buildTextField(STTextField textField) {
        EditTextSelected androidField = new EditTextSelected(activityContext);
        androidField.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        androidField.setSingleLine(true);
        androidField.setMinWidth(500);
        androidField.setMinHeight(80);
        androidField.setTextSize(14);
        androidField.setTypeface(Typeface.MONOSPACE);
        androidField.setText(textField.getText());
        androidField.setTextColor(activityContext.getResources().getColor(R.color.console_green));
        androidField.setBackgroundColor(activityContext.getResources().getColor(R.color.black));
        androidField.setListener(new EditTextSelected.SelectionListener() {
            @Override
            public void onSelectedChange(String selected) {
                textField.setSelectedText(selected);
            }
        });
        androidField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                textField.setText(androidField.getText().toString());
            }
        });
        return androidField;
    }

    private View buildTextArea(STTextArea textArea) {
        EditTextSelected androidTextArea = new EditTextSelected(activityContext);
        androidTextArea.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        androidTextArea.setMinWidth(500);
        androidTextArea.setMinHeight(80);
        androidTextArea.setTextSize(14);
        androidTextArea.setTypeface(Typeface.MONOSPACE);
        androidTextArea.setText(textArea.getText());
        androidTextArea.setTextColor(activityContext.getResources().getColor(R.color.console_green));
        androidTextArea.setBackgroundColor(activityContext.getResources().getColor(R.color.black));
        androidTextArea.setListener(new EditTextSelected.SelectionListener() {
            @Override
            public void onSelectedChange(String selected) {
                textArea.setSelectedText(selected);
            }
        });
        androidTextArea.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                textArea.setText(androidTextArea.getText().toString());
            }
        });
        return androidTextArea;
    }

    private View buildLabelPanel(STLabelPanel labelPanel) {
        STTextView textView = labelPanel.getTextView();
        TextView androidText = new TextView(activityContext);
        androidText.setTextSize(18);
        androidText.setPadding(10, 10, 10, 10);
        androidText.setText(textView.getText());
        return androidText;
    }

    private View buildScrollBar(STScrollBar scrollBar) {
        return null;
    }

    private View buildButton(STButton button) {
        Button androidButton = new Button(activityContext);
        androidButton.setText(button.getText());
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        androidButton.setLayoutParams(layoutParams);
        androidButton.setTextSize(14);
        androidButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button.getListener().onClick();
            }
        });
        return androidButton;
    }

    private View buildListView(STListView listView) {
        RecyclerView androidList = new RecyclerView(activityContext);
        androidList.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 400));
        androidList.setMinimumWidth(150);
        androidList.setMinimumHeight(300);
        androidList.setBackgroundResource(R.drawable.list_background);

        DroidListAdapter adapter = new DroidListAdapter(activityContext);
        androidList.setLayoutManager(new LinearLayoutManager(activityContext));
        androidList.setAdapter(adapter);
        adapter.setListener(new DroidListAdapter.DroidListListener() {
            @Override
            public void onItemClick(int index, SmallObject data) {
                listView.setSelectedIndex(index);
                listView.getListener().onListItemClick(index, data);
            }
        });

        List<SmallObject> listData = listView.getDataList();
        adapter.setDataList(listData);

        return androidList;
    }

    private View buildBorderPanel(STWindow window, STBorderPanel borderPanel) {
        DroidBorderPanel androidPanel = new DroidBorderPanel(activityContext);
        androidPanel.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        String windowTag = window.getId() + "";

        if (borderPanel.getCenter() != null) {
            STView stCenter = borderPanel.getCenter();
            View androidCenter = dispatchBuildView(window, stCenter);
            androidPanel.addCenter(androidCenter);
            String tag = borderPanel.getCenter().getId() + "";

            WidgetPair widgetPair = new WidgetPair(androidCenter, stCenter);
            localWindowMap.get(windowTag).registerWidget(tag, widgetPair);
        }
        if (borderPanel.getTop() != null) {
            STView stTop = borderPanel.getTop();
            View androidTop = dispatchBuildView(window, stTop);
            androidPanel.addTop(androidTop);
            String tag = borderPanel.getTop().getId() + "";

            WidgetPair widgetPair = new WidgetPair(androidTop, stTop);
            localWindowMap.get(windowTag).registerWidget(tag, widgetPair);
        }
        if (borderPanel.getRight() != null) {
            STView stRight = borderPanel.getRight();
            View androidRight = dispatchBuildView(window, stRight);
            androidPanel.addRight(androidRight);
            String tag = borderPanel.getRight().getId() + "";

            WidgetPair widgetPair = new WidgetPair(androidRight, stRight);
            localWindowMap.get(windowTag).registerWidget(tag, widgetPair);
        }
        if (borderPanel.getBottom() != null) {
            STView stBottom = borderPanel.getBottom();
            View androidBottom = dispatchBuildView(window, stBottom);
            androidPanel.addBottom(androidBottom);
            String tag = borderPanel.getBottom().getId() + "";

            WidgetPair widgetPair = new WidgetPair(androidBottom, stBottom);
            localWindowMap.get(windowTag).registerWidget(tag, widgetPair);
        }
        if (borderPanel.getLeft() != null) {
            STView stLeft = borderPanel.getLeft();
            View androidLeft = dispatchBuildView(window, stLeft);
            androidPanel.addLeft(androidLeft);
            String tag = borderPanel.getLeft().getId() + "";

            WidgetPair widgetPair = new WidgetPair(androidLeft, stLeft);
            localWindowMap.get(windowTag).registerWidget(tag, widgetPair);
        }
        return androidPanel;
    }

    private View buildGridPanel(STWindow window, STGridPanel gridPanel) {
        GridLayout androidGrid = new GridLayout(activityContext);
        androidGrid.setRowCount(gridPanel.getRows());
        androidGrid.setColumnCount(gridPanel.getCols());

        List<STView> children = gridPanel.getChildren();
        for (STView child : children) {
            View androidChild = dispatchBuildView(window, child);
            androidGrid.addView(androidChild);
            String tag = child.getId() + "";
            String windowTag = window.getId() + "";

            WidgetPair widgetPair = new WidgetPair(androidChild, child);
            localWindowMap.get(windowTag).registerWidget(tag, widgetPair);
        }
        return androidGrid;
    }

    private WidgetPair findWidgetPairInWindowMap(String tag) {

        Set<Map.Entry<String, WindowPair>> entrySet = localWindowMap.entrySet();
        for (Map.Entry<String, WindowPair> entry : entrySet) {
            WindowPair windowPair = entry.getValue();
            if (windowPair != null) {
                WidgetPair result = windowPair.findWidgetPair(tag);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    @Override
    public void commit(STView target, int action) {
        UIAction uiAction = new UIAction();
        uiAction.target = target;
        uiAction.action = action;
        // do not need uiActionList in Android
        //uiActionList.add(uiAction);

        Message message = Message.obtain();
        message.obj = uiAction;
        uiHandler.sendMessage(message);
    }

    public static class UIAction {
        public STView target;
        public int action;
    }

    public static class WindowPair {
        public DroidWindow androidWindow;
        public STView stWindow;

        // Flat widgetMap for fast searching view in the view tree
        final Map<String, WidgetPair> realWidgetMap = new HashMap<>();

        public void registerWidget(String tag, WidgetPair widgetPair) {
            realWidgetMap.put(tag, widgetPair);
        }

        public WidgetPair findWidgetPair(String tag) {
            return realWidgetMap.get(tag);
        }
    }

    public static class WidgetPair {
        public View androidWidget;
        public STView stWidget;

        public WidgetPair(View androidWidget, STView stWidget) {
            this.androidWidget = androidWidget;
            this.stWidget = stWidget;
        }
    }

}
