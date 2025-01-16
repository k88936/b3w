package kvtodev.b3w;

import com.artemis.World;
import com.artemis.WorldConfiguration;
import com.artemis.WorldConfigurationBuilder;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.utils.ScreenUtils;
import kvtodev.b3w.components.PhysicsCM;
import kvtodev.b3w.components.TransformCM;
import kvtodev.b3w.systems.PhysicsSystem;
import static com.badlogic.gdx.box2d.Box2d.*;


/**
 * {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms.
 */
public class Main extends ApplicationAdapter {


    @Override
    public void create() {
        initialize();
        WorldConfiguration config = new WorldConfigurationBuilder()
            .with(new PhysicsSystem())
            .build();
        World world = new World(config);
        int entity = world.create();
        TransformCM transform = world.edit(entity).create(TransformCM.class);
        transform.transform.idt();
        PhysicsCM physics = world.edit(entity).create(PhysicsCM.class);
        physics.bodyId = b2CreateBody(world.getSystem(PhysicsSystem.class).worldId, b2DefaultBodyDef().asPointer());
        world.process();
    }


    @Override
    public void render() {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
    }

    @Override
    public void dispose() {
    }
}
