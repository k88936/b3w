package kvtodev.b3w.systems;

import com.artemis.BaseEntitySystem;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.badlogic.gdx.box2d.enums.b2BodyType;
import com.badlogic.gdx.box2d.structs.*;
import com.badlogic.gdx.box2d.utils.Box2dWorldTaskSystem;
import com.badlogic.gdx.utils.Disposable;
import kvtodev.b3w.components.DrawableCM;
import kvtodev.b3w.components.PhysicsCM;
import kvtodev.b3w.components.TransformCM;

import java.util.Arrays;

import static com.badlogic.gdx.box2d.Box2d.*;

@All({TransformCM.class, PhysicsCM.class})
public class PhysicsSystem {
    private static final int workerCount = 6;
    public final b2WorldId worldId;
//    private final Box2dWorldTaskSystem taskSystem;
    public ComponentMapper<TransformCM> transformMapper;
    public ComponentMapper<PhysicsCM> physicsMapper;

    public PhysicsSystem() {

        b2WorldDef worldDef = b2DefaultWorldDef();
        worldDef.gravity().y(0);
        worldDef.enableSleep(true);
        Thread currentThread = Thread.currentThread();
//        taskSystem = Box2dWorldTaskSystem.createForWorld(worldDef, workerCount, currentThread::interrupt);
        worldId = b2CreateWorld(worldDef.asPointer());
         for (int i = 0; i < 100; i++) {
            for (int j = 0; j < 100 ;j++) {

                b2BodyDef bdef = b2DefaultBodyDef();
                bdef.position().x(i);
                bdef.position().y(j);
//                bdef.linearVelocity().x(-1f);
//                bdef.angularVelocity(1f);
                bdef.isAwake(true);
                bdef.type(b2BodyType.b2_dynamicBody);
                b2Polygon b2Polygon = b2MakeSquare(0.5f);
                b2ShapeDef shape = b2DefaultShapeDef();
                b2BodyId bodyId = b2CreateBody(worldId, bdef.asPointer());
                b2CreatePolygonShape(bodyId, shape.asPointer(), b2Polygon.asPointer());
            }
        }
    }

    public void processSystem() {

        b2World_Step(worldId, 1 / 60f, 4);
//        taskSystem.afterStep();
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


    }


}
