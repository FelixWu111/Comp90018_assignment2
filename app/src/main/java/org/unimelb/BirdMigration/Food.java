package org.unimelb.BirdMigration;

import android.widget.ImageView;

public class Food {
    private float x = -80.0f, y = -80.0f;
    private float speed;
    private int score;
    private int bias;
    private int sound;
    private ImageView image;

    public Food() {
    }

    public Food(int score, int bias, float speed, ImageView image, int sound) {
        this.score = score;
        this.bias = bias;
        this.speed = speed;
        this.image = image;
        this.image.setX(this.x);
        this.image.setY(this.y);
        this.sound = sound;
    }

    public void updatePos(int screenWidth, int frameHeight) {
        this.x -= this.speed;
        // when X < 0, reset its position to the right side of the screen with an initial value
        if (this.x < 0) {
            this.x = screenWidth + bias;
            this.y = (float) Math.floor(Math.random() * (frameHeight - image.getHeight()));
        }
        this.image.setX(this.x);
        this.image.setY(this.y);
    }


    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
        this.image.setX(x);
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
        this.getImage().setY(y);
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public ImageView getImage() {
        return image;
    }

    public void setImage(ImageView image) {
        this.image = image;
    }

    public int getSound() {
        return sound;
    }

    public void setSound(int sound) {
        this.sound = sound;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getBias() {
        return bias;
    }

    public void setBias(int bias) {
        this.bias = bias;
    }
}


