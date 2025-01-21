package kvtodev.b3w.components;

import com.artemis.PooledComponent;
import com.badlogic.gdx.math.Affine2;

public class TransformCM extends PooledComponent {
    public transient Affine2 transform = new Affine2();

    @Override
    protected void reset() {
        transform.idt();
    }
}
