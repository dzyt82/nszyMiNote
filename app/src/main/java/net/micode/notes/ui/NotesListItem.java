/*
 * Copyright (c) 2010-2011, The MiCode Open Source Community (www.micode.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.micode.notes.ui;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.micode.notes.R;
import net.micode.notes.data.Notes;
import net.micode.notes.tool.DataUtils;
import net.micode.notes.tool.ResourceParser.NoteItemBgResources;

/** 便签列表的单元素，视图类，DONE */
public class NotesListItem extends LinearLayout {
    private ImageView mAlert;//闹钟图片
    private TextView mTitle;//标题
    private TextView mTime;//时间
    private TextView mCallName;
    private NoteItemData mItemData;//标签数据
    private CheckBox mCheckBox;//勾选框
    //初始化基本信息
    public NotesListItem(Context context) {
        super(context);
        inflate(context, R.layout.note_item, this);
        //Inflate可用于将一个xml中定义的布局控件找出来,这里的xml是R.layout
        mAlert = (ImageView) findViewById(R.id.iv_alert_icon);
        mTitle = (TextView) findViewById(R.id.tv_title);
        mTime = (TextView) findViewById(R.id.tv_time);
        mCallName = (TextView) findViewById(R.id.tv_name);
        mCheckBox = (CheckBox) findViewById(android.R.id.checkbox);
        //findViewById用于从contentView中查找指定ID的View
    }
    /* 将数据类NoteItemData绑定到本View上用于显示 */
    public void bind(Context context, NoteItemData data, boolean choiceMode, boolean checked) {
        if (choiceMode && data.getType() == Notes.TYPE_NOTE) {
            mCheckBox.setVisibility(View.VISIBLE);//设置可见
            mCheckBox.setChecked(checked);//勾选
        } else {
            mCheckBox.setVisibility(View.GONE);
        }

        mItemData = data;
        //设置控件属性，根据data的id和父id是否与保存到文件夹的id一致来决定
        if (data.getId() == Notes.ID_CALL_RECORD_FOLDER) {
            mCallName.setVisibility(View.GONE);
            mAlert.setVisibility(View.VISIBLE);
            //设置该textview的style
            mTitle.setTextAppearance(context, R.style.TextAppearancePrimaryItem);
            //settext为设置内容
            mTitle.setText(context.getString(R.string.call_record_folder_name)
                    + context.getString(R.string.format_folder_files_count, data.getNotesCount()));
            mAlert.setImageResource(R.drawable.call_record);
        } else if (data.getParentId() == Notes.ID_CALL_RECORD_FOLDER) {
            mCallName.setVisibility(View.VISIBLE);
            mCallName.setText(data.getCallName());
            mTitle.setTextAppearance(context,R.style.TextAppearanceSecondaryItem);
            mTitle.setText(DataUtils.getFormattedSnippet(data.getSnippet()));
            //关于闹钟的设置
            if (data.hasAlert()) {
                mAlert.setImageResource(R.drawable.clock);
                mAlert.setVisibility(View.VISIBLE);
            } else {
                mAlert.setVisibility(View.GONE);
            }
        } else {
            mCallName.setVisibility(View.GONE);
            mTitle.setTextAppearance(context, R.style.TextAppearancePrimaryItem);
            //设置title格式
            if (data.getType() == Notes.TYPE_FOLDER) {
                mTitle.setText(data.getSnippet()
                        + context.getString(R.string.format_folder_files_count,
                                data.getNotesCount()));
                mAlert.setVisibility(View.GONE);
            } else {
                mTitle.setText(DataUtils.getFormattedSnippet(data.getSnippet()));
                if (data.hasAlert()) {
                    mAlert.setImageResource(R.drawable.clock);
                    mAlert.setVisibility(View.VISIBLE);
                } else {
                    mAlert.setVisibility(View.GONE);
                }
            }
        }
        //设置内容，获取相关时间，从data里编辑的日期中获取
        mTime.setText(DateUtils.getRelativeTimeSpanString(data.getModifiedDate()));

        setBackground(data);
    }
    //根据data的文件属性来设置背景
    private void setBackground(NoteItemData data) {
        int id = data.getBgColorId();
        //若是note型文件，则4种情况，对于4种不同情况的背景来源
        if (data.getType() == Notes.TYPE_NOTE) {
            //单个数据并且只有一个子文件夹
            if (data.isSingle() || data.isOneFollowingFolder()) {
                setBackgroundResource(NoteItemBgResources.getNoteBgSingleRes(id));
            } else if (data.isLast()) {//是最后一个数据
                setBackgroundResource(NoteItemBgResources.getNoteBgLastRes(id));
            } else if (data.isFirst() || data.isMultiFollowingFolder()) {//是一个数据并有多个子文件夹
                setBackgroundResource(NoteItemBgResources.getNoteBgFirstRes(id));
            } else {
                setBackgroundResource(NoteItemBgResources.getNoteBgNormalRes(id));
            }
        } else {//若不是note直接调用文件夹的来源
            setBackgroundResource(NoteItemBgResources.getFolderBgRes());
        }
    }

    public NoteItemData getItemData() {
        return mItemData;
    }
}
