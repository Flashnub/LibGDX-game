package com.mygdx.game.views.management;

public abstract class SelectorViewCell extends Actor {

    Label header;
    Label description;
    Label frameData;
    Label damage;
    Label hitStun;
    Label inputs;
    Skin headerLabelSkin;
    Skin contentLabelSkin;
    Skin inputLabelSkin;

    float width;
    float height;

    public SelectorViewCell(float width, float height) {
        headerLabelSkin = new Skin(Gdx.files.internal("SelectorViewCell_HeaderLabel"));
        contentLabelSkin = new Skin(Gdx.files.internal("SelectorViewCell_ContentLabel"));
        inputLabelSkin = new Skin(Gdx.files.internal("SelectorViewCell_InputLabel"));

        header = new Label(headerLabelSkin);
        description = new Label(contentLabelSkin);
        frameData = new Label(contentLabelSkin);
        damage = new Label(contentLabelSkin);
        hitStun = new Label(contentLabelSkin);
        inputs = new Label(inputLabelSkin);
    }

    public abstract void act(float delta);
}