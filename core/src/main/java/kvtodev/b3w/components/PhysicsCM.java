package kvtodev.b3w.components;

import com.artemis.PooledComponent;
import com.badlogic.gdx.box2d.structs.b2BodyId;

public class PhysicsCM extends PooledComponent {
    public b2BodyId bodyId;
    @Override
    protected void reset() {
        bodyId.free();
    }
}
