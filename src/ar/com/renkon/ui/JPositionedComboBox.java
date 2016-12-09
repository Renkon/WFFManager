package ar.com.renkon.ui;

import javax.swing.JComboBox;

import ca.odell.glazedlists.swing.AutoCompleteSupport;

@SuppressWarnings({ "serial", "rawtypes" })
public class JPositionedComboBox extends JComboBox
{
	private int i;
	private int j;
	private AutoCompleteSupport a = null;
	
	public JPositionedComboBox(int i, int j)
	{
		super();
		this.i = i;
		this.j = j;
	}
	
	public int getI(){
		return i;
	}
	
	public int getJ(){
		return j;
	}
	
	public void setAutoCompleteSupport(AutoCompleteSupport a)
	{
		this.a = a;
	}
	
	public AutoCompleteSupport getAutoCompleteSupport(){
		return a;
	}
	
	public String toString()
	{	
		String def = super.toString();
		if (def == null)
			return "";
		return def;		
	}
}
