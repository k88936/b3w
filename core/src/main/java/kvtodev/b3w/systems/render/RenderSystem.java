package kvtodev.b3w.systems.render;

import com.artemis.BaseEntitySystem;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.box2d.Box2d;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import kvtodev.b3w.components.DrawableCM;
import kvtodev.b3w.components.TransformCM;

import java.util.stream.IntStream;

@All({TransformCM.class, DrawableCM.class})
public class RenderSystem extends BaseEntitySystem implements Disposable {
    private final SpriteBatch batch = new SpriteBatch();
    private final Camera camera = new OrthographicCamera(10, 10);
    private final Viewport viewport = new ExtendViewport(20, 20, camera);
    private final BitmapFont font = new BitmapFont();
    ComponentMapper<TransformCM> transformMapper;
    ComponentMapper<DrawableCM> drawableMapper;

    @Override
    protected void processSystem() {
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        System.out.println(viewport.getWorldHeight());
        System.out.println(viewport.getWorldWidth());


        viewport.apply();
        // render
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        // render all entities
        IntStream.range(0, subscription.getEntities().size()).forEach(this::process);
        batch.end();
        System.out.println("FPS: "+ Gdx.graphics.getFramesPerSecond());
    }

    private void process(int entity) {
        TransformCM transformCM = transformMapper.get(entity);
        DrawableCM drawable = drawableMapper.get(entity);
        drawable.renderLogic.render(transformCM.transform, batch);
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
    }
}
