package me.shouheng.notepal.model.enums;

import android.support.annotation.StringRes;

import me.shouheng.notepal.R;
import me.shouheng.notepal.model.Attachment;
import me.shouheng.notepal.model.Model;
import me.urakalee.next2.model.Note;
import me.shouheng.notepal.model.Notebook;
import me.shouheng.notepal.model.TimeLine;


/**
 * Created by wangshouheng on 2017/8/12. */
public enum ModelType {
    NONE(0, Model.class, R.string.model_name_none),
    NOTE(3, Note.class, R.string.model_name_note),
    NOTEBOOK(9, Notebook.class, R.string.model_name_notebook),
    ATTACHMENT(11, Attachment.class, R.string.model_name_attachment),
    TIME_LINE(15, TimeLine.class, R.string.model_name_timeline);

    public final int id;

    public final Class<?> cls;

    @StringRes
    public final int typeName;

    ModelType(int id, Class<?> cls, int typeName) {
        this.id = id;
        this.cls = cls;
        this.typeName = typeName;
    }

    public static ModelType getTypeById(int id){
        for (ModelType type : values()){
            if (type.id == id){
                return type;
            }
        }
        return NONE;
    }

    public static ModelType getTypeByName(Class<?> cls) {
        for (ModelType type : values()){
            if (type.cls.getName().equals(cls.getName())){
                return type;
            }
        }
        return NONE;
    }
}
