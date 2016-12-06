package com.jms.trgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

/**
 * Created by jmontes on 27/11/16.
 */
public class Pacman extends ScreenObject {

    // -----------------------------------------------------------------------------------------------------------------

    private TRGame game;

    private TextureRegion manTexture;
    private TextureRegion manTextureUp;
    private TextureRegion manTextureDown;
    private TextureRegion manTextureLeft;
    private TextureRegion manTextureRight;

    private Texture         walkSheet;
    private TextureRegion[] walkFramesUp;
    private TextureRegion[] walkFramesDown;
    private TextureRegion[] walkFramesLeft;
    private TextureRegion[] walkFramesRight;
    private Animation       walkAnimationUp;
    private Animation       walkAnimationDown;
    private Animation       walkAnimationLeft;
    private Animation       walkAnimationRight;
    private Animation       walkAnimation;

    private float stateTime = 0.0f;

    private Boolean moving = false;
    private Boolean alive  = true;

    //private Rectangle rect;

    // -----------------------------------------------------------------------------------------------------------------

    public Pacman(TRGame game, int startX, int startY) {

        this.game = game;

        walkSheet = new Texture(Gdx.files.internal(TRGame.MAN_SPRITES_PATH));
        TextureRegion[][] tmp = TextureRegion.split(
                walkSheet, walkSheet.getWidth()/TRGame.FRAME_COLS,
                walkSheet.getHeight()/TRGame.FRAME_ROWS);

        walkFramesUp    = new TextureRegion[TRGame.FRAME_COLS];
        walkFramesDown  = new TextureRegion[TRGame.FRAME_COLS];
        walkFramesLeft  = new TextureRegion[TRGame.FRAME_COLS];
        walkFramesRight = new TextureRegion[TRGame.FRAME_COLS];

        for (int i = 0; i < TRGame.FRAME_COLS; i++) {
            walkFramesDown[i]  = tmp[0][i];
            walkFramesLeft[i]  = tmp[1][i];
            walkFramesRight[i] = tmp[2][i];
            walkFramesUp[i]    = tmp[3][i];
        }
        walkAnimationUp    = new Animation(TRGame.ANIMATION_DURATION/TRGame.FRAME_COLS, walkFramesUp);
        walkAnimationDown  = new Animation(TRGame.ANIMATION_DURATION/TRGame.FRAME_COLS, walkFramesDown);
        walkAnimationLeft  = new Animation(TRGame.ANIMATION_DURATION/TRGame.FRAME_COLS, walkFramesLeft);
        walkAnimationRight = new Animation(TRGame.ANIMATION_DURATION/TRGame.FRAME_COLS, walkFramesRight);
        walkAnimation = walkAnimationRight;

        this.manTextureUp    = walkFramesUp[TRGame.FRAME_COLS/2];
        this.manTextureDown  = walkFramesDown[TRGame.FRAME_COLS/2];
        this.manTextureLeft  = walkFramesLeft[TRGame.FRAME_COLS/2];
        this.manTextureRight = walkFramesRight[TRGame.FRAME_COLS/2];
        this.manTexture = manTextureRight;

        this.rect = new Rectangle();
        this.rect.height = TRGame.PACMAN_HEIGHT;
        this.rect.width  = TRGame.PACMAN_WIDTH;
        this.rect.x = startX - rect.width/2;
        this.rect.y = startY - rect.height/2;

    }

    // -----------------------------------------------------------------------------------------------------------------

    public void dispose() {
        walkSheet.dispose();
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public void draw() {
        TextureRegion currentFrame = manTexture;

        if (alive && moving) {
            stateTime += Gdx.graphics.getDeltaTime();
            currentFrame = walkAnimation.getKeyFrame(stateTime, true);
        }
        game.getSpriteBatch().draw(currentFrame, rect.x, rect.y, rect.width, rect.height);
    }

    // -----------------------------------------------------------------------------------------------------------------

    public void dontMove() {
        moving = false;
    }

    // -----------------------------------------------------------------------------------------------------------------

    public void move(int direction) {

        float step = TRGame.MAN_SPEED * Gdx.graphics.getDeltaTime();

        switch (direction) {
            case TRGame.DIRECTION_UP :
                rect.y += step;
                manTexture = manTextureUp;
                walkAnimation = walkAnimationUp;
                break;
            case TRGame.DIRECTION_DOWN :
                rect.y -= step;
                manTexture = manTextureDown;
                walkAnimation = walkAnimationDown;
                break;
            case TRGame.DIRECTION_LEFT :
                rect.x -= step;
                manTexture = manTextureLeft;
                walkAnimation = walkAnimationLeft;
                break;
            case TRGame.DIRECTION_RIGHT :
                rect.x += step;
                manTexture = manTextureRight;
                walkAnimation = walkAnimationRight;
                break;
        }

        if (rect.x > TRGame.SCREEN_WIDTH - TRGame.PACMAN_WIDTH)
            rect.x = TRGame.SCREEN_WIDTH - TRGame.PACMAN_WIDTH;
        else if (rect.x < 0)
            rect.x = 0;
        else if (rect.y > TRGame.SCREEN_HEIGHT - TRGame.PACMAN_HEIGHT)
            rect.y = TRGame.SCREEN_HEIGHT - TRGame.PACMAN_HEIGHT;
        else if (rect.y < 0)
        rect.y = 0;

        moving = true;
    }

    public void move(float x, float y) {

        double difX = this.getX() - x;
        double difY = this.getY() - y;

        int newDirection = 0;

        if (Math.abs(difX) > TRGame.PACMAN_WIDTH || Math.abs(difY) > TRGame.PACMAN_WIDTH) {

            if (Math.max(Math.abs(difX),Math.abs(difY))/Math.min(Math.abs(difX),Math.abs(difY)) > 1.05) {

                if (Math.abs(difX) > Math.abs(difY)) {
                    if (difX > 0)
                        newDirection = TRGame.DIRECTION_LEFT;
                    else
                        newDirection = TRGame.DIRECTION_RIGHT;
                } else {
                    if (difY > 0)
                        newDirection = TRGame.DIRECTION_DOWN;
                    else
                        newDirection = TRGame.DIRECTION_UP;

                }

                move(newDirection);
            }
        } else {
            moving = false;
        }

        /*
        double vectorL = Math.sqrt(Math.pow(difX, 2) + Math.pow(difY, 2));
        double ratio = TRGame.MAN_SPEED/vectorL;

        float delta = Gdx.graphics.getDeltaTime();

        if (Math.abs(difX) >= TRGame.PACMAN_WIDTH)
            rect.x -= difX * ratio * delta;
        if (Math.abs(difY) >= TRGame.PACMAN_HEIGHT)
            rect.y -= difY * ratio * delta;

        if (Math.abs(difX) > Math.abs(difY)) {
            if (difX > 0)
                manTexture = manTextureLeft;
            else
                manTexture = manTextureRight;
        } else {
            if (difY > 0)
                manTexture = manTextureDown;
            else
                manTexture = manTextureUp;
        }
        */
    }

    public boolean isAlive() {
        return alive;
    }

    public void enemyGotYou() {
        alive = false;
    }
}