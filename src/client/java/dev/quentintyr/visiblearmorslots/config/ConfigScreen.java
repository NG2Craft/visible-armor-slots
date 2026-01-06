package dev.quentintyr.visiblearmorslots.config;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.*;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

import dev.quentintyr.visiblearmorslots.config.ModConfig;
import dev.quentintyr.visiblearmorslots.config.ContainerListWidget;


public class ConfigScreen extends Screen {

    private final Screen parent;
    private final ModConfig config;

    private enum Category { SETTINGS, CONTAINERS }
    private Category currentCategory = Category.SETTINGS;

    private ContainerListWidget containerListWidget;
    private ButtonWidget toggleAllButton;
    private boolean allEnabled = true;

    public ConfigScreen(Screen parent) {
        super(Text.translatable("config.visiblearmorslots.title"));
        this.parent = parent;
        this.config = ModConfig.getInstance();
    }

    @Override
    protected void init() {
        int topBarY = 20;
        int buttonHeight = 20;
        int buttonWidth = 100;
        int gap = 5;
        int startX = (this.width - (buttonWidth * 2 + gap)) / 2;

        ButtonWidget settingsBtn = ButtonWidget.builder(
                Text.translatable("config.visiblearmorslots.tab.settings"),
                b -> switchCategory(Category.SETTINGS)
        ).dimensions(startX, topBarY, buttonWidth, buttonHeight).build();
        settingsBtn.active = currentCategory != Category.SETTINGS;
        addDrawableChild(settingsBtn);

        ButtonWidget containersBtn = ButtonWidget.builder(
                Text.translatable("config.visiblearmorslots.tab.containers"),
                b -> switchCategory(Category.CONTAINERS)
        ).dimensions(startX + buttonWidth + gap, topBarY, buttonWidth, buttonHeight).build();
        containersBtn.active = currentCategory != Category.CONTAINERS;
        addDrawableChild(containersBtn);

        if (currentCategory == Category.SETTINGS) {
            initSettingsContent();
            addDrawableChild(
                    ButtonWidget.builder(ScreenTexts.DONE, b -> close())
                            .dimensions(width / 2 - 100, height - 30, 200, 20)
                            .build()
            );
        } else {
            initContainersContent();
        }
    }

    private void switchCategory(Category newCategory) {
        currentCategory = newCategory;
        resize(client, width, height);
    }

    private void initSettingsContent() {
        GridWidget grid = new GridWidget();
        grid.getMainPositioner().marginX(10).marginY(4);
        GridWidget.Adder adder = grid.createAdder(1);

        int w = 220;
        int h = 20;

        adder.add(CyclingButtonWidget.onOffBuilder(config.isEnabled())
                .build(0, 0, w, h,
                        Text.translatable("config.visiblearmorslots.enabled"),
                        (b, v) -> config.setEnabled(v)));

        adder.add(CyclingButtonWidget.onOffBuilder(config.shouldShowTooltips())
                .build(0, 0, w, h,
                        Text.translatable("config.visiblearmorslots.showTooltips"),
                        (b, v) -> config.setShowTooltips(v)));

        adder.add(CyclingButtonWidget.onOffBuilder(config.isShowOffhandSlot())
                .build(0, 0, w, h,
                        Text.translatable("config.visiblearmorslots.showOffhand"),
                        (b, v) -> config.setShowOffhandSlot(v)));

        adder.add(CyclingButtonWidget.onOffBuilder(config.isDarkMode())
                .build(0, 0, w, h,
                        Text.translatable("config.visiblearmorslots.darkMode"),
                        (b, v) -> config.setDarkMode(v)));

        adder.add(CyclingButtonWidget.onOffBuilder(config.isAutoPositioning())
                .build(0, 0, w, h,
                        Text.translatable("config.visiblearmorslots.autoPosition"),
                        (b, v) -> config.setAutoPositioning(v)));

        adder.add(CyclingButtonWidget.<ModConfig.Side>builder(s -> Text.literal(s.toString()))
                .values(ModConfig.Side.LEFT, ModConfig.Side.RIGHT)
                .initially(config.getPositioning())
                .build(0, 0, w, h,
                        Text.translatable("config.visiblearmorslots.side"),
                        (b, s) -> config.setPositioning(s)));

        adder.add(new SliderWidget(0, 0, w, h, 
                Text.translatable("config.visiblearmorslots.marginX", config.getMarginX()), 
                config.getMarginX() / 31.0) {
            @Override
            protected void updateMessage() {
                setMessage(Text.translatable("config.visiblearmorslots.marginX", (int)(value * 31)));
            }
            @Override
            protected void applyValue() {
                config.setMarginX((int)(value * 31));
            }
        });

        adder.add(new SliderWidget(0, 0, w, h, 
                Text.translatable("config.visiblearmorslots.marginY", config.getMarginY()), 
                (config.getMarginY() + 64) / 127.0) {
            @Override
            protected void updateMessage() {
                setMessage(Text.translatable("config.visiblearmorslots.marginY", (int)(value * 127) - 64));
            }
            @Override
            protected void applyValue() {
                config.setMarginY((int)(value * 127) - 64);
            }
        });

        grid.refreshPositions();
        SimplePositioningWidget.setPos(grid, 0, 50, width, height - 80, 0.5f, 0f);
        grid.forEachChild(this::addDrawableChild);
    }

    private void initContainersContent() {
        int listTop = 50;
        int listBottom = height - 60;

        containerListWidget = new ContainerListWidget(
                client, width, listBottom - listTop, listTop, 25, config
        );
        addDrawableChild(containerListWidget);

        int bw = 100;
        int spacing = 5;
        int startX = (width - (bw * 3 + spacing * 2)) / 2;
        int y = height - 28;

        toggleAllButton = ButtonWidget.builder(
                Text.empty(),
                b -> {
                    allEnabled = !allEnabled;
                    containerListWidget.setAllEnabled(allEnabled);
                    updateToggleAllButton();
                }).dimensions(startX, y, bw, 20).build();
        addDrawableChild(toggleAllButton);
        updateToggleAllButton();

        addDrawableChild(ButtonWidget.builder(
                Text.translatable("config.visiblearmorslots.containers.reset"),
                b -> {
                    config.resetContainersToDefault();
                    containerListWidget.refreshEntries();
                }).dimensions(startX + bw + spacing, y, bw, 20).build());

        addDrawableChild(ButtonWidget.builder(
                ScreenTexts.DONE,
                b -> close()
        ).dimensions(startX + (bw + spacing) * 2, y, bw, 20).build());
    }

    private void updateToggleAllButton() {
        toggleAllButton.setMessage(Text.translatable(
                allEnabled
                        ? "config.visiblearmorslots.containers.disableAll"
                        : "config.visiblearmorslots.containers.enableAll"
        ));
    }

    @Override
    public void render(DrawContext ctx, int mx, int my, float delta) {
        super.renderBackground(ctx, mx, my, delta);
        super.render(ctx, mx, my, delta);
        
        ctx.drawCenteredTextWithShadow(textRenderer, title, width / 2, 8, 0xFFFFFF);
        
        if (currentCategory == Category.CONTAINERS) {
            ctx.drawCenteredTextWithShadow(
                    textRenderer,
                    Text.translatable("config.visiblearmorslots.containers.help").getString(),
                    width / 2,
                    height - 45,
                    0x808080
            );
        }
    }

    @Override
    public void close() {
        ModConfig.save();
        if (client != null) {
            client.setScreen(parent);
        }
    }
}