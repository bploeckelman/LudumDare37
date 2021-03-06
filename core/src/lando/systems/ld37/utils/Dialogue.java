package lando.systems.ld37.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

/**
 * Author: Ian McNamara <ian.mcnamara@wisc.edu>
 */
public class Dialogue extends InputAdapter {

    // FINAL
    private static final float MARGIN = 32f;
    private static final float LINE_HEIGHT = 20f;
    private static final char SPACE = ' ';
    private static final float DEBOUNCE_TIME = .25f;
    private static final float BACKGROUND_ALPHA = 0.8f;

    // SETTINGS
    private static final boolean SHOW_PRESS_ENTER_DEFAULT = true;
    private boolean showPressEnter;
    private static final float CPS_DEFAULT = 20f;
    private float cps;
    private boolean allowClose;

    // FLAGS
    private boolean isShown = false;
    private boolean atEndOfMessage = false;
    private boolean isComplete = false;
    private boolean fastForward = false;

    // Base Properties
    private float startX;
    private float startY;
    private float width;
    private float height;
    private Array<String> messages;

    // Measurements
    private float fontDrawX;
    private float fontDrawTopY;
    private float fontDrawWidth;
    private float pressEnterX;
    private float pressEnterY;

    // Message vars
    private int currentMessageIndex;
    private int currentMessageCharIndex;
    private Array<String> currentMessageLines;
    private GlyphLayout finalLayout = new GlyphLayout();
    private GlyphLayout measuringLayout = new GlyphLayout();
    private GlyphLayout pressEnterLayout = new GlyphLayout(Assets.font8pt, "CLICK / ENTER...");
    private Color pressEnterColor = new Color(1f, 1f, 1f, 1f);

    // Time Tracking
    private float updateTime;
    private float pressEnterTime;
    private float keyDeBounce;

    public Dialogue() {}

    // -----------------------------------------------------------------------------------------------------------------

    /**
     *
     * @param startTileX start pos in tiles
     * @param startTileY start pos in tiles
     * @param width Width in tiles
     * @param height Height in tiles
     * @param messages The message(s) to be displayed
     * @param showPressEnter Display the "press enter" message after the message completes
     * @param cps Characters per second
     */
    public void show(    int startTileX,
                         int startTileY,
                         int width,
                         int height,
                         Array<String> messages,
                         boolean showPressEnter,
                         float cps) {

        show(startTileX, startTileY, width, height, messages, showPressEnter, cps, true);
    }

    /**
     *
     * @param startTileX start pos in tiles
     * @param startTileY start pos in tiles
     * @param width Width in tiles
     * @param height Height in tiles
     * @param messages The message(s) to be displayed
     * @param showPressEnter Display the "press enter" message after the message completes
     * @param cps Characters per second
     * @param allowClose should we allow the User to close it
     */
    public void show(int startTileX,
                     int startTileY,
                     int width,
                     int height,
                     Array<String> messages,
                     boolean showPressEnter,
                     float cps,
                     boolean allowClose) {
        this.allowClose = allowClose;
        this.startX = startTileX;
        this.startY = startTileY;
        this.width = width;
        this.height = height;
        this.messages = messages;
        this.showPressEnter = showPressEnter;
        this.cps = cps;


        this.fontDrawX = this.startX + MARGIN;
        this.fontDrawTopY = this.startY + this.height - MARGIN;
        this.fontDrawWidth = this.width - (MARGIN * 2);

        this.nextMessage(true);

        this.isShown = true;
        this.isComplete = false;
        this.keyDeBounce = DEBOUNCE_TIME;

        // Measure out where the "press enter" will be placed
        pressEnterX = fontDrawX + fontDrawWidth - pressEnterLayout.width;
        pressEnterY = startY + MARGIN/2f;

        // Times
        updateTime = 0f;
        pressEnterTime = 0f;
    }

    public void show(int startTileX, int startTileY, int width, int height, Array<String> messages) {
        show(
                startTileX,
                startTileY,
                width,
                height,
                messages,
                SHOW_PRESS_ENTER_DEFAULT,
                CPS_DEFAULT);
    }

    public void show(int startTileX, int startTileY, int width, int height, String... messages) {
        show(
                startTileX,
                startTileY,
                width,
                height,
                new Array<String>(messages),
                SHOW_PRESS_ENTER_DEFAULT,
                CPS_DEFAULT);
    }

    public void show(int startTileX, int startTileY, int width, int height, Array<String> messages, boolean allowClose) {
        show(
                startTileX,
                startTileY,
                width,
                height,
                messages,
                SHOW_PRESS_ENTER_DEFAULT,
                CPS_DEFAULT,
                allowClose);
    }


    private void hide() {
        this.isShown = false;
    }

