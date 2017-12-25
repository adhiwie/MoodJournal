package com.adhiwie.moodjournal.exception;

public class ConsetMissingException extends Exception{

	private static final long serialVersionUID = -12347;
	
	public ConsetMissingException()
	{
		super("User has not given the consent yet!");
	}

}
