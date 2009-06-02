package org.paradise.etrc.wizard;

public abstract class Wizard {
	public static final int FINISHED = 0;
	public static final int CANCELED = 1;

	protected int wizardSteps;
	
	abstract protected int doWizard();
}
