package kvtodev.b3w.systems.render;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Affine2;

public interface RenderLogic {
    void render(Affine2 transform, SpriteBatch batch);
}
