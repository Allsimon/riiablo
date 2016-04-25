package com.gmail.collinsmith70.diablo;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.gmail.collinsmith70.command.CommandManager;
import com.gmail.collinsmith70.cvar.Cvar;
import com.gmail.collinsmith70.cvar.CvarStateAdapter;
import com.gmail.collinsmith70.diablo.scene.HudedScene;
import com.gmail.collinsmith70.diablo.widget.ClientConsoleWidget;
import com.gmail.collinsmith70.key.Key;
import com.gmail.collinsmith70.key.KeyStateAdapter;
import com.gmail.collinsmith70.unifi.content.res.Resources;
import com.gmail.collinsmith70.unifi.graphics.Canvas;
import com.gmail.collinsmith70.unifi.graphics.ImmutablePaint;
import com.gmail.collinsmith70.unifi.graphics.Paint;
import com.gmail.collinsmith70.unifi.math.Dimension;
import com.gmail.collinsmith70.unifi.math.ImmutableDimension;
import com.gmail.collinsmith70.util.serializer.LocaleStringSerializer;

import java.io.OutputStream;
import java.io.PrintStream;
import java.text.ParseException;
import java.util.Locale;

public class Client implements ApplicationListener {

  private static final String TAG = Client.class.getSimpleName();

  private final Dimension RESOLUTION;
  private final PrintStream STDOUT;
  private final PrintStream STDERR;

  private boolean forcedWindowed;
  private boolean forcedDrawFps;

  private ClientConsole CONSOLE;
  private ClientConsoleWidget CONSOLE_WIDGET;

  private ClientInputProcessor INPUT_PROCESSOR;

  private CommandManager COMMAND_MANAGER;
  private GdxCvarManager CVAR_MANAGER;
  private GdxKeyManager KEY_MANAGER;
  private AssetManager ASSET_MANAGER;

//private Stage STAGE;

  //private Window WINDOW;
  //private Batch BATCH;

  private HudedScene scene;

  private byte pCvar_showFps;
  private boolean pCvar_Windowed;

  private BitmapFont font;

  public Client(int width, int height) {
    this.RESOLUTION = ImmutableDimension.newImmutableDimension(width, height);

    this.STDOUT = System.out;
    this.STDERR = System.err;
  }

  public void setForcedWindowed(boolean windowed) {
    this.forcedWindowed = windowed;
  }

  public void setForcedDrawFps(boolean drawFps) {
    this.forcedDrawFps = drawFps;
  }

