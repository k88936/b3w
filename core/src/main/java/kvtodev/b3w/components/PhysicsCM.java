package kvtodev.b3w.components;

import com.artemis.PooledComponent;
import com.badlogic.gdx.box2d.structs.b2BodyId;

public class PhysicsCM extends PooledComponent {
    public b2BodyId bodyId;
    public boolean isStatic;

    @Override
    protected void reset() {
    }
}
