package com.mygdx.game.components;


import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.behaviours.BlackBoard;
import com.mygdx.game.behaviours.Task;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import org.jetbrains.annotations.NotNull;

public class BehaviourComponent implements Component{
	public Array<Function1<Entity, Unit>> destroyFuncs = new Array<>();

	private Task currTask;

	private BlackBoard bb = new BlackBoard();
	public final BlackBoard getBlackBoard()
	{
		return bb;
	}
	private void setbb(BlackBoard value)
	{
		bb = value;
	}

    public Callback onCompletionCallback;
    public final Callback getOnCompletionCallback()
    {
        return onCompletionCallback;
    }
    public final void setOnCompletionCallback(Callback value)
    {
        onCompletionCallback = value;
    }

	public final boolean isIdle()
	{
		return currTask == null || !currTask.getController().getRunning();
	}

	public String[] currTaskName = new String[10];

    public BehaviourComponent(Entity entity){
    	BlackBoard bb = new BlackBoard();
    	bb.myself = entity;

		this.setbb(bb);
	}

	/**
	 Sets the current task. Handles resetting and starting the task.
	*/
	public final Task getCurrTask()
	{
		return currTask;
	}
	public final void setCurrTask(Task value)
	{
		currTask = value;
		currTask.getController().safeReset();
		currTask.getController().safeStart();
	}

    public interface Callback
	{
		void invoke();
	}
}