  @Override
  public void create() {
    Gdx.app.setLogLevel(Application.LOG_DEBUG);

    //FileHandle consoleFileHandle = Gdx.files.local("console.out");
    //OutputStream consoleOut = consoleFileHandle.write(false);
    OutputStream consoleOut = System.out;

    this.CONSOLE = new ClientConsole(this, consoleOut);
    System.setOut(CONSOLE);
    System.setErr(CONSOLE);

    this.CVAR_MANAGER = new GdxCvarManager();
    setSerializers();
    Cvars.addTo(CVAR_MANAGER);

    this.COMMAND_MANAGER = new CommandManager();
    Commands.addTo(COMMAND_MANAGER);

    this.KEY_MANAGER = new GdxKeyManager();
    Keys.addTo(KEY_MANAGER);

    FileHandleResolver fhResolver = new InternalFileHandleResolver();
    this.ASSET_MANAGER = new AssetManager(fhResolver);
    setAssetLoaders(fhResolver);
    initPCvars();
    loadCommonAssets();

    this.CONSOLE_WIDGET = new ClientConsoleWidget(CONSOLE);

    //this.STAGE = new Stage();
    //STAGE.setViewport(new FitViewport(RESOLUTION.width, RESOLUTION.height));
    //STAGE.setDebugAll(true);

    /*
    Button button1 = new Button();
    button1.put(WidgetGroup.LayoutParams.layout_width, "128px");
    button1.put(WidgetGroup.LayoutParams.layout_height, "128px");
    Button button2 = new Button();
    button2.put(WidgetGroup.LayoutParams.layout_width, "256px");
    button2.put(WidgetGroup.LayoutParams.layout_height, "256px");
    button2.setVisibility(Widget.Visibility.INVISIBLE);
    Button button3 = new Button();
    button3.put(WidgetGroup.LayoutParams.layout_width, "256px");
    button3.put(WidgetGroup.LayoutParams.layout_height, "128px");
    button3.put("anchorDst", AnchoredLayout.Anchor.CENTER);

    Text text = new Text("Hello World!", getDefaultFont());
    text.put(WidgetGroup.LayoutParams.layout_width, "wrap_content");
    text.put(WidgetGroup.LayoutParams.layout_height, "wrap_content");

    LinearLayout ll1 = new VerticalLayout();
    ll1.put(WidgetGroup.LayoutParams.layout_width, "wrap_content");
    ll1.put(WidgetGroup.LayoutParams.layout_height, "wrap_content");
    ll1.setMargins(5);
    ll1.setPadding(10);
    ll1.addWidget(button1);
    ll1.addWidget(button2);
    LinearLayout ll3 = new HorizontalLayout();
    ll3.put(WidgetGroup.LayoutParams.layout_width, "wrap_content");
    ll3.put(WidgetGroup.LayoutParams.layout_height, "wrap_content");
    ll3.setPadding(10);
    ll3.addWidget(text);

    LinearLayout ll2 = new HorizontalLayout();
    ll2.put(WidgetGroup.LayoutParams.layout_width, "wrap_content");
    ll2.put(WidgetGroup.LayoutParams.layout_height, "wrap_content");
    ll2.addWidget(ll1);
    ll2.addWidget(button3);
    ll2.addWidget(ll3);

    this.BATCH = new SpriteBatch(1024);
    this.WINDOW = new Window(RESOLUTION.width, RESOLUTION.height);
    this.WINDOW.addWidget(ll2);
    this.WINDOW.setDebugging(true);
    */

    //LinearLayout linearLayout = new LinearLayout();
    //linearLayout.put(Widget.LayoutParams.layout_width, Widget.LayoutParams.FILL_PARENT);
    //linearLayout.put(Widget.LayoutParams.layout_height, Widget.LayoutParams.FILL_PARENT);
    //linearLayout.setBackground(new ColorDrawable(ColorUtils.GREEN));

    //this.WINDOW = new Window(RESOLUTION.width, RESOLUTION.height);
    //WINDOW.setWidget(linearLayout);

    Gdx.input.setCatchBackKey(true);
    Gdx.input.setCatchMenuKey(true);
    //this.INPUT_PROCESSOR = new ClientInputProcessor(this, STAGE);
    //this.INPUT_PROCESSOR = new ClientInputProcessor(this, WINDOW);
    Gdx.input.setInputProcessor(INPUT_PROCESSOR);

    Keys.Console.addStateListener(new KeyStateAdapter<Integer>() {
      @Override
      public void onPressed(Key<Integer> key, Integer binding) {
        CONSOLE_WIDGET.setVisible(!CONSOLE_WIDGET.isVisible());
        Gdx.app.log(TAG, "Console visible = " + CONSOLE_WIDGET.isVisible());
      }
    });

    setScene(new HudedScene(this));

    Resources res = new Resources(ASSET_MANAGER);
    Color black = res.getColor("@color/black");
    System.out.println("color = " + black.toString());

    try {
      Resources.ResourceReference ref = Resources.ResourceReference.parse("@unifi:color/name");
      Gdx.app.debug(TAG, "Resource: " + ref);
    } catch (ParseException e) {
      Gdx.app.error(TAG, e.getMessage(), e);
    }
  }

  private void setSerializers() {
    Gdx.app.log(TAG, "Setting serializers...");

    CVAR_MANAGER.putSerializer(Locale.class, LocaleStringSerializer.INSTANCE);
  }

  private void initPCvars() {
    Cvars.Client.Windowed.addStateListener(new CvarStateAdapter<Boolean>() {
      @Override
      public void onChanged(Cvar<Boolean> cvar, Boolean from, Boolean to) {
        Client.this.pCvar_Windowed = to;
        if (Gdx.app.getType() == Application.ApplicationType.Desktop
                && !Client.this.forcedWindowed) {
          if (to) {
            Gdx.graphics.setWindowedMode(RESOLUTION.getWidth(), RESOLUTION.getHeight());
          } else {
            Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
          }
        }
      }
    });

    Cvars.Client.Render.ShowFPS.addStateListener(new CvarStateAdapter<Byte>() {
      @Override
      public void onChanged(Cvar<Byte> cvar, Byte from, Byte to) {
        Client.this.pCvar_showFps = to;
      }
    });

    Cvars.Client.Console.Font.addStateListener(new CvarStateAdapter<String>() {
      @Override
      public void onChanged(Cvar<String> cvar, String from, String to) {
        Client.this.ASSET_MANAGER.load(to, BitmapFont.class);
        ASSET_MANAGER.finishLoading();
        Client.this.font = Client.this.ASSET_MANAGER.get(to);
      }
    });
  }

  private void setAssetLoaders(FileHandleResolver resolver) {
    Gdx.app.log(TAG, "Setting asset loaders...");
  }

  private void loadCommonAssets() {
    Gdx.app.log(TAG, "Loading common assets...");


    ASSET_MANAGER.finishLoading();
  }

