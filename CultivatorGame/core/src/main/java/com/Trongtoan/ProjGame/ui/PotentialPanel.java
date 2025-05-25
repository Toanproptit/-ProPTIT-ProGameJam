    package com.Trongtoan.ProjGame.ui;

    import com.Trongtoan.ProjGame.entities.Player;
    import com.badlogic.gdx.Gdx;
    import com.badlogic.gdx.graphics.Color;
    import com.badlogic.gdx.graphics.Texture;
    import com.badlogic.gdx.graphics.g2d.TextureRegion;
    import com.badlogic.gdx.scenes.scene2d.Group;
    import com.badlogic.gdx.scenes.scene2d.InputEvent;
    import com.badlogic.gdx.scenes.scene2d.ui.*;
    import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
    import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

    public class PotentialPanel extends Group {
        private final Table panelTable;
        private BottomMenuPanel bottomMenu;
        private Container<Label> selectedContainer = null;

        private Label potentialTitleLabel;
        private Label hpLabel;
        private Label mpLabel;
        private Label dmgLabel;

        public void setBottomMenu(BottomMenuPanel bottomMenu) {
            this.bottomMenu = bottomMenu;
        }

        public PotentialPanel(Skin skin, Player player) {
            float width = 300;
            float height = 300;

            Image bgColor = new Image(skin.newDrawable("white", 0.75f, 0.55f, 0.35f, 1f));
            Texture bgTexture = new Texture(Gdx.files.internal("ui/potential_bg.png"));
            Image bgFrame = new Image(new TextureRegionDrawable(new TextureRegion(bgTexture)));

            panelTable = new Table(skin);
            panelTable.top().pad(10);
            panelTable.setFillParent(true);

            // Tiêu đề tiềm năng
            potentialTitleLabel = new Label("Potential:      " + player.getPotentialPoints(), skin);
            panelTable.add(createSelectableContainer(skin, potentialTitleLabel)).colspan(2).padBottom(20).row();

            // HP
            hpLabel = new Label("Max HP:       " + player.getMaxHp(), skin);
            panelTable.add(createSelectableContainer(skin, hpLabel)).colspan(2).padBottom(20).row();

            // MP
            mpLabel = new Label("Max MP:       " + player.getMaxMp(), skin);
            panelTable.add(createSelectableContainer(skin, mpLabel)).colspan(2).padBottom(20).row();

            // Damage
            dmgLabel = new Label("Base Damage:  " + player.getBaseDamage(), skin);
            panelTable.add(createSelectableContainer(skin, dmgLabel)).colspan(2).padBottom(20).row();

            // Nút
            ImageButton addButton = createImageButton("ui/add_button.png");
            ImageButton closeButton = createImageButton("ui/close_button.png");

            closeButton.addListener(new ClickListener() {
                public void clicked(InputEvent event, float x, float y) {
                    setVisible(false);
                    if (bottomMenu != null) {
                        bottomMenu.setVisible(true);
                    }
                }
            });

            addButton.addListener(new ClickListener() {
                public void clicked(InputEvent event, float x, float y) {
                    if (selectedContainer == null) return;

                    Label selectedLabel = selectedContainer.getActor();
                    String text = selectedLabel.getText().toString();

                    if (text.startsWith("Max HP")) {
                        player.increaseMaxHp();
                        hpLabel.setText("Max HP:       " + player.getMaxHp());
                    } else if (text.startsWith("Max MP")) {
                        player.increaseMaxMp();
                        mpLabel.setText("Max MP:       " + player.getMaxMp());
                    } else if (text.startsWith("Base Damage")) {
                        player.increaseBaseDamage();
                        dmgLabel.setText("Base Damage:  " + player.getBaseDamage());
                    }

                    // Cập nhật lại điểm tiềm năng
                    potentialTitleLabel.setText("Potential:      " + player.getPotentialPoints());
                }
            });

            Stack stack = new Stack();
            stack.setSize(width, height);
            stack.setPosition((Gdx.graphics.getWidth() - width) / 2f, (Gdx.graphics.getHeight() - height) / 2f);

            float centerX = (Gdx.graphics.getWidth() - 300) / 2f;

            addButton.setPosition(centerX + 30, (Gdx.graphics.getHeight() - 300) / 2f - 60);
            closeButton.setPosition(centerX + 160, (Gdx.graphics.getHeight() - 300) / 2f - 60);

            stack.add(bgColor);
            stack.add(bgFrame);
            stack.add(panelTable);

            addActor(stack);
            addActor(addButton);
            addActor(closeButton);
            setVisible(false);
        }

        public void update(Player player) {
            potentialTitleLabel.setText("Potential: " + player.getPotentialPoints());
            hpLabel.setText("Max HP:       " + player.getMaxHp());
            mpLabel.setText("Max MP:       " + player.getMaxMp());
            dmgLabel.setText("Base Damage:  " + player.getBaseDamage());
        }

        private ImageButton createImageButton(String path) {
            Texture tex = new Texture(Gdx.files.internal(path));
            TextureRegionDrawable drawable = new TextureRegionDrawable(new TextureRegion(tex));
            return new ImageButton(drawable);
        }

        private Container<Label> createSelectableContainer(Skin skin, Label label) {
            Container<Label> container = new Container<>(label);
            container.pad(20);
            container.setBackground(skin.newDrawable("white", Color.GOLD));

            container.addListener(new ClickListener() {
                public void clicked(InputEvent event, float x, float y) {
                    if (selectedContainer != null) {
                        selectedContainer.setBackground(skin.newDrawable("white", Color.GOLD));
                    }
                    container.setBackground(skin.newDrawable("white", Color.ORANGE));
                    selectedContainer = container;
                }
            });

            return container;
        }
    }

