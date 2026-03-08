package com.gorf.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;
import com.gorf.Constants;

public class InputManager implements ControllerListener {
    private Controller activeController;

    private static final int AXIS_LEFT_X = 0;
    private static final int AXIS_LEFT_Y = 1;
    private static final int BUTTON_CROSS = 0;
    private static final int BUTTON_CIRCLE = 1;
    private static final int BUTTON_SQUARE = 2;
    private static final int BUTTON_TRIANGLE = 3;
    private static final int BUTTON_L1 = 4;
    private static final int BUTTON_R1 = 5;
    private static final int BUTTON_OPTIONS = 6;
    private static final int BUTTON_DPAD_UP = 11;
    private static final int BUTTON_DPAD_DOWN = 12;
    private static final int BUTTON_DPAD_LEFT = 13;
    private static final int BUTTON_DPAD_RIGHT = 14;

    private float moveX, moveY;
    private boolean firePressed, fireJustPressed, prevFireState;
    private boolean startPressed, startJustPressed, prevStartState;
    private boolean leftPressed, rightPressed, upPressed, downPressed;
    private boolean confirmJustPressed, prevConfirmState, confirmPressed;

    public void initialize() {
        Controllers.addListener(this);
        if (Controllers.getControllers().size > 0) {
            activeController = Controllers.getControllers().first();
            Gdx.app.log("InputManager", "Controller detected: " + activeController.getName());
        } else {
            Gdx.app.log("InputManager", "No controller detected. Using keyboard.");
        }
    }

    public void update() {
        prevFireState = firePressed;
        prevStartState = startPressed;
        prevConfirmState = confirmPressed;

        if (activeController != null && activeController.isConnected()) {
            updateController();
        } else {
            updateKeyboard();
        }

        fireJustPressed = firePressed && !prevFireState;
        startJustPressed = startPressed && !prevStartState;
        confirmJustPressed = confirmPressed && !prevConfirmState;
    }

    private void updateController() {
        float lx = applyDeadzone(activeController.getAxis(AXIS_LEFT_X));
        float ly = applyDeadzone(-activeController.getAxis(AXIS_LEFT_Y));

        // D-pad
        boolean dUp = activeController.getButton(BUTTON_DPAD_UP);
        boolean dDown = activeController.getButton(BUTTON_DPAD_DOWN);
        boolean dLeft = activeController.getButton(BUTTON_DPAD_LEFT);
        boolean dRight = activeController.getButton(BUTTON_DPAD_RIGHT);

        moveX = lx;
        moveY = ly;
        if (dLeft) moveX = -1;
        if (dRight) moveX = 1;
        if (dUp) moveY = 1;
        if (dDown) moveY = -1;

        leftPressed = dLeft || lx < -0.5f;
        rightPressed = dRight || lx > 0.5f;
        upPressed = dUp || ly > 0.5f;
        downPressed = dDown || ly < -0.5f;

        firePressed = activeController.getButton(BUTTON_CROSS);
        startPressed = activeController.getButton(BUTTON_OPTIONS);
        confirmPressed = activeController.getButton(BUTTON_CROSS)
                      || activeController.getButton(BUTTON_OPTIONS);
    }

    private void updateKeyboard() {
        moveX = 0;
        moveY = 0;

        if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) moveX -= 1;
        if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) moveX += 1;
        if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)) moveY += 1;
        if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) moveY -= 1;

        leftPressed = Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT);
        rightPressed = Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT);
        upPressed = Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP);
        downPressed = Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN);

        firePressed = Gdx.input.isKeyPressed(Input.Keys.SPACE);
        startPressed = Gdx.input.isKeyPressed(Input.Keys.ENTER);
        confirmPressed = Gdx.input.isKeyPressed(Input.Keys.ENTER)
                      || Gdx.input.isKeyPressed(Input.Keys.SPACE);
    }

    private float applyDeadzone(float value) {
        if (Math.abs(value) < Constants.CONTROLLER_DEADZONE) return 0;
        float sign = Math.signum(value);
        float magnitude = (Math.abs(value) - Constants.CONTROLLER_DEADZONE)
                        / (1f - Constants.CONTROLLER_DEADZONE);
        return sign * Math.min(magnitude, 1f);
    }

    public float getMoveX() { return moveX; }
    public float getMoveY() { return moveY; }
    public boolean isFirePressed() { return firePressed; }
    public boolean isFireJustPressed() { return fireJustPressed; }
    public boolean isStartJustPressed() { return startJustPressed; }
    public boolean isConfirmJustPressed() { return confirmJustPressed; }
    public boolean isLeftPressed() { return leftPressed; }
    public boolean isRightPressed() { return rightPressed; }
    public boolean isUpPressed() { return upPressed; }
    public boolean isDownPressed() { return downPressed; }
    public boolean hasController() { return activeController != null && activeController.isConnected(); }

    @Override
    public void connected(Controller controller) {
        if (activeController == null) {
            activeController = controller;
            Gdx.app.log("InputManager", "Controller connected: " + controller.getName());
        }
    }

    @Override
    public void disconnected(Controller controller) {
        if (controller == activeController) {
            activeController = null;
            Gdx.app.log("InputManager", "Controller disconnected.");
        }
    }

    @Override public boolean buttonDown(Controller controller, int buttonIndex) { return false; }
    @Override public boolean buttonUp(Controller controller, int buttonIndex) { return false; }
    @Override public boolean axisMoved(Controller controller, int axisIndex, float value) { return false; }
}
