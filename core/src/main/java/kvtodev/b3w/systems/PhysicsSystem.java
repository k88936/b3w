package kvtodev.b3w.systems;

import com.artemis.BaseEntitySystem;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;

import static com.badlogic.gdx.box2d.Box2d.*;

import com.badlogic.gdx.box2d.enums.*;
import com.badlogic.gdx.box2d.structs.*;
import com.badlogic.gdx.jnigen.runtime.closure.ClosureObject;
import com.badlogic.gdx.jnigen.runtime.pointer.VoidPointer;
import kvtodev.b3w.components.PhysicsCM;
import kvtodev.b3w.components.TransformCM;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@All({TransformCM.class, PhysicsCM.class})
public class PhysicsSystem extends BaseEntitySystem {
    public ComponentMapper<TransformCM> transformMapper;
    public ComponentMapper<PhysicsCM> physicsMapper;

    public final b2WorldId worldId;

    private final ExecutorService executor;
    private static final int e_maxTasks = 64;
    private static final int workerCount = 8;
    private final ClosureObject<b2EnqueueTaskCallback> enqueueTask;//Task distribution logic
    private final ClosureObject<b2FinishTaskCallback> finishTask;//Task completion logic

    public PhysicsSystem() {
        AtomicInteger taskCount = new AtomicInteger(0);
        HashMap<Long, List<Future<Void>>> userTasks = new HashMap<>();
        ConcurrentHashMap<Long, Integer> threadToWorkerIndex = new ConcurrentHashMap<>();
        AtomicInteger nextWorkerIndex = new AtomicInteger(0);
        ThreadFactory threadFactory = r -> {
            Thread thread = new Thread(r);
            threadToWorkerIndex.put(thread.threadId(), nextWorkerIndex.getAndIncrement());
            return thread;
        };

        executor = Executors.newFixedThreadPool(workerCount, threadFactory);
        enqueueTask = ClosureObject.fromClosure((box2dTask, itemCount, minRange, box2dContext, userContext) -> {
            if (taskCount.get() < e_maxTasks) {

                int numTasks = Math.min(workerCount, (itemCount + minRange - 1) / minRange);
                int baseItemsPerTask = itemCount / numTasks;
                int remainingItems = itemCount % numTasks;

                List<Future<Void>> tasks = new ArrayList<>(numTasks);

                for (int i = 0; i < numTasks; i++) {
                    int start = i * baseItemsPerTask + Math.min(i, remainingItems);
                    int end = (i + 1) * baseItemsPerTask + Math.min(i + 1, remainingItems);

                    tasks.add(executor.submit(() -> {
                        box2dTask.getClosure().b2TaskCallback_call(start, end, threadToWorkerIndex.get(Thread.currentThread().threadId()), box2dContext);
                        return null;
                    }));
                }

                VoidPointer voidPointer = new VoidPointer(taskCount.incrementAndGet(), false);

                userTasks.put(voidPointer.getPointer(), tasks);
                return voidPointer;
            } else {
                box2dTask.getClosure().b2TaskCallback_call(0, itemCount, 0, box2dContext);
                return VoidPointer.NULL;
            }
        });
        finishTask = ClosureObject.fromClosure((userTask, userContext) -> {
            try {
                for (Future<Void> task : userTasks.get(userTask.getPointer())) {
                    task.get();
                }
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        });

        b2WorldDef worldDef = b2DefaultWorldDef();
        worldDef.enqueueTask(enqueueTask);
        worldDef.finishTask(finishTask);
        worldDef.workerCount(workerCount);
        worldDef.enableSleep(true);
//
        worldId = b2CreateWorld(worldDef.asPointer());

    }

    @Override
    protected void dispose() {
        super.dispose();
        b2DestroyWorld(worldId);
        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }
        finishTask.free();
        enqueueTask.free();
    }

    @Override
    protected void processSystem() {
        b2World_Step(worldId, 1 / 60f, 4);
        Arrays.stream(subscription.getEntities().getData()).parallel().forEach(this::process);
    }

    protected void process(int entityId) {

        PhysicsCM physics = physicsMapper.get(entityId);
        if (b2Body_GetType(physics.bodyId) == b2BodyType.b2_staticBody || !b2Body_IsAwake(physics.bodyId)) {
            return;
        }

        b2Transform b2Transform = b2Body_GetTransform(physics.bodyId);
        TransformCM transform = transformMapper.get(entityId);
        transform.transform.val[0] = b2Transform.q().c();
        transform.transform.val[1] = b2Transform.q().s();
        transform.transform.val[2] = 0;
        transform.transform.val[3] = -b2Transform.q().s();
        transform.transform.val[4] = b2Transform.q().c();
        transform.transform.val[5] = 0;
        transform.transform.val[6] = b2Transform.p().x();
        transform.transform.val[7] = b2Transform.p().y();
        transform.transform.val[8] = 1;
    }

}