    public boolean isActive() {
        return (this.isShown && !this.isComplete);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public boolean keyDown(int keycode) {
        // Don't block if we're hidden
        if (!isShown) return false;

        // Limit the rate at which we'll accept input, but still block
        if (keyDeBounce > 0) return true;
        // Reset the keyDeBounce timer
        keyDeBounce = DEBOUNCE_TIME;

        // Listen for a specific key(s)
        if (keycode == Input.Keys.ENTER) {
            if (atEndOfMessage) {
                if (!allowClose) return false;
                // Get the next message going.
                nextMessage();
            } else {
                // Fast forward to the end of the message.
                this.fastForward = true;
            }
        }

        // Input handled.
        return true;
    }

    @Override
    public boolean touchUp (int screenX, int screenY, int pointer, int button) {
        if (!isShown) return false;
        if (pointer == 0) {
            if (atEndOfMessage) {
                if (!allowClose) return false;
                nextMessage();
            } else {
                fastForward = true;
            }
        }
        return true;
    }

    // -----------------------------------------------------------------------------------------------------------------

    private float getFontDrawX() {
        return this.fontDrawX;
    }
    private float getFontDrawY(int lineNumber) {
        return this.fontDrawTopY + (LINE_HEIGHT * lineNumber);
    }

    private void nextMessage() {
        this.nextMessage(false);
    }
    private void nextMessage(boolean isFirst) {
        if (isFirst) {
            this.currentMessageIndex = 0;
        } else {
            this.currentMessageIndex++;
        }

        // Are we out of messages?
        if (this.currentMessageIndex >= this.messages.size) {
            // Todo: might want to split this out of here, especially if we animate the show/hide
            this.hide();
            this.isComplete = true;
            return;
        }

        // Update the shit
        String currentMessage = this.messages.get(this.currentMessageIndex);
        this.currentMessageLines = wrapLine(currentMessage);
        this.currentMessageCharIndex = 0;
        this.finalLayout.setText(Assets.font, "");

        this.atEndOfMessage = false;
        this.updateTime = 0;
    }

    private Array<String> wrapLine(String givenLine) {
        Array<String> processedLines = new Array<String>();

        // Cram that string into our measuring layout
        this.measuringLayout.setText(Assets.font, givenLine);
        // Does it fit?
        if (this.measuringLayout.width <= this.fontDrawWidth) {
            processedLines.add(givenLine);
            return processedLines;
        } else {
            // It doesn't fit.
            // Move backwards to the spaces.
            int wrapIndex = givenLine.lastIndexOf(SPACE, givenLine.length());
            String wrappedString = givenLine.substring(0, wrapIndex);
            this.measuringLayout.setText(Assets.font, wrappedString);
            while (this.measuringLayout.width > this.fontDrawWidth) {
                // It still didn't fit.  Shorten it.
                wrapIndex--;
                wrapIndex = givenLine.lastIndexOf(SPACE, wrapIndex);
                // Catch the unexpected.
                if (wrapIndex <= 2) {
//                    Gdx.app.log("ERROR",
//                            "Wrapping has gone horribly horribly wrong.  GivenLine='" +
//                                    givenLine + "' and text area width='" + String.valueOf(this.fontDrawWidth) + "'");
                    Gdx.app.exit();
                }
                wrappedString = givenLine.substring(0, wrapIndex);
                this.measuringLayout.setText(Assets.font, wrappedString);
            }

            // Add the line that fits.
            processedLines.add(givenLine.substring(0, wrapIndex));

            // Get the rest (skipping over that space)
            String newLine = givenLine.substring(wrapIndex + 1);

            // Wrap the remainder if needed.
            Array<String> wrapResult = wrapLine(newLine);
            for (int i = 0; i < wrapResult.size; i++) {
                processedLines.add(wrapResult.get(i));
            }

            return processedLines;
        }
    }


    // -----------------------------------------------------------------------------------------------------------------

    public void render(SpriteBatch batch) {
        if (this.isShown) {
            batch.setColor(0, 0, 0, BACKGROUND_ALPHA);
            batch.draw(Assets.whitePixel, this.startX, this.startY, this.width, this.height);
            batch.setColor(1, 1, 1, 1);

            // If we're complete, show the "press enter"
            if (atEndOfMessage && showPressEnter) {
                float pressEnterAlpha = Math.abs(MathUtils.sin(pressEnterTime * 2.5f));
                pressEnterColor.a = pressEnterAlpha;
                Assets.font8pt.setColor(1, 1, 1, pressEnterAlpha);
                Assets.font8pt.draw(batch, "CLICK / ENTER...", pressEnterX, pressEnterY);
                Assets.font8pt.setColor(1,1,1,1);
            }

            Assets.font.setColor(1f, 1f, 1f, 1f);
            Assets.font.draw(batch, finalLayout, this.getFontDrawX(), this.getFontDrawY(0));
        }
    }
    public void update(float dt) {
        keyDeBounce -= dt;
        if (atEndOfMessage) this.pressEnterTime += dt;

        // Abort?
        if (!isShown || atEndOfMessage || isComplete) return;

        this.updateTime += dt;

        int totalCharsToShow;
        totalCharsToShow = MathUtils.floor(this.updateTime * cps);

        // If we get to show more than we currently are:
        if (totalCharsToShow > this.currentMessageCharIndex) {

            // Start building the render string
            String renderString = "";
            int i;
            int charsRemaining = totalCharsToShow;
            for (i = 0; i < this.currentMessageLines.size; i++) {

                // If we've done this before, go to a new line.
                if (i > 0) {
                    renderString += "\n";
                }

                if (charsRemaining >= this.currentMessageLines.get(i).length() || this.fastForward) {
                    // Use the whole line
                    renderString += this.currentMessageLines.get(i);
                    // Update the remaining count.
                    charsRemaining -= this.currentMessageLines.get(i).length();
                } else {
                    // Use part of the line.
                    renderString += this.currentMessageLines.get(i).substring(0, charsRemaining);
                    // Update the remaining count.
                    charsRemaining = 0;
                }

                // Continue?
                if (charsRemaining < 1 && !this.fastForward) {
                    break;
                }
            }

            this.finalLayout.setText(Assets.font, renderString);

            // Check for end of message
            if (charsRemaining > 0 || this.fastForward) {
                // End of message has been reached.
                this.atEndOfMessage = true;
                this.fastForward = false;
            }

            // Update the current thing
            this.currentMessageCharIndex = totalCharsToShow;
        }
    }

}