  public Dimension getResolution() {
    return RESOLUTION;
  }

  //public Stage getStage() { return STAGE; }
  //public Window getWindow() {
  //  return WINDOW;
  //}

  public ClientConsole getConsole() {
    return CONSOLE;
  }

  public AssetManager getAssetManager() {
    return ASSET_MANAGER;
  }

  public CommandManager getCommandManager() {
    return COMMAND_MANAGER;
  }

  public GdxCvarManager getCvarManager() {
    return CVAR_MANAGER;
  }

  public GdxKeyManager getKeyManager() {
    return KEY_MANAGER;
  }

  public BitmapFont getDefaultFont() {
    return font;
  }

  public boolean isFullscreen() {
    return !pCvar_Windowed && !forcedWindowed;
  }

  public void setScene(HudedScene scene) {
    //STAGE.clear();
    //STAGE.addActor(scene);
    //STAGE.addActor(CONSOLE_WIDGET);

    HudedScene oldScene = this.scene;
    if (oldScene != null) {
      oldScene.dispose();
    }

    this.scene = scene;
  }

  @Override
  public void resize(int width, int height) {
    //STAGE.getViewport().update(width, height, true);
  }

  Canvas canvas;
  ImmutablePaint paint = ImmutablePaint.builder()
          .setStyle(Paint.Style.STROKE)
          .setColor(Color.NAVY)
          .setStrokeWidth(5)
          .build();

  @Override
  public void render() {
    Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    if (canvas == null) {
      canvas = new Canvas(RESOLUTION.getWidth(), RESOLUTION.getHeight());
      canvas.setRestoreGlState(true);
    }

    Gdx.app.setLogLevel(Application.LOG_DEBUG);
    canvas.begin();
    canvas.clipRect(0, 0, 100, 50);
    canvas.drawRoundRect(0, 0, 100, 50, 8, Paint.DEFAULT);
    canvas.drawRoundRect(1, 1, 98, 48, 8, paint);
    canvas.end();

    //WINDOW.draw();

    //STAGE.act(Gdx.graphics.getDeltaTime());
    //STAGE.draw();
    //Batch b = STAGE.getBatch();
    /*final Batch batch = BATCH;
    batch.begin();
    {
      WINDOW.draw(batch);
      final BitmapFont font = getDefaultFont();
      if (pCvar_showFps > 0 || forcedDrawFps) {
        GlyphLayout fps = new GlyphLayout(
                font,
                Integer.toString(Gdx.graphics.getFramesPerSecond()));

        float x = 0;
        float y = 0;
        int resolvedShowFps = forcedDrawFps ? 1 : pCvar_showFps;
        switch (resolvedShowFps) {
          case 1:
            x = 0;
            y = RESOLUTION.height;
            break;
          case 2:
            x = RESOLUTION.width - fps.width;
            y = RESOLUTION.height;
            break;
          case 3:
            x = 0;
            y = 0;
            break;
          case 4:
            x = RESOLUTION.width - fps.width;
            y = 0;
            break;
        }

        font.draw(batch, fps, x, y);
      }
    }
    batch.end();*/
  }

  @Override
  public void pause() {

  }

  @Override
  public void resume() {

  }

  @Override
  public void dispose() {
    Gdx.app.log(TAG, "Disposing scene...");
    if (scene != null) {
      scene.dispose();
      this.scene = null;
    }

    Gdx.app.log(TAG, "Disposing stage...");
    //STAGE.dispose();
    //this.STAGE = null;
    //WINDOW.dispose();
    //this.WINDOW = null;
    //this.BATCH.dispose();
    //this.BATCH = null;

    Gdx.app.log(TAG, "Saving cvars...");
    CVAR_MANAGER.saveAll();
    Gdx.app.log(TAG, "Disposing cvar manager...");
    this.CVAR_MANAGER = null;

    Gdx.app.log(TAG, "Saving key bindings...");
    KEY_MANAGER.saveAll();
    Gdx.app.log(TAG, "Disposing key manager...");
    this.KEY_MANAGER = null;

    Gdx.app.log(TAG, "Disposing command manager...");
    this.COMMAND_MANAGER = null;

    Gdx.app.log(TAG, "Resetting stdout...");
    System.setOut(STDOUT);
    Gdx.app.log(TAG, "Resetting stderr...");
    System.setErr(STDERR);
    Gdx.app.log(TAG, "Flushing console...");
    CONSOLE.flush();
    CONSOLE.close();

    Gdx.app.log(TAG, "Disposing console...");
    this.CONSOLE = null;
    this.CONSOLE_WIDGET = null;

    Gdx.app.log(TAG, "Disposing assets...");
    ASSET_MANAGER.dispose();
    this.ASSET_MANAGER = null;
  }

}
