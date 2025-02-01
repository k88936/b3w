package kvtodev.b3w.systems;

import com.artemis.BaseEntitySystem;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.badlogic.gdx.box2d.structs.b2AABB;
import com.badlogic.gdx.box2d.structs.b2Transform;
import com.badlogic.gdx.box2d.structs.b2WorldDef;
import com.badlogic.gdx.box2d.structs.b2WorldId;
import com.badlogic.gdx.box2d.utils.Box2dWorldTaskSystem;
import com.badlogic.gdx.utils.Disposable;
import kvtodev.b3w.components.PhysicsCM;
import kvtodev.b3w.components.TransformCM;

import java.util.Arrays;

import static com.badlogic.gdx.box2d.Box2d.*;

@All({TransformCM.class, PhysicsCM.class})
public class PhysicsSystem extends BaseEntitySystem implements Disposable {
    private static final int workerCount = 6;
    public final b2WorldId worldId;
    private final Box2dWorldTaskSystem taskSystem;
    public ComponentMapper<TransformCM> transformMapper;
    public ComponentMapper<PhysicsCM> physicsMapper;

    public PhysicsSystem() {

        b2WorldDef worldDef = b2DefaultWorldDef();
        worldDef.gravity().y(0);
        worldDef.enableSleep(true);
        Thread currentThread = Thread.currentThread();
        taskSystem = Box2dWorldTaskSystem.createForWorld(worldDef, workerCount, currentThread::interrupt);
        worldId = b2CreateWorld(worldDef.asPointer());
    }

    @Override
    protected void processSystem() {

        b2World_Step(worldId, 1 / 60f, 4);
        taskSystem.afterStep();
        b2AABB box = new b2AABB();
        box.upperBound().x(10);
        box.upperBound().y(10);
        box.lowerBound().x(-10);
        box.lowerBound().y(-10);
//
//        b2World_OverlapAABB(worldId, box, Box2d.b2DefaultQueryFilter(), ClosureObject.fromClosure((id, context) -> {
//            System.out.println(6);
//            return true;
//        }), new VoidPointer(1));


        Arrays.stream(subscription.getEntities().getData()).parallel().forEach(this::process);
    }

    //load data back from box2d world
    //performance bottleneck
    protected void process(int entityId) {

        PhysicsCM physics = physicsMapper.get(entityId);
        //some legacy code for debugging
//        if (b2Body_GetType(physics.bodyId) == b2BodyType.b2_staticBody || !b2Body_IsAwake(physics.bodyId)) {
//            return;
//        }

        b2Transform b2Transform = b2Body_GetTransform(physics.bodyId);
//        System.out.println(b2Vec21.y());
//        b2Vec2 b2Vec2 = new b2Vec2();
//        b2Vec2.y(-10);
//        b2Body_ApplyForce(physics.bodyId,b2Vec2,b2Vec2,true);
        TransformCM transform = transformMapper.get(entityId);
        transform.transform.m00 = b2Transform.q().c();
        transform.transform.m01 = b2Transform.q().s();
        transform.transform.m10 = -b2Transform.q().s();
        transform.transform.m11 = b2Transform.q().c();
        transform.transform.m02 = b2Transform.p().x();
        transform.transform.m12 = b2Transform.p().y();
//        System.out.println(b2Transform.p().y());
//        System.out.println(b2Transform.q().c());
    }


    @Override
    public void dispose() {

        b2DestroyWorld(worldId);
        taskSystem.dispose();
    }
}
