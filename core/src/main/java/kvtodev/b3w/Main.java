package kvtodev.b3w;

import com.artemis.World;
import com.artemis.WorldConfiguration;
import com.artemis.WorldConfigurationBuilder;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.box2d.enums.b2BodyType;
import com.badlogic.gdx.box2d.structs.b2BodyDef;
import com.badlogic.gdx.box2d.structs.b2Polygon;
import com.badlogic.gdx.box2d.structs.b2ShapeDef;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.utils.ScreenUtils;
import kvtodev.b3w.components.DrawableCM;
import kvtodev.b3w.components.PhysicsCM;
import kvtodev.b3w.components.TransformCM;
import kvtodev.b3w.systems.PhysicsSystem;
import kvtodev.b3w.systems.render.RenderLogic;
import kvtodev.b3w.systems.render.RenderSystem;

import static com.badlogic.gdx.box2d.Box2d.*;


/**
 * {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms.
 */
public class Main extends ApplicationAdapter {


    World world;
    private AssetManager assetManager;

    @Override
    public void create() {
        initialize();

        //use assetManager to load textures
        assetManager = new AssetManager();
        assetManager.load("libgdx.png", Texture.class);
        assetManager.finishLoading();


        //create ecs world
        WorldConfiguration config = new WorldConfigurationBuilder()
            .with(new PhysicsSystem())
            .with(new RenderSystem())
            .build();
        world = new World(config);


        //add some bodies for testing
        //show the way to create entities and components ,to create box2d bodies, to give a render logic
        RenderLogic renderLogic = new RenderLogic() {
            private final Texture tex = assetManager.get("libgdx.png", Texture.class);
            private final TextureRegion texReg = new TextureRegion(tex, 16, 16);

            @Override
            public void render(Affine2 _transform, SpriteBatch _batch) {
                _transform.translate(-0.45f, -0.45f);
                //due to the way libgdx handles texture drawing, we need to add offset to correct the center of the texture
                _batch.draw(texReg, 0.9f, 0.9f, _transform);
            }
        };
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                int entity = world.create();
                TransformCM transform = world.edit(entity).create(TransformCM.class);
//                transform.transform.setToTranslation(0.5f,0.5f);
//                transform.transform.setToTranslation(i, j);
                PhysicsCM physics = world.edit(entity).create(PhysicsCM.class);
                DrawableCM drawable = world.edit(entity).create(DrawableCM.class);

                b2BodyDef bdef = b2DefaultBodyDef();
                bdef.position().x(i);
                bdef.position().y(j);
                bdef.linearVelocity().x(-1f);
                bdef.angularVelocity(1f);
                bdef.isAwake(true);
                bdef.type(b2BodyType.b2_dynamicBody);
                physics.isStatic = false;
                drawable.renderLogic = renderLogic;
                b2Polygon b2Polygon = b2MakeSquare(0.4f);
                b2ShapeDef shape = b2DefaultShapeDef();
                physics.bodyId = b2CreateBody(world.getSystem(PhysicsSystem.class).worldId, bdef.asPointer());
                b2CreatePolygonShape(physics.bodyId, shape.asPointer(), b2Polygon.asPointer());
            }
        }
    }


    @Override
    public void render() {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        //where calls physics and render systems till now
        world.process();
    }

}
