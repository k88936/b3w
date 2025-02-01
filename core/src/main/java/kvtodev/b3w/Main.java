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
    PhysicsSystem physicsSystem;
    private AssetManager assetManager;
    @Override
    public void create() {
        initialize();
        //use assetManager to load textures
        assetManager = new AssetManager();
        assetManager.load("libgdx.png", Texture.class);
        assetManager.finishLoading();
        //create ecs world

        //add some bodies for testing
        //show the way to create entities and components ,to create box2d bodies, to give a render logic

        physicsSystem= new PhysicsSystem();


    }


    @Override
    public void render() {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        //where calls physics and render systems till now
        physicsSystem.processSystem();
    }

}
