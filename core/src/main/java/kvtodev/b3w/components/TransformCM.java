package kvtodev.b3w.components;

import com.artemis.PooledComponent;
import com.badlogic.gdx.math.Matrix3;

public class TransformCM extends PooledComponent {
    public transient Matrix3 transform = new Matrix3();

    @Override
    protected void reset() {
        transform.idt();
    }
}
