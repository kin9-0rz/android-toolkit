package parser.dex;

import com.googlecode.dex2jar.Field;
import com.googlecode.dex2jar.visitors.DexAnnotationVisitor;
import com.googlecode.dex2jar.visitors.DexFieldVisitor;

/**
 * Created by lai on 14-5-29.
 */
public class FieldAdapter  implements DexFieldVisitor {
    protected Field field;
    protected Object value;
    protected int accessFlags;

    public FieldAdapter(int accessFlags, Field field, Object value) {
        super();
        this.field = field;
        this.value = value;
        this.accessFlags = accessFlags;
    }

    @Override
    public void visitEnd() {

    }

    @Override
    public DexAnnotationVisitor visitAnnotation(String name, boolean visible) {
        return null;
    }
}